package org.alibaba.cloud.ai.agent.nodes;

import com.alibaba.cloud.ai.graph.OverAllState;
import com.alibaba.cloud.ai.graph.action.NodeAction;
import lombok.extern.slf4j.Slf4j;
import org.alibaba.cloud.ai.agent.service.EmailService;

import java.io.File;
import java.util.Map;
import java.util.Optional;

/**
 *
 * @author myseital
 * @date 2026/4/22
 */
@Slf4j
public class SendEmailNode implements NodeAction {

    private final EmailService emailService;

    public SendEmailNode(EmailService emailService) {
        this.emailService = emailService;
    }

    @Override
    public Map<String, Object> apply(OverAllState state) throws Exception {
        Optional<Object> excelFile = state.value("excelFile");
        if (excelFile.isEmpty()) {
            emailService.sendMail("myseital@163.com", "查询数据不存在");
        } else {
            emailService.sendMail("myseital@163.com", (File) excelFile.get());
        }

        return Map.of();
    }
}
