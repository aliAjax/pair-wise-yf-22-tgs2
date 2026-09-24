package com.generated.qualityTrace.controllers;

import com.generated.qualityTrace.models.MaterialRecallCase;
import com.generated.qualityTrace.routes.MaterialRecallRoutes;
import com.generated.qualityTrace.services.MaterialRecallService;
import java.util.Map;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

/** 原料召回排查单详情入口（独立控制器，避免与处置子路径耦合）。 */
@RestController
public class MaterialRecallCaseController {

  private final MaterialRecallService service;

  public MaterialRecallCaseController(MaterialRecallService service) {
    this.service = service;
  }

  /** 单张排查单的追溯详情：使用它的产品批次与所属工单。 */
  @GetMapping(MaterialRecallRoutes.DETAIL)
  public Map<String, Object> detail(@PathVariable("id") Long id) {
    MaterialRecallCase recallCase = service.getCase(id);
    return service.buildInvestigateResult(recallCase, false);
  }
}
