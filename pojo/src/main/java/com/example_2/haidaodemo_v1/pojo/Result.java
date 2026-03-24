package com.example_2.haidaodemo_v1.pojo;

import com.example_2.haidaodemo_v1.constant.Code;
import lombok.Data;

@Data
public class Result<T> {
    private Integer code;
    private String msg;
    private T data;

    public static <E> Result<E> success(E data) {
        Result<E> result = new Result<>();
        result.setCode(Code.SUCCESS);
        result.setMsg("success");
        result.setData(data);
        return result;
    }

    public static <E> Result<E> error(String msg) {
        Result<E> result = new Result<>();
        result.setCode(Code.ERROR);
        result.setMsg(msg);
        result.setData(null);
        return result;
    }

    public boolean isSuccess() {
        return this.code == Code.SUCCESS;
    }
}
