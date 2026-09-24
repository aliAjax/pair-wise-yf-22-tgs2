package com.generated.qualityTrace.constants;

/** 生产工单状态：未结工单在原料召回时会转为 QUALITY_FROZEN（质量冻结）。 */
public enum WorkOrderStatus {
  PLANNED,
  RUNNING,
  PAUSED,
  FINISHED,
  CANCELLED,
  QUALITY_FROZEN;

  /** 已结工单（完工/取消）只追溯、不冻结。 */
  public static boolean isClosed(String status) {
    return FINISHED.name().equals(status) || CANCELLED.name().equals(status);
  }
}
