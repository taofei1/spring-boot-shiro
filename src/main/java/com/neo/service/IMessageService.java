package com.neo.service;

import com.neo.entity.Message;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;


public interface IMessageService {
    //查询消息

    /**
     *
     * @param userId 发送或接受用户id
     * @param pageNum 页数
     * @param pageSize 每页条数
     * @param type 1：接受消息 2：发送的消息 3：草稿箱 4：垃圾箱
     * @param sortName 排序字段，默认createTime
     * @param sortType 排序方式
     * @return 分页结果
     *
     */
    Page<Message> findMessages(Integer userId, int pageNum, int pageSize, String sortName,Integer sortType,int type, String search);

}
