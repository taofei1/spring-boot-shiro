package com.neo.sync;

import com.neo.entity.FileInfo;
import com.neo.entity.Server;
import com.neo.entity.SyncFilePlan;
import com.neo.ftp.FtpUtils;
import com.neo.quartz.SpringUtil;
import com.neo.service.FileInfoService;
import com.neo.service.ServerService;
import com.neo.service.SyncFilePlanService;
import com.neo.thread.CustomThreadPoolExecutor;
import com.neo.util.CMDUtil;
import com.neo.util.DeCompressUtil;
import com.neo.util.WinRARUtil;
import org.apache.commons.net.ftp.FTPFile;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Component;

import java.io.*;
import java.util.Date;
import java.util.concurrent.ExecutorService;


@Component
public class SyncFile2 {

    private static final Logger  log= LoggerFactory.getLogger(SyncFile2.class);

    private ExecutorService es= CustomThreadPoolExecutor.getThreadPoolExecutor();
    private FileInfoService fileInfoService= (FileInfoService) SpringUtil.getBean("fileInfoService");
    private SyncFilePlanService syncFilePlanService=(SyncFilePlanService)SpringUtil.getBean("syncFilePlanService");
    private CacheManager cacheManager=(CacheManager)SpringUtil.getBean("cacheManager");
    private ServerService serverService=(ServerService)SpringUtil.getBean("serverService");
    public void execute(String planId) throws Exception{

        SyncFilePlan syncFilePlan=syncFilePlanService.findById(Integer.parseInt(planId));
        if(syncFilePlan==null){
            log.info("id={}的同步计划不存在！",planId);
            return ;
        }
        //本地--本地
        if (syncFilePlan.getOriginId() ==-1 && syncFilePlan.getTargetId() == -1) {

            localToLocal(syncFilePlan);

        }//本地--ftp
        else if (syncFilePlan.getOriginId() == -1 && syncFilePlan.getTargetServer() != null && syncFilePlan.getTargetServer().getType().equals("1")) {
            localToFtp(syncFilePlan);
        }//ftp--本地
        else if (syncFilePlan.getOriginId() != -1 && syncFilePlan.getOriginServer().getType().equals("1") && syncFilePlan.getTargetId() == -1) {
            ftpToLocal(syncFilePlan);

            //ftp--ftp
        } else if (syncFilePlan.getOriginId() != -1 && syncFilePlan.getOriginServer().getType().equals("1") && syncFilePlan.getTargetServer() != null && syncFilePlan.getTargetServer().getType().equals("1")) {
            ftpToFtp(syncFilePlan);

            //本地--远程目录
        } else if (syncFilePlan.getOriginId() == -1 && syncFilePlan.getTargetServer() != null && syncFilePlan.getTargetServer().getType().equals("2")) {
            localToShare(syncFilePlan);
            //远程目录--本地
        }else if(syncFilePlan.getOriginId()!=-1 && syncFilePlan.getOriginServer().getType().equals("2")&&syncFilePlan.getTargetId()==-1){
            shareToLocal(syncFilePlan);
            //远程目录--远程目录
        }else if(syncFilePlan.getOriginServer()!=null && syncFilePlan.getOriginServer().getType().equals("2")&&syncFilePlan.getTargetServer()!=null&&syncFilePlan.getTargetServer().getType().equals("2")){
            shareToShare(syncFilePlan);
            //ftp--远程目录
        }else if(syncFilePlan.getOriginServer()!=null && syncFilePlan.getOriginServer().getType().equals("1")&&syncFilePlan.getTargetServer()!=null&&syncFilePlan.getTargetServer().getType().equals("2")) {
            ftpToShare(syncFilePlan);
            //远程目录--ftp
        }else if(syncFilePlan.getOriginServer()!=null && syncFilePlan.getOriginServer().getType().equals("2")&&syncFilePlan.getTargetServer()!=null&&syncFilePlan.getTargetServer().getType().equals("1")){
            shareToFtp(syncFilePlan);
        }else{
            throw new Exception("源服务器或者目的服务器配置有误！");
        }
    }

