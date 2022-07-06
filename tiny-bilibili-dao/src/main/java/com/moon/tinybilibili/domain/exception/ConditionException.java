package com.moon.tinybilibili.domain.exception;

import lombok.Getter;
import lombok.Setter;

import java.io.Serial;

/**
 * @author Chanmoey
 * @date 2022年07月06日
 */
@Getter
@Setter
public class ConditionException extends RuntimeException {

    @Serial
    private static final long serialVersionUID = 1L;
    
    private String code;

    public ConditionException(String code, String name) {
        super(name);
        this.code = code;
    }

    public ConditionException(String name) {
        super(name);
        this.code = "500";
    }
}
