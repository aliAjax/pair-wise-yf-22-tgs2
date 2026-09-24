package com.generated.qualityTrace.services;

import com.generated.qualityTrace.constants.ErrorCodes;
import com.generated.qualityTrace.constants.ErrorMessages;
import com.generated.qualityTrace.constants.LogTemplates;
import com.generated.qualityTrace.constants.ProductBatchStatus;
import com.generated.qualityTrace.constants.RecallAction;
import com.generated.qualityTrace.constants.RecallCaseStatus;
import com.generated.qualityTrace.constants.WorkOrderStatus;
import com.generated.qualityTrace.constructors.RecallDispositionDtoFactory;
import com.generated.qualityTrace.models.MaterialRecallCase;
import com.generated.qualityTrace.models.RecallDisposition;
import com.generated.qualityTrace.repositories.MaterialRecallCaseRepository;
import com.generated.qualityTrace.repositories.ProductBatchRepository;
import com.generated.qualityTrace.repositories.RecallDispositionRepository;
import com.generated.qualityTrace.repositories.WorkOrderRepository;
import com.generated.qualityTrace.types.BizException;
import com.generated.qualityTrace.types.RecallDispositionPayload;
import com.generated.qualityTrace.utils.Formatters;
import com.generated.qualityTrace.validators.RecallDispositionValidator;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/** 处置记录：整批报废 / 让步接收；同一排查单内同一批次重复处置时返回首次结果 */
@Service
public class RecallDispositionService {
  private static final Logger log = LoggerFactory.getLogger(RecallDispositionService.class);

  private final RecallDispositionRepository dispositionRepo;
  private final MaterialRecallCaseRepository recallRepo;
  private final ProductBatchRepository batchRepo;
  private final WorkOrderRepository workOrderRepo;

  public RecallDispositionService(RecallDispositionRepository dispositionRepo,
                                  MaterialRecallCaseRepository recallRepo,
                                  ProductBatchRepository batchRepo,
                                  WorkOrderRepository workOrderRepo) {
    this.dispositionRepo = dispositionRepo;
    this.recallRepo = recallRepo;
    this.batchRepo = batchRepo;
    this.workOrderRepo = workOrderRepo;
  }

  /** 返回处置结果视图；replayed=true 表示命中幂等，返回的是首次处置结果 */
  public Map<String, Object> dispose(RecallDispositionPayload payload) {
    RecallAction action = RecallDispositionValidator.validate(payload);
    MaterialRecallCase recallCase = recallRepo.findById(payload.recallId());
    if (recallCase == null) {
      throw new BizException(ErrorCodes.RECALL_NOT_FOUND, ErrorMessages.RECALL_NOT_FOUND);
    }
    if (!recallCase.affectedBatchIds.contains(payload.batchId())) {
      throw new BizException(ErrorCodes.RECALL_BATCH_NOT_IN_CASE, ErrorMessages.RECALL_BATCH_NOT_IN_CASE);
    }

    RecallDisposition existing = dispositionRepo.findByRecallIdAndBatchId(payload.recallId(), payload.batchId());
    if (existing != null) {
      log.info("{} recallNo={} dispositionNo={}", LogTemplates.RECALL_IDEMPOTENT_HIT,
          recallCase.recallNo, existing.dispositionNo);
      return RecallDispositionDtoFactory.dispositionResult(existing, true, recallCase.status);
    }

    Map<String, Object> batch = batchRepo.findById(payload.batchId());
    Map<String, Object> workOrder = workOrderRepo.findById((Long) batch.get("workOrderId"));

    RecallDisposition disposition = new RecallDisposition();
    disposition.recallId = recallCase.id;
    disposition.batchId = payload.batchId();
    disposition.batchNo = (String) batch.get("batchNo");
    disposition.workOrderId = (Long) batch.get("workOrderId");
    disposition.orderNo = workOrder == null ? null : (String) workOrder.get("orderNo");
    disposition.action = action.name();
    disposition.approver = payload.approver();
    disposition.reason = payload.reason();
    disposition.disposedBy = payload.disposedBy();
    disposition.disposedAt = Formatters.now();

    if (action == RecallAction.SCRAP) {
      applyScrap(recallCase, disposition);
    } else {
      applyConcession(recallCase, disposition);
    }

    dispositionRepo.save(disposition);
    refreshCaseStatus(recallCase);
    return RecallDispositionDtoFactory.dispositionResult(disposition, false, recallCase.status);
  }

