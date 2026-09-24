package com.generated.qualityTrace.constants;

/** 日志模板集中维护：每个实体至少 4 条，所有写操作都要记录日志。 */
public final class LogTemplates {
  public static final String CREATE = "create";
  public static final String UPDATE = "update";
  public static final String STATUS = "status";
  public static final String EXPORT = "export";

  // ---- 生产工单（含召回联动）----
  public static final String WORK_ORDER_LIST = "work_order.list";
  public static final String WORK_ORDER_CREATE = "work_order.create orderNo=%s";
  public static final String WORK_ORDER_FINISH = "work_order.finish orderNo=%s";
  public static final String WORK_ORDER_FREEZE = "work_order.quality_freeze orderNo=%s materialLot=%s";
  public static final String WORK_ORDER_RESUME = "work_order.resume orderNo=%s status=%s";
  public static final String WORK_ORDER_BACK_TO_PLAN = "work_order.back_to_planned orderNo=%s reason=scrap batchNo=%s";
  public static final String WORK_ORDER_FINISH_BLOCKED = "work_order.finish_blocked orderNo=%s frozenBatches=%d";

  // ---- 原料召回：排查判定 ----
  public static final String RECALL_INVESTIGATE = "material_recall.investigate materialLot=%s caseNo=%s affectedBatches=%d frozenWorkOrders=%d";
  public static final String RECALL_IDEMPOTENT_HIT = "material_recall.investigate_idempotent materialLot=%s caseNo=%s";
  public static final String RECALL_BATCH_FREEZE = "material_recall.batch_freeze batchNo=%s materialLot=%s";
  public static final String RECALL_CASE_CLOSE = "material_recall.case_close caseNo=%s";

  // ---- 原料召回：处置记录 ----
  public static final String RECALL_DISPOSE = "recall_disposition.dispose caseNo=%s batchNo=%s action=%s handler=%s";
  public static final String RECALL_DISPOSE_IDEMPOTENT = "recall_disposition.dispose_idempotent batchNo=%s dispositionId=%s";
  public static final String RECALL_SCRAP = "recall_disposition.scrap batchNo=%s workOrder=%s";
  public static final String RECALL_CONCESSION = "recall_disposition.concession batchNo=%s approver=%s";

  private LogTemplates() {}
}
