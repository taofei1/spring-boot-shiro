package com.neo;

import com.neo.config.GlobalConfig;
import org.springframework.web.client.RestTemplate;

public class Test {
    public static void main(String[] args) {

        RestTemplate rs = new RestTemplate();
        System.out.println(rs.getForEntity("http://www.baidu.com", String.class));

    }
}
