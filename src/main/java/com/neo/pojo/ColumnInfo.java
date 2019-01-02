package com.neo.pojo;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.ToString;
import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;

@ToString
public class ColumnInfo implements Serializable{
    /** 字段名称 */
    private String columnName;

    /** 字段类型 */
    private String dataType;
    /**最大长度*/
    private Integer maxLen;

    /** 列描述 */
    private String columnComment;

    /** 列配置 */
    private ColumnConfigInfo configInfo;

    /** Java属性类型 */
    private String attrType;

    /** Java属性名称(第一个字母大写)，如：user_name => UserName */
    private String attrName;

    /** Java属性名称(第一个字母小写)，如：user_name => userName */
    private String attrname;
    /**是否可为空*/
    private String isNullable;
    /**是否主键*/
    private String isPrimaryKey;
    public String getColumnName()
    {
        return columnName;
    }

    public void setColumnName(String columnName)
    {
        this.columnName = columnName;
    }

    public String getDataType()
    {
        return dataType;
    }

    public void setDataType(String dataType)
    {
        this.dataType = dataType;
    }

    public String getColumnComment()
    {
        return columnComment;
    }

    public Integer getMaxLen() {
        return maxLen;
    }

    public void setMaxLen(Integer maxLen) {
        this.maxLen = maxLen;
    }

    public void setColumnComment(String columnComment) throws Exception
    {
        // 根据列描述解析列的配置信息
        if (StringUtils.isNotEmpty(columnComment) && columnComment.startsWith("{"))
        {
            ObjectMapper mapper = new ObjectMapper();
            this.configInfo = mapper.readValue(columnComment,ColumnConfigInfo.class);
            this.columnComment = configInfo.getTitle();
        }
        else
        {
            this.columnComment = columnComment;
        }
    }

    public String getAttrName()
    {
        return attrName;
    }

    public void setAttrName(String attrName)
    {
        this.attrName = attrName;
    }

    public String getAttrname()
    {
        return attrname;
    }

    public void setAttrname(String attrname)
    {
        this.attrname = attrname;
    }

    public String getAttrType()
    {
        return attrType;
    }

    public void setAttrType(String attrType)
    {
        this.attrType = attrType;
    }

    public ColumnConfigInfo getConfigInfo()
    {
        return configInfo;
    }

    public void setConfigInfo(ColumnConfigInfo configInfo)
    {
        this.configInfo = configInfo;
    }

    public String getIsNullable() {
        return isNullable;
    }

    public void setIsNullable(String isNullable) {
        this.isNullable = isNullable;
    }

    public String getIsPrimaryKey() {
        return isPrimaryKey;
    }

    public void setIsPrimaryKey(String isPrimaryKey) {
        this.isPrimaryKey = isPrimaryKey;
    }
}
