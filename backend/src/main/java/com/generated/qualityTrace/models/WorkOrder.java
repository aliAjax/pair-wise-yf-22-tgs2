package com.generated.qualityTrace.models;

import com.generated.qualityTrace.constants.WorkOrderStatus;

/** 生产工单。冻结前状态保存在 statusBeforeFreeze，解冻/让步后恢复。 */
public class WorkOrder {
  private Long id;
  private String orderNo;
  private String productCode;
  private String productName;
  private String plannedQty;
  private String lineCode;
  private String startAt;
  private String status;
  /** 质量冻结前的工单状态，用于报废回待排产之外的恢复路径。 */
  private String statusBeforeFreeze;

  public WorkOrder() {}

  public Long getId() { return id; }
  public void setId(Long id) { this.id = id; }

  public String getOrderNo() { return orderNo; }
  public void setOrderNo(String orderNo) { this.orderNo = orderNo; }

  public String getProductCode() { return productCode; }
  public void setProductCode(String productCode) { this.productCode = productCode; }

  public String getProductName() { return productName; }
  public void setProductName(String productName) { this.productName = productName; }

  public String getPlannedQty() { return plannedQty; }
  public void setPlannedQty(String plannedQty) { this.plannedQty = plannedQty; }

  public String getLineCode() { return lineCode; }
  public void setLineCode(String lineCode) { this.lineCode = lineCode; }

  public String getStartAt() { return startAt; }
  public void setStartAt(String startAt) { this.startAt = startAt; }

  public String getStatus() { return status; }
  public void setStatus(String status) { this.status = status; }

  public String getStatusBeforeFreeze() { return statusBeforeFreeze; }
  public void setStatusBeforeFreeze(String statusBeforeFreeze) { this.statusBeforeFreeze = statusBeforeFreeze; }

  public boolean isOpen() {
    return !WorkOrderStatus.isClosed(status);
  }
}
