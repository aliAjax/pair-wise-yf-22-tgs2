package com.generated.qualityTrace.constants;

/** 错误码集中维护：service 与 controller 分别包装业务异常，禁止只在全局处理器吞掉。 */
public final class ErrorCodes {
  public static final String AUTH_REQUIRED = "AUTH_REQUIRED";
  public static final String RBAC_DENIED = "RBAC_DENIED";

  // ---- 原料召回：排查判定 ----
  public static final String MATERIAL_LOT_REQUIRED = "MATERIAL_LOT_REQUIRED";
  public static final String MATERIAL_LOT_NOT_USED = "MATERIAL_LOT_NOT_USED";

  // ---- 原料召回：处置 ----
  public static final String RECALL_CASE_NOT_FOUND = "RECALL_CASE_NOT_FOUND";
  public static final String RECALL_CASE_CLOSED = "RECALL_CASE_CLOSED";
  public static final String BATCH_NOT_IN_RECALL = "BATCH_NOT_IN_RECALL";
  public static final String BATCH_NOT_FROZEN = "BATCH_NOT_FROZEN";
  public static final String INVALID_DISPOSITION_ACTION = "INVALID_DISPOSITION_ACTION";
  public static final String CONCESSION_APPROVER_REQUIRED = "CONCESSION_APPROVER_REQUIRED";
  public static final String CONCESSION_REASON_REQUIRED = "CONCESSION_REASON_REQUIRED";
  public static final String HANDLER_REQUIRED = "HANDLER_REQUIRED";
  public static final String BATCH_NO_REQUIRED = "BATCH_NO_REQUIRED";

  // ---- 工单完工守卫 ----
  public static final String WORK_ORDER_FROZEN = "WORK_ORDER_FROZEN";
  public static final String WORK_ORDER_PENDING_RECALL = "WORK_ORDER_PENDING_RECALL";
  public static final String WORK_ORDER_NOT_FOUND = "WORK_ORDER_NOT_FOUND";
  public static final String WORK_ORDER_NOT_RUNNING = "WORK_ORDER_NOT_RUNNING";

  private ErrorCodes() {}
}
