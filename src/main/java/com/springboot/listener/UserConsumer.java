package com.springboot.listener;

import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.stereotype.Component;

/**
 * mq消费者
 *
 * @author 麦
 * @date 2023/9/19 16:51
 */
@Component
@Slf4j
// selectorExpression 是 tag 分类，默认*是代表topic下的全部tag
//@RocketMQMessageListener(topic = "kingsTopic", consumerGroup = "hero",selectorExpression = "shooter")
@RocketMQMessageListener(topic = "kingsTopic", consumerGroup = "hero", selectorExpression = "*")

public class UserConsumer implements RocketMQListener<String> {

    @Override
    public void onMessage(String message) {
        log.info("mq收到的消息：{}", message);
    }
}