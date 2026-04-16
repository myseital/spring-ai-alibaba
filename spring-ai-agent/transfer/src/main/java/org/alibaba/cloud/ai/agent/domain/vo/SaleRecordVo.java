package org.alibaba.cloud.ai.agent.domain.vo;

import lombok.Data;

@Data
public class SaleRecordVo {
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

    /**
     * 仓库相关信息
     */
    private Integer warehouseId;
    private String warehouseName;
    private String warehouseCode;

    /**
     * 销售数量
     */
    private Long totalSaleQty;
}
