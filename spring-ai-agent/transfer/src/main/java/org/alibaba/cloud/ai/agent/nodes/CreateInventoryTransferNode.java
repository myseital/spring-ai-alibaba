package org.alibaba.cloud.ai.agent.nodes;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.alibaba.cloud.ai.graph.OverAllState;
import com.alibaba.cloud.ai.graph.action.NodeAction;
import lombok.extern.slf4j.Slf4j;
import org.alibaba.cloud.ai.agent.domain.param.TransferOrderParam;
import org.alibaba.cloud.ai.agent.service.TransferOrderService;

import java.util.Map;

/**
 *
 * @author myseital
 * @date 2026/4/15
 */
@Slf4j
public class CreateInventoryTransferNode implements NodeAction {

    private final TransferOrderService transferOrderService;

    public CreateInventoryTransferNode(TransferOrderService transferOrderService) {
        this.transferOrderService = transferOrderService;
    }

    @Override
    public Map<String, Object> apply(OverAllState state) throws Exception {
        String inventoryTransferJsonStr = state.value("inventoryTransferJsonStr", "");
        if (StrUtil.isBlank(inventoryTransferJsonStr)) {
            return Map.of();
        }
        TransferOrderParam orderParam = JSONUtil.toBean(inventoryTransferJsonStr, TransferOrderParam.class);
        transferOrderService.create(orderParam);
        return Map.of();
    }
}
