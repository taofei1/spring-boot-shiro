package com.neo.shiro.service;

import com.neo.enums.ErrorEnum;
import com.neo.exception.BusinessException;
import com.neo.model.PageModel;
import com.neo.model.online.OnlineUser;
import com.neo.util.ReflectUtil;
import com.neo.util.ShiroConstants;
import com.neo.util.StringUtils;
import org.apache.shiro.cache.Cache;
import org.apache.shiro.cache.ehcache.EhCacheManager;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

@Service
public class OnlineUserService {
    @Resource(name = "shiroEhcache")
    private EhCacheManager cacheManager;

    public PageModel onlineLists(int pageNum, int pageSize, OnlineUser onlineUser) {
        Cache<String, OnlineUser> cache = cacheManager.getCache(ShiroConstants.SESSIONS_CACHE);
        Collection<OnlineUser> online = cache.values();
        //根据搜索值进行过滤
        for (OnlineUser o : online) {
            if (!StringUtils.isEmpty(onlineUser.getSessionId())) {
                if (!o.getSessionId().contains(onlineUser.getSessionId())) {
                    online.remove(o);
                    continue;
                }
            }
            if (!StringUtils.isEmpty(onlineUser.getBrowser())) {
                if (!o.getBrowser().contains(onlineUser.getBrowser())) {
                    online.remove(o);
                    continue;
                }
            }
            if (!StringUtils.isEmpty(onlineUser.getIpaddr())) {
                if (!o.getIpaddr().contains(onlineUser.getIpaddr())) {
                    online.remove(o);
                    continue;
                }
            }
            if (!StringUtils.isEmpty(onlineUser.getLoginLocation())) {
                if (!o.getLoginLocation().contains(onlineUser.getLoginLocation())) {
                    online.remove(o);
                    continue;
                }
            }
            if (!StringUtils.isEmpty(onlineUser.getLoginName())) {
                if (!o.getLoginName().contains(onlineUser.getLoginName())) {
                    online.remove(o);
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
            // if("".equalsIgnoreCase(map.get("order")))
        }

        int totalNums = online.size();
        int totalPages = (int) Math.ceil((double) totalNums / pageSize);
        int startIndex = (pageNum - 1) * pageSize;
        int endIndex = pageNum * pageSize;
        List list = new ArrayList(online);
        PageModel page = new PageModel();
        page.setList(list.subList(startIndex, endIndex));

        return null;
    }

}
