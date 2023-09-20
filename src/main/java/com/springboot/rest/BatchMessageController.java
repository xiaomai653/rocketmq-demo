package com.springboot.rest;

import com.alibaba.fastjson.JSON;
import com.springboot.entity.User;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * 批量消息
 *
 * @author 麦
 * @date 2023/9/19 11:09
 */

@RestController
@Slf4j
@Api(tags = "批量消息")
public class BatchMessageController {

    @Resource
    private RocketMQTemplate rocketMqTemplate;

    @GetMapping("/syncSendBatchMessage")
    public void syncSendBatchMessage(){
        List<User> userList = new ArrayList<>();
        User user1 = new User();
        user1.setId("geya");
        user1.setName("戈娅");
        userList.add(user1);

        User user2 = new User();
        user2.setId("direnjie");
        user2.setName("狄仁杰");
        userList.add(user2);

        List<Message<String>> msgs = new ArrayList<>();
        for (User user : userList) {
            Message<String> message = MessageBuilder.withPayload(JSON.toJSONString(user))
                    .setHeader("KEYS", user.getId())
                    .build();
            msgs.add(message);
        }

        SendResult sendResult = rocketMqTemplate.syncSend("kingsTopic".concat(":shooter"), msgs);
        log.info(sendResult.toString());
    }
}
