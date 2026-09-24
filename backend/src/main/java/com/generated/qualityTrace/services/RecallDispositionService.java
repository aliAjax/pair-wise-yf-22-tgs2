package com.generated.qualityTrace.services;

import com.generated.qualityTrace.constants.BatchStatus;
import com.generated.qualityTrace.constants.DispositionAction;
import com.generated.qualityTrace.constants.ErrorCodes;
import com.generated.qualityTrace.constants.ErrorMessages;
import com.generated.qualityTrace.constants.LogTemplates;
import com.generated.qualityTrace.constants.MaterialRecallStatus;
import com.generated.qualityTrace.constructors.RecallDispositionDtoFactory;
import com.generated.qualityTrace.exceptions.BusinessException;
import com.generated.qualityTrace.models.MaterialRecallCase;
import com.generated.qualityTrace.models.ProductBatch;
import com.generated.qualityTrace.models.RecallDisposition;
import com.generated.qualityTrace.models.WorkOrder;
import com.generated.qualityTrace.repositories.MaterialRecallCaseRepository;
import com.generated.qualityTrace.repositories.ProductBatchRepository;
import com.generated.qualityTrace.repositories.RecallDispositionRepository;
import com.generated.qualityTrace.repositories.WorkOrderRepository;
import com.generated.qualityTrace.types.RecallDispositionPayload;
import com.generated.qualityTrace.validators.RecallDispositionValidator;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 召回处置服务（处置记录与排查判定分开维护）：
 * 整批报废后工单回到待排产；让步接收登记审批人和原因后工单恢复；
 * 重复处置同一批次返回第一次结果；未处置（质量冻结）前不能完工（完工守卫在 WorkOrderService）。
 */
@Service
public class RecallDispositionService {

  private static final Logger log = LoggerFactory.getLogger(RecallDispositionService.class);

  private final RecallDispositionRepository dispositionRepo;
  private final MaterialRecallCaseRepository caseRepo;
  private final ProductBatchRepository batchRepo;
  private final WorkOrderRepository workOrderRepo;
  private final ProductBatchService batchService;
  private final WorkOrderService workOrderService;
  private final MaterialRecallService recallService;

  public RecallDispositionService(RecallDispositionRepository dispositionRepo,
                                  MaterialRecallCaseRepository caseRepo,
                                  ProductBatchRepository batchRepo,
                                  WorkOrderRepository workOrderRepo,
                                  ProductBatchService batchService,
                                  WorkOrderService workOrderService,
                                  MaterialRecallService recallService) {
    this.dispositionRepo = dispositionRepo;
    this.caseRepo = caseRepo;
    this.batchRepo = batchRepo;
    this.workOrderRepo = workOrderRepo;
    this.batchService = batchService;
    this.workOrderService = workOrderService;
    this.recallService = recallService;
  }

  /** 处置记录清单（独立入口，照常可查）。 */
  public List<RecallDisposition> list() {
    return dispositionRepo.findAll();
  }

  public List<RecallDisposition> listByCase(Long caseId) {
    return dispositionRepo.findByCaseId(caseId);
  }

  /**
   * 对排查单内的一个冻结批次执行处置。
   */
  @Transactional
  public synchronized Map<String, Object> dispose(Long caseId, RecallDispositionPayload payload) {
    RecallDispositionValidator.validate(payload);

    MaterialRecallCase recallCase = caseRepo.findById(caseId)
        .orElseThrow(() -> new BusinessException(
            ErrorCodes.RECALL_CASE_NOT_FOUND,
            String.format(ErrorMessages.RECALL_CASE_NOT_FOUND, caseId)));

    ProductBatch batch = batchRepo.findByBatchNo(payload.batchNo().trim())
        .orElseThrow(() -> new BusinessException(
            ErrorCodes.BATCH_NOT_IN_RECALL,
            String.format(ErrorMessages.BATCH_NOT_IN_RECALL, payload.batchNo(), recallCase.getCaseNo())));

    // 批次必须属于该召回单的材料批号影响范围。
    if (!recallCase.getMaterialLotNo().equals(batch.getMaterialLotNo())) {
      throw new BusinessException(
          ErrorCodes.BATCH_NOT_IN_RECALL,
          String.format(ErrorMessages.BATCH_NOT_IN_RECALL, batch.getBatchNo(), recallCase.getCaseNo()));
    }

    // 重复处置同一批次返回第一次结果（即使排查单已关闭，也优先返回首次结果）。
    var existing = dispositionRepo.findByBatchId(batch.getId());
    if (existing.isPresent()) {
      RecallDisposition first = existing.get();
      log.info(LogTemplates.RECALL_DISPOSE_IDEMPOTENT, batch.getBatchNo(), first.getId());
      return RecallDispositionDtoFactory.toResponse(first, true);
    }

    if (MaterialRecallStatus.CLOSED.name().equals(recallCase.getCaseStatus())) {
      throw new BusinessException(
          ErrorCodes.RECALL_CASE_CLOSED,
          String.format(ErrorMessages.RECALL_CASE_CLOSED, recallCase.getCaseNo()));
    }

    // 未处置前必须处于质量冻结状态。
    if (!BatchStatus.frozen(batch.getBatchStatus())) {
      throw new BusinessException(
          ErrorCodes.BATCH_NOT_FROZEN,
          String.format(ErrorMessages.BATCH_NOT_FROZEN, batch.getBatchNo()));
    }

    WorkOrder workOrder = batch.getWorkOrderId() == null ? null
        : workOrderRepo.findById(batch.getWorkOrderId()).orElse(null);

    RecallDisposition disposition = new RecallDisposition();
    disposition.setRecallCaseId(recallCase.getId());
    disposition.setCaseNo(recallCase.getCaseNo());
    disposition.setMaterialLotNo(recallCase.getMaterialLotNo());
    disposition.setBatchId(batch.getId());
    disposition.setBatchNo(batch.getBatchNo());
    disposition.setHandlerId(payload.handlerId().trim());
    disposition.setCreatedAt(OffsetDateTime.now().toString());

    String action = payload.action();
    disposition.setAction(action);

    if (DispositionAction.SCRAP.name().equals(action)) {
      batchService.scrap(batch);
      log.info(LogTemplates.RECALL_SCRAP, batch.getBatchNo(),
          workOrder == null ? "-" : workOrder.getOrderNo());
      // 报废后工单回到待排产。
      if (workOrder != null) {
        workOrderService.backToPlanned(workOrder, batch.getBatchNo());
        disposition.setWorkOrderId(workOrder.getId());
        disposition.setWorkOrderNo(workOrder.getOrderNo());
      }
    } else {
      // 让步接收：登记审批人和原因，批次恢复，工单恢复冻结前状态（无其他冻结批次时）。
      disposition.setApproverId(payload.approverId().trim());
      disposition.setConcessionReason(payload.concessionReason().trim());
      batchService.concessionAccept(batch);
      log.info(LogTemplates.RECALL_CONCESSION, batch.getBatchNo(), disposition.getApproverId());
      if (workOrder != null) {
        workOrderService.resumeIfNoFrozenBatches(workOrder);
        disposition.setWorkOrderId(workOrder.getId());
        disposition.setWorkOrderNo(workOrder.getOrderNo());
      }
    }

    dispositionRepo.save(disposition);
    recallService.refreshCaseStatus(recallCase);

    log.info(LogTemplates.RECALL_DISPOSE, recallCase.getCaseNo(), batch.getBatchNo(),
        action, disposition.getHandlerId());
    return RecallDispositionDtoFactory.toResponse(disposition, false);
  }
}
