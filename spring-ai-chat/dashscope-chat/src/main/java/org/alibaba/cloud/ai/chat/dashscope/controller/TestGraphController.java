package org.alibaba.cloud.ai.chat.dashscope.controller;

import com.alibaba.cloud.ai.graph.CompiledGraph;
import com.alibaba.cloud.ai.graph.OverAllState;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.Optional;

/**
 *
 * @author myseital
 * @date 2026/4/13 6:39
 */
@Slf4j
@RestController
@RequestMapping("/graph")
public class TestGraphController {

    @Resource
    private CompiledGraph testGraph;

    @GetMapping("/test")
    public Map<String, Object> test(@RequestParam String msg) {
        OverAllState overAllState = testGraph.invoke(Map.of("msg", msg)).get();

        return overAllState.data();
    }
}
