package org.alibaba.cloud.ai.agent.component;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.alibaba.cloud.ai.dashscope.image.DashScopeImageOptions;
import com.yomahub.liteflow.annotation.LiteflowComponent;
import com.yomahub.liteflow.core.NodeComponent;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.alibaba.cloud.ai.agent.domain.dto.CustomContext;
import org.springframework.ai.image.ImageMessage;
import org.springframework.ai.image.ImageModel;
import org.springframework.ai.image.ImagePrompt;
import org.springframework.ai.image.ImageResponse;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;

/**
 *
 * @author myseital
 * @date 2026/4/29
 */
@Slf4j
@LiteflowComponent("editPhoto")
public class EditPhoto extends NodeComponent {

    @Value("${DASHSCOPE_API_KEY}")
    private String apiKey;

    @Resource
    @Qualifier("dashScopeImageModel")
    private ImageModel imageModel;

    @Override
    public void process() throws Exception {
        CustomContext context = this.getContextBean(CustomContext.class);
        String imageUrl = context.getMsg().peek();
        String styleIndex = this.getCmpData(String.class);

        JSONObject requestBody = new JSONObject();
        requestBody.set("model", "wanx-style-repaint-v1");
        JSONObject input = new JSONObject();
        input.set("image_url", imageUrl);
        input.set("style_index", Integer.parseInt(styleIndex));
        requestBody.set("input", input);

        HttpResponse response = HttpRequest.post("https://dashscope.aliyuncs.com/api/v1/services/aigc/image-generation/generation")
                .header("X-DashScope-Async", "enable")
                .header("Authorization", "Bearer " + apiKey)
                .header("Content-Type", "application/json")
                .body(requestBody.toString(), "application/json")
                .execute(true);
        String bodyStr = response.body();
        JSONObject responseObj = JSONUtil.parseObj(bodyStr);
        JSONObject output = responseObj.get("output", JSONObject.class);
        String taskStatus = output.get("task_status", String.class);
        if ("FAILED".equals(taskStatus) || "UNKNOWN".equals(taskStatus)) {
            throw new RuntimeException("任务风格重绘失败");
        }
        Thread.sleep(60 * 1000);
        String taskId = output.get("task_id", String.class);
        HttpResponse taskResponse = HttpRequest.get("https://dashscope.aliyuncs.com/api/v1/tasks/" + taskId)
                .header("Authorization", "Bearer " + apiKey).execute();

        JSONObject taskBodyObj = JSONUtil.parseObj(taskResponse.body());
        JSONObject jsonObject = taskBodyObj.get("output", JSONObject.class);
        JSONArray results = jsonObject.get("results", JSONArray.class);
        JSONObject result = (JSONObject) results.get(0);
        String url = result.get("url", String.class);

        log.info("新风格图片={}", url);
        context.getMsg().push(url);
    }

    private void modelCall() {
        CustomContext context = this.getContextBean(CustomContext.class);
        String imageUrl = context.getMsg().peek();
        String styleIndex = this.getCmpData(String.class);
        ImageMessage imageMessage = new ImageMessage(imageUrl);
        DashScopeImageOptions options = DashScopeImageOptions.builder()
                .build();
        ImagePrompt imagePrompt = new ImagePrompt(imageUrl);
        ImageResponse imageResponse = imageModel.call(imagePrompt);
    }
}
