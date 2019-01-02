package com.neo.service;

import com.neo.entity.Email;
import org.springframework.data.domain.Page;

public interface IMailService {
    //查询消息

    /**
     *
     * @param userId 发送者id
     * @param pageNum 页数
     * @param pageSize 每页条数
     * @param type 0：草稿箱 1：发送的消息
     * @param sortName 排序字段，默认createTime
     * @param sortType 排序方式，默认降序
     * @return 分页结果
     *
     */
    Page<Email> findMessages(Integer userId, int pageNum, int pageSize, String sortName, Integer sortType, int type, String search);

    /**
     * 单个删除
     * @param id
     */
    void delete(Long id);

    /**
     * 根据id查找
     * @param id
     * @return email对象
     */
    Email findById(Long id);

    /**
     * 保存email
     * @param email
     * @return
     */
    Email save(Email email);

    /**
     * 查找未读的已发数量
     * @return
     */
    Integer findNoReadCountSended();

    /**
     * 查找未读的草稿数量
     * @return
     */
    Integer findNoReadCountDrafts();
}
