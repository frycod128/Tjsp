# Generic-Interface-Version_sj5
## 分支说明：

## 快速接入新表（以 headphone 表为例）

### 前提条件
- 数据库表已创建，**无外键约束**
- 主键为自增整数类型（建议 `INT AUTO_INCREMENT`）

---

## 操作步骤

### 1. 创建实体类

新建实体类，实现 `BaseEntity` 接口。**必须**实现 4 个方法：`getTableName()`、`getPrimaryKeyColumn()`、`getPrimaryKeyValue()`、`setPrimaryKeyValue()`。

**字段命名规则**：Java 字段使用驼峰命名，对应数据库下划线列名（如 `driverSize` → `driver_size`）。

**参考文件**：[Headphone.java](src/main/java/cn/yznu/abc4321/headphone/entity/Headphone.java)

```java
public class YourEntity implements BaseEntity {
    private Integer id;           // 主键
    private String someField;     // 对应数据库 some_field 列
    // ... 其他字段及 getter/setter

    @Override
    public String getTableName() { return "your_table_name"; }

    @Override
    public String getPrimaryKeyColumn() { return "id"; }

    @Override
    public Object getPrimaryKeyValue() { return id; }

    @Override
    public void setPrimaryKeyValue(Object value) {
        this.id = ((Number) value).intValue();
    }
}
```

---

### 2. 创建 DAO 接口和实现

DAO 接口继承 `BaseDao<YourEntity>`，DAO 实现类继承 `BaseDaoImpl<YourEntity>` 并实现该接口。

**两文件均无需编写任何方法体。**

**参考文件**：
- [HeadphoneDao.java](src/main/java/cn/yznu/abc4321/headphone/dao/HeadphoneDao.java)
- [HeadphoneDaoImpl.java](src/main/java/cn/yznu/abc4321/headphone/dao/impl/HeadphoneDaoImpl.java)

```java
// DAO 接口
public interface YourDao extends BaseDao<YourEntity> { }

// DAO 实现
public class YourDaoImpl extends BaseDaoImpl<YourEntity> implements YourDao { }
```

---

### 3. 创建 Service 接口和实现

Service 接口继承 `BaseService<YourEntity>`，Service 实现类继承 `BaseServiceImpl<YourEntity>` 并实现该接口。

Service 实现类需要**重写 `getDao()` 方法**，返回步骤 2 的 DAO 实例。

**参考文件**：
- [HeadphoneService.java](src/main/java/cn/yznu/abc4321/headphone/service/HeadphoneService.java)
- [HeadphoneServiceImpl.java](src/main/java/cn/yznu/abc4321/headphone/service/impl/HeadphoneServiceImpl.java)

```java
// Service 接口
public interface YourService extends BaseService<YourEntity> { }

// Service 实现
public class YourServiceImpl extends BaseServiceImpl<YourEntity> implements YourService {
    private YourDao yourDao = new YourDaoImpl();

    @Override
    protected BaseDao<YourEntity> getDao() { return yourDao; }
}
```

---

### 4. 创建 Servlet 控制器

Servlet 继承 `BaseServlet<YourEntity>`，使用 `@WebServlet` 注解指定访问路径。

需要**重写 4 个方法**：`getService()`、`getEntityName()`、`getViewPath()`、`createEntity()`。

**参考文件**：[HeadphoneServlet.java](src/main/java/cn/yznu/abc4321/headphone/servlet/HeadphoneServlet.java)

```java
@WebServlet("/yourentity")
public class YourServlet extends BaseServlet<YourEntity> {
    private YourService service = new YourServiceImpl();

    @Override
    protected BaseService<YourEntity> getService() { return service; }

    @Override
    protected String getEntityName() { return "yourentity"; }

    @Override
    protected String getViewPath() { return "/WEB-INF/yourentity/"; }

    @Override
    protected YourEntity createEntity() { return new YourEntity(); }
}
```

---

### 5. 配置列名映射（可选）

在 `src/main/resources/` 下创建 `{entityName}-mapping.json` 文件，将数据库列名映射为中文显示名。

**不创建此文件时，自动使用数据库列注释作为表头；若注释也为空，则直接显示列名。**

**参考文件**：[headphone-mapping.json](src/main/resources/headphone-mapping.json)

```json
{
  "columns": {
    "id": "编号",
    "some_column": "某字段中文名"
  }
}
```

---

## 完成

启动项目，访问：`http://localhost:8080/{entityName}?action=list`

功能包括：
- 分页列表（每页 5 条）
- 按任意列模糊搜索
- 添加记录
- 编辑记录
- 删除记录

---

## 修改分页条数

在 `list()` 和 `search()` 方法中，将 `int pageSize = 5;` 改为其他数值即可。

**文件位置**：[BaseServlet.java](src/main/java/cn/yznu/abc4321/common/servlet/BaseServlet.java) 第 60 行附近。
