package com.neo.service.impl;

import com.neo.dao.EmailRepository;
import com.neo.entity.Email;
import com.neo.service.IMailService;
import com.neo.util.PageableUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class MailServiceImpl implements IMailService{
    @Autowired
    private EmailRepository emailRepository;
    @Override
    public Page<Email> findMessages(Integer userId, int pageNum, int pageSize, String sortName, Integer sortType, int type, String search) {
        Pageable pageable= PageableUtil.getMessagePageable(pageNum,pageSize,sortName,sortType);
        Page<Email> page=null;
        switch (type){
            case 0:
                if(search==null||"".equals(search)){
                    page=emailRepository.findSendDraftsBySenderId(userId,pageable);
                }else {
                    page = emailRepository.findSendDraftsBySenderId(userId, search,pageable);
                }
                break;
            case 1:
                if(search==null||"".equals(search)){
                    page=emailRepository.findSendMessageBySenderId(userId,pageable);
                }else {
                    page = emailRepository.findSendMessageBySenderId(userId, search,pageable);
                }
                break;

        }
        return page;

    }

    @Override
    public void delete(Long id) {
        emailRepository.delete(id);
    }

    @Override
    public Email findById(Long id) {
        return emailRepository.findOne(id);
    }

    @Override
    public Email save(Email email) {
        return emailRepository.save(email);
    }

    @Override
    public Integer findNoReadCountSended() {
        return emailRepository.findNoReadCountSended();
    }

    @Override
    public Integer findNoReadCountDrafts() {
        return emailRepository.findNoReadCountDrafts();
    }
}
