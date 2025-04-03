package com.study.json.exception;

/**
 * @author 王文涛
 * @date 2025/4/3
 * @description
 **/
public class JsonParseException extends RuntimeException {

    public JsonParseException() {
        super();
    }

    public JsonParseException(String message) {
        super(message);
    }

}
