package com.generated.qualityTrace.services;

import com.generated.qualityTrace.constants.BatchStatus;
import com.generated.qualityTrace.constants.ErrorCodes;
import com.generated.qualityTrace.constants.ErrorMessages;
import com.generated.qualityTrace.constants.LogTemplates;
import com.generated.qualityTrace.constants.MaterialRecallStatus;
import com.generated.qualityTrace.constants.WorkOrderStatus;
import com.generated.qualityTrace.exceptions.BusinessException;
import com.generated.qualityTrace.models.MaterialRecallCase;
import com.generated.qualityTrace.models.ProductBatch;
import com.generated.qualityTrace.models.WorkOrder;
import com.generated.qualityTrace.repositories.MaterialRecallCaseRepository;
import com.generated.qualityTrace.repositories.ProductBatchRepository;
import com.generated.qualityTrace.repositories.WorkOrderRepository;
import com.generated.qualityTrace.types.MaterialRecallInvestigatePayload;
import com.generated.qualityTrace.validators.MaterialRecallValidator;
import java.time.OffsetDateTime;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 原料召回排查判定服务。
 * 职责边界：只负责"录入不合格材料批号 → 返回使用它的产品批次和所属工单 → 未结对象冻结"，
 * 处置动作在 {@link RecallDispositionService}，排查记录/处置记录分开维护。
 */
@Service
public class MaterialRecallService {

  private static final Logger log = LoggerFactory.getLogger(MaterialRecallService.class);

  private final MaterialRecallCaseRepository caseRepo;
  private final ProductBatchRepository batchRepo;
  private final WorkOrderRepository workOrderRepo;
  private final ProductBatchService batchService;
  private final WorkOrderService workOrderService;

  public MaterialRecallService(MaterialRecallCaseRepository caseRepo,
                               ProductBatchRepository batchRepo,
                               WorkOrderRepository workOrderRepo,
                               ProductBatchService batchService,
                               WorkOrderService workOrderService) {
    this.caseRepo = caseRepo;
    this.batchRepo = batchRepo;
    this.workOrderRepo = workOrderRepo;
    this.batchService = batchService;
    this.workOrderService = workOrderService;
  }

  /** 排查判定清单，照常可查。 */
  public List<MaterialRecallCase> listCases() {
    return caseRepo.findAll();
  }

  public MaterialRecallCase getCase(Long caseId) {
    return caseRepo.findById(caseId)
        .orElseThrow(() -> new BusinessException(
            ErrorCodes.RECALL_CASE_NOT_FOUND,
            String.format(ErrorMessages.RECALL_CASE_NOT_FOUND, caseId)));
  }

  /**
   * 录入不合格材料批号：
   * 1. 同材料批号存在未结案排查单 → 幂等返回第一次结果；
   * 2. 反查使用该批号的产品批次及所属工单；
   * 3. 未结工单与批次转质量冻结（已结工单只追溯）。
   */
  @Transactional
  public synchronized Map<String, Object> investigate(MaterialRecallInvestigatePayload payload) {
    MaterialRecallValidator.validateInvestigate(payload);
    String materialLotNo = payload.materialLotNo().trim();

    Optional<MaterialRecallCase> existing = caseRepo.findOpenByMaterialLotNo(materialLotNo);
    if (existing.isPresent()) {
      MaterialRecallCase first = existing.get();
      log.info(LogTemplates.RECALL_IDEMPOTENT_HIT, materialLotNo, first.getCaseNo());
      return buildInvestigateResult(first, true);
    }

    List<ProductBatch> affectedBatches = batchRepo.findByMaterialLotNo(materialLotNo);
    if (affectedBatches.isEmpty()) {
      throw new BusinessException(
          ErrorCodes.MATERIAL_LOT_NOT_USED,
          String.format(ErrorMessages.MATERIAL_LOT_NOT_USED, materialLotNo));
    }

    MaterialRecallCase recallCase = new MaterialRecallCase();
    recallCase.setCaseNo(generateCaseNo());
    recallCase.setMaterialLotNo(materialLotNo);
    recallCase.setInvestigatorId(payload.investigatorId());
    recallCase.setReason(payload.reason());
    recallCase.setCreatedAt(OffsetDateTime.now().toString());

    Set<Long> frozenWorkOrderIds = new HashSet<>();
    for (ProductBatch batch : affectedBatches) {
      WorkOrder workOrder = batch.getWorkOrderId() == null ? null
          : workOrderRepo.findById(batch.getWorkOrderId()).orElse(null);
      boolean workOrderOpen = workOrder != null && workOrder.isOpen();
      if (workOrderOpen && BatchStatus.freezable(batch.getBatchStatus())) {
        batchService.freeze(batch);
        workOrderService.freeze(workOrder, materialLotNo);
        frozenWorkOrderIds.add(workOrder.getId());
      }
    }

    refreshCaseStatus(recallCase);

    log.info(LogTemplates.RECALL_INVESTIGATE, materialLotNo, recallCase.getCaseNo(),
        affectedBatches.size(), frozenWorkOrderIds.size());
    return buildInvestigateResult(recallCase, false);
  }

