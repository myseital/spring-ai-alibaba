package org.alibaba.cloud.ai.agent.service;

import java.io.File;
import java.util.Map;

/**
 *
 * @author myseital
 * @date 2026/4/14
 */
public interface EmailService {

    void sendMail(String to, String content);

    void sendMail(String to, File file);
}
