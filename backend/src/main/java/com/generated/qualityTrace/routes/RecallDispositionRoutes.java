package com.generated.qualityTrace.routes;

/**
 * 原料召回 —— 处置记录接口入口（与排查判定分开维护）：
 *   POST /api/material-recalls/{caseId}/dispositions  整批报废 / 让步接收
 *   GET  /api/material-recalls/{caseId}/dispositions   某排查单处置记录
 *   GET  /api/recall-dispositions                     全部处置记录
 */
public final class RecallDispositionRoutes {
  public static final String PATH_BY_CASE = "/api/material-recalls/{caseId}/dispositions";
  public static final String ALL = "/api/recall-dispositions";

  private RecallDispositionRoutes() {}
}
