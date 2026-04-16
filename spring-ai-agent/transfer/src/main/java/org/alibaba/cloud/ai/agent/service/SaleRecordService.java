package org.alibaba.cloud.ai.agent.service;

import org.alibaba.cloud.ai.agent.domain.vo.SaleRecordVo;

import java.util.List;

public interface SaleRecordService {
    List<SaleRecordVo> collectSaleRecordDataByProductId(Integer productId);
}