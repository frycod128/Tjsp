package cn.yznu.abc4321;

import cn.yznu.abc4321.config.TableConfig;
import cn.yznu.abc4321.mapper.DynamicMapper;
import cn.yznu.abc4321.utils.ConfigLoader;
import cn.yznu.abc4321.utils.MyBatisUtil;
import org.apache.ibatis.session.SqlSession;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

public class DynamicTest {

    private SqlSession sqlSession;
    private DynamicMapper mapper;
    private String tableName;
    private String primaryKey;

    @Before
    public void setUp() {
        sqlSession = MyBatisUtil.getSqlSession(false);
        mapper = sqlSession.getMapper(DynamicMapper.class);
        TableConfig config = ConfigLoader.getTableConfig();
        tableName = config.getTableName();
        primaryKey = config.getPrimaryKey();
        System.out.println("================== 开始测试 ==================");
        System.out.println("当前表: " + tableName);
        System.out.println("主键: " + primaryKey);
    }

    @After
    public void tearDown() {
        if (sqlSession != null) {
            sqlSession.close();
        }
        System.out.println("================== 测试结束 ==================\n");
    }

    @Test
    public void testFindAll() {
        System.out.println("测试: 查询所有记录");
        List<Map<String, Object>> list = mapper.findAll(tableName, primaryKey);
        assertNotNull(list);
        assertTrue(list.size() > 0);
        System.out.println("共查询到 " + list.size() + " 条记录");
        for (Map<String, Object> row : list) {
            System.out.println(row);
        }
    }

    @Test
    public void testFindById() {
        System.out.println("测试: 根据ID查询");
        Map<String, Object> entity = mapper.findById(tableName, primaryKey, 1);
        assertNotNull(entity);
        System.out.println("查询结果: " + entity);
    }

    @Test
    public void testSearch() {
        System.out.println("测试: 搜索功能");
        List<Map<String, Object>> list = mapper.search(tableName, primaryKey, "brand", "Sony");
        assertNotNull(list);
        System.out.println("搜索到 " + list.size() + " 条记录");
        for (Map<String, Object> row : list) {
            System.out.println(row);
        }
    }

    @Test
    public void testInsert() {
        System.out.println("测试: 新增记录");
        Map<String, Object> data = new HashMap<>();
        data.put("model", "TestModel");
        data.put("brand", "TestBrand");
        data.put("price", 999.00);
        data.put("stock", 10);

        int rows = mapper.insert(tableName, primaryKey, data);
        // 获取生成的ID
        Long generatedId = mapper.getLastInsertId();
        if (generatedId != null) {
            data.put(primaryKey, generatedId);
        }
        sqlSession.commit();
        assertEquals(1, rows);
        System.out.println("新增成功，ID: " + data.get(primaryKey));

        // 清理
        if (data.get(primaryKey) != null) {
            mapper.deleteById(tableName, primaryKey, data.get(primaryKey));
            sqlSession.commit();
            System.out.println("测试数据已清理");
        }
    }

    @Test
    public void testUpdate() {
        System.out.println("测试: 更新记录");
        Map<String, Object> entity = mapper.findById(tableName, primaryKey, 1);
        assertNotNull(entity);

        Object oldPrice = entity.get("price");
        entity.put("price", 9999.00);

        int rows = mapper.update(tableName, primaryKey, entity);
        sqlSession.commit();
        assertEquals(1, rows);
        System.out.println("更新成功");

        // 恢复
        entity.put("price", oldPrice);
        mapper.update(tableName, primaryKey, entity);
        sqlSession.commit();
        System.out.println("数据已恢复");
    }

    @Test
    public void testDelete() {
        System.out.println("测试: 删除记录");
        // 先插入
        Map<String, Object> data = new HashMap<>();
        data.put("model", "TempTest");
        data.put("brand", "Temp");
        data.put("price", 100.00);

        mapper.insert(tableName, primaryKey, data);
        Long generatedId = mapper.getLastInsertId();
        if (generatedId != null) {
            data.put(primaryKey, generatedId);
        }
        sqlSession.commit();

        Object id = data.get(primaryKey);
        assertNotNull(id);

        // 删除
        int rows = mapper.deleteById(tableName, primaryKey, id);
        sqlSession.commit();
        assertEquals(1, rows);
        System.out.println("删除成功");
    }

