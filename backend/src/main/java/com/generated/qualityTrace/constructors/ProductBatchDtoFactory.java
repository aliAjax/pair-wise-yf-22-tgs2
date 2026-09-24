package com.generated.qualityTrace.constructors;

import com.generated.qualityTrace.constants.BatchStatus;
import com.generated.qualityTrace.models.ProductBatch;
import java.util.LinkedHashMap;
import java.util.Map;

/** 产品批次默认表单/响应对象构造器。 */
public final class ProductBatchDtoFactory {

  private ProductBatchDtoFactory() {}

  public static Map<String, Object> create() {
    Map<String, Object> form = new LinkedHashMap<>();
    form.put("batchNo", "");
    form.put("workOrderId", "");
    form.put("quantity", "");
    form.put("materialLotNo", "");
    form.put("batchStatus", BatchStatus.NORMAL.name());
    return form;
  }

  public static Map<String, Object> toResponse(ProductBatch b) {
    Map<String, Object> body = new LinkedHashMap<>();
    body.put("id", b.getId());
    body.put("batchNo", b.getBatchNo());
    body.put("workOrderId", b.getWorkOrderId());
    body.put("quantity", b.getQuantity());
    body.put("materialLotNo", b.getMaterialLotNo());
    body.put("producedAt", b.getProducedAt());
    body.put("batchStatus", b.getBatchStatus());
    body.put("statusText", statusText(b.getBatchStatus()));
    return body;
  }

  /** 批次状态中文文案，清单筛选和详情展示共用。 */
  public static String statusText(String status) {
    if (status == null) {
      return "";
    }
    return switch (BatchStatus.valueOf(status)) {
      case NORMAL -> "正常";
      case QUALITY_FROZEN -> "质量冻结";
      case SCRAPPED -> "已报废";
      case CONCESSION_ACCEPTED -> "让步接收";
    };
  }
}
