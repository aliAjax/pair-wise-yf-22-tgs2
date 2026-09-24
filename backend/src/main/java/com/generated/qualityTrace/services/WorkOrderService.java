package com.generated.qualityTrace.services;

import com.generated.qualityTrace.constants.BatchStatus;
import com.generated.qualityTrace.constants.ErrorCodes;
import com.generated.qualityTrace.constants.ErrorMessages;
import com.generated.qualityTrace.constants.LogTemplates;
import com.generated.qualityTrace.constants.WorkOrderStatus;
import com.generated.qualityTrace.exceptions.BusinessException;
import com.generated.qualityTrace.models.ProductBatch;
import com.generated.qualityTrace.models.WorkOrder;
import com.generated.qualityTrace.repositories.ProductBatchRepository;
import com.generated.qualityTrace.repositories.WorkOrderRepository;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/** 生产工单业务：清单照常可查；提供召回联动冻结/恢复与完工守卫。 */
@Service
public class WorkOrderService {

  private static final Logger log = LoggerFactory.getLogger(WorkOrderService.class);

  private final WorkOrderRepository repo;
  private final ProductBatchRepository batchRepo;

  public WorkOrderService(WorkOrderRepository repo, ProductBatchRepository batchRepo) {
    this.repo = repo;
    this.batchRepo = batchRepo;
  }

  /** 原有工单清单照常可查。 */
  public List<WorkOrder> list() {
    return repo.findAll();
  }

  public WorkOrder getById(Long id) {
    return repo.findById(id)
        .orElseThrow(() -> new BusinessException(
            ErrorCodes.WORK_ORDER_NOT_FOUND,
            String.format(ErrorMessages.WORK_ORDER_NOT_FOUND, id)));
  }

  /** 未结工单转质量冻结，并记住冻结前状态。 */
  public WorkOrder freeze(WorkOrder workOrder, String materialLotNo) {
    if (!WorkOrderStatus.QUALITY_FROZEN.name().equals(workOrder.getStatus())) {
      workOrder.setStatusBeforeFreeze(workOrder.getStatus());
      workOrder.setStatus(WorkOrderStatus.QUALITY_FROZEN.name());
      repo.save(workOrder);
      log.info(LogTemplates.WORK_ORDER_FREEZE, workOrder.getOrderNo(), materialLotNo);
    }
    return workOrder;
  }

  /**
   * 整批报废后工单回到待排产（PLANNED）。
   */
  public WorkOrder backToPlanned(WorkOrder workOrder, String scrappedBatchNo) {
    workOrder.setStatus(WorkOrderStatus.PLANNED.name());
    workOrder.setStatusBeforeFreeze(null);
    repo.save(workOrder);
    log.info(LogTemplates.WORK_ORDER_BACK_TO_PLAN, workOrder.getOrderNo(), scrappedBatchNo);
    return workOrder;
  }

  /**
   * 让步接收：冻结批次处置完后，工单恢复到冻结前状态（RUNNING/PAUSED/PLANNED）。
   * 如果该工单还有其他未处置的冻结批次，则保持质量冻结。
   */
  public WorkOrder resumeIfNoFrozenBatches(WorkOrder workOrder) {
    boolean stillFrozen = batchRepo.findByWorkOrderId(workOrder.getId()).stream()
        .anyMatch(b -> BatchStatus.frozen(b.getBatchStatus()));
    if (stillFrozen) {
      return workOrder;
    }
    String previous = workOrder.getStatusBeforeFreeze();
    workOrder.setStatus(previous == null ? WorkOrderStatus.PAUSED.name() : previous);
    workOrder.setStatusBeforeFreeze(null);
    repo.save(workOrder);
    log.info(LogTemplates.WORK_ORDER_RESUME, workOrder.getOrderNo(), workOrder.getStatus());
    return workOrder;
  }

  /**
   * 未处置前不能继续完工：质量冻结中的工单，或仍有质量冻结批次的工单禁止完工。
   */
  public WorkOrder finish(Long id, String operatorId) {
    WorkOrder workOrder = getById(id);
    List<ProductBatch> frozenBatches = batchRepo.findByWorkOrderId(id).stream()
        .filter(b -> BatchStatus.frozen(b.getBatchStatus()))
        .toList();

    if (WorkOrderStatus.QUALITY_FROZEN.name().equals(workOrder.getStatus())
        || !frozenBatches.isEmpty()) {
      log.warn(LogTemplates.WORK_ORDER_FINISH_BLOCKED, workOrder.getOrderNo(), frozenBatches.size());
      if (WorkOrderStatus.QUALITY_FROZEN.name().equals(workOrder.getStatus())) {
        throw new BusinessException(
            ErrorCodes.WORK_ORDER_FROZEN,
            String.format(ErrorMessages.WORK_ORDER_FROZEN, workOrder.getOrderNo()));
      }
      throw new BusinessException(
          ErrorCodes.WORK_ORDER_PENDING_RECALL,
          String.format(ErrorMessages.WORK_ORDER_PENDING_RECALL, workOrder.getOrderNo()));
    }

    if (!WorkOrderStatus.RUNNING.name().equals(workOrder.getStatus())) {
      throw new BusinessException(
          ErrorCodes.WORK_ORDER_NOT_RUNNING,
          String.format(ErrorMessages.WORK_ORDER_NOT_RUNNING,
              workOrder.getOrderNo(), workOrder.getStatus()));
    }

    workOrder.setStatus(WorkOrderStatus.FINISHED.name());
    repo.save(workOrder);
    log.info(LogTemplates.WORK_ORDER_FINISH, workOrder.getOrderNo());
    return workOrder;
  }
}
