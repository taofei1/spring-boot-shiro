package com.neo.dao;

import com.neo.entity.Message;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.repository.query.QueryByExampleExecutor;



public interface MessageRepo extends JpaRepository<Message,Integer>,QueryByExampleExecutor<Message>{
     //查找已发消息
    @Query("select m from Message m where m.sender.uid=?1 and m.hasSended='1'")
     Page<Message> findSendMessageBySenderId(Integer senderId, Pageable pageable);
    //查找草稿消息
    @Query("select m from Message m where m.sender.uid=?1 and m.hasSended='0'")
    Page<Message> findSendDraftsBySenderId(Integer senderId, Pageable pageable);
    //查找已发消息有检索条件
    @Query("select m from Message m where m.sender.uid=:id and m.hasSended='1' and  m.receiver.name like CONCAT('%',:search,'%') or m.subject like CONCAT('%',:search,'%') or m.content like CONCAT('%',:search,'%')")
    Page<Message> findSendMessageBySenderId(@Param("id") Integer senderId, @Param("search") String search, Pageable pageable);
    //查找草稿消息有检索条件
    @Query("select m from Message m where m.sender.uid=:id and m.hasSended='0' and m.receiver.name like CONCAT('%',:search,'%') or m.subject like CONCAT('%',:search,'%') or m.content like CONCAT('%',:search,'%')")
    Page<Message> findSendDraftsBySenderId(@Param("id") Integer senderId, @Param("search") String search, Pageable pageable);
    //查找已接收
    @Query("select m from Message m where m.receiver.uid=?1 and m.hasDel='0'")
    Page<Message> findMessagesByReceiverId(Integer id,Pageable pageable);
    //查找已删除
    @Query("select m from Message m where m.receiver.uid=?1 and m.hasDel='1'")
    Page<Message> findDelMessagesByReceiverId(Integer id,Pageable pageable);
    //查找已接收消息有检索条件
    @Query("select m from Message m where m.receiver.uid=:id and m.hasDel='0' and m.sender.name like CONCAT('%',:search,'%') or m.subject like CONCAT('%',:search,'%') or m.content like CONCAT('%',:search,'%')")
    Page<Message> findMessagesByReceiverId(@Param("id")Integer id,@Param("search") String search,Pageable pageable);
    //查找已删除消息有检索条件
    @Query("select m from Message m where m.receiver.uid=:id and m.hasDel='1' and m.sender.name like CONCAT('%',:search,'%') or m.subject like CONCAT('%',:search,'%') or m.content like CONCAT('%',:search,'%')")
    Page<Message> findDelMessagesByReceiverId(@Param("id")Integer id,@Param("search") String search,Pageable pageable);


}
