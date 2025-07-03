package com.actual.combat.ai.langchain4j.properties;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.EnumUtil;
import cn.hutool.core.util.StrUtil;
import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.document.DocumentParser;
import dev.langchain4j.data.document.loader.ClassPathDocumentLoader;
import dev.langchain4j.data.document.loader.FileSystemDocumentLoader;
import dev.langchain4j.data.document.loader.UrlDocumentLoader;
import dev.langchain4j.data.document.parser.TextDocumentParser;
import dev.langchain4j.data.document.parser.apache.pdfbox.ApachePdfBoxDocumentParser;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;


/**
 * @Author yan
 * @Date 2025/6/28 11:09:16
 * @Description
 */
@Slf4j
@Accessors(chain = true)
@Configuration
@NoArgsConstructor
@Data
@AllArgsConstructor

@ConfigurationProperties(prefix = LoaderDocumentProperties.LOADER_DOCUMENT)
public class LoaderDocumentProperties {
    public final static String LOADER_DOCUMENT = "config.langchain4j.prop.loader-document";
    /**
     * ==>resources/pdf_dir,pdf
     * config.langchain4j.prop.loader-document.class-paths=content,pdf_dir:pdf,....
     * config.langchain4j.prop.loader-document.file-paths=/path/to/pdf_dir,/path/to/pdf_dir:pdf,....
     * config.langchain4j.prop.loader-document.url-paths=https://xxxxx/file.md,https://xxxxx/file.pdf:pdf,....
     */
    String classPaths = StrUtil.EMPTY;
    String filePaths = StrUtil.EMPTY;
    String urlPaths = StrUtil.EMPTY;

    //@Getter
    @AllArgsConstructor
    public enum LoaderPathType {
        CLASS, FILE, URL
    }

    @Getter
    @AllArgsConstructor
    public enum LoaderDocumentType {
        DEFAULT(new TextDocumentParser()),
        PDF(new ApachePdfBoxDocumentParser());
        DocumentParser documentParser;
    }


    /**
     * @return
     */

    public Map<LoaderDocumentType, DocumentParser> fetchDocumentParserMap() {
        LinkedHashMap<LoaderDocumentType, DocumentParser> map = new LinkedHashMap<>();
        //LoaderDocumentType documentType = LoaderDocumentType.PDF;
        Map<String, LoaderDocumentType> loaderDocumentTypeMap = EnumUtil.getEnumMap(LoaderDocumentType.class);
        loaderDocumentTypeMap.entrySet().stream().forEach(entry -> {
            LoaderDocumentType documentType = entry.getValue();
            map.put(documentType, documentType.getDocumentParser());
        });
        return map;
    }

    /**
     * @return
     */
    public Map<LoaderPathType, List<String>> fetchLoaderPathTypeMap() {
        List<String> fetchClassPaths = fetchClassPaths();
        List<String> fetchFilePaths = fetchFilePaths();
        List<String> fetchUrlPaths = fetchUrlPaths();
        Map<LoaderPathType, List<String>> paths = new LinkedHashMap<>();

        paths.put(LoaderPathType.CLASS, fetchClassPaths);
        paths.put(LoaderPathType.FILE, fetchFilePaths);
        paths.put(LoaderPathType.URL, fetchUrlPaths);
        return paths;
    }

    public List<String> fetchClassPaths() {
        String paths = getClassPaths();
        List<String> split = StrUtil.isBlank(paths) ? null : StrUtil.split(paths, StrUtil.COMMA);
        return split;
    }

    public List<String> fetchFilePaths() {
        String paths = getFilePaths();
        List<String> split = StrUtil.isBlank(paths) ? null : StrUtil.split(paths, StrUtil.COMMA);
        return split;
    }

    public List<String> fetchUrlPaths() {
        String paths = getUrlPaths();
        List<String> split = StrUtil.isBlank(paths) ? null : StrUtil.split(paths, StrUtil.COMMA);
        return split;
    }

    public List<Document> loadDocuments() {
        Map<LoaderPathType, List<String>> loaderPathTypeListMap = fetchLoaderPathTypeMap();
        Map<LoaderDocumentType, DocumentParser> typeParserMap = fetchDocumentParserMap();

        List<Document> documents = CollUtil.newArrayList();
        loaderPathTypeListMap.entrySet().stream().forEach(entry -> {
            LoaderPathType loaderPathType = entry.getKey();
            List<String> valuePaths = entry.getValue();

            if (CollUtil.isNotEmpty(valuePaths)) {
                valuePaths.stream().forEach(pathDocument -> {
                    String[] split = pathDocument.split(":");
                    String path = split[0];

                    String document = split.length == 1 ? null : split[1].toUpperCase();

                    DocumentParser documentParser = document == null ? null : typeParserMap.get(LoaderDocumentType.valueOf(document));

                    List<Document> loadDocuments = loadDocuments(loaderPathType, path, documentParser);

                    if (CollUtil.isEmpty(loadDocuments)) {
                        log.error("[load] ==> type:{},path:{},文档加载失败 or 无文档 <==", loaderPathType,path);
                    } else {
                        documents.addAll(loadDocuments);
                    }
                });
            } else {

                log.warn("[load] ==> type:{},未配置文档加载路径@配置:{} <==", loaderPathType, LOADER_DOCUMENT + "." + loaderPathType.name().toLowerCase() + "-paths");
            }

        });
        return documents;
    }

    /**
     * @param pathType
     * @param path
     * @param documentParser
     * @return
     */
    public List<Document> loadDocuments(LoaderPathType pathType, String path, DocumentParser documentParser) {
        List<Document> loadDocuments = CollUtil.newArrayList();
        switch (pathType) {
            case FILE:
                if (documentParser == null) {
                    loadDocuments.addAll(FileSystemDocumentLoader.loadDocuments(path));
                } else {
                    loadDocuments.addAll(FileSystemDocumentLoader.loadDocuments(path, documentParser));
                }
                break;
            case URL:
                if (documentParser == null) {
                    LoaderDocumentType loaderDocumentType = LoaderDocumentType.DEFAULT;
                    loadDocuments.add(UrlDocumentLoader.load(path, loaderDocumentType.getDocumentParser()));
                } else {
                    loadDocuments.add(UrlDocumentLoader.load(path, documentParser));
                }
                break;
            case CLASS:
            default:
                if (documentParser == null) {
                    loadDocuments = ClassPathDocumentLoader.loadDocuments(path);
                } else {
                    loadDocuments = ClassPathDocumentLoader.loadDocuments(path, documentParser);
                }
                break;
        }

        log.debug("[load] ==>  type:{},path:{}  <==", pathType, path);
        return loadDocuments;
    }


}
