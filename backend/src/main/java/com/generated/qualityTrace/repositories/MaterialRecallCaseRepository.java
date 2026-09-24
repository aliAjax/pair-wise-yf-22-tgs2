package com.generated.qualityTrace.repositories;

import com.generated.qualityTrace.models.MaterialRecallCase;
import com.generated.qualityTrace.utils.Formatters;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;
import org.springframework.stereotype.Repository;

@Repository
public class MaterialRecallCaseRepository {
  private final Map<Long, MaterialRecallCase> rows = new LinkedHashMap<>();
  private final AtomicLong seq = new AtomicLong(0);

  public MaterialRecallCase save(MaterialRecallCase recallCase) {
    long id = seq.incrementAndGet();
    recallCase.id = id;
    recallCase.recallNo = Formatters.recallNo(id);
    rows.put(id, recallCase);
    return recallCase;
  }

  public MaterialRecallCase findById(Long id) { return rows.get(id); }

  public List<MaterialRecallCase> findAll() { return new ArrayList<>(rows.values()); }
}
