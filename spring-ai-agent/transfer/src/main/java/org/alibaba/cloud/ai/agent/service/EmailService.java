package org.alibaba.cloud.ai.agent.service;

import java.util.Map;

/**
 *
 * @author myseital
 * @date 2026/4/14
 */
public interface EmailService {

    void sendTemplateMail(String to, Map<String, Object> variables);
}
