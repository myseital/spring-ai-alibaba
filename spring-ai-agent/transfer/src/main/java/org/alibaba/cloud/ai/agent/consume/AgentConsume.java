package org.alibaba.cloud.ai.agent.consume;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.json.JSONUtil;
import com.alibaba.cloud.ai.graph.CompiledGraph;
import com.alibaba.cloud.ai.graph.OverAllState;
import com.alibaba.cloud.ai.graph.RunnableConfig;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.alibaba.cloud.ai.agent.constants.KeyPrefixConstant;
import org.alibaba.cloud.ai.agent.constants.TopicConstant;
import org.alibaba.cloud.ai.agent.domain.param.ProductSaleParam;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 *
 * @author myseital
 * @date 2026/4/16
 */
@Slf4j
@Service
public class AgentConsume {

    @Resource
    private CompiledGraph graph;

    @Resource
    private RedisTemplate redisTemplate;

    @KafkaListener(topics = {TopicConstant.PRODUCT_SALE_TOPIC}, groupId = "group1")
    public void agentConsume(List<String> messages, Acknowledgment acknowledgement) {
        try {
            log.info("receive message size ={}", messages.size());
            for (String message : messages) {
                log.info("receive message={}", messages);
                ProductSaleParam saleParam = JSONUtil.toBean(message, ProductSaleParam.class);
                Long productId = saleParam.getProductId();
                String key = KeyPrefixConstant.PRODUCT_SALE_ONLY_KEY + ":" + productId + ":"
                        + saleParam.getWarehouseId() + ":"
                        + DateUtil.format(saleParam.getSaleDate(), "yyyy-MM-dd");
                Boolean first = redisTemplate.opsForValue().setIfAbsent(key, message, 1, TimeUnit.DAYS);
                if (!Boolean.TRUE.equals(first)) {
                    acknowledgement.acknowledge();
                    return;
                }
                String threadId = IdUtil.simpleUUID();
                RunnableConfig runnableConfig = RunnableConfig.builder()
                        .threadId(threadId)
                        .build();
                OverAllState overAllState = graph.invoke(
                        Map.of("productId", String.valueOf(productId), "threadId", threadId), runnableConfig).get();

                acknowledgement.acknowledge();
                log.info("agentConsume end ....");
            }
        } catch (Exception e) {
            log.error("agentConsume error, msg= {}", e.getMessage(), e);
        }
    }
}
