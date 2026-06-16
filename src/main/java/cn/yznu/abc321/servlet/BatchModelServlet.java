package cn.yznu.abc321.servlet;

import cn.yznu.abc321.dao.HeadphoneDao;
import cn.yznu.abc321.entity.Headphone;
import cn.yznu.abc321.util.MyBatisUtil;
import org.apache.ibatis.session.SqlSession;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@WebServlet("/batchModel")
public class BatchModelServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        String raw = req.getParameter("models");

        if (raw == null || raw.trim().isEmpty()) {
            req.setAttribute("batchMsg", "请输入至少一个商品名");
            req.getRequestDispatcher("/index.jsp").forward(req, resp);
            return;
        }

        // 按逗号/顿号/空格分割，去空去重
        List<String> models = Arrays.stream(raw.split("[,，、\\s]+"))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .distinct()
                .collect(Collectors.toList());

        if (models.isEmpty()) {
            req.setAttribute("batchMsg", "未识别到有效商品名");
            req.getRequestDispatcher("/index.jsp").forward(req, resp);
            return;
        }

        try (SqlSession session = MyBatisUtil.getSqlSessionFactory().openSession()) {
            HeadphoneDao dao = session.getMapper(HeadphoneDao.class);
            List<Headphone> list = dao.searchByModels(models);
            req.setAttribute("batchResults", list);
            req.setAttribute("batchMsg", list.isEmpty() ? "未匹配到商品" : "共 " + list.size() + " 件");
            req.setAttribute("batchInput", raw.trim());
        } catch (Exception e) {
            e.printStackTrace();
            req.setAttribute("batchMsg", "查询出错: " + e.getMessage());
        }

        req.getRequestDispatcher("/index.jsp").forward(req, resp);
    }
}
