package org.alibaba.cloud.ai.agent.service.base.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.alibaba.cloud.ai.agent.mapper.BbProductMapper;
import org.alibaba.cloud.ai.agent.model.BbProduct;
import org.alibaba.cloud.ai.agent.service.base.BbProductService;
import org.springframework.stereotype.Service;

@Service
public class BbProductServiceImpl extends ServiceImpl<BbProductMapper, BbProduct> implements BbProductService {

}
