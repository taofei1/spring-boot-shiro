package com.neo.entity;

import lombok.Data;
import lombok.ToString;

import javax.persistence.*;
import java.util.Date;
@Entity
@Table(name = "sys_login_log")
@Data
@ToString
public class LoginLog {
    @Id
    @GeneratedValue
    private Integer id;
    private String os;
    private String browser;
    private String ip;
    private Date loginTime;
    private Integer isSuccess;
    private String msg;
    private String username;
    private Integer signType;//1:登录 2：登出
}
