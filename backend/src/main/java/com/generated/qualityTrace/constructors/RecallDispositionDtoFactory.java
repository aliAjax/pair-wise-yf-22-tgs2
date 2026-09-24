package com.generated.qualityTrace.constructors;

import com.generated.qualityTrace.constants.DispositionAction;
import com.generated.qualityTrace.models.RecallDisposition;
import java.util.LinkedHashMap;
import java.util.Map;

/** 召回处置记录响应/默认表单对象构造器，页面、service 不散写结构。 */
public final class RecallDispositionDtoFactory {

  private RecallDispositionDtoFactory() {}

  /** 处置接口默认表单结构。 */
  public static Map<String, Object> create() {
    Map<String, Object> form = new LinkedHashMap<>();
    form.put("batchNo", "");
    form.put("action", DispositionAction.SCRAP.name());
    form.put("handlerId", "");
    form.put("approverId", "");
    form.put("concessionReason", "");
    return form;
  }

  /** 处置结果响应；idempotent=true 表示命中重复处置，返回的是第一次结果。 */
  public static Map<String, Object> toResponse(RecallDisposition d, boolean idempotent) {
    Map<String, Object> body = new LinkedHashMap<>();
    body.put("idempotent", idempotent);
    body.put("dispositionId", d.getId());
    body.put("caseId", d.getRecallCaseId());
    body.put("caseNo", d.getCaseNo());
    body.put("materialLotNo", d.getMaterialLotNo());
    body.put("batchId", d.getBatchId());
    body.put("batchNo", d.getBatchNo());
    body.put("workOrderId", d.getWorkOrderId());
    body.put("workOrderNo", d.getWorkOrderNo());
    body.put("action", d.getAction());
    body.put("actionText", DispositionAction.SCRAP.name().equals(d.getAction())
        ? "整批报废" : "让步接收");
    body.put("handlerId", d.getHandlerId());
    body.put("approverId", d.getApproverId());
    body.put("concessionReason", d.getConcessionReason());
    body.put("createdAt", d.getCreatedAt());
    body.put("resultingBatchStatus", DispositionAction.SCRAP.name().equals(d.getAction())
        ? "SCRAPPED" : "CONCESSION_ACCEPTED");
    body.put("resultingWorkOrderStatus", DispositionAction.SCRAP.name().equals(d.getAction())
        ? "PLANNED" : "RESTORED");
    return body;
  }
}
