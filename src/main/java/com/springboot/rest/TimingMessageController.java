package com.springboot.rest;

import com.alibaba.fastjson.JSON;
import com.springboot.entity.User;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.producer.SendCallback;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;


/**
 * 定时消息
 *
 * @author 麦
 * @date 2023/9/19 11:09
 */

@RestController
@Slf4j
@Api(tags = "定时消息")
public class TimingMessageController {

    @Resource
    private RocketMQTemplate rocketMqTemplate;

    @GetMapping("/syncSendDelayTime")
    @ApiOperation(value = "同步发送延迟消息")
    public void syncSendDelayTimeSeconds(){
        User user = new User();
        user.setId("baiqi");
        user.setName("白起");



        Message<String> message = MessageBuilder.withPayload(JSON.toJSONString(user)).setHeader("KEYS", user.getId()).build();

        //delayLevel = "1s 5s 10s 30s 1m 2m 3m 4m 5m 6m 7m 8m 9m 10m 20m 30m 1h 2h";
        //10毫秒后才能消费这条消息
        SendResult sendResult = rocketMqTemplate.syncSend("kingsTopic".concat(":tank"), message, 10000, 2);
        log.info(sendResult.toString());
    }


    @GetMapping("/asyncSendDelayTime")
    @ApiOperation(value = "异步发送延迟消息")
    public void syncSendDeliverTimeMills(){
        User user = new User();
        user.setId("liubang");
        user.setName("刘邦");

        Message<String> message = MessageBuilder.withPayload(JSON.toJSONString(user)).setHeader("KEYS", user.getId()).build();

        rocketMqTemplate.asyncSend("kingsTopic".concat(":tank"), message, new SendCallback() {
            @Override
            public void onSuccess(SendResult sendResult) {
                log.info(sendResult.toString());
            }

            @Override
            public void onException(Throwable e) {
                log.info(e.getMessage());
            }
        }, 10000, 3);
    }

}
