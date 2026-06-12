package cn.yznu.abc321.dao;

import cn.yznu.abc321.entity.FkInfo;
import org.apache.ibatis.annotations.Param;
import java.util.List;
import java.util.Map;

public interface GenericDao {
    List<String> getTables(@Param("dbName") String dbName);

    List<String> getColumns(@Param("dbName") String dbName,
                            @Param("tableName") String tableName);

    List<Map<String, Object>> queryByKey(Map<String, Object> params);

    List<FkInfo> getOutgoingFks(@Param("dbName") String dbName,
                                @Param("tableName") String tableName);

    List<FkInfo> getIncomingFks(@Param("dbName") String dbName,
                                @Param("tableName") String tableName);
}
