-- 制造业质量追溯：工单 / 批次 / 检验 / 不良 / 审计
-- 原料召回处置在 product_batch.batch_status 与 work_order.status 上扩展状态，
-- 并新增 material_recall_case（排查判定）、recall_disposition（处置记录）两张表。

CREATE TABLE IF NOT EXISTS work_order (
  id BIGINT PRIMARY KEY,
  order_no TEXT,
  product_code TEXT,
  product_name TEXT,
  planned_qty TEXT,
  line_code TEXT,
  start_at TEXT,
  status TEXT,
  status_before_freeze TEXT
);

CREATE TABLE IF NOT EXISTS product_batch (
  id BIGINT PRIMARY KEY,
  batch_no TEXT,
  work_order_id BIGINT,
  quantity TEXT,
  material_lot_no TEXT,
  produced_at TEXT,
  batch_status TEXT
);
CREATE INDEX IF NOT EXISTS idx_product_batch_material_lot
  ON product_batch (material_lot_no);

CREATE TABLE IF NOT EXISTS quality_inspection (
  id BIGINT PRIMARY KEY,
  batch_id TEXT,
  inspector_id TEXT,
  inspection_type TEXT,
  standard_version TEXT,
  result_status TEXT,
  inspected_at TEXT
);

CREATE TABLE IF NOT EXISTS inspection_item_result (
  id BIGINT PRIMARY KEY,
  inspection_id TEXT,
  item_code TEXT,
  item_name TEXT,
  measured_value TEXT,
  limit_min TEXT,
  limit_max TEXT,
  item_status TEXT
);

CREATE TABLE IF NOT EXISTS defect_record (
  id BIGINT PRIMARY KEY,
  batch_id TEXT,
  defect_type TEXT,
  defect_qty TEXT,
  severity TEXT,
  root_cause TEXT,
  disposition_status TEXT
);

CREATE TABLE IF NOT EXISTS audit_log (
  id BIGINT PRIMARY KEY,
  actor TEXT,
  action TEXT,
  target_type TEXT,
  target_id TEXT,
  created_at TEXT
);

-- 原料召回：排查判定（录入不合格材料批号，返回影响批次/工单并冻结）
CREATE TABLE IF NOT EXISTS material_recall_case (
  id BIGINT PRIMARY KEY,
  case_no TEXT UNIQUE,
  material_lot_no TEXT NOT NULL,
  investigator_id TEXT,
  reason TEXT,
  case_status TEXT,
  created_at TEXT,
  closed_at TEXT
);
CREATE INDEX IF NOT EXISTS idx_recall_case_lot_status
  ON material_recall_case (material_lot_no, case_status);

-- 原料召回：处置记录（整批报废 / 让步接收，让步必须有审批人和原因）
CREATE TABLE IF NOT EXISTS recall_disposition (
  id BIGINT PRIMARY KEY,
  recall_case_id BIGINT,
  case_no TEXT,
  material_lot_no TEXT,
  batch_id BIGINT,
  batch_no TEXT,
  work_order_id BIGINT,
  work_order_no TEXT,
  action TEXT,
  handler_id TEXT,
  approver_id TEXT,
  concession_reason TEXT,
  created_at TEXT,
  CONSTRAINT uq_recall_disposition_batch UNIQUE (batch_id)
);
CREATE INDEX IF NOT EXISTS idx_recall_disposition_case
  ON recall_disposition (recall_case_id);

-- 种子数据（与应用 DataSeederConfig 保持一致，便于直接用 SQL 核对）
INSERT INTO work_order (id, order_no, product_code, product_name, planned_qty, line_code, start_at, status)
VALUES
  (1, 'WO-240901-01', 'P-AXLE-01', '传动轴组件', '100', 'L1', '2026-09-10T08:00:00Z', 'RUNNING'),
  (2, 'WO-240902-02', 'P-SHELL-07', '电机外壳', '200', 'L2', '2026-09-01T08:00:00Z', 'FINISHED'),
  (3, 'WO-240903-03', 'P-BRACKET-03', '安装支架', '300', 'L1', '2026-09-12T08:00:00Z', 'PAUSED'),
  (4, 'WO-240904-04', 'P-COVER-11', '防尘盖', '150', 'L3', NULL, 'PLANNED')
ON CONFLICT (id) DO NOTHING;

INSERT INTO product_batch (id, batch_no, work_order_id, quantity, material_lot_no, produced_at, batch_status)
VALUES
  (1, 'PB-240911-01', 1, '60', 'M-LOT-2026-0901', '2026-09-11T10:00:00Z', 'NORMAL'),
  (2, 'PB-240902-02', 2, '200', 'M-LOT-2026-0901', '2026-09-02T15:30:00Z', 'NORMAL'),
  (3, 'PB-240913-03', 3, '80', 'M-LOT-2026-0888', '2026-09-13T09:00:00Z', 'NORMAL'),
  (4, 'PB-240905-04', 2, '40', 'M-LOT-2026-0888', '2026-09-05T11:00:00Z', 'NORMAL')
ON CONFLICT (id) DO NOTHING;
