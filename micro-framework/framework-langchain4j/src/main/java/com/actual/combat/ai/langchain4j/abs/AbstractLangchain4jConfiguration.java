package com.actual.combat.ai.langchain4j.abs;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.extra.spring.SpringUtil;
import com.actual.combat.ai.langchain4j.properties.LoaderDocumentProperties;
import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.document.DocumentSplitter;
import dev.langchain4j.memory.ChatMemory;
import dev.langchain4j.memory.chat.ChatMemoryProvider;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.rag.content.retriever.ContentRetriever;
import dev.langchain4j.rag.content.retriever.EmbeddingStoreContentRetriever;
import dev.langchain4j.store.embedding.EmbeddingStore;
import dev.langchain4j.store.embedding.EmbeddingStoreIngestor;
import dev.langchain4j.store.embedding.inmemory.InMemoryEmbeddingStore;
import dev.langchain4j.store.memory.chat.ChatMemoryStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * @Author yan
 * @Date 2025/6/24 22:27:23
 * @Description
 */
public interface AbstractLangchain4jConfiguration{

    default Logger logger() {
        Logger logger = LoggerFactory.getLogger(this.getClass());
        return logger;
    }

    //每段会话最多保存多少条消息
    default Integer getChatMemoryMaxMessage() {
        return 20;
    }

    //每段?字
    default int getMaxSegmentSizeInChars() {
        return 500;
    }

    //每段可重复?字
    default int getMaxOverlapSizeInChars() {
        return 100;
    }

    //向量检索最小分数
    default Double getMinScoreOnContentRetriever() {
        return 0.5;
    }

    //向量检索最大结果数
    default Integer getMaxResultsOnContentRetriever() {
        return 3;
    }

    //构建会话对象
    default ChatMemory chatMemory(Integer maxMessages) {
        MessageWindowChatMemory.Builder builder = MessageWindowChatMemory.builder();
        if (maxMessages != null) {
            builder.maxMessages(maxMessages);
        }
        MessageWindowChatMemory build = builder.build();
        return build;
    }

    //构建ChatMemoryProvider会话对象
    default ChatMemoryProvider chatMemoryProvider(Integer maxMessages, ChatMemoryStore chatMemoryStore) {
        ChatMemoryProvider chatMemoryProvider = new ChatMemoryProvider() {
            @Override
            public ChatMemory get(Object memoryId) {
                MessageWindowChatMemory.Builder builder = MessageWindowChatMemory.builder();
                if (maxMessages != null) {
                    builder.maxMessages(maxMessages);
                }
                if (chatMemoryStore != null) {
                    builder.chatMemoryStore(chatMemoryStore);
                }
                MessageWindowChatMemory build = builder
                        .id(memoryId)
                        .build();
                return build;
            }
        };
        return chatMemoryProvider;
    }


    default EmbeddingStore store(LoaderDocumentProperties loaderDocumentProperties, DocumentSplitter documentSplitter, EmbeddingStore embeddingStore, EmbeddingModel embeddingModel) {
        List<Document> documents = loaderDocumentProperties.loadDocuments();
        //构建EmbeddingStoreIngestor对象 完成文本切割，向量话 存储
        EmbeddingStoreIngestor.Builder builder = EmbeddingStoreIngestor.builder();
        if (embeddingStore == null) {
            embeddingStore = new InMemoryEmbeddingStore<>();
        }

        builder.embeddingStore(embeddingStore);

        if (embeddingModel != null) {
            builder.embeddingModel(embeddingModel);
        }
        if (documentSplitter != null) {
            builder.documentSplitter(documentSplitter);
        }
        EmbeddingStoreIngestor ingestor = builder.build();
        //加载文档
        if (CollUtil.isNotEmpty(documents)) {
            ingestor.ingest(documents);
        } else {
            logger().warn("[local]  ==> 文档加载失败 or 无文档 <==");
        }
        return embeddingStore;
    }


    default ContentRetriever contentRetriever(EmbeddingStore embeddingStore, EmbeddingModel embeddingModel, Double minScore, Integer maxResults) {
        EmbeddingStoreContentRetriever.EmbeddingStoreContentRetrieverBuilder builder = EmbeddingStoreContentRetriever.builder();

        if (minScore != null) {
            builder.minScore(minScore);
        }
        if (maxResults != null) {
            builder.maxResults(maxResults);
        }
        if (embeddingStore != null) {
            builder.embeddingStore(embeddingStore);
        }
        if (embeddingModel != null) {
            builder.embeddingModel(embeddingModel);
        }
        return builder.build();
    }


// 返回一个ChatMemory对象，该对象的最大消息数为getChatMemoryMaxMessage()的返回值
    default ChatMemory chatMemory() {
        return chatMemory(getChatMemoryMaxMessage());
    }

    //构建ChatMemoryProvider会话对象
    default ChatMemoryProvider chatMemoryProvider() {
        throw new RuntimeException("请实现chatMemoryProvider方法");
    }

    //构建向量数据库对象 不需要每次启动都向量化只需要第一次启动向量化
    default EmbeddingStore store() {
        throw new RuntimeException("请实现store方法");
    }

    //构建向量数据库检索对象
    default ContentRetriever contentRetriever() {
        throw new RuntimeException("请实现contentRetriever方法");
    }

}
