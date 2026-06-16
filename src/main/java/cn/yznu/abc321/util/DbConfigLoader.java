package cn.yznu.abc321.util;

import cn.yznu.abc321.dao.GenericDao;
import cn.yznu.abc321.entity.FkInfo;
import cn.yznu.abc321.entity.TableMeta;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.ibatis.session.SqlSession;

import java.io.InputStream;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DbConfigLoader {

    private static final String DB_NAME = extractDbName();
    private static final Map<String, TableMeta> config = new LinkedHashMap<>();
    private static final Map<String, List<FkInfo>> outgoingCache = new HashMap<>();
    private static final Map<String, List<FkInfo>> incomingCache = new HashMap<>();
    private static final ObjectMapper mapper = new ObjectMapper();

    static {
        InputStream is = DbConfigLoader.class.getClassLoader()
                .getResourceAsStream(DB_NAME + ".json");
        if (is != null) {
            try {
                JsonNode root = mapper.readTree(is);
                for (Iterator<String> it = root.fieldNames(); it.hasNext(); ) {
                    String table = it.next();
                    JsonNode tn = root.get(table);
                    TableMeta meta = new TableMeta();
                    if (tn.has("queryable"))
                        meta.setQueryable(tn.get("queryable").asBoolean());

                    // columns: 值可以是字符串 "标签" 或对象 {"label":"标签"}
                    Map<String, String> cols = new LinkedHashMap<>();
                    JsonNode cn = tn.get("columns");
                    if (cn != null) {
                        for (Iterator<String> ci = cn.fieldNames(); ci.hasNext(); ) {
                            String col = ci.next();
                            JsonNode cv = cn.get(col);
                            if (cv.isTextual())
                                cols.put(col, cv.asText());
                            else if (cv.has("label"))
                                cols.put(col, cv.get("label").asText());
                            else
                                cols.put(col, col);
                        }
                    }
                    meta.setColumns(cols);

                    // fuzzyColumns
                    List<String> fcs = new ArrayList<>();
                    JsonNode fn = tn.get("fuzzyColumns");
                    if (fn != null && fn.isArray()) {
                        for (JsonNode f : fn) fcs.add(f.asText());
                    }
                    meta.setFuzzyColumns(fcs);
                    config.put(table, meta);
                }
            } catch (Exception e) {
                throw new RuntimeException("解析 " + DB_NAME + ".json 失败", e);
            }
        }
    }

    // ============= 公开 API =============

    public static String getDbName() { return DB_NAME; }

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

    /** 列名→标签 */
    public static Map<String, String> getColumnLabels(String tableName) {
        TableMeta meta = config.get(tableName);
        if (meta != null && !meta.getColumns().isEmpty()) return meta.getColumns();
        return fetchColumnsFromDb(tableName);
    }

    public static List<String> getColumnNames(String tableName) {
        return new ArrayList<>(getColumnLabels(tableName).keySet());
    }

    public static boolean isValidColumn(String tableName, String column) {
        return getColumnNames(tableName).contains(column);
    }

    /** 适合批量模糊查询的列；未配置则返回所有列 */
    public static List<String> getFuzzyColumns(String tableName) {
        TableMeta meta = config.get(tableName);
        if (meta != null && !meta.getFuzzyColumns().isEmpty())
            return meta.getFuzzyColumns();
        return getColumnNames(tableName);
    }

    public static String getColumnLabel(String tableName, String column) {
        return getColumnLabels(tableName).getOrDefault(column, column);
    }

    public static List<FkInfo> getOutgoingFks(String tableName) {
        return outgoingCache.computeIfAbsent(tableName, t -> {
            try (SqlSession s = MyBatisUtil.getSqlSessionFactory().openSession()) {
                return s.getMapper(GenericDao.class).getOutgoingFks(DB_NAME, t);
            }
        });
    }

    public static List<FkInfo> getIncomingFks(String tableName) {
        return incomingCache.computeIfAbsent(tableName, t -> {
            try (SqlSession s = MyBatisUtil.getSqlSessionFactory().openSession()) {
                return s.getMapper(GenericDao.class).getIncomingFks(DB_NAME, t);
            }
        });
    }

    // ============= 私有 =============

    private static List<String> fetchTablesFromDb() {
        try (SqlSession s = MyBatisUtil.getSqlSessionFactory().openSession()) {
            return s.getMapper(GenericDao.class).getTables(DB_NAME);
        } catch (Exception e) { return Collections.emptyList(); }
    }

    private static Map<String, String> fetchColumnsFromDb(String tableName) {
        Map<String, String> cols = new LinkedHashMap<>();
        try (SqlSession s = MyBatisUtil.getSqlSessionFactory().openSession()) {
            for (String c : s.getMapper(GenericDao.class).getColumns(DB_NAME, tableName))
                cols.put(c, c);
        } catch (Exception ignored) {}
        return cols;
    }

    private static String extractDbName() {
        try (InputStream is = DbConfigLoader.class.getClassLoader()
                .getResourceAsStream("mybatis-config.xml")) {
            if (is == null)
                throw new RuntimeException("未找到 mybatis-config.xml");
            String xml = new String(is.readAllBytes(), java.nio.charset.StandardCharsets.UTF_8);
            Matcher m = Pattern.compile("jdbc:mysql://[^/]+/([^?&]+)").matcher(xml);
            if (m.find()) return m.group(1);
            throw new RuntimeException("未能从 mybatis-config.xml 解析数据库名");
        } catch (RuntimeException e) { throw e;
        } catch (Exception e) { throw new RuntimeException("读取 mybatis-config.xml 失败", e); }
    }
}
