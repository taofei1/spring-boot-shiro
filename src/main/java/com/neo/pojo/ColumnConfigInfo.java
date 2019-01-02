package com.neo.pojo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.ToString;

import java.io.Serializable;

@JsonIgnoreProperties(ignoreUnknown = true)
@ToString
public class ColumnConfigInfo implements Serializable{
    /**
     * 属性标题
     */
    private String title;

    /**
     * 属性类型 dict(字典，value对应字典管理的字典类型), date(包括date)
     */
    private String type;

    /**
     * 属性值，参考数据类型，可为空
     */
    private String value;

    public ColumnConfigInfo()
    {
        super();
    }

    public ColumnConfigInfo(String title, String type, String value)
    {
        super();
        this.title = title;
        this.type = type;
        this.value = value;
    }

    public String getTitle()
    {
        return title;
    }

    public void setTitle(String title)
    {
        this.title = title;
    }

    public String getType()
    {
        return type;
    }

    public void setType(String type)
    {
        this.type = type;
    }

    public String getValue()
    {
        return value;
    }

    public void setValue(String value)
    {
        this.value = value;
    }
}
