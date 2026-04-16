package org.alibaba.cloud.ai.agent.service.impl;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.alibaba.cloud.ai.agent.dao.SaleRecordDao;
import org.alibaba.cloud.ai.agent.domain.vo.SaleRecordVo;
import org.alibaba.cloud.ai.agent.service.SaleRecordService;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class SaleRecordServiceImpl implements SaleRecordService {

    @Resource
    private SaleRecordDao saleRecordDao;

    @Override
    public List<SaleRecordVo> collectSaleRecordDataByProductId(Integer productId) {
        return saleRecordDao.collectSaleRecordDataByProductId(productId);
    }
}
