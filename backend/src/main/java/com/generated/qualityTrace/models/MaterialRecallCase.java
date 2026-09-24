package com.generated.qualityTrace.models;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/** 原料召回排查单（排查判定）：记录不合格材料批号波及的产品批次与工单，以及冻结前的工单状态 */
public class MaterialRecallCase {
  public Long id;
  public String recallNo;
  public String materialLotNo;
  public String reason;
  public String status;
  public String createdBy;
  public String createdAt;
  public List<Long> affectedBatchIds = new ArrayList<>();
  public List<Long> affectedWorkOrderIds = new ArrayList<>();
  /** 被本排查单冻结的工单 -> 冻结前状态，让步接收放行时按此恢复 */
  public Map<Long, String> frozenWorkOrderPreviousStatus = new LinkedHashMap<>();
}
