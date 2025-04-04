package com.study.json;

import com.study.json.enums.TokenType;
import com.study.json.exception.JsonParseException;
import com.study.json.exception.ReadException;
import com.study.json.pojo.Token;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author 王文涛
 * @date 2025/4/3
 * @description 词法解析器
 **/
public class JsonTokenizer {

    /**
     * 字符流
     */
    private CharReader charReader;

    /**
     * 词法解析器生成的Token集合
     */
    private List<Token> tokens;

    private static final String TRUE_LITERAL = "true";
    private static final String FALSE_LITERAL = "false";
    private static final String NULL_LITERAL = "null";
    // 预定义转义字符映射
    private static final Map<Character, Character> ESCAPE_MAP = new HashMap<>();

    static {
        ESCAPE_MAP.put('"', '"');
        ESCAPE_MAP.put('\\', '\\');
        ESCAPE_MAP.put('/', '/');
        ESCAPE_MAP.put('b', '\b');
        ESCAPE_MAP.put('f', '\f');
        ESCAPE_MAP.put('n', '\n');
        ESCAPE_MAP.put('r', '\r');
        ESCAPE_MAP.put('t', '\t');
    }

    /**
     * 对输入的字符串进行词法解析，生成Token集合
     *
     * @param str
     * @return
     */
    public List<Token> tokenizer(String str) {
        return tokenizer(new StringReader(str));
    }

    public List<Token> tokenizer(Reader reader) {
        try (CharReader charReader = new CharReader(reader)) {
            this.charReader = charReader;
            this.tokens = new ArrayList<>();
            tokenize();
            return tokens;
        } catch (IOException e) {
            throw new ReadException("Failed to close character reader");
        }
    }

    public List<Token> tokenizer(CharReader charReader) {
        try {
            this.charReader = charReader;
            this.tokens = new ArrayList<>();
            tokenize();
            return tokens;
        } catch (Exception e) {
            throw new JsonParseException("Failed to tokenize JSON string");
        }
    }

    /**
     * 词法解析
     */
    private void tokenize() {

        Token token = null;

        do {

            token = generateToken();
            tokens.add(token);

        } while (token.getType() != TokenType.END_DOCUMENT);

    }

    /**
     * 生成一个Token
     *
     * @return
     */
    private Token generateToken() {

        try {

            if (!charReader.hasMore()) {
                return new Token(TokenType.END_DOCUMENT, null);
            }

            // 跳过空白字符
            charReader.skipWhitespace();

            char ch = charReader.next();

            // 根据首字符判断要生成的Token类型
            return switch (ch) {
                case '{' -> new Token(TokenType.BEGIN_OBJECT, "{");
                case '}' -> new Token(TokenType.END_OBJECT, "}");
                case '[' -> new Token(TokenType.BEGIN_ARRAY, "[");
                case ']' -> new Token(TokenType.END_ARRAY, "]");
                case ':' -> new Token(TokenType.SEP_COLON, ":");
                case ',' -> new Token(TokenType.SEP_COMMA, ",");
                // 处理字符串
                case '"' -> readString();
                // 处理null
                case 'n' -> readNull();
                // 处理布尔值
                case 't', 'f' -> readBoolean(ch);
                // 处理数字
                case '-' -> readNumber();
                default -> isDigit(ch) ? readNumber() : new Token(TokenType.END_DOCUMENT, null);
            };


        } catch (IOException e) {
            // 读取字符时发生异常
            throw new ReadException("Failed to read next character");
        }
    }

    /**
     * 处理布尔值
     *
     * @param firstChar 首字符
     * @return
     */
    private Token readBoolean(char firstChar) {

        String expected = firstChar == 't' ? TRUE_LITERAL : FALSE_LITERAL;
        StringBuilder builder = new StringBuilder().append(firstChar);
        try {
            for (int i = 1; i < expected.length(); i++) {
                char ch = charReader.next();
                validateExpectedChar(expected.charAt(i), ch);
                builder.append(ch);
            }
            return new Token(TokenType.BOOLEAN, builder.toString());
        } catch (IOException e) {
            throw new ReadException("Unexpected characters in boolean value");
        }
    }

