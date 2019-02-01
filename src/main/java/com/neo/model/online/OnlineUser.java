package com.neo.model.online;

import com.neo.enums.OnlineStatus;
import com.neo.pojo.BaseEntity;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;
import java.util.Date;

@Data
@ToString
public class OnlineUser extends BaseEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 用户会话id
     */
    private String sessionId;

    /**
     * 登录名称
     */
    private String loginName;

    /**
     * 登录IP地址
     */
    private String ipaddr;

    /**
     * 登录地址
     */
    private String loginLocation;

    /**
     * 浏览器类型
     */
    private String browser;

    /**
     * 操作系统
     */
    private String os;

    /**
     * session创建时间
     */
    private Date startTimestamp;

    /**
     * session最后访问时间
     */
    private Date lastAccessTime;

    /**
     * 超时时间，单位为分钟
     */
    private Long expireTime;

    /**
     * 在线状态
     */
    private OnlineStatus status = OnlineStatus.ON_LINE;

    /**
     * 备份的当前用户会话
     */
    private OnlineSession session;
}
