package org.alibaba.cloud.ai.agent.nodes;

import com.alibaba.cloud.ai.graph.OverAllState;
import com.alibaba.cloud.ai.graph.RunnableConfig;
import com.alibaba.cloud.ai.graph.StateGraph;
import com.alibaba.cloud.ai.graph.action.NodeActionWithConfig;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.Optional;

/**
 *
 * @author myseital
 * @date 2026/4/15
 */
@Slf4j
public class HumanApprovalNodeWishConfig implements NodeActionWithConfig {

    @Override
    public Map<String, Object> apply(OverAllState state, RunnableConfig config) throws Exception {
        Optional<Object> feedback = config.metadata(RunnableConfig.HUMAN_FEEDBACK_METADATA_KEY);
        String nextStep = StateGraph.END;
        if (feedback.isPresent()) {
            Map<String, Object> feedbackData = (Map<String, Object>) feedback.get();
            Boolean approval = (Boolean) feedbackData.get("approval");
            log.info("Human feedback received, approval: {}", approval);
            if (Boolean.TRUE.equals(approval)) {
                log.info("审核成功, 生成调拨单");
                nextStep = "createInventoryTransferNode";
            } else {
                log.info("审核失败");
            }
        }

        return Map.of("humanApprovalNextStep", nextStep);
    }


}
