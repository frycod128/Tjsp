package cn.yznu.abc321.servlet;

import cn.yznu.abc321.dao.PurchaseDao;
import cn.yznu.abc321.entity.User;
import cn.yznu.abc321.util.MyBatisUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.ibatis.session.SqlSession;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collections;
import java.util.List;

@WebServlet("/suggest")
public class SuggestServlet extends HttpServlet {

    private final ObjectMapper mapper = new ObjectMapper();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json;charset=UTF-8");

        String keyword = req.getParameter("keyword");
        if (keyword == null || keyword.trim().isEmpty()) {
            mapper.writeValue(resp.getOutputStream(), Collections.emptyList());
            return;
        }

        try (SqlSession session = MyBatisUtil.getSqlSessionFactory().openSession()) {
            PurchaseDao dao = session.getMapper(PurchaseDao.class);
            List<User> users = dao.suggestByPhone(keyword.trim());
            mapper.writeValue(resp.getOutputStream(), users);
        } catch (Exception e) {
            resp.setStatus(500);
            mapper.writeValue(resp.getOutputStream(),
                    Collections.singletonMap("error", e.getMessage()));
        }
    }
}