    private void shareToFtp(SyncFilePlan syncFilePlan) throws Exception {
        Server server1=syncFilePlan.getOriginServer();
        Server server2=syncFilePlan.getTargetServer();
        String url="\\\\"+server1.getIp()+syncFilePlan.getOrginPath().replace("/","\\");
        boolean flag=false;
        if(!"".equals(server1.getUsername())){
            loginShare(server1.getIp(),syncFilePlan.getOrginPath(),server1.getUsername(),server1.getPassword());
            flag=true;
        }
        File src=new File(url);
        FtpUtils ftpUtils=new FtpUtils(server2.getIp(),Integer.parseInt(server2.getPort()),server2.getUsername(),server2.getPassword());
        String destPath=syncFilePlan.getTargetPath();

        if(!ftpUtils.changeWorkingDirectory(destPath)){
            ftpUtils.createDirecroty(destPath);
        }
        FilesFilter filter=new FilesFilter(syncFilePlan);

        String prefix="\\\\"+server1.getIp()+"\\";
        try {
            checkFileToFtp(prefix,src, ftpUtils, destPath, syncFilePlan,filter);
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            ftpUtils.disconnect();
            if(flag){
                logoutShare(server1.getIp(),syncFilePlan.getOrginPath());
            }
        }


    }


    private void ftpToShare(SyncFilePlan syncFilePlan) throws Exception {
        Server server2=syncFilePlan.getOriginServer();
        Server server1=syncFilePlan.getTargetServer();
        String originPath=syncFilePlan.getOriginPath();
        FtpUtils ftpUtils=new FtpUtils(server2.getIp(),Integer.parseInt(server2.getPort()),server2.getUsername(),server2.getPassword());
        if(!ftpUtils.changeWorkingDirectory(originPath)){
            throw new Exception("ftp源目录不存在!");
        }
        String url="\\\\"+server1.getIp()+syncFilePlan.getTargetPath().replace("/","\\");
        boolean flag=false;
        if(!"".equals(server1.getUsername())){
            loginShare(server1.getIp(),syncFilePlan.getTargetPath(),server1.getUsername(),server1.getPassword());
            flag=true;
        }

        String prefix=originPath.substring(0,originPath.lastIndexOf("/")+1);
        try {
            checkFileToLocal(prefix,originPath, ftpUtils, url, syncFilePlan);
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            ftpUtils.disconnect();
            if(flag){
                logoutShare(server1.getIp(),syncFilePlan.getTargetPath());
            }
        }



    }



    private void shareToShare(SyncFilePlan syncFilePlan)   {
        Server server1=syncFilePlan.getOriginServer();
        Server server2=syncFilePlan.getTargetServer();
        String url1="\\\\"+server1.getIp()+syncFilePlan.getOrginPath().replace("/","\\");
        String url2="\\\\"+server2.getIp()+syncFilePlan.getTargetPath().replace("/","\\");

        boolean flag1=false;
        boolean flag2=false;
        if(!"".equals(server1.getUsername())){
            loginShare(server1.getIp(),syncFilePlan.getOrginPath(),server1.getUsername(),server1.getPassword());
            flag1=true;
        }
        if(!"".equals(server2.getUsername())){
            loginShare(server2.getIp(),syncFilePlan.getTargetPath(),server2.getUsername(),server2.getPassword());
            flag2=true;
        }
        File src=new File(url1);
        File dest=new File(url2);
        FilesFilter filter=new FilesFilter(syncFilePlan);
        String prefix="\\\\"+server1.getIp()+"\\";

        try {
            checkFile(prefix,src, dest, syncFilePlan,filter);
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            if(flag1){
                logoutShare(server1.getIp(),syncFilePlan.getOrginPath());
            }
            if(flag2){
                logoutShare(server2.getIp(),syncFilePlan.getTargetPath());
            }
        }


    }



