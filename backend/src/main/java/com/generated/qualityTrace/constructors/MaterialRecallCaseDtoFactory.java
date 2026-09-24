package com.generated.qualityTrace.constructors;

import com.generated.qualityTrace.constants.MaterialRecallStatus;
import java.util.LinkedHashMap;
import java.util.Map;

/** 原料召回排查单默认表单/响应片段构造器。 */
public final class MaterialRecallCaseDtoFactory {

  private MaterialRecallCaseDtoFactory() {}

  /** 排查判定录入默认表单。 */
  public static Map<String, Object> create() {
    Map<String, Object> form = new LinkedHashMap<>();
    form.put("materialLotNo", "");
    form.put("investigatorId", "");
    form.put("reason", "");
    return form;
  }

  /** 排查单状态文案，供清单/详情统一展示。 */
  public static String statusText(String status) {
    if (MaterialRecallStatus.OPEN.name().equals(status)) {
      return "待处置";
    }
    if (MaterialRecallStatus.PARTIALLY_DISPOSED.name().equals(status)) {
      return "部分处置";
    }
    if (MaterialRecallStatus.CLOSED.name().equals(status)) {
      return "已关闭";
    }
    return status;
  }
}
