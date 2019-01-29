package com.neo.config;

import at.pollux.thymeleaf.shiro.dialect.ShiroDialect;
import org.apache.shiro.authc.credential.HashedCredentialsMatcher;
import org.apache.shiro.codec.Base64;

import org.apache.shiro.mgt.SecurityManager;
import org.apache.shiro.session.mgt.eis.EnterpriseCacheSessionDAO;
import org.apache.shiro.spring.security.interceptor.AuthorizationAttributeSourceAdvisor;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.web.filter.authc.FormAuthenticationFilter;
import org.apache.shiro.web.mgt.CookieRememberMeManager;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.apache.shiro.web.servlet.SimpleCookie;
import org.apache.shiro.web.session.mgt.DefaultWebSessionManager;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.handler.SimpleMappingExceptionResolver;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Properties;

//@Configuration
public class ShiroConfig {
	@Bean
	public ShiroFilterFactoryBean shirFilter(SecurityManager securityManager) {
		ShiroFilterFactoryBean shiroFilterFactoryBean = new ShiroFilterFactoryBean();
		//shiroFilterFactoryBean.setSecurityManager(securityManager);
		// 配置shiro安全管理器 SecurityManager
		shiroFilterFactoryBean.setSecurityManager(securityManager);
		//添加kickout认证
//		HashMap<String,Filter> hashMap=new HashMap<String,Filter>();
//		hashMap.put("kickout",formFilter());
//		shiroFilterFactoryBean.setFilters(hashMap);
		//拦截器.
		Map<String,String> filterChainDefinitionMap = new LinkedHashMap<String,String>();
		// 解决登录下载favicon.ico图标问题
		filterChainDefinitionMap.put("/druid/**","perms[druid:monitor]");
		filterChainDefinitionMap.put("/getKaptchaImage","anon");
		filterChainDefinitionMap.put("/favicon.ico", "anon");
		// 配置不会被拦截的链接 顺序判断
		filterChainDefinitionMap.put("/common/**", "anon");
		filterChainDefinitionMap.put("/js/**", "anon");
		filterChainDefinitionMap.put("/layui/**","anon");
		filterChainDefinitionMap.put("/h-ui/**","anon");
		filterChainDefinitionMap.put("/jquery-validation/**","anon");
		filterChainDefinitionMap.put("/laydate/**","anon");
		filterChainDefinitionMap.put("/skin/**","anon");
		filterChainDefinitionMap.put("/bootstrap/**","anon");
		//配置退出 过滤器,其中的具体的退出代码Shiro已经替我们实现了
		filterChainDefinitionMap.put("/logout", "logout");
		//<!-- 过滤链定义，从上向下顺序执行，一般将/**放在最为下边 -->:这是一个坑呢，一不小心代码就不好使了;
		//<!-- authc:所有url都必须认证通过才可以访问; anon:所有url都都可以匿名访问-->
		filterChainDefinitionMap.put("/vip", "roles[vip]");
        filterChainDefinitionMap.put("/**", "user,onlineSession,syncOnlineSession");
        //	filterChainDefinitionMap.put("/**", "kickout");
		// 如果不设置默认会自动寻找Web工程根目录下的"/login.jsp"页面

		shiroFilterFactoryBean.setLoginUrl("/login");
		// 登录成功后要跳转的链接
		shiroFilterFactoryBean.setSuccessUrl("/index");

		//未授权界面;
		shiroFilterFactoryBean.setUnauthorizedUrl("/403");
		shiroFilterFactoryBean.setFilterChainDefinitionMap(filterChainDefinitionMap);
		return shiroFilterFactoryBean;
	}

	/**
	 * 凭证匹配器
	 * （由于我们的密码校验交给Shiro的c进行处理了
	 * ）
	 * @return
	 */

	@Bean
 	public HashedCredentialsMatcher hashedCredentialsMatcher(){
		 		HashedCredentialsMatcher hashedCredentialsMatcher = new HashedCredentialsMatcher();
		 		hashedCredentialsMatcher.setHashAlgorithmName("md5");//散列算法:这里使用MD5算法;
		 		hashedCredentialsMatcher.setHashIterations(2);//散列的次数，比如散列两次，相当于 md5(md5(""));
				return hashedCredentialsMatcher;
	}

