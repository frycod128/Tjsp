package cn.yznu.abc321.servlet;

import cn.yznu.abc321.dao.GenericDao;
import cn.yznu.abc321.entity.Condition;
import cn.yznu.abc321.entity.ExpandableRow;
import cn.yznu.abc321.util.DbConfigLoader;
import cn.yznu.abc321.util.FkExpander;
import cn.yznu.abc321.util.MyBatisUtil;
import org.apache.ibatis.session.SqlSession;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@WebServlet(value = "/query", loadOnStartup = 1)
public class QueryServlet extends HttpServlet {

    @Override
    public void init() throws ServletException {
        List<String> tables = DbConfigLoader.getQueryableTables();
        getServletContext().log("[QueryServlet] 启动完成，可用表: " + tables);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        req.setAttribute("tables", DbConfigLoader.getQueryableTables());
        req.getRequestDispatcher("/index.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        List<String> validTables = DbConfigLoader.getQueryableTables();
        req.setAttribute("tables", validTables);

        String table = req.getParameter("table");
        if (table == null || !validTables.contains(table)) {
            req.setAttribute("msg", "请选择有效的表");
            req.getRequestDispatcher("/index.jsp").forward(req, resp);
            return;
        }

        String action = req.getParameter("action");
        Map<String, String> labels = DbConfigLoader.getColumnLabels(table);

        // ===== 模式1：单键查询（兼容旧逻辑） =====
        if (action == null || "single".equals(action)) {
            handleSingle(req, resp, table, labels);
            return;
        }

        // ===== 模式2：多字段 AND 联合查询 =====
        if ("multi".equals(action)) {
            handleMulti(req, resp, table, labels);
            return;
        }

        // ===== 模式3：批量模糊查询 =====
        if ("batch".equals(action)) {
            handleBatch(req, resp, table, labels);
            return;
        }

        req.setAttribute("tables", validTables);
        req.setAttribute("msg", "未知操作");
        req.getRequestDispatcher("/index.jsp").forward(req, resp);
    }

    // ============= 单键查询 =============

    private void handleSingle(HttpServletRequest req, HttpServletResponse resp,
                              String table, Map<String, String> labels)
            throws ServletException, IOException {
        String key = req.getParameter("key");
        String value = req.getParameter("value");

        if (key == null || key.trim().isEmpty()) {
            req.setAttribute("table", table);
            req.setAttribute("columns", labels);
            req.getRequestDispatcher("/index.jsp").forward(req, resp);
            return;
        }

        if (!DbConfigLoader.isValidColumn(table, key.trim())) {
            req.setAttribute("msg", "无效的查询键");
            req.setAttribute("table", table);
            req.setAttribute("columns", labels);
            req.getRequestDispatcher("/index.jsp").forward(req, resp);
            return;
        }

        if (value == null || value.trim().isEmpty()) {
            req.setAttribute("msg", "请输入查询值");
            req.setAttribute("table", table);
            req.setAttribute("columns", labels);
            req.getRequestDispatcher("/index.jsp").forward(req, resp);
            return;
        }

        try (SqlSession s = MyBatisUtil.getSqlSessionFactory().openSession()) {
            Map<String, Object> params = new HashMap<>();
            params.put("tableName", table);
            params.put("keyColumn", key.trim());
            params.put("keyValue", value.trim());
            List<Map<String, Object>> raw = s.getMapper(GenericDao.class).queryByKey(params);

            if (raw.isEmpty()) {
                req.setAttribute("msg", "未找到匹配记录");
                req.setAttribute("table", table);
                req.setAttribute("columns", labels);
            } else {
                FkExpander expander = new FkExpander(MyBatisUtil.getSqlSessionFactory(), 3);
                req.setAttribute("table", table);
                req.setAttribute("columns", labels);
                req.setAttribute("searchKey", key.trim());
                req.setAttribute("searchValue", value.trim());
                req.setAttribute("expandedRows", expander.expand(table, raw));
            }
        } catch (Exception e) {
            req.setAttribute("msg", "查询出错：" + e.getMessage());
            req.setAttribute("table", table);
            req.setAttribute("columns", labels);
        }
        req.getRequestDispatcher("/index.jsp").forward(req, resp);
    }

    // ============= 多字段 AND 联合查询 =============

    private void handleMulti(HttpServletRequest req, HttpServletResponse resp,
                             String table, Map<String, String> labels)
            throws ServletException, IOException {
        List<String> colNames = DbConfigLoader.getColumnNames(table);
        List<Condition> conditions = new ArrayList<>();

        for (String col : colNames) {
            String val = req.getParameter("c_" + col);
            if (val != null && !val.trim().isEmpty()) {
                boolean fuzzy = "on".equals(req.getParameter("f_" + col));
                conditions.add(new Condition(col, val.trim(), fuzzy));
            }
        }

        if (conditions.isEmpty()) {
            req.setAttribute("msg", "请至少填写一个查询条件");
            req.setAttribute("table", table);
            req.setAttribute("columns", labels);
            req.setAttribute("showMulti", true);
            req.getRequestDispatcher("/index.jsp").forward(req, resp);
            return;
        }

        try (SqlSession s = MyBatisUtil.getSqlSessionFactory().openSession()) {
            GenericDao dao = s.getMapper(GenericDao.class);
            List<Map<String, Object>> raw = dao.dynamicQuery(table, conditions);

            if (raw.isEmpty()) {
                req.setAttribute("msg", "未找到匹配记录");
                req.setAttribute("table", table);
                req.setAttribute("columns", labels);
                req.setAttribute("showMulti", true);
            } else {
                FkExpander expander = new FkExpander(MyBatisUtil.getSqlSessionFactory(), 3);
                req.setAttribute("table", table);
                req.setAttribute("columns", labels);
                req.setAttribute("conditions", conditions);
                req.setAttribute("expandedRows", expander.expand(table, raw));
            }
        } catch (Exception e) {
            req.setAttribute("msg", "查询出错：" + e.getMessage());
            req.setAttribute("table", table);
            req.setAttribute("columns", labels);
            req.setAttribute("showMulti", true);
        }
        req.getRequestDispatcher("/index.jsp").forward(req, resp);
    }

    // ============= 批量模糊查询 =============

    private void handleBatch(HttpServletRequest req, HttpServletResponse resp,
                             String table, Map<String, String> labels)
            throws ServletException, IOException {
        String column = req.getParameter("fuzzyColumn");
        String rawValues = req.getParameter("fuzzyValues");

        if (column == null || column.trim().isEmpty()) {
            req.setAttribute("msg", "请选择模糊查询列");
            req.setAttribute("table", table);
            req.setAttribute("columns", labels);
            req.setAttribute("showBatch", true);
            req.getRequestDispatcher("/index.jsp").forward(req, resp);
            return;
        }

        if (!DbConfigLoader.isValidColumn(table, column.trim())) {
            req.setAttribute("msg", "无效的查询列");
            req.setAttribute("table", table);
            req.setAttribute("columns", labels);
            req.setAttribute("showBatch", true);
            req.getRequestDispatcher("/index.jsp").forward(req, resp);
            return;
        }

        if (rawValues == null || rawValues.trim().isEmpty()) {
            req.setAttribute("msg", "请输入查询值（多个用逗号分隔）");
            req.setAttribute("table", table);
            req.setAttribute("columns", labels);
            req.setAttribute("showBatch", true);
            req.getRequestDispatcher("/index.jsp").forward(req, resp);
            return;
        }

        List<String> values = Arrays.stream(rawValues.split("[,，]"))
                .map(String::trim)
                .filter(v -> !v.isEmpty())
                .collect(Collectors.toList());

        if (values.isEmpty()) {
            req.setAttribute("msg", "请输入有效的查询值");
            req.setAttribute("table", table);
            req.setAttribute("columns", labels);
            req.setAttribute("showBatch", true);
            req.getRequestDispatcher("/index.jsp").forward(req, resp);
            return;
        }

        try (SqlSession s = MyBatisUtil.getSqlSessionFactory().openSession()) {
            GenericDao dao = s.getMapper(GenericDao.class);
            List<Map<String, Object>> raw = dao.batchFuzzyQuery(table, column.trim(), values);

            if (raw.isEmpty()) {
                req.setAttribute("msg", "未找到匹配记录");
                req.setAttribute("table", table);
                req.setAttribute("columns", labels);
                req.setAttribute("showBatch", true);
            } else {
                FkExpander expander = new FkExpander(MyBatisUtil.getSqlSessionFactory(), 3);
                req.setAttribute("table", table);
                req.setAttribute("columns", labels);
                req.setAttribute("fuzzyColumn", column.trim());
                req.setAttribute("fuzzyValues", rawValues.trim());
                req.setAttribute("expandedRows", expander.expand(table, raw));
            }
        } catch (Exception e) {
            req.setAttribute("msg", "查询出错：" + e.getMessage());
            req.setAttribute("table", table);
            req.setAttribute("columns", labels);
            req.setAttribute("showBatch", true);
        }
        req.getRequestDispatcher("/index.jsp").forward(req, resp);
    }
}
