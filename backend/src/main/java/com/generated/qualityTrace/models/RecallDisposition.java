package com.generated.qualityTrace.models;

import com.generated.qualityTrace.constants.DispositionAction;

/**
 * 召回处置记录：整批报废（SCRAP）或让步接收（CONCESSION_ACCEPT）。
 * 让步接收必须登记审批人 approverId 和原因 concessionReason。
 */
public class RecallDisposition {
  private Long id;
  private Long recallCaseId;
  private String caseNo;
  private String materialLotNo;
  private Long batchId;
  private String batchNo;
  private Long workOrderId;
  private String workOrderNo;
  private String action;
  private String handlerId;
  private String approverId;
  private String concessionReason;
  private String createdAt;

  public RecallDisposition() {}

  public boolean isConcession() {
    return DispositionAction.CONCESSION_ACCEPT.name().equals(action);
  }

  public boolean isScrap() {
    return DispositionAction.SCRAP.name().equals(action);
  }

  public Long getId() { return id; }
  public void setId(Long id) { this.id = id; }

  public Long getRecallCaseId() { return recallCaseId; }
  public void setRecallCaseId(Long recallCaseId) { this.recallCaseId = recallCaseId; }

  public String getCaseNo() { return caseNo; }
  public void setCaseNo(String caseNo) { this.caseNo = caseNo; }

  public String getMaterialLotNo() { return materialLotNo; }
  public void setMaterialLotNo(String materialLotNo) { this.materialLotNo = materialLotNo; }

  public Long getBatchId() { return batchId; }
  public void setBatchId(Long batchId) { this.batchId = batchId; }

  public String getBatchNo() { return batchNo; }
  public void setBatchNo(String batchNo) { this.batchNo = batchNo; }

  public Long getWorkOrderId() { return workOrderId; }
  public void setWorkOrderId(Long workOrderId) { this.workOrderId = workOrderId; }

  public String getWorkOrderNo() { return workOrderNo; }
  public void setWorkOrderNo(String workOrderNo) { this.workOrderNo = workOrderNo; }

  public String getAction() { return action; }
  public void setAction(String action) { this.action = action; }

  public String getHandlerId() { return handlerId; }
  public void setHandlerId(String handlerId) { this.handlerId = handlerId; }

  public String getApproverId() { return approverId; }
  public void setApproverId(String approverId) { this.approverId = approverId; }

  public String getConcessionReason() { return concessionReason; }
  public void setConcessionReason(String concessionReason) { this.concessionReason = concessionReason; }

  public String getCreatedAt() { return createdAt; }
  public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }
}
