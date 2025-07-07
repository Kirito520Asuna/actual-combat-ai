package com.actual.combat.ai.langchain4j.config;

import com.actual.combat.ai.langchain4j.abs.AbstractLangchain4j;
import com.actual.combat.ai.langchain4j.abs.AbstractLangchain4jConfiguration;
import dev.langchain4j.memory.ChatMemory;
import dev.langchain4j.memory.chat.ChatMemoryProvider;
import dev.langchain4j.rag.content.retriever.ContentRetriever;
import dev.langchain4j.store.embedding.EmbeddingStore;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;

/**
 * @Author yan
 * @Date 2025/7/7 16:31:21
 * @Description
 */
@AutoConfigureBefore(AbstractLangchain4j.class)
@ConditionalOnMissingBean(AbstractLangchain4jConfiguration.class)
@AutoConfiguration
public class Langchain4jConfig implements AbstractLangchain4jConfiguration {

    @Bean
    @ConditionalOnMissingBean(ChatMemory.class)
    @Override
    public ChatMemory chatMemory() {
        return AbstractLangchain4jConfiguration.super.chatMemory();
    }


    @Bean
    @ConditionalOnMissingBean(ChatMemoryProvider.class)
    @Override
    public ChatMemoryProvider chatMemoryProvider() {
        return AbstractLangchain4jConfiguration.super.chatMemoryProvider();
    }


    @Bean
    @ConditionalOnMissingBean(EmbeddingStore.class)
    @Override
    public EmbeddingStore store() {
        return AbstractLangchain4jConfiguration.super.store();
    }

    @Bean
    @ConditionalOnMissingBean(ContentRetriever.class)
    @Override
    public ContentRetriever contentRetriever() {
        return AbstractLangchain4jConfiguration.super.contentRetriever();
    }
}
