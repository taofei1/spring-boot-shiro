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

    /*主要是用来进行身份认证的，也就是说验证用户输入的账号和密码是否正确。*/
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken authcToken)
            throws AuthenticationException {
        UsernamePasswordCaptchaToken token = (UsernamePasswordCaptchaToken) authcToken;
        //获取用户的输入的账号.
        String username = (String)token.getPrincipal();
        System.out.println(token.getCredentials());
        //通过username从数据库中查找 User对象，如果找到，没找到.
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
            throw new LockedAccountException("账号已被锁定！请联系管理员");
        }
        // 增加判断验证码逻辑
      /*  String captcha = token.getCaptcha();
        String exitCode = (String) SecurityUtils.getSubject().getSession()
                .getAttribute("kaptcha");
        System.out.println("input:"+captcha+",sessionCapcha:"+exitCode);
        if (null == captcha || !captcha.equalsIgnoreCase(exitCode)) {
            throw new CaptchaException("验证码错误");
        }*/
//            LoginLog log=new LoginLog(RequestUtil.getOSInfo(request),RequestUtil.getBrowserInfo(request),
//                    RequestUtil.getIP(request),new Date(),RequestUtil.getSessionId(request), ShiroUtil.getSysUser());
//            userLoginLogService.save(log);

        SimpleAuthenticationInfo authenticationInfo = new SimpleAuthenticationInfo(
                userInfo, //用户
                userInfo.getPassword(), //密码
                ByteSource.Util.bytes(userInfo.getCredentialsSalt()),//salt=username+salt
                getName()  //realm name
        );
        return authenticationInfo;
    }

}