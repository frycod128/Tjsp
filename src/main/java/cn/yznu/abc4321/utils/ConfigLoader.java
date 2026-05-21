package cn.yznu.abc4321.utils;

import cn.yznu.abc4321.config.TableConfig;
import cn.yznu.abc4321.mapper.DynamicMapper;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;

import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 配置加载器 - 支持动态获取表结构
 */
public class ConfigLoader {
    private static TableConfig tableConfig;
    private static final Gson gson = new GsonBuilder().create();

    static {
        loadConfig();
    }

    private static void loadConfig() {
        // 1. 先尝试从JSON文件加载
        try (Reader reader = new InputStreamReader(
                Resources.getResourceAsStream("table-config.json"),
                StandardCharsets.UTF_8)) {
            tableConfig = gson.fromJson(reader, TableConfig.class);
            System.out.println("从配置文件加载成功: " + tableConfig.getTableName());
        } catch (Exception e) {
            System.out.println("配置文件不存在或加载失败，尝试从数据库获取: " + e.getMessage());
            // 2. 从数据库动态获取
            loadFromDatabase();
        }

        // 3. 如果都没有，使用默认配置
        if (tableConfig == null) {
            tableConfig = getDefaultConfig();
        }
    }

    private static void loadFromDatabase() {
        try (SqlSession sqlSession = MyBatisUtil.getSqlSession()) {
            DynamicMapper mapper = sqlSession.getMapper(DynamicMapper.class);

            // 从mybatis-config.xml获取表名
            String tableName = getTableNameFromConfig();
            if (tableName == null) {
                tableName = "headphone";
            }

            // 获取表的所有列
            List<Map<String, Object>> columns = mapper.getTableColumns(tableName);
            String primaryKey = mapper.getPrimaryKey(tableName);
            Map<String, Object> tableInfo = mapper.getTableInfo(tableName);

            if (columns != null && !columns.isEmpty()) {
                tableConfig = new TableConfig();
                tableConfig.setTableName(tableName);
                tableConfig.setPrimaryKey(primaryKey != null ? primaryKey : "id");

                // 动态构建列映射
                Map<String, String> columnMap = new HashMap<>();
                List<String> editableColumns = new java.util.ArrayList<>();
                List<String> searchableColumns = new java.util.ArrayList<>();
                Map<String, String> formTypes = new HashMap<>();

                for (Map<String, Object> col : columns) {
                    String colName = (String) col.get("COLUMN_NAME");
                    String comment = (String) col.get("COLUMN_COMMENT");
                    String dataType = (String) col.get("DATA_TYPE");

                    // 使用注释作为显示名，没有注释则用列名
                    String label = (comment != null && !comment.isEmpty()) ? comment : colName;
                    columnMap.put(colName, label);

                    // 排除主键和自动生成的时间戳
                    if (!colName.equals(primaryKey) && !colName.equals("create_time") && !colName.equals("update_time")) {
                        editableColumns.add(colName);
                        searchableColumns.add(colName);

                        // 根据数据类型推断表单类型
                        if (dataType.equals("int") || dataType.equals("tinyint") || dataType.equals("smallint")) {
                            // 整数类型
                        } else if (dataType.equals("decimal") || dataType.equals("float") || dataType.equals("double")) {
                            formTypes.put(colName, "number");
                        } else if (dataType.equals("tinyint") && colName.contains("is_") || colName.contains("enable")) {
                            formTypes.put(colName, "select:0=否,1=是");
                        }
                    }
                }

                tableConfig.setColumns(columnMap);
                tableConfig.setEditableColumns(editableColumns);
                tableConfig.setSearchableColumns(searchableColumns);
                tableConfig.setFormTypes(formTypes);

                System.out.println("从数据库动态加载成功: " + tableName + ", 共 " + columns.size() + " 列");
            }
        } catch (Exception e) {
            System.err.println("从数据库加载失败: " + e.getMessage());
        }
    }

    private static String getTableNameFromConfig() {
        try (Reader reader = new InputStreamReader(
                Resources.getResourceAsStream("mybatis-config.xml"),
                StandardCharsets.UTF_8)) {
            // 简单解析XML获取tableName
            java.io.BufferedReader br = new java.io.BufferedReader(reader);
            String line;
            while ((line = br.readLine()) != null) {
                if (line.contains("property name=\"tableName\"")) {
                    int start = line.indexOf("value=\"") + 7;
                    int end = line.indexOf("\"", start);
                    if (start > 7 && end > start) {
                        return line.substring(start, end);
                    }
                }
            }
        } catch (Exception e) {
            // 忽略
        }
        return null;
    }

    private static TableConfig getDefaultConfig() {
        TableConfig config = new TableConfig();
        config.setTableName("headphone");
        config.setPrimaryKey("id");
        Map<String, String> columns = new HashMap<>();
        columns.put("id", "ID");
        columns.put("model", "型号");
        columns.put("brand", "品牌");
        config.setColumns(columns);
        config.setEditableColumns(new java.util.ArrayList<>());
        config.setSearchableColumns(new java.util.ArrayList<>());
        return config;
    }

    public static TableConfig getTableConfig() {
        return tableConfig;
    }

    public static void reloadConfig() {
        loadConfig();
    }
}