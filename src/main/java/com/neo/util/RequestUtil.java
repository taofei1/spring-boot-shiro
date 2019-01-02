package com.neo.util;

import eu.bitwalker.useragentutils.UserAgent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;

public class RequestUtil {
    private static final Logger log= LoggerFactory.getLogger(RequestUtil.class);
    public static String getIP(HttpServletRequest request){
            String ip = request.getHeader("X-Forwarded-For");
            log.info("ipInfo:{}",ip);
            if(!StringUtils.isEmpty(ip) && !"unKnown".equalsIgnoreCase(ip)){
                //多次反向代理后会有多个ip值，第一个ip才是真实ip
                int index = ip.indexOf(",");
                if(index != -1){
                    return ip.substring(0,index);
                }else{
                    return ip;
                }
            }
            ip = request.getHeader("X-Real-IP");
            if(!StringUtils.isEmpty(ip) && !"unKnown".equalsIgnoreCase(ip)){
                return ip;
            }
            return request.getRemoteAddr();

    }
    public static String getSessionId(HttpServletRequest request){
        return request.getSession().getId();
    }
    public static String getOSInfo(HttpServletRequest request){
           UserAgent userAgent = UserAgent.parseUserAgentString(request.getHeader("User-Agent"));
           return userAgent.getOperatingSystem().getName();

    }
    public static String getBrowserInfo(HttpServletRequest request) {
        UserAgent userAgent = UserAgent.parseUserAgentString(request.getHeader("User-Agent"));
        return userAgent.getBrowser().getName()+" "+userAgent.getBrowserVersion();
    }
    //通过request.getReader()的方式来获取body
    public static String getBodyMsg(HttpServletRequest request) {

        InputStream is = null;
        StringBuilder sb = new StringBuilder();
        try {
            is = request.getInputStream();

            byte[] b = new byte[4096];
            for (int n; (n = is.read(b)) != -1; ) {
                sb.append(new String(b, 0, n));
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (null != is) {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return sb.toString();
        }
    }



}

