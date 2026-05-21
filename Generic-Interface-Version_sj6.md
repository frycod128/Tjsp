# 分支操作说明

## 系统简介

本系统是一个**完全通用的数据库管理工具**，无需编写任何代码，只需配置即可对任意无外键的数据库表进行增删改查操作。

---

## 快速开始

### 1. 准备数据库表

确保你的数据库中有要管理的表。系统会自动识别表结构，无需手动定义字段。

### 2. 修改数据库连接

编辑 [`src/main/resources/mybatis-config.xml`](/src/main/resources/mybatis-config.xml)，修改以下配置：

```xml
<properties>
    <property name="url" value="jdbc:mysql://localhost:3306/你的数据库名?..."/>
    <property name="username" value="你的用户名"/>
    <property name="password" value="你的密码"/>
    <property name="tableName" value="你的表名"/>
</properties>
```

### 3. （可选）配置显示名称

编辑 [`src/main/resources/table-config.json`](/src/main/resources/table-config.json)，为字段设置中文显示名：

```json
{
  "tableName": "你的表名",
  "primaryKey": "id",
  "columns": {
    "字段名1": "显示名称1",
    "字段名2": "显示名称2"
  },
  "editableColumns": ["字段名1", "字段名2"],
  "searchableColumns": ["字段名1", "字段名2"],
  "formTypes": {
    "字段名": "number",
    "状态字段": "select:0=否,1=是"
  }
}
```

**如果不提供此文件**，系统会自动从数据库读取字段注释作为显示名。

---

## formTypes 支持的格式

| 格式 | 说明 | 示例 |
|------|------|------|
| `number` | 数字输入框 | `"price": "number"` |
| `textarea` | 多行文本 | `"desc": "textarea"` |
| `date` | 日期选择器 | `"birth": "date"` |
| `select:值=文本,值=文本` | 下拉选择框 | `"status": "select:0=禁用,1=启用"` |

---

## 使用界面

### 主界面布局

```
┌─────────────────────────────────────────────┐
│           数据管理系统                        │
├─────────────────────────────────────────────┤
│ ▼ 查询                                        │
│   查询字段: [下拉选择]  关键字: [输入框]       │
│   [查询] [显示全部]                           │
├─────────────────────────────────────────────┤
│ ▼ 新增                                        │
│   字段1: [输入框]  字段2: [输入框]            │
│   [保存] [清空]                               │
├─────────────────────────────────────────────┤
│ 共 X 条记录                    [刷新]         │
│ ┌─────┬───────┬─────┬─────┬─────┐           │
│ │ ID  │ 字段1 │字段2│字段3│操作 │           │
│ ├─────┼───────┼─────┼─────┼─────┤           │
│ │ 1   │ xxx   │ xxx │ xxx │编辑删除│           │
│ └─────┴───────┴─────┴─────┴─────┘           │
└─────────────────────────────────────────────┘
```

### 功能说明

#### 查询数据

1. 点击 **「▼ 查询」** 展开查询面板
2. 选择查询字段和关键字
3. 点击 **「查询」** 按钮
4. 点击 **「显示全部」** 可重置查询

#### 新增数据

1. 点击 **「▼ 新增」** 展开表单面板
2. 填写各个字段
3. 点击 **「保存」** 按钮
4. 点击 **「清空」** 可重置表单

#### 修改数据

1. 在数据表格中找到要修改的记录
2. 点击该行的 **「编辑」** 按钮
3. 表单面板会自动展开并显示当前数据
4. 修改字段内容
5. 点击 **「保存」** 按钮
6. 点击 **「取消」** 可放弃修改

#### 删除数据

1. 在数据表格中找到要删除的记录
2. 点击该行的 **「删除」** 按钮
3. 确认删除

#### 刷新数据

点击右上角的 **「刷新」** 按钮可重新加载数据

---

## 相关文件

| 文件 | 说明 |
|------|------|
| [pom.xml](/pom.xml) | Maven项目配置 |
| [src/main/resources/mybatis-config.xml](/src/main/resources/mybatis-config.xml) | MyBatis主配置 |
| [src/main/resources/table-config.json](/src/main/resources/table-config.json) | 表显示配置 |
| [src/main/resources/headphone.sql](/src/main/resources/headphone.sql) | 示例表SQL |
| [src/main/webapp/index.jsp](/src/main/webapp/index.jsp) | 前端页面 |
| [src/main/java/cn/yznu/abc4321/controller/DynamicController.java](/src/main/java/cn/yznu/abc4321/controller/DynamicController.java) | 后端控制器 |
| [src/main/java/cn/yznu/abc4321/mapper/DynamicMapper.java](/src/main/java/cn/yznu/abc4321/mapper/DynamicMapper.java) | 数据访问接口 |
| [src/main/java/cn/yznu/abc4321/mapper/DynamicMapper.xml](/src/main/java/cn/yznu/abc4321/mapper/DynamicMapper.xml) | SQL映射文件 |
| [src/main/java/cn/yznu/abc4321/utils/MyBatisUtil.java](/src/main/java/cn/yznu/abc4321/utils/MyBatisUtil.java) | MyBatis工具类 |
| [src/main/java/cn/yznu/abc4321/utils/ConfigLoader.java](/src/main/java/cn/yznu/abc4321/utils/ConfigLoader.java) | 配置加载器 |
| [src/main/java/cn/yznu/abc4321/config/TableConfig.java](/src/main/java/cn/yznu/abc4321/config/TableConfig.java) | 配置实体类 |

---

# Generic-Interface-Version_sj6