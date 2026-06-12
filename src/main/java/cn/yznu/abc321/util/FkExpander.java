package cn.yznu.abc321.util;

import cn.yznu.abc321.dao.GenericDao;
import cn.yznu.abc321.entity.ExpandableRow;
import cn.yznu.abc321.entity.FkInfo;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;

import java.util.*;

public class FkExpander {

    private final SqlSessionFactory ssf;
    private final int maxDepth;

    public FkExpander(SqlSessionFactory ssf, int maxDepth) {
        this.ssf = ssf;
        this.maxDepth = maxDepth;
    }

    public List<ExpandableRow> expand(String tableName, List<Map<String, Object>> rows) {
        return expand(tableName, rows, 0, new HashSet<>());
    }

    private List<ExpandableRow> expand(String tableName, List<Map<String, Object>> rows,
                                       int depth, Set<String> visitedAncestors) {
        if (depth >= maxDepth || rows == null || rows.isEmpty())
            return Collections.emptyList();

        Set<String> visited = new HashSet<>(visitedAncestors);
        visited.add(tableName);

        Map<String, String> labels = DbConfigLoader.getColumnLabels(tableName);
        List<FkInfo> outgoing = DbConfigLoader.getOutgoingFks(tableName);
        List<FkInfo> incoming = DbConfigLoader.getIncomingFks(tableName);

        List<ExpandableRow> result = new ArrayList<>();
        for (Map<String, Object> row : rows) {
            ExpandableRow er = new ExpandableRow();
            er.setTableName(tableName);
            er.setColumnLabels(labels);
            er.setRowData(row);

            // 1) 本表外键→父表，父表若已访问则跳过
            for (FkInfo fk : outgoing) {
                if (visited.contains(fk.getReferencedTable())) continue;
                Object val = row.get(fk.getColumnName());
                if (val == null) continue;
                List<Map<String, Object>> parentRows = queryByKey(
                        fk.getReferencedTable(), fk.getReferencedColumn(), val);
                if (!parentRows.isEmpty()) {
                    String desc = fk.getReferencedTable() + " (←" + fk.getColumnName() + ")";
                    er.getChildren().put(desc,
                            expand(fk.getReferencedTable(), parentRows, depth + 1, visited));
                }
            }

            // 2) 子表外键→本表，子表若已访问则跳过
            Object pkVal = row.get("id");
            if (pkVal != null) {
                for (FkInfo fk : incoming) {
                    String childTable = fk.getReferencedTable();
                    if (visited.contains(childTable)) continue;
                    List<Map<String, Object>> childRows = queryByKey(
                            childTable, fk.getColumnName(), pkVal);
                    if (!childRows.isEmpty()) {
                        String desc = childTable + " (→" + fk.getColumnName() + ")";
                        er.getChildren().put(desc,
                                expand(childTable, childRows, depth + 1, visited));
                    }
                }
            }

            result.add(er);
        }
        return result;
    }

    private List<Map<String, Object>> queryByKey(String table, String column, Object value) {
        try (SqlSession s = ssf.openSession()) {
            GenericDao dao = s.getMapper(GenericDao.class);
            Map<String, Object> params = new HashMap<>();
            params.put("tableName", table);
            params.put("keyColumn", column);
            params.put("keyValue", value);
            return dao.queryByKey(params);
        } catch (Exception e) {
            return Collections.emptyList();
        }
    }
}
