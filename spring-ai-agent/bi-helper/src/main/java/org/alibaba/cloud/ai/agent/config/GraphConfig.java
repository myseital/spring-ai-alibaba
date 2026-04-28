package org.alibaba.cloud.ai.agent.config;

import com.alibaba.cloud.ai.graph.*;
import com.alibaba.cloud.ai.graph.action.AsyncEdgeAction;
import com.alibaba.cloud.ai.graph.action.AsyncNodeActionWithConfig;
import com.alibaba.cloud.ai.graph.checkpoint.config.SaverConfig;
import com.alibaba.cloud.ai.graph.checkpoint.savers.redis.RedisSaver;
import com.alibaba.cloud.ai.graph.exception.GraphStateException;
import com.alibaba.cloud.ai.graph.state.strategy.ReplaceStrategy;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import net.sourceforge.plantuml.FileFormat;
import net.sourceforge.plantuml.FileFormatOption;
import net.sourceforge.plantuml.SourceStringReader;
import org.alibaba.cloud.ai.agent.edges.EvaluateEdge;
import org.alibaba.cloud.ai.agent.nodes.EvaluateNode;
import org.alibaba.cloud.ai.agent.nodes.ExecSqlAndCreateExcelNode;
import org.alibaba.cloud.ai.agent.nodes.GenSQLNode;
import org.alibaba.cloud.ai.agent.nodes.SendEmailNode;
import org.alibaba.cloud.ai.agent.service.EmailService;
import org.mapstruct.Qualifier;
import org.redisson.api.RedissonClient;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Map;

import static com.alibaba.cloud.ai.graph.action.AsyncNodeAction.node_async;

/**
 *
 * @author myseital
 * @date 2026/4/20
 */
@Slf4j
@Configuration
public class GraphConfig {

    @Resource
    private RedissonClient redissonClient;

    @Resource
    private VectorStore vectorStore;

    @Resource
    private JdbcTemplate jdbcTemplate;

    @Resource
    private EmailService emailService;

    @Bean
    public CompiledGraph graph(ChatClient.Builder chatClientBuilder) throws GraphStateException {
        KeyStrategyFactory keyStrategyFactory = new KeyStrategyFactory() {
            @Override
            public Map<String, KeyStrategy> apply() {
                return Map.of("userInput", new ReplaceStrategy(),
                        "genSQL", new ReplaceStrategy(),
                        "excelFile", new ReplaceStrategy(),
                        "evaluateResult", new ReplaceStrategy(),
                        "loop", new ReplaceStrategy()
                );
            }
        };

        StateGraph stateGraph = new StateGraph("biHelperGraph", keyStrategyFactory);

        stateGraph.addNode("GenSQLNode", node_async(new GenSQLNode(chatClientBuilder, vectorStore)));
        stateGraph.addNode("EvaluateNode", node_async(new EvaluateNode(chatClientBuilder, vectorStore)));
        stateGraph.addNode("ExecSqlAndCreateExcelNode", node_async(new ExecSqlAndCreateExcelNode(jdbcTemplate)));
        stateGraph.addNode("SendEmailNode", node_async(new SendEmailNode(emailService)));

        stateGraph.addEdge(StateGraph.START, "GenSQLNode");
        stateGraph.addEdge("GenSQLNode","EvaluateNode");
//        stateGraph.addEdge("EvaluateNode","ExecSqlAndCreateExcelNode");
        stateGraph.addConditionalEdges("EvaluateNode", AsyncEdgeAction.edge_async(new EvaluateEdge()),
                Map.of("GenSQLNode", "GenSQLNode", "ExecSqlAndCreateExcelNode", "ExecSqlAndCreateExcelNode",
                        StateGraph.END, StateGraph.END));
        stateGraph.addEdge("ExecSqlAndCreateExcelNode","SendEmailNode");
        stateGraph.addEdge("SendEmailNode", StateGraph.END);

        // 创建持久化配置
        RedisSaver redisSaver = RedisSaver.builder().redisson(redissonClient).build();
        SaverConfig saverConfig = SaverConfig.builder()
                .register(redisSaver)
                .build();

        // 定义编译配置
//        CompileConfig compileConfig = CompileConfig.builder()
//                .saverConfig(saverConfig)
//                .build();
//        CompiledGraph compiledGraph = stateGraph.compile(compileConfig);
        CompiledGraph compiledGraph = stateGraph.compile();

        // 实现PlantUML可视化
        printGraphRepresentation(stateGraph, "BI报表工作流");

        return compiledGraph;
    }

    private void printGraphRepresentation(StateGraph graph, String graphName) {
        try {
            GraphRepresentation representation = graph.getGraph(
                    GraphRepresentation.Type.PLANTUML,
                    graphName, true
            );
            log.info("\n========== {} ==========\n{}\n====================\n",
                    graphName, representation.content());
            // 生成流程图
            plantUML2PNG(representation.content());
            //todo 保存生成数据
        } catch (Exception e) {
            log.warn("Failed to generate graph representation: {}", e.getMessage());
        }
    }

    private void plantUML2PNG(String plantUmlCode) throws IOException {
        // 关闭 PlantUML 安全检查（避免部分语法报错）
//        OptionFlags.SECRET_RECOMMENDED = true;

        // 读取 PlantUML 源码
        SourceStringReader reader = new SourceStringReader(plantUmlCode);
        String outputPath = "F:\\projects\\java\\spring-ai-alibaba\\.temp\\temp.png";
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
             FileOutputStream fos = new FileOutputStream(outputPath)) {

            // 生成 PNG 图片流
            reader.outputImage(baos, new FileFormatOption(FileFormat.PNG));

            // 写入文件
            fos.write(baos.toByteArray());
            fos.flush();
        }
    }
}
