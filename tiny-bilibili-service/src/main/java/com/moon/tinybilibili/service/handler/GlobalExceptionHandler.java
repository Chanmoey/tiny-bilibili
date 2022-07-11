package com.moon.tinybilibili.service.handler;

import com.moon.tinybilibili.domain.JsonResponse;
import com.moon.tinybilibili.domain.exception.ConditionException;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;

/**
 * @author Chanmoey
 * @date 2022年07月06日
 */
@ControllerAdvice
@Order(Ordered.HIGHEST_PRECEDENCE)
public class GlobalExceptionHandler {

    @ExceptionHandler(value = Exception.class)
    @ResponseBody
    public JsonResponse<String> commonExceptionHandler(HttpServletRequest request, Exception e) {
        String errorMsg = e.getMessage();
        if (e instanceof ConditionException conditionException) {
            String errorCode = conditionException.getCode();
            return new JsonResponse<>(errorCode, errorMsg);
        } else {
            e.printStackTrace();
            return new JsonResponse<>("500", errorMsg);
        }
    }
}
