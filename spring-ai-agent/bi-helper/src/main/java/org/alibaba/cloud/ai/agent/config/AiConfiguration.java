package org.alibaba.cloud.ai.agent.config;

import io.micrometer.observation.ObservationRegistry;
import io.milvus.client.MilvusServiceClient;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.observation.AdvisorObservationConvention;
import org.springframework.ai.chat.client.observation.ChatClientObservationConvention;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.model.chat.client.autoconfigure.ChatClientBuilderConfigurer;
import org.springframework.ai.vectorstore.milvus.MilvusVectorStore;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

/**
 *
 * @author myseital
 * @date 2026/4/29
 */
@Configuration
public class AiConfiguration {

    @Bean
    public MilvusVectorStore milvusVectorStore(
            MilvusServiceClient milvusServiceClient,
            @Qualifier("openAiEmbeddingModel") EmbeddingModel embeddingModel) {
        return MilvusVectorStore.builder(milvusServiceClient, embeddingModel).build();
    }

    @Bean
    @Scope("prototype")
    @ConditionalOnMissingBean
    ChatClient.Builder chatClientBuilder(ChatClientBuilderConfigurer chatClientBuilderConfigurer,
                                         @Qualifier("openAiChatModel") ChatModel chatModel,
                                         ObjectProvider<ObservationRegistry> observationRegistry,
                                         ObjectProvider<ChatClientObservationConvention> chatClientObservationConvention,
                                         ObjectProvider<AdvisorObservationConvention> advisorObservationConvention) {
        ChatClient.Builder builder = ChatClient.builder(chatModel, (ObservationRegistry)observationRegistry.getIfUnique(() -> ObservationRegistry.NOOP), (ChatClientObservationConvention)chatClientObservationConvention.getIfUnique(() -> null), (AdvisorObservationConvention)advisorObservationConvention.getIfUnique(() -> null));
        return chatClientBuilderConfigurer.configure(builder);
    }
}
