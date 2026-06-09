package cn.yznu.abc321.util;

import cn.yznu.abc321.dao.GenericDao;
import cn.yznu.abc321.entity.TableMeta;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.ibatis.session.SqlSession;

import java.io.InputStream;
import java.util.*;

/**
 * 加载 {dbName}.json，缺失的表/列回退查 information_schema。
 */
public class DbConfigLoader {
    private static final String DB_NAME = "headphone_sj8";
    private static final Map<String, TableMeta> config = new LinkedHashMap<>();
    private static final ObjectMapper mapper = new ObjectMapper();

    static {
        InputStream is = DbConfigLoader.class.getClassLoader()
                .getResourceAsStream(DB_NAME + ".json");
        if (is != null) {
            try {
                Map<String, TableMeta> loaded = mapper.readValue(is,
                        new TypeReference<Map<String, TableMeta>>() {});
                config.putAll(loaded);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /** 所有可查询的表名（JSON中 queryable=true + 数据库中未在JSON出现的表） */
    public static List<String> getQueryableTables() {
        Set<String> result = new LinkedHashSet<>();
        for (Map.Entry<String, TableMeta> e : config.entrySet()) {
            if (e.getValue().isQueryable()) result.add(e.getKey());
        }
        for (String t : fetchTablesFromDb()) {
            if (!config.containsKey(t)) result.add(t);   // 未配置 → 默认可查
        }
        return new ArrayList<>(result);
    }

    /** 指定表的 列名→标签 映射 */
    public static Map<String, String> getColumnLabels(String tableName) {
        TableMeta meta = config.get(tableName);
        if (meta != null && !meta.getColumns().isEmpty()) {
            return meta.getColumns();
        }
        return fetchColumnsFromDb(tableName);
    }

    public static String getDbName() { return DB_NAME; }

    // ---------- 数据库回退 ----------

    private static List<String> fetchTablesFromDb() {
        try (SqlSession s = MyBatisUtil.getSqlSessionFactory().openSession()) {
            return s.getMapper(GenericDao.class).getTables(DB_NAME);
        } catch (Exception e) {
            return Collections.emptyList();
        }
    }

    private static Map<String, String> fetchColumnsFromDb(String tableName) {
        Map<String, String> cols = new LinkedHashMap<>();
        try (SqlSession s = MyBatisUtil.getSqlSessionFactory().openSession()) {
            for (String c : s.getMapper(GenericDao.class).getColumns(DB_NAME, tableName)) {
                cols.put(c, c);
            }
        } catch (Exception ignored) {}
        return cols;
    }
}
