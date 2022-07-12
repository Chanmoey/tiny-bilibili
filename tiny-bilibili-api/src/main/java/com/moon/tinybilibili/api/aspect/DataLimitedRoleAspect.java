package com.moon.tinybilibili.api.aspect;

import com.moon.tinybilibili.api.support.UserSupport;
import com.moon.tinybilibili.domain.UserMoment;
import com.moon.tinybilibili.domain.annotation.ApiLimited;
import com.moon.tinybilibili.domain.auth.UserRole;
import com.moon.tinybilibili.domain.constant.AuthRoleConstant;
import com.moon.tinybilibili.domain.exception.ConditionException;
import com.moon.tinybilibili.service.UserRoleService;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author Chanmoey
 * @date 2022年07月11日
 */
@Order(1)
@Component
@Aspect
public class DataLimitedRoleAspect {

    @Autowired
    private UserSupport userSupport;

    @Autowired
    private UserRoleService userRoleService;

    @Pointcut("@annotation(com.moon.tinybilibili.domain.annotation.DataLimited)")
    public void check() {
    }

    @Before("check()")
    public void doBefore(JoinPoint joinPoint){
        Long userId = userSupport.getCurrentUserId();
        List<UserRole> userRoleList = userRoleService.getUserRoleByUserId(userId);
        Set<String> roleCodeSet = userRoleList.stream().map(UserRole::getRoleCode).collect(Collectors.toSet());
        Object[] args = joinPoint.getArgs();
        for (Object arg : args) {
            if (arg instanceof UserMoment userMoment) {
                String type = userMoment.getType();
                if (roleCodeSet.contains(AuthRoleConstant.ROLE_LV1) && !"0".equals(type)) {
                    throw new ConditionException("参数异常!");
                }
            }
        }
    }
}
