package org.alibaba.cloud.ai.agent.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 商品销售记录表
 */
@Data
@TableName(value = "bb_sales_record")
public class BbSalesRecord {
    /**
     * 主键ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 仓库ID
     */
    @TableField(value = "warehouse_id")
    private Long warehouseId;

    /**
     * 商品ID
     */
    @TableField(value = "product_id")
    private Long productId;

    /**
     * 销售日期
     */
    @TableField(value = "sale_date")
    private LocalDate saleDate;

    /**
     * 销售数量
     */
    @TableField(value = "quantity")
    private BigDecimal quantity;

    /**
     * 销售金额
     */
    @TableField(value = "revenue")
    private BigDecimal revenue;

    /**
     * 创建时间
     */
    @TableField(value = "created_time")
    private LocalDateTime createdTime;
}