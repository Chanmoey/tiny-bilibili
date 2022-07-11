package com.moon.tinybilibili.service;

import com.alibaba.fastjson.JSON;
import com.moon.tinybilibili.dao.UserMomentsDao;
import com.moon.tinybilibili.domain.UserMoment;
import com.moon.tinybilibili.domain.constant.UserMomentsConstant;
import com.moon.tinybilibili.service.utils.RocketMQUtil;
import org.apache.rocketmq.client.exception.MQBrokerException;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.remoting.exception.RemotingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.List;

/**
 * @author Chanmoey
 * @date 2022年07月10日
 */
@Service
public class UserMomentsService {

    @Autowired
    private UserMomentsDao userMomentsDao;

    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    public void addUserMoments(UserMoment userMoment) throws MQBrokerException, RemotingException, InterruptedException, MQClientException {
        userMoment.setCreateTime(new Date());
        userMomentsDao.addUserMoments(userMoment);

        // 向MQ发送消息
        DefaultMQProducer producer = (DefaultMQProducer) applicationContext.getBean("momentsProducer");
        Message msg = new Message(UserMomentsConstant.TOPIC_MOMENTS,
                JSON.toJSONString(userMoment).getBytes(StandardCharsets.UTF_8));
        RocketMQUtil.syncSendMsg(producer, msg);
    }

    public List<UserMoment> getUserSubscribedMoments(Long userId) {
        String key = UserMomentsConstant.USER_SUBSCRIBED_MOMENT_PRE_KEY + userId;
        String listStr = redisTemplate.opsForValue().get(key);
        return JSON.parseArray(listStr, UserMoment.class);
    }
}
