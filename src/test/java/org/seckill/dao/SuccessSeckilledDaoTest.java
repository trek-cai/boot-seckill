package org.seckill.dao;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.seckill.entity.SuccessSeckilled;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.annotation.Resource;

import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({"classpath:spring/spring-dao.xml"})
public class SuccessSeckilledDaoTest {

    @Resource
    private SuccessSeckilledDao successSeckilledDao;

    @Test
    public void insertSuccessSeckilled() throws Exception {
        long seckillId = 1001L;
        long userPhone = 13543982783L;
        int insert = successSeckilledDao.insertSuccessSeckilled(seckillId, userPhone);
        System.out.println("insert count = " + insert);
    }

    @Test
    public void queryByIdWithSeckill() throws Exception {
        long seckillId = 1001L;
        long userPhone = 13543982783L;
        SuccessSeckilled successSeckilled = successSeckilledDao.queryByIdWithSeckill(seckillId, userPhone);
        System.out.println(successSeckilled);
        System.out.println(successSeckilled.getSeckill());
    }

}