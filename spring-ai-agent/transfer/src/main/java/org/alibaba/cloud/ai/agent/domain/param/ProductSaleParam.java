package org.alibaba.cloud.ai.agent.domain.param;

import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

/**
 *
 * @author myseital
 * @date 2026/4/16
 */
@Data
public class ProductSaleParam {
    /**
     * 仓库ID
     */
    private Long warehouseId;
    /**
     * 商品ID
     */
    private Long productId;
    /**
     * 销售日期
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date saleDate;
    /**
     * 销售数量
     */
    private Integer quantity;
}
