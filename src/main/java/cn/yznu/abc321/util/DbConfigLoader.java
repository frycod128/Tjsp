package cn.yznu.abc321.util;

import cn.yznu.abc321.dao.GenericDao;
import cn.yznu.abc321.entity.TableMeta;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.ibatis.session.SqlSession;

import java.io.InputStream;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DbConfigLoader {

    private static final String DB_NAME = extractDbName();
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

    // ---------- 公开 API ----------

    public static String getDbName() { return DB_NAME; }

    /** 可查询的表名列表 */
    public static List<String> getQueryableTables() {
        Set<String> result = new LinkedHashSet<>();
        for (Map.Entry<String, TableMeta> e : config.entrySet()) {
            if (e.getValue().isQueryable()) result.add(e.getKey());
        }
        for (String t : fetchTablesFromDb()) {
            if (!config.containsKey(t)) result.add(t);
        }
        return new ArrayList<>(result);
    }

    /** 指定表的列名→标签 */
    public static Map<String, String> getColumnLabels(String tableName) {
        TableMeta meta = config.get(tableName);
        if (meta != null && !meta.getColumns().isEmpty()) {
            return meta.getColumns();
        }
        return fetchColumnsFromDb(tableName);
    }

    /** 指定表的所有列名 */
    public static List<String> getColumnNames(String tableName) {
        return new ArrayList<>(getColumnLabels(tableName).keySet());
    }

    /** 校验列名是否合法 */
    public static boolean isValidColumn(String tableName, String column) {
        return getColumnNames(tableName).contains(column);
    }

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

    // ---------- 从 mybatis-config.xml 解析库名 ----------

    private static String extractDbName() {
        try (InputStream is = DbConfigLoader.class.getClassLoader()
                .getResourceAsStream("mybatis-config.xml")) {
            if (is == null) return "headphone_sj8";
            String xml = new String(is.readAllBytes(), java.nio.charset.StandardCharsets.UTF_8);
            // 匹配 JDBC URL 中的数据库名
            Matcher m = Pattern.compile(
                    "jdbc:mysql://[^/]+/([^?&]+)").matcher(xml);
            if (m.find()) return m.group(1);
        } catch (Exception ignored) {}
        return "headphone_sj8";   // 兜底
    }
}
