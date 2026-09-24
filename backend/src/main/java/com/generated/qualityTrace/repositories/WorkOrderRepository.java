package com.generated.qualityTrace.repositories;

import com.generated.qualityTrace.models.WorkOrder;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import org.springframework.stereotype.Repository;

/** 工单数据访问层（本地内存库，数据由 DataSeeder 初始化）。 */
@Repository
public class WorkOrderRepository {

  private final ConcurrentMap<Long, WorkOrder> store = new ConcurrentHashMap<>();

  public List<WorkOrder> findAll() {
    return store.values().stream()
        .sorted(Comparator.comparing(WorkOrder::getId))
        .toList();
  }

  public Optional<WorkOrder> findById(Long id) {
    return Optional.ofNullable(store.get(id));
  }

  public Optional<WorkOrder> findByOrderNo(String orderNo) {
    return store.values().stream().filter(w -> orderNo.equals(w.getOrderNo())).findFirst();
  }

  public WorkOrder save(WorkOrder workOrder) {
    store.put(workOrder.getId(), workOrder);
    return workOrder;
  }

  /** DataSeeder 启动时灌种子数据。 */
  public void seed(List<WorkOrder> workOrders) {
    workOrders.forEach(w -> store.put(w.getId(), w));
  }
}
