package com.study.json;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import com.study.json.exception.JsonParseException;
import com.study.json.util.DateUtils;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.time.LocalDateTime;
import java.util.Date;
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

    /**
     * 将json对象转换为指定的类
     *
     * @param clazz
     * @return
     */
    public <T> T convertClass(Class<T> clazz) throws NoSuchFieldException, IllegalAccessException, NoSuchMethodException, InvocationTargetException, InstantiationException {

        Constructor<T> constructor = clazz.getConstructor(null);
        T obj = constructor.newInstance();

        // 遍历json对象中的所有key
        for (String key : map.keySet()) {

            Field field = clazz.getDeclaredField(key);

            field.setAccessible(true);

            // 如何属性的类型为日期类型
            Class<?> dateType = DateUtils.getDateType(field.getType());
            if (dateType != null) {

                DateTime parse = DateUtil.parse(String.valueOf(map.get(key)));

                if (dateType.equals(LocalDateTime.class)) {
                    field.set(obj, parse.toLocalDateTime());
                } else if (dateType.equals(Date.class)) {
                    field.set(obj, parse.toJdkDate());
                } else if (dateType.equals(java.sql.Date.class)) {
                    field.set(obj, parse.toSqlDate());
                } else if (dateType.equals(java.sql.Timestamp.class)) {
                    field.set(obj, parse.toTimestamp());
                }


            } else {
                field.set(obj, map.get(key));
            }
        }

        return obj;
    }

    @Override
    public String toString() {
        return map.toString();
    }

}
