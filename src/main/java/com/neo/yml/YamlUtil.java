package com.neo.yml;

import com.neo.util.StringUtils;

import org.yaml.snakeyaml.Yaml;

import java.util.*;

import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;

import org.yaml.snakeyaml.DumperOptions;


/**
 * 配置处理工具类
 *
 * @author yml
 */
public class YamlUtil
{
    public static void main(String[]  args) throws FileNotFoundException {
        Map map=loadYaml("application.yml");
      //  List<String> s= Arrays.asList(new String[]{"1","2"});
       // setProperty(map,"aa","1");
        //System.out.println(getProperty(map,"entity.packages"));

    }
    public static Map<?, ?> loadYaml(String fileName) throws FileNotFoundException
    {
        InputStream in = YamlUtil.class.getClassLoader().getResourceAsStream(fileName);
        return StringUtils.isNotEmpty(fileName) ? (LinkedHashMap<?, ?>) new Yaml().load(in) : null;
    }

    public static void dumpYaml(String fileName, Map<?, ?> map) throws IOException
    {
        if (StringUtils.isNotEmpty(fileName))
        {
            FileWriter fileWriter = new FileWriter(YamlUtil.class.getResource(fileName).getFile());
            DumperOptions options = new DumperOptions();
            options.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
            Yaml yaml = new Yaml(options);
            yaml.dump(map, fileWriter);
        }
    }

    public static Object getProperty(Map<?, ?> map, Object qualifiedKey)
    {
        if (map != null && !map.isEmpty() && qualifiedKey != null)
        {
            String input = String.valueOf(qualifiedKey);
            if (!input.equals(""))
            {
                if (input.contains("."))
                {
                    int index = input.indexOf(".");
                    String left = input.substring(0, index);
                    String right = input.substring(index + 1, input.length());
                    return getProperty((Map<?, ?>) map.get(left), right);
                }
                else if (map.containsKey(input))
                {
                    return map.get(input);
                }
                else
                {
                    return null;
                }
            }
        }
        return null;
    }

    /**
     * 设置属性仅在内存中
     * @param map
     * @param qualifiedKey
     * @param value
     */
    public static void setProperty(Map<?, ?> map, Object qualifiedKey, Object value)
    {
        if (map != null && !map.isEmpty() && qualifiedKey != null)
        {
            String input = String.valueOf(qualifiedKey);
            if (!input.equals(""))
            {
                if (input.contains("."))
                {
                    int index = input.indexOf(".");
                    String left = input.substring(0, index);
                    String right = input.substring(index + 1, input.length());
                    setProperty((Map<?, ?>) map.get(left), right, value);
                }
                else
                {
                    ((Map<Object, Object>) map).put(qualifiedKey, value);
                }
            }
        }
    }
}
