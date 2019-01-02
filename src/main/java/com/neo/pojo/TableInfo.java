package com.neo.pojo;

import com.neo.util.StringUtils;
import lombok.ToString;

import java.io.Serializable;
import java.util.List;
@ToString
public class TableInfo extends BaseEntity implements Serializable{
    private String tableName;
    private String tableComment;
    private ColumnInfo primaryKey;
    private List<ColumnInfo> columns;
    /** 类名(第一个字母大写) */
    private String className;

    /** 类名(第一个字母小写) */
    private String classname;
    private CreateTable createTable;
    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public String getTableComment() {
        return tableComment;
    }

    public void setTableComment(String tableComment) {
        this.tableComment = tableComment;
    }

    public ColumnInfo getPrimaryKey() {
        return primaryKey;
    }

    public void setPrimaryKey(ColumnInfo primaryKey) {
        this.primaryKey = primaryKey;
    }

    public List<ColumnInfo> getColumns() {
        return columns;
    }

    public void setColumns(List<ColumnInfo> columns) {
        this.columns = columns;
    }


    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getClassname() {
        return classname;
    }

    public void setClassname(String classname) {
        this.classname = classname;
    }

    public CreateTable getCreateTable() {
        return createTable;
    }

    public void setCreateTable(CreateTable createTable) {
        this.createTable = createTable;
    }

    public ColumnInfo getColumnsLast()
    {
        ColumnInfo columnInfo = null;
        if (StringUtils.isNotNull(columns) && columns.size() > 0)
        {
            columnInfo = columns.get(0);
        }
        return columnInfo;
    }
}
