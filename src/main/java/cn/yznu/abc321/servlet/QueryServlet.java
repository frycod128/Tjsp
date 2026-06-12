package cn.yznu.abc321.servlet;

import cn.yznu.abc321.dao.GenericDao;
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

        String key = req.getParameter("key");
        String value = req.getParameter("value");

        // 未选键 → 展示键选择界面
        if (key == null || key.trim().isEmpty()) {
            req.setAttribute("table", table);
            req.setAttribute("columns", DbConfigLoader.getColumnLabels(table));
            req.getRequestDispatcher("/index.jsp").forward(req, resp);
            return;
        }

        if (!DbConfigLoader.isValidColumn(table, key.trim())) {
            req.setAttribute("msg", "无效的查询键");
            req.setAttribute("table", table);
            req.setAttribute("columns", DbConfigLoader.getColumnLabels(table));
            req.getRequestDispatcher("/index.jsp").forward(req, resp);
            return;
        }

        if (value == null || value.trim().isEmpty()) {
            req.setAttribute("msg", "请输入查询值");
            req.setAttribute("table", table);
            req.setAttribute("columns", DbConfigLoader.getColumnLabels(table));
            req.getRequestDispatcher("/index.jsp").forward(req, resp);
            return;
        }

        try (SqlSession session = MyBatisUtil.getSqlSessionFactory().openSession()) {
            GenericDao dao = session.getMapper(GenericDao.class);
            Map<String, Object> params = new HashMap<>();
            params.put("tableName", table);
            params.put("keyColumn", key.trim());
            params.put("keyValue", value.trim());

            List<Map<String, Object>> raw = dao.queryByKey(params);

            if (raw.isEmpty()) {
                req.setAttribute("msg", "未找到匹配记录");
                req.setAttribute("table", table);
                req.setAttribute("columns", DbConfigLoader.getColumnLabels(table));
            } else {
                // 外键展开（深度3）
                FkExpander expander = new FkExpander(MyBatisUtil.getSqlSessionFactory(), 3);
                List<ExpandableRow> expanded = expander.expand(table, raw);

                req.setAttribute("table", table);
                req.setAttribute("columns", DbConfigLoader.getColumnLabels(table));
                req.setAttribute("searchKey", key.trim());
                req.setAttribute("searchValue", value.trim());
                req.setAttribute("expandedRows", expanded);
            }
        } catch (Exception e) {
            req.setAttribute("msg", "查询出错：" + e.getMessage());
            req.setAttribute("table", table);
            req.setAttribute("columns", DbConfigLoader.getColumnLabels(table));
        }

        req.getRequestDispatcher("/index.jsp").forward(req, resp);
    }
}
