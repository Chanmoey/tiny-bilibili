package com.moon.tinybilibili.domain;

import lombok.Getter;
import lombok.Setter;

/**
 * @author Chanmoey
 * @date 2022年07月06日
 */
@Getter
@Setter
public class JsonResponse<T> {

    private String code;
    private String msg;
    private T data;

    public JsonResponse(String code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public JsonResponse(T data) {
        this.msg = "Success";
        this.code = "0";
        this.data = data;
    }

    public static JsonResponse<String> success() {
        return new JsonResponse<>(null);
    }

    public static JsonResponse<String> success(String data) {
        return new JsonResponse<>(data);
    }

    public static JsonResponse<String> fail() {
        return new JsonResponse<>("1", "Fail");
    }

    public static JsonResponse<String> fail(String code, String msg) {
        return new JsonResponse<>(code, msg);
    }
}
