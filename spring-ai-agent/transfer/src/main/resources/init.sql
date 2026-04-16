-- 仓库表：存放仓库基本信息
CREATE TABLE bb_warehouse (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    warehouse_code VARCHAR(50) NOT NULL UNIQUE COMMENT '仓库编码',
    warehouse_name VARCHAR(100) NOT NULL COMMENT '仓库名称',
    location VARCHAR(255) DEFAULT NULL COMMENT '仓库位置描述',
    manager VARCHAR(50) DEFAULT NULL COMMENT '负责人',
    status TINYINT DEFAULT 1 COMMENT '状态（1：启用，0：停用）',
    created_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='仓库信息表';

-- 商品表：记录商品的基本信息
CREATE TABLE bb_product (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    product_code VARCHAR(50) NOT NULL UNIQUE COMMENT '商品编码',
    product_name VARCHAR(100) NOT NULL COMMENT '商品名称',
    spec VARCHAR(100) DEFAULT NULL COMMENT '规格型号',
    unit VARCHAR(20) DEFAULT '件' COMMENT '计量单位',
    category VARCHAR(50) DEFAULT NULL COMMENT '分类',
    status TINYINT DEFAULT 1 COMMENT '状态（1：启用，0：停用）',
    created_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='商品信息表';

-- 销售表
CREATE TABLE bb_sales_record (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    warehouse_id BIGINT NOT NULL COMMENT '仓库ID',
    product_id BIGINT NOT NULL COMMENT '商品ID',
    sale_date DATE NOT NULL COMMENT '销售日期',
    quantity DECIMAL(18,2) NOT NULL COMMENT '销售数量',
    revenue DECIMAL(18,2) DEFAULT 0 COMMENT '销售金额',
    created_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='商品销售记录表';

-- 调拨单主表
CREATE TABLE bb_transfer_order (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    order_no VARCHAR(50) NOT NULL UNIQUE COMMENT '调拨单号',
    source_warehouse_id BIGINT NOT NULL COMMENT '调出仓库ID',
    target_warehouse_id BIGINT NOT NULL COMMENT '调入仓库ID',
    status TINYINT DEFAULT 0 COMMENT '状态（0：待审核，1：已审核，2：调拨中，3：已完成，4：已取消）',
    transfer_type INT DEFAULT 0 COMMENT '调拨类型：1智能，0人工',
    transfer_date DATE DEFAULT NULL COMMENT '调拨日期',
    comment TEXT COMMENT '说明',
    created_by VARCHAR(50) DEFAULT NULL COMMENT '创建人',
    approved_by VARCHAR(50) DEFAULT NULL COMMENT '审核人',
    created_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='调拨单主表';

-- 调拨单明细表
CREATE TABLE bb_transfer_order_item (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    transfer_order_id BIGINT NOT NULL COMMENT '调拨单ID',
    product_id BIGINT NOT NULL COMMENT '商品ID',
    transfer_quantity DECIMAL(18,2) NOT NULL COMMENT '调拨数量',
    actual_quantity DECIMAL(18,2) DEFAULT 0 COMMENT '实际到货数量',
    remark VARCHAR(255) DEFAULT NULL COMMENT '备注'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='调拨单明细表';

-- 库存操作日志表（用于追踪库存变动）
CREATE TABLE bb_inventory_log (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    warehouse_id BIGINT NOT NULL COMMENT '仓库ID',
    product_id BIGINT NOT NULL COMMENT '商品ID',
    change_type VARCHAR(50) NOT NULL COMMENT '变动类型（IN：入库，OUT：出库，TRANSFER_OUT：调拨出库，TRANSFER_IN：调拨入库）',
    quantity_change DECIMAL(18,2) NOT NULL COMMENT '变动数量（正为增加，负为减少）',
    reference_no VARCHAR(50) DEFAULT NULL COMMENT '关联单据号（如调拨单号）',
    created_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '操作时间',
    remark VARCHAR(255) DEFAULT NULL COMMENT '备注'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='库存操作日志表';


-- ----------------------------
-- 1. 插入仓库表测试数据
-- ----------------------------
INSERT INTO bb_warehouse (warehouse_code, warehouse_name, location, manager)
VALUES
('WH001', '华北仓', '北京', '张三'),
('WH002', '华东仓', '上海', '李四'),
('WH003', '华南仓', '广州', '王五');

-- ----------------------------
-- 2. 插入商品表测试数据
-- ----------------------------
INSERT INTO bb_product (product_code, product_name, spec, unit, category)
VALUES
('P001', '智能手机', '6.5英寸 128G', '台', '电子产品'),
('P002', '蓝牙耳机', '入耳式', '副', '电子产品'),
('P003', '办公椅', '可调节靠背', '张', '家具'),
('P004', '显示器', '27寸 IPS', '台', '电子产品'),
('P005', '机械键盘', '104键 RGB', '个', '外设');

-- ----------------------------
-- 3. 初始化库存（每仓库每商品都有库存）
-- 逻辑：随机生成 100-300 之间的库存数量
-- ----------------------------
INSERT INTO bb_inventory (warehouse_id, product_id, quantity, locked_quantity)
SELECT 
    w.id AS warehouse_id, 
    p.id AS product_id, 
    FLOOR(RAND() * 200 + 100) AS quantity,  -- 随机生成 100~300 的库存数
    0 AS locked_quantity                     -- 初始锁定库存为0
FROM bb_warehouse w, bb_product p;

-- 创建过去三年的季度销售数据（2023-2025）
-- 生成销售数据（每季度每仓库每商品一条）
SET @start_date = '2023-01-01';
SET @end_date = '2025-12-31';

INSERT INTO bb_sales_record (warehouse_id, product_id, sale_date, quantity, revenue)
SELECT *
FROM (
         SELECT
             w.id AS warehouse_id,
             p.id AS product_id,
             -- 生成2023-01-01起，0~35个月内的随机日期（覆盖2023-2025全年）
             DATE_ADD('2023-01-01', INTERVAL (FLOOR(RAND() * 12 * 3)) MONTH) AS sale_date,
             -- 销售数量：50.00 ~ 550.00，保留2位小数
             ROUND(RAND() * 500 + 50, 2) AS quantity,
             -- 销售金额：5000.00 ~ 55000.00，保留2位小数
             ROUND(RAND() * 50000 + 5000, 2) AS revenue
         FROM bb_warehouse w, bb_product p,
              -- 生成3行数据，实现「每仓库每商品3条记录（对应3年，每年1条/季度维度）」
              (SELECT 1 FROM dual UNION SELECT 2 UNION SELECT 3) t
     ) t
WHERE t.sale_date BETWEEN @start_date AND @end_date;

-- 创建调拨单主表（模拟每季度一次调拨）
-- 插入随机调拨单数据（排除相同仓库）
INSERT INTO bb_transfer_order (
    order_no,
    source_warehouse_id,
    target_warehouse_id,
    status,
    created_by,
    transfer_type,
    transfer_date
)
SELECT
    t.order_no,
    t.source_warehouse_id,
    t.target_warehouse_id,
    t.status,
    t.created_by,
    t.transfer_type,
    t.transfer_date
FROM (
         SELECT
             CONCAT('TO', LPAD(FLOOR(RAND()*99999), 5, '0')) AS order_no,
             -- 随机生成1-3的仓库ID（对应3个仓库）
             FLOOR(1 + RAND()*3) AS source_warehouse_id,
             FLOOR(1 + RAND()*3) AS target_warehouse_id,
             3 AS status,  -- 已完成
             '系统自动' AS created_by,
             1 AS transfer_type, -- 假设 1=系统自动
             -- 生成2023-01-01到2025-12-31之间的随机日期
             DATE_ADD('2023-01-01', INTERVAL FLOOR(RAND()*36) MONTH) AS transfer_date
         FROM (
                  SELECT 1 FROM dual
                  UNION SELECT 2
                  UNION SELECT 3
                  UNION SELECT 4
                  UNION SELECT 5
              ) tmp
     ) t
WHERE t.source_warehouse_id <> t.target_warehouse_id;

-- 创建调拨明细数据
INSERT INTO bb_transfer_order_item (
    transfer_order_id,
    product_id,
    transfer_quantity,
    actual_quantity,
    remark
)
SELECT
    o.id,
    p.id,
    ROUND(RAND()*50 + 10, 2),
    ROUND(RAND()*50 + 10, 2),
    '季度调拨'
FROM bb_transfer_order o, bb_product p
WHERE o.status = 3
ORDER BY o.id LIMIT 50;

-- 为库存操作日志生成随机调拨记录（出库）
INSERT INTO bb_inventory_log (
    warehouse_id,
    product_id,
    change_type,
    quantity_change,
    reference_no,
    remark
)
SELECT
    t.source_warehouse_id,
    i.product_id,
    'TRANSFER_OUT',
    -item.transfer_quantity,
    o.order_no,
    '系统模拟调拨出库'
FROM bb_transfer_order o
         JOIN bb_transfer_order_item item ON o.id = item.transfer_order_id
         JOIN bb_inventory i ON item.product_id = i.product_id
         JOIN bb_transfer_order t ON o.id = t.id;

-- 调拨入库日志
INSERT INTO bb_inventory_log (
    warehouse_id,
    product_id,
    change_type,
    quantity_change,
    reference_no,
    remark
)
SELECT
    t.target_warehouse_id,
    i.product_id,
    'TRANSFER_IN',
    item.transfer_quantity,
    o.order_no,
    '系统模拟调拨入库'
FROM bb_transfer_order o
         JOIN bb_transfer_order_item item ON o.id = item.transfer_order_id
         JOIN bb_inventory i ON item.product_id = i.product_id
         JOIN bb_transfer_order t ON o.id = t.id;