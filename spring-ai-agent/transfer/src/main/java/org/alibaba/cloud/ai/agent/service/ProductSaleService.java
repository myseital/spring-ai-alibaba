package org.alibaba.cloud.ai.agent.service;

import org.alibaba.cloud.ai.agent.domain.param.ProductSaleParam;

/**
 *
 * @author myseital
 * @date 2026/4/16
 */
public interface ProductSaleService {
    void sale(ProductSaleParam productSaleParam);

    void approval(Boolean approval, String threadId);
}
