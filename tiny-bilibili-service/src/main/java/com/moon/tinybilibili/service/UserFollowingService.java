package com.moon.tinybilibili.service;

import com.moon.tinybilibili.dao.UserFollowingDao;
import com.moon.tinybilibili.domain.FollowingGroup;
import com.moon.tinybilibili.domain.User;
import com.moon.tinybilibili.domain.UserFollowing;
import com.moon.tinybilibili.domain.UserInfo;
import com.moon.tinybilibili.domain.constant.UserConstant;
import com.moon.tinybilibili.domain.exception.ConditionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author Chanmoey
 * @date 2022年07月07日
 */
@Service
public class UserFollowingService {

    @Autowired
    private UserFollowingDao userFollowingDao;

    @Autowired
    private FollowingGroupService followingGroupService;

    @Autowired
    private UserService userService;

    @Transactional
    public void addUserFollowings(UserFollowing userFollowing) {
        Long groupId = userFollowing.getGroupId();
        if (groupId == null) {
            FollowingGroup followingGroup =
                    followingGroupService.getByType(UserConstant.USER_FOLLOWING_GROUP_TYPE_DEFAULT);
            userFollowing.setGroupId(followingGroup.getUserId());
        } else {
            FollowingGroup followingGroup = followingGroupService.getById(groupId);
            if (followingGroup == null) {
                throw new ConditionException("关注分组不存在!");
            }
        }

        Long followingId = userFollowing.getFollowingId();
        User user = userService.getUserById(followingId);
        if (user == null) {
            throw new ConditionException("关注的用户不存在!");
        }

        userFollowingDao.deleteUserFollowing(userFollowing.getUserId(), userFollowing.getFollowingId());
        userFollowing.setCreateTime(new Date());
        userFollowingDao.addUserFollowing(userFollowing);
    }

    /**
     * 查询某用户的关注列表
     *
     * @param userId 用户id
     * @return 关注列表
     */
    public List<FollowingGroup> getUserFollowings(Long userId) {

        // 查询用户的关注信息
        List<UserFollowing> list = userFollowingDao.getUserFollowings(userId);

        // 获取被关注人员的id，查询出被关注人员的信息
        Set<Long> followingIdSet = list.stream().map(UserFollowing::getFollowingId).collect(Collectors.toSet());
        List<UserInfo> userInfoList = new ArrayList<>();
        if (!followingIdSet.isEmpty()) {
            userInfoList = userService.getUserInfoByUserIds(followingIdSet);
        }

        // 把被关注人员的信息添加到userFollowing中
        for (UserFollowing userFollowing : list) {
            for (UserInfo userInfo : userInfoList) {
                if (userFollowing.getFollowingId().equals(userInfo.getUserId())) {
                    userFollowing.setUserInfo(userInfo);
                }
            }
        }

        // 获取用户关注列表的所有分组
        List<FollowingGroup> groupList = followingGroupService.getByUserId(userId);
        FollowingGroup allGroup = new FollowingGroup();

        // 构建“全部关注”列表
        allGroup.setName(UserConstant.USER_FOLLOWING_GROUP_ALL_NAME);
        allGroup.setFollowingUserInfoList(userInfoList);

        List<FollowingGroup> result = new ArrayList<>();
        result.add(allGroup);

        // 构建其他关注列表
        for (FollowingGroup group : groupList) {
            List<UserInfo> infoList = new ArrayList<>();
            for (UserFollowing userFollowing : list) {
                if (group.getId().equals(userFollowing.getGroupId())) {
                    infoList.add(userFollowing.getUserInfo());
                }
            }
            group.setFollowingUserInfoList(infoList);
            result.add(group);
        }

        return result;
    }

    /**
     * 获取某用户的粉丝
     *
     * @param userId 用户id
     * @return 粉丝列表
     */
    public List<UserFollowing> getUserFans(Long userId) {

        // 获取用户的粉丝列表
        List<UserFollowing> fanList = userFollowingDao.getUserFans(userId);

        // 获取粉丝信息
        Set<Long> fanIdSet = fanList.stream().map(UserFollowing::getUserId).collect(Collectors.toSet());
        List<UserInfo> userInfoList = new ArrayList<>();
        if (!fanIdSet.isEmpty()) {
            userInfoList = userService.getUserInfoByUserIds(fanIdSet);
        }

        // 获取用户的关注列表
        List<UserFollowing> followingList = userFollowingDao.getUserFollowings(userId);

        // 遍历每一个粉丝，如果用户本身也关注了该粉丝，则设置互粉状态。
        for (UserFollowing fan : fanList) {
            for (UserInfo userInfo : userInfoList) {
                if (fan.getUserId().equals(userInfo.getUserId())) {
                    userInfo.setFollowed(false);
                    fan.setUserInfo(userInfo);
                }
            }

            for (UserFollowing following : followingList) {
                if (fan.getUserId().equals(following.getFollowingId())) {
                    fan.getUserInfo().setFollowed(true);
                }
            }
        }

        return fanList;
    }
}
