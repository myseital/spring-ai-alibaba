package org.alibaba.cloud.ai.agent.edges;

import com.alibaba.cloud.ai.graph.OverAllState;
import com.alibaba.cloud.ai.graph.StateGraph;
import com.alibaba.cloud.ai.graph.action.EdgeAction;
import lombok.extern.slf4j.Slf4j;

/**
 *
 * @author myseital
 * @date 2026/4/22
 */
@Slf4j
public class EvaluateEdge implements EdgeAction {

    @Override
    public String apply(OverAllState state) throws Exception {
        String evaluateResult = state.value("evaluateResult", "");
        Integer loop = state.value("loop", 0);
        log.info("到达评估条件边，loop={}", loop);
        if (loop > 5) {
            return StateGraph.END;
        }
        if ("PASS".equals(evaluateResult)) {
            return "ExecSqlAndCreateExcelNode";
        }

        return "GenSQL";
    }
}
