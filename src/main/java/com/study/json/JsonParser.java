package com.study.json;

import com.study.json.enums.TokenType;
import com.study.json.exception.JsonParseException;
import com.study.json.pojo.Token;

import java.util.List;
import java.util.ListIterator;

/**
 * @author 王文涛
 * @description 语法解析器
 */
public class JsonParser {

    /**
     * 当前token的迭代器
     */
    private final ListIterator<Token> iterator;

    /**
     * 当前要进行解析的Token
     */
    private Token currentToken;

    public JsonParser(List<Token> tokens) {
        this.iterator = tokens.listIterator();
        if (iterator.hasNext()) {
            this.currentToken = iterator.next();
        }
    }

    public Object parse() {

        Object result = parseValue();

        if (currentToken.getType() != TokenType.END_DOCUMENT) {
            throw new JsonParseException("Extra data after end of JSON: " + currentToken);
        }

        return result;
    }

    private Object parseValue() {
        return switch (currentToken.getType()) {
            case BEGIN_OBJECT -> parseObject();
            case BEGIN_ARRAY -> parseArray();
            case STRING -> parseString();
            case NUMBER -> parseNumber();
            case BOOLEAN -> parseBoolean();
            case NULL -> parseNull();
            default -> throw new JsonParseException("Unexpected token: " + currentToken);
        };
    }

    private JsonObject parseObject() {

        JsonObject object = new JsonObject();

        consume(TokenType.BEGIN_OBJECT);

        while (currentToken.getType() != TokenType.END_OBJECT) {

            // 解析key
            String key = parseString();

            // 解析:
            consume(TokenType.SEP_COLON);

            // 解析value
            Object value = parseValue();
            object.put(key, value);

            // 还没有结束，下一个应该是','字符
            if (currentToken.getType() != TokenType.END_OBJECT) {
                consume(TokenType.SEP_COMMA);
            }

        }

        consume(TokenType.END_OBJECT);

        return object;
    }

    private JsonArray parseArray() {

        JsonArray jsonArray = new JsonArray();
        consume(TokenType.BEGIN_ARRAY);

        while (currentToken.getType() != TokenType.END_ARRAY) {
            Object value = parseValue();
            jsonArray.add(value);
            if (currentToken.getType() != TokenType.END_ARRAY) {
                consume(TokenType.SEP_COMMA);
            }
        }

        consume(TokenType.END_ARRAY);
        return jsonArray;
    }

    private String parseString() {

        String value = currentToken.getValue();

        consume(TokenType.STRING);

        return value;
    }

    private Number parseNumber() {
        String value = currentToken.getValue();
        consume(TokenType.NUMBER);
        // 判断是否是浮点数，如果是，则返回double类型
        if (value.contains(".") || value.contains("e") || value.contains("E")) {
            return Double.parseDouble(value);
        } else {
            // 尝试使用整形进行解析，不行则使用长整形进行解析
            try {
                return Integer.parseInt(value);
            } catch (NumberFormatException e) {
                return Long.parseLong(value);
            }
        }
    }

    private Boolean parseBoolean() {
        Boolean value = Boolean.valueOf(currentToken.getValue());
        consume(TokenType.BOOLEAN);
        return value;
    }

    private Object parseNull() {
        consume(TokenType.NULL);
        return null;
    }

    private void consume(TokenType expectedType) {

        // 当前token的类型不是期望的类型，抛出异常
        if (currentToken.getType() != expectedType) {
            throw new JsonParseException("Expected " + expectedType + " but found " + currentToken);
        }

        // 指向下一个token
        if (iterator.hasNext()) {
            currentToken = iterator.next();
        } else {
            // 已经解析完Token了，放置结束标记
            currentToken = new Token(TokenType.END_DOCUMENT, null);
        }
    }

}