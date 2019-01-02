package com.neo.entity;

import javax.persistence.*;
import java.io.Serializable;

@Entity
public class Server implements Serializable{
    @Id@GeneratedValue
    private Integer id;
    @Column(columnDefinition = "varchar(25) not null")
    private String ip; //ip???
    @Column(columnDefinition ="varchar(5)")
    private String port;//??????ftp
    @Column(columnDefinition = "varchar(100) not null")
    private String username; //?????
    @Column(columnDefinition = "varchar(100) not null")
    private String password;  //????
    @Column(columnDefinition="enum('1','2') not null default '1' ")
    private String type; //???? 1??ftp?????? 2??????????.??
    private Integer status;
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getPort() {
        return port;
    }

    public void setPort(String port) {
        this.port = port;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "Server{" + "id=" + id + ", ip='" + ip + '\'' + ", port='" + port + '\'' + ", username='" + username + '\'' + ", password='" + password + '\'' + ", type='" + type + '\'' + ", status=" + status + '}';
    }
}