    private void shareToLocal(SyncFilePlan syncFilePlan)   {
        Server server=syncFilePlan.getOriginServer();
        String url="\\\\"+server.getIp()+syncFilePlan.getOrginPath().replace("/","\\");
        boolean flag=false;
        if(!"".equals(server.getUsername())){
            loginShare(server.getIp(),syncFilePlan.getOrginPath(),server.getUsername(),server.getPassword());
            flag=true;
        }
        File src=new File(url);
        File dest=new File(syncFilePlan.getTargetPath());
        if(!dest.exists()){
            dest.mkdirs();
        }
        FilesFilter filter=new FilesFilter(syncFilePlan);
        String prefix="\\\\"+server.getIp()+"\\";
        try {
            checkFile(prefix,src, dest, syncFilePlan,filter);
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            if(flag){
                logoutShare(server.getIp(),syncFilePlan.getOrginPath());
            }
        }


    }


    private void localToShare(SyncFilePlan syncFilePlan) throws Exception {
        File src=new File(syncFilePlan.getOrginPath());
        Server server=syncFilePlan.getTargetServer();

        if(!src.exists()||!src.isDirectory()){
            throw new FileNotFoundException("本地源目录不存在!");
        }
        String url="\\\\"+server.getIp()+syncFilePlan.getTargetPath().replace("/","\\");
        boolean flag=false;
        if(!"".equals(server.getUsername())){
            loginShare(server.getIp(),syncFilePlan.getTargetPath(),server.getUsername(),server.getPassword());
            flag=true;
        }
        File dest=new File(url);
        if(!dest.exists()){
           throw new FileNotFoundException("共享目的目录不存在!");
        }
        FilesFilter filter=new FilesFilter(syncFilePlan);

        String prefix=src.getAbsolutePath().substring(0,src.getAbsolutePath().lastIndexOf("\\")+1);
        try {
            checkFile(prefix,src, dest, syncFilePlan,filter);
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            if(flag){
                logoutShare(server.getIp(),syncFilePlan.getTargetPath());
            }
        }



    }



    private void ftpToLocal(SyncFilePlan syncFilePlan) throws Exception {
        //首次同步，直接上传
        Server server=syncFilePlan.getOriginServer();
        File target=new File(syncFilePlan.getTargetPath());
        String originPath=syncFilePlan.getOriginPath();
        if(!target.exists()){
            target.mkdirs();
        }
        FtpUtils ftpUtils=new FtpUtils(server.getIp(),Integer.parseInt(server.getPort()),server.getUsername(),server.getPassword());
        if(!ftpUtils.changeWorkingDirectory(originPath)){
            throw new Exception("ftp源目录不存在!");
        }
        String prefix=originPath.substring(0,originPath.lastIndexOf("/")+1);
        try {
            checkFileToLocal(prefix,syncFilePlan.getOrginPath(), ftpUtils, syncFilePlan.getTargetPath(), syncFilePlan);
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            ftpUtils.disconnect();
        }



    }


