package com.generated.qualityTrace.services;

import com.generated.qualityTrace.constants.ErrorCodes;
import com.generated.qualityTrace.constants.ErrorMessages;
import com.generated.qualityTrace.constants.LogTemplates;
import com.generated.qualityTrace.constants.ProductBatchStatus;
import com.generated.qualityTrace.constants.RecallCaseStatus;
import com.generated.qualityTrace.constants.WorkOrderStatus;
import com.generated.qualityTrace.constructors.MaterialRecallCaseDtoFactory;
import com.generated.qualityTrace.models.MaterialRecallCase;
import com.generated.qualityTrace.repositories.MaterialRecallCaseRepository;
import com.generated.qualityTrace.repositories.ProductBatchRepository;
import com.generated.qualityTrace.repositories.WorkOrderRepository;
import com.generated.qualityTrace.types.BizException;
import com.generated.qualityTrace.types.MaterialRecallRegisterPayload;
import com.generated.qualityTrace.utils.Formatters;
import com.generated.qualityTrace.validators.MaterialRecallRegisterValidator;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/** 排查判定：录入不合格材料批号，定位波及的产品批次与工单，并把未结工单/批次转为质量冻结 */
@Service
public class MaterialRecallCaseService {
  private static final Logger log = LoggerFactory.getLogger(MaterialRecallCaseService.class);

  private final MaterialRecallCaseRepository recallRepo;
  private final ProductBatchRepository batchRepo;
  private final WorkOrderRepository workOrderRepo;

  public MaterialRecallCaseService(MaterialRecallCaseRepository recallRepo,
                                   ProductBatchRepository batchRepo,
                                   WorkOrderRepository workOrderRepo) {
    this.recallRepo = recallRepo;
    this.batchRepo = batchRepo;
    this.workOrderRepo = workOrderRepo;
  }

  public Map<String, Object> register(MaterialRecallRegisterPayload payload) {
    MaterialRecallRegisterValidator.validate(payload);
    List<Map<String, Object>> batches = batchRepo.findByMaterialLotNo(payload.materialLotNo());
    if (batches.isEmpty()) {
      throw new BizException(ErrorCodes.MATERIAL_LOT_NOT_FOUND, ErrorMessages.MATERIAL_LOT_NOT_FOUND);
    }

    MaterialRecallCase recallCase = new MaterialRecallCase();
    recallCase.materialLotNo = payload.materialLotNo();
    recallCase.reason = payload.reason();
    recallCase.createdBy = payload.createdBy();
    recallCase.createdAt = Formatters.now();
    recallCase.status = RecallCaseStatus.FROZEN.name();

    for (Map<String, Object> batch : batches) {
      Long batchId = (Long) batch.get("id");
      Long workOrderId = (Long) batch.get("workOrderId");
      recallCase.affectedBatchIds.add(batchId);
      if (!recallCase.affectedWorkOrderIds.contains(workOrderId)) {
        recallCase.affectedWorkOrderIds.add(workOrderId);
      }
      freezeBatch(batchId, (String) batch.get("batchStatus"));
      freezeWorkOrder(recallCase, workOrderId);
    }

    recallRepo.save(recallCase);
    log.info("{} recallNo={} materialLotNo={} batches={} workOrders={}",
        LogTemplates.RECALL_REGISTER, recallCase.recallNo, recallCase.materialLotNo,
        recallCase.affectedBatchIds, recallCase.affectedWorkOrderIds);
    return investigationView(recallCase);
  }

  public List<Map<String, Object>> list() {
    return MaterialRecallCaseDtoFactory.caseList(recallRepo.findAll());
  }

  public Map<String, Object> get(Long id) {
    MaterialRecallCase recallCase = recallRepo.findById(id);
    if (recallCase == null) {
      throw new BizException(ErrorCodes.RECALL_NOT_FOUND, ErrorMessages.RECALL_NOT_FOUND);
    }
    return investigationView(recallCase);
  }

  /** 排查结果视图：波及批次与所属工单的最新状态 */
  private Map<String, Object> investigationView(MaterialRecallCase recallCase) {
    List<Map<String, Object>> batches = new ArrayList<>();
    for (Long batchId : recallCase.affectedBatchIds) {
      Map<String, Object> batch = batchRepo.findById(batchId);
      if (batch != null) { batches.add(batch); }
    }
    List<Map<String, Object>> workOrders = new ArrayList<>();
    for (Long workOrderId : recallCase.affectedWorkOrderIds) {
      Map<String, Object> workOrder = workOrderRepo.findById(workOrderId);
      if (workOrder != null) { workOrders.add(workOrder); }
    }
    return MaterialRecallCaseDtoFactory.investigationResult(recallCase, batches, workOrders);
  }

  /** 未结批次（NORMAL）转为质量冻结；已结案批次只登记不冻结 */
  private void freezeBatch(Long batchId, String currentStatus) {
    if (ProductBatchStatus.NORMAL.name().equals(currentStatus)) {
      batchRepo.updateStatus(batchId, ProductBatchStatus.QUALITY_FROZEN.name());
      log.info("{} target={} batchStatus={}", LogTemplates.RECALL_FREEZE,
          Formatters.audit("product-batch", batchId), ProductBatchStatus.QUALITY_FROZEN);
    }
  }

  /** 未结工单（PLANNED/RUNNING/PAUSED）转为质量冻结，并记录冻结前状态供让步接收恢复 */
  private void freezeWorkOrder(MaterialRecallCase recallCase, Long workOrderId) {
    Map<String, Object> workOrder = workOrderRepo.findById(workOrderId);
    if (workOrder == null) { return; }
    String status = (String) workOrder.get("status");
    boolean open = WorkOrderStatus.PLANNED.name().equals(status)
        || WorkOrderStatus.RUNNING.name().equals(status)
        || WorkOrderStatus.PAUSED.name().equals(status);
    if (open) {
      recallCase.frozenWorkOrderPreviousStatus.putIfAbsent(workOrderId, status);
      workOrderRepo.updateStatus(workOrderId, WorkOrderStatus.QUALITY_FROZEN.name());
      log.info("{} target={} workOrderStatus={}", LogTemplates.RECALL_FREEZE,
          Formatters.audit("work-order", workOrderId), WorkOrderStatus.QUALITY_FROZEN);
    }
  }
}
