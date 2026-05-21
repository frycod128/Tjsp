package cn.yznu.abc4321.controller;

import cn.yznu.abc4321.config.TableConfig;
import cn.yznu.abc4321.mapper.DynamicMapper;
import cn.yznu.abc4321.utils.ConfigLoader;
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
import java.util.*;

@WebServlet("/api/*")
public class DynamicController extends HttpServlet {

    private ObjectMapper objectMapper = new ObjectMapper();

    // 从配置中获取表名和主键
    private String getTableName() {
        TableConfig config = ConfigLoader.getTableConfig();
        return config != null && config.getTableName() != null ? config.getTableName() : "headphone";
    }

    private String getPrimaryKey() {
        TableConfig config = ConfigLoader.getTableConfig();
        return config != null && config.getPrimaryKey() != null ? config.getPrimaryKey() : "id";
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String pathInfo = req.getPathInfo();
        resp.setContentType("application/json;charset=UTF-8");
        PrintWriter out = resp.getWriter();

        String tableName = getTableName();
        String primaryKey = getPrimaryKey();

        try (SqlSession sqlSession = MyBatisUtil.getSqlSession()) {
            DynamicMapper mapper = sqlSession.getMapper(DynamicMapper.class);
            Map<String, Object> result = new HashMap<>();
            TableConfig config = ConfigLoader.getTableConfig();

            if (pathInfo == null || "/".equals(pathInfo) || "/all".equals(pathInfo)) {
                // 查询所有
                List<Map<String, Object>> list = mapper.findAll(tableName, primaryKey);
                result.put("code", 200);
                result.put("data", list);
                result.put("msg", "success");
            } else if (pathInfo.equals("/config")) {
                // 获取配置信息（用于前端渲染）
                result.put("code", 200);
                result.put("data", getClientConfig(config));
                result.put("msg", "success");
            } else if (pathInfo.equals("/columns")) {
                // 获取表结构（从数据库实时获取）
                List<Map<String, Object>> columns = mapper.getTableColumns(tableName);
                result.put("code", 200);
                result.put("data", columns);
                result.put("msg", "success");
            } else if (pathInfo.equals("/info")) {
                // 获取表信息
                Map<String, Object> info = mapper.getTableInfo(tableName);
                result.put("code", 200);
                result.put("data", info);
                result.put("msg", "success");
            } else if (pathInfo.equals("/search")) {
                // 搜索
                String field = req.getParameter("field");
                String keyword = req.getParameter("keyword");
                if (field != null && keyword != null && !keyword.isEmpty()) {
                    List<Map<String, Object>> list = mapper.search(tableName, primaryKey, field, keyword);
                    result.put("code", 200);
                    result.put("data", list);
                    result.put("msg", "success");
                } else {
                    List<Map<String, Object>> list = mapper.findAll(tableName, primaryKey);
                    result.put("code", 200);
                    result.put("data", list);
                    result.put("msg", "success");
                }
            } else if (pathInfo != null && pathInfo.matches("/\\d+")) {
                // 根据ID查询
                Object id = Integer.parseInt(pathInfo.substring(1));
                Map<String, Object> entity = mapper.findById(tableName, primaryKey, id);
                if (entity != null) {
                    result.put("code", 200);
                    result.put("data", entity);
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

        String tableName = getTableName();
        String primaryKey = getPrimaryKey();

        try (SqlSession sqlSession = MyBatisUtil.getSqlSession(false)) {
            DynamicMapper mapper = sqlSession.getMapper(DynamicMapper.class);
            @SuppressWarnings("unchecked")
            Map<String, Object> data = objectMapper.readValue(req.getInputStream(), Map.class);

            // 过滤掉null值和空字符串，去掉主键字段（让数据库自动生成）
            Map<String, Object> insertData = new HashMap<>();
            for (Map.Entry<String, Object> entry : data.entrySet()) {
                String key = entry.getKey();
                Object value = entry.getValue();
                if (value != null && !value.toString().isEmpty() && !key.equals(primaryKey)) {
                    insertData.put(key, value);
                }
            }

            int rows = mapper.insert(tableName, primaryKey, insertData);
            // 获取自动生成的主键值
            Long generatedId = mapper.getLastInsertId();
            if (generatedId != null) {
                insertData.put(primaryKey, generatedId);
            }
            sqlSession.commit();

            if (rows > 0) {
                result.put("code", 200);
                result.put("data", insertData);
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

        String tableName = getTableName();
        String primaryKey = getPrimaryKey();

        try (SqlSession sqlSession = MyBatisUtil.getSqlSession(false)) {
            DynamicMapper mapper = sqlSession.getMapper(DynamicMapper.class);
            @SuppressWarnings("unchecked")
            Map<String, Object> data = objectMapper.readValue(req.getInputStream(), Map.class);

            if (data.get(primaryKey) == null) {
                result.put("code", 400);
                result.put("msg", primaryKey + " required");
            } else {
                int rows = mapper.update(tableName, primaryKey, data);
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

        String tableName = getTableName();
        String primaryKey = getPrimaryKey();

        try (SqlSession sqlSession = MyBatisUtil.getSqlSession(false)) {
            DynamicMapper mapper = sqlSession.getMapper(DynamicMapper.class);

            if (pathInfo != null && pathInfo.matches("/\\d+")) {
                Object id = Integer.parseInt(pathInfo.substring(1));
                int rows = mapper.deleteById(tableName, primaryKey, id);
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

    private Map<String, Object> getClientConfig(TableConfig config) {
        Map<String, Object> clientConfig = new HashMap<>();
        clientConfig.put("tableName", config.getTableName());
        clientConfig.put("primaryKey", config.getPrimaryKey());
        clientConfig.put("columns", config.getColumns());
        clientConfig.put("editableColumns", config.getEditableColumns());
        clientConfig.put("formTypes", config.getFormTypes());
        clientConfig.put("searchableColumns", config.getSearchableColumns());
        return clientConfig;
    }
}