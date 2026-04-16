package org.alibaba.cloud.ai.agent.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 商品信息表
 */
@Data
@TableName(value = "bb_product")
public class BbProduct {
    /**
     * 主键ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 商品编码
     */
    @TableField(value = "product_code")
    private String productCode;

    /**
     * 商品名称
     */
    @TableField(value = "product_name")
    private String productName;

    /**
     * 规格型号
     */
    @TableField(value = "spec")
    private String spec;

    /**
     * 计量单位
     */
    @TableField(value = "unit")
    private String unit;

    /**
     * 分类
     */
    @TableField(value = "category")
    private String category;

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