package com.neo.util;

import java.io.*;

public class CMDUtil {
    public synchronized static String exec(String cmd) throws Exception {
        Process p = Runtime.getRuntime().exec(cmd);

        InputStream fis = p.getInputStream();
        //用一个读输出流类去读
        //用缓冲器读行
        String resultstr = "";
        BufferedReader br = new BufferedReader(new InputStreamReader(fis,"GB2312"));
        String line;
        //直到读完为止
        while ((line = br.readLine()) != null) {
            //解析符合自己需要的內容，获取之后，直接返回。
            resultstr += line+"\n";
        }

        p.waitFor();
        fis.close();
        br.close();

        if (p.exitValue() != 0) {
            p.destroy();
            throw new Exception("cmd命令执行有误:" + cmd+";返回结果:"+resultstr);
        }
        p.destroy();
        return resultstr;




    }

    public static void main(String[] args)  {

        String result= null;
        try {
            result = exec("net use z: \\\\160.6.87.216\\l");
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println(result);
    }
}
