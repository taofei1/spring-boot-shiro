package com.neo.mapper;


import com.neo.pojo.CreateTable;
import com.neo.pojo.ColumnInfo;
import com.neo.pojo.TableInfo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 代码生成 数据层
 *
 * @author ruoyi
 */
@Repository
public interface GenMapper
{
    /**
     * 查询ry数据库表信息
     *
     * @param tableInfo 表信息
     * @return 数据库表列表
     */
     List<TableInfo> selectTableList(TableInfo tableInfo);

    /**
     * 根据表名称查询信息
     *
     * @param tableName 表名称
     * @return 表信息
     */
     TableInfo selectTableByName(String tableName);

    /**
     * 根据表名称查询列信息
     *
     * @param tableName 表名称
     * @return 列信息
     */
     List<ColumnInfo> selectTableColumnsByName(String tableName);
    /**
     * 查询建表信息
     * @param tableName 表名
     * @return 建表语句
     *
     */
    CreateTable selectCreateTable(@Param("tableName") String tableName);
}
