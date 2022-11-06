package org.seckill.service.impl;

import org.apache.commons.collections.MapUtils;
import org.seckill.dao.SeckillDao;
import org.seckill.dao.SuccessSeckilledDao;
import org.seckill.dao.cache.RedisDao;
import org.seckill.dto.Exposer;
import org.seckill.dto.SeckillExecution;
import org.seckill.entity.Seckill;
import org.seckill.entity.SuccessSeckilled;
import org.seckill.enums.SeckillStateEnum;
import org.seckill.exception.RepeatKillException;
import org.seckill.exception.SeckillCloseException;
import org.seckill.exception.SeckillException;
import org.seckill.service.SeckillService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.DigestUtils;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class SeckillServiceImpl implements SeckillService {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    private final String slat = "sahdhkey9217291uih*u(&(*sify(*&f(a*^(_$jkpu)";

    @Autowired
    private SeckillDao seckillDao;

    @Autowired
    private SuccessSeckilledDao successSeckilledDao;

    @Autowired
    private RedisDao redisDao;

    @Override
    public List<Seckill> getSeckillList() {
        return seckillDao.queryAll(0, 4);
    }

    @Override
    public Seckill queryById(long seckillId) {
        return seckillDao.queryById(seckillId);
    }

    @Override
    public Exposer exportSeckillUrl(long seckillId) {
        //优化点：缓存优化，一致性：建立在超时的情况下
        //访问redis
        Seckill seckill = redisDao.getSeckill(seckillId);
        if(seckill == null) {
            //访问数据库
            seckill = seckillDao.queryById(seckillId);
            if(seckill == null) {
                return new Exposer(false, seckillId);
            } else {
                //放入redis缓存
                redisDao.putSeckill(seckill);
            }
        }
        long start = seckill.getStartTime().getTime();
        long end = seckill.getEndTime().getTime();
        long now = System.currentTimeMillis();
        if(start > now || end < now) {
            return new Exposer(false,seckillId,now,start,end);
        }
        String md5 = getMD5(seckillId);
        return new Exposer(true, seckillId, md5);
    }

    private String getMD5(long seckillId) {
        String base = seckillId + "/" + slat;
        return DigestUtils.md5DigestAsHex(base.getBytes());
    }

    //秒杀逻辑，减库存，插入明细
    @Override
    @Transactional
    /**
     * 建议使用基于注解的声明式事务处理，好处：
         1、开发团队统一达成约定，明确标注事务方法的编程风格
         2、尽可能使事务控制的范围最小，执行的时间最短，尽量不要在事务中进行RPC/HTTP等网络请求，如果需要，则剥离到事务方法之外
         3、不是所有方法都需要事务，比如一次修改操作或者只读操作不需要事务控制
     */
    public SeckillExecution executeSeckill(long seckillId, long userPhone, String md5) throws SeckillException, RepeatKillException, SeckillCloseException {
        try {
            //判断请求的安全性，即MD5是否被篡改
            if(md5 == null || !md5.equals(getMD5(seckillId))) {
                throw new SeckillException("seckill data rewrite");
            }
            //减库存
            Date seckillTime = new Date();
            int updateCount = seckillDao.reduceNumber(seckillId, seckillTime);
            if(updateCount <= 0) {
                throw new SeckillCloseException("seckill is closed");
            } else {
                //插入成功秒杀的明细
                int insertCount = successSeckilledDao.insertSuccessSeckilled(seckillId, userPhone);
                if(insertCount <= 0) {
                    throw new RepeatKillException("seckill repeated");
                } else {
                    SuccessSeckilled successSeckilled = successSeckilledDao.queryByIdWithSeckill(seckillId, userPhone);
                    return new SeckillExecution(seckillId, SeckillStateEnum.SUCCESS, successSeckilled);
                }
            }
        } catch (SeckillCloseException e1) {
            throw e1;
        } catch (RepeatKillException e2) {
            throw e2;
        } catch (Exception e) {
            e.printStackTrace();
            logger.error(e.getMessage(), e);
            //将编译期异常转换为运行期异常，当抛出异常时，spring框架会对数据库操作进行回滚
            throw new SeckillException("seckill inner error: " + e.getMessage());
        }
    }

    @Override
    public SeckillExecution executeSeckillByProcedure(long seckillId, long userPhone, String md5) {
        //判断请求的安全性，即MD5是否被篡改
        if(md5 == null || !md5.equals(getMD5(seckillId))) {
            return new SeckillExecution(seckillId, SeckillStateEnum.DATA_REWRITE);
        }
        Date seckillTime = new Date();
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("seckillId", seckillId);
        params.put("userPhone", userPhone);
        params.put("killTime", seckillTime);
        params.put("result", null);
        seckillDao.executeSeckillByProcedure(params);
        try {
            int result = MapUtils.getIntValue(params, "result", -3);
            if(result == 1) {
                SuccessSeckilled successSeckilled = successSeckilledDao.queryByIdWithSeckill(seckillId, userPhone);
                return new SeckillExecution(seckillId, SeckillStateEnum.SUCCESS, successSeckilled);
            } else {
                return new SeckillExecution(seckillId, SeckillStateEnum.stateOf(result));
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return new SeckillExecution(seckillId, SeckillStateEnum.INNER_ERROR);
        }
    }
}
