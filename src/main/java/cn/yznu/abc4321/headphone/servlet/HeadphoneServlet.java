package cn.yznu.abc4321.headphone.servlet;

import cn.yznu.abc4321.headphone.entity.Headphone;
import cn.yznu.abc4321.headphone.entity.PageInfo;
import cn.yznu.abc4321.headphone.service.HeadphoneService;
import cn.yznu.abc4321.headphone.service.impl.HeadphoneServiceImpl;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.math.BigDecimal;

@WebServlet("/headphone/*")
public class HeadphoneServlet extends HttpServlet {
    private HeadphoneService headphoneService = new HeadphoneServiceImpl();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        doPost(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        String pathInfo = req.getPathInfo();

        if (pathInfo == null || "/".equals(pathInfo)) {
            listHeadphones(req, resp);
        } else if ("/list".equals(pathInfo)) {
            listHeadphones(req, resp);
        } else if ("/add".equals(pathInfo)) {
            showAddForm(req, resp);
        } else if ("/save".equals(pathInfo)) {
            saveHeadphone(req, resp);
        } else if ("/edit".equals(pathInfo)) {
            showEditForm(req, resp);
        } else if ("/update".equals(pathInfo)) {
            updateHeadphone(req, resp);
        } else if ("/delete".equals(pathInfo)) {
            deleteHeadphone(req, resp);
        } else if ("/search".equals(pathInfo)) {
            searchHeadphones(req, resp);
        } else {
            listHeadphones(req, resp);
        }
    }

    private void listHeadphones(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        int currentPage = 1;
        int pageSize = 5;

        String pageStr = req.getParameter("currentPage");
        if (pageStr != null && !pageStr.isEmpty()) {
            currentPage = Integer.parseInt(pageStr);
        }

        PageInfo<Headphone> pageInfo = headphoneService.getHeadphonesByPage(currentPage, pageSize);
        req.setAttribute("pageInfo", pageInfo);
        req.getRequestDispatcher("/WEB-INF/headphone/headphone-list.jsp").forward(req, resp);
    }

    private void showAddForm(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        req.getRequestDispatcher("/WEB-INF/headphone/headphone-add.jsp").forward(req, resp);
    }

    private void saveHeadphone(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {
        Headphone hp = parseHeadphoneFromRequest(req);
        boolean success = headphoneService.addHeadphone(hp);
        resp.sendRedirect(req.getContextPath() + "/headphone/list");
    }

    private void showEditForm(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        Integer id = Integer.parseInt(req.getParameter("id"));
        Headphone hp = headphoneService.getHeadphoneById(id);
        req.setAttribute("headphone", hp);
        req.getRequestDispatcher("/WEB-INF/headphone/headphone-edit.jsp").forward(req, resp);
    }

    private void updateHeadphone(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {
        Headphone hp = parseHeadphoneFromRequest(req);
        hp.setId(Integer.parseInt(req.getParameter("id")));
        headphoneService.updateHeadphone(hp);
        resp.sendRedirect(req.getContextPath() + "/headphone/list");
    }

    private void deleteHeadphone(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {
        Integer id = Integer.parseInt(req.getParameter("id"));
        headphoneService.deleteHeadphone(id);
        resp.sendRedirect(req.getContextPath() + "/headphone/list");
    }

    private void searchHeadphones(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String keyword = req.getParameter("keyword");
        int currentPage = 1;
        int pageSize = 5;

        String pageStr = req.getParameter("currentPage");
        if (pageStr != null && !pageStr.isEmpty()) {
            currentPage = Integer.parseInt(pageStr);
        }

        PageInfo<Headphone> pageInfo = headphoneService.searchByModelWithPage(keyword, currentPage, pageSize);
        req.setAttribute("pageInfo", pageInfo);
        req.setAttribute("keyword", keyword);
        req.getRequestDispatcher("/WEB-INF/headphone/headphone-list.jsp").forward(req, resp);
    }

    private Headphone parseHeadphoneFromRequest(HttpServletRequest req) {
        Headphone hp = new Headphone();
        hp.setModel(req.getParameter("model"));
        hp.setBrand(req.getParameter("brand"));
        hp.setDriverSize(Double.parseDouble(req.getParameter("driverSize")));
        hp.setImpedance(Integer.parseInt(req.getParameter("impedance")));
        hp.setSensitivity(Integer.parseInt(req.getParameter("sensitivity")));
        hp.setFrequencyResponse(req.getParameter("frequencyResponse"));
        hp.setPrice(new BigDecimal(req.getParameter("price")));
        hp.setStock(Integer.parseInt(req.getParameter("stock")));
        hp.setWireless("1".equals(req.getParameter("wireless")));
        hp.setNoiseCancelling("1".equals(req.getParameter("noiseCancelling")));
        return hp;
    }
}