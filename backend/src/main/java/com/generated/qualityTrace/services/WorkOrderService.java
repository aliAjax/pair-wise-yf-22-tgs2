package com.generated.qualityTrace.services;

import com.generated.qualityTrace.constants.ErrorCodes;
import com.generated.qualityTrace.constants.ErrorMessages;
import com.generated.qualityTrace.constants.LogTemplates;
import com.generated.qualityTrace.constants.WorkOrderStatus;
import com.generated.qualityTrace.repositories.WorkOrderRepository;
import com.generated.qualityTrace.types.BizException;
import com.generated.qualityTrace.utils.Formatters;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class WorkOrderService {
  private static final Logger log = LoggerFactory.getLogger(WorkOrderService.class);

  private final WorkOrderRepository repo;

  public WorkOrderService(WorkOrderRepository repo) { this.repo = repo; }

  public List<Map<String, Object>> list() { return repo.findAll(); }

  /** 完工：质量冻结（存在未处置的原料召回）的工单禁止完工 */
  public Map<String, Object> finish(Long id) {
    Map<String, Object> workOrder = repo.findById(id);
    if (workOrder == null) {
      throw new BizException(ErrorCodes.WORK_ORDER_NOT_FOUND, ErrorMessages.WORK_ORDER_NOT_FOUND);
    }
    String status = (String) workOrder.get("status");
    if (WorkOrderStatus.QUALITY_FROZEN.name().equals(status)) {
      log.warn("{} target={}", LogTemplates.WORK_ORDER_FINISH_BLOCKED, Formatters.audit("work-order", id));
      throw new BizException(ErrorCodes.WORK_ORDER_FROZEN, ErrorMessages.WORK_ORDER_FROZEN);
    }
    boolean finishable = WorkOrderStatus.RUNNING.name().equals(status)
        || WorkOrderStatus.PAUSED.name().equals(status);
    if (!finishable) {
      throw new BizException(ErrorCodes.WORK_ORDER_FINISH_INVALID, ErrorMessages.WORK_ORDER_FINISH_INVALID);
    }
    repo.updateStatus(id, WorkOrderStatus.FINISHED.name());
    log.info("{} target={} workOrderStatus={}", LogTemplates.WORK_ORDER_FINISH,
        Formatters.audit("work-order", id), WorkOrderStatus.FINISHED);
    return repo.findById(id);
  }
}
