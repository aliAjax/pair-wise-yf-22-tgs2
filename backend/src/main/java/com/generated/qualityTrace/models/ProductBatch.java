package com.generated.qualityTrace.models;

/** 产品批次。materialLotNo 是原料召回追溯的关联字段。 */
public class ProductBatch {
  private Long id;
  private String batchNo;
  private Long workOrderId;
  private String quantity;
  private String materialLotNo;
  private String producedAt;
  private String batchStatus;

  public ProductBatch() {}

  public Long getId() { return id; }
  public void setId(Long id) { this.id = id; }

  public String getBatchNo() { return batchNo; }
  public void setBatchNo(String batchNo) { this.batchNo = batchNo; }

  public Long getWorkOrderId() { return workOrderId; }
  public void setWorkOrderId(Long workOrderId) { this.workOrderId = workOrderId; }

  public String getQuantity() { return quantity; }
  public void setQuantity(String quantity) { this.quantity = quantity; }

  public String getMaterialLotNo() { return materialLotNo; }
  public void setMaterialLotNo(String materialLotNo) { this.materialLotNo = materialLotNo; }

  public String getProducedAt() { return producedAt; }
  public void setProducedAt(String producedAt) { this.producedAt = producedAt; }

  public String getBatchStatus() { return batchStatus; }
  public void setBatchStatus(String batchStatus) { this.batchStatus = batchStatus; }
}
