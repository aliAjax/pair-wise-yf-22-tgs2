package com.generated.qualityTrace.types;

/**
 * 召回处置入参。action=SCRAP 整批报废；action=CONCESSION_ACCEPT 让步接收。
 * 让步接收时 approverId / concessionReason 必填。
 */
public record RecallDispositionPayload(
    String batchNo,
    String action,
    String handlerId,
    String approverId,
    String concessionReason) {}
