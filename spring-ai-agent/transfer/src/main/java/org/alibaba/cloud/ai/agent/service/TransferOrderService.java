package org.alibaba.cloud.ai.agent.service;

import org.alibaba.cloud.ai.agent.domain.param.TransferOrderParam;

/**
 *
 * @author myseital
 * @date 2026/4/15
 */
public interface TransferOrderService {

    void create(TransferOrderParam param);
}
