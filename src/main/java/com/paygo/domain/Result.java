package com.paygo.domain;

/**
 * result of method
 */
public class Result {
    private ResultCode code;
    private String msg;

    public Result (ResultCode code, String msg) {
        this.msg = msg;
        this.code = code;
    }

    public ResultCode getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }
}
