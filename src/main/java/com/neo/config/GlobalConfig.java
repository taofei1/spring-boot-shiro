package com.neo.config;

import com.neo.util.StringUtils;
import com.neo.yml.YamlUtil;
import lombok.extern.slf4j.Slf4j;

import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Map;

@Slf4j
public enum GlobalConfig {
    INSTANCE;
    /**
     * 保存全局属性值
     */
    private static Map<String, String> map = new HashMap<>();
    private static final String CONFIGNAME = "application.yml";

    public static GlobalConfig getInstance() {
        return INSTANCE;
    }

    /**
     * 获取配置
     */
    public static String getConfig(String key) {
        String value = map.get(key);
        if (value == null) {
            Map<?, ?> yamlMap = null;
            try {
                yamlMap = YamlUtil.loadYaml(CONFIGNAME);
                value = String.valueOf(YamlUtil.getProperty(yamlMap, key));
                map.put(key, value != null ? value : StringUtils.EMPTY);
            } catch (FileNotFoundException e) {
                log.error("获取全局配置异常 " + key, e);
            }
        }
        return value;
    }

    /**
     * 获取文件上传路径
     */
    public static String getProfile() {
        return getConfig("feifei.profile");
    }

    /**
     * 获取头像上传路径
     */
    public static String getAvatarPath() {
        return getProfile() + "/avatar/";
    }

    /**
     * 获取下载上传路径
     */
    public static String getDownloadPath() {
        return getProfile() + "/download/";
    }

    /**
     * 获取云盘路径
     *
     * @return
     */
    public static String getCloudFilePath() {
        return getProfile() + "/cloudFile/";
    }

    public static Long getMaxFileSizeByte() {
        String configSize = getConfig("feifei.maxSize").toUpperCase();
        Long size = StringUtils.getPrefixNum(configSize);

        if (configSize.endsWith("K")) {
            return size;
        } else if (configSize.endsWith("KB")) {
            return size * 1024;
        } else if (configSize.endsWith("M") || configSize.endsWith("MB")) {
            return size * (1024 * 1024);
        } else if (configSize.endsWith("G") || configSize.endsWith("GB")) {
            return size * (1024 * 1024 * 1024);
        } else {
            return 1024 * 1024L;
        }
    }


}
