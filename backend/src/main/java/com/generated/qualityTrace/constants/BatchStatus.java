package com.generated.qualityTrace.constants;

/** 产品批次状态：质量冻结、整批报废、让步接收。 */
public enum BatchStatus {
  NORMAL,
  QUALITY_FROZEN,
  SCRAPPED,
  CONCESSION_ACCEPTED;

  /** 可被召回冻结的批次（已报废/已让步的不重复冻结）。 */
  public static boolean freezable(String status) {
    return NORMAL.name().equals(status);
  }

  public static boolean frozen(String status) {
    return QUALITY_FROZEN.name().equals(status);
  }
}
