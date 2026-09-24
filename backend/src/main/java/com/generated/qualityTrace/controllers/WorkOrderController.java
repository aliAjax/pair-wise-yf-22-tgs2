package com.generated.qualityTrace.controllers;

import com.generated.qualityTrace.constants.LogTemplates;
import com.generated.qualityTrace.routes.WorkOrderRoutes;
import com.generated.qualityTrace.services.WorkOrderService;
import com.generated.qualityTrace.types.BizException;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(WorkOrderRoutes.PATH)
public class WorkOrderController {
  private static final Logger log = LoggerFactory.getLogger(WorkOrderController.class);

  private final WorkOrderService service;

  public WorkOrderController(WorkOrderService service) { this.service = service; }

  @GetMapping
  public List<Map<String, Object>> list() { return service.list(); }

  /** 完工：质量冻结（召回未处置）的工单会被拒绝 */
  @PostMapping(WorkOrderRoutes.FINISH)
  public Map<String, Object> finish(@PathVariable Long id) {
    try {
      return service.finish(id);
    } catch (BizException e) {
      log.warn("{} failed id={} code={} message={}", LogTemplates.WORK_ORDER_FINISH, id, e.code, e.getMessage());
      throw e;
    }
  }
}
