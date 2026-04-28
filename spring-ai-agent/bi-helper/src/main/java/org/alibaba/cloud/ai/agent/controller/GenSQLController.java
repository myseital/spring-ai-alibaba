package org.alibaba.cloud.ai.agent.controller;

import com.alibaba.cloud.ai.graph.CompiledGraph;
import com.alibaba.cloud.ai.graph.OverAllState;
import com.alibaba.cloud.ai.graph.RunnableConfig;
import jakarta.annotation.Resource;
import org.alibaba.cloud.ai.common.domain.R;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 *
 * @author myseital
 * @date 2026/4/20
 */
@RestController
@RequestMapping("/gen_sql")
public class GenSQLController {

    private final static String THREAD_PREFIX = "threadId:";

    @Resource
    private CompiledGraph compiledGraph;

    @GetMapping("/talk")
    public R talk(@RequestParam String userInput, @RequestParam String userId) {
        RunnableConfig runnableConfig = RunnableConfig.builder()
                .threadId(THREAD_PREFIX + userId)
                .build();
        OverAllState overAllState = compiledGraph.invoke(
                Map.of("userInput", userInput, "loop", 0), runnableConfig).get();
        Map<String, Object> data = overAllState.data();

        return R.ok(data);
    }
}
