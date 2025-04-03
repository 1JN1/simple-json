package com.study.json.util;

import com.study.json.JsonArray;
import com.study.json.JsonObject;
import com.study.json.enums.TokenType;
import com.study.json.exception.JsonParseException;
import com.study.json.pojo.Token;

import java.util.List;
import java.util.ListIterator;

/**
 * @author 王文涛
 */
public class JsonParser {
    private final List<Token> tokens;
    private final ListIterator<Token> iterator;
    private Token currentToken;

    public JsonParser(List<Token> tokens) {
        this.tokens = tokens;
        this.iterator = tokens.listIterator();
        if (iterator.hasNext()) {
            this.currentToken = iterator.next();
        }
    }

    public Object parse() {
        return parseValue();
    }

    private Object parseValue() {
        switch (currentToken.getType()) {
            case BEGIN_OBJECT:
                return parseObject();
            case BEGIN_ARRAY:
                return parseArray();
            case STRING:
                return parseString();
            case NUMBER:
                return parseNumber();
            case BOOLEAN:
                return parseBoolean();
            case NULL:
                return parseNull();
            default:
                throw new JsonParseException("Unexpected token: " + currentToken);
        }
    }

    private JsonObject parseObject() {
        JsonObject jsonObject = new JsonObject();
        consume(TokenType.BEGIN_OBJECT);

        while (currentToken.getType() != TokenType.END_OBJECT) {
            String key = parseString();
            consume(TokenType.SEP_COLON);
            Object value = parseValue();
            jsonObject.put(key, value);

            if (currentToken.getType() != TokenType.END_OBJECT) {
                consume(TokenType.SEP_COMMA);
            }
        }

        consume(TokenType.END_OBJECT);
        return jsonObject;
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

        if (value.contains(".") || value.contains("e") || value.contains("E")) {
            return Double.parseDouble(value);
        } else {
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
        if (currentToken.getType() != expectedType) {
            throw new JsonParseException("Expected " + expectedType + " but found " + currentToken);
        }
        if (iterator.hasNext()) {
            currentToken = iterator.next();
        } else {
            currentToken = new Token(TokenType.END_DOCUMENT, null);
        }
    }
}