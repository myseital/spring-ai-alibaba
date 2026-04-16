package org.alibaba.cloud.ai.agent.domain.vo;

import lombok.Data;

/**
 *
 * @author myseital
 * @date 2026/4/14
 */
@Data
public class InventoryOrderVo {
    private Integer productId;
    private String productCode;
    private String productName;

    /**
     * 哪一年的销售情况
     */
    private String year;

    /**
     * 哪一个季度的销售的情况
     */
    private Integer quarter;
    private Long totalTransferQty;

    /**
     * 仓库相关信息
     */
    private Integer sourceWarehouseId;
    private String sourceWarehouseCode;
    private String sourceWarehouseName;
    private Integer targetWarehouseId;
    private String targetWarehouseCode;
    private String targetWarehouseName;

}
