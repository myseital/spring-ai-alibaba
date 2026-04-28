package org.alibaba.cloud.ai.agent.service.impl;

import jakarta.annotation.Resource;
import org.alibaba.cloud.ai.agent.service.DocumentService;
import org.alibaba.cloud.ai.agent.splitter.HeadingTextSplitter;
import org.springframework.ai.document.Document;
import org.springframework.ai.reader.tika.TikaDocumentReader;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 *
 * @author myseital
 * @date 2026/4/16
 */
@Service
public class DocumentServiceImpl implements DocumentService {

    @Resource
    private VectorStore vectorStore;

    @Override
    public void handleDocument(MultipartFile file) {
        TikaDocumentReader documentReader = new TikaDocumentReader(file.getResource());
        List<Document> documents = documentReader.read();
        HeadingTextSplitter headingTextSplitter = new HeadingTextSplitter();
        List<Document> split = headingTextSplitter.split(documents);
        vectorStore.add(split);
    }
}
