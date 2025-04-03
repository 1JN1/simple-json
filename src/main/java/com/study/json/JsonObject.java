package com.study.json;

import com.study.json.exception.JsonParseException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author 王文涛
 * @date 2025/4/3
 * @description
 **/
public class JsonObject {

    private Map<String, Object> map = null;

    public JsonObject() {
        this.map = new HashMap<>();
    }

    public void put(String key, Object value) {
        this.map.put(key, value);
    }

    public Object get(String key) {
        return this.map.get(key);
    }

    public List<Map.Entry<String, Object>> getAllKeyValue() {
        return this.map.entrySet().stream().toList();
    }

    public JsonObject getJsonObject(String key) {

        if (!map.containsKey(key)) {
            throw new IllegalArgumentException("Invalid key");
        }

        Object obj = map.get(key);

        if (!(obj instanceof JsonObject)) {
            throw new JsonParseException("Type of value is not JsonObject");
        }

        return (JsonObject) obj;
    }

    public JsonArray getJsonArray(String key) {

        if (!map.containsKey(key)) {
            throw new IllegalArgumentException("Invalid key");
        }

        Object obj = map.get(key);

        if (!(obj instanceof JsonArray)) {
            throw new JsonParseException("Type of value is not JsonArray");
        }

        return (JsonArray) obj;
    }

    @Override
    public String toString() {
        return map.toString();
    }

}
