package org.alibaba.cloud.ai.agent.service.base.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.alibaba.cloud.ai.agent.mapper.BbInventoryMapper;
import org.alibaba.cloud.ai.agent.model.BbInventory;
import org.alibaba.cloud.ai.agent.service.base.BbInventoryService;
import org.springframework.stereotype.Service;

@Service
public class BbInventoryServiceImpl extends ServiceImpl<BbInventoryMapper, BbInventory> implements BbInventoryService {

}