    /**
     * 处理null
     *
     * @return
     */
    private Token readNull() {
        try {
            validateExpectedChar('u', charReader.next());
            validateExpectedChar('l', charReader.next());
            validateExpectedChar('l', charReader.next());
            return new Token(TokenType.NULL, NULL_LITERAL);
        } catch (IOException e) {
            throw new ReadException("Unexpected characters in null value");
        }
    }

    /**
     * 处理字符串
     *
     * @return
     */
    private Token readString() {

        StringBuilder builder = new StringBuilder();
        try {
            while (true) {
                char ch = charReader.next();
                if (ch == '"') {
                    break;
                } else if (ch == '\\') {
                    // 处理转义字符
                    processEscape(builder);
                } else if (isControlCharacter(ch)) {
                    // 不可以是控制字符
                    throw new ReadException("Invalid control character in string");
                } else {
                    builder.append(ch);
                }
            }
            return new Token(TokenType.STRING, builder.toString());
        } catch (IOException e) {
            throw new ReadException("Failed to read string value");
        }
    }

    /**
     * 处理转义字符
     *
     * @param builder
     * @throws IOException
     */
    private void processEscape(StringBuilder builder) throws IOException {
        char ch = charReader.next();
        if (ESCAPE_MAP.containsKey(ch)) {
            // 预定义的转义字符
            builder.append(ESCAPE_MAP.get(ch));
        } else if (ch == 'u') {
            // 处理Unicode转义字符
            builder.append(readUnicodeEscape());
        } else {
            throw new JsonParseException("Invalid escape sequence: \\" + ch);
        }
    }

    /**
     * 处理Unicode转义字符
     *
     * @return
     * @throws IOException
     */
    private char readUnicodeEscape() throws IOException {
        StringBuilder hex = new StringBuilder();
        for (int i = 0; i < 4; i++) {
            hex.append(charReader.next());
        }
        return (char) Integer.parseInt(hex.toString(), 16);
    }


    /**
     * 处理数字
     *
     * @return
     */
    private Token readNumber() {

        charReader.back();
        StringBuilder builder = new StringBuilder();

        try {
            // 读取整数部分
            readIntegerPart(builder);
            // 读取小数部分
            readFractionPart(builder);
            // 读取指数部分
            readExponentPart(builder);
            return new Token(TokenType.NUMBER, builder.toString());
        } catch (IOException e) {
            throw new ReadException("Unexpected characters in number value");
        }
    }

    /**
     * 读取整数部分
     *
     * @param builder
     * @throws IOException
     */
    private void readIntegerPart(StringBuilder builder) throws IOException {
        if (charReader.peek() == '-') {
            builder.append(charReader.next());
        }
        readDigits(builder);
    }

    /**
     * 读取小数部分
     *
     * @param builder
     * @throws IOException
     */
    private void readFractionPart(StringBuilder builder) throws IOException {
        if (charReader.peek() == '.') {
            builder.append(charReader.next());
            readDigits(builder);
        }
    }

    /**
     * 读取指数部分
     *
     * @param builder
     * @throws IOException
     */
    private void readExponentPart(StringBuilder builder) throws IOException {
        if (charReader.peek() == 'e' || charReader.peek() == 'E') {
            builder.append(charReader.next());
            if (charReader.peek() == '+' || charReader.peek() == '-') {
                builder.append(charReader.next());
            }
            readDigits(builder);
        }
    }

    /**
     * 读取连续数字
     *
     * @param builder
     */
    private void readDigits(StringBuilder builder) {
        try {
            while (isDigit(charReader.peek())) {
                builder.append(charReader.next());
            }
        } catch (IOException e) {
            throw new ReadException("read exception");
        }
    }

    /**
     * 校验期望的字符
     *
     * @param expected
     * @param actual
     */
    private void validateExpectedChar(char expected, char actual) {
        if (actual != expected) {
            throw new ReadException(String.format("Expected '%c' but found '%c'", expected, actual));
        }
    }

    /**
     * 判断是否为控制字符
     *
     * @param ch
     * @return
     */
    private boolean isControlCharacter(char ch) {
        return ch < 0x20;
    }

    /**
     * 判断一个字符是否为数字
     *
     * @param ch
     * @return
     */
    private boolean isDigit(char ch) {
        return Character.isDigit(ch);
    }
}
