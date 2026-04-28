package org.alibaba.cloud.ai.agent.nodes;

import com.alibaba.cloud.ai.graph.OverAllState;
import com.alibaba.cloud.ai.graph.action.NodeAction;
import lombok.extern.slf4j.Slf4j;
import org.mapstruct.Qualifier;
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
 * @date 2026/4/20
 */
@Slf4j
public class GenSQLNode implements NodeAction {

    private final ChatClient.Builder chatClientBuilder;

    private final VectorStore vectorStore;

    public GenSQLNode(ChatClient.Builder chatClientBuilder, VectorStore vectorStore) {
        this.chatClientBuilder = chatClientBuilder;
        this.vectorStore = vectorStore;
    }

    @Override
    public Map<String, Object> apply(OverAllState state) throws Exception {
        String userInput = state.value("userInput", "");

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

        Flux<String> content = chatClientBuilder.build().prompt()
                .advisors(advisor)
                .system("""
                        # 角色
                        你是一名熟练的 SQL 专家，负责根据企业数据衣结构生成 SQL 查询。用户将以自然语言提出数据需求。
                        你有能力访问企业数据库表结构和表之间的关系(这些信息通过矢量数据库检索得到)
                        
                        # 要求
                        1. 仅生成可执行 SQL，不输出任何与SQL 无关的文字或解释，也不可以输出。
                        2. 在生成 SQL 前，首先理解用户需求和检索得到的表结构信息。
                        3. 根据表结构和关系选择合适的表和字段，生成可执行的 SQL。
                        4. 输出 SQL 时,禁止使用markdown格式```sqL来输出,直接以文本格式输出。
                        5. 如果存在多种实现方式，优先选择最简洁、性能较优的写法
                        6. 禁止输出与 SQL 无关的文本或解释。
                        7. 不可凭空虚构数据，若数据不足，请返回空字符串。
                        8. 禁止生成 DELETE、DROP、UPDATE、INSERT 语句，只允生成SELECT语句。
                           当用户的输入涉及DELETE、DROP、UPDATE、INSERT这四种不安全SQL时，你必须输出空内容。
                        9. 每个查询字段必须使用 AS 指定一个中文别名，中文别名应尽量简短、清晰、贴合字段含义。
                           例如：user_name AS '用户名'
                        """)
                .user(userInput)
                .stream().content();
        StringBuilder sb = new StringBuilder();
        content.doOnNext(sb::append).blockLast();
        log.info("genSQL={}", sb);

        return Map.of("genSQL", sb.toString());
    }
}
