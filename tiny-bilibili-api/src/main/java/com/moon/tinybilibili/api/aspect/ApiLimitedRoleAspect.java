package com.moon.tinybilibili.api.aspect;

import com.moon.tinybilibili.api.support.UserSupport;
import com.moon.tinybilibili.domain.annotation.ApiLimited;
import com.moon.tinybilibili.domain.auth.UserRole;
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
public class ApiLimitedRoleAspect {

    @Autowired
    private UserSupport userSupport;

    @Autowired
    private UserRoleService userRoleService;

    @Pointcut("@annotation(com.moon.tinybilibili.domain.annotation.ApiLimited)")
    public void check() {
    }

    @Before("check() && @annotation(apiLimited)")
    public void doBefore(JoinPoint joinPoint, ApiLimited apiLimited){
        Long userId = userSupport.getCurrentUserId();
        List<UserRole> userRoleList = userRoleService.getUserRoleByUserId(userId);
        String[] limitedRoleCodeList = apiLimited.limitedRoleCodeList();
        Set<String> limitedRoleCodeSet = Arrays.stream(limitedRoleCodeList).collect(Collectors.toSet());
        Set<String> roleCodeSet = userRoleList.stream().map(UserRole::getRoleCode).collect(Collectors.toSet());
        roleCodeSet.retainAll(limitedRoleCodeSet);
        if (!roleCodeSet.isEmpty()) {
            throw new ConditionException("权限不足!");
        }
    }
}
