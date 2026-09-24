package com.generated.qualityTrace.repositories;

import com.generated.qualityTrace.models.MaterialRecallCase;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicLong;
import org.springframework.stereotype.Repository;

/** 原料召回排查单数据访问层。 */
@Repository
public class MaterialRecallCaseRepository {

  private final ConcurrentMap<Long, MaterialRecallCase> store = new ConcurrentHashMap<>();
  private final AtomicLong idSequence = new AtomicLong(0);

  public List<MaterialRecallCase> findAll() {
    return store.values().stream()
        .sorted(Comparator.comparing(MaterialRecallCase::getId))
        .toList();
  }

  public Optional<MaterialRecallCase> findById(Long id) {
    return Optional.ofNullable(store.get(id));
  }

  public Optional<MaterialRecallCase> findByCaseNo(String caseNo) {
    return store.values().stream().filter(c -> caseNo.equals(c.getCaseNo())).findFirst();
  }

  /**
   * 幂等关键：同一材料批号只要还有未结案（OPEN / PARTIALLY_DISPOSED）的排查单，
   * 重复录入就返回第一次的那张。
   */
  public Optional<MaterialRecallCase> findOpenByMaterialLotNo(String materialLotNo) {
    return store.values().stream()
        .filter(c -> materialLotNo.equals(c.getMaterialLotNo()))
        .filter(c -> !"CLOSED".equals(c.getCaseStatus()))
        .sorted(Comparator.comparing(MaterialRecallCase::getId))
        .findFirst();
  }

  public MaterialRecallCase save(MaterialRecallCase recallCase) {
    if (recallCase.getId() == null) {
      recallCase.setId(idSequence.incrementAndGet());
    }
    store.put(recallCase.getId(), recallCase);
    return recallCase;
  }
}
