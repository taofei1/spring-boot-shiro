package com.neo.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.neo.entity.UserInfo;
import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class MessageDTO {
    private Long messageId;//消息id
    private int messageType;//(消息类型：1：私信:2：公告)
    private int messageStatus;//(消息状态：0：草稿，1：已发送未查看，2：已查看)
    private int isDel;

    private UserInfo fromUser;//（消息来源）
    private List<UserInfo> toUsers;//消息接收人可多个

    private String subject;//主题

    private String content;//内容
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;//创建时间

}