    private void checkFileToLocal(String prefix,String originPath, FtpUtils ftpUtils, String targetPath, SyncFilePlan syncFilePlan) throws Exception {

        targetPath=targetPath.replace("/","\\");
        String parentDir ;
        if(!originPath.endsWith("/")){
            parentDir=originPath.substring(originPath.lastIndexOf("/"));
            originPath+="/";
        }else{
            String path=originPath.substring(0,originPath.length()-1);
            parentDir=path.substring(path.lastIndexOf("/")+1);
        }

        FilesFilter filesFilter=new FilesFilter(syncFilePlan,parentDir);
        FTPFile []ftpFiles=ftpUtils.ftpClient.listFiles(originPath,filesFilter);
        for(FTPFile ftpFile:ftpFiles){

            if(ftpFile.isFile()){
               FileInfo fileInfoSrc;
               // if(syncFilePlan.getIsCopyAll()!=1) {
                    fileInfoSrc = fileInfoService.findByFilePathAndSyncFilePlan(originPath  + ftpFile.getName(), syncFilePlan);
               // }
                String filename=ftpFile.getName();
                if(syncFilePlan.getIsTransformFilename()==1){
                    filename=originPath.substring(prefix.length()).replace("/","_")+filename;
                }
                if(syncFilePlan.getIsCopyAll()==1||fileInfoSrc==null){
                    if(syncFilePlan.getIsDeCompress()==1){
                        //文件下载到本地
                        ftpUtils.downloadFile(originPath,ftpFile.getName(),targetPath,filename);
                        File file=new File(targetPath+File.separator+filename);
                        //尝试java类解压
                        boolean flag=DeCompressUtil.deCompressFile(file.getAbsolutePath(),targetPath,file.getAbsolutePath(),syncFilePlan) ;
                        //若解压失败则尝试winrar解压(若解压不成功可认为此文件非压缩文件)
                        if(!flag){
                            flag=WinRARUtil.deCompress(file.getAbsolutePath(),targetPath,syncFilePlan);
                        }
                        //如果解压成功且设置了不保留源文件则将压缩文件删除
                        if(flag&&syncFilePlan.getIsKeepSource()==0){
                            file.delete();
                        }

                    }else {
                        //不需要解压则直接下载就完事了
                        ftpUtils.downloadFile(originPath,ftpFile.getName(),targetPath,filename);

                    }

                   saveFileInfo(null, ftpFile, originPath, syncFilePlan);


                }else{

                    if(ftpFile.getTimestamp().getTimeInMillis()!=fileInfoSrc.getLastModifyTime()||ftpFile.getSize()!=fileInfoSrc.getFileSize()){
                        ftpUtils.downloadFile(originPath,filename,targetPath);
                        saveFileInfo(fileInfoSrc, ftpFile, originPath, syncFilePlan);
                        File file=new File(targetPath+File.separator+filename);
                        if(syncFilePlan.getIsDeCompress()==1){
                            boolean flag=DeCompressUtil.deCompressFile(file.getAbsolutePath(),targetPath,file.getAbsolutePath(),syncFilePlan);
                            if(!flag){
                                flag=WinRARUtil.deCompress(file.getAbsolutePath(),targetPath,syncFilePlan);
                            }
                            if(flag&&syncFilePlan.getIsKeepSource()==0){
                                file.delete();
                            }
                        }
                    }
                }
            }else{
                //隐藏文件夹，会无限包含，导致异常
               if(ftpFile.getName().equals(".")||ftpFile.getName().equals("..")){
                   continue;
               }else if(syncFilePlan.getIsScanSubDir()==1) {
                    checkFileToLocal(prefix,originPath + ftpFile.getName(), ftpUtils, targetPath + "/" + ftpFile.getName(), syncFilePlan);
                }
            }
        }
    }




