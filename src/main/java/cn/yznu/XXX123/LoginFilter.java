package cn.yznu.XXX123;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

public class LoginFilter implements Filter {
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {}

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse resp = (HttpServletResponse) response;
        HttpSession session = req.getSession(false);

        String uri = req.getRequestURI();
        // 登录页和登录处理页不需要过滤
        if (uri.contains("login.jsp") || uri.contains("doLogin.jsp") || uri.contains("logout.jsp")) {
            chain.doFilter(request, response);
            return;
        }

        if (session != null && session.getAttribute("username") != null) {
            chain.doFilter(request, response);
        } else {
            resp.setContentType("text/html;charset=UTF-8");
            resp.getWriter().write("<script>alert('请先登录！');location.href='login.jsp';</script>");
        }
    }

    @Override
    public void destroy() {}
}