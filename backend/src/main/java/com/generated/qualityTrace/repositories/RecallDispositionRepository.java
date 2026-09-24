package com.generated.qualityTrace.repositories;

import com.generated.qualityTrace.models.RecallDisposition;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicLong;
import org.springframework.stereotype.Repository;

/** 召回处置记录数据访问层（与排查判定分开维护）。 */
@Repository
public class RecallDispositionRepository {

  private final ConcurrentMap<Long, RecallDisposition> store = new ConcurrentHashMap<>();
  private final AtomicLong idSequence = new AtomicLong(0);

  public List<RecallDisposition> findAll() {
    return store.values().stream()
        .sorted(Comparator.comparing(RecallDisposition::getId))
        .toList();
  }

  public Optional<RecallDisposition> findById(Long id) {
    return Optional.ofNullable(store.get(id));
  }

  public List<RecallDisposition> findByCaseId(Long caseId) {
    return store.values().stream()
        .filter(d -> caseId.equals(d.getRecallCaseId()))
        .sorted(Comparator.comparing(RecallDisposition::getId))
        .toList();
  }

  /** 重复处置同一批次返回第一次结果。 */
  public Optional<RecallDisposition> findByBatchId(Long batchId) {
    return store.values().stream()
        .filter(d -> batchId.equals(d.getBatchId()))
        .sorted(Comparator.comparing(RecallDisposition::getId))
        .findFirst();
  }

  public RecallDisposition save(RecallDisposition disposition) {
    if (disposition.getId() == null) {
      disposition.setId(idSequence.incrementAndGet());
    }
    store.put(disposition.getId(), disposition);
    return disposition;
  }
}
