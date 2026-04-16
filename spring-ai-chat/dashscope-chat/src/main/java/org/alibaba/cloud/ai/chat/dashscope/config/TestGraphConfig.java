package org.alibaba.cloud.ai.chat.dashscope.config;

import com.alibaba.cloud.ai.graph.CompiledGraph;
import com.alibaba.cloud.ai.graph.KeyStrategy;
import com.alibaba.cloud.ai.graph.KeyStrategyFactory;
import com.alibaba.cloud.ai.graph.StateGraph;
import com.alibaba.cloud.ai.graph.state.strategy.ReplaceStrategy;
import jakarta.annotation.Resource;
import org.alibaba.cloud.ai.chat.dashscope.nodes.GenSentenceNode;
import org.alibaba.cloud.ai.chat.dashscope.nodes.TransferEnglishNode;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

import static com.alibaba.cloud.ai.graph.action.AsyncNodeAction.node_async;

/**
 *
 * @author myseital
 * @date 2026/4/13
 */
@Configuration
public class TestGraphConfig {

    @Bean
    public CompiledGraph testGraph(ChatClient.Builder chatClientBuilder) throws Exception {
//        KeyStrategyFactory keyStrategyFactory = new KeyStrategyFactoryBuilder()
//                .addPatternStrategy("session_id", new ReplaceStrategy())
//                .addPatternStrategy("user_input", new ReplaceStrategy())
//                .addPatternStrategy("intent_type", new ReplaceStrategy())
//                .addPatternStrategy("chat_reply", new ReplaceStrategy())
//                .addPatternStrategy("tasks", new ReplaceStrategy())
//                .addPatternStrategy("created_task", new ReplaceStrategy())
//                .addPatternStrategy("answer", new ReplaceStrategy())
//                .build();
        ChatClient chatClient = chatClientBuilder.build();

        KeyStrategyFactory keyStrategyFactory = new KeyStrategyFactory() {
            @Override
            public Map<String, KeyStrategy> apply() {
                return Map.of("msg", new ReplaceStrategy(),
                        "sentence", new ReplaceStrategy(),
                        "English", new ReplaceStrategy());
            }
        };
        StateGraph testGraph = new StateGraph("testGraph", keyStrategyFactory);
        // 在图中添加节点
        testGraph.addNode("getSentence", node_async(new GenSentenceNode(chatClient)));
        testGraph.addNode("transferEnglish", node_async(new TransferEnglishNode(chatClient)));

        // 添加边
        testGraph.addEdge(StateGraph.START, "getSentence");
        testGraph.addEdge("getSentence", "transferEnglish");
        testGraph.addEdge("transferEnglish", StateGraph.END);

        return testGraph.compile();
    }
}
