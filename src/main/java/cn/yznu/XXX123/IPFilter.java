package cn.yznu.XXX123;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class IPFilter implements Filter {
    // 黑名单IP列表，至少包含两个IP地址
    private List<String> blacklist = Arrays.asList("192.168.1.100", "10.0.0.50");

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        System.out.println("IP过滤器初始化完成");
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse resp = (HttpServletResponse) response;

        // 获取客户端IP地址
        String clientIp = getClientIp(req);
        System.out.println("访问IP: " + clientIp);

        // 检查IP是否在黑名单中
        if (blacklist.contains(clientIp)) {
            resp.setContentType("text/html;charset=UTF-8");
            resp.getWriter().write("<script>alert('您的IP地址已被禁止访问！');location.href='login.jsp';</script>");
            return;
        }

        // IP不在黑名单，放行请求
        chain.doFilter(request, response);
    }

    // 获取真实客户端IP（考虑代理情况）
    private String getClientIp(HttpServletRequest request) {
        String ip = request.getHeader("x-forwarded-for");
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        return ip;
    }

    @Override
    public void destroy() {
        System.out.println("IP过滤器销毁");
    }
}