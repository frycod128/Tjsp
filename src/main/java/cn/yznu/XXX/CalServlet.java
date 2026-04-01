package cn.yznu.XXX;

import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.WebServlet;

import cn.yznu.XXX.util.PrimeUtil;

/**
 * 哥德巴赫猜想计算Servlet
 * 处理质数分解的核心业务逻辑
 * 注意：输入校验已在LoginServlet中完成，本Servlet专注于业务逻辑
 *
 * @author YourName
 * @version 2.0
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

        // 检查会话中是否存在数字参数（防御性编程）
        if (numberStr == null || numberStr.trim().isEmpty()) {
            request.setAttribute("errorMsg", "会话已过期，请重新登录！");
            RequestDispatcher dispatcher = request.getRequestDispatcher("login.jsp");
            dispatcher.forward(request, response);
            return;
        }

        // 转换数字（此时应该已经是有效数字，由LoginServlet保证）
        int n = Integer.parseInt(numberStr.trim());

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
            request.setAttribute("errorMsg", "无法找到符合条件的两个质数，请尝试其他数字！");
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