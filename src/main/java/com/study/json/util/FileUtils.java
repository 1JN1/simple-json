package com.study.json.util;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author 王文涛
 */
public class FileUtils {

    private final static Pattern pattern = Pattern.compile("\\{([^{}]|\\{[^{}]*\\})*\\}");

    public static List<String> extractAllJsonStrings(String filePath) throws IOException {
        String jsonContent = new String(Files.readAllBytes(Paths.get(filePath))).trim();
        return parseJsonContent(jsonContent);
    }


    private static List<String> parseJsonContent(String jsonString) {
        List<String> jsonObjects = new ArrayList<>();
        jsonString = jsonString.trim();

        if (jsonString.startsWith("[")) {
            // 处理JSON数组
            extractJsonArray(jsonString, jsonObjects);
        } else if (jsonString.startsWith("{")) {
            // 处理单个JSON对象
            jsonObjects.add(jsonString);
        } else {
            throw new IllegalArgumentException("Invalid JSON: Must start with [ or {");
        }

        return jsonObjects;
    }


    private static void extractJsonArray(String jsonArray, List<String> output) {
        // 移除外层方括号
        String content = jsonArray.substring(1, jsonArray.length() - 1).trim();
        if (content.isEmpty()) {
            return;
        }

        // 正则匹配JSON对象（简单版，不处理嵌套对象中的逗号）
        Matcher matcher = pattern.matcher(content);

        while (matcher.find()) {
            output.add(matcher.group());
        }
    }
}