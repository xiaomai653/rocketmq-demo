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
 * 顺序消息
 *
 * @author 麦
 * @date 2023/9/19 11:09
 */

@RestController
@Slf4j
@Api(tags = "顺序消息")
public class OrderlyMessageController {

    @Resource
    private RocketMQTemplate rocketMqTemplate;


    @GetMapping("/syncSendOrderly")
    @ApiOperation(value = "同步发送消息", notes = "和普通消息区别于顺序")
    public void syncSendOrderly(){
        User user = new User();
        user.setId("niumo");
        user.setName("牛魔");

        Message<String> message = MessageBuilder.withPayload(JSON.toJSONString(user))
                .setHeader("KEYS", user.getId()).build();

        SendResult sendResult = rocketMqTemplate.syncSendOrderly("kingsTopic".concat(":tank"), message, user.getId());
        log.info(sendResult.toString());
    }


    @GetMapping("/asyncSendOrderly")
    @ApiOperation(value = "异步发送消息", notes = "和普通消息区别于顺序")
    public void asyncSendOrderly(){
        User user = new User();
        user.setId("xiangyu");
        user.setName("项羽");

        Message<String> message = MessageBuilder.withPayload(JSON.toJSONString(user))
                .setHeader("KEYS", user.getId()).build();

        rocketMqTemplate.asyncSendOrderly("kingsTopic".concat(":tank"), message, user.getId(), new SendCallback() {
            @Override
            public void onSuccess(SendResult sendResult) {
                log.info(sendResult.toString());
            }

            @Override
            public void onException(Throwable e) {
                log.info(e.getMessage());
            }
        });
    }

    @GetMapping("/sendOneWayOrderly")
    @ApiOperation(value = "单向模式发送消息", notes = "和普通消息区别于顺序")
    public void sendOneWayOrderly(){
        User user = new User();
        user.setId("zhangfei");
        user.setName("张飞");

        Message<String> message = MessageBuilder.withPayload(JSON.toJSONString(user))
                .setHeader("KEYS", user.getId()).build();

        //顺序消息比普通消息多一个参数，第三个参数只要唯一就行，比如ID
        rocketMqTemplate.sendOneWayOrderly("kingsTopic".concat(":tank"), message, user.getId());
    }

}