  /** 处置后由处置服务回调，重算排查单状态；待处置（质量冻结）批次为零时关闭。 */
  public MaterialRecallCase refreshCaseStatus(MaterialRecallCase recallCase) {
    List<ProductBatch> batches = batchRepo.findByMaterialLotNo(recallCase.getMaterialLotNo());
    boolean anyFrozen = batches.stream()
        .anyMatch(b -> BatchStatus.frozen(b.getBatchStatus()));
    boolean anyDisposed = batches.stream()
        .anyMatch(b -> BatchStatus.SCRAPPED.name().equals(b.getBatchStatus())
            || BatchStatus.CONCESSION_ACCEPTED.name().equals(b.getBatchStatus()));

    if (!anyFrozen) {
      // 没有待处置的冻结批次：要么已全部处置，要么影响批次都在已结工单上无需处置。
      recallCase.setCaseStatus(MaterialRecallStatus.CLOSED.name());
      recallCase.setClosedAt(OffsetDateTime.now().toString());
      log.info(LogTemplates.RECALL_CASE_CLOSE, recallCase.getCaseNo());
    } else if (anyDisposed) {
      recallCase.setCaseStatus(MaterialRecallStatus.PARTIALLY_DISPOSED.name());
    } else {
      recallCase.setCaseStatus(MaterialRecallStatus.OPEN.name());
    }
    caseRepo.save(recallCase);
    return recallCase;
  }

  /** 组装"使用它的产品批次和所属工单"追溯结果。 */
  public Map<String, Object> buildInvestigateResult(MaterialRecallCase recallCase, boolean idempotent) {
    List<ProductBatch> batches = batchRepo.findByMaterialLotNo(recallCase.getMaterialLotNo());
    List<Map<String, Object>> batchItems = new java.util.ArrayList<>();
    for (ProductBatch batch : batches) {
      Map<String, Object> item = new LinkedHashMap<>();
      item.put("batchId", batch.getId());
      item.put("batchNo", batch.getBatchNo());
      item.put("batchStatus", batch.getBatchStatus());
      item.put("quantity", batch.getQuantity());
      item.put("producedAt", batch.getProducedAt());
      WorkOrder workOrder = batch.getWorkOrderId() == null ? null
          : workOrderRepo.findById(batch.getWorkOrderId()).orElse(null);
      if (workOrder != null) {
        Map<String, Object> wo = new LinkedHashMap<>();
        wo.put("workOrderId", workOrder.getId());
        wo.put("orderNo", workOrder.getOrderNo());
        wo.put("productCode", workOrder.getProductCode());
        wo.put("productName", workOrder.getProductName());
        wo.put("lineCode", workOrder.getLineCode());
        wo.put("status", workOrder.getStatus());
        wo.put("closed", WorkOrderStatus.isClosed(workOrder.getStatus()));
        item.put("workOrder", wo);
      } else {
        item.put("workOrder", null);
      }
      batchItems.add(item);
    }

    long frozenCount = batches.stream()
        .filter(b -> BatchStatus.frozen(b.getBatchStatus())).count();
    long closedWoCount = batchItems.stream()
        .filter(i -> i.get("workOrder") != null
            && Boolean.TRUE.equals(((Map<?, ?>) i.get("workOrder")).get("closed")))
        .count();

    Map<String, Object> result = new LinkedHashMap<>();
    result.put("idempotent", idempotent);
    result.put("caseId", recallCase.getId());
    result.put("caseNo", recallCase.getCaseNo());
    result.put("materialLotNo", recallCase.getMaterialLotNo());
    result.put("investigatorId", recallCase.getInvestigatorId());
    result.put("reason", recallCase.getReason());
    result.put("caseStatus", recallCase.getCaseStatus());
    result.put("createdAt", recallCase.getCreatedAt());
    result.put("closedAt", recallCase.getClosedAt());
    result.put("affectedBatchCount", batches.size());
    result.put("frozenBatchCount", frozenCount);
    result.put("closedWorkOrderBatchCount", closedWoCount);
    result.put("affectedBatches", batchItems);
    return result;
  }

  private String generateCaseNo() {
    return "RC-" + System.currentTimeMillis();
  }
}
