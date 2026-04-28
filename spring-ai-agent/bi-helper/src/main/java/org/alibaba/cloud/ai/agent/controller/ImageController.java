package org.alibaba.cloud.ai.agent.controller;

import com.alibaba.cloud.ai.dashscope.image.DashScopeImageModel;
import com.alibaba.cloud.ai.dashscope.image.DashScopeImageOptions;
import com.yomahub.liteflow.core.FlowExecutor;
import com.yomahub.liteflow.flow.LiteflowResponse;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.alibaba.cloud.ai.agent.domain.dto.CustomContext;
import org.springframework.ai.image.ImageMessage;
import org.springframework.ai.image.ImagePrompt;
import org.springframework.ai.image.ImageResponse;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Stack;

/**
 *
 * @author myseital
 * @date 2026/4/29
 */
@Slf4j
@RestController
@RequestMapping("/image")
public class ImageController {

    @Resource
    private FlowExecutor flowExecutor;

    @Resource
    @Qualifier("dashScopeImageModel")
    private DashScopeImageModel dashScopeImageModel;

    @GetMapping("/zz")
    public String zz(@RequestParam String url) {
        ImageMessage imageMessage = new ImageMessage(url);
        DashScopeImageOptions options = DashScopeImageOptions.builder()
                .style("2")
                .build();
        ImagePrompt imagePrompt = new ImagePrompt(url, options);
        ImageResponse imageResponse = dashScopeImageModel.call(imagePrompt);

        return "";
    }


    @GetMapping("/test")
    public String test(@RequestParam String input) {
        CustomContext context = new CustomContext();
        Stack<String> stack = new Stack<>();
        stack.push(input);
        context.setMsg(stack);
        LiteflowResponse response = flowExecutor.execute2Resp("chain1", context, CustomContext.class);
        CustomContext contextBean = response.getContextBean(CustomContext.class);
        return contextBean.getMsg().peek();
    }
}
