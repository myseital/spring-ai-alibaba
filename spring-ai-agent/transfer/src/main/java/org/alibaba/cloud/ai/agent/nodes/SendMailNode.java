package org.alibaba.cloud.ai.agent.nodes;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.alibaba.cloud.ai.graph.OverAllState;
import com.alibaba.cloud.ai.graph.action.NodeAction;
import lombok.extern.slf4j.Slf4j;
import org.alibaba.cloud.ai.agent.service.EmailService;

import java.util.Map;

/**
 *
 * @author myseital
 * @date 2026/4/14
 */
@Slf4j
public class SendMailNode implements NodeAction {

    private final EmailService emailService;

    public SendMailNode(EmailService emailService) {
        this.emailService = emailService;
    }

    @Override
    public Map<String, Object> apply(OverAllState state) throws Exception {
        String inventoryTransferJsonStr = state.value("inventoryTransferJsonStr", "");
        if (StrUtil.isBlank(inventoryTransferJsonStr)) {
            return Map.of();
        }
        JSONObject jsonObject = JSONUtil.parseObj(inventoryTransferJsonStr);
        String comment = jsonObject.getStr("comment");
        String threadId = state.value("threadId", "");
        String adoptLink = "http://localhost:9001/saleProduct/approval?approval=true&threadId=" + threadId;
        String rejectLink = "http://localhost:9001/saleProduct/approval?approval=false&threadId=" + threadId;
        log.info("adoptLink: {}, rejectLink: {}", adoptLink, rejectLink);
        emailService.sendTemplateMail("myseital@163.com", Map.of("inventoryTransferSaveParamStr", comment,
                "adoptLink", adoptLink, "rejectLink", rejectLink));

        return Map.of();
    }
}
