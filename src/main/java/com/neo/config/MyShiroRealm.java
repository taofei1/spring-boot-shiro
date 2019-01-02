package com.neo.config;

import com.neo.entity.Role;
import com.neo.entity.RolePermission;
import com.neo.entity.UserInfo;
import com.neo.service.UserInfoService;
import org.apache.shiro.authc.*;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.util.ByteSource;

import javax.annotation.Resource;


public class MyShiroRealm extends AuthorizingRealm {
    @Resource
    private UserInfoService userInfoService;
    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
        SimpleAuthorizationInfo authorizationInfo = new SimpleAuthorizationInfo();
        UserInfo userInfo  = (UserInfo)principals.getPrimaryPrincipal();
        for(Role role:userInfo.getRoleList()){
            authorizationInfo.addRole(role.getRole());
            for(RolePermission p:role.getPermissions()){
                authorizationInfo.addStringPermission(p.getPermission());
            }
        }
        return authorizationInfo;
    }

    /*��Ҫ���������������֤�ģ�Ҳ����˵��֤�û�������˺ź������Ƿ���ȷ��*/
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken authcToken)
            throws AuthenticationException {
        UsernamePasswordCaptchaToken token = (UsernamePasswordCaptchaToken) authcToken;
        //��ȡ�û���������˺�.
        String username = (String)token.getPrincipal();
        System.out.println(token.getCredentials());
        //ͨ��username�����ݿ��в��� User��������ҵ���û�ҵ�.
        UserInfo userInfo;
        if(username.contains("@")){
            userInfo=userInfoService.findByEmail(username);
        }else {
            userInfo = userInfoService.findByUsername(username);
        }
        if(userInfo == null){
            return null;
        }
        if (0==userInfo.getState()) {
            throw new LockedAccountException("�˺��ѱ�����������ϵ����Ա");
        }
        // �����ж���֤���߼�
      /*  String captcha = token.getCaptcha();
        String exitCode = (String) SecurityUtils.getSubject().getSession()
                .getAttribute("kaptcha");
        System.out.println("input:"+captcha+",sessionCapcha:"+exitCode);
        if (null == captcha || !captcha.equalsIgnoreCase(exitCode)) {
            throw new CaptchaException("��֤�����");
        }*/
//            LoginLog log=new LoginLog(RequestUtil.getOSInfo(request),RequestUtil.getBrowserInfo(request),
//                    RequestUtil.getIP(request),new Date(),RequestUtil.getSessionId(request), ShiroUtil.getSysUser());
//            userLoginLogService.save(log);

        SimpleAuthenticationInfo authenticationInfo = new SimpleAuthenticationInfo(
                userInfo, //�û�
                userInfo.getPassword(), //����
                ByteSource.Util.bytes(userInfo.getCredentialsSalt()),//salt=username+salt
                getName()  //realm name
        );
        return authenticationInfo;
    }

}