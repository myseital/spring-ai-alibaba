package org.alibaba.cloud.ai.chat.dashscope.nodes;

import com.alibaba.cloud.ai.graph.OverAllState;
import com.alibaba.cloud.ai.graph.action.NodeAction;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import reactor.core.publisher.Flux;

import java.util.Map;

/**
 *
 * @author myseital
 * @date 2026/4/13 6:30
 */
@Slf4j
public class TransferEnglishNode implements NodeAction {
    private final ChatClient chatClient;

    public TransferEnglishNode(ChatClient chatClient) {
        this.chatClient = chatClient;
    }

    @Override
    public Map<String, Object> apply(OverAllState state) throws Exception {
        // 获取用户输入的单词
        String sentence = state.value("sentence", "");
        Flux<String> content = chatClient.prompt()
                .user(t -> t.text("""
                        根据输入的句子：{sentence}
                        翻译成英语
                        """).param("sentence", sentence))
                .stream().content();
        StringBuilder sb = new StringBuilder();
        // 阻塞在这里，收集LLM流式输出的内容放到sb中
        content.doOnNext(sb::append).blockLast();
        log.info("English={}", sb.toString());

        return Map.of("English", sb.toString());
    }
}
