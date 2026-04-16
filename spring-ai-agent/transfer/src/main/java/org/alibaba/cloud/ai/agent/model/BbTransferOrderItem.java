package org.alibaba.cloud.ai.agent.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 调拨单明细表
 */
@Data
@TableName(value = "bb_transfer_order_item")
public class BbTransferOrderItem {
    /**
     * 主键ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 调拨单ID
     */
    @TableField(value = "transfer_order_id")
    private Long transferOrderId;

    /**
     * 商品ID
     */
    @TableField(value = "product_id")
    private Long productId;

    /**
     * 调拨数量
     */
    @TableField(value = "transfer_quantity")
    private BigDecimal transferQuantity;

    /**
     * 实际到货数量
     */
    @TableField(value = "actual_quantity")
    private BigDecimal actualQuantity;

    /**
     * 备注
     */
    @TableField(value = "remark")
    private String remark;
}