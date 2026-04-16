package org.alibaba.cloud.ai.agent.service.impl;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.alibaba.cloud.ai.agent.dao.InventoryOrderDao;
import org.alibaba.cloud.ai.agent.domain.vo.InventoryOrderVo;
import org.alibaba.cloud.ai.agent.service.InventoryOrderService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 *
 * @author myseital
 * @date 2026/4/14
 */
@Slf4j
@Service
public class InventoryOrderServiceImpl implements InventoryOrderService {

    @Resource
    private InventoryOrderDao inventoryOrderDao;

    @Override
    public List<InventoryOrderVo> collectInventoryOrderDataByProductId(Integer productId) {
        return inventoryOrderDao.collectInventoryOrderDataByProductId(productId);
    }
}
