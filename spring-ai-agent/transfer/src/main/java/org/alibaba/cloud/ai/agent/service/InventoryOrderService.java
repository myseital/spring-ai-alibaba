package org.alibaba.cloud.ai.agent.service;

import org.alibaba.cloud.ai.agent.domain.vo.InventoryOrderVo;

import java.util.List;

/**
 *
 * @author myseital
 * @date 2026/4/14
 */
public interface InventoryOrderService {

    List<InventoryOrderVo> collectInventoryOrderDataByProductId(Integer productId);
}
