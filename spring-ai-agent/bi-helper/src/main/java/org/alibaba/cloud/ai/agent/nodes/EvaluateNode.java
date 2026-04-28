package org.alibaba.cloud.ai.agent.nodes;

import com.alibaba.cloud.ai.graph.OverAllState;
import com.alibaba.cloud.ai.graph.action.NodeAction;
import jodd.util.StringUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.rag.advisor.RetrievalAugmentationAdvisor;
import org.springframework.ai.rag.generation.augmentation.ContextualQueryAugmenter;
import org.springframework.ai.rag.preretrieval.query.transformation.RewriteQueryTransformer;
import org.springframework.ai.rag.retrieval.search.VectorStoreDocumentRetriever;
import org.springframework.ai.vectorstore.VectorStore;
import reactor.core.publisher.Flux;

import java.util.Map;

/**
 *
 * @author myseital
 * @date 2026/4/22
 */
@Slf4j
public class EvaluateNode implements NodeAction {

    private final ChatClient.Builder chatClientBuilder;

    private final VectorStore vectorStore;

    public EvaluateNode(ChatClient.Builder chatClientBuilder, VectorStore vectorStore) {
        this.chatClientBuilder = chatClientBuilder;
        this.vectorStore = vectorStore;
    }

    @Override
    public Map<String, Object> apply(OverAllState state) throws Exception {
        String genSQL = state.value("genSQL", "");
        Integer loop = state.value("loop", 0);
        loop++;
        log.info("到达评估，loop={}", loop);
        if (StringUtil.isBlank(genSQL)) {
            log.info("评估结果=PASS，结果为空");
            return Map.of("evaluateResult", "PASS", "loop", loop);
        }
        String userQuery = state.value("userInput", "");

        RewriteQueryTransformer rewriteQueryTransformer = RewriteQueryTransformer.builder()
                .chatClientBuilder(chatClientBuilder)
                .build();

        RetrievalAugmentationAdvisor advisor = RetrievalAugmentationAdvisor.builder()
                .queryTransformers(rewriteQueryTransformer)
                .documentRetriever(VectorStoreDocumentRetriever.builder()
                        .vectorStore(vectorStore).build())
                .queryAugmenter(ContextualQueryAugmenter.builder()
                        .allowEmptyContext(true).build())
                .build();

        ChatClient chatClient = chatClientBuilder.build();

        Flux<String> flux = chatClient.prompt()
                .advisors(advisor)
                .system(t -> t.text("""
                        # 角色:
                        你是一名专业SQL审查专家，专门负责评估生成的SQL查询的质量、准确性和安全性。
                        # 评估标准：
                        请严格依据以下标准来对SQL进行评估：
                        1. 语法正确性
                        - SQL语法是否符合MYSQL规范
                        - 关键字、函数名称使用是否正确
                        - 括号匹配、引号使用是否正确
                        2. Schema匹配度
                        - 检查表名、列名是否存在于提供的数据库结构中
                        - 验证数据类型是否匹配（如字符串含糊不应该用于数字列）
                        - 确认JOIN条件中使用的关联字段是否正确
                        3. 语义准确性
                        - SQL逻辑是否准确反映了用户意图: "{user_query}"
                        - WHERE条件、GROUP BY、HAVING等子句是否合理
                        - 聚合函数使用是否恰当
                        4. 执行安全性
                        - 是否值包含查询操作、禁止出现DELETE、UPDATE、DROP、INSERT等潜在的破环性操作
                        - 是否存在SQL注入风险模式
                        - 查询复杂度是否合理范围内
                        5. 性能考量
                        - 是否存在明显的性能问题（如笛卡尔积、缺少关键性过滤条件）
                        - 子查询使用是否必要和优化
                        
                        # 输出要求
                        如果SQL评估通过返回PASS，如果SQL评估不通过返回FAIl
                        """).param("user_query", userQuery))
                .user(t -> t.text("需要评估的SQL: {genSQL}").param("genSQL", genSQL))
                .stream().content();
        StringBuilder betterSQL = new StringBuilder();
        flux.doOnNext(betterSQL::append).blockLast();
        log.info("评估结果={}", betterSQL);

        return Map.of("evaluateResult", betterSQL.toString(), "loop", loop);
    }
}
