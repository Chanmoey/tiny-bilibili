package com.moon.tinybilibili.api.support;

import com.moon.tinybilibili.domain.exception.ConditionException;
import com.moon.tinybilibili.service.utils.TokenUtil;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

/**
 * @author Chanmoey
 * @date 2022年07月07日
 */
@Component
public class UserSupport {

    public Long getCurrentUserId() {
        ServletRequestAttributes requestAttributes =
                (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        String token = requestAttributes.getRequest().getHeader("token");
        Long userId = TokenUtil.verifyToken(token);
        if (userId < 0) {
            throw new ConditionException("非法用户!");
        }

        return userId;
    }
}
