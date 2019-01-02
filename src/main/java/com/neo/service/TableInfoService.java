package com.neo.service;

import com.neo.exception.BusinessException;
import com.neo.pojo.CreateTable;
import com.neo.pojo.ColumnInfo;
import com.neo.pojo.TableInfo;

import java.util.List;

public interface TableInfoService {
    /**
     * 查询ry数据库表信息
     *
     * @param tableInfo 表信息
     * @return 数据库表列表
     */
     List<TableInfo> selectTableList(TableInfo tableInfo);

    /**
     * 查询建表语句
     *
     * @param tableName 表名
     * @return
     */
    CreateTable selectCreateTable(String tableName);

    /**
     *  查询列信息
     * @param tableName 表名
     * @return 列信息列表
     */
     List<ColumnInfo> getColumnsByTableName(String tableName) throws BusinessException;

    /**
     * 生成代码
     *
     * @param tableName 表名称
     * @return 数据
     */
     byte[] generatorCode(String tableName);

    /**
     * 批量生成代码
     *
     * @param tableNames 表数组
     * @return 数据
     */
     byte[] generatorCode(String[] tableNames);

}
