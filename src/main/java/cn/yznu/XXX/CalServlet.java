package cn.yznu.XXX;

import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.WebServlet;

@WebServlet("/CalServlet")
public class CalServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);

        // 登录检查
        if (session == null || session.getAttribute("username") == null) {
            response.sendRedirect("login.jsp");
            return;
        }

        String username = (String) session.getAttribute("username");
        String numberStr = (String) session.getAttribute("number");

        if (numberStr == null) {
            response.sendRedirect("login.jsp");
            return;
        }

        int n = Integer.parseInt(numberStr);

        if (n <= 100 || n % 2 != 0) {
            request.setAttribute("errorMsg", "请输入大于100的偶数！");
            RequestDispatcher dispatcher = request.getRequestDispatcher("login.jsp");
            dispatcher.forward(request, response);
            return;
        }

        int[] primes = findTwoPrimes(n);

        if (primes != null) {
            request.setAttribute("prime1", primes[0]);
            request.setAttribute("prime2", primes[1]);
            request.setAttribute("n", n);
            request.setAttribute("username", username);

            // 转发
            RequestDispatcher dispatcher = request.getRequestDispatcher("prime/cal/show.jsp");
            dispatcher.forward(request, response);
        } else {
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

    private boolean isPrime(int num) {
        if (num <= 1) return false;
        if (num == 2) return true;
        if (num % 2 == 0) return false;

        for (int i = 3; i <= Math.sqrt(num); i += 2) {
            if (num % i == 0) return false;
        }
        return true;
    }

    private int[] findTwoPrimes(int n) {
        for (int i = 2; i <= n / 2; i++) {
            if (isPrime(i) && isPrime(n - i)) {
                return new int[]{i, n - i};
            }
        }
        return null;
    }
}