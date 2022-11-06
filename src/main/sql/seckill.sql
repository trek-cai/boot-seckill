# r_result输出所代表的意义
# SUCCESS(1, "秒杀成功"),
# END(0, "秒杀结束"),
# REPEAT_KILL(-1, "重复秒杀"),
# DATA_REWRITE(-2, "数据篡改"),
# INNER_ERROR(-3, "系统异常");

# 存储过程中，row_count()函数用来返回上一条sql（delete,insert,update）影响的行数。
# 根据row_count()返回值，可以进行接下来的流程判断：
# 0：未修改数据；
# >0: 表示修改的行数；
# <0: 表示SQL错误或未执行修改SQL

DELIMITER $$  -- 将;转换为$$
CREATE PROCEDURE `sekill`.`execute_seckill` (IN v_seckill_id BIGINT, IN v_user_phone BIGINT, IN v_kill_time TIMESTAMP,
  out r_result INT)
  BEGIN
    DECLARE result_count INT DEFAULT 0;
    START TRANSACTION;
      INSERT IGNORE INTO success_seckilled (seckill_id, user_phone) VALUES
        (v_seckill_id, v_user_phone);
      SELECT row_count() INTO result_count;
      IF(result_count = 0) THEN
        ROLLBACK;
        SET r_result = -1;
      ELSEIF(result_count < 0) THEN
        ROLLBACK;
        SET r_result = -3;
      ELSE  -- 插入成功，执行更新操作
        UPDATE seckill
        SET number = number - 1
        WHERE seckill_id = v_seckill_id
        AND start_time <= v_kill_time
        AND end_time >= v_kill_time
        AND number > 0;

        SELECT row_count() INTO result_count;
        IF(result_count = 0) THEN
          ROLLBACK;
          SET r_result = 0;
        ELSEIF(result_count < 0) THEN
          ROLLBACK;
          SET r_result = -3;
        ELSE
          COMMIT;
          SET r_result = 1;
        END IF;
      END IF;
    COMMIT;
  END $$

DELIMITER ;

SET @r_result = -3;
CALL execute_seckill(1001, 13543982783, now(), @r_result);
SELECT @r_result;

# 存储过程:
# 1.存储过程优化:事务行级锁持有的时间
# 2.不要过度依赖存储过程
# 3.简单的逻辑依赖存储过程
# 4.QPS:一个秒杀6000/qps

# show create procedure procedure_name来查看查询过程的结构\G
