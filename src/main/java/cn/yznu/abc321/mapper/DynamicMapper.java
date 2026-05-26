package cn.yznu.abc321.mapper;

import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * 动态Mapper接口 - 完全通用，不依赖任何具体表结构
 */
public interface DynamicMapper {

    // 查询所有
    List<Map<String, Object>> findAll(@Param("tableName") String tableName,
                                      @Param("primaryKey") String primaryKey);

    // 根据ID查询
    Map<String, Object> findById(@Param("tableName") String tableName,
                                 @Param("primaryKey") String primaryKey,
                                 @Param("id") Object id);

    // 搜索
    List<Map<String, Object>> search(@Param("tableName") String tableName,
                                     @Param("primaryKey") String primaryKey,
                                     @Param("field") String field,
                                     @Param("keyword") String keyword);

    // 分页查询
    List<Map<String, Object>> findByPage(@Param("tableName") String tableName,
                                         @Param("primaryKey") String primaryKey,
                                         @Param("offset") int offset,
                                         @Param("limit") int limit);

    // 获取总数
    long getTotalCount(@Param("tableName") String tableName);

    // 插入
    int insert(@Param("tableName") String tableName,
               @Param("primaryKey") String primaryKey,
               @Param("data") Map<String, Object> data);

    // 获取最后插入的ID
    Long getLastInsertId();

    // 更新
    int update(@Param("tableName") String tableName,
               @Param("primaryKey") String primaryKey,
               @Param("data") Map<String, Object> data);

    // 删除
    int deleteById(@Param("tableName") String tableName,
                   @Param("primaryKey") String primaryKey,
                   @Param("id") Object id);

    // 获取表结构
    List<Map<String, Object>> getTableColumns(@Param("tableName") String tableName);

    // 获取主键
    String getPrimaryKey(@Param("tableName") String tableName);

    // 获取表信息
    Map<String, Object> getTableInfo(@Param("tableName") String tableName);
}