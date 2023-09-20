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
 * 普通消息
 *
 * @author 麦
 * @date 2023/9/19 11:09
 */

@RestController
@Slf4j
@Api(tags = "普通消息")
public class OrdinaryMessageController {

    @Resource
    private RocketMQTemplate rocketMqTemplate;

    @GetMapping("/syncSend")
    @ApiOperation(value = "同步发送消息", notes = "同步发送是最常用的方式，是指消息发送方发出一条消息后，会在收到服务端同步响应之后才发下一条消息的通讯方式，可靠的同步传输被广泛应用于各种场景，如重要的通知消息、短消息通知等。")
    public void syncSend(){
        User user = new User();
        user.setId("luban");
        user.setName("小鲁班");

        Message<String> message = MessageBuilder.withPayload(JSON.toJSONString(user))
                .setHeader("KEYS", user.getId()).build();

        SendResult sendResult = rocketMqTemplate.syncSend("kingsTopic".concat(":shooter"), message);

        log.info(sendResult.toString());
    }

    @GetMapping("/asyncSend")
    @ApiOperation(value = "异步发送消息", notes = "异步发送是指发送方发出一条消息后，不等服务端返回响应，接着发送下一条消息的通讯方式。")
    public void asyncSend(){
        User user = new User();
        user.setId("sunshangxiang");
        user.setName("孙尚香");

        Message<String> message = MessageBuilder.withPayload(JSON.toJSONString(user)).setHeader("KEYS", user.getId()).build();

        rocketMqTemplate.asyncSend("kingsTopic".concat(":shooter"), message, new SendCallback() {
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

    @GetMapping("/sendOneWay")
    @ApiOperation(value = "单向模式发送消息", notes = "发送方只负责发送消息，不等待服务端返回响应且没有回调函数触发，即只发送请求不等待应答。此方式发送消息的过程耗时非常短，一般在微秒级别。适用于某些耗时非常短，但对可靠性要求并不高的场景，例如日志收集。")
    public void sendOneWay(){
        User user = new User();
        user.setId("jialuo");
        user.setName("伽罗");

        Message<String> message = MessageBuilder.withPayload(JSON.toJSONString(user))
                //设置消息KEYS,一般是数据的唯一ID,主要用于在仪表盘中方便搜索
                .setHeader("KEYS", user.getId())
                .build();

        //给消息打上射手的标签。主题+tag，中间用“:”分隔,主要是用于消息的过滤，比如说在消费的时候，只消费ESS标签下的消息
        String topic = "kingsTopic".concat(":shooter");
        rocketMqTemplate.sendOneWay(topic, message);
    }

}
