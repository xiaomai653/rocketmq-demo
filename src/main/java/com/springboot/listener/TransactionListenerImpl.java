package com.springboot.listener;

import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.annotation.RocketMQTransactionListener;
import org.apache.rocketmq.spring.core.RocketMQLocalTransactionListener;
import org.apache.rocketmq.spring.core.RocketMQLocalTransactionState;
import org.apache.rocketmq.spring.support.RocketMQHeaders;
import org.springframework.messaging.Message;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 事务消息的监听方法
 *
 * @author 麦
 * @date 2023/9/19 15:53
 */

@RocketMQTransactionListener
@Slf4j
public class TransactionListenerImpl implements RocketMQLocalTransactionListener {
    private final AtomicInteger transactionIndex = new AtomicInteger(0);

    private final ConcurrentHashMap<String, Integer> localTrans = new ConcurrentHashMap<>();

    /**
     *执行事务
     */
    @Override
    public RocketMQLocalTransactionState executeLocalTransaction(Message msg, Object arg) {
        //事务ID
        String transId = (String) msg.getHeaders().get(RocketMQHeaders.TRANSACTION_ID);

        int value = transactionIndex.getAndIncrement();
        int status = value % 3;
        assert transId != null;
        localTrans.put(transId, status);

        if (status == 0) {
            log.info("success");
            //成功，提交事务
            return RocketMQLocalTransactionState.COMMIT;
        }

        if (status == 1) {
            log.info("failure");
            //失败，回滚事务
            return RocketMQLocalTransactionState.ROLLBACK;
        }

        log.info("unknown");
        //中间状态
        return RocketMQLocalTransactionState.UNKNOWN;
    }

    /**
     *检查事务状态
     */
    @Override
    public RocketMQLocalTransactionState checkLocalTransaction(Message msg) {
        String transId = (String) msg.getHeaders().get(RocketMQHeaders.TRANSACTION_ID);
        RocketMQLocalTransactionState retState;
        Integer status = localTrans.get(transId);
        if (null != status) {
            switch (status) {
                case 0:
                    retState = RocketMQLocalTransactionState.COMMIT;
                    break;
                case 1:
                    retState = RocketMQLocalTransactionState.ROLLBACK;
                    break;
                case 2:
                    retState = RocketMQLocalTransactionState.UNKNOWN;
                    break;

                default: break;
            }
        }
        log.info("msgTransactionId:{},TransactionState:{},status:{}",transId,retState,status);
        return retState;
    }
}