package com.neo.pojo;


import lombok.Data;
import lombok.ToString;

import java.util.Date;

/**
 * 操作日志记录表 oper_log
 *
 * @author ruoyi
 */
@Data
@ToString
public class OperLog extends BaseEntity
{

    /** 日志主键 */
    private Long operId;

    /** 操作模块 */
    private String title;

    /** 业务类型（0其它 1新增 2修改 3删除） */
    private Integer operType;

    /** 请求方法 */
    private String method;

    /** 操作类别（0其它 1后台用户 2手机端用户） */
    private Integer operatorType;

    /** 操作人员 */
    private String operName;

    /** 部门名称 */
    private String deptName;

    /** 请求url */
    private String operUrl;

    /** 操作地址 */
    private String operIp;

    /** 操作地点 */
    private String operLocation;

    /** 请求参数 */
    private String operParam;

    /** 操作状态（0正常 1异常） */
    private Integer status;

    /** 错误消息 */
    private String errorMsg;

    /** 操作时间 */
    private Date operTime;



}
