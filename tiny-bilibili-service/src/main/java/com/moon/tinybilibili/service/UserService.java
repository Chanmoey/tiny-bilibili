package com.moon.tinybilibili.service;

import com.alibaba.fastjson.JSONObject;
import com.moon.tinybilibili.dao.UserDao;
import com.moon.tinybilibili.domain.PageResult;
import com.moon.tinybilibili.domain.RefreshTokenDetail;
import com.moon.tinybilibili.domain.User;
import com.moon.tinybilibili.domain.UserInfo;
import com.moon.tinybilibili.domain.exception.ConditionException;
import com.moon.tinybilibili.service.utils.MD5Util;
import com.moon.tinybilibili.service.utils.RSAUtil;
import com.moon.tinybilibili.service.utils.TokenUtil;
import com.mysql.cj.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * @author Chanmoey
 * @date 2022年07月06日
 */
@Service
public class UserService {

    @Autowired
    private UserDao userDao;

    @Autowired
    private UserAuthService userAuthService;

    @Transactional(rollbackFor = Exception.class)
    public void addUser(User user) {
        String phone = user.getPhone();
        this.validPhone(phone);
        User dbUser = this.userDao.getUserByPhone(phone);
        if (dbUser != null) {
            throw new ConditionException("该手机号已被注册！");
        }
        Date now = new Date();
        String salt = String.valueOf(now.getTime());
        String password = user.getPassword();
        String rawPassword = this.getRawPassword(password);
        String md5Password = MD5Util.sign(rawPassword, salt);

        user.setSalt(salt);
        user.setPassword(md5Password);
        user.setCreateTime(now);

        if (this.userDao.addUser(user) != 1) {
            throw new ConditionException("新增用户失败!");
        }
        // 添加用户信息
        UserInfo userInfo = new UserInfo();
        userInfo.setUserId(user.getId());
        userInfo.setDefaultData();
        if (this.userDao.addUserInfo(userInfo) != 1) {
            throw new ConditionException("新增用户失败!");
        }
        // 添加用户默认权限角色
        userAuthService.addUserDefaultRole(user.getId());
    }

    public User getUserByPhone(String phone) {
        return this.userDao.getUserByPhone(phone);
    }

    public User getUserInfo(Long userId) {
        User user = this.userDao.getUserById(userId);
        UserInfo userInfo = this.userDao.getUserInfoByUserId(userId);
        user.setUserInfo(userInfo);
        return user;
    }

    public User getUserById(Long userId) {
        return this.userDao.getUserById(userId);
    }

    public String login(User user) throws Exception {
        String phone = user.getPhone();
        this.validPhone(phone);
        User dbUser = this.userDao.getUserByPhone(phone);
        if (dbUser == null) {
            throw new ConditionException("当前用户不存在!");
        }
        String rawPassword = this.getRawPassword(user.getPassword());
        String salt = dbUser.getSalt();
        String md5Password = MD5Util.sign(rawPassword, salt);

        if (!md5Password.equals(dbUser.getPassword())) {
            throw new ConditionException("密码错误!");
        }

        return TokenUtil.generateToken(dbUser.getId());
    }

    private void validPhone(String phone) {
        if (StringUtils.isNullOrEmpty(phone)) {
            throw new ConditionException("手机号不能为空！");
        }
    }

    private String getRawPassword(String password) {
        try {
            return RSAUtil.decrypt(password);
        } catch (Exception e) {
            throw new ConditionException("密码解密失败!");
        }
    }

    public void updateUsers(User user) throws Exception {
        Long id = user.getId();
        User dbUser = userDao.getUserById(id);
        if (dbUser == null) {
            throw new ConditionException("用户不存在！");
        }
        if (!StringUtils.isNullOrEmpty(user.getPassword())) {
            String rawPassword = RSAUtil.decrypt(user.getPassword());
            String md5Password = MD5Util.sign(rawPassword, dbUser.getSalt());
            user.setPassword(md5Password);
        }
        user.setUpdateTime(new Date());
        if (userDao.updateUsers(user) != 1) {
            throw new ConditionException("修改User信息失败!");
        }
    }

    public void updateUserInfos(UserInfo userInfo) {
        userInfo.setUpdateTime(new Date());
        if (userDao.updateUserInfos(userInfo) != 1) {
            throw new ConditionException("修改UserInfo信息失败!");
        }
    }

    public List<UserInfo> getUserInfoByUserIds(Set<Long> userIdList) {
        return userDao.getUserInfoByUserIds(userIdList);
    }

    public PageResult<UserInfo> pageListUserInfos(JSONObject params) {
        Integer no = params.getInteger("no");
        Integer size = params.getInteger(("size"));
        params.put("start", (no - 1) * size);
        params.put("limit", size);
        Integer total = userDao.pageCountUserInfos(params);
        List<UserInfo> list = new ArrayList<>();
        if (total > 0) {
            list = userDao.pageListUserInfos(params);
        }

        return new PageResult<>(total, list);
    }

    public Map<String, Object> loginForDts(User user) throws Exception {
        String phone = user.getPhone();
        this.validPhone(phone);
        User dbUser = this.userDao.getUserByPhone(phone);
        if (dbUser == null) {
            throw new ConditionException("当前用户不存在!");
        }
        String rawPassword = this.getRawPassword(user.getPassword());
        String salt = dbUser.getSalt();
        String md5Password = MD5Util.sign(rawPassword, salt);

        if (!md5Password.equals(dbUser.getPassword())) {
            throw new ConditionException("密码错误!");
        }

        Long dbUserId = dbUser.getId();
        String accessToken = TokenUtil.generateToken(dbUserId);
        String refreshToken = TokenUtil.generateRefreshToken(dbUserId);

        // 保存refresh token到数据库
        userDao.deleteRefreshToken(refreshToken, dbUserId);
        userDao.addRefreshToken(refreshToken, dbUserId, new Date());

        Map<String, Object> result = new HashMap<>(4);
        result.put("accessToken", accessToken);
        result.put("refreshToken", refreshToken);
        return result;
    }

    public void logout(String refreshToken, Long userId) {
        userDao.deleteRefreshToken(refreshToken, userId);
    }

    public String refreshAccessToken(String refreshToken) throws Exception {
        RefreshTokenDetail refreshTokenDetail = userDao.getRefreshTokenDetail(refreshToken);
        if (refreshTokenDetail == null) {
            throw new ConditionException("555", "refreshToken过期!");
        }

        Long userId = refreshTokenDetail.getId();
        return TokenUtil.generateToken(userId);
    }
}
