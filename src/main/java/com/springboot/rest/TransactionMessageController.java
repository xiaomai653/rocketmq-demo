package com.springboot.rest;

import com.alibaba.fastjson.JSON;
import com.springboot.entity.User;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.producer.TransactionSendResult;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.apache.rocketmq.spring.support.RocketMQHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * 事务消息
 *
 * @author 麦
 * @date 2023/9/19 11:09
 */

@RestController
@Slf4j
@Api(tags = "事务消息")
public class TransactionMessageController {

    @Resource
    private RocketMQTemplate rocketMqTemplate;

    @GetMapping("/sendMessageInTransaction")
    public void sendMessageInTransaction(){
        User user = new User();
        user.setId("houyi");
        user.setName("后裔");

        Message<String> message = MessageBuilder.withPayload(JSON.toJSONString(user))
                .setHeader("KEYS", user.getId())
                //设置事务ID
                .setHeader(RocketMQHeaders.TRANSACTION_ID,"KEY_"+user.getId())
                .build();

        TransactionSendResult transactionSendResult = rocketMqTemplate.sendMessageInTransaction("kingsTopic".concat(":shooter"), message, null);
        log.info(transactionSendResult.toString());
    }

}
