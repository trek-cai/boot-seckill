package org.seckill.dao;

import org.apache.ibatis.annotations.Param;
import org.seckill.entity.Seckill;
import org.seckill.entity.SuccessSeckilled;

public interface SuccessSeckilledDao {

    /**
     * 插入秒杀成功明细
     * 其中successSeckill表的联合主键可用来过滤重复秒杀
     * @param seckillId
     * @param userPhone
     * @return
     */
   int insertSuccessSeckilled(@Param(value = "seckillId") long seckillId, @Param(value = "userPhone") long userPhone);

    /**
     * 通过秒杀商品id查询秒杀明细，带出商品详情
     * @param seckillId
     * @return
     */
   SuccessSeckilled queryByIdWithSeckill(@Param(value = "seckillId") long seckillId, @Param(value = "userPhone") long userPhone);

}
