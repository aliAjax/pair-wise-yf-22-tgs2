package com.generated.qualityTrace.models;

/**
 * 原料召回排查单（排查判定，与处置记录分开维护）。
 * 同一材料批号在存在未结案排查单时重复录入，直接返回第一次的结果（幂等）。
 */
public class MaterialRecallCase {
  private Long id;
  private String caseNo;
  private String materialLotNo;
  private String investigatorId;
  private String reason;
  private String caseStatus;
  private String createdAt;
  private String closedAt;

  public MaterialRecallCase() {}

  public Long getId() { return id; }
  public void setId(Long id) { this.id = id; }

  public String getCaseNo() { return caseNo; }
  public void setCaseNo(String caseNo) { this.caseNo = caseNo; }

  public String getMaterialLotNo() { return materialLotNo; }
  public void setMaterialLotNo(String materialLotNo) { this.materialLotNo = materialLotNo; }

  public String getInvestigatorId() { return investigatorId; }
  public void setInvestigatorId(String investigatorId) { this.investigatorId = investigatorId; }

  public String getReason() { return reason; }
  public void setReason(String reason) { this.reason = reason; }

  public String getCaseStatus() { return caseStatus; }
  public void setCaseStatus(String caseStatus) { this.caseStatus = caseStatus; }

  public String getCreatedAt() { return createdAt; }
  public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }

  public String getClosedAt() { return closedAt; }
  public void setClosedAt(String closedAt) { this.closedAt = closedAt; }
}
