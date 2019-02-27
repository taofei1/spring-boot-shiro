package com.neo.util;

import com.neo.entity.UserInfo;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.session.Session;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.subject.SimplePrincipalCollection;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.BeanUtils;
import org.springframework.util.StringUtils;

public class ShiroUtil {
    public static Subject getSubjct()
    {
        return SecurityUtils.getSubject();
    }

    public static UserInfo getSysUser()
    {
        UserInfo user = null;
        Object obj = getSubjct().getPrincipal();
        if (!StringUtils.isEmpty(obj))
        {
            user = new UserInfo();
            BeanUtils.copyProperties(obj, user);
        }
        return user;
    }
    public static void setSysUser(UserInfo user)
    {
        Subject subject = getSubjct();
        PrincipalCollection principalCollection = subject.getPrincipals();
        String realmName = principalCollection.getRealmNames().iterator().next();
        PrincipalCollection newPrincipalCollection = new SimplePrincipalCollection(user, realmName);
        // 重新加载Principal
        subject.runAs(newPrincipalCollection);
    }

    public static Session getSession() {
        return getSubjct().getSession();
    }

}
