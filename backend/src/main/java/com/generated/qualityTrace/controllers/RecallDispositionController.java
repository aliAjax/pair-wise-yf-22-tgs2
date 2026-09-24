package com.generated.qualityTrace.controllers;

import com.generated.qualityTrace.constructors.RecallDispositionDtoFactory;
import com.generated.qualityTrace.routes.RecallDispositionRoutes;
import com.generated.qualityTrace.services.RecallDispositionService;
import com.generated.qualityTrace.types.RecallDispositionPayload;
import java.util.List;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

/**
 * 原料召回 —— 处置记录接口（与排查判定入口分开维护）。
 */
@RestController
public class RecallDispositionController {

  private final RecallDispositionService service;

  public RecallDispositionController(RecallDispositionService service) {
    this.service = service;
  }

  /** 整批报废 / 让步接收；重复处置同一批次返回第一次结果。 */
  @PostMapping(RecallDispositionRoutes.PATH_BY_CASE)
  @ResponseStatus(HttpStatus.CREATED)
  public Map<String, Object> dispose(@PathVariable("caseId") Long caseId,
                                     @RequestBody RecallDispositionPayload payload) {
    return service.dispose(caseId, payload);
  }

  /** 某排查单下的处置记录。 */
  @GetMapping(RecallDispositionRoutes.PATH_BY_CASE)
  public List<Map<String, Object>> listByCase(@PathVariable("caseId") Long caseId) {
    return service.listByCase(caseId).stream()
        .map(d -> RecallDispositionDtoFactory.toResponse(d, false))
        .toList();
  }

  /** 全部处置记录清单照常可查。 */
  @GetMapping(RecallDispositionRoutes.ALL)
  public List<Map<String, Object>> listAll() {
    return service.list().stream()
        .map(d -> RecallDispositionDtoFactory.toResponse(d, false))
        .toList();
  }
}
