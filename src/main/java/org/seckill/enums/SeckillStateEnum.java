package org.seckill.enums;

public enum SeckillStateEnum {
    SUCCESS(1, "秒杀成功"),
    END(0, "秒杀结束"),
    REPEAT_KILL(-1, "重复秒杀"),
    DATA_REWRITE(-2, "数据篡改"),
    INNER_ERROR(-3, "系统异常");

    private int state;

    private String stateInfo;

    public int getState() {
        return state;
    }

    public String getStateInfo() {
        return stateInfo;
    }

    public static SeckillStateEnum stateOf(int index) {
        for(SeckillStateEnum seckillStateEnum : values()) {
            if(seckillStateEnum.getState() == index) {
                return seckillStateEnum;
            }
        }
        return null;
    }

    SeckillStateEnum(int state, String stateInfo) {
        this.state = state;
        this.stateInfo = stateInfo;
    }
}
