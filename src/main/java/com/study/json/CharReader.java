package com.study.json;

import java.io.Closeable;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.Objects;

/**
 * @author 王文涛
 * @date 2025/4/3
 * @description 字符读取器
 **/
public class CharReader implements Closeable {

    /**
     * 底层字符输入流
     */
    private final Reader reader;

    /**
     * 缓冲区
     */
    private final char[] buffer;

    /**
     * 默认缓冲区大小
     */
    private static final int BUFFER_SIZE = 8192;

    /**
     * 当前读取位置
     */
    private int pos;

    /**
     * 缓冲区中有效数据大小
     */
    private int size;

    /**
     * 缓存当前字符
     */
    private int currentChar = EOF;

    /**
     * 结束位置标记
     */
    public static final char EOF = (char) -1;

    public CharReader(String str) {
        this(new StringReader(str));
    }

    public CharReader(Reader reader) {
        this(reader, BUFFER_SIZE);
    }

    public CharReader(Reader reader, int bufferSize) {

        if (Objects.isNull(reader)) {
            throw new IllegalArgumentException("reader is null");
        }

        if (bufferSize <= 0) {
            throw new IllegalArgumentException("bufferSize is invalid");
        }

        this.reader = reader;
        this.buffer = new char[bufferSize];
        this.pos = 0;
        this.size = 0;
    }

    /**
     * 查看当前位置字符
     *
     * @return
     */
    public char peek() throws IOException {

        if (currentChar == EOF && hasMore()) {
            currentChar = buffer[pos];
        }

        return (char) currentChar;
    }

    /**
     * 回退
     */
    public void back() {
        if (pos <= 0) {
            throw new IllegalArgumentException("pos <= 0");
        }

        pos--;
        currentChar = buffer[pos];
    }

    /**
     * 读取下一个字符
     *
     * @return
     * @throws IOException
     */
    public char next() throws IOException {

        char result = peek();

        if (result != EOF) {
            pos++;
            currentChar = (pos < size) ? buffer[pos] : EOF;
        }

        return result;
    }

    /**
     * 是否还有更多字符
     *
     * @return
     * @throws IOException
     */
    public boolean hasMore() throws IOException {

        if (pos < size) {
            return true;
        }

        // 尝试填充缓冲区
        fillBuffer();

        return pos < size;
    }

    /**
     * 填充缓冲区
     *
     * @throws IOException
     */
    private void fillBuffer() throws IOException {

        int n = reader.read(buffer);

        // 没有数据了
        if (n == -1) {
            size = 0;
            currentChar = EOF;
            return;
        }

        pos = 0;
        size = n;
        currentChar = buffer[pos];
    }

    /**
     * 跳过空白字符
     *
     * @throws IOException
     */
    public void skipWhitespace() throws IOException {
        while (hasMore()) {
            char ch = peek();
            if (!Character.isWhitespace(ch)) {
                break;
            }
            next();
        }
    }

    @Override
    public void close() throws IOException {
        reader.close();
    }
}
