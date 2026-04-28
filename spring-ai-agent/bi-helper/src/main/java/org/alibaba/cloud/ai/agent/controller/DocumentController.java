package org.alibaba.cloud.ai.agent.controller;

import jakarta.annotation.Resource;
import org.alibaba.cloud.ai.agent.service.DocumentService;
import org.alibaba.cloud.ai.common.domain.R;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/**
 *
 * @author myseital
 * @date 2026/4/16
 */
@RestController
@RequestMapping("/document")
public class DocumentController {

    @Resource
    private DocumentService documentService;

    @PostMapping("/upload")
    public R upload(MultipartFile file) {
        documentService.handleDocument(file);

        return R.ok();
    }
}
