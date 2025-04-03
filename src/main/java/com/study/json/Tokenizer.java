package com.study.json;

import com.study.json.enums.TokenType;
import com.study.json.exception.JsonParseException;
import com.study.json.pojo.CharReader;
import com.study.json.pojo.Token;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author 王文涛
 * @date 2025/4/3
 * @description 词法解析器
 **/
public class Tokenizer {

    private CharReader charReader;
    private List<Token> tokens;

    public List<Token> tokenize(CharReader charReader) throws IOException {
        this.charReader = charReader;
        this.tokens = new ArrayList<>();
        tokenize();
        return tokens;
    }

    private void tokenize() throws IOException {
        Token token;
        do {
            token = start();
            tokens.add(token);
        } while (token.getType() != TokenType.END_DOCUMENT);
    }

    private Token start() throws IOException {
        char ch;
        // 跳过空白字符
        for (; ; ) {
            if (!charReader.hasMore()) {
                return new Token(TokenType.END_DOCUMENT, null);
            }
            ch = charReader.next();
            if (!isWhitespace(ch)) {
                break;
            }
        }

        // 根据字符类型生成对应Token
        switch (ch) {
            case '{':
                return new Token(TokenType.BEGIN_OBJECT, "{");
            case '}':
                return new Token(TokenType.END_OBJECT, "}");
            case '[':
                return new Token(TokenType.BEGIN_ARRAY, "[");
            case ']':
                return new Token(TokenType.END_ARRAY, "]");
            case ',':
                return new Token(TokenType.SEP_COMMA, ",");
            case ':':
                return new Token(TokenType.SEP_COLON, ":");
            case 'n':
                return readNull();
            case 't':
            case 'f':
                return readBoolean(ch);
            case '"':
                return readString();
            case '-':
                return readNumber();
            default:
        }

        if (isDigit(ch)) {
            return readNumber();
        }

        throw new JsonParseException("Illegal character: " + ch);
    }

    private Token readNull() throws IOException {
        if (charReader.next() != 'u' || charReader.next() != 'l' || charReader.next() != 'l') {
            throw new JsonParseException("Invalid null value");
        }
        return new Token(TokenType.NULL, "null");
    }

    private Token readBoolean(char firstChar) throws IOException {
        StringBuilder sb = new StringBuilder().append(firstChar);
        String expected = firstChar == 't' ? "rue" : "alse";

        for (char c : expected.toCharArray()) {
            if (charReader.next() != c) {
                throw new JsonParseException("Invalid boolean value");
            }
            sb.append(c);
        }

        return new Token(TokenType.BOOLEAN, sb.toString());
    }

    private Token readString() throws IOException {
        StringBuilder sb = new StringBuilder();
        while (true) {
            char ch = charReader.next();
            if (ch == '\\') {
                // 处理转义字符
                ch = charReader.next();
                switch (ch) {
                    case '"':
                        sb.append('"');
                        break;
                    case '\\':
                        sb.append('\\');
                        break;
                    case '/':
                        sb.append('/');
                        break;
                    case 'b':
                        sb.append('\b');
                        break;
                    case 'f':
                        sb.append('\f');
                        break;
                    case 'n':
                        sb.append('\n');
                        break;
                    case 'r':
                        sb.append('\r');
                        break;
                    case 't':
                        sb.append('\t');
                        break;
                    case 'u':
                        // 处理Unicode转义（简化版）
                        char[] hex = new char[4];
                        for (int i = 0; i < 4; i++) {
                            hex[i] = charReader.next();
                        }
                        sb.append((char) Integer.parseInt(new String(hex), 16));
                        break;
                    default:
                        throw new JsonParseException("Invalid escape character");
                }
            } else if (ch == '"') {
                break; // 字符串结束
            } else if (ch < 0x20) {
                throw new JsonParseException("Invalid control character in string");
            } else {
                sb.append(ch);
            }
        }
        return new Token(TokenType.STRING, sb.toString());
    }

    private Token readNumber() throws IOException {
        charReader.back(); // 回退到第一个数字字符（或负号）
        StringBuilder sb = new StringBuilder();

        // 读取整数部分
        readDigits(sb);

        // 处理小数部分
        if (charReader.peek() == '.') {
            sb.append(charReader.next());
            readDigits(sb);
        }

        // 处理指数部分
        char ch = charReader.peek();
        if (ch == 'e' || ch == 'E') {
            sb.append(charReader.next());
            ch = charReader.peek();
            if (ch == '+' || ch == '-') {
                sb.append(charReader.next());
            }
            readDigits(sb);
        }

        return new Token(TokenType.NUMBER, sb.toString());
    }

    private void readDigits(StringBuilder sb) throws IOException {
        while (isDigit(charReader.peek())) {
            sb.append(charReader.next());
        }
    }

    private boolean isWhitespace(char ch) {
        return ch == ' ' || ch == '\t' || ch == '\n' || ch == '\r';
    }

    private boolean isDigit(char ch) {
        return ch >= '0' && ch <= '9';
    }

}
