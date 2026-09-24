package com.generated.qualityTrace.constructors;

import com.generated.qualityTrace.models.RecallDisposition;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/** 处置记录响应构造 */
public final class RecallDispositionDtoFactory {
  private RecallDispositionDtoFactory() {}

  public static Map<String, Object> create() { return Map.of("id", 1, "name", "召回处置记录"); }

  public static Map<String, Object> dispositionView(RecallDisposition d) {
    Map<String, Object> map = new LinkedHashMap<>();
    map.put("id", d.id);
    map.put("dispositionNo", d.dispositionNo);
    map.put("recallId", d.recallId);
    map.put("batchId", d.batchId);
    map.put("batchNo", d.batchNo);
    map.put("workOrderId", d.workOrderId);
    map.put("orderNo", d.orderNo);
    map.put("action", d.action);
    map.put("approver", d.approver);
    map.put("reason", d.reason);
    map.put("disposedBy", d.disposedBy);
    map.put("disposedAt", d.disposedAt);
    return map;
  }

  /** 处置结果：replayed=true 表示重复处置，返回的是第一次的处置记录 */
  public static Map<String, Object> dispositionResult(RecallDisposition d, boolean replayed, String recallStatus) {
    Map<String, Object> result = new LinkedHashMap<>();
    result.put("disposition", dispositionView(d));
    result.put("replayed", replayed);
    result.put("recallStatus", recallStatus);
    return result;
  }

  public static List<Map<String, Object>> dispositionList(List<RecallDisposition> dispositions) {
    List<Map<String, Object>> list = new ArrayList<>();
    for (RecallDisposition d : dispositions) { list.add(dispositionView(d)); }
    return list;
  }
}
