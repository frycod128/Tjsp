package cn.yznu.abc321.servlet;

import cn.yznu.abc321.dao.GenericDao;
import cn.yznu.abc321.util.DbConfigLoader;
import cn.yznu.abc321.util.MyBatisUtil;
import org.apache.ibatis.session.SqlSession;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.util.*;

@WebServlet("/query")
public class QueryServlet extends HttpServlet {

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

        Map<String, String> columnLabels = DbConfigLoader.getColumnLabels(table);
        String[] cols = req.getParameterValues("cols");

        // 无列选择 → 显示列勾选界面
        if (cols == null || cols.length == 0) {
            req.setAttribute("table", table);
            req.setAttribute("columns", columnLabels);
            req.getRequestDispatcher("/index.jsp").forward(req, resp);
            return;
        }

        // 白名单过滤列名
        List<String> safeCols = new ArrayList<>();
        for (String c : cols) {
            if (columnLabels.containsKey(c.trim())) safeCols.add(c.trim());
        }
        if (safeCols.isEmpty()) {
            req.setAttribute("msg", "请至少选择一个有效列");
            req.setAttribute("table", table);
            req.setAttribute("columns", columnLabels);
            req.getRequestDispatcher("/index.jsp").forward(req, resp);
            return;
        }

        // 执行查询
        try (SqlSession session = MyBatisUtil.getSqlSessionFactory().openSession()) {
            GenericDao dao = session.getMapper(GenericDao.class);
            Map<String, Object> params = new HashMap<>();
            params.put("tableName", table);
            params.put("columns", String.join(", ", safeCols));

            List<Map<String, Object>> results = dao.queryTable(params);
            req.setAttribute("table", table);
            req.setAttribute("columns", columnLabels);
            req.setAttribute("selectedCols", safeCols);
            req.setAttribute("results", results);
        } catch (Exception e) {
            req.setAttribute("msg", "查询出错：" + e.getMessage());
            req.setAttribute("table", table);
            req.setAttribute("columns", columnLabels);
        }

        req.getRequestDispatcher("/index.jsp").forward(req, resp);
    }
}