    @Test
    public void testPageQuery() {
        System.out.println("测试: 分页查询");
        long total = mapper.getTotalCount(tableName);
        System.out.println("总记录数: " + total);

        List<Map<String, Object>> page = mapper.findByPage(tableName, primaryKey, 0, 3);
        assertEquals(3, page.size());
        System.out.println("第1页数据:");
        for (Map<String, Object> row : page) {
            System.out.println(row);
        }
    }

    @Test
    public void testGetTableColumns() {
        System.out.println("测试: 获取表结构");
        List<Map<String, Object>> columns = mapper.getTableColumns(tableName);
        assertNotNull(columns);
        System.out.println("表字段列表:");
        for (Map<String, Object> col : columns) {
            System.out.println("  - " + col.get("COLUMN_NAME") + " (" + col.get("COLUMN_COMMENT") + ") [" + col.get("DATA_TYPE") + "]");
        }
    }

    @Test
    public void testGetTableInfo() {
        System.out.println("测试: 获取表信息");
        Map<String, Object> info = mapper.getTableInfo(tableName);
        assertNotNull(info);
        System.out.println("表信息: " + info);
    }

    @Test
    public void testConfig() {
        System.out.println("测试: 配置加载");
        TableConfig config = ConfigLoader.getTableConfig();
        assertNotNull(config);
        System.out.println("表名: " + config.getTableName());
        System.out.println("主键: " + config.getPrimaryKey());
        System.out.println("字段映射:");
        if (config.getColumns() != null) {
            for (Map.Entry<String, String> entry : config.getColumns().entrySet()) {
                System.out.println("  " + entry.getKey() + " -> " + entry.getValue());
            }
        }
    }

    @Test
    public void testCrudComplete() {
        System.out.println("测试: 完整CRUD流程");

        // 1. 创建
        Map<String, Object> newData = new HashMap<>();
        newData.put("model", "CRUD Test Model");
        newData.put("brand", "CRUD Test Brand");
        newData.put("price", 888.00);
        newData.put("stock", 20);

        int insertRows = mapper.insert(tableName, primaryKey, newData);
        Long generatedId = mapper.getLastInsertId();
        if (generatedId != null) {
            newData.put(primaryKey, generatedId);
        }
        sqlSession.commit();
        assertEquals(1, insertRows);
        Object newId = newData.get(primaryKey);
        System.out.println("1. 创建成功, ID=" + newId);

        // 2. 读取
        Map<String, Object> readData = mapper.findById(tableName, primaryKey, newId);
        assertNotNull(readData);
        assertEquals("CRUD Test Model", readData.get("model"));
        System.out.println("2. 读取成功: " + readData);

        // 3. 更新
        readData.put("price", 999.00);
        readData.put("stock", 30);
        int updateRows = mapper.update(tableName, primaryKey, readData);
        sqlSession.commit();
        assertEquals(1, updateRows);
        System.out.println("3. 更新成功");

        // 4. 验证更新
        Map<String, Object> updatedData = mapper.findById(tableName, primaryKey, newId);
        assertEquals(999.00, ((Number) updatedData.get("price")).doubleValue(), 0.01);
        assertEquals(30, updatedData.get("stock"));
        System.out.println("4. 验证更新成功");

        // 5. 删除
        int deleteRows = mapper.deleteById(tableName, primaryKey, newId);
        sqlSession.commit();
        assertEquals(1, deleteRows);
        System.out.println("5. 删除成功");

        // 6. 验证删除
        Map<String, Object> deletedData = mapper.findById(tableName, primaryKey, newId);
        assertNull(deletedData);
        System.out.println("6. 验证删除成功");
    }
}