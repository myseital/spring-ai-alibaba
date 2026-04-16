package org.alibaba.cloud.ai.agent.dao;

import org.alibaba.cloud.ai.agent.domain.vo.SaleRecordVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface SaleRecordDao {

    @Select("""
            SELECT p.id as productId, 
                p.product_code, 
                p.product_name,
                   YEAR(s.sale_date) as year, 
                   QUARTER(s.sale_date) as quarter,
                   w.id as warehouseId, 
                   w.warehouse_name, 
                   w.warehouse_code,
                   SUM(s.quantity) as totalSaleQty
                from bb_sales_record as s 
                join bb_product as p on s.product_id = p.id
                join bb_warehouse as w on s.warehouse_id = w.id
                where s.product_id = #{productId}
                group by p.id, year, quarter, w.id
            """)
    List<SaleRecordVo> collectSaleRecordDataByProductId(@Param("productId") Integer productId);
}
