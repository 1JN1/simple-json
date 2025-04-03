package com.study;

import com.study.json.Tokenizer;
import com.study.json.pojo.CharReader;
import com.study.json.pojo.Token;
import com.study.json.util.FileUtils;
import org.junit.Test;

import java.io.StringReader;
import java.util.List;

/**
 * @author 王文涛
 * @date 2025/4/3
 * @description
 **/
public class TokenizerTest {

    @Test
    public void test1() throws Exception {

        List<String> list = FileUtils.extractAllJsonStrings("D:\\STUDY\\code\\Java\\json\\src\\test\\java\\com\\study\\data.json");
        for (String jsonString : list) {
            System.out.println(jsonString);
        }

    }

    @Test
    public void test() throws Exception {

        List<String> jsonStrs = FileUtils.extractAllJsonStrings("D:\\STUDY\\code\\Java\\json\\src\\test\\java\\com\\study\\data.json");

        Tokenizer tokenizer = new Tokenizer();

        for (String jsonStr : jsonStrs) {

            CharReader charReader = new CharReader(jsonStr);

            List<Token> tokenize = tokenizer.tokenize(charReader);

            System.out.println("====================");
            for (Token token : tokenize) {
                System.out.println(token);
            }
        }

    }

}
