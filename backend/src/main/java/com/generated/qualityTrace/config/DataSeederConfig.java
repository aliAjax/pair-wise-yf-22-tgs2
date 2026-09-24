package com.generated.qualityTrace.config;

import com.generated.qualityTrace.constants.BatchStatus;
import com.generated.qualityTrace.constants.WorkOrderStatus;
import com.generated.qualityTrace.models.ProductBatch;
import com.generated.qualityTrace.models.WorkOrder;
import com.generated.qualityTrace.repositories.ProductBatchRepository;
import com.generated.qualityTrace.repositories.WorkOrderRepository;
import java.util.List;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 本地内存库种子数据：让"录入不合格材料批号 → 召回"开箱即可演示。
 * 不合格材料批号 M-LOT-2026-0901 同时被一个未结工单和一个已完工工单使用。
 */
@Configuration
public class DataSeederConfig {

  public static final String RECALL_LOT_NO = "M-LOT-2026-0901";
  public static final String NORMAL_LOT_NO = "M-LOT-2026-0888";

  @Bean
  CommandLineRunner seedMasterData(WorkOrderRepository workOrderRepo,
                                   ProductBatchRepository batchRepo) {
    return args -> {
      WorkOrder wo1 = workOrder(1L, "WO-240901-01", "P-AXLE-01", "传动轴组件",
          "100", "L1", "2026-09-10T08:00:00Z", WorkOrderStatus.RUNNING.name(), null);
      WorkOrder wo2 = workOrder(2L, "WO-240902-02", "P-SHELL-07", "电机外壳",
          "200", "L2", "2026-09-01T08:00:00Z", WorkOrderStatus.FINISHED.name(), null);
      WorkOrder wo3 = workOrder(3L, "WO-240903-03", "P-BRACKET-03", "安装支架",
          "300", "L1", "2026-09-12T08:00:00Z", WorkOrderStatus.PAUSED.name(), null);
      WorkOrder wo4 = workOrder(4L, "WO-240904-04", "P-COVER-11", "防尘盖",
          "150", "L3", null, WorkOrderStatus.PLANNED.name(), null);
      workOrderRepo.seed(List.of(wo1, wo2, wo3, wo4));

      ProductBatch b1 = batch(1L, "PB-240911-01", 1L, "60", RECALL_LOT_NO,
          "2026-09-11T10:00:00Z", BatchStatus.NORMAL.name());
      ProductBatch b2 = batch(2L, "PB-240902-02", 2L, "200", RECALL_LOT_NO,
          "2026-09-02T15:30:00Z", BatchStatus.NORMAL.name());
      ProductBatch b3 = batch(3L, "PB-240913-03", 3L, "80", NORMAL_LOT_NO,
          "2026-09-13T09:00:00Z", BatchStatus.NORMAL.name());
      ProductBatch b4 = batch(4L, "PB-240905-04", 2L, "40", NORMAL_LOT_NO,
          "2026-09-05T11:00:00Z", BatchStatus.NORMAL.name());
      batchRepo.seed(List.of(b1, b2, b3, b4));
    };
  }

  private WorkOrder workOrder(Long id, String orderNo, String productCode, String productName,
                              String plannedQty, String lineCode, String startAt,
                              String status, String beforeFreeze) {
    WorkOrder wo = new WorkOrder();
    wo.setId(id);
    wo.setOrderNo(orderNo);
    wo.setProductCode(productCode);
    wo.setProductName(productName);
    wo.setPlannedQty(plannedQty);
    wo.setLineCode(lineCode);
    wo.setStartAt(startAt);
    wo.setStatus(status);
    wo.setStatusBeforeFreeze(beforeFreeze);
    return wo;
  }

  private ProductBatch batch(Long id, String batchNo, Long workOrderId, String quantity,
                             String materialLotNo, String producedAt, String batchStatus) {
    ProductBatch b = new ProductBatch();
    b.setId(id);
    b.setBatchNo(batchNo);
    b.setWorkOrderId(workOrderId);
    b.setQuantity(quantity);
    b.setMaterialLotNo(materialLotNo);
    b.setProducedAt(producedAt);
    b.setBatchStatus(batchStatus);
    return b;
  }
}
