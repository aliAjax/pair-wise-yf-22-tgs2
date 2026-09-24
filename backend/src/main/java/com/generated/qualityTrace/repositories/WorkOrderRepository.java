package com.generated.qualityTrace.repositories;

import com.generated.qualityTrace.constants.WorkOrderStatus;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Repository;

@Repository
public class WorkOrderRepository {
  private final Map<Long, Map<String, Object>> rows = new LinkedHashMap<>();

  public WorkOrderRepository() {
    seed(1L, "WO-1001", "P-100", "齿轮", 500, "L1", "2026-09-01T08:00:00", WorkOrderStatus.RUNNING);
    seed(2L, "WO-1002", "P-100", "齿轮", 400, "L2", "2026-09-02T08:00:00", WorkOrderStatus.RUNNING);
    seed(3L, "WO-1003", "P-200", "传动轴", 300, "L1", "2026-08-10T08:00:00", WorkOrderStatus.FINISHED);
    seed(4L, "WO-1004", "P-300", "壳体", 200, "L3", "2026-09-06T08:00:00", WorkOrderStatus.PLANNED);
  }

  private void seed(Long id, String orderNo, String productCode, String productName, int plannedQty,
                    String lineCode, String startAt, WorkOrderStatus status) {
    Map<String, Object> row = new LinkedHashMap<>();
    row.put("id", id);
    row.put("orderNo", orderNo);
    row.put("productCode", productCode);
    row.put("productName", productName);
    row.put("plannedQty", plannedQty);
    row.put("lineCode", lineCode);
    row.put("startAt", startAt);
    row.put("status", status.name());
    rows.put(id, row);
  }

  public List<Map<String, Object>> findAll() { return new ArrayList<>(rows.values()); }

  public Map<String, Object> findById(Long id) { return rows.get(id); }

  public void updateStatus(Long id, String status) {
    Map<String, Object> row = rows.get(id);
    if (row != null) { row.put("status", status); }
  }
}
