package com.generated.qualityTrace.controllers;

import com.generated.qualityTrace.constructors.MaterialRecallCaseDtoFactory;
import com.generated.qualityTrace.models.MaterialRecallCase;
import com.generated.qualityTrace.routes.MaterialRecallRoutes;
import com.generated.qualityTrace.services.MaterialRecallService;
import com.generated.qualityTrace.types.MaterialRecallInvestigatePayload;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

/**
 * 原料召回 —— 排查判定接口（与处置接口入口分开维护）。
 */
@RestController
public class MaterialRecallController {

  private final MaterialRecallService service;

  public MaterialRecallController(MaterialRecallService service) {
    this.service = service;
  }

  /** 录入不合格材料批号，返回使用它的产品批次和所属工单，未结对象转质量冻结。 */
  @PostMapping(MaterialRecallRoutes.PATH)
  @ResponseStatus(HttpStatus.CREATED)
  public Map<String, Object> investigate(@RequestBody MaterialRecallInvestigatePayload payload) {
    return service.investigate(payload);
  }

  /** 排查判定清单照常可查。 */
  @GetMapping(MaterialRecallRoutes.PATH)
  public List<Map<String, Object>> list() {
    return service.listCases().stream().map(this::summary).toList();
  }

  private Map<String, Object> summary(MaterialRecallCase c) {
    Map<String, Object> body = new LinkedHashMap<>();
    body.put("caseId", c.getId());
    body.put("caseNo", c.getCaseNo());
    body.put("materialLotNo", c.getMaterialLotNo());
    body.put("investigatorId", c.getInvestigatorId());
    body.put("reason", c.getReason());
    body.put("caseStatus", c.getCaseStatus());
    body.put("statusText", MaterialRecallCaseDtoFactory.statusText(c.getCaseStatus()));
    body.put("createdAt", c.getCreatedAt());
    body.put("closedAt", c.getClosedAt());
    return body;
  }
}
