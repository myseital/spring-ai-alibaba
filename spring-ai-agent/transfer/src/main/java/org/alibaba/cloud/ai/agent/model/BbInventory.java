package org.alibaba.cloud.ai.agent.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 库存表
 */
@Data
@TableName(value = "bb_inventory")
public class BbInventory {
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
     * 当前库存数量
     */
    @TableField(value = "quantity")
    private BigDecimal quantity;

    /**
     * 锁定库存数量（例如调拨中未完成的部分）
     */
    @TableField(value = "locked_quantity")
    private BigDecimal lockedQuantity;

    /**
     * 更新时间
     */
    @TableField(value = "updated_time")
    private LocalDateTime updatedTime;
}