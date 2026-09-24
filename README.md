# 制造业质量追溯 API 服务

面向小型制造工厂的批次质量追溯后端服务，覆盖工单、批次、检验项、不良记录和追溯查询，并支持原料召回处置：录入不合格材料批号即可排查波及的产品批次与所属工单，未结工单与批次自动转为质量冻结，处理人可整批报废或让步接收。

## 快速启动

```bash
cp .env.example .env && docker compose up -d
```

## 访问地址或 CLI 示例

后端健康检查：<http://localhost:21114/health>

### 原料召回处置流程

```bash
# 1. 排查判定：录入不合格材料批号，返回使用它的产品批次和所属工单，
#    未结工单/批次同步转为质量冻结（QUALITY_FROZEN）
curl -X POST http://localhost:21114/api/material-recalls \
  -H 'Content-Type: application/json' \
  -d '{"materialLotNo":"ML-2026-001","reason":"来料抽检不合格","createdBy":"zhangsan"}'

# 2. 整批报废：批次转为 SCRAPPED，工单回到待排产（PLANNED）
curl -X POST http://localhost:21114/api/recall-dispositions \
  -H 'Content-Type: application/json' \
  -d '{"recallId":1,"batchId":1,"action":"SCRAP","disposedBy":"lisi"}'

# 3. 让步接收：必须登记审批人和原因，批次放行，工单恢复冻结前状态
curl -X POST http://localhost:21114/api/recall-dispositions \
  -H 'Content-Type: application/json' \
  -d '{"recallId":1,"batchId":2,"action":"CONCESSION","approver":"wangwu","reason":"偏差可接受","disposedBy":"lisi"}'

# 重复处置同一批次：返回第一次的处置结果（replayed=true），不重复执行
# 未处置前完工冻结工单会被拒绝（WORK_ORDER_FROZEN）
curl -X POST http://localhost:21114/api/work-order/2/finish

# 排查单与处置记录清单
curl http://localhost:21114/api/material-recalls
curl http://localhost:21114/api/material-recalls/1
curl 'http://localhost:21114/api/recall-dispositions?recallId=1'
```

原有清单接口照常可查：`GET /api/work-order`、`GET /api/product-batch`、`GET /api/quality-inspection`、`GET /api/inspection-item-result`、`GET /api/defect-record`。

## 本地开发方式

- 后端：进入 `backend` 后按技术栈运行开发命令，接口统一挂在 `/api`。

## 技术栈

| 层 | 技术 |
|---|---|
| 前端 | - |
| 后端 | Spring Boot 3 + Java 17 + MyBatis-Plus |
| 数据库 | PostgreSQL 15 |
| 部署 | Docker Compose |

## 项目目录结构

```text
backend/src/routes, controllers, services, models, repositories, middlewares, constants, constructors, validators, utils, types, config
```

原料召回相关文件按“排查判定 / 处置记录 / 接口入口”分开维护：

- 排查判定：`models/MaterialRecallCase`、`services/MaterialRecallCaseService`、`repositories/MaterialRecallCaseRepository`、`constructors/MaterialRecallCaseDtoFactory`、`validators/MaterialRecallRegisterValidator`
- 处置记录：`models/RecallDisposition`、`services/RecallDispositionService`、`repositories/RecallDispositionRepository`、`constructors/RecallDispositionDtoFactory`、`validators/RecallDispositionValidator`
- 接口入口：`routes/MaterialRecallCaseRoutes` + `controllers/MaterialRecallCaseController`、`routes/RecallDispositionRoutes` + `controllers/RecallDispositionController`

## 环境变量说明

- `COMPOSE_PROJECT_NAME`: Compose 项目名，默认 `quality-trace`
- `BACKEND_PORT`: 后端端口，默认 `21114`
- `DB_PORT`: 数据库宿主机端口
- `DB_USER/DB_PASSWORD/DB_NAME`: 本地数据库凭据

## Docker 部署说明

- 根 Compose 文件不写 `version`，顶层 `name: quality-trace`。
- 容器名均使用 `${COMPOSE_PROJECT_NAME:-quality-trace}` 前缀。
- 数据库使用命名卷，避免绑定中文路径。
- 常见问题：端口占用时修改 `.env` 中端口后重启；需要重置数据时执行 `docker compose down -v`。

## 枚举/常量出现位置清单

- WorkOrderStatus（含召回新增的质量冻结值 QUALITY_FROZEN）: constants/WorkOrderStatus、services/WorkOrderService（完工拦截）、services/MaterialRecallCaseService（冻结/解冻判定）、services/RecallDispositionService（报废回待排产、让步恢复）、repositories/WorkOrderRepository（种子数据）、logTemplates、errorMessages、controllers/WorkOrderController 均有引用。
- InspectionResultStatus: constants/InspectionResultStatus、types、constructors、logTemplates、errorMessages、筛选器、展示组件/控制器均有引用。
- DefectSeverity: constants/DefectSeverity、types、constructors、logTemplates、errorMessages、筛选器、展示组件/控制器均有引用。
- ProductBatchStatus（NORMAL / QUALITY_FROZEN / SCRAPPED / CONCESSION_RELEASED / FINISHED）: constants/ProductBatchStatus、services/MaterialRecallCaseService、services/RecallDispositionService、repositories/ProductBatchRepository（种子数据）均有引用。
- RecallCaseStatus（FROZEN / DISPOSING / CLOSED）: constants/RecallCaseStatus、services/MaterialRecallCaseService、services/RecallDispositionService、constructors/MaterialRecallCaseDtoFactory 均有引用。
- RecallAction（SCRAP / CONCESSION）: constants/RecallAction、validators/RecallDispositionValidator、services/RecallDispositionService、types/RecallDispositionPayload 均有引用。

## 为什么会牵一发动全身

实体字段、枚举、日志模板、错误消息、构造器、筛选器和展示组件被刻意拆散到多个目录；修改一个状态值通常需要同步类型、构造器、服务、控制器、store、页面、README 与数据库种子。例如新增一种召回处置方式，需要同时改动 RecallAction 常量、RecallDispositionValidator 校验、RecallDispositionService 分支、RecallDispositionPayload 类型、日志模板与 README 清单。

## License

MIT
