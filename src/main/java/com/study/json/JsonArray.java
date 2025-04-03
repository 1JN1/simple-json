package com.study.json;

import com.study.json.exception.JsonParseException;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * @author 王文涛
 * @date 2025/4/3
 * @description
 **/
public class JsonArray implements Iterable<Object> {

    private List<Object> list;

    public JsonArray() {
        list = new ArrayList<>();
    }

    public void add(Object obj) {
        list.add(obj);
    }

    public Object get(int index) {
        return list.get(index);
    }

    public int size() {
        return list.size();
    }

    public JsonObject getJsonObject(int index) {

        Object obj = list.get(index);

        if (!(obj instanceof JsonObject)) {
            throw new JsonParseException("Type of value is not JsonObject");
        }

        return (JsonObject) obj;
    }

    public JsonArray getJsonArray(int index) {

        Object obj = list.get(index);

        if (!(obj instanceof JsonArray)) {
            throw new JsonParseException("Type of value is not JsonArray");
        }

        return (JsonArray) obj;
    }

    @Override
    public Iterator<Object> iterator() {
        return list.iterator();
    }
}
