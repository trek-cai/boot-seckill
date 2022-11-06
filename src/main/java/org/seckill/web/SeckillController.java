package org.seckill.web;

import org.seckill.dto.Exposer;
import org.seckill.dto.SeckillExecution;
import org.seckill.dto.SeckillResult;
import org.seckill.entity.Seckill;
import org.seckill.enums.SeckillStateEnum;
import org.seckill.exception.RepeatKillException;
import org.seckill.exception.SeckillCloseException;
import org.seckill.exception.SeckillException;
import org.seckill.service.SeckillService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

@Controller
@RequestMapping("/seckill")
public class SeckillController {

    private static final Logger logger = LoggerFactory.getLogger(SeckillController.class);

    @Autowired
    private SeckillService seckillService;

    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public String list(Model model) {
        List<Seckill> seckills = seckillService.getSeckillList();
        model.addAttribute("list", seckills);
        return "list";
    }

    @RequestMapping(value = "/{seckillId}/detail", method = RequestMethod.GET)
    public String detail(@PathVariable("seckillId") Long seckillId, Model model) {
        if(seckillId == null) {
            return "redirect:/seckill/list";
        }
        Seckill seckill = seckillService.queryById(seckillId);
        if(seckill == null) {
            return "forward:/seckill/list";
        }
        model.addAttribute("seckill",seckill);
        return "detail";
    }

    @RequestMapping(value = "/{seckillId}/exposer", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
    @ResponseBody
    public SeckillResult<Exposer> exposer(@PathVariable("seckillId") Long seckillId) {
        SeckillResult<Exposer> result;
        try {
            Exposer exposer = seckillService.exportSeckillUrl(seckillId);
            result = new SeckillResult<Exposer>(true, exposer);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            e.printStackTrace();
            result = new SeckillResult<Exposer>(false, e.getMessage());
        }
        return result;
    }

    @RequestMapping(value = "/{seckillId}/{md5}/execution", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
    @ResponseBody
    public SeckillResult<SeckillExecution> execute(@PathVariable("seckillId") Long seckillId,
                                                   @PathVariable("md5") String md5,
                                                   @CookieValue("killPhone") Long phone) {
        if(phone == null) {
            return new SeckillResult<SeckillExecution>(false, "未注册");
        }
        SeckillExecution execution;
        try {
            //成功返回
            execution = seckillService.executeSeckillByProcedure(seckillId, phone, md5);
        } catch (RepeatKillException e) {
            logger.error(e.getMessage(), e);
            execution = new SeckillExecution(seckillId, SeckillStateEnum.REPEAT_KILL);
        } catch (SeckillCloseException e) {
            logger.error(e.getMessage(), e);
            execution = new SeckillExecution(seckillId, SeckillStateEnum.END);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            execution = new SeckillExecution(seckillId, SeckillStateEnum.INNER_ERROR);
        }
        //失败返回
        return new SeckillResult<SeckillExecution>(true, execution);
    }

    @RequestMapping(value = "/time/now", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
    @ResponseBody
    public SeckillResult<Long> now() {
        Date now = new Date();
        return new SeckillResult<Long>(true, now.getTime());
    }
}