    private  void localToLocal(SyncFilePlan syncFilePlan) throws Exception {
        //首次同步，直接复制
        File src=new File(syncFilePlan.getOrginPath());
        File dest=new File(syncFilePlan.getTargetPath());
        if(!src.exists()||!src.isDirectory()){
            throw new FileNotFoundException("本地源目录不存在!");

        }
        if(!dest.exists()){
            dest.mkdirs();
        }
        FilesFilter filter=new FilesFilter(syncFilePlan);
        String prefix=src.getAbsolutePath().substring(0,src.getAbsolutePath().lastIndexOf("\\")+1);
        checkFile(prefix,src, dest, syncFilePlan,filter);
    }
    private void localToFtp(SyncFilePlan syncFilePlan) throws Exception {
        Server server=serverService.findById(syncFilePlan.getTargetId());
        File src=new File(syncFilePlan.getOrginPath());
        String destPath=syncFilePlan.getTargetPath();
        FtpUtils ftpUtils= new FtpUtils(server.getIp(),Integer.parseInt(server.getPort()),server.getUsername(),server.getPassword());
        if(!src.exists()||!src.isDirectory()){
            throw new Exception("源目录不存在!");
        }
        if(!ftpUtils.changeWorkingDirectory(destPath)){
            ftpUtils.createDirecroty(destPath);
        }
        FilesFilter filter=new FilesFilter(syncFilePlan);
        String prefix=src.getAbsolutePath().substring(0,src.getAbsolutePath().lastIndexOf("\\")+1);

        try {
            checkFileToFtp(prefix,src, ftpUtils, destPath, syncFilePlan,filter);
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            ftpUtils.disconnect();
        }

    }
    private void uploadFileToFtp(File src, FtpUtils ftpUtils, String targetPath )  {
        if(src.isDirectory()){

            String files[]=src.list();
            for(String file:files){
                File srcFile=new File(src,file);
                if(!targetPath.endsWith("/")){
                    targetPath+="/";
                }
                String path=targetPath+srcFile.getName();
                uploadFileToFtp(srcFile,ftpUtils,path);
            }
        }else{
            String directoryPath=targetPath.substring(0,targetPath.lastIndexOf("/"));

            ftpUtils.uploadFile(directoryPath,src.getName(),src.getAbsolutePath());

        }
    }

    private void checkFileToFtp(String prefix,File src, FtpUtils ftpUtils, String targetPath, SyncFilePlan syncFilePlan,FilenameFilter filter ) throws IOException {
       // ftpUtils.reconnected();
        String originPath=syncFilePlan.getOriginPath().replace("/","\\");
        if(syncFilePlan.getIsScanSubDir()==1||syncFilePlan.getIsScanSubDir()==0&&(src.getAbsolutePath().equals(originPath)||src.isFile()&&src.getParent().equals(originPath))){
            if(src.isDirectory()){

                String files[]=src.list(filter);
                for(String file:files){
                    File srcFile=new File(src,file);
                    String destName=file;
                    if(srcFile.isFile()&&syncFilePlan.getIsTransformFilename()==1) {
                            destName = srcFile.getAbsolutePath().replace(prefix, "").replace("\\", "_");
                    }
                    String path=targetPath+"/"+destName;
                    checkFileToFtp(prefix,srcFile,ftpUtils,path,syncFilePlan,filter);
                }
            }else {
                String directoryPath = targetPath.substring(0, targetPath.lastIndexOf("/"));
                String searchPath = src.getAbsolutePath();

                FileInfo fileInfoSrc;
               // if(syncFilePlan.getIsCopyAll()!=1) {
                    fileInfoSrc = fileInfoService.findByFilePathAndSyncFilePlan(searchPath, syncFilePlan);
               // }
                String filename=src.getName();
                if(syncFilePlan.getIsTransformFilename()==1){
                    filename=targetPath.substring(targetPath.lastIndexOf("/")+1);
                }
                //完全复制：不管文件修改与否，复制-插入记录；默认 新文件复制插入记录；修改过的文件：修改属性；
                if (syncFilePlan.getIsCopyAll()==1||fileInfoSrc==null||fileInfoSrc.getLastModifyTime() != src.lastModified() || fileInfoSrc.getFileSize() != src.length()) {
                    if ( syncFilePlan.getIsDeCompress() == 1) {
                        String dir = src.getName().substring(0, src.getName().lastIndexOf("."));
                        //解压到本地
                        String tmp = src.getParent() + File.separator + dir + "_temp";

                        boolean flag=DeCompressUtil.deCompressFile(src.getAbsolutePath(),tmp,src.getAbsolutePath(),syncFilePlan);
                        if(!flag){
                            flag=WinRARUtil.deCompress(src.getAbsolutePath(),tmp,syncFilePlan);
                        }
                        //是压缩文件
                        if(flag) {
                            //压缩文件需要上传
                            if(syncFilePlan.getIsKeepSource()==1){
                                ftpUtils.uploadFile(directoryPath,filename,src.getAbsolutePath());
                            }
                            //将解压后的文件夹及文件上传
                            uploadFileToFtp(new File(tmp), ftpUtils, directoryPath);
                            //删除本地的文件夹
                            DeCompressUtil.delFolder(tmp);
                        }else{ //解压失败（1.非压缩文件2.因为各种原因）直接上传
                            ftpUtils.uploadFile(directoryPath,filename,src.getAbsolutePath());
                        }
                        //不需要解压缩直接上传
                    }else{
                        ftpUtils.uploadFile(directoryPath,filename,src.getAbsolutePath());
                    }
                        if(syncFilePlan.getIsCopyAll()==1){
                            saveFileInfo(null,src,syncFilePlan);
                        }else {
                            saveFileInfo(fileInfoSrc, src, syncFilePlan);
                        }

                }

            }


        }
    }

