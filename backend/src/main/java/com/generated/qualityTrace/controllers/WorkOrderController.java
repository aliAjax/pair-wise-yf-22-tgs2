package com.generated.qualityTrace.controllers;

import com.generated.qualityTrace.constructors.WorkOrderDtoFactory;
import com.generated.qualityTrace.models.WorkOrder;
import com.generated.qualityTrace.routes.WorkOrderRoutes;
import com.generated.qualityTrace.services.WorkOrderService;
import com.generated.qualityTrace.types.WorkOrderFinishPayload;
import java.util.List;
import java.util.Map;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/** 生产工单接口：清单照常可查；完工接口带未处置冻结批次守卫。 */
@RestController
@RequestMapping(WorkOrderRoutes.PATH)
public class WorkOrderController {

  private final WorkOrderService service;

  public WorkOrderController(WorkOrderService service) {
    this.service = service;
  }

  @GetMapping
  public List<Map<String, Object>> list() {
    return service.list().stream().map(WorkOrderDtoFactory::toResponse).toList();
  }

  /** 未处置前不能继续完工：存在质量冻结批次时由 service 抛业务异常。 */
  @PostMapping("/{id}/finish")
  public Map<String, Object> finish(@PathVariable("id") Long id,
                                    @RequestBody(required = false) WorkOrderFinishPayload payload) {
    WorkOrder workOrder = service.finish(id, payload == null ? null : payload.operatorId());
    return WorkOrderDtoFactory.toResponse(workOrder);
  }
}
