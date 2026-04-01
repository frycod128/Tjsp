package cn.yznu.XXX;

import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.WebServlet;

import cn.yznu.XXX.util.PrimeUtil;

/**
 * 哥德巴赫猜想计算Servlet
 * 处理质数分解的核心业务逻辑
 *
 * @author YourName
 * @version 1.0
 */
@WebServlet("/CalServlet")
public class CalServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // 获取session，不自动创建
        HttpSession session = request.getSession(false);

        // 登录检查：未登录则跳转到登录页
        if (session == null || session.getAttribute("username") == null) {
            response.sendRedirect("login.jsp");
            return;
        }

        String username = (String) session.getAttribute("username");
        String numberStr = (String) session.getAttribute("number");

        // 检查会话中是否存在数字参数
        if (numberStr == null || numberStr.trim().isEmpty()) {
            request.setAttribute("errorMsg", "请输入有效的偶数！");
            RequestDispatcher dispatcher = request.getRequestDispatcher("login.jsp");
            dispatcher.forward(request, response);
            return;
        }

        int n;
        try {
            // 尝试将字符串转换为整数
            n = Integer.parseInt(numberStr.trim());
        } catch (NumberFormatException e) {
            // 数字格式错误时的处理
            request.setAttribute("errorMsg", "请输入有效的数字！");
            RequestDispatcher dispatcher = request.getRequestDispatcher("login.jsp");
            dispatcher.forward(request, response);
            return;
        }

        // 校验输入的偶数是否有效（大于100且为偶数）
        if (!PrimeUtil.isValidEvenNumber(n)) {
            request.setAttribute("errorMsg", "请输入大于100的偶数！");
            RequestDispatcher dispatcher = request.getRequestDispatcher("login.jsp");
            dispatcher.forward(request, response);
            return;
        }

        // 调用工具类查找两个质数
        int[] primes = PrimeUtil.findTwoPrimesForGoldbach(n);

        if (primes != null) {
            // 找到质数对，设置属性并转发到结果页面
            request.setAttribute("prime1", primes[0]);
            request.setAttribute("prime2", primes[1]);
            request.setAttribute("n", n);
            request.setAttribute("username", username);

            RequestDispatcher dispatcher = request.getRequestDispatcher("prime/cal/show.jsp");
            dispatcher.forward(request, response);
        } else {
            // 未找到质数对（理论上对于大于2的偶数总是存在，但为了健壮性保留此处理）
            request.setAttribute("errorMsg", "无法找到符合条件的两个质数！");
            RequestDispatcher dispatcher = request.getRequestDispatcher("login.jsp");
            dispatcher.forward(request, response);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doGet(request, response);
    }
}