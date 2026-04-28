package org.alibaba.cloud.ai.agent.service;

import org.springframework.web.multipart.MultipartFile;

/**
 *
 * @author myseital
 * @date 2026/4/16
 */
public interface DocumentService {
    void handleDocument(MultipartFile file);
}
