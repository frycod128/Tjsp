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
                List<Headphone> list = mapper.findAll();
                result.put("code", 200);
                result.put("data", list);
                result.put("msg", "success");
            } else if (pathInfo.matches("/\\d+")) {
                int id = Integer.parseInt(pathInfo.substring(1));
                Headphone headphone = mapper.findById(id);
                if (headphone != null) {
                    result.put("code", 200);
                    result.put("data", headphone);
                    result.put("msg", "success");
                } else {
                    result.put("code", 404);
                    result.put("msg", "not found");
                }
            } else {
                result.put("code", 400);
                result.put("msg", "invalid request");
            }
            out.write(objectMapper.writeValueAsString(result));
        } catch (Exception e) {
            e.printStackTrace();
            Map<String, Object> error = new HashMap<>();
            error.put("code", 500);
            error.put("msg", e.getMessage());
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
            Headphone headphone = objectMapper.readValue(req.getInputStream(), Headphone.class);

            int rows = mapper.insert(headphone);
            sqlSession.commit();

            if (rows > 0) {
                result.put("code", 200);
                result.put("data", headphone);
                result.put("msg", "success");
            } else {
                result.put("code", 500);
                result.put("msg", "insert failed");
            }
        } catch (Exception e) {
            e.printStackTrace();
            result.put("code", 500);
            result.put("msg", e.getMessage());
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

            if (headphone.getId() == null) {
                result.put("code", 400);
                result.put("msg", "id required");
            } else {
                int rows = mapper.update(headphone);
                sqlSession.commit();
                if (rows > 0) {
                    result.put("code", 200);
                    result.put("msg", "success");
                } else {
                    result.put("code", 404);
                    result.put("msg", "record not found");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            result.put("code", 500);
            result.put("msg", e.getMessage());
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
                    result.put("msg", "success");
                } else {
                    result.put("code", 404);
                    result.put("msg", "not found");
                }
            } else {
                result.put("code", 400);
                result.put("msg", "invalid request");
            }
        } catch (Exception e) {
            e.printStackTrace();
            result.put("code", 500);
            result.put("msg", e.getMessage());
        }
        out.write(objectMapper.writeValueAsString(result));
    }
}