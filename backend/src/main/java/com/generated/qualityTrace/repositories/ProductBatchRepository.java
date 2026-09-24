package com.generated.qualityTrace.repositories;

import com.generated.qualityTrace.constants.ProductBatchStatus;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Repository;

@Repository
public class ProductBatchRepository {
  private final Map<Long, Map<String, Object>> rows = new LinkedHashMap<>();

  public ProductBatchRepository() {
    seed(1L, "B-2026-001", 1L, 200, "ML-2026-001", "2026-09-02T10:00:00", ProductBatchStatus.NORMAL);
    seed(2L, "B-2026-002", 2L, 180, "ML-2026-001", "2026-09-03T10:00:00", ProductBatchStatus.NORMAL);
    seed(3L, "B-2026-003", 3L, 150, "ML-2026-002", "2026-08-20T10:00:00", ProductBatchStatus.FINISHED);
    seed(4L, "B-2026-004", 4L, 120, "ML-2026-001", "2026-09-05T10:00:00", ProductBatchStatus.NORMAL);
  }

  private void seed(Long id, String batchNo, Long workOrderId, int quantity, String materialLotNo,
                    String producedAt, ProductBatchStatus batchStatus) {
    Map<String, Object> row = new LinkedHashMap<>();
    row.put("id", id);
    row.put("batchNo", batchNo);
    row.put("workOrderId", workOrderId);
    row.put("quantity", quantity);
    row.put("materialLotNo", materialLotNo);
    row.put("producedAt", producedAt);
    row.put("batchStatus", batchStatus.name());
    rows.put(id, row);
  }

  public List<Map<String, Object>> findAll() { return new ArrayList<>(rows.values()); }

  public Map<String, Object> findById(Long id) { return rows.get(id); }

  public List<Map<String, Object>> findByMaterialLotNo(String materialLotNo) {
    List<Map<String, Object>> result = new ArrayList<>();
    for (Map<String, Object> row : rows.values()) {
      if (materialLotNo.equals(row.get("materialLotNo"))) { result.add(row); }
    }
    return result;
  }

  public void updateStatus(Long id, String batchStatus) {
    Map<String, Object> row = rows.get(id);
    if (row != null) { row.put("batchStatus", batchStatus); }
  }
}
