package org.alibaba.cloud.ai.agent.nodes;

import cn.hutool.core.date.DateUnit;
import cn.hutool.core.date.DateUtil;
import com.alibaba.cloud.ai.graph.OverAllState;
import com.alibaba.cloud.ai.graph.action.NodeAction;
import com.alibaba.cloud.ai.graph.state.strategy.ReplaceStrategy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import reactor.core.publisher.Flux;

import java.util.Map;

/**
 *
 * @author myseital
 * @date 2026/4/14
 */
@Slf4j
public class PredictNode implements NodeAction {

    private final ChatClient chatClient;

    private static final String systemPrompt = """
            # 角色:
            你是一名专业的供应链分析师和库存管理专家,擅长基于销售数据和季节趋势进行精准的库存预测和调拨规划。
            
            # 任务:
            基于提供的商品历史销售数据、商品历史库存调拨数据、当前库存状况和日期信息,严格基于用户提供的销售数据和库存数据进行分析销售模式,预测未来需求,并生成一份科学合理的库存调拨单。

            # 分析要求:
            ##第一步:销售模式分析
            1.识别销售的季节性规律和趋势
            2.分析促销活动对销售的影响程度
            3.计算季度环比增长率和同比增长率
            ## 第二步:需求预测
            1.基于历史数据和当前季度位置,预测未来周期的需求量
            2.考虑季节性因素和增长趋势
            3.评估预测的不确定性范围
            ## 第三步:库存优化计算
            1.计算各仓库的安全库存水平,减少库存积压与缺货风险
            2.识别库存过剩或不足的仓库
            3.确定最优的库存分配方案
            # 输出要求:
            生成一份格式化的"库存调拨单";
            输出结果以JSON格式输出,便于系统解析;
            不可凭空虚构数据,若数据不足,请说明假设。
            请始终输出清晰、可直接用于业务系统的数据结构,JSON格式数据中禁止出现运算。
            只返回一个仓库的调拨意见即可。
            将详细的解释说明放在comment字段中。
            输出示例结构:
            {
                "sourceWarehouseId": 1,
                "targetWarehouseId": 2,
                "status":0,
                "createdBy": "AI智能助手",
                "transferType": 1,
                "transferDate": "2025-11-12",
                "comment": "解释说明"
                "items": [
                    {
                    "productId": 11,
                    "transferQuantity": 直接给出具体数值,
                    "actualQuantity": 0,
                    "remark": "备注"
                    },
                    ...
                ]
            }
            """;

    public PredictNode(ChatClient chatClient) {
        this.chatClient = chatClient;
    }

    @Override
    public Map<String, Object> apply(OverAllState state) throws Exception {
        String productId = state.value("productId", "");
        String saleRecordData = state.value("saleRecordData", "");
        String nowProductInventoryData = state.value("nowProductInventoryData", "");
        String inventoryOrderData = state.value("inventoryOrderData", "");
        String saleDate = DateUtil.today();
        Flux<String> content = chatClient.prompt()
                .system(systemPrompt)
                .user(t -> t.text("""
                        输入数据：
                        商品信息：{productId}
                        商品历史销售数据：{saleRecordData}
                        商品历史库存调拨数据: {inventoryOrderData}
                        当前库存状况: {nowProductInventoryData}
                        销售日期：{saleDate}
                        
                        帮我生成一份调拨意见
                        """).params(
                        Map.of("productId", productId,
                                "saleRecordData", saleRecordData,
                                "inventoryOrderData", inventoryOrderData,
                                "nowProductInventoryData", nowProductInventoryData,
                                "saleDate", saleDate)))
                .stream().content();
        StringBuilder sb = new StringBuilder();
        content.doOnNext(sb::append).blockLast();
        log.info("PredictNode result=[{}]", sb);

        return Map.of("inventoryTransferStr", sb.toString());
    }
}
