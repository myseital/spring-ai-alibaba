package org.alibaba.cloud.ai.agent.component;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.yomahub.liteflow.annotation.LiteflowComponent;
import com.yomahub.liteflow.core.NodeComponent;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.alibaba.cloud.ai.agent.domain.dto.CustomContext;
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
@LiteflowComponent("genPhoto")
public class GenPhotoComponent extends NodeComponent {

    @Value("${SILICONFLOW_AGENT_API_KEY}")
    private String apiKey;

    @Resource
    @Qualifier("openAiImageModel")
    private ImageModel imageModel;

    @Override
    public void process() throws Exception {
        String input = this.getCmpData(String.class);
        ImageResponse response = imageModel.call(new ImagePrompt(input));
        String imageUrl = response.getResult().getOutput().getUrl();
        log.info("生成图片={}", imageUrl);
        CustomContext context = this.getContextBean(CustomContext.class);
        context.getMsg().push(imageUrl);
    }

    private void httpCall() {
        JSONObject requestBody = new JSONObject();
        requestBody.set("model", "Kwai-Kolors/Kolors");
        requestBody.set("prompt", this.getCmpData(String.class));
        requestBody.set("image_size", "1024x1024");
        requestBody.set("batch_size", 1);
        requestBody.set("num_inference_steps", 20);
        requestBody.set("guidance_scale", 7.5);

        HttpRequest request = HttpRequest.post("https://api.siliconflow.cn/v1/images/generations")
                .header("Authorization", "Bearer " + apiKey)
                .header("Content-Type", "application/json")
                .body(requestBody.toString(), "application/json");

        HttpResponse response = request.execute();
        String body = response.body();
        JSONObject jsonObject = JSONUtil.parseObj(body);
        JSONArray images = jsonObject.getJSONArray("images");
        JSONObject image = (JSONObject) images.get(0);
        String url = image.get("url", String.class);
        log.info("url={}", url);
    }
}
