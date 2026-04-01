package cn.yznu.XXX;

import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.WebServlet;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Properties;

import cn.yznu.XXX.util.PrimeUtil;

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

    // 配置文件路径常量
    private static final String USER_CONFIG_PATH = "/WEB-INF/users.properties";

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
            String path = getServletContext().getRealPath(USER_CONFIG_PATH);
            // 检查配置文件是否存在
            if (path == null || !Files.exists(Paths.get(path))) {
                throw new ServletException("用户配置文件不存在: " + USER_CONFIG_PATH);
            }
            // 加载用户配置
            users.load(Files.newInputStream(Paths.get(path)));

            // 记录加载成功的日志（可选）
            System.out.println("成功加载用户配置文件，共加载 " + users.size() + " 个用户");
        } catch (IOException e) {
            e.printStackTrace();
            throw new ServletException("无法加载用户配置文件，请检查文件格式是否正确", e);
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

        // 前置校验1：检查用户名和密码是否为空
        if (username == null || username.trim().isEmpty()) {
            request.setAttribute("errorMsg", "用户名不能为空！");
            forwardToLogin(request, response);
            return;
        }

        if (password == null || password.trim().isEmpty()) {
            request.setAttribute("errorMsg", "密码不能为空！");
            forwardToLogin(request, response);
            return;
        }

        // 前置校验2：检查输入的number是否为空
        if (numberStr == null || numberStr.trim().isEmpty()) {
            request.setAttribute("errorMsg", "请输入偶数n！");
            forwardToLogin(request, response);
            return;
        }

        // 前置校验3：校验数字格式
        int n;
        try {
            n = Integer.parseInt(numberStr.trim());
        } catch (NumberFormatException e) {
            request.setAttribute("errorMsg", "请输入有效的数字！");
            forwardToLogin(request, response);
            return;
        }

        // 前置校验4：校验是否为大于100的偶数
        if (!PrimeUtil.isValidEvenNumber(n)) {
            request.setAttribute("errorMsg", "请输入大于100的偶数！");
            forwardToLogin(request, response);
            return;
        }

        // 从配置文件中获取存储的密码
        String storedPassword = users.getProperty(username.trim());

        // 验证用户名和密码是否匹配
        if (storedPassword != null && storedPassword.equals(password)) {
            // 认证成功，创建会话
            HttpSession session = request.getSession();
            session.setAttribute("username", username.trim());
            session.setAttribute("number", numberStr.trim());

            // 转发到计算Servlet继续处理
            RequestDispatcher dispatcher = request.getRequestDispatcher("CalServlet");
            dispatcher.forward(request, response);
        } else {
            // 认证失败，设置错误信息并返回登录页
            request.setAttribute("errorMsg", "用户名或密码错误！");
            forwardToLogin(request, response);
        }
    }

    /**
     * 转发到登录页面
     *
     * @param request HttpServletRequest对象
     * @param response HttpServletResponse对象
     * @throws ServletException Servlet异常
     * @throws IOException IO异常
     */
    private void forwardToLogin(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        RequestDispatcher dispatcher = request.getRequestDispatcher("login.jsp");
        dispatcher.forward(request, response);
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