package org.alibaba.cloud.ai.agent.nodes;

import cn.hutool.core.util.StrUtil;
import com.alibaba.cloud.ai.graph.OverAllState;
import com.alibaba.cloud.ai.graph.StateGraph;
import com.alibaba.cloud.ai.graph.action.EdgeAction;

import java.util.Map;

/**
 *
 * @author myseital
 * @date 2026/4/15
 */
public class ApprovalEdge implements EdgeAction {

    @Override
    public String apply(OverAllState state) throws Exception {
        String humanApprovalNextStep = state.value("humanApprovalNextStep", "");
        if ("createInventoryTransferNode".equals(humanApprovalNextStep)) {
            return "createInventoryTransferNode";
        }

        return StateGraph.END;
    }
}