  public List<Map<String, Object>> list(Long recallId) {
    List<RecallDisposition> rows =
        recallId == null ? dispositionRepo.findAll() : dispositionRepo.findByRecallId(recallId);
    return RecallDispositionDtoFactory.dispositionList(rows);
  }

  /** 整批报废：批次报废；同工单无其他待处置批次时，工单回到待排产 */
  private void applyScrap(MaterialRecallCase recallCase, RecallDisposition disposition) {
    batchRepo.updateStatus(disposition.batchId, ProductBatchStatus.SCRAPPED.name());
    if (!hasOtherPendingBatches(recallCase, disposition.workOrderId, disposition.batchId)) {
      recallCase.frozenWorkOrderPreviousStatus.remove(disposition.workOrderId);
      workOrderRepo.updateStatus(disposition.workOrderId, WorkOrderStatus.PLANNED.name());
    }
    log.info("{} recallNo={} batch={} workOrder={}",
        LogTemplates.RECALL_DISPOSE_SCRAP, recallCase.recallNo,
        Formatters.audit("product-batch", disposition.batchId),
        Formatters.audit("work-order", disposition.workOrderId));
  }

  /** 让步接收：登记审批人和原因后放行批次；同工单无其他待处置批次时，工单恢复冻结前状态 */
  private void applyConcession(MaterialRecallCase recallCase, RecallDisposition disposition) {
    batchRepo.updateStatus(disposition.batchId, ProductBatchStatus.CONCESSION_RELEASED.name());
    String previous = null;
    if (!hasOtherPendingBatches(recallCase, disposition.workOrderId, disposition.batchId)) {
      previous = recallCase.frozenWorkOrderPreviousStatus.remove(disposition.workOrderId);
      if (previous != null) {
        workOrderRepo.updateStatus(disposition.workOrderId, previous);
      }
    }
    log.info("{} recallNo={} batch={} approver={} restoredWorkOrderStatus={}",
        LogTemplates.RECALL_DISPOSE_CONCESSION, recallCase.recallNo,
        Formatters.audit("product-batch", disposition.batchId), disposition.approver, previous);
  }

  /** 同一排查单内，该工单是否还有其他批次未处置（当前批次除外） */
  private boolean hasOtherPendingBatches(MaterialRecallCase recallCase, Long workOrderId, Long currentBatchId) {
    for (Long batchId : recallCase.affectedBatchIds) {
      if (batchId.equals(currentBatchId)) { continue; }
      Map<String, Object> batch = batchRepo.findById(batchId);
      if (batch != null && workOrderId.equals(batch.get("workOrderId"))
          && dispositionRepo.findByRecallIdAndBatchId(recallCase.id, batchId) == null) {
        return true;
      }
    }
    return false;
  }

  /** 全部波及批次处置完成后排查单关闭，否则标记为处置中 */
  private void refreshCaseStatus(MaterialRecallCase recallCase) {
    int disposed = dispositionRepo.findByRecallId(recallCase.id).size();
    if (disposed >= recallCase.affectedBatchIds.size()) {
      recallCase.status = RecallCaseStatus.CLOSED.name();
      log.info("{} recallNo={}", LogTemplates.RECALL_CASE_CLOSED, recallCase.recallNo);
    } else {
      recallCase.status = RecallCaseStatus.DISPOSING.name();
    }
  }
}
