package org.alibaba.cloud.ai.common.domain;

import java.io.Serializable;

/**
 * @author myseital
 * @date 2022/9/19
 */
public class R<T> implements Serializable {

    private static final long serialVersionUID = 1617610523176455642L;

    public static final Integer SUCCESS_CODE = 1;
    public static final Integer ERROR_CODE = 2;
    private Integer code;
    private T data;
    private String desc;

    public R() {
    }

    public R(Integer code, T data, String desc) {
        this.code = code;
        this.data = data;
        this.desc = desc;
    }

    public Integer getCode() {
        return this.code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public T getData() {
        return this.data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public String getDesc() {
        return this.desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public static <T> R<T> success(T data) {
        return new R(SUCCESS_CODE, data, "");
    }

    public static <T> R<T> ok() {
        return new R(SUCCESS_CODE, (Object)null, "");
    }

    public static <T> R<T> ok(T data) {
        return restResult(data, SUCCESS_CODE, null);
    }

    public static <T> R<T> error(String msg) {
        return error(msg, null);
    }

    public static <T> R<T> error(String msg, T obj) {
        return new R(ERROR_CODE, obj, msg);
    }

    public static <T> R<T> error(Integer code, T obj, String msg) {
        return new R(code, obj, msg);
    }

    public static <T> R<T> restResult(T data, int code, String desc) {
        R<T> apiResult = new R<>();
        apiResult.setCode(code);
        apiResult.setData(data);
        apiResult.setDesc(desc);
        return apiResult;
    }

    public boolean isSuccess() {
       return SUCCESS_CODE.equals(code);
    }
}
