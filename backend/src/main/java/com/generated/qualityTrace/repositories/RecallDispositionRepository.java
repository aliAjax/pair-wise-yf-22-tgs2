package com.generated.qualityTrace.repositories;

import com.generated.qualityTrace.models.RecallDisposition;
import com.generated.qualityTrace.utils.Formatters;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;
import org.springframework.stereotype.Repository;

@Repository
public class RecallDispositionRepository {
  private final Map<Long, RecallDisposition> rows = new LinkedHashMap<>();
  private final AtomicLong seq = new AtomicLong(0);

  public RecallDisposition save(RecallDisposition disposition) {
    long id = seq.incrementAndGet();
    disposition.id = id;
    disposition.dispositionNo = Formatters.dispositionNo(id);
    rows.put(id, disposition);
    return disposition;
  }

  public List<RecallDisposition> findAll() { return new ArrayList<>(rows.values()); }

  public List<RecallDisposition> findByRecallId(Long recallId) {
    List<RecallDisposition> result = new ArrayList<>();
    for (RecallDisposition row : rows.values()) {
      if (row.recallId.equals(recallId)) { result.add(row); }
    }
    return result;
  }

  /** 幂等键：同一排查单内同一批次只存在一条处置记录 */
  public RecallDisposition findByRecallIdAndBatchId(Long recallId, Long batchId) {
    for (RecallDisposition row : rows.values()) {
      if (row.recallId.equals(recallId) && row.batchId.equals(batchId)) { return row; }
    }
    return null;
  }
}
