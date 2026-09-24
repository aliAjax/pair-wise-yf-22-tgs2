package com.generated.qualityTrace.constants;

/** 错误消息模板集中维护；占位符使用 String.format 约定。 */
public final class ErrorMessages {
  public static final String AUTH_REQUIRED = "missing token";
  public static final String RBAC_DENIED = "role denied";

  public static final String MATERIAL_LOT_REQUIRED = "材料批号不能为空";
  public static final String MATERIAL_LOT_NOT_USED = "材料批号 %s 未被任何产品批次使用";

  public static final String RECALL_CASE_NOT_FOUND = "召回排查单不存在: %s";
  public static final String RECALL_CASE_CLOSED = "召回排查单 %s 已全部处置关闭，不能重复处置";
  public static final String BATCH_NOT_IN_RECALL = "产品批次 %s 不属于召回单 %s 的影响范围";
  public static final String BATCH_NOT_FROZEN = "产品批次 %s 当前未处于质量冻结状态，不能处置";
  public static final String INVALID_DISPOSITION_ACTION = "无效的处置方式 %s，仅支持 SCRAP（整批报废）/ CONCESSION_ACCEPT（让步接收）";
  public static final String CONCESSION_APPROVER_REQUIRED = "让步接收必须登记审批人";
  public static final String CONCESSION_REASON_REQUIRED = "让步接收必须登记原因";
  public static final String HANDLER_REQUIRED = "处理人不能为空";
  public static final String BATCH_NO_REQUIRED = "待处置产品批次号不能为空";

  public static final String WORK_ORDER_FROZEN = "工单 %s 处于质量冻结状态，处置完成前不能完工";
  public static final String WORK_ORDER_PENDING_RECALL = "工单 %s 存在未处置的质量冻结批次，处置完成前不能完工";
  public static final String WORK_ORDER_NOT_FOUND = "工单不存在: %s";
  public static final String WORK_ORDER_NOT_RUNNING = "工单 %s 当前状态为 %s，只有进行中的工单可以完工";

  private ErrorMessages() {}
}
