package com.neo.dao;

import com.neo.entity.Email;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.repository.query.QueryByExampleExecutor;


public interface EmailRepository extends JpaRepository<Email,Long>,QueryByExampleExecutor<Email> {
    //查找已发消息
    @Query("select e from Email e where e.sender.uid=?1 and e.hasSended='1'")
    Page<Email> findSendMessageBySenderId(Integer senderId, Pageable pageable);
    //查找草稿消息
    @Query("select e from Email e where e.sender.uid=?1 and e.hasSended='0'")
    Page<Email> findSendDraftsBySenderId(Integer senderId, Pageable pageable);
    //查找已发消息有检索条件
    @Query("select m from Email m where m.sender.uid=:id and m.hasSended='1' and m.subject like CONCAT('%',:search,'%') or m.content like CONCAT('%',:search,'%')")
    Page<Email> findSendMessageBySenderId(@Param("id") Integer senderId, @Param("search") String search, Pageable pageable);
    //查找草稿消息有检索条件
    @Query("select m from Email m where m.sender.uid=:id and m.hasSended='0' and  m.subject like CONCAT('%',:search,'%') or m.content like CONCAT('%',:search,'%')")
    Page<Email> findSendDraftsBySenderId(@Param("id") Integer senderId, @Param("search") String search, Pageable pageable);
    //查找未读已发数量
    @Query("select count(e) from Email e where e.sender.uid=?1 and e.hasSended='1'")
    Integer findNoReadCountSended();
    //查找未读草稿数量
    @Query("select count(e) from Email e where e.sender.uid=?1 and e.hasSended='0'")
    Integer findNoReadCountDrafts();
}
