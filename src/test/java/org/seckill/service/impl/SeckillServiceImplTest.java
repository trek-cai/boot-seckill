package org.seckill.service.impl;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.seckill.dto.Exposer;
import org.seckill.dto.SeckillExecution;
import org.seckill.entity.Seckill;
import org.seckill.enums.SeckillStateEnum;
import org.seckill.exception.RepeatKillException;
import org.seckill.exception.SeckillCloseException;
import org.seckill.exception.SeckillException;
import org.seckill.service.SeckillService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;

import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({"classpath:spring/spring-dao.xml","classpath:spring/spring-service.xml"})
public class SeckillServiceImplTest {

    private static final Logger logger = LoggerFactory.getLogger(SeckillServiceImplTest.class);

    @Autowired
    private SeckillService seckillService;

    @Test
    public void getSeckillList() throws Exception {
        List<Seckill> seckills = seckillService.getSeckillList();
        logger.info("seckills={}", seckills);
    }

    @Test
    public void queryById() throws Exception {
        long id = 1000L;
        Seckill seckill = seckillService.queryById(id);
        logger.info("seckill={}", seckill);
    }

    @Test
    public void tesSeckillLogic() throws Exception {
        try {
            long id = 1000L;
            Exposer exposer = seckillService.exportSeckillUrl(id);
            logger.info("exposer={}", exposer);

            if(exposer.isExposed()) {
                long userPhone = 13543982783L;
                String md5 = exposer.getMd5();
                SeckillExecution execution = seckillService.executeSeckill(id, userPhone, md5);
                logger.info("execution={}", execution);
            } else {
                //秒杀未开启
                logger.warn("exposer={}", exposer);
            }
        } catch (RepeatKillException e) {
            logger.error(e.getMessage());
        } catch (SeckillCloseException e) {
            logger.error(e.getMessage());
        }
        //Exposer{exposed=true, seckillId=1000, md5='ab5ddd1cc462792acc694a7c1729f6ea', now=0, start=0, end=0}
    }

    @Test
    public void executeSeckill() throws Exception {
        try {
            long id = 1000L;
            long userPhone = 13543982783L;
            String md5 = "ab5ddd1cc462792acc694a7c1729f6ea";
            SeckillExecution execution = seckillService.executeSeckill(id, userPhone, md5);
            logger.info("execution={}", execution);
        } catch (RepeatKillException e) {
           logger.error(e.getMessage());
        } catch (SeckillCloseException e) {
            logger.error(e.getMessage());
        }
    }

    @Test
    public void executeSeckillByProcedure() throws Exception {
        long id = 1000L;
        long userPhone = 13543382783L;
        Exposer exposer = seckillService.exportSeckillUrl(id);
        String md5 = exposer.getMd5();
        if(exposer.isExposed()) {
            SeckillExecution execution = seckillService.executeSeckillByProcedure(id, userPhone, md5);
            System.out.println(execution);
        } else {
            SeckillExecution execution = new SeckillExecution(id, SeckillStateEnum.END);
            System.out.println(execution);
        }
    }

}