package cn.yznu.XXX;

import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.WebServlet;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Properties;

/**
 * 登录处理Servlet
 * 负责用户认证和会话管理
 *
 * @author YourName
 * @version 1.0
 */
@WebServlet("/LoginServlet")
public class LoginServlet extends HttpServlet {

    // 存储用户信息的Properties对象
    private Properties users = new Properties();

    /**
     * Servlet初始化方法
     * 加载用户配置文件到内存中
     *
     * @throws ServletException 如果无法加载配置文件
     */
    @Override
    public void init() throws ServletException {
        try {
            // 获取配置文件的真实路径
            String path = getServletContext().getRealPath("/WEB-INF/users.properties");
            // 加载用户配置
            users.load(Files.newInputStream(Paths.get(path)));
        } catch (IOException e) {
            e.printStackTrace();
            throw new ServletException("无法加载用户配置文件");
        }
    }

    /**
     * 处理POST请求：用户登录
     * 验证用户名和密码，验证通过后创建会话并转发到计算Servlet
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // 设置请求编码，避免中文乱码
        request.setCharacterEncoding("UTF-8");

        // 获取表单参数
        String username = request.getParameter("username");
        String password = request.getParameter("password");
        String numberStr = request.getParameter("number");

        // 从配置文件中获取存储的密码
        String storedPassword = users.getProperty(username);

        // 验证用户名和密码是否匹配
        if (storedPassword != null && storedPassword.equals(password)) {
            // 认证成功，创建会话
            HttpSession session = request.getSession();
            session.setAttribute("username", username);
            session.setAttribute("number", numberStr);

            // 转发到计算Servlet继续处理
            RequestDispatcher dispatcher = request.getRequestDispatcher("CalServlet");
            dispatcher.forward(request, response);
        } else {
            // 认证失败，设置错误信息并返回登录页
            request.setAttribute("errorMsg", "请输入正确的用户名或密码！");
            RequestDispatcher dispatcher = request.getRequestDispatcher("login.jsp");
            dispatcher.forward(request, response);
        }
    }

    /**
     * 处理GET请求：退出登录
     * 销毁会话并重定向到登录页
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String action = request.getParameter("action");

        // 处理退出登录请求
        if ("logout".equals(action)) {
            HttpSession session = request.getSession(false);
            if (session != null) {
                // 清除会话属性
                session.removeAttribute("username");
                session.removeAttribute("number");
                // 使会话无效
                session.invalidate();
            }
            // 重定向到登录页面
            response.sendRedirect("login.jsp");
        }
    }
}