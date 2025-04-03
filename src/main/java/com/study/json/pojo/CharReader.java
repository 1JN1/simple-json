package com.study.json.pojo;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;

/**
 * @author 王文涛
 * @date 2025/4/3
 * @description 字符读取器
 **/
public class CharReader {

    /**
     * 底层字符输入流
     */
    private final Reader reader;

    /**
     * 缓冲区
     */
    private final char[] buffer;

    /**
     * 当前读取位置
     */
    private int pos;

    /**
     * 缓冲区中有效数据大小
     */
    private int size;

    /**
     * 默认缓冲区大小
     */
    private static final int BUFFER_SIZE = 8192;

    public static final char EOF = (char) -1;

    public CharReader(String str) {
        this(new StringReader(str));
    }

    public CharReader(Reader reader) {
        this.reader = reader;
        this.buffer = new char[BUFFER_SIZE];
        this.pos = 0;
        this.size = 0;
    }

    /**
     * 查看当前位置字符
     *
     * @return
     */
    public char peek() {

        if (pos >= size) {
            return EOF;
        }

        int idx = Math.max(0, pos);

        return buffer[idx];
    }

    /**
     * 读取下一个字符并移动当前位置指针
     *
     * @return
     * @throws IOException
     */
    public char next() throws IOException {

        // 已经读取完毕
        if (!hasMore()) {
            return EOF;
        }

        return buffer[pos++];
    }

    /**
     * 回退指针
     */
    public void back() {
        pos = Math.max(0, pos - 1);
    }

    /**
     * 判断是否还有更多字符
     *
     * @return
     * @throws IOException
     */
    public boolean hasMore() throws IOException {

        if (pos < size) {
            return true;
        }

        // 缓冲区中无数据，从流中读取数据
        fillBuffer();

        return pos < size;
    }

    /**
     * 从流中读取数据到缓冲区中
     *
     * @throws IOException
     */
    private void fillBuffer() throws IOException {

        int n = reader.read(buffer);

        // 已达到流末尾
        if (n == -1) {
            return;
        }

        pos = 0;
        size = n;
    }

}
