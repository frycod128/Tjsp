package cn.yznu.abc321.dao;

import cn.yznu.abc321.entity.Condition;
import cn.yznu.abc321.entity.FkInfo;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface GenericDao {

    List<String> getTables(@Param("dbName") String dbName);

    List<String> getColumns(@Param("dbName") String dbName,
                            @Param("tableName") String tableName);

    /** 单键等值查询 */
    List<Map<String, Object>> queryByKey(Map<String, Object> params);

    /** 多条件 AND 查询 */
    List<Map<String, Object>> dynamicQuery(@Param("tableName") String tableName,
                                           @Param("conditions") List<Condition> conditions);

    /** 单列多值 OR 模糊查询 */
    List<Map<String, Object>> batchFuzzyQuery(@Param("tableName") String tableName,
                                              @Param("column") String column,
                                              @Param("values") List<String> values);

    List<FkInfo> getOutgoingFks(@Param("dbName") String dbName,
                                @Param("tableName") String tableName);

    List<FkInfo> getIncomingFks(@Param("dbName") String dbName,
                                @Param("tableName") String tableName);
}