    private void checkFile(String prefix,File src,File dest,SyncFilePlan syncFilePlan,FilenameFilter filter )   {
        String originPath=syncFilePlan.getOriginPath().replace("/","\\");
        //扫描子目录||是第一层目录||是第一层目录下的文件
        if(syncFilePlan.getIsScanSubDir()==1||originPath.equals(src.getAbsolutePath())||(src.isFile()&&src.getParent().equals(originPath))){
            if(src.isDirectory()){
                String files[]=src.list(filter);
                for (String file : files) {
                    File srcFile = new File(src, file);
                    String destName=file;
                    if(srcFile.isFile()&&syncFilePlan.getIsTransformFilename()==1) {
                        destName = srcFile.getAbsolutePath().replace(prefix, "").replace("\\", "_");
                    }
                    File destFile = new File(dest, destName);

                    // 递归复制
                    checkFile(prefix,srcFile, destFile,syncFilePlan,filter);
                }
            }else {



                String searchPath = src.getAbsolutePath();
                FileInfo fileInfoSrc;
               // if(syncFilePlan.getIsCopyAll()!=1) {
                fileInfoSrc= fileInfoService.findByFilePathAndSyncFilePlan(searchPath, syncFilePlan);
               // }
                if (syncFilePlan.getIsCopyAll()==1||fileInfoSrc==null||fileInfoSrc.getLastModifyTime() != src.lastModified() || fileInfoSrc.getFileSize() != src.length()) {
                    if (!dest.getParentFile().exists()) {
                        dest.getParentFile().mkdirs();
                    }

                    if (syncFilePlan.getIsDeCompress() == 1) {


                        boolean flag=DeCompressUtil.deCompressFile(src.getAbsolutePath(), dest.getParent(), src.getAbsolutePath(), syncFilePlan);
                        if(!flag){
                            try {
                                flag=WinRARUtil.deCompress(src.getAbsolutePath(),dest.getParent(),syncFilePlan);
                            } catch (IOException e) {
                                log.error(src.getAbsolutePath()+"解压失败！",e);
                            }
                        }
                        if (!flag||syncFilePlan.getIsKeepSource() == 1) {

                            copyFile(src, dest);

                        }

                    } else {
                        copyFile(src, dest);

                    }
                        if(syncFilePlan.getIsCopyAll()==1) {
                            saveFileInfo(null, src, syncFilePlan);
                        }else{
                            saveFileInfo(fileInfoSrc,src,syncFilePlan);
                        }

                }
            }

        }
    }


    public static boolean copyFile(File src,File dest)   {
        boolean result=true;
        try {
            InputStream in = new FileInputStream(src);
            OutputStream out = new FileOutputStream(dest);

            byte[] buffer = new byte[1024];

            int length;

            while ((length = in.read(buffer)) > 0) {
                out.write(buffer, 0, length);
            }
            in.close();
            out.close();
        }catch (IOException e){
            log.error(src.getAbsolutePath()+"复制失败！",e);
            result=false;
        }finally {
            return result;
        }
    }

