package org.alibaba.cloud.ai.agent.nodes;

import cn.hutool.core.util.StrUtil;
import com.alibaba.cloud.ai.graph.OverAllState;
import com.alibaba.cloud.ai.graph.action.NodeAction;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import reactor.core.publisher.Flux;

import java.util.Map;

/**
 *
 * @author myseital
 * @date 2026/4/14
 */
@Slf4j
public class ExtractNode implements NodeAction {

    private final ChatClient chatClient;

    public ExtractNode(ChatClient chatClient) {
        this.chatClient = chatClient;
    }

    @Override
    public Map<String, Object> apply(OverAllState state) throws Exception {
        String inventoryTransferStr = state.value("inventoryTransferStr", "");
        if (StrUtil.isBlank(inventoryTransferStr)) {
            return Map.of();
        }
        Flux<String> content = chatClient.prompt()
                .system("""
                        # 角色
                            你是一个数据处理专家，能够从文本数据中提取JSON数据并且修正格式
                        # 输出要求
                            仅输出JSON数据，禁止改变原来的数据类型，并且不要输出任何解释，任何备注或者Markdown标记。
                            输出时不要包含   ```json或者```之类的包裹符号，也不要包含多余的文字。
                        """)
                .user(t -> t.text("""
                        文本数据：{content}
                        """).param("content", inventoryTransferStr))
                .stream().content();
        StringBuilder sb = new StringBuilder();
        content.doOnNext(sb::append).blockLast();
        log.info("ExtractNode result=[{}]", sb);

        return Map.of("inventoryTransferJsonStr", sb.toString());
    }
}
