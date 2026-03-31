package cn.yznu.XXX;

import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.WebServlet;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Properties;

@WebServlet("/LoginServlet")
public class LoginServlet extends HttpServlet {

    private Properties users = new Properties();

    @Override
    public void init() throws ServletException {
        try {
            String path = getServletContext().getRealPath("/WEB-INF/users.properties");
            users.load(Files.newInputStream(Paths.get(path)));
        } catch (IOException e) {
            e.printStackTrace();
            throw new ServletException("无法加载用户配置文件");
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");

        String username = request.getParameter("username");
        String password = request.getParameter("password");
        String numberStr = request.getParameter("number");

        String storedPassword = users.getProperty(username);

        if (storedPassword != null && storedPassword.equals(password)) {
            HttpSession session = request.getSession();
            session.setAttribute("username", username);
            session.setAttribute("number", numberStr);

            // 转发
            RequestDispatcher dispatcher = request.getRequestDispatcher("CalServlet");
            dispatcher.forward(request, response);
        } else {
            request.setAttribute("errorMsg", "请输入正确的用户名或密码！");
            RequestDispatcher dispatcher = request.getRequestDispatcher("login.jsp");
            dispatcher.forward(request, response);
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String action = request.getParameter("action");

        if ("logout".equals(action)) {
            HttpSession session = request.getSession(false);
            if (session != null) {
                session.removeAttribute("username");
                session.removeAttribute("number");
                session.invalidate();
            }
            // 重定向
            response.sendRedirect("login.jsp");
        }
    }
}