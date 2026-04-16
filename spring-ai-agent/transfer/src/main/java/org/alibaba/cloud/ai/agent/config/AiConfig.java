package org.alibaba.cloud.ai.agent.config;

import com.alibaba.cloud.ai.dashscope.chat.DashScopeChatModel;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.ollama.OllamaChatModel;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AiConfig {

    // Ollama 模型的 ChatClient
    @Bean
    public ChatClient ollamaChatClient(OllamaChatModel ollamaChatModel) {
        return ChatClient.create(ollamaChatModel);
    }

    // 通义千问模型的 ChatClient
    @Bean
    public ChatClient dashScopeChatClient(DashScopeChatModel dashScopeChatModel) {
        return ChatClient.create(dashScopeChatModel);
    }
}