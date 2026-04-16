package org.alibaba.cloud.ai.agent.domain.param;

import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;

import java.math.BigDecimal;

/**
 *
 * @author myseital
 * @date 2026/4/15
 */
@Data
public class TransferOrderItemParam {
    /**
     * 调拨单ID
     */
    private Long transferOrderId;
    /**
     * 商品ID
     */
    private Long productId;
    /**
     * 调拨数量
     */
    private BigDecimal transferQuantity;
    /**
     * 备注
     */
    private String remark;
}