    private void ftpToFtp(SyncFilePlan syncFilePlan) throws IOException {
        Server origin=syncFilePlan.getOriginServer();
        Server target=syncFilePlan.getTargetServer();
        String originPath=syncFilePlan.getOriginPath();
        String destPath=syncFilePlan.getTargetPath();
        FtpUtils ftpUtils1= new FtpUtils(origin.getIp(),Integer.parseInt(origin.getPort()),origin.getUsername(),origin.getPassword());
        FtpUtils ftpUtils2=new FtpUtils(target.getIp(),Integer.parseInt(target.getPort()),target.getUsername(),target.getPassword());
        if(!ftpUtils1.changeWorkingDirectory(originPath)){
            throw new FileNotFoundException(originPath+"源目录不存在或无法访问!");
        }
        if(!ftpUtils2.changeWorkingDirectory(destPath)){
            ftpUtils2.createDirecroty(destPath);
        }

        String prefix=originPath.substring(0,originPath.lastIndexOf("/")+1);
        try {
            checkFileFtpToFtp(prefix,ftpUtils1,ftpUtils2,originPath,destPath,syncFilePlan);
        } catch (Exception e) {
            e.printStackTrace();
        }finally {

            ftpUtils1.disconnect();
            ftpUtils2.disconnect();
        }

    }
    private void checkFileFtpToFtp(String prefix,FtpUtils ftpUtils1, FtpUtils ftpUtils2, String originPath, String destPath, SyncFilePlan syncFilePlan) throws Exception {
        String classPath=this.getClass().getResource("/").getPath().replace("/","\\").substring(1);
        String localPath=classPath+destPath.substring(destPath.lastIndexOf("/")+1);
        String parentDir;
        if(!originPath.endsWith("/")){
            parentDir=originPath.substring(originPath.lastIndexOf("/"));
            originPath+="/";
        }else{
            String path=originPath.substring(0,originPath.length()-1);
            parentDir=path.substring(path.lastIndexOf("/")+1);
        }
        if(syncFilePlan.getIsDeCompress()==1){
            checkFileToLocal(prefix,originPath,ftpUtils1,localPath,syncFilePlan);
            uploadFileToFtp(new File(localPath),ftpUtils2,destPath);
            DeCompressUtil.delFolder(classPath+localPath);
        }else{
            FilesFilter filesFilter=new FilesFilter(syncFilePlan,parentDir);
            FTPFile []ftpFiles=ftpUtils1.ftpClient.listFiles(originPath,filesFilter);
            for(FTPFile ftpFile:ftpFiles){
                if(ftpFile.isFile()){
                    FileInfo fileInfoSrc;
                    //if(syncFilePlan.getIsCopyAll()!=1) {
                        fileInfoSrc= fileInfoService.findByFilePathAndSyncFilePlan(originPath + ftpFile.getName(), syncFilePlan);
                  //  }
                    if(syncFilePlan.getIsCopyAll()==1||fileInfoSrc==null){
                        FtpUtils.FTPToFTP(ftpUtils1, ftpUtils2, originPath, destPath, ftpFile.getName());

                        saveFileInfo(null,ftpFile, originPath,syncFilePlan);
                    }else{
                        if(ftpFile.getTimestamp().getTimeInMillis()!=fileInfoSrc.getLastModifyTime()||ftpFile.getSize()!=fileInfoSrc.getFileSize()){
                            FtpUtils.FTPToFTP(ftpUtils1, ftpUtils2, originPath, destPath, ftpFile.getName());
                            saveFileInfo(fileInfoSrc, ftpFile, originPath, syncFilePlan);

                        }
                    }
                } else{
                      if(ftpFile.getName().equals(".")||ftpFile.getName().equals("..")){
                        continue;
                    }
                    else if(syncFilePlan.getIsScanSubDir()==1) {
                        checkFileFtpToFtp(prefix,ftpUtils1, ftpUtils2, originPath + ftpFile.getName(), destPath + "/" + ftpFile.getName(), syncFilePlan);
                    }
                }
            }
        }
    }

