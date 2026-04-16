package org.alibaba.cloud.ai.agent.nodes;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.alibaba.cloud.ai.graph.OverAllState;
import com.alibaba.cloud.ai.graph.action.NodeAction;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.extern.slf4j.Slf4j;
import org.alibaba.cloud.ai.agent.domain.vo.SaleRecordVo;
import org.alibaba.cloud.ai.agent.model.BbInventory;
import org.alibaba.cloud.ai.agent.service.SaleRecordService;
import org.alibaba.cloud.ai.agent.service.base.BbInventoryService;

import java.util.List;
import java.util.Map;


@Slf4j
public class CollectSaleRecordNode implements NodeAction {

    private final SaleRecordService saleRecordService;

    private final BbInventoryService bbInventoryService;

    public CollectSaleRecordNode(SaleRecordService saleRecordService, BbInventoryService bbInventoryService) {
        this.saleRecordService = saleRecordService;
        this.bbInventoryService = bbInventoryService;
    }

    /**
     * 1. 从状态机获取到对应的商品 id
     * 2. 写 sql 去查询对应的历史销售数据
     * 3. 写 sql 查询商品剩余库存
     * <p>
     * 维度：年份、季度、仓库
     *
     * @param state OverAllState
     * @return Map<String, Object>
     * @throws Exception
     */
    @Override
    public Map<String, Object> apply(OverAllState state) throws Exception {
        // 拿到商品 id
        String productId = state.value("productId", "");
        if (StrUtil.isBlank(productId)) {
            return Map.of();
        }

        // 查询商品的历史销售数据情况
        List<SaleRecordVo> saleRecordVos = saleRecordService.collectSaleRecordDataByProductId(Integer.parseInt(productId));
        String saleRecordData = JSONUtil.toJsonStr(saleRecordVos);
        log.info("saleRecordData=[{}]", saleRecordData);

        // 查询商品现在的库存数据
        List<BbInventory> inventoryList = bbInventoryService.list(Wrappers.lambdaQuery(BbInventory.class)
                .eq(BbInventory::getProductId, productId));
        // 专场 json 让大模型更好的理解
        String nowProductInventoryData = JSONUtil.toJsonStr(inventoryList);
        log.info("nowProductInventoryData=[{}]", nowProductInventoryData);

        return Map.of("saleRecordData", saleRecordData,
                "nowProductInventoryData", nowProductInventoryData);
    }
}
