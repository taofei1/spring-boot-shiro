package com.neo.ftp;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.MalformedURLException;
import java.util.*;

public class FtpUtils {

    private Logger log= LoggerFactory.getLogger(getClass());
    private String hostname;
    public FTPClient ftpClient ;

    public String getHostname() {
        return hostname;
    }

    public   FtpUtils(String hostname, Integer port, String username, String password)  {
        ftpClient = new FTPClient();
        ftpClient.setControlEncoding("GBK");
        try {
            ftpClient.connect(hostname, port);
            if("".equals(username)){
                username="anonymous";
            }
            ftpClient.login(username, password);
            ftpClient.enterLocalPassiveMode();
            int replyCode = ftpClient.getReplyCode();
            if(!FTPReply.isPositiveCompletion(replyCode)){
                log.info("connect failed.:"+hostname+":"+port);
            }else {
                this.hostname=hostname;
                log.info("connect successful" + hostname + ":" + port);
            }
        }catch (MalformedURLException e) {
            e.printStackTrace();
        }catch (IOException e) {
            e.printStackTrace();
        }
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
        try{
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
    /**
     *
     * @param pathname
     * @param fileName
     * @param inputStream
     * @return
     */
    public boolean uploadFile( String pathname, String fileName,InputStream inputStream){
        try{
            ftpClient.setFileType(ftpClient.BINARY_FILE_TYPE);
            createDirecroty(pathname);
            ftpClient.makeDirectory(pathname);
            ftpClient.changeWorkingDirectory(pathname);
            ftpClient.storeFile(fileName, inputStream);
            inputStream.close();
            log.info("upload "+pathname+"/"+fileName+" successfully!");
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
    //?????¡¤??
    public boolean changeWorkingDirectory(String directory) {
        boolean flag = true;
        try {
            flag = ftpClient.changeWorkingDirectory(directory);
        } catch (IOException ioe) {
            log.error("go into" + directory + " failed!",ioe);
        }
        return flag;
    }

    public boolean createDirecroty(String remote) throws IOException {
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

    //
    public boolean existFile(String path,String fileName) throws IOException {
        boolean flag = false;
        FTPFile[] ftpFileArr = ftpClient.listFiles(path);

        for(FTPFile ftpFile:ftpFileArr){
            if(ftpFile.getName().equals(fileName)){
                flag=true;
                break;
            }
        }
        return flag;
    }
    public boolean existFile(String path) throws IOException {
        boolean flag = false;
        FTPFile[] ftpFileArr = ftpClient.listFiles(path);

        if(ftpFileArr.length>0){
            flag=true;
        }
        return flag;
    }

    public boolean makeDirectory(String dir) {
        boolean flag;
        try {
            flag = ftpClient.makeDirectory(dir);

        } catch (Exception e) {
            flag=false;
            log.error("make" + dir + " failed??",e);
        }
        return flag;
    }
    public void disconnect() throws IOException {
        this.ftpClient.logout();
        if(ftpClient.isConnected()){
            this.ftpClient.disconnect();
        }

    }

    /**
     * @param pathname
     * @param filename
     * @param localpath
     * @return */
    public  boolean downloadFile(String pathname, String filename, String localpath){
        boolean flag = false;
        OutputStream os=null;
        try {
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
        try {
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
    /**
     * @param pathname
     * @param filename
     * @param os
     * @return */
    public  boolean downloadFile(String pathname, String filename,OutputStream os){
        boolean flag = false;
        try {

            ftpClient.changeWorkingDirectory(pathname);
            ftpClient.retrieveFile(filename, os);
            os.close();
            flag = true;
            log.info("download "+pathname+"/"+filename+" successfully");
        } catch (Exception e) {
            log.error("download "+pathname+"/"+filename+" successfully",e);
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
    /** * ?????? *
     * @param pathname FTP???????????? *
     * @param filename ????????????? *
     * @return */
    public boolean deleteFile(String pathname, String filename){
        boolean flag = false;
        try {
            //?§Ý?FTP??
            ftpClient.changeWorkingDirectory(pathname);
            ftpClient.dele(filename);
            flag = true;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if(ftpClient.isConnected()){
                try{
                    ftpClient.disconnect();
                }catch(IOException e){
                    e.printStackTrace();
                }
            }
        }
        return flag;
    }
    /**
     *
     *
     * @param pathName
     * @throws IOException
     */
    public void list(String pathName) throws IOException {
        if (pathName.startsWith("/") && pathName.endsWith("/")) {

            this.changeWorkingDirectory(pathName);
            FTPFile[] files = ftpClient.listFiles();
            for (FTPFile file : files) {
                if (file.isFile()) {
                  //  System.out.println(file.getGroup());
                   // System.out.println(file.getRawListing());
                } else if (file.isDirectory()) {
                    if (!".".equals(file.getName()) && !"..".equals(file.getName())) {
                        list(pathName + file.getName() + "/");
                    }
                }
            }
        }
    }
    public FTPFile getFTPFileByName(String path,String name) throws IOException {
        FTPFile []ftpFiles=ftpClient.listFiles(path);
        FTPFile file=null;
        for(FTPFile ftpFile:ftpFiles){
            if(ftpFile.getName().equals(name)){
                file=ftpFile;
            }
        }
        return file;
    }
    /**
     *
     *
     * @param pathName
     * @param ext
     * @throws IOException
     */
    public void List(String pathName, String ext) throws IOException {
        if (pathName.startsWith("/") && pathName.endsWith("/")) {

            changeWorkingDirectory(pathName);
            FTPFile[] files = ftpClient.listFiles();
            for (FTPFile file : files) {
                if (file.isFile()) {
                    if (file.getName().endsWith(ext)) {

                        System.out.println(file.getRawListing());
                    }
                } else if (file.isDirectory()) {
                    if (!".".equals(file.getName()) && !"..".equals(file.getName())) {
                        List(pathName + file.getName() + "/", ext);
                    }
                }
            }
        }
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
    public  void listFileName(String dir,String parent) throws IOException {
        FTPFile[] ftpFiles=this.ftpClient.listFiles(dir);
        for(FTPFile ftpFile:ftpFiles){
            if(ftpFile.isDirectory()){
                System.out.println("dir:"+parent+ftpFile.getName());
                listFileName(ftpFile.getName(),parent+ftpFile.getName()+"/");
            }else{
                System.out.println("file:"+parent+ftpFile.getName());
            }
        }
    }
    public static void main(String[] args) throws IOException {

    }
}