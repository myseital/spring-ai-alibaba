package org.alibaba.cloud.ai.agent.domain.convert;

import org.alibaba.cloud.ai.agent.domain.param.TransferOrderItemParam;
import org.alibaba.cloud.ai.agent.domain.param.TransferOrderParam;
import org.alibaba.cloud.ai.agent.model.BbTransferOrder;
import org.alibaba.cloud.ai.agent.model.BbTransferOrderItem;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

/**
 *
 * @author myseital
 * @date 2026/4/15
 */
@Mapper
public interface TransferOrderConvert {
    TransferOrderConvert INSTANCE = Mappers.getMapper(TransferOrderConvert.class);

    BbTransferOrder paramToModel(TransferOrderParam param);

    BbTransferOrderItem itemParamToModel(TransferOrderItemParam param);

    List<BbTransferOrderItem> itemParamToModel(List<TransferOrderItemParam> params);
}
