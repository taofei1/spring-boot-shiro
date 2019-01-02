package com.neo.entity;

import javax.persistence.*;
import java.util.Date;
@Entity
@Table(name = "sys_login_log")
public class LoginLog {
    @Id
    @GeneratedValue
    private Integer id;
    private String os;
    private String browser;
    private String ip;

    private Date loginTime;
    private String sessionId;
    @ManyToOne
    @JoinColumn(name="user_id")
    private UserInfo user;
    public LoginLog(){}
    public LoginLog(String os, String browser, String ip, Date loginTime, String sessionId, UserInfo user) {
        this.os = os;
        this.browser = browser;
        this.ip = ip;
        this.loginTime = loginTime;
        this.sessionId = sessionId;
        this.user = user;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getOs() {
        return os;
    }

    public void setOs(String os) {
        this.os = os;
    }

    public String getBrowser() {
        return browser;
    }

    public void setBrowser(String browser) {
        this.browser = browser;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public Date getLoginTime() {
        return loginTime;
    }

    public void setLoginTime(Date loginTime) {
        this.loginTime = loginTime;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public UserInfo getUser() {
        return user;
    }

    public void setUser(UserInfo user) {
        this.user = user;
    }
}
