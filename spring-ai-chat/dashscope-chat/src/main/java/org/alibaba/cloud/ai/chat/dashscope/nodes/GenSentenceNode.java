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
 * @date 2026/4/13
 */
@Slf4j
public class GenSentenceNode implements NodeAction {

    private final ChatClient chatClient;

    public GenSentenceNode(ChatClient chatClient) {
        this.chatClient = chatClient;
    }

    @Override
    public Map<String, Object> apply(OverAllState state) throws Exception {
        // 获取用户输入的单词
        String msg = state.value("msg", "");
        Flux<String> content = chatClient.prompt()
                .user(t -> t.text("""
                        根据用户输入的单词：{msg}
                        生成一个句子
                        """).param("msg", msg))
                .stream().content();
        StringBuilder sb = new StringBuilder();
        content.doOnNext(sb::append).blockLast();
        log.info("sentence={}", sb);

        return Map.of("sentence", sb.toString());
    }
}
