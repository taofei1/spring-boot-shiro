package com.neo.dto;

import lombok.Data;

@Data
public class MailConfigDTO {
    private Integer uid;
    private String email;
    private String emailPassword;
    private String emailHost;
    private String emailPort;
}
