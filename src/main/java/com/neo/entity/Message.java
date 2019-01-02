package com.neo.entity;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Data;
import org.hibernate.validator.constraints.NotEmpty;
import org.springframework.data.annotation.CreatedDate;

import javax.persistence.*;
import java.util.Date;
@Entity
@Data
@Table(name="sys_message")
public class Message {
    @Id
    @GeneratedValue
    private Long messageId;//消息id
    private int messageType;//(消息类型：1：私信，对一人或者多人:2：公告，对所有人)
    private int hasSended;//(是否发送：0：草稿，1：已发送)
    private int hasDel;//是否删除（在垃圾箱）0：未删除 1：已删除
    private int hasSeen;//是否查看针对收件人 0：查看，1：未查看
    @ManyToOne
    @JoinColumn(name="sender_id")
    private UserInfo sender;//（消息来源）
    @ManyToOne
    @JoinColumn(name="receiver_id")
    private UserInfo receiver;//消息接收人
    @NotEmpty(message="消息主题不能为空")
    private String subject;//主题
    @NotEmpty(message="消息内容不能为空")
    private String content;//内容
    @ManyToOne
    @JoinColumn(name="file_id")
    private Attachment fileid;//邮件附件id
    @CreatedDate
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;//创建时间

}
