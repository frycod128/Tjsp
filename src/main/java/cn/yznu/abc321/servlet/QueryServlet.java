package cn.yznu.abc321.servlet;

import cn.yznu.abc321.entity.PurchaseRecord;
import cn.yznu.abc321.service.UserOrderService;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@WebServlet("/query")
public class QueryServlet extends HttpServlet {

    private final UserOrderService service = new UserOrderService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String phone = req.getParameter("phone");

        if (phone == null || phone.trim().isEmpty()) {
            req.setAttribute("msg", "请输入手机号码");
            req.getRequestDispatcher("/index.jsp").forward(req, resp);
            return;
        }

        phone = phone.trim();
        List<PurchaseRecord> records = service.queryByPhone(phone);

        req.setAttribute("phone", phone);   // 无论有无结果都回显

        if (records.isEmpty()) {
            req.setAttribute("msg", "未找到该手机号对应的购买记录");
        } else {
            req.setAttribute("records", records);
        }

        req.getRequestDispatcher("/index.jsp").forward(req, resp);
    }
}
