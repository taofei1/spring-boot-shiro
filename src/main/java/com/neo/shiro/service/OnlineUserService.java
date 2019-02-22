package com.neo.shiro.service;

import com.neo.enums.ErrorEnum;
import com.neo.exception.BusinessException;
import com.neo.model.PageModel;
import com.neo.model.online.OnlineUser;
import com.neo.shiro.session.OnlineSessionDAO;
import com.neo.util.OnlineUserComparator;
import com.neo.util.ReflectUtil;
import com.neo.util.ShiroConstants;
import com.neo.util.StringUtils;
import org.apache.shiro.cache.Cache;
import org.apache.shiro.cache.ehcache.EhCacheManager;
import org.apache.shiro.session.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;

@Service
public class OnlineUserService {
    @Resource(name = "shiroEhcache")
    private EhCacheManager cacheManager;
    @Autowired
    private OnlineSessionDAO onlineSessionDAO;
    public PageModel onlineLists(int pageNum, int pageSize, OnlineUser onlineUser) {
        Cache<String, OnlineUser> cache = cacheManager.getCache(ShiroConstants.SESSIONS_CACHE);
        List<OnlineUser> online = new ArrayList<>(cache.values());
        Map<String, String> search = new HashMap<>();
        //根据搜索值进行过滤
        Iterator<OnlineUser> iter = online.iterator();
        while (iter.hasNext()) {
            OnlineUser o = iter.next();
            if (!StringUtils.isEmpty(onlineUser.getSessionId())) {
                search.put("sessionId", onlineUser.getSessionId());
                if (!o.getSessionId().contains(onlineUser.getSessionId())) {
                    iter.remove();
                    continue;
                }
            }
            if (!StringUtils.isEmpty(onlineUser.getBrowser())) {
                search.put("browser", onlineUser.getBrowser());
                if (!o.getBrowser().contains(onlineUser.getBrowser())) {
                    iter.remove();
                    continue;
                }
            }
            if (!StringUtils.isEmpty(onlineUser.getIpaddr())) {
                search.put("ipaddr", onlineUser.getIpaddr());
                if (!o.getIpaddr().contains(onlineUser.getIpaddr())) {
                    iter.remove();
                    continue;
                }
            }
            if (!StringUtils.isEmpty(onlineUser.getLoginLocation())) {
                search.put("loginLocation", onlineUser.getLoginLocation());
                if (!o.getLoginLocation().contains(onlineUser.getLoginLocation())) {
                    iter.remove();
                    continue;
                }
            }
            if (!StringUtils.isEmpty(onlineUser.getLoginName())) {
                search.put("loginName", onlineUser.getLoginName());

                if (!o.getLoginName().contains(onlineUser.getLoginName())) {
                    iter.remove();
                    continue;
                }
            }
        }
        //根据排序参数排序
        Map<String, Object> map = onlineUser.getParams();
        if (!StringUtils.isEmpty((String) map.get("sort"))) {
            String sort = (String) map.get("sort");
            String field = ReflectUtil.getFieldByNameAndClass(sort, OnlineUser.class);
            if (StringUtils.isNull(field)) {
                throw new BusinessException(ErrorEnum.PARAM_ERROR, "排序字段" + sort + "未找到！");

            }
            String order = "asc";
            if ("DESC".equalsIgnoreCase((String) map.get("order"))) {
                order = "desc";
            }
            Collections.sort(online, new OnlineUserComparator(field, order));
        }
        if (pageNum < 0) {
            pageNum = 0;
        }

        int totalNums = online.size();
        int totalPages = (int) Math.ceil((double) totalNums / pageSize);
        int startIndex = (pageNum - 1) * pageSize;
        int endIndex = pageNum * pageSize;
        PageModel page = new PageModel();
        page.setPageNum(pageNum);
        page.setOrder((String) map.get("order"));
        page.setSort((String) map.get("sort"));
        page.setPageSize(pageSize);
        page.setTotalPages(totalPages);
        page.setTotalNum(totalNums);
        page.setSearch(search);
        if (startIndex >= online.size()) {
            startIndex = (totalPages - 1) * pageSize;
            page.setList(online.subList(startIndex, online.size()));
        } else if (endIndex >= online.size()) {
            page.setList(online.subList(startIndex, online.size()));
        } else {
            page.setList(online.subList(startIndex, endIndex));
        }

        return page;
    }

    public OnlineUser selectOnlineById(String sessionId) {
        Cache<String, OnlineUser> cache = cacheManager.getCache(ShiroConstants.SESSIONS_CACHE);


        return cache.get(sessionId);
    }

    public void removeBySessionId(String sessionId) {
        Cache<String, OnlineUser> cache = cacheManager.getCache(ShiroConstants.SESSIONS_CACHE);
        cache.remove(sessionId);
    }

    public void forceLogout(String sessionId) {

        Session session = onlineSessionDAO.readSession(sessionId);
        if (session == null) {
            return;
        }
        session.setTimeout(1000);

        removeBySessionId(sessionId);

    }

}
