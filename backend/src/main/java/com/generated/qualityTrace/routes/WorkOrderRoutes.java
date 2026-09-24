package com.generated.qualityTrace.routes;

/** 工单接口入口（完工守卫挂在 FINISH 子路径）。 */
public final class WorkOrderRoutes {
  public static final String PATH = "/api/work-order";
  public static final String FINISH = PATH + "/{id}/finish";

  private WorkOrderRoutes() {}
}
