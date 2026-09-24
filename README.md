# 制造业质量追溯 API 服务

面向小型制造工厂的批次质量追溯后端服务，覆盖工单、批次、检验项、不良记录、追溯查询，并提供**原料召回处置**：录入不合格材料批号即可反查使用它的产品批次与所属工单，未结工单/批次自动转质量冻结，处理人可整批报废或让步接收。

## 快速启动

```bash
cp .env.example .env && docker compose up -d
```

健康检查：<http://localhost:21114/health>

## 原料召回处置流程

车间材料不合格时，不再需要挨个问订单。追溯服务提供三段式能力，**排查判定、处置记录、接口入口均分开维护**：

1. **录入排查（判定）**：`POST /api/material-recalls` 提交不合格材料批号，服务返回所有使用该批号的产品批次及其所属工单；未结工单（RUNNING/PAUSED/PLANNED）与批次立即转为**质量冻结（QUALITY_FROZEN）**，已结工单（FINISHED/CANCELLED）只追溯不冻结。
2. **处置**：`POST /api/material-recalls/{caseId}/dispositions`
   - `SCRAP`（整批报废）：批次置为 SCRAPPED，工单回到**待排产（PLANNED）**；
   - `CONCESSION_ACCEPT`（让步接收）：必须登记审批人 `approverId` 和原因 `concessionReason`，批次置为 CONCESSION_ACCEPTED，工单恢复冻结前状态。
   - **未处置（质量冻结）前工单不能继续完工**：`POST /api/work-order/{id}/finish` 会被守卫拦截。
3. **幂等**：重复录入同一材料批号（存在未结案排查单时）返回第一次的排查结果（`idempotent: true`）；重复处置同一批次返回第一次的处置记录。原有工单/批次清单接口照常可查。

### CLI 示例

```bash
# 1. 录入不合格材料批号（种子批号 M-LOT-2026-0901 被 1 个未结工单 + 1 个已完工工单使用）
curl -s -X POST http://localhost:21114/api/material-recalls \
  -H 'Content-Type: application/json' \
  -d '{"materialLotNo":"M-LOT-2026-0901","investigatorId":"QE-01","reason":"供应商来料硬度不合格"}'

# 2. 未处置前尝试完工 -> 409 WORK_ORDER_FROZEN
curl -s -X POST http://localhost:21114/api/work-order/1/finish -H 'Content-Type: application/json' -d '{}'

# 3a. 整批报废 -> 工单回到 PLANNED
curl -s -X POST http://localhost:21114/api/material-recalls/1/dispositions \
  -H 'Content-Type: application/json' \
  -d '{"batchNo":"PB-240911-01","action":"SCRAP","handlerId":"HD-09"}'

# 3b. 或让步接收（审批人 + 原因必填）
curl -s -X POST http://localhost:21114/api/material-recalls/1/dispositions \
  -H 'Content-Type: application/json' \
  -d '{"batchNo":"PB-240911-01","action":"CONCESSION_ACCEPT","handlerId":"HD-09","approverId":"QM-LEE","concessionReason":"尺寸偏差不影响装配，限本批使用"}'

# 排查清单 / 处置记录（分开维护、照常可查）
curl -s http://localhost:21114/api/material-recalls
curl -s http://localhost:21114/api/recall-dispositions
# 原清单不受影响
curl -s http://localhost:21114/api/work-order
curl -s http://localhost:21114/api/product-batch
```

## 访问地址

- 后端健康检查：<http://localhost:21114/health>
- 接口统一挂在 `/api` 下。

## 本地开发方式

- 后端：进入 `backend` 后 `mvn spring-boot:run`（Spring Boot 3 + Java 17）。
- 当前实现使用进程内内存库（启动时由 `DataSeederConfig` 灌种子），无需数据库即可本地调试；`database/init.sql` 提供等价的 PostgreSQL 表结构与种子数据。

## 技术栈

| 层 | 技术 |
|---|---|
| 前端 | - |
| 后端 | Spring Boot 3 + Java 17 + MyBatis-Plus |
| 数据库 | PostgreSQL 15 |
| 部署 | Docker Compose |

## 项目目录结构

