package com.neo.ftp;

/**
 * @Auther: 13965
 * @Date: 2018/11/1 16:28
 * @Description:
 * @Version: 1.0
 */

import java.io.*;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * ftp客户端辅助bean
 *
 * @author jelly
 *
 */
public class FTPClientHelper {

    private static Logger log= LoggerFactory.getLogger(FTPClientHelper.class);
    private FTPClientPool  ftpClientPool;

    public void setFtpClientPool(FTPClientPool ftpClientPool) {
        this.ftpClientPool = ftpClientPool;
    }
    /**
     *
     * @param pathname
     * @param fileName
     *  @param originfilename
     * @return
     */
    public boolean uploadFile( String pathname, String fileName,String originfilename){
        InputStream inputStream = null;
        FTPClient ftpClient;
        try{
            ftpClient=ftpClientPool.borrowObject();
            inputStream = new FileInputStream(new File(originfilename));
            ftpClient.setFileType(ftpClient.BINARY_FILE_TYPE);
            createDirecroty(pathname);
            // System.out.println(pathname);
            ftpClient.makeDirectory(pathname);

            ftpClient.changeWorkingDirectory(pathname);
            ftpClient.storeFile(fileName, inputStream);
            inputStream.close();
            // log.info("upload "+pathname+"/"+fileName +" successfully!");
        }catch (Exception e) {
            log.error("upload "+pathname+"/"+fileName +" failed!",e);
        }finally{
            if(null != inputStream){
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return true;
    }

    public boolean changeWorkingDirectory(String directory) {
        boolean flag = true;
        FTPClient ftpClient;
        try{
            ftpClient=ftpClientPool.borrowObject();
            flag = ftpClient.changeWorkingDirectory(directory);
        } catch (Exception ioe) {
            log.error("go into" + directory + " failed!",ioe);
        }
        return flag;
    }

    public boolean createDirecroty(String remote) throws Exception {
        changeWorkingDirectory("/");
        boolean success = true;
        String directory=remote;
        if(!remote.endsWith("/")) {
            directory = remote + "/";
        }
        if (!directory.equalsIgnoreCase("/") && !changeWorkingDirectory(new String(directory))) {
            int start ;
            int end ;
            if (directory.startsWith("/")) {
                start = 1;
            } else {
                start = 0;
            }
            end = directory.indexOf("/", start);
            String path = "";
            String paths = "";
            while (true) {
                //  log.info(ftpClient.printWorkingDirectory());
                String subDirectory = remote.substring(start, end);
                path = path + "/" + subDirectory;
                if (!existFile(path)) {

                    if (makeDirectory(subDirectory)) {
                        changeWorkingDirectory(path);
                    } else {
                        changeWorkingDirectory(path);
                    }
                } else {
                    changeWorkingDirectory(path);
                }
                //  System.out.println(ftpClient.printWorkingDirectory());
                paths = paths + "/" + subDirectory;
                start = end + 1;
                end = directory.indexOf("/", start);

                if (end <= start) {
                    break;
                }
            }
        }
        return success;
    }


    public boolean existFile(String path) throws Exception {
        boolean flag = false;
        FTPClient ftpClient=ftpClientPool.borrowObject();
        FTPFile[] ftpFileArr = ftpClient.listFiles(path);

        if(ftpFileArr.length>0){
            flag=true;
        }
        return flag;
    }

    public boolean makeDirectory(String dir) {
        boolean flag;
        FTPClient ftpClient;
        try{
            ftpClient=ftpClientPool.borrowObject();
            flag = ftpClient.makeDirectory(dir);

        } catch (Exception e) {
            flag=false;
            log.error("make" + dir + " failed??",e);
        }
        return flag;
    }

    /**
     * @param pathname
     * @param filename
     * @param localpath
     * @return */
    public  boolean downloadFile(String pathname, String filename, String localpath){
        boolean flag = false;
        OutputStream os=null;
        FTPClient ftpClient;
        try{
            ftpClient=ftpClientPool.borrowObject();
            ftpClient.changeWorkingDirectory(pathname);
            File parentDir=new File(localpath);
            if(!parentDir.exists()){
                parentDir.mkdirs();
            }
            File localFile = new File(localpath + "/" + filename);
            os = new FileOutputStream(localFile);
            ftpClient.retrieveFile(filename, os);
            os.close();
            flag = true;
            //  log.info("download "+pathname+"/"+filename+" successfully");
        } catch (Exception e) {
            //  log.error("download "+pathname+"/"+filename+" failed!",e);
        } finally{
            if(null != os){
                try {
                    os.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return flag;
    }

    public  boolean downloadFile(String pathname, String filename, String localpath,String name){
        boolean flag = false;
        OutputStream os=null;
        FTPClient ftpClient;
        try{
            ftpClient=ftpClientPool.borrowObject();
            ftpClient.changeWorkingDirectory(pathname);
            File parentDir=new File(localpath);
            if(!parentDir.exists()){
                parentDir.mkdirs();
            }
            File localFile = new File(localpath + "/" + name);
            os = new FileOutputStream(localFile);
            ftpClient.retrieveFile(filename, os);
            os.close();
            flag = true;
            //   log.info("download "+pathname+"/"+filename+" successfully");
        } catch (Exception e) {
            log.error("download "+pathname+"/"+filename+" failed!",e);
        } finally{
            if(null != os){
                try {
                    os.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return flag;
    }



    public static boolean FTPToFTP(FtpUtils ftpUtils1,FtpUtils ftpUtils2,String originPath,String destPath,String fileName) throws IOException {
        ftpUtils1.changeWorkingDirectory(originPath);
        ftpUtils1.ftpClient.enterLocalPassiveMode();
        InputStream is=ftpUtils1.ftpClient.retrieveFileStream(fileName);
        if(is==null){
            throw new FileNotFoundException(originPath+"/"+fileName+" not exist!");
        }

        ftpUtils2.createDirecroty(destPath);
        ftpUtils2.changeWorkingDirectory(destPath);
        ftpUtils2.ftpClient.setFileType(FTPClient.BINARY_FILE_TYPE);

        boolean flag= ftpUtils2.ftpClient.storeFile(fileName,is);
        is.close();
        ftpUtils1.ftpClient.completePendingCommand();
        return flag;
    }


}