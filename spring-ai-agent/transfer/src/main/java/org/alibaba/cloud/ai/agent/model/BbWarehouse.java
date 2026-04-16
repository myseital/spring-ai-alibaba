package org.alibaba.cloud.ai.agent.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 仓库信息表
 */
@Data
@TableName(value = "bb_warehouse")
public class BbWarehouse {
    /**
     * 主键ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 仓库编码
     */
    @TableField(value = "warehouse_code")
    private String warehouseCode;

    /**
     * 仓库名称
     */
    @TableField(value = "warehouse_name")
    private String warehouseName;

    /**
     * 仓库位置描述
     */
    @TableField(value = "`location`")
    private String location;

    /**
     * 负责人
     */
    @TableField(value = "manager")
    private String manager;

    /**
     * 状态（1：启用，0：停用）
     */
    @TableField(value = "`status`")
    private Integer status;

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