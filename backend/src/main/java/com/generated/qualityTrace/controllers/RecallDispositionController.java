package com.generated.qualityTrace.controllers;

import com.generated.qualityTrace.constants.LogTemplates;
import com.generated.qualityTrace.routes.RecallDispositionRoutes;
import com.generated.qualityTrace.services.RecallDispositionService;
import com.generated.qualityTrace.types.BizException;
import com.generated.qualityTrace.types.RecallDispositionPayload;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/** 接口入口-处置记录：整批报废 / 让步接收；重复处置同一批次返回第一次结果 */
@RestController
@RequestMapping(RecallDispositionRoutes.PATH)
public class RecallDispositionController {
  private static final Logger log = LoggerFactory.getLogger(RecallDispositionController.class);

  private final RecallDispositionService service;

  public RecallDispositionController(RecallDispositionService service) { this.service = service; }

  @PostMapping
  public Map<String, Object> dispose(@RequestBody RecallDispositionPayload payload) {
    try {
      return service.dispose(payload);
    } catch (BizException e) {
      log.warn("{} failed code={} message={}", LogTemplates.RECALL_DISPOSE, e.code, e.getMessage());
      throw e;
    }
  }

  @GetMapping
  public List<Map<String, Object>> list(@RequestParam(required = false) Long recallId) {
    return service.list(recallId);
  }
}
