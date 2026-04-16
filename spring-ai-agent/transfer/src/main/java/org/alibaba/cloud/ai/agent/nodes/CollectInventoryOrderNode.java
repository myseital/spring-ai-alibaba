package org.alibaba.cloud.ai.agent.nodes;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.alibaba.cloud.ai.graph.OverAllState;
import com.alibaba.cloud.ai.graph.action.NodeAction;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.extern.slf4j.Slf4j;
import org.alibaba.cloud.ai.agent.domain.vo.InventoryOrderVo;
import org.alibaba.cloud.ai.agent.domain.vo.SaleRecordVo;
import org.alibaba.cloud.ai.agent.model.BbInventory;
import org.alibaba.cloud.ai.agent.service.InventoryOrderService;

import java.util.List;
import java.util.Map;

/**
 *
 * @author myseital
 * @date 2026/4/14
 */
@Slf4j
public class CollectInventoryOrderNode implements NodeAction {

    private final InventoryOrderService inventoryOrderService;

    public CollectInventoryOrderNode(InventoryOrderService inventoryOrderService) {
        this.inventoryOrderService = inventoryOrderService;
    }

    @Override
    public Map<String, Object> apply(OverAllState state) throws Exception {
        String productId = state.value("productId", "");
        if (StrUtil.isBlank(productId)) {
            return Map.of();
        }

        List<InventoryOrderVo> inventoryOrderVos = inventoryOrderService.collectInventoryOrderDataByProductId(Integer.parseInt(productId));
        String inventoryOrderData = JSONUtil.toJsonStr(inventoryOrderVos);
        log.info("inventoryOrderData=[{}]", inventoryOrderData);

        return Map.of("inventoryOrderData", inventoryOrderData);
    }
}
