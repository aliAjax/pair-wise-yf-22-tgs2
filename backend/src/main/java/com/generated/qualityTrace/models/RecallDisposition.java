package com.generated.qualityTrace.models;

/** 召回处置记录：一个排查单内同一批次只允许处置一次，重复提交返回首次记录 */
public class RecallDisposition {
  public Long id;
  public String dispositionNo;
  public Long recallId;
  public Long batchId;
  public String batchNo;
  public Long workOrderId;
  public String orderNo;
  public String action;
  public String approver;
  public String reason;
  public String disposedBy;
  public String disposedAt;
}
