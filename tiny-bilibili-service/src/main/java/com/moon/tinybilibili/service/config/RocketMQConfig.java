package com.moon.tinybilibili.service.config;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.moon.tinybilibili.domain.UserFollowing;
import com.moon.tinybilibili.domain.UserMoment;
import com.moon.tinybilibili.domain.constant.UserMomentsConstant;
import com.moon.tinybilibili.service.UserFollowingService;
import com.moon.tinybilibili.service.websocket.WebSocketService;
import com.mysql.cj.util.StringUtils;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.common.message.MessageExt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Chanmoey
 * @date 2022年07月10日
 */
@Configuration
public class RocketMQConfig {

    @Value("${rocketmq.name.server.address}")
    private String nameServerAddr;

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @Autowired
    private UserFollowingService userFollowingService;

    @Bean("momentsProducer")
    public DefaultMQProducer momentsProducer() throws MQClientException {
        DefaultMQProducer producer = new DefaultMQProducer(UserMomentsConstant.GROUP_MOMENTS);
        producer.setNamesrvAddr(nameServerAddr);
        producer.start();
        return producer;
    }

    @Bean("momentsConsumer")
    public DefaultMQPushConsumer momentsConsumer() throws MQClientException {
        DefaultMQPushConsumer consumer = new DefaultMQPushConsumer(UserMomentsConstant.GROUP_MOMENTS);
        consumer.setNamesrvAddr(nameServerAddr);
        consumer.subscribe(UserMomentsConstant.TOPIC_MOMENTS, "*");
        consumer.registerMessageListener((MessageListenerConcurrently) (list, consumeConcurrentlyContext) -> {
            MessageExt msg = list.get(0);
            if (msg == null) {
                return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
            }
            // 获取消息实体对象
            String bodyStr = new String(msg.getBody());
            UserMoment userMoment = JSON.toJavaObject(JSON.parseObject(bodyStr), UserMoment.class);

            // 查询动态发布者的粉丝
            Long userId = userMoment.getUserId();
            List<UserFollowing> fanList = userFollowingService.getUserFans(userId);
            // 将动态通知发送到Redis
            for (UserFollowing fan : fanList) {
                String key = UserMomentsConstant.USER_SUBSCRIBED_MOMENT_PRE_KEY + fan.getUserId();
                String subscribedListStr = redisTemplate.opsForValue().get(key);
                List<UserMoment> subscribedList;
                if (StringUtils.isNullOrEmpty(subscribedListStr)) {
                    subscribedList = new ArrayList<>();
                } else {
                    subscribedList = JSON.parseArray(subscribedListStr, UserMoment.class);
                }
                subscribedList.add(userMoment);
                redisTemplate.opsForValue().set(key, JSON.toJSONString(subscribedList));
            }

            return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
        });
        consumer.start();
        return consumer;
    }

    @Bean("danmusProducer")
    public DefaultMQProducer danmusProducer() throws MQClientException {
        DefaultMQProducer producer = new DefaultMQProducer(UserMomentsConstant.GROUP_DANMUS);
        producer.setNamesrvAddr(nameServerAddr);
        producer.start();
        return producer;
    }

    @Bean("danmusConsumer")
    public DefaultMQPushConsumer danmusConsumer() throws MQClientException {
        DefaultMQPushConsumer consumer = new DefaultMQPushConsumer(UserMomentsConstant.GROUP_DANMUS);
        consumer.setNamesrvAddr(nameServerAddr);
        consumer.subscribe(UserMomentsConstant.TOPIC_DANMUS, "*");
        consumer.registerMessageListener((MessageListenerConcurrently) (list, consumeConcurrentlyContext) -> {
            MessageExt msg = list.get(0);
            if (msg == null) {
                return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
            }
            // 获取消息实体对象
            String bodyStr = new String(msg.getBody());
            JSONObject jsonObject = JSONObject.parseObject(bodyStr);
            String sessionId = jsonObject.getString("sessionId");
            String message = jsonObject.getString("message");
            WebSocketService webSocketService = WebSocketService.WEBSOCKET_MAP.get(sessionId);
            if (webSocketService.getSession().isOpen()) {
                try {
                    webSocketService.sendMessage(message);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            // 标记该消息已经被成功消费
            return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;

        });
        consumer.start();
        return consumer;
    }
}
