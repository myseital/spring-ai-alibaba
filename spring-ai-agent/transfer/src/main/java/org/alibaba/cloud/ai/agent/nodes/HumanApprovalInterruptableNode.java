package org.alibaba.cloud.ai.agent.nodes;

import com.alibaba.cloud.ai.graph.OverAllState;
import com.alibaba.cloud.ai.graph.RunnableConfig;
import com.alibaba.cloud.ai.graph.StateGraph;
import com.alibaba.cloud.ai.graph.action.AsyncNodeActionWithConfig;
import com.alibaba.cloud.ai.graph.action.InterruptableAction;
import com.alibaba.cloud.ai.graph.action.InterruptionMetadata;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

/**
 *
 * @author myseital
 * @date 2026/4/15
 */
@Slf4j
public class HumanApprovalInterruptableNode implements AsyncNodeActionWithConfig, InterruptableAction {

    @Override
    public Optional<InterruptionMetadata> interrupt(String nodeId, OverAllState state, RunnableConfig config) {
        log.info("HumanApprovalNode.interrupt() called, nodeId: {}", nodeId);
        String nextStep = StateGraph.END;
        Optional<Object> feedback = config.metadata(RunnableConfig.HUMAN_FEEDBACK_METADATA_KEY);
        if (feedback.isPresent()) {
            Map<String, Object> feedbackData = (Map<String, Object>) feedback.get();
            Boolean approval = (Boolean) feedbackData.get("approval");
            log.info("Human feedback received, approval: {}", approval);
            if (Boolean.TRUE.equals(approval)) {
                nextStep = "createInventoryTransferNode";
            }
        }
        InterruptionMetadata interruptionMetadata = InterruptionMetadata.builder(nodeId, state)
                .addMetadata("humanApprovalNextStep", nextStep)
                .build();

        return Optional.of(interruptionMetadata);
    }

    @Override
    public CompletableFuture<Map<String, Object>> apply(OverAllState state, RunnableConfig config) {
        log.info("HumanApprovalNode.apply() executing");
        boolean approval = state.value("approval", false);
        String nextStep = StateGraph.END;
        if (approval) {
            nextStep = "createInventoryTransferNode";
        }

        return CompletableFuture.completedFuture(Map.of("humanApprovalNextStep", nextStep));
    }
}
