package com.study;

import com.study.json.Json;
import com.study.json.JsonObject;
import com.study.json.util.FileUtils;
import org.junit.Test;

import java.util.List;

/**
 * @author 王文涛
 * @date 2025/4/3
 * @description
 **/
public class JsonTest {

    @Test
    public void testParse() throws Exception {

        List<String> list = FileUtils.extractAllJsonStrings("D:\\STUDY\\code\\Java\\json\\src\\test\\java\\com\\study\\data.json");

        for (String jsonString : list) {
            JsonObject object = Json.parseToJsonObject(jsonString);
            System.out.println(object);
        }

    }

}
