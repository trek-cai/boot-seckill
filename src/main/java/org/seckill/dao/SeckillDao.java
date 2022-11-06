package org.seckill.dao;

import org.apache.ibatis.annotations.Param;
import org.seckill.entity.Seckill;

import java.util.Date;
import java.util.List;
import java.util.Map;

public interface SeckillDao {

    /**
     * 减库存
     * @param seckillId
     * @return
     */
    int reduceNumber(@Param("seckillId") long seckillId, @Param("seckillTime") Date seckillTime);

    /**
     * 通过商品id查询商品详情
     * @param seckillId
     * @return
     */
    Seckill queryById(@Param("seckillId") long seckillId);

    /**
     * 根据偏移量和数量查询秒杀商品列表
     * @param offset
     * @param limit
     * @return
     */
    List<Seckill> queryAll(@Param(value = "offset") int offset, @Param(value = "limit") int limit);

    /**
     * 调用存储过程处理秒杀
     * @param param
     */
    void executeSeckillByProcedure(Map<String, Object> param);
}
