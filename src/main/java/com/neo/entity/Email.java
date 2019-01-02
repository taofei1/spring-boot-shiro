package com.neo.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.springframework.data.annotation.CreatedDate;

import javax.persistence.*;
import java.util.Date;

@Entity
@Data
public class Email {
    @Id
    @GeneratedValue
    private Long id;
    @ManyToOne
    @JoinColumn(name="sender_id")
    private UserInfo sender;
    private String receiversId;
    private String subject;
    private String content;
    @OneToOne
    @JoinColumn(name="file_id")
    private Attachment attachments;
    private Integer hasSended;//是否发送0：草稿1：发送
    private Integer hasRead;
    @CreatedDate
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private Date sendTime;
}
