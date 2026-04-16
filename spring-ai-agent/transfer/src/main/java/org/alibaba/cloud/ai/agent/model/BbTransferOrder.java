package org.alibaba.cloud.ai.agent.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 调拨单主表
 */
@Data
@TableName(value = "bb_transfer_order")
public class BbTransferOrder {
    /**
     * 主键ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 调拨单号
     */
    @TableField(value = "order_no")
    private String orderNo;

    /**
     * 调出仓库ID
     */
    @TableField(value = "source_warehouse_id")
    private Long sourceWarehouseId;

    /**
     * 调入仓库ID
     */
    @TableField(value = "target_warehouse_id")
    private Long targetWarehouseId;

    /**
     * 状态（0：待审核，1：已审核，2：调拨中，3：已完成，4：已取消）
     */
    @TableField(value = "`status`")
    private Integer status;

    /**
     * 调拨类型：1智能，0人工
     */
    @TableField(value = "transfer_type")
    private Integer transferType;

    /**
     * 调拨日期
     */
    @TableField(value = "transfer_date")
    private LocalDate transferDate;

    /**
     * 说明
     */
    @TableField(value = "`comment`")
    private String comment;

    /**
     * 创建人
     */
    @TableField(value = "created_by")
    private String createdBy;

    /**
     * 审核人
     */
    @TableField(value = "approved_by")
    private String approvedBy;

    /**
     * 创建时间
     */
    @TableField(value = "created_time")
    private LocalDateTime createdTime;

    /**
     * 更新时间
     */
    @TableField(value = "updated_time")
    private LocalDateTime updatedTime;
}