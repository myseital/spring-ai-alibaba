package org.alibaba.cloud.ai.agent.service.impl;

import cn.hutool.json.JSONUtil;
import com.alibaba.cloud.ai.graph.CompiledGraph;
import com.alibaba.cloud.ai.graph.OverAllState;
import com.alibaba.cloud.ai.graph.RunnableConfig;
import com.alibaba.cloud.ai.graph.state.StateSnapshot;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.alibaba.cloud.ai.agent.constants.TopicConstant;
import org.alibaba.cloud.ai.agent.domain.convert.SalesRecordConvert;
import org.alibaba.cloud.ai.agent.domain.param.ProductSaleParam;
import org.alibaba.cloud.ai.agent.model.BbInventory;
import org.alibaba.cloud.ai.agent.model.BbSalesRecord;
import org.alibaba.cloud.ai.agent.service.ProductSaleService;
import org.alibaba.cloud.ai.agent.service.base.BbInventoryService;
import org.alibaba.cloud.ai.agent.service.base.BbSalesRecordService;
import org.alibaba.cloud.ai.common.domain.R;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author myseital
 * @date 2026/4/16
 */
@Slf4j
@Service
@RefreshScope
public class ProductSaleServiceImpl implements ProductSaleService {

    @Resource
    private BbSalesRecordService bbSalesRecordService;

    @Resource
    private BbInventoryService bbInventoryService;

    @Value("${threshold.productCount}")
    private Integer thresholdProductCount;

    @Resource
    private KafkaTemplate kafkaTemplate;

    @Resource
    private CompiledGraph graph;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void sale(ProductSaleParam productSaleParam) {
        BbSalesRecord salesRecord = SalesRecordConvert.INSTANCE.paramToModel(productSaleParam);
        bbSalesRecordService.save(salesRecord);

        bbInventoryService.update(Wrappers.lambdaUpdate(BbInventory.class)
                .eq(BbInventory::getProductId, productSaleParam.getProductId())
                .eq(BbInventory::getWarehouseId, productSaleParam.getWarehouseId())
                .setSql("quantity = quantity - " + productSaleParam.getQuantity())
        );


        BbInventory inventory = bbInventoryService.getOne(Wrappers.lambdaQuery(BbInventory.class)
                .eq(BbInventory::getProductId, productSaleParam.getProductId())
                .eq(BbInventory::getWarehouseId, productSaleParam.getWarehouseId()));
        if (inventory.getQuantity().intValue() > thresholdProductCount) {
            return;
        }

//        kafkaTemplate.send(TopicConstant.PRODUCT_SALE_TOPIC, productSaleParam.getProductId().toString());
        kafkaTemplate.send(TopicConstant.PRODUCT_SALE_TOPIC, JSONUtil.toJsonStr(productSaleParam));
    }

    @Override
    public void approval(Boolean approval, String threadId) {
        try {
            RunnableConfig runnableConfig = RunnableConfig.builder()
                    .threadId(threadId)
                    .build();
            StateSnapshot stateSnapshot = graph.getState(runnableConfig);
            OverAllState state = stateSnapshot.state();
            log.info("StateSnapshot state = [{}]", state);
            Map<String, Object> feedback = new HashMap<>();
            feedback.put("approval", approval);
            Map<String, Object> stateUpdate = new HashMap<>();
            stateUpdate.put("approval", approval);
            RunnableConfig resumeConfig = RunnableConfig.builder()
                    .threadId(threadId)
                    .addMetadata(RunnableConfig.HUMAN_FEEDBACK_METADATA_KEY, feedback)
                    .addMetadata(RunnableConfig.STATE_UPDATE_METADATA_KEY, stateUpdate)
                    .build();
            // 从中断点继续执行工作流
            OverAllState overAllState = graph.invoke(state, resumeConfig).get();
        } catch (Exception e) {
            log.error("error msg = {}", e.getMessage(), e);
        }
    }
}
