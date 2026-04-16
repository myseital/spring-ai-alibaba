package org.alibaba.cloud.ai.agent.nodes;

import com.alibaba.cloud.ai.graph.OverAllState;
import com.alibaba.cloud.ai.graph.StateGraph;
import com.alibaba.cloud.ai.graph.action.NodeAction;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;

/**
 *
 * @author myseital
 * @date 2026/4/15
 */
@Slf4j
public class HumanApprovalNode implements NodeAction {

    @Override
    public Map<String, Object> apply(OverAllState state) throws Exception {
        Map<String, Object> data = state.data();
        Boolean approval = (Boolean) data.getOrDefault("approval", false);
        String nextStep = StateGraph.END;
        if (Boolean.TRUE.equals(approval)) {
            nextStep = "createInventoryTransferNode";
        }

        return Map.of("humanApprovalNextStep", nextStep);
    }


}
