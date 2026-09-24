package com.generated.qualityTrace.repositories;

import com.generated.qualityTrace.models.ProductBatch;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import org.springframework.stereotype.Repository;

/** 产品批次数据访问层；原料召回通过 materialLotNo 反查批次。 */
@Repository
public class ProductBatchRepository {

  private final ConcurrentMap<Long, ProductBatch> store = new ConcurrentHashMap<>();

  public List<ProductBatch> findAll() {
    return store.values().stream()
        .sorted(Comparator.comparing(ProductBatch::getId))
        .toList();
  }

  public Optional<ProductBatch> findById(Long id) {
    return Optional.ofNullable(store.get(id));
  }

  public Optional<ProductBatch> findByBatchNo(String batchNo) {
    return store.values().stream().filter(b -> batchNo.equals(b.getBatchNo())).findFirst();
  }

  /** 录入不合格材料批号后，返回用过它的全部产品批次（含已结工单上的批次）。 */
  public List<ProductBatch> findByMaterialLotNo(String materialLotNo) {
    return store.values().stream()
        .filter(b -> materialLotNo.equals(b.getMaterialLotNo()))
        .sorted(Comparator.comparing(ProductBatch::getId))
        .toList();
  }

  public List<ProductBatch> findByWorkOrderId(Long workOrderId) {
    return store.values().stream()
        .filter(b -> workOrderId.equals(b.getWorkOrderId()))
        .sorted(Comparator.comparing(ProductBatch::getId))
        .toList();
  }

  public ProductBatch save(ProductBatch batch) {
    store.put(batch.getId(), batch);
    return batch;
  }

  /** DataSeeder 启动时灌种子数据，原清单照常可查。 */
  public void seed(List<ProductBatch> batches) {
    batches.forEach(b -> store.put(b.getId(), b));
  }
}
