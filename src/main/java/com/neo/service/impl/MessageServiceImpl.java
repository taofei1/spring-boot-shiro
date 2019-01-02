package com.neo.service.impl;

import com.neo.dao.MessageRepo;
import com.neo.entity.Message;
import com.neo.service.IMessageService;
import com.neo.util.PageableUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

@Service
public class MessageServiceImpl implements IMessageService{
    @Autowired
    private MessageRepo messageRepo;

    @Override
    public Page<Message> findMessages(Integer userId, int pageNum, int pageSize, String sortName,Integer sortType,int type, String search)   {
        Pageable pageable=PageableUtil.getMessagePageable(pageNum,pageSize,sortName,sortType);
        Page<Message> page=null;
        switch (type){
            case 1:
                if(search==null||"".equals(search)){
                    page=messageRepo.findMessagesByReceiverId(userId,pageable);
                }else {
                    page = messageRepo.findMessagesByReceiverId(userId, search,pageable);
                }
                break;
            case 2:
                if(search==null||"".equals(search)){
                    page=messageRepo.findSendMessageBySenderId(userId,pageable);
                }else {
                    page = messageRepo.findSendMessageBySenderId(userId, search,pageable);
                }
                break;
            case 3:
                if(search==null||"".equals(search)){
                    page=messageRepo.findSendDraftsBySenderId(userId,pageable);
                }else {
                    page = messageRepo.findSendDraftsBySenderId(userId, search,pageable);
                }
                break;
            case 4:
                if(search==null||"".equals(search)){
                    page=messageRepo.findDelMessagesByReceiverId(userId,pageable);
                }else {
                    page = messageRepo.findDelMessagesByReceiverId(userId, search,pageable);
                }
                break;
        }

        return page;
    }
}
