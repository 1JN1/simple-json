package com.study.json;

import com.study.json.exception.JsonParseException;
import com.study.json.pojo.CharReader;
import com.study.json.pojo.Token;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.lang.reflect.InvocationTargetException;
import java.util.List;

/**
 * @author 王文涛
 */
public class Json {
    public static Object parse(String jsonString) throws IOException {
        try (StringReader reader = new StringReader(jsonString)) {
            return parse(reader);
        }
    }

    public static Object parse(Reader reader) throws IOException {
        CharReader charReader = new CharReader(reader);
        Tokenizer tokenizer = new Tokenizer();
        List<Token> tokens = tokenizer.tokenize(charReader);
        JsonParser parser = new JsonParser(tokens);
        return parser.parse();
    }

    public static JsonObject parseToJsonObject(String jsonString) throws IOException {
        Object result = parse(jsonString);
        if (result instanceof JsonObject) {
            return (JsonObject) result;
        }
        throw new JsonParseException("Not a JSON Object");
    }

    public static <T> T parseToClass(String jsonString, Class<T> clazz) throws IOException, NoSuchFieldException, InvocationTargetException, IllegalAccessException, NoSuchMethodException, InstantiationException {
        Object result = parse(jsonString);

        if (result instanceof JsonObject) {
            return ((JsonObject) result).convertClass(clazz);
        }

        throw new JsonParseException("Not a JSON Object");
    }

    public static JsonArray parseToJsonArray(String jsonString) throws IOException {
        Object result = parse(jsonString);
        if (result instanceof JsonArray) {
            return (JsonArray) result;
        }
        throw new JsonParseException("Not a JSON Array");
    }
}