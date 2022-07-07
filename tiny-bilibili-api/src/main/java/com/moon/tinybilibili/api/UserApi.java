package com.moon.tinybilibili.api;

import com.moon.tinybilibili.api.support.UserSupport;
import com.moon.tinybilibili.domain.JsonResponse;
import com.moon.tinybilibili.domain.User;
import com.moon.tinybilibili.domain.UserInfo;
import com.moon.tinybilibili.service.UserService;
import com.moon.tinybilibili.service.utils.RSAUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @author Chanmoey
 * @date 2022年07月06日
 */
@RestController
public class UserApi {

    @Autowired
    private UserService userService;

    @Autowired
    private UserSupport userSupport;

    @GetMapping("/get-rsa-code")
    public JsonResponse<String> getRsaCode() throws Exception {
        return new JsonResponse<>(RSAUtil.encrypt("123456"));
    }

    @GetMapping("/users")
    public JsonResponse<User> getUser() {
        Long userId = this.userSupport.getCurrentUserId();
        User user = this.userService.getUserInfo(userId);
        return new JsonResponse<>(user);
    }

    @GetMapping("/rsa-pks")
    public JsonResponse<String> getRasPublicKey() {
        String pk = RSAUtil.getPublicKeyStr();
        return new JsonResponse<>(pk);
    }

    @PostMapping("/users")
    public JsonResponse<String> addUser(@RequestBody User user) {
        userService.addUser(user);
        return JsonResponse.success();
    }

    @PostMapping("/user-tokens")
    public JsonResponse<String> login(@RequestBody User user) throws Exception {
        String token = userService.login(user);
        return new JsonResponse<>(token);
    }

    @PutMapping("/users")
    public JsonResponse<String> updateUsers(@RequestBody User user) throws Exception {
        Long userId = userSupport.getCurrentUserId();
        user.setId(userId);
        this.userService.updateUsers(user);
        return JsonResponse.success();
    }

    @PutMapping("/user-infos")
    public JsonResponse<String> updateUserInfos(@RequestBody UserInfo userInfo) {
        Long userId = userSupport.getCurrentUserId();
        userInfo.setUserId(userId);
        userService.updateUserInfos(userInfo);
        return JsonResponse.success();
    }
}
