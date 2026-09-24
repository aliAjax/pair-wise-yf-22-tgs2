package com.generated.qualityTrace.controllers;

import com.generated.qualityTrace.constructors.ProductBatchDtoFactory;
import com.generated.qualityTrace.services.ProductBatchService;
import java.util.List;
import java.util.Map;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/** 产品批次接口：原有清单照常可查，召回只改状态，不新增入口。 */
@RestController
@RequestMapping("/api/product-batch")
public class ProductBatchController {

  private final ProductBatchService service;

  public ProductBatchController(ProductBatchService service) {
    this.service = service;
  }

  @GetMapping
  public List<Map<String, Object>> list() {
    return service.list().stream().map(ProductBatchDtoFactory::toResponse).toList();
  }
}