	@Bean
	public MyShiroRealm myShiroRealm(){
		MyShiroRealm myShiroRealm = new MyShiroRealm();
		myShiroRealm.setCredentialsMatcher(hashedCredentialsMatcher());
		return myShiroRealm;
	}
	@Bean
	public FormAuthenticationFilter formFilter(){
		CustomFormAuthenticationFilter kickoutSessionFilter = new CustomFormAuthenticationFilter();

		return kickoutSessionFilter;

	}
	@Bean
	public SimpleCookie rememberMeCookie(){
		//System.out.println("ShiroConfiguration.rememberMeCookie()");
		//这个参数是cookie的名称，对应前端的checkbox的name = rememberMe
		SimpleCookie simpleCookie = new SimpleCookie("rememberMe");


		//<!-- 记住我cookie生效时间30天 ,单位秒;-->

		simpleCookie.setMaxAge(259200);
		return simpleCookie;
	}
	/**
	 * cookie管理对象;
	 * rememberMeManager()方法是生成rememberMe管理器，而且要将这个rememberMe管理器设置到securityManager中
	 * @return
	 */
	@Bean
	public CookieRememberMeManager rememberMeManager(){
		//System.out.println("ShiroConfiguration.rememberMeManager()");
		CookieRememberMeManager cookieRememberMeManager = new CookieRememberMeManager();
		cookieRememberMeManager.setCookie(rememberMeCookie());
		//rememberMe cookie加密的密钥 建议每个项目都不一样 默认AES算法 密钥长度(128 256 512 位)
		cookieRememberMeManager.setCipherKey(Base64.decode("2AvVhdsgUs0FSA3SDFAdag=="));

		return cookieRememberMeManager;
	}
	@Bean
	public ShiroDialect shiroDialect() {
		return new ShiroDialect();
	}
	@Bean
	public SecurityManager securityManager(){
		DefaultWebSecurityManager securityManager =  new DefaultWebSecurityManager();
		securityManager.setRealm(myShiroRealm());
		//注入记住我管理器
		securityManager.setRememberMeManager(rememberMeManager());
		// //注入ehcache缓存管理器;
	//	securityManager.setCacheManager(ehCacheManager());
		// //注入session管理器;
		securityManager.setSessionManager(sessionManager());
		return securityManager;
	}

	/**
	 *  开启shiro aop注解支持.
	 *  使用代理方式;所以需要开启代码支持;
	 * @param securityManager
	 * @return
	 */
	@Bean
	public AuthorizationAttributeSourceAdvisor authorizationAttributeSourceAdvisor(SecurityManager securityManager){
		AuthorizationAttributeSourceAdvisor authorizationAttributeSourceAdvisor = new AuthorizationAttributeSourceAdvisor();
		authorizationAttributeSourceAdvisor.setSecurityManager(securityManager);
		return authorizationAttributeSourceAdvisor;
	}

	@Bean(name="simpleMappingExceptionResolver")
	public SimpleMappingExceptionResolver
	createSimpleMappingExceptionResolver() {
		SimpleMappingExceptionResolver r = new SimpleMappingExceptionResolver();
		Properties mappings = new Properties();
		mappings.setProperty("DatabaseException", "databaseError");//数据库异常处理
		mappings.setProperty("UnauthorizedException","403");
		r.setExceptionMappings(mappings);  // None by default
		r.setDefaultErrorView("error");    // No default
		r.setExceptionAttribute("ex");     // Default is "exception"
		//r.setWarnLogCategory("example.MvcLogger");     // No default
		return r;
	}
	/**
	 * ehcache缓存管理器；shiro整合ehcache：
	 * 通过安全管理器：securityManager
	 * 单例的cache防止热部署重启失败
	 * @return EhCacheManager
	 */
/*	@Bean
	public EhCacheManager ehCacheManager() {

		EhCacheManager ehcache = new EhCacheManager();
		CacheManager cacheManager = CacheManager.getCacheManager("shiro");
		if(cacheManager == null){
			try {

				cacheManager = CacheManager.create(ResourceUtils.getInputStreamForPath("classpath:config/ehcache.xml"));

			} catch (CacheException | IOException e) {
				e.printStackTrace();
			}
		}
		ehcache.setCacheManager(cacheManager);

		return ehcache;
	}*/
	/**
	 * EnterpriseCacheSessionDAO shiro sessionDao层的实现；
	 * 提供了缓存功能的会话维护，默认情况下使用MapCache实现，内部使用ConcurrentHashMap保存缓存的会话。
	 */
	@Bean
	public EnterpriseCacheSessionDAO enterCacheSessionDAO() {
		EnterpriseCacheSessionDAO enterCacheSessionDAO = new EnterpriseCacheSessionDAO();
		//添加缓存管理器
		//enterCacheSessionDAO.setCacheManager(ehCacheManager());
		//添加ehcache活跃缓存名称（必须和ehcache缓存名称一致）
		enterCacheSessionDAO.setActiveSessionsCacheName("shiro-activeSessionCache");
		return enterCacheSessionDAO;
	}

	/**
	 *
	 * @描述：sessionManager添加session缓存操作DAO
	 * @创建人：wyait
	 * @创建时间：2018年4月24日 下午8:13:52
	 * @return
	 */
	@Bean
	public DefaultWebSessionManager sessionManager() {
		DefaultWebSessionManager sessionManager = new DefaultWebSessionManager();
	//	sessionManager.setCacheManager(ehCacheManager());
		sessionManager.setSessionDAO(enterCacheSessionDAO());
		sessionManager.setGlobalSessionTimeout(-1000l);
		return sessionManager;
	}




}