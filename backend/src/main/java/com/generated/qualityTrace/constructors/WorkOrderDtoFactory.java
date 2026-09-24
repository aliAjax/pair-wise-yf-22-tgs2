package com.generated.qualityTrace.constructors;

import com.generated.qualityTrace.constants.WorkOrderStatus;
import com.generated.qualityTrace.models.WorkOrder;
import java.util.LinkedHashMap;
import java.util.Map;

/** 生产工单默认表单/响应对象构造器，清单、详情、日志出参统一走这里。 */
public final class WorkOrderDtoFactory {

  private WorkOrderDtoFactory() {}

  public static Map<String, Object> create() {
    Map<String, Object> form = new LinkedHashMap<>();
    form.put("orderNo", "");
    form.put("productCode", "");
    form.put("productName", "");
    form.put("plannedQty", "");
    form.put("lineCode", "");
    form.put("status", WorkOrderStatus.PLANNED.name());
    return form;
  }

  public static Map<String, Object> toResponse(WorkOrder w) {
    Map<String, Object> body = new LinkedHashMap<>();
    body.put("id", w.getId());
    body.put("orderNo", w.getOrderNo());
    body.put("productCode", w.getProductCode());
    body.put("productName", w.getProductName());
    body.put("plannedQty", w.getPlannedQty());
    body.put("lineCode", w.getLineCode());
    body.put("startAt", w.getStartAt());
    body.put("status", w.getStatus());
    body.put("statusText", statusText(w.getStatus()));
    body.put("statusBeforeFreeze", w.getStatusBeforeFreeze());
    body.put("qualityFrozen", WorkOrderStatus.QUALITY_FROZEN.name().equals(w.getStatus()));
    return body;
  }

  /** 工单状态中文文案，清单筛选和详情展示共用。 */
  public static String statusText(String status) {
    if (status == null) {
      return "";
    }
    return switch (WorkOrderStatus.valueOf(status)) {
      case PLANNED -> "待排产";
      case RUNNING -> "进行中";
      case PAUSED -> "已暂停";
      case FINISHED -> "已完工";
      case CANCELLED -> "已取消";
      case QUALITY_FROZEN -> "质量冻结";
    };
  }
}
