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
import org.alibaba.cloud.ai.agent.nodes.*;
import org.alibaba.cloud.ai.agent.service.EmailService;
import org.alibaba.cloud.ai.agent.service.InventoryOrderService;
import org.alibaba.cloud.ai.agent.service.SaleRecordService;
import org.alibaba.cloud.ai.agent.service.TransferOrderService;
import org.alibaba.cloud.ai.agent.service.base.BbInventoryService;
import org.redisson.api.RedissonClient;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Map;

import static com.alibaba.cloud.ai.graph.action.AsyncNodeAction.node_async;

@Slf4j
@Configuration
public class GraphConfig {

    @Resource
    private SaleRecordService saleRecordService;

    @Resource
    private BbInventoryService bbInventoryService;

    @Resource
    private InventoryOrderService inventoryOrderService;

    @Resource
    private EmailService emailService;

    @Resource
    private TransferOrderService transferOrderService;

    // 注入 Ollama 的 ChatClient
    @Autowired
    @Qualifier("ollamaChatClient")
    private ChatClient ollamaChatClient;

    // 注入通义千问的 ChatClient
    @Autowired
    @Qualifier("dashScopeChatClient")
    private ChatClient dashScopeChatClient;

    @Resource
    private RedissonClient redissonClient;

    @Bean
    public CompiledGraph graph() throws GraphStateException {
        KeyStrategyFactory keyStrategyFactory = new KeyStrategyFactory() {
            @Override
            public Map<String, KeyStrategy> apply() {
                return Map.of("productId", new ReplaceStrategy(),
                        "saleRecordData", new ReplaceStrategy(),
                        "nowProductInventoryData", new ReplaceStrategy(),
                        "inventoryOrderData", new ReplaceStrategy(),
                        "inventoryTransferStr", new ReplaceStrategy(),
                        "inventoryTransferJsonStr", new ReplaceStrategy(),
                        "humanApprovalNextStep", new ReplaceStrategy(),
                        "threadId", new ReplaceStrategy()
                );
            }
        };

        StateGraph stateGraph = new StateGraph("inventoryTransferGraph", keyStrategyFactory);

        // 往图中定义节点
        stateGraph.addNode("saleRecordDataNode", node_async(new CollectSaleRecordNode(saleRecordService, bbInventoryService)));
        stateGraph.addNode("inventoryOrderData", node_async(new CollectInventoryOrderNode(inventoryOrderService)));
        stateGraph.addNode("predictNode", node_async(new PredictNode(ollamaChatClient)));
        stateGraph.addNode("extractNode", node_async(new ExtractNode(ollamaChatClient)));
        stateGraph.addNode("sendMailNode", node_async(new SendMailNode(emailService)));
        stateGraph.addNode("createInventoryTransferNode", node_async(new CreateInventoryTransferNode(transferOrderService)));
//        stateGraph.addNode("humanApprovalNode", node_async(new HumanApprovalNode()));
        stateGraph.addNode("humanApprovalNode", AsyncNodeActionWithConfig.node_async(new HumanApprovalNodeWishConfig()));
//        stateGraph.addNode("humanApprovalNode", new HumanApprovalNode());

        // 往图中加边
        stateGraph.addEdge(StateGraph.START, "saleRecordDataNode");
        stateGraph.addEdge(StateGraph.START, "inventoryOrderData");
        stateGraph.addEdge("saleRecordDataNode", "predictNode");
        stateGraph.addEdge("inventoryOrderData", "predictNode");
        stateGraph.addEdge("predictNode", "extractNode");
        stateGraph.addEdge("extractNode", "sendMailNode");
        stateGraph.addEdge("sendMailNode", "humanApprovalNode");
//        stateGraph.addEdge("humanApprovalNode", "createInventoryTransferNode");
        stateGraph.addConditionalEdges("humanApprovalNode", AsyncEdgeAction.edge_async(new ApprovalEdge()),
                Map.of("createInventoryTransferNode", "createInventoryTransferNode", StateGraph.END, StateGraph.END));
        stateGraph.addEdge("createInventoryTransferNode", StateGraph.END);

        // 创建持久化配置
        RedisSaver redisSaver = RedisSaver.builder().redisson(redissonClient).build();
        SaverConfig saverConfig = SaverConfig.builder()
                .register(redisSaver)
                .build();

        // 定义编译配置
        CompileConfig compileConfig = CompileConfig.builder()
                .interruptBefore("humanApprovalNode")
                .saverConfig(saverConfig)
                .build();
        CompiledGraph compiledGraph = stateGraph.compile(compileConfig);

        // 实现PlantUML可视化
        printGraphRepresentation(stateGraph, "AI智能调拨工作流");

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
