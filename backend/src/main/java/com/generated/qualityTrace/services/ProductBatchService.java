package com.generated.qualityTrace.services;

import com.generated.qualityTrace.constants.BatchStatus;
import com.generated.qualityTrace.constants.LogTemplates;
import com.generated.qualityTrace.models.ProductBatch;
import com.generated.qualityTrace.repositories.ProductBatchRepository;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/** 产品批次业务：原清单照常可查；提供召回的冻结/报废/让步状态动作。 */
@Service
public class ProductBatchService {

  private static final Logger log = LoggerFactory.getLogger(ProductBatchService.class);

  private final ProductBatchRepository repo;

  public ProductBatchService(ProductBatchRepository repo) {
    this.repo = repo;
  }

  /** 原有批次清单照常可查。 */
  public List<ProductBatch> list() {
    return repo.findAll();
  }

  public ProductBatch getByBatchNo(String batchNo) {
    return repo.findByBatchNo(batchNo).orElse(null);
  }

  /** 未结工单上的批次在录入不合格材料批号时转质量冻结。 */
  public ProductBatch freeze(ProductBatch batch) {
    if (BatchStatus.freezable(batch.getBatchStatus())) {
      batch.setBatchStatus(BatchStatus.QUALITY_FROZEN.name());
      repo.save(batch);
      log.info(LogTemplates.RECALL_BATCH_FREEZE, batch.getBatchNo(), batch.getMaterialLotNo());
    }
    return batch;
  }

  /** 整批报废。 */
  public ProductBatch scrap(ProductBatch batch) {
    batch.setBatchStatus(BatchStatus.SCRAPPED.name());
    repo.save(batch);
    return batch;
  }

  /** 让步接收，批次恢复正常可用状态。 */
  public ProductBatch concessionAccept(ProductBatch batch) {
    batch.setBatchStatus(BatchStatus.CONCESSION_ACCEPTED.name());
    repo.save(batch);
    return batch;
  }
}
