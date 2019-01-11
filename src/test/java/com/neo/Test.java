package com.neo;

import com.neo.config.GlobalConfig;
import com.neo.util.StringUtils;
import org.springframework.web.client.RestTemplate;

public class Test {
    public static void main(String[] args) {

        System.out.println(StringUtils.getPrefixNum("1e100M"));

    }
}
