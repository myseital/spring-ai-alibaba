package org.alibaba.cloud.ai.agent.service.impl;

import cn.hutool.core.util.IdUtil;
import jakarta.annotation.Resource;
import org.alibaba.cloud.ai.agent.domain.convert.TransferOrderConvert;
import org.alibaba.cloud.ai.agent.domain.param.TransferOrderParam;
import org.alibaba.cloud.ai.agent.model.BbTransferOrder;
import org.alibaba.cloud.ai.agent.model.BbTransferOrderItem;
import org.alibaba.cloud.ai.agent.service.TransferOrderService;
import org.alibaba.cloud.ai.agent.service.base.BbTransferOrderItemService;
import org.alibaba.cloud.ai.agent.service.base.BbTransferOrderService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 *
 * @author myseital
 * @date 2026/4/15
 */
@Service
public class TransferOrderServiceImpl implements TransferOrderService {

    @Resource
    private BbTransferOrderService bbTransferOrderService;

    @Resource
    private BbTransferOrderItemService bbTransferOrderItemService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void create(TransferOrderParam param) {
        param.setOrderNo(IdUtil.simpleUUID());
        BbTransferOrder bbTransferOrder = TransferOrderConvert.INSTANCE.paramToModel(param);
        List<BbTransferOrderItem> bbTransferOrderItems = TransferOrderConvert.INSTANCE.itemParamToModel(param.getItems());
        bbTransferOrderService.save(bbTransferOrder);
        bbTransferOrderItems.forEach(e -> {
            e.setTransferOrderId(bbTransferOrder.getId());
        });
        bbTransferOrderItemService.saveBatch(bbTransferOrderItems);
    }
}
