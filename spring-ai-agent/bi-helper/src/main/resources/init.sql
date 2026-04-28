-- 零售数仓全套表结构 + 初始化数据
-- 商品维度、门店维度、客户维度、时间维度、销售事实、库存事实
-- 执行顺序：直接全选运行即可

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- 1. 商品维度表 dim_product
-- ----------------------------
DROP TABLE IF EXISTS dim_product;
CREATE TABLE dim_product (
                             product_id bigint NOT NULL COMMENT '商品唯一ID',
                             product_name varchar(255) DEFAULT NULL COMMENT '商品名称',
                             category_id bigint DEFAULT NULL COMMENT '商品分类ID',
                             category_name varchar(255) DEFAULT NULL COMMENT '商品分类名称',
                             brand varchar(255) DEFAULT NULL COMMENT '品牌',
                             cost_price decimal(10,2) DEFAULT NULL COMMENT '成本价',
                             retail_price decimal(10,2) DEFAULT NULL COMMENT '建议零售价',
                             PRIMARY KEY (product_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='商品维度表';

-- ----------------------------
-- 2. 门店维度表 dim_store
-- ----------------------------
DROP TABLE IF EXISTS dim_store;
CREATE TABLE dim_store (
                           store_id bigint NOT NULL COMMENT '门店唯一ID',
                           store_name varchar(255) DEFAULT NULL COMMENT '门店名称',
                           province varchar(100) DEFAULT NULL COMMENT '所在省份',
                           city varchar(100) DEFAULT NULL COMMENT '所在城市',
                           address varchar(255) DEFAULT NULL COMMENT '门店详细地址',
                           open_date date DEFAULT NULL COMMENT '开业日期',
                           PRIMARY KEY (store_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='门店维度表';

-- ----------------------------
-- 3. 客户维度表 dim_customer
-- ----------------------------
DROP TABLE IF EXISTS dim_customer;
CREATE TABLE dim_customer (
                              customer_id bigint NOT NULL COMMENT '客户唯一ID',
                              customer_name varchar(255) DEFAULT NULL COMMENT '客户姓名',
                              gender varchar(10) DEFAULT NULL COMMENT '客户性别',
                              age int DEFAULT NULL COMMENT '客户年龄',
                              city varchar(100) DEFAULT NULL COMMENT '所在城市',
                              province varchar(100) DEFAULT NULL COMMENT '所在省份',
                              PRIMARY KEY (customer_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='客户维度表';

-- ----------------------------
-- 4. 时间维度表 dim_date
-- ----------------------------
DROP TABLE IF EXISTS dim_date;
CREATE TABLE dim_date (
                          date_id int NOT NULL COMMENT '日期(yyyyMMdd)',
                          date date DEFAULT NULL COMMENT '实际日期',
                          year int DEFAULT NULL COMMENT '年份',
                          quarter int DEFAULT NULL COMMENT '季度(1-4)',
                          month int DEFAULT NULL COMMENT '月份(1-12)',
                          day int DEFAULT NULL COMMENT '日(1-31)',
                          week int DEFAULT NULL COMMENT '周数',
                          weekday int DEFAULT NULL COMMENT '星期(1-7)',
                          PRIMARY KEY (date_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='时间维度表';

-- ----------------------------
-- 5. 销售事实表 fact_sales
-- ----------------------------
DROP TABLE IF EXISTS fact_sales;
CREATE TABLE fact_sales (
                            sales_id bigint NOT NULL COMMENT '销售记录唯一ID',
                            date_id int DEFAULT NULL COMMENT '销售日期',
                            store_id bigint DEFAULT NULL COMMENT '销售门店',
                            product_id bigint DEFAULT NULL COMMENT '销售商品',
                            customer_id bigint DEFAULT NULL COMMENT '客户ID',
                            quantity int DEFAULT NULL COMMENT '销售数量',
                            sales_amount decimal(10,2) DEFAULT NULL COMMENT '销售金额（实收）',
                            original_amount decimal(10,2) DEFAULT NULL COMMENT '应收金额（未折扣）',
                            discount_amount decimal(10,2) DEFAULT NULL COMMENT '折扣金额',
                            PRIMARY KEY (sales_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='销售事实表';

-- ----------------------------
-- 6. 库存事实表 fact_inventory
-- ----------------------------
DROP TABLE IF EXISTS fact_inventory;
CREATE TABLE fact_inventory (
                                inventory_id bigint NOT NULL COMMENT '库存记录ID',
                                date_id int DEFAULT NULL COMMENT '日期',
                                store_id bigint DEFAULT NULL COMMENT '门店',
                                product_id bigint DEFAULT NULL COMMENT '商品',
                                stock_qty int DEFAULT NULL COMMENT '库存数量',
                                stock_value decimal(10,2) DEFAULT NULL COMMENT '库存金额',
                                last_in_time datetime DEFAULT NULL COMMENT '最近一次入库时间',
                                last_out_time datetime DEFAULT NULL COMMENT '最近一次出库时间',
                                PRIMARY KEY (inventory_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='库存事实表';

-- ----------------------------
-- 初始化数据开始
-- ----------------------------

-- ============================
-- dim_product 100条
-- ============================
INSERT INTO dim_product
SELECT seq,
       CONCAT('商品_', seq),
       FLOOR(RAND() * 5) + 1,
       ELT(FLOOR(RAND()*5)+1,'饮料','零食','日用品','生鲜','粮油'),
       CONCAT('品牌_', FLOOR(RAND()*20)+1),
       ROUND(RAND()*20 + 1,2),
       ROUND(RAND()*30 + 3,2)
FROM (
         SELECT @row:=@row+1 seq FROM information_schema.tables t1,
             information_schema.tables t2,
             (SELECT @row:=0) t0 LIMIT 100
     ) tmp;

-- ============================
-- dim_store 20条
-- ============================
INSERT INTO dim_store
SELECT seq,
       CONCAT('门店_', seq),
       ELT(FLOOR(RAND()*10)+1,'北京','上海','广东','江苏','浙江','四川','湖北','湖南','河南','山东'),
       ELT(FLOOR(RAND()*15)+1,'北京市','上海市','广州市','深圳市','杭州市','南京市','成都市','武汉市','长沙市','郑州市','济南市','苏州市','宁波市','重庆市','天津市'),
       CONCAT('地址_', seq),
       '2023-01-01'
FROM (
         SELECT @row:=@row+1 seq FROM information_schema.tables t1,
             information_schema.tables t2,
             (SELECT @row:=0) t0 LIMIT 20
     ) tmp;

-- ============================
-- dim_customer 200条
-- ============================
INSERT INTO dim_customer
SELECT seq,
       CONCAT('客户_', seq),
       ELT(FLOOR(RAND()*2)+1,'男','女'),
       FLOOR(RAND()*50)+18,
       ELT(FLOOR(RAND()*15)+1,'北京市','上海市','广州市','深圳市','杭州市','南京市','成都市','武汉市','长沙市','郑州市','济南市','苏州市','宁波市','重庆市','天津市'),
       ELT(FLOOR(RAND()*10)+1,'北京','上海','广东','江苏','浙江','四川','湖北','湖南','河南','山东')
FROM (
         SELECT @row:=@row+1 seq FROM information_schema.tables t1,
             information_schema.tables t2,
             (SELECT @row:=0) t0 LIMIT 200
     ) tmp;

-- ============================
-- dim_date 2025-01-01 至 2025-12-31
-- ============================
INSERT INTO dim_date
SELECT
    YEAR(d)*10000 + MONTH(d)*100 + DAY(d) AS date_id,
    d AS date,
    YEAR(d) AS year,
    QUARTER(d) AS quarter,
    MONTH(d) AS month,
    DAY(d) AS day,
    WEEK(d) AS week,
    DAYOFWEEK(d) AS weekday
FROM (
    SELECT '2025-01-01' + INTERVAL (a.a + (10*b.a) + (100*c.a)) DAY AS d
    FROM (SELECT 0 a UNION SELECT 1 UNION SELECT 2 UNION SELECT 3 UNION SELECT 4 UNION SELECT 5 UNION SELECT 6 UNION SELECT 7 UNION SELECT 8 UNION SELECT 9) a,
    (SELECT 0 a UNION SELECT 1 UNION SELECT 2 UNION SELECT 3 UNION SELECT 4 UNION SELECT 5 UNION SELECT 6 UNION SELECT 7 UNION SELECT 8 UNION SELECT 9) b,
    (SELECT 0 a UNION SELECT 1 UNION SELECT 2 UNION SELECT 3) c
    HAVING d <= '2025-12-31'
    ) tmp;

-- ============================
-- fact_sales 500条
-- ============================
INSERT INTO fact_sales (sales_id, date_id, store_id, product_id, customer_id, quantity, sales_amount, original_amount, discount_amount)
SELECT
    seq,
    (SELECT date_id FROM dim_date ORDER BY RAND() LIMIT 1),
    (SELECT store_id FROM dim_store ORDER BY RAND() LIMIT 1),
    (SELECT product_id FROM dim_product ORDER BY RAND() LIMIT 1),
    (SELECT customer_id FROM dim_customer ORDER BY RAND() LIMIT 1),
    FLOOR(RAND()*20)+1,
    ROUND(RAND()*500 + 10,2),
    ROUND(RAND()*550 + 15,2),
    ROUND(RAND()*50,2)
FROM (
    SELECT @row:=@row+1 seq FROM information_schema.tables t1,
    information_schema.tables t2,
    (SELECT @row:=0) t0 LIMIT 500
    ) tmp;

-- ============================
-- fact_inventory 800条
-- ============================
INSERT INTO fact_inventory (inventory_id, date_id, store_id, product_id, stock_qty, stock_value, last_in_time, last_out_time)
SELECT
    seq,
    (SELECT date_id FROM dim_date ORDER BY RAND() LIMIT 1),
    (SELECT store_id FROM dim_store ORDER BY RAND() LIMIT 1),
    (SELECT product_id FROM dim_product ORDER BY RAND() LIMIT 1),
    FLOOR(RAND()*500)+10,
    ROUND(RAND()*5000 + 50,2),
    '2025-01-01 09:00:00' + INTERVAL FLOOR(RAND()*365) DAY,
    '2025-01-01 18:00:00' + INTERVAL FLOOR(RAND()*365) DAY
FROM (
    SELECT @row:=@row+1 seq FROM information_schema.tables t1,
    information_schema.tables t2,
    (SELECT @row:=0) t0 LIMIT 800
    ) tmp;

SET FOREIGN_KEY_CHECKS = 1;