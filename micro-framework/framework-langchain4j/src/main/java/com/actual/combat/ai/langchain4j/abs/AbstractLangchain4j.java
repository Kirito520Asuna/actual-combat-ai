package com.actual.combat.ai.langchain4j.abs;

import cn.hutool.extra.spring.SpringUtil;
import com.actual.combat.ai.langchain4j.properties.LoaderDocumentProperties;
import dev.langchain4j.data.document.DocumentSplitter;
import dev.langchain4j.data.document.splitter.DocumentSplitters;
import dev.langchain4j.memory.ChatMemory;
import dev.langchain4j.memory.chat.ChatMemoryProvider;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.rag.content.retriever.ContentRetriever;
import dev.langchain4j.store.embedding.EmbeddingStore;
import dev.langchain4j.store.memory.chat.ChatMemoryStore;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;


/**
 * @Author yan
 * @Date 2025/7/6 20:18:42
 * @Description
 */
@Slf4j
//@ConditionalOnMissingBean(AbstractLangchain4jConfiguration.class)
//@AutoConfiguration
public abstract class AbstractLangchain4j<CM extends ChatMemory, CS extends ChatMemoryStore,
        EM extends EmbeddingModel, ES extends EmbeddingStore> implements AbstractLangchain4jConfiguration {
    @Resource
    protected CS chatMemoryStore;
    @Resource
    protected CM chatMemory;
    @Resource
    protected EM embeddingModel;
    @Resource
    protected ES embeddingStore;

    @Override
    //每段会话最多保存多少条消息
    public Integer getChatMemoryMaxMessage() {
        return 20;
    }

    @Override
    //每段?字
    public int getMaxSegmentSizeInChars() {
        return 500;
    }

    @Override
    //每段可重复?字
    public int getMaxOverlapSizeInChars() {
        return 100;
    }

    @Override
    //向量检索最小分数
    public Double getMinScoreOnContentRetriever() {
        return 0.5;
    }

    @Override
    //向量检索最大结果数
    public Integer getMaxResultsOnContentRetriever() {
        return 3;
    }

    //构建会话对象
    @Override
    @Bean
    @ConditionalOnMissingBean(ChatMemory.class)
    public ChatMemory chatMemory() {
        Integer maxMessage = getChatMemoryMaxMessage();
        return AbstractLangchain4jConfiguration.super.chatMemory(maxMessage);
    }

    @Override
    //构建ChatMemoryProvider会话对象
    @Bean
    @ConditionalOnMissingBean(ChatMemoryProvider.class)
    public ChatMemoryProvider chatMemoryProvider() {
        Integer maxMessage = getChatMemoryMaxMessage();
        return AbstractLangchain4jConfiguration.super.chatMemoryProvider(maxMessage, chatMemoryStore);
    }

    @Override
    @Bean
    @ConditionalOnMissingBean(EmbeddingStore.class)
    //构建向量数据库对象 不需要每次启动都向量化只需要第一次启动向量化
    public EmbeddingStore store() {
        //获取LoaderDocumentProperties对象
        LoaderDocumentProperties loaderDocumentProperties = SpringUtil.getBean(LoaderDocumentProperties.class);
        //构建内存向量数据库
        //InMemoryEmbeddingStore store = new InMemoryEmbeddingStore<>();
        //构建文档切割对象 500==>每段500字 100==>每段可重复100字 对于.md会回答失效
        int maxSegmentSizeInChars = getMaxSegmentSizeInChars();
        int maxOverlapSizeInChars = getMaxOverlapSizeInChars();
        DocumentSplitter ds = DocumentSplitters.recursive(maxSegmentSizeInChars, maxOverlapSizeInChars);
        AbstractLangchain4jConfiguration.super.store(loaderDocumentProperties, ds, embeddingStore, embeddingModel);
        return embeddingStore;
    }

    @Override
    //构建向量数据库检索对象
    @Bean
    @ConditionalOnMissingBean(ContentRetriever.class)
    public ContentRetriever contentRetriever() {
        Double minScore = getMinScoreOnContentRetriever();
        Integer maxResults = getMaxResultsOnContentRetriever();
        return AbstractLangchain4jConfiguration.super.contentRetriever(embeddingStore, embeddingModel, minScore, maxResults);
    }
}
