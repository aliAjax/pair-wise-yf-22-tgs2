package com.generated.qualityTrace.types;

/**
 * 录入不合格材料批号（排查判定入参）。
 *
 * @param materialLotNo 不合格材料批号
 * @param investigatorId 排查人
 * @param reason 不合格描述/来源
 */
public record MaterialRecallInvestigatePayload(
    String materialLotNo,
    String investigatorId,
    String reason) {}
