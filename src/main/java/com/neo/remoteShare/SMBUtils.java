package com.neo.remoteShare;
import jcifs.Config;
import jcifs.UniAddress;
import jcifs.smb.NtlmPasswordAuthentication;
import jcifs.smb.SmbFile;
import jcifs.smb.SmbSession;
import org.apache.commons.io.IOUtils;

import java.io.*;

public class SMBUtils {
    /**
     * Description: 从共享目录拷贝文件到本地
     *
     * @Version1.0 Sep 25, 2009 3:48:38 PM
     */

    public static boolean smbGet(SmbFile smbFile, File local) {

        try {
            if (smbFile == null) {
                System.out.println("共享文件不存在");
                return false;
            }
            OutputStream out = new FileOutputStream(local);
            IOUtils.copyLarge(smbFile.getInputStream(), out);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

    }

    /**
     * Description: 从本地上传文件到共享目录
     *
     * @Version1.0 Sep 25, 2009 3:49:00 PM
     */
    public static String smbPut(SmbFile smbFile, File src) {
        String result = null;
        FileInputStream fis = null;
        try {
            src.setReadOnly();
            fis = new FileInputStream(src);
            IOUtils.copyLarge(fis, smbFile.getOutputStream());
            result = "success";
        } catch (Exception e) {
            result = "failed";
            e.printStackTrace();
        } finally {
            try {
                if (fis != null) {
                    fis.close();
                }
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        return result;
    }

    /**
     * Description: 从共享目录删除文件
     *
     * @param remoteUrl 共享目录上的文件路径
     * @Version1.0 Sep 25, 2009 3:48:38 PM
     */
    public static void smbDel(String remoteUrl) {
        try {
            SmbFile remoteFile = new SmbFile(remoteUrl);
            if (remoteFile.exists()) {
                remoteFile.delete();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static boolean smbToSmb(SmbFile smbFile1, SmbFile smbFile2) {
        if (smbFile1 == null) {
            System.out.println("共享文件不存在");
            return false;
        }
        try {
            IOUtils.copyLarge(smbFile1.getInputStream(), smbFile2.getOutputStream());
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static void main(String[] args) throws IOException, InterruptedException {
        //smb://xxx:xxx@192.168.2.188/testIndex/
        //xxx:xxx是共享机器的用户名密码
    /*    File file1 =new File("\\\\160.6.87.216\\backup");
        File files[]=file1.listFiles();
        for(File file2:files){
            System.out.println(file2.getName());
        }*/
        Config.setProperty( "jcifs.smb.client.dfs.disabled", "true" );

        NtlmPasswordAuthentication auth = new NtlmPasswordAuthentication("160.6.87.216", "1396587180@qq.com", "taofei1994");
        String url = "smb://160.6.87.216/backup/";
        SmbFile file = new SmbFile(url,auth);
        for(SmbFile child:file.listFiles()){
            System.out.println(child.getPath());
        }





    }
}


