package org.seckill.dto;

import org.seckill.entity.SuccessSeckilled;
import org.seckill.enums.SeckillStateEnum;

/**
 * 提交秒杀后返回结果
 */
public class SeckillExecution {

    //秒杀id
    private long seckillId;

    //秒杀结果状态
    private int state;

    //结果状态描述
    private String stateInfo;

    //秒杀成功结果明细
    private SuccessSeckilled successSeckilled;

    public SeckillExecution(long seckillId, SeckillStateEnum seckillStateEnum, SuccessSeckilled successSeckilled) {
        this.seckillId = seckillId;
        this.state = seckillStateEnum.getState();
        this.stateInfo = seckillStateEnum.getStateInfo();
        this.successSeckilled = successSeckilled;
    }

    //秒杀失败构造函数
    public SeckillExecution(long seckillId, SeckillStateEnum seckillStateEnum) {
        this.seckillId = seckillId;
        this.state = seckillStateEnum.getState();
        this.stateInfo = seckillStateEnum.getStateInfo();
    }

    @Override
    public String toString() {
        return "SeckillExecution{" +
                "seckillId=" + seckillId +
                ", state=" + state +
                ", stateInfo='" + stateInfo + '\'' +
                ", successSeckilled=" + successSeckilled +
                '}';
    }

    public long getSeckillId() {
        return seckillId;
    }

    public void setSeckillId(long seckillId) {
        this.seckillId = seckillId;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public String getStateInfo() {
        return stateInfo;
    }

    public void setStateInfo(String stateInfo) {
        this.stateInfo = stateInfo;
    }

    public SuccessSeckilled getSuccessSeckilled() {
        return successSeckilled;
    }

    public void setSuccessSeckilled(SuccessSeckilled successSeckilled) {
        this.successSeckilled = successSeckilled;
    }
}
