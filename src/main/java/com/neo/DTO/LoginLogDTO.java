package com.neo.DTO;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;

@Data
public class LoginLogDTO {
    private Integer id;
    private String os;
    private String browser;
    private String ip;
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private Date loginTime;
    private String sessionId;
    private String username;
}
