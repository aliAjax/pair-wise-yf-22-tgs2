package com.generated.qualityTrace.constants;

public final class ErrorMessages {
  public static final String AUTH_REQUIRED = "missing token";
  public static final String RBAC_DENIED = "role denied";
  public static final String VALIDATION_FAILED = "请求参数校验失败";
  public static final String MATERIAL_LOT_NOT_FOUND = "未找到使用该材料批号的产品批次";
  public static final String RECALL_NOT_FOUND = "召回排查单不存在";
  public static final String RECALL_BATCH_NOT_IN_CASE = "该批次不在此召回排查单范围内";
  public static final String CONCESSION_APPROVAL_REQUIRED = "让步接收必须登记审批人和原因";
  public static final String WORK_ORDER_NOT_FOUND = "工单不存在";
  public static final String WORK_ORDER_FROZEN = "工单处于质量冻结状态，存在未处置的原料召回，不能完工";
  public static final String WORK_ORDER_FINISH_INVALID = "当前工单状态不能执行完工";
}
