package cn.yznu.abc4321.common.servlet;

import cn.yznu.abc4321.common.entity.BaseEntity;
import cn.yznu.abc4321.common.service.BaseService;
import cn.yznu.abc4321.common.util.ColumnMappingLoader;
import cn.yznu.abc4321.common.util.ReflectUtil;
import cn.yznu.abc4321.headphone.entity.PageInfo;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;

public abstract class BaseServlet<T extends BaseEntity> extends HttpServlet {

    protected abstract BaseService<T> getService();
    protected abstract String getEntityName();        // 如 "headphone"
    protected abstract String getViewPath();          // 如 "/WEB-INF/headphone/"
    protected abstract T createEntity();

    private Class<T> entityClass;

    @SuppressWarnings("unchecked")
    @Override
    public void init() throws ServletException {
        Type superClass = getClass().getGenericSuperclass();
        if (superClass instanceof ParameterizedType) {
            ParameterizedType pt = (ParameterizedType) superClass;
            entityClass = (Class<T>) pt.getActualTypeArguments()[0];
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        doPost(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        String action = req.getParameter("action");
        if (action == null) action = "list";

        switch (action) {
            case "list": list(req, resp); break;
            case "add": showAdd(req, resp); break;
            case "save": save(req, resp); break;
            case "edit": showEdit(req, resp); break;
            case "update": update(req, resp); break;
            case "delete": delete(req, resp); break;
            case "search": search(req, resp); break;
            default: list(req, resp);
        }
    }

    protected void list(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        int currentPage = 1;
        int pageSize = 5;
        String pageStr = req.getParameter("currentPage");
        if (pageStr != null && !pageStr.isEmpty()) {
            currentPage = Integer.parseInt(pageStr);
        }

        PageInfo<T> pageInfo = getService().getByPage(currentPage, pageSize);

        // 加载列信息(含映射)
        loadColumnInfo(req);

        req.setAttribute("pageInfo", pageInfo);
        req.setAttribute("entityName", getEntityName());
        req.getRequestDispatcher(getViewPath() + "list.jsp").forward(req, resp);
    }

    protected void showAdd(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        loadColumnInfo(req);
        req.setAttribute("entityName", getEntityName());
        req.getRequestDispatcher(getViewPath() + "add.jsp").forward(req, resp);
    }

    protected void save(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {
        T entity = parseRequest(req);
        getService().add(entity);
        resp.sendRedirect(req.getContextPath() + "/" + getEntityName() + "?action=list");
    }

    protected void showEdit(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        Object id = req.getParameter("id");
        T entity = getService().getById(id);
        loadColumnInfo(req);
        req.setAttribute("entity", entity);
        req.setAttribute("entityName", getEntityName());
        req.getRequestDispatcher(getViewPath() + "edit.jsp").forward(req, resp);
    }

    protected void update(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {
        T entity = parseRequest(req);
        entity.setPrimaryKeyValue(req.getParameter("id"));
        getService().update(entity);
        resp.sendRedirect(req.getContextPath() + "/" + getEntityName() + "?action=list");
    }

    protected void delete(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {
        Object id = req.getParameter("id");
        getService().delete(id);
        resp.sendRedirect(req.getContextPath() + "/" + getEntityName() + "?action=list");
    }

    protected void search(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String keyword = req.getParameter("keyword");
        String searchColumn = req.getParameter("searchColumn");
        int currentPage = 1;
        int pageSize = 5;
        String pageStr = req.getParameter("currentPage");
        if (pageStr != null && !pageStr.isEmpty()) {
            currentPage = Integer.parseInt(pageStr);
        }

        PageInfo<T> pageInfo = getService().searchByColumn(
                searchColumn, keyword, true, currentPage, pageSize);

        loadColumnInfo(req);
        req.setAttribute("pageInfo", pageInfo);
        req.setAttribute("keyword", keyword);
        req.setAttribute("searchColumn", searchColumn);
        req.setAttribute("entityName", getEntityName());
        req.getRequestDispatcher(getViewPath() + "list.jsp").forward(req, resp);
    }

    /**
     * 加载列信息(数据库元数据 + 映射配置)
     */
    protected void loadColumnInfo(HttpServletRequest req) {
        // 获取数据库列元数据
        List<Map<String, Object>> columns = getService().getColumns();

        // 获取映射配置
        Map<String, String> mapping = ColumnMappingLoader.loadMapping(
                createEntity().getTableName(), getEntityName());

        // 构建显示列信息
        List<Map<String, String>> displayColumns = new ArrayList<>();
        for (Map<String, Object> col : columns) {
            Map<String, String> displayCol = new HashMap<>();
            String columnName = (String) col.get("columnName");
            displayCol.put("columnName", columnName);

            // 显示名称优先级：配置文件映射 > 数据库注释 > 列名
            String displayName = mapping.getOrDefault(columnName,
                    col.get("comment") != null && !((String) col.get("comment")).isEmpty()
                            ? (String) col.get("comment") : columnName);
            displayCol.put("displayName", displayName);
            displayCol.put("isPrimaryKey", String.valueOf(col.get("isPrimaryKey")));
            displayColumns.add(displayCol);
        }

        req.setAttribute("columns", displayColumns);
    }

    /**
     * 从请求参数解析实体对象(通过反射自动映射)
     */
    protected T parseRequest(HttpServletRequest req) {
        T entity = createEntity();
        // 获取数据库列信息
        List<Map<String, Object>> columns = getService().getColumns();
        Set<String> columnNames = new HashSet<>();
        for (Map<String, Object> col : columns) {
            if (!(Boolean) col.get("isPrimaryKey")) {
                columnNames.add((String) col.get("columnName"));
            }
        }

        Field[] fields = ReflectUtil.getAllFields(entityClass);
        Map<String, Field> columnFieldMap = new HashMap<>();
        for (Field field : fields) {
            String columnName = ReflectUtil.camelToSnake(field.getName());
            if (columnNames.contains(columnName)) {
                columnFieldMap.put(columnName, field);
            }
        }

        for (Map.Entry<String, Field> entry : columnFieldMap.entrySet()) {
            String columnName = entry.getKey();
            Field field = entry.getValue();
            String value = req.getParameter(columnName);
            if (value != null && !value.isEmpty()) {
                try {
                    field.setAccessible(true);
                    setFieldValueFromString(field, entity, value);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return entity;
    }

    private void setFieldValueFromString(Field field, Object entity, String value)
            throws IllegalAccessException {
        Class<?> type = field.getType();
        if (type == String.class) {
            field.set(entity, value);
        } else if (type == Integer.class || type == int.class) {
            field.set(entity, Integer.parseInt(value));
        } else if (type == Long.class || type == long.class) {
            field.set(entity, Long.parseLong(value));
        } else if (type == Double.class || type == double.class) {
            field.set(entity, Double.parseDouble(value));
        } else if (type == BigDecimal.class) {
            field.set(entity, new BigDecimal(value));
        } else if (type == Boolean.class || type == boolean.class) {
            field.set(entity, "1".equals(value) || "true".equalsIgnoreCase(value));
        } else {
            field.set(entity, value);
        }
    }
}