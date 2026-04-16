package org.alibaba.cloud.ai.agent.domain.convert;

import org.alibaba.cloud.ai.agent.domain.param.ProductSaleParam;
import org.alibaba.cloud.ai.agent.model.BbSalesRecord;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

/**
 *
 * @author myseital
 * @date 2026/4/16
 */
@Mapper
public interface SalesRecordConvert {

    SalesRecordConvert INSTANCE = Mappers.getMapper(SalesRecordConvert.class);

    BbSalesRecord paramToModel(ProductSaleParam param);
}
