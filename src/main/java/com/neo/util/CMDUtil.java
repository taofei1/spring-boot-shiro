package com.neo.util;

import java.io.*;

public class CMDUtil {
    public synchronized static String exec(String cmd) throws Exception {
        Process p = Runtime.getRuntime().exec(cmd);

        InputStream fis = p.getInputStream();
        //��һ�����������ȥ��
        //�û���������
        String resultstr = "";
        BufferedReader br = new BufferedReader(new InputStreamReader(fis,"GB2312"));
        String line;
        //ֱ������Ϊֹ
        while ((line = br.readLine()) != null) {
            //���������Լ���Ҫ�ă��ݣ���ȡ֮��ֱ�ӷ��ء�
            resultstr += line+"\n";
        }

        p.waitFor();
        fis.close();
        br.close();

        if (p.exitValue() != 0) {
            p.destroy();
            throw new Exception("cmd����ִ������:" + cmd+";���ؽ��:"+resultstr);
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