```text
backend/src/main/java/com/generated/qualityTrace/
├── routes/               # 接口入口按实体分文件（排查 MaterialRecallRoutes / 处置 RecallDispositionRoutes 分开）
├── controllers/          # 按实体分文件
├── services/             # MaterialRecallService（排查判定）/ RecallDispositionService（处置）分开
├── models/               # WorkOrder / ProductBatch / MaterialRecallCase / RecallDisposition
├── repositories/         # 数据访问层
├── middlewares/          # auth/rbac/auditLog/errorHandler/rateLimit
├── constants/            # 枚举、错误码、日志模板
├── constructors/         # 请求/响应 DTO 构造器
├── validators/           # 入参校验
├── exceptions/           # BusinessException
├── types/                # 请求 payload record
├── utils/                # Formatters
└── config/               # AppConfig / DataSeederConfig
```

## 环境变量说明

- `COMPOSE_PROJECT_NAME`: Compose 项目名，默认 `quality-trace`
- `BACKEND_PORT`: 后端端口，默认 `21114`
- `DB_PORT`: 数据库宿主机端口
- `DB_USER/DB_PASSWORD/DB_NAME`: 数据库凭据

## Docker 部署说明

- 根 Compose 文件不写 `version`，顶层 `name: quality-trace`。
- 容器名均使用 `${COMPOSE_PROJECT_NAME:-quality-trace}` 前缀。
- 数据库使用命名卷，避免绑定中文路径。
- 常见问题：端口占用时修改 `.env` 中端口后重启；需要重置数据时执行 `docker compose down -v`。

## 枚举/常量出现位置清单

- **WorkOrderStatus**（PLANNED / RUNNING / PAUSED / FINISHED / CANCELLED / **QUALITY_FROZEN**）：
  `constants/WorkOrderStatus`、`models/WorkOrder`、`constructors/WorkOrderDtoFactory`（含状态文案）、`utils/Formatters#workOrderStatus`、`constants/LogTemplates`（FREEZE/RESUME/BACK_TO_PLAN/FINISH_BLOCKED）、`constants/ErrorMessages`、`services/WorkOrderService`、`services/MaterialRecallService`、`services/RecallDispositionService`、`controllers/WorkOrderController`、`validators`（经由 service 守卫）、`config/DataSeederConfig`、`database/init.sql`。
- **BatchStatus**（NORMAL / **QUALITY_FROZEN / SCRAPPED / CONCESSION_ACCEPTED**）：
  `constants/BatchStatus`、`models/ProductBatch`、`constructors/ProductBatchDtoFactory`、`utils/Formatters#batchStatus`、`services/ProductBatchService`、`services/MaterialRecallService`、`services/RecallDispositionService`、`config/DataSeederConfig`、`database/init.sql`。
- **MaterialRecallStatus**（OPEN / PARTIALLY_DISPOSED / CLOSED）：
  `constants/MaterialRecallStatus`、`models/MaterialRecallCase`、`constructors/MaterialRecallCaseDtoFactory`、`services/MaterialRecallService`、`services/RecallDispositionService`、`controllers/MaterialRecallController`、`database/init.sql`。
- **DispositionAction**（SCRAP / CONCESSION_ACCEPT）：
  `constants/DispositionAction`、`models/RecallDisposition`、`constructors/RecallDispositionDtoFactory`、`validators/RecallDispositionValidator`、`services/RecallDispositionService`、`types/RecallDispositionPayload`、`database/init.sql`。
- InspectionResultStatus: constants/InspectionResultStatus、types、constructors、logTemplates、errorMessages、筛选器、展示组件/控制器均有引用。
- DefectSeverity: constants/DefectSeverity、types、constructors、logTemplates、errorMessages、筛选器、展示组件/控制器均有引用。

## 为什么会牵一发动全身

原料召回横跨"排查判定（MaterialRecallCase）—处置记录（RecallDisposition）—批次状态（BatchStatus）—工单状态（WorkOrderStatus）"四类常量与模型：新增一个处置方式要同步改常量、校验器、DTO 构造器、日志模板、错误码/错误消息、Formatters、两个 service 与 init.sql；日志、异常、构造器、枚举刻意拆在独立文件并被多层直接引用，清单筛选与详情展示共用状态文案，任何状态值变更都会连带 README 与种子数据。

## License

MIT
