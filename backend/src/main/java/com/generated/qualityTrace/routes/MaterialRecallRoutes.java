package com.generated.qualityTrace.routes;

/**
 * 原料召回 —— 排查判定接口入口。
 * 排查判定与处置记录入口分开维护：
 *   POST   /api/material-recalls            录入不合格材料批号并冻结
 *   GET    /api/material-recalls            排查单清单
 *   GET    /api/material-recalls/{id}       排查单追溯详情
 */
public final class MaterialRecallRoutes {
  public static final String PATH = "/api/material-recalls";
  public static final String DETAIL = PATH + "/{id}";

  private MaterialRecallRoutes() {}
}
