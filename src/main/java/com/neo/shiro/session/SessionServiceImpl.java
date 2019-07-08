package com.neo.shiro.session;


import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.neo.entity.UserInfo;
import com.neo.enums.OnlineStatus;
import com.neo.model.online.OnlineUser;
import org.apache.shiro.session.Session;
import org.apache.shiro.session.mgt.eis.SessionDAO;
import org.apache.shiro.subject.SimplePrincipalCollection;
import org.apache.shiro.subject.support.DefaultSubjectContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;


public class SessionServiceImpl {

    @Autowired
    ObjectMapper mapper;
    @Autowired
    private SessionDAO sessionDAO;

    public List<OnlineUser> list() {
        List<OnlineUser> list = new ArrayList<>();
        Collection<Session> sessions = sessionDAO.getActiveSessions();
        for (Session session : sessions) {
            OnlineUser userOnline = new OnlineUser();
            SimplePrincipalCollection principalCollection;
            if (session.getAttribute(DefaultSubjectContext.PRINCIPALS_SESSION_KEY) == null) {
                continue;
            } else {
                principalCollection = (SimplePrincipalCollection) session
                        .getAttribute(DefaultSubjectContext.PRINCIPALS_SESSION_KEY);
                UserInfo user = (UserInfo) principalCollection.getPrimaryPrincipal();
                userOnline.setLoginName(user.getUsername());
                //userOnline.s(user.getUserId().toString());
            }
            userOnline.setSessionId((String) session.getId());
            userOnline.setIpaddr(session.getHost());
            userOnline.setStartTimestamp(session.getStartTimestamp());
            userOnline.setLastAccessTime(session.getLastAccessTime());
            Long timeout = session.getTimeout();
            if (timeout == 0l) {
                userOnline.setStatus(OnlineStatus.OFF_LINE);
            } else {
                userOnline.setStatus(OnlineStatus.ON_LINE);
            }
         /*   String address = AddressUtils.getRealAddressByIP(userOnline.getHost(), mapper);
            userOnline.setLocation(address);
            userOnline.setTimeout(timeout);*/
            list.add(userOnline);
        }
        return list;
    }

    public boolean forceLogout(String sessionId) {
        Session session = sessionDAO.readSession(sessionId);
        session.setTimeout(0);
        return true;
    }

}

