package org.seckill.service;

import org.seckill.dto.Exposer;
import org.seckill.dto.SeckillExecution;
import org.seckill.entity.Seckill;
import org.seckill.exception.RepeatKillException;
import org.seckill.exception.SeckillCloseException;
import org.seckill.exception.SeckillException;

import java.util.List;

//秒杀接口
public interface SeckillService {

    /**
     * 获取秒杀列表
     * @return
     */
    List<Seckill> getSeckillList();

    /**
     * 根据id获取秒杀商品的详情
     * @param seckillId
     * @return
     */
    Seckill queryById(long seckillId);

    /**
     * 暴露秒杀的接口地址
     * 如果未到秒杀开启时间显示系统时间或倒计时
     * @param seckillId
     */
    Exposer exportSeckillUrl(long seckillId);

    /**
     * 提交秒杀请求
     * @param seckillId
     * @param userPhone
     * @param md5   验证秒杀url是否遭到篡改
     */
    SeckillExecution executeSeckill(long seckillId, long userPhone, String md5)
        throws SeckillException, RepeatKillException, SeckillCloseException;

    /**
     * 提交秒杀请求
     * @param seckillId
     * @param userPhone
     * @param md5   验证秒杀url是否遭到篡改
     */
    SeckillExecution executeSeckillByProcedure(long seckillId, long userPhone, String md5);
}
