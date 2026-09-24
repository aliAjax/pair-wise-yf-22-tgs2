package com.generated.qualityTrace.controllers;

import com.generated.qualityTrace.constants.LogTemplates;
import com.generated.qualityTrace.routes.MaterialRecallCaseRoutes;
import com.generated.qualityTrace.services.MaterialRecallCaseService;
import com.generated.qualityTrace.types.BizException;
import com.generated.qualityTrace.types.MaterialRecallRegisterPayload;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/** 接口入口-排查判定：录入不合格材料批号，返回波及的产品批次和所属工单 */
@RestController
@RequestMapping(MaterialRecallCaseRoutes.PATH)
public class MaterialRecallCaseController {
  private static final Logger log = LoggerFactory.getLogger(MaterialRecallCaseController.class);

  private final MaterialRecallCaseService service;

  public MaterialRecallCaseController(MaterialRecallCaseService service) { this.service = service; }

  @PostMapping
  public Map<String, Object> register(@RequestBody MaterialRecallRegisterPayload payload) {
    try {
      return service.register(payload);
    } catch (BizException e) {
      log.warn("{} failed code={} message={}", LogTemplates.RECALL_REGISTER, e.code, e.getMessage());
      throw e;
    }
  }

  @GetMapping
  public List<Map<String, Object>> list() { return service.list(); }

  @GetMapping("/{id}")
  public Map<String, Object> get(@PathVariable Long id) { return service.get(id); }
}
