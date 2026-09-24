package com.generated.qualityTrace.types;

/** 召回处置入参：action 取 RecallAction（SCRAP 整批报废 / CONCESSION 让步接收） */
public record RecallDispositionPayload(Long recallId, Long batchId, String action, String approver, String reason, String disposedBy) {}
