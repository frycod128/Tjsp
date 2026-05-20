package cn.yznu.abc4321.controller;

import cn.yznu.abc4321.entity.Headphone;
import cn.yznu.abc4321.mapper.HeadphoneMapper;
import cn.yznu.abc4321.utils.MyBatisUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.ibatis.session.SqlSession;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@WebServlet("/api/headphone/*")
public class HeadphoneController extends HttpServlet {

    private ObjectMapper objectMapper = new ObjectMapper();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String pathInfo = req.getPathInfo();
        resp.setContentType("application/json;charset=UTF-8");
        PrintWriter out = resp.getWriter();

        try (SqlSession sqlSession = MyBatisUtil.getSqlSession()) {
            HeadphoneMapper mapper = sqlSession.getMapper(HeadphoneMapper.class);
            Map<String, Object> result = new HashMap<>();

            if (pathInfo == null || "/".equals(pathInfo) || "/all".equals(pathInfo)) {
                // 查询所有
                List<Headphone> list = mapper.findAll();
                result.put("code", 200);
                result.put("data", list);
                result.put("msg", "查询成功");
            } else if (pathInfo.equals("/wireless")) {
                // 查询无线耳机
                List<Headphone> list = mapper.findWirelessHeadphones();
                result.put("code", 200);
                result.put("data", list);
                result.put("msg", "查询无线耳机成功");
            } else if (pathInfo.equals("/noise")) {
                // 查询降噪耳机
                List<Headphone> list = mapper.findNoiseCancellingHeadphones();
                result.put("code", 200);
                result.put("data", list);
                result.put("msg", "查询降噪耳机成功");
            } else if (pathInfo.equals("/brands")) {
                // 统计各品牌
                List<Map<String, Object>> list = mapper.countByBrand();
                result.put("code", 200);
                result.put("data", list);
                result.put("msg", "统计成功");
            } else if (pathInfo.matches("/\\d+")) {
                // 根据ID查询
                int id = Integer.parseInt(pathInfo.substring(1));
                Headphone headphone = mapper.findById(id);
                if (headphone != null) {
                    result.put("code", 200);
                    result.put("data", headphone);
                    result.put("msg", "查询成功");
                } else {
                    result.put("code", 404);
                    result.put("msg", "耳机不存在");
                }
            } else {
                // 条件查询
                String brand = req.getParameter("brand");
                if (brand != null && !brand.isEmpty()) {
                    List<Headphone> list = mapper.findByBrand(brand);
                    result.put("code", 200);
                    result.put("data", list);
                    result.put("msg", "按品牌查询成功");
                } else {
                    result.put("code", 400);
                    result.put("msg", "无效的请求");
                }
            }
            out.write(objectMapper.writeValueAsString(result));
        } catch (Exception e) {
            e.printStackTrace();
            Map<String, Object> error = new HashMap<>();
            error.put("code", 500);
            error.put("msg", "服务器错误: " + e.getMessage());
            out.write(objectMapper.writeValueAsString(error));
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        resp.setContentType("application/json;charset=UTF-8");
        PrintWriter out = resp.getWriter();
        Map<String, Object> result = new HashMap<>();

        try (SqlSession sqlSession = MyBatisUtil.getSqlSession(false)) {
            HeadphoneMapper mapper = sqlSession.getMapper(HeadphoneMapper.class);

            // 解析JSON数据
            Headphone headphone = objectMapper.readValue(req.getInputStream(), Headphone.class);

            int rows = mapper.insert(headphone);
            sqlSession.commit();

            if (rows > 0) {
                result.put("code", 200);
                result.put("data", headphone);
                result.put("msg", "新增成功");
            } else {
                result.put("code", 500);
                result.put("msg", "新增失败");
            }
        } catch (Exception e) {
            e.printStackTrace();
            result.put("code", 500);
            result.put("msg", "服务器错误: " + e.getMessage());
        }
        out.write(objectMapper.writeValueAsString(result));
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        resp.setContentType("application/json;charset=UTF-8");
        PrintWriter out = resp.getWriter();
        Map<String, Object> result = new HashMap<>();

        try (SqlSession sqlSession = MyBatisUtil.getSqlSession(false)) {
            HeadphoneMapper mapper = sqlSession.getMapper(HeadphoneMapper.class);

            Headphone headphone = objectMapper.readValue(req.getInputStream(), Headphone.class);

            int rows = mapper.update(headphone);
            sqlSession.commit();

            if (rows > 0) {
                result.put("code", 200);
                result.put("msg", "更新成功");
            } else {
                result.put("code", 500);
                result.put("msg", "更新失败");
            }
        } catch (Exception e) {
            e.printStackTrace();
            result.put("code", 500);
            result.put("msg", "服务器错误: " + e.getMessage());
        }
        out.write(objectMapper.writeValueAsString(result));
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        resp.setContentType("application/json;charset=UTF-8");
        PrintWriter out = resp.getWriter();
        String pathInfo = req.getPathInfo();
        Map<String, Object> result = new HashMap<>();

        try (SqlSession sqlSession = MyBatisUtil.getSqlSession(false)) {
            HeadphoneMapper mapper = sqlSession.getMapper(HeadphoneMapper.class);

            if (pathInfo != null && pathInfo.matches("/\\d+")) {
                int id = Integer.parseInt(pathInfo.substring(1));
                int rows = mapper.deleteById(id);
                sqlSession.commit();

                if (rows > 0) {
                    result.put("code", 200);
                    result.put("msg", "删除成功");
                } else {
                    result.put("code", 404);
                    result.put("msg", "耳机不存在");
                }
            } else {
                result.put("code", 400);
                result.put("msg", "无效的请求");
            }
        } catch (Exception e) {
            e.printStackTrace();
            result.put("code", 500);
            result.put("msg", "服务器错误: " + e.getMessage());
        }
        out.write(objectMapper.writeValueAsString(result));
    }
}