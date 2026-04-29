package cn.yznu.abc4321.common.util;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 列名映射加载器
 * 优先加载特定表的映射配置，没有则使用数据库列的注释，最后回退到列名本身
 */
public class ColumnMappingLoader {

    private static final Map<String, Map<String, String>> CACHE = new ConcurrentHashMap<>();

    /**
     * 加载表的列名映射
     * @param tableName 表名
     * @param className 类名(用于查找配置文件)
     * @return 列名 -> 显示名称 的映射
     */
    public static Map<String, String> loadMapping(String tableName, String className) {
        String cacheKey = tableName + "_" + className;
        if (CACHE.containsKey(cacheKey)) {
            return CACHE.get(cacheKey);
        }

        Map<String, String> mapping = new HashMap<>();

        // 1. 尝试加载特定表的映射配置文件
        String configPath = className.toLowerCase() + "-mapping.json";
        try (InputStream is = ColumnMappingLoader.class.getClassLoader()
                .getResourceAsStream(configPath)) {
            if (is != null) {
                try (InputStreamReader reader = new InputStreamReader(is, "UTF-8")) {
                    JsonObject json = JsonParser.parseReader(reader).getAsJsonObject();
                    if (json.has("columns")) {
                        JsonObject cols = json.getAsJsonObject("columns");
                        cols.entrySet().forEach(entry -> {
                            mapping.put(entry.getKey(), entry.getValue().getAsString());
                        });
                    }
                }
            }
        } catch (Exception e) {
            // 配置文件不存在，忽略
        }

        CACHE.put(cacheKey, mapping);
        return mapping;
    }
}