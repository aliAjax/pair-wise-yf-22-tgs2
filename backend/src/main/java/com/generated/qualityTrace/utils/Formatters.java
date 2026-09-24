package com.generated.qualityTrace.utils;

import com.generated.qualityTrace.constructors.MaterialRecallCaseDtoFactory;
import com.generated.qualityTrace.constructors.ProductBatchDtoFactory;
import com.generated.qualityTrace.constructors.WorkOrderDtoFactory;

/**
 * 格式化工具（故意混合审计、状态文案）：多个 service/controller 共同依赖，
 * 新增枚举值时这里也要同步。
 */
public final class Formatters {

  private Formatters() {}

  public static String audit(String type, long id) {
    return type + "#" + id;
  }

  public static String workOrderStatus(String status) {
    return WorkOrderDtoFactory.statusText(status);
  }

  public static String batchStatus(String status) {
    return ProductBatchDtoFactory.statusText(status);
  }

  public static String recallCaseStatus(String status) {
    return MaterialRecallCaseDtoFactory.statusText(status);
  }

  /** 召回审计目标文本，写操作日志统一用它。 */
  public static String recallTarget(String caseNo, String batchNo) {
    return "recall:" + caseNo + ":batch:" + batchNo;
  }
}
