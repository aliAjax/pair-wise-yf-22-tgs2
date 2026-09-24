package com.generated.qualityTrace.constructors;

import com.generated.qualityTrace.models.MaterialRecallCase;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/** 排查判定响应构造：排查单 + 波及批次 + 波及工单 */
public final class MaterialRecallCaseDtoFactory {
  private MaterialRecallCaseDtoFactory() {}

  public static Map<String, Object> create() { return Map.of("id", 1, "name", "原料召回排查单"); }

  public static Map<String, Object> caseSummary(MaterialRecallCase c) {
    Map<String, Object> map = new LinkedHashMap<>();
    map.put("id", c.id);
    map.put("recallNo", c.recallNo);
    map.put("materialLotNo", c.materialLotNo);
    map.put("reason", c.reason);
    map.put("status", c.status);
    map.put("createdBy", c.createdBy);
    map.put("createdAt", c.createdAt);
    map.put("affectedBatchIds", c.affectedBatchIds);
    map.put("affectedWorkOrderIds", c.affectedWorkOrderIds);
    return map;
  }

  /** 排查结果：返回使用该材料批号的产品批次和所属工单，并标注是否已转质量冻结 */
  public static Map<String, Object> investigationResult(MaterialRecallCase c,
                                                        List<Map<String, Object>> batches,
                                                        List<Map<String, Object>> workOrders) {
    Map<String, Object> result = new LinkedHashMap<>();
    result.put("recall", caseSummary(c));
    result.put("affectedBatches", batches);
    result.put("affectedWorkOrders", workOrders);
    result.put("summary", Map.of(
        "batchCount", batches.size(),
        "workOrderCount", workOrders.size(),
        "frozenWorkOrderCount", c.frozenWorkOrderPreviousStatus.size()));
    return result;
  }

  public static List<Map<String, Object>> caseList(List<MaterialRecallCase> cases) {
    List<Map<String, Object>> list = new ArrayList<>();
    for (MaterialRecallCase c : cases) { list.add(caseSummary(c)); }
    return list;
  }
}