    private void saveFileInfo(FileInfo info,File file ,SyncFilePlan syncFilePlan){
        //es.execute(()-> {
            FileInfo fileInfo = info;
            if (fileInfo == null) {
                fileInfo = new FileInfo(file.getName(), file.getAbsolutePath(), file.length(), file.lastModified(), syncFilePlan);
            } else {
                fileInfo.setFileSize(file.length());
                fileInfo.setLastModifyTime(file.lastModified());
                fileInfo.setUpdateTime(new Date());
            }
            fileInfoService.save(fileInfo);
      //  });
         //  Cache cache=cacheManager.getCache("fileInfo");
        //   cache.put("file_"+fileInfo.getFilePath()+'_'+syncFilePlan.getId(),fileInfo);

    }
    private void saveShareFileInfo(FileInfo info,File file,SyncFilePlan syncFilePlan){
      //  es.execute(()-> {
                    FileInfo fileInfo = info;
                    if (fileInfo == null)

                    {
                        fileInfo = new FileInfo(file.getName(), getSharePath(file, syncFilePlan), file.length(), file.lastModified(), syncFilePlan);
                    } else

                    {
                        fileInfo.setFileSize(file.length());
                        fileInfo.setLastModifyTime(file.lastModified());
                        fileInfo.setUpdateTime(new Date());
                    }

                    fileInfoService.save(fileInfo);
            //    });

        //    Cache cache=cacheManager.getCache("fileInfo");
          // cache.put("file_"+fileInfo.getFilePath()+'_'+syncFilePlan.getId(),fileInfo);

    }
    private String getSharePath(File file ,SyncFilePlan syncFilePlan){
        String absolutePath=file.getAbsolutePath();
        String path1=syncFilePlan.getOrginPath().replace("/","\\");
        String  path=path1+absolutePath.substring(absolutePath.indexOf(":")+1);
        return path;
    }
    private void saveFileInfo(FileInfo info,FTPFile file,String ftpPath,SyncFilePlan syncFilePlan){
        if (!ftpPath.endsWith("/")) {
            ftpPath += "/";
        }
        String finalFtpPath = ftpPath;
      //  es.execute(()-> {
            FileInfo fileInfo = info;

            if (info == null) {
                fileInfo = new FileInfo(file.getName(), finalFtpPath + file.getName(), file.getSize(), file.getTimestamp().getTimeInMillis(), syncFilePlan);
            } else {
                fileInfo.setFileSize(file.getSize());
                fileInfo.setLastModifyTime(file.getTimestamp().getTimeInMillis());
                fileInfo.setUpdateTime(new Date());
            }
            fileInfoService.save(fileInfo);
     //   });
      //  cacheManager.getCache("fileInfo").put("file_"+fileInfo.getFilePath()+'_'+syncFilePlan.getId(),fileInfo);

    }
    public static void loginShare(String host,String path,String username,String password){
        path=path.replace("/","\\");
        if(!path.startsWith("\\")){
            path="\\"+path;
        }
        String command="net use \\\\"+host+path+" "+password+" /user:"+username;
        try {
             CMDUtil.exec(command);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public static void logoutShare(String host,String path)   {
        path=path.replace("/","\\");
        if(!path.startsWith("\\")){
            path="\\"+path;
        }
        String command="net use \\\\"+host+path+" /del";
        try {
            CMDUtil.exec(command);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public static boolean isShareUrl(String url){
        String reg="\\\\\\\\((2[0-4]\\d|25[0-5]|[01]?\\d\\d?)\\.){3}(2[0-4]\\d|25[0-5]|[01]?\\d\\d?)\\\\\\S+";
        return url.matches(reg);
    }
    public synchronized static boolean isExists(File file){
        File[] files = File.listRoots();
        boolean flag=false;
        for(File f:files){
            if(f.getAbsolutePath().equalsIgnoreCase(file.getAbsolutePath())){
                flag=true;
                break;
            }
        }
        return flag;
    }




}
