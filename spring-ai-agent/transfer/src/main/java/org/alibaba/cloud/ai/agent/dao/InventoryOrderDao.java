package org.alibaba.cloud.ai.agent.dao;

import org.alibaba.cloud.ai.agent.domain.vo.InventoryOrderVo;
import org.alibaba.cloud.ai.agent.domain.vo.SaleRecordVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface InventoryOrderDao {

    @Select("""
            SELECT p.id as productId, 
                   p.product_code, 
                   p.product_name,
                    YEAR(o.transfer_date) as year, 
                    QUARTER(o.transfer_date) as quarter,
                    o.source_warehouse_id, 
                    w.warehouse_name as sourceWarehouseName, 
                    w.warehouse_code as sourceWarehouseCode,
                    o.target_warehouse_id, 
                    ww.warehouse_name as targetWarehouseName, 
                    ww.warehouse_code as targetWarehouseCode,
                    SUM(i.transfer_quantity) as totalTransferQty
            from bb_transfer_order as o 
            join bb_transfer_order_item as i on o.id = i.transfer_order_id
            join bb_product as p on i.product_id = p.id
            join bb_warehouse as w on o.source_warehouse_id = w.id
            join bb_warehouse as ww on o.target_warehouse_id = ww.id
            where i.product_id = #{productId} and o.status = 3
            group by p.id, year, quarter, w.id, ww.id
            """)
    List<InventoryOrderVo> collectInventoryOrderDataByProductId(@Param("productId") Integer productId);
}
