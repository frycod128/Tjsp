# 数据库适配指南

## 原理

`DbConfigLoader` 在启动时读取 `mybatis-config.xml` 中 JDBC URL 的库名，并加载 `resources/` 下**同名 JSON** 作为表/列的元数据配置。查表与列时，优先使用 JSON 中指定的中文标签，缺失项则回退查询 `information_schema`，列名原文作为标签。

因此切换数据库只需三步。

---

## 步骤

### 1. 修改 JDBC 连接

编辑 [mybatis-config.xml](src/main/resources/mybatis-config.xml)，修改 `<dataSource>` 内的四要素：

```xml
<property name="url" value="jdbc:mysql://localhost:3306/新库名?useSSL=false&amp;serverTimezone=Asia/Shanghai&amp;characterEncoding=UTF-8"/>
<property name="username" value="root"/>
<property name="password" value="你的密码"/>
```

> 库名从 URL 中自动提取，例如 `jdbc:mysql://localhost:3306/mydb?…` → 库名 `mydb`。

### 2. 创建同名 JSON 配置

在 `src/main/resources/` 下新建 `数据库库名.json`，参考示例文件：

- [headphone_sj8.json](src/main/resources/headphone_sj8.json)


| 字段 | 含义 | 必填 |
|------|------|------|
| `queryable` | `true` 在页面下拉框中出现，`false` 隐藏 | 否，默认 `true` |
| `columns` | 列名 → 页面表头标签映射 | 否，缺失列从 `information_schema` 补全，标签用原文 |
| `表名` | 整个表缺省时默认 `queryable: true`，所有列自动从 DB 获取 | 否，默认使用源名 |
| `fuzzyColumns` | 模糊查询的列的列表 | 是，此项必须且无默认值 |
### 3. 创建或转换 SQL 数据

DDL / DML 不限文件名，放在 `src/main/resources/` 下执行即可，例：

- [headphone.sql](src/main/resources/headphone.sql)

**约束**：表必须有主键或至少一个可用于 `WHERE` 的列（页面通过选择"键列 → 输入值"来查询）。

---

## 示例：切换到 `shop` 库

### `mybatis-config.xml` 改动

```diff
- <property name="url" value="jdbc:mysql://localhost:3306/headphone_sj8?…"/>
+ <property name="url" value="jdbc:mysql://localhost:3306/shop?…"/>
```

### 新建 `src/main/resources/<数据库库名>.json`

```json
{
  "product": {
    "columns": {
      "id": "商品ID",
      "name": "商品名",
      "price": "单价"
    }
  },
  "customer": {
    "queryable": false
  }
}
```

- `product` 表只展示 id/name/price 三列，标签中文化
- `customer` 表不出现于下拉框
- 其他表（如 `order`）未在 JSON 中配置 → 自动出现，所有列标签为原文
