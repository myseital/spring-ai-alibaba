package org.alibaba.cloud.ai.agent.controller;

import cn.hutool.core.util.IdUtil;
import com.alibaba.cloud.ai.graph.CompiledGraph;
import com.alibaba.cloud.ai.graph.OverAllState;
import com.alibaba.cloud.ai.graph.RunnableConfig;
import com.alibaba.cloud.ai.graph.state.StateSnapshot;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.alibaba.cloud.ai.agent.domain.param.ProductSaleParam;
import org.alibaba.cloud.ai.agent.service.ProductSaleService;
import org.alibaba.cloud.ai.common.domain.R;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/saleProduct")
public class SaleProductController {

    @Resource
    private CompiledGraph graph;

    @Resource
    private ProductSaleService productSaleService;

    @PostMapping("/sale")
    public R sale(@RequestBody ProductSaleParam productSaleParam) {
        productSaleService.sale(productSaleParam);
        return R.ok();
    }


    @GetMapping("/sale")
    public Map<String, Object> sale(@RequestParam String productId) {
        String threadId = IdUtil.simpleUUID();
        RunnableConfig runnableConfig = RunnableConfig.builder()
                .threadId(threadId)
                .build();
        OverAllState overAllState = graph.invoke(
                Map.of("productId", productId, "threadId", threadId), runnableConfig).get();

        return overAllState.data();
    }

    @GetMapping("/approval")
    public R approval(@RequestParam Boolean approval, @RequestParam String threadId) {
        productSaleService.approval(approval, threadId);
        return R.ok();
    }
}
