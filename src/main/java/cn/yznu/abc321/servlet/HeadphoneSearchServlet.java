package cn.yznu.abc321.servlet;

import cn.yznu.abc321.dao.HeadphoneDao;
import cn.yznu.abc321.entity.Headphone;
import cn.yznu.abc321.entity.HeadphoneSearchCriteria;
import cn.yznu.abc321.util.MyBatisUtil;
import org.apache.ibatis.session.SqlSession;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

@WebServlet("/headphoneSearch")
public class HeadphoneSearchServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        HeadphoneSearchCriteria c = new HeadphoneSearchCriteria();

        // 文本字段
        String model = req.getParameter("model");
        if (model != null && !model.trim().isEmpty()) c.setModel(model.trim());

        String brand = req.getParameter("brand");
        if (brand != null && !brand.trim().isEmpty()) c.setBrand(brand.trim());

        String freq = req.getParameter("frequencyResponse");
        if (freq != null && !freq.trim().isEmpty()) c.setFrequencyResponse(freq.trim());

        // 数值字段，解析失败则忽略
        try { String v = req.getParameter("driverSize");
            if (v != null && !v.isEmpty()) c.setDriverSize(Double.parseDouble(v)); } catch (NumberFormatException ignored) {}
        try { String v = req.getParameter("impedance");
            if (v != null && !v.isEmpty()) c.setImpedance(Integer.parseInt(v)); } catch (NumberFormatException ignored) {}
        try { String v = req.getParameter("sensitivity");
            if (v != null && !v.isEmpty()) c.setSensitivity(Integer.parseInt(v)); } catch (NumberFormatException ignored) {}
        try { String v = req.getParameter("priceMin");
            if (v != null && !v.isEmpty()) c.setPriceMin(new BigDecimal(v)); } catch (NumberFormatException ignored) {}
        try { String v = req.getParameter("priceMax");
            if (v != null && !v.isEmpty()) c.setPriceMax(new BigDecimal(v)); } catch (NumberFormatException ignored) {}
        try { String v = req.getParameter("stock");
            if (v != null && !v.isEmpty()) c.setStock(Integer.parseInt(v)); } catch (NumberFormatException ignored) {}

        // 无线/降噪：null=不限, 0=否, 1=是
        String wireless = req.getParameter("wireless");
        if (wireless != null && !wireless.isEmpty()) c.setWireless(Integer.parseInt(wireless));

        String nc = req.getParameter("noiseCancelling");
        if (nc != null && !nc.isEmpty()) c.setNoiseCancelling(Integer.parseInt(nc));

        try (SqlSession session = MyBatisUtil.getSqlSessionFactory().openSession()) {
            HeadphoneDao dao = session.getMapper(HeadphoneDao.class);
            List<Headphone> list = dao.dynamicSearch(c);
            req.setAttribute("headphones", list);
            req.setAttribute("headphoneMsg", list.isEmpty() ? "未匹配到商品" : "共 " + list.size() + " 件");
        } catch (Exception e) {
            e.printStackTrace();
            req.setAttribute("headphoneMsg", "查询出错: " + e.getMessage());
        }

        req.getRequestDispatcher("/index.jsp").forward(req, resp);
    }
}
