package com.study.json.enums;

/**
 * @author 王文涛
 * @date 2025/4/3
 * @description 常用的合法json token类型
 **/

public enum TokenType {

    /**
     * {
     */
    BEGIN_OBJECT(1),

    /**
     * }
     */
    END_OBJECT(2),

    /**
     * [
     */
    BEGIN_ARRAY(3),

    /**
     * ]
     */
    END_ARRAY(4),

    /**
     * null
     */
    NULL(5),

    /**
     * 数字
     */
    NUMBER(6),

    /**
     * 字符串
     */
    STRING(7),

    /**
     * 布尔
     */
    BOOLEAN(8),

    /**
     * :
     */
    SEP_COLON(9),

    /**
     * ,
     */
    SEP_COMMA(10),

    /**
     * 结束标记
     */
    END_DOCUMENT(11);


    TokenType(
            int code) {
        this.code = code;
    }

    private int code;

    public int getCode() {
        return code;
    }

}
