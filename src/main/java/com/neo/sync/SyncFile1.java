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
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.io.*;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutorService;


@Component
public class SyncFile1 {

    private final Logger log= LoggerFactory.getLogger(this.getClass());

    private ExecutorService es= CustomThreadPoolExecutor.getThreadPoolExecutor();
    private FileInfoService fileInfoService= (FileInfoService) SpringUtil.getBean("fileInfoService");
    private SyncFilePlanService syncFilePlanService=(SyncFilePlanService)SpringUtil.getBean("syncFilePlanService");
    private CacheManager cacheManager=(CacheManager)SpringUtil.getBean("cacheManager");
    private ServerService serverService=(ServerService)SpringUtil.getBean("serverService");
    public void execute(String planId) throws Exception{

        SyncFilePlan syncFilePlan=syncFilePlanService.findById(Integer.parseInt(planId));
        if(syncFilePlan==null){
            log.info("id="+planId+"的同步计划不存在！");
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
        String disk=mapDisk(server1.getUsername(),server1.getPassword(),server1.getIp(),syncFilePlan.getOrginPath());
        File src=new File(disk);
        FtpUtils ftpUtils=new FtpUtils(server2.getIp(),Integer.parseInt(server2.getPort()),server2.getUsername(),server2.getPassword());
        String destPath=syncFilePlan.getTargetPath();

        if(!ftpUtils.changeWorkingDirectory(destPath)){
            ftpUtils.createDirecroty(destPath);
        }
        FilesFilter filter=new FilesFilter(syncFilePlan);

        String prefix=syncFilePlan.getOrginPath().substring(syncFilePlan.getOrginPath().lastIndexOf("\\")+1);
        try {
            checkFileToFtp(prefix,src, ftpUtils, destPath, syncFilePlan,filter,true);
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            ftpUtils.disconnect();
            CMDUtil.exec("net use "+disk+" /del /yes");
        }


    }


    private void ftpToShare(SyncFilePlan syncFilePlan) throws Exception {
        Server server2=syncFilePlan.getOriginServer();
        Server server1=syncFilePlan.getTargetServer();
        String originPath=syncFilePlan.getOriginPath();
        FtpUtils ftpUtils=new FtpUtils(server2.getIp(),Integer.parseInt(server2.getPort()),server2.getUsername(),server2.getPassword());
        // System.out.println(originPath);
        if(!ftpUtils.changeWorkingDirectory(originPath)){
            throw new Exception("ftp源目录不存在!");
        }
        String disk=mapDisk(server1.getUsername(),server1.getPassword(),server1.getIp(),syncFilePlan.getTargetPath());
        String prefix=originPath.substring(0,originPath.lastIndexOf("/")+1);
        try {
            checkFileToLocal(prefix,originPath, ftpUtils, disk, syncFilePlan);
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            ftpUtils.disconnect();
            CMDUtil.exec("net use "+disk+" /del /yes");
        }



    }



    private void shareToShare(SyncFilePlan syncFilePlan) throws Exception {
        Server server1=syncFilePlan.getOriginServer();
        Server server2=syncFilePlan.getTargetServer();
        String disk1=mapDisk(server1.getUsername(),server1.getPassword(),server1.getIp(),syncFilePlan.getOrginPath());
        String disk2=mapDisk(server2.getUsername(),server2.getPassword(),server2.getIp(),syncFilePlan.getTargetPath());
        File src=new File(disk1);
        File dest=new File(disk2);
        FilesFilter filter=new FilesFilter(syncFilePlan);
        String originPath=syncFilePlan.getOrginPath().replace("/","\\");
        String prefix=originPath.substring(originPath.lastIndexOf("\\")+1);

        try {
            checkFile(prefix,src, dest, syncFilePlan,filter,true);
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            CMDUtil.exec("net use "+disk1+" /del /yes");
            CMDUtil.exec("net use "+disk2+" /del /yes");
        }


    }



    private void shareToLocal(SyncFilePlan syncFilePlan) throws Exception {
        Server server=syncFilePlan.getOriginServer();
        String disk=mapDisk(server.getUsername(),server.getPassword(),server.getIp(),syncFilePlan.getOrginPath());
        File src=new File(disk);
        File dest=new File(syncFilePlan.getTargetPath());
        if(!dest.exists()){
            dest.mkdirs();
        }
        FilesFilter filter=new FilesFilter(syncFilePlan);
        String originPath=syncFilePlan.getOrginPath().replace("/","\\");
        String prefix=originPath.substring(originPath.lastIndexOf("\\")+1);
        try {
            checkFile(prefix,src, dest, syncFilePlan,filter,true);
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            CMDUtil.exec("net use "+disk+" /del /yes");
        }


    }


    private void localToShare(SyncFilePlan syncFilePlan) throws Exception {
        File src=new File(syncFilePlan.getOrginPath());
        Server server=syncFilePlan.getTargetServer();

        if(!src.exists()||!src.isDirectory()){
            throw new FileNotFoundException("本地源目录不存在!");
        }
        String disk=mapDisk(server.getUsername(),server.getPassword(),server.getIp(),syncFilePlan.getTargetPath());
        File dest=new File(disk);
        FilesFilter filter=new FilesFilter(syncFilePlan);

        String prefix=src.getAbsolutePath().substring(0,src.getAbsolutePath().lastIndexOf("\\")+1);
        try {
            checkFile(prefix,src, dest, syncFilePlan,filter,false);
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            CMDUtil.exec("net use "+disk+" /del /yes");
        }



    }
    private void localToShareDirectly(SyncFilePlan syncFilePlan) throws FileNotFoundException {
        File src=new File(syncFilePlan.getOrginPath());
        Server server=syncFilePlan.getTargetServer();

        if(!src.exists()||!src.isDirectory()){
            throw new FileNotFoundException("本地源目录不存在!");
        }
        String destPath="\\\\"+server.getIp()+syncFilePlan.getTargetPath().replace("/","\\");
        File dest;
        if(!server.getUsername().equals("")){
            dest=new File(destPath);
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
                        ftpUtils.downloadFile(originPath,ftpFile.getName(),targetPath,filename);
                        File file=new File(targetPath+File.separator+filename);
                        boolean flag=DeCompressUtil.deCompressFile(file.getAbsolutePath(),targetPath,file.getAbsolutePath(),syncFilePlan) ;
                        if(!flag){
                            flag=WinRARUtil.deCompress(file.getAbsolutePath(),targetPath,syncFilePlan);
                        }
                        if(flag&&syncFilePlan.getIsKeepSource()==0){
                            file.delete();
                        }

                    }else {
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
        checkFile(prefix,src, dest, syncFilePlan,filter,false);
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
            checkFileToFtp(prefix,src, ftpUtils, destPath, syncFilePlan,filter,false);
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
    private void checkFileToFtp(String prefix,File src, FtpUtils ftpUtils, String targetPath, SyncFilePlan syncFilePlan,FilenameFilter filter,boolean isMapDisk) throws IOException {
        String originPath=syncFilePlan.getOriginPath().replace("/","\\");
        if(syncFilePlan.getIsScanSubDir()==1||syncFilePlan.getIsScanSubDir()==0&&(src.getAbsolutePath().equals(originPath)||src.isFile()&&src.getParent().equals(originPath))){
            if(src.isDirectory()){

                String files[]=src.list(filter);
                for(String file:files){
                    File srcFile=new File(src,file);
                    String destName=file;
                    if(srcFile.isFile()&&syncFilePlan.getIsTransformFilename()==1) {
                        if(isMapDisk){
                            destName=prefix+srcFile.getAbsolutePath().substring(srcFile.getAbsolutePath().indexOf(":")+1).replace("\\", "_");
                        }else {
                            destName = srcFile.getAbsolutePath().replace(prefix, "").replace("\\", "_");
                        }
                    }
                    String path=targetPath+"/"+destName;
                    checkFileToFtp(prefix,srcFile,ftpUtils,path,syncFilePlan,filter,isMapDisk);
                }
            }else {
                String directoryPath = targetPath.substring(0, targetPath.lastIndexOf("/"));
                String searchPath = src.getAbsolutePath();
                if (isMapDisk) {
                    searchPath = getSharePath(src, syncFilePlan);
                }
                FileInfo fileInfoSrc;
                // if(syncFilePlan.getIsCopyAll()!=1) {
                fileInfoSrc = fileInfoService.findByFilePathAndSyncFilePlan(searchPath, syncFilePlan);
                // }
                String filename=src.getName();
                if(syncFilePlan.getIsTransformFilename()==1){
                    filename=targetPath.substring(targetPath.lastIndexOf("/")+1);
                }
                if (syncFilePlan.getIsCopyAll()==1||fileInfoSrc==null) {
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
                            //将解压后的文件夹上传
                            uploadFileToFtp(new File(tmp), ftpUtils, directoryPath);
                            //删除本地的文件夹
                            DeCompressUtil.delFolder(tmp);
                        }else{ //非压缩文件直接上传
                            ftpUtils.uploadFile(directoryPath,filename,src.getAbsolutePath());
                        }
                        //不需要解压缩直接上传
                    }else{
                        ftpUtils.uploadFile(directoryPath,filename,src.getAbsolutePath());
                    }
                    if (isMapDisk) {
                        saveShareFileInfo(null, src, syncFilePlan);
                    } else {
                        saveFileInfo(null, src, syncFilePlan);
                    }

                } else {
                    if (fileInfoSrc.getLastModifyTime() != src.lastModified() || fileInfoSrc.getFileSize() != src.length()) {
                        if (syncFilePlan.getIsDeCompress() == 1) {
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
                                //将解压后的文件夹上传
                                uploadFileToFtp(new File(tmp), ftpUtils, directoryPath);
                                //删除本地的文件夹
                                DeCompressUtil.delFolder(tmp);
                            }else{ //非压缩文件直接上传
                                ftpUtils.uploadFile(directoryPath,filename,src.getAbsolutePath());
                            }
                        } else {
                            ftpUtils.uploadFile(directoryPath, filename, src.getAbsolutePath());
                        }
                        if (isMapDisk) {

                            saveShareFileInfo(fileInfoSrc, src, syncFilePlan);

                        } else {

                            saveFileInfo(fileInfoSrc, src, syncFilePlan);

                        }
                    }
                }

            }


        }
    }

    private void checkFile(String prefix,File src,File dest,SyncFilePlan syncFilePlan,FilenameFilter filter,boolean isMapDisk) throws Exception {
        String originPath=syncFilePlan.getOriginPath().replace("/","\\");
        if(isMapDisk){
            originPath=src.getAbsolutePath().substring(0,src.getAbsolutePath().indexOf(":")+1)+"\\";
        }
        if(syncFilePlan.getIsScanSubDir()==1||originPath.equals(src.getAbsolutePath())||(src.isFile()&&src.getParent().equals(originPath))){
            if(src.isDirectory()){
                String files[]=src.list(filter);
                for (String file : files) {
                    File srcFile = new File(src, file);
                    String destName=file;
                    if(srcFile.isFile()&&syncFilePlan.getIsTransformFilename()==1) {
                        if(isMapDisk){
                            destName=prefix+srcFile.getAbsolutePath().substring(srcFile.getAbsolutePath().indexOf(":")+1).replace("\\", "_");
                        }else {
                            destName = srcFile.getAbsolutePath().replace(prefix, "").replace("\\", "_");
                        }
                    }
                    File destFile = new File(dest, destName);

                    // 递归复制
                    checkFile(prefix,srcFile, destFile,syncFilePlan,filter,isMapDisk);
                }
            }else {



                String searchPath = src.getAbsolutePath();
                if (isMapDisk) {
                    searchPath = getSharePath(src, syncFilePlan);
                }
                FileInfo fileInfoSrc;
                // if(syncFilePlan.getIsCopyAll()!=1) {
                fileInfoSrc= fileInfoService.findByFilePathAndSyncFilePlan(searchPath, syncFilePlan);
                // }
                if (syncFilePlan.getIsCopyAll()==1||fileInfoSrc==null) {
                    if (!dest.getParentFile().exists()) {
                        dest.getParentFile().mkdirs();
                    }

                    if (syncFilePlan.getIsDeCompress() == 1) {

                        boolean flag=DeCompressUtil.deCompressFile(src.getAbsolutePath(), dest.getParent(), src.getAbsolutePath(), syncFilePlan);
                        if(!flag){
                            flag=WinRARUtil.deCompress(src.getAbsolutePath(),dest.getParent(),syncFilePlan);
                        }
                        if(!flag){
                            copyFile(src, dest);
                        }else{
                            if (syncFilePlan.getIsKeepSource() == 1) {
                                copyFile(src, dest);
                            }
                        }
                    } else {
                        copyFile(src, dest);
                    }
                    if (isMapDisk) {
                        saveShareFileInfo(null, src, syncFilePlan);
                    } else {
                        saveFileInfo(null, src, syncFilePlan);
                    }
                } else {

                    if (fileInfoSrc.getLastModifyTime() != src.lastModified() || fileInfoSrc.getFileSize() != src.length()) {

                        if (DeCompressUtil.isCompressFile(dest) && syncFilePlan.getIsDeCompress() == 1) {
                            if (syncFilePlan.getIsKeepSource() == 1) {
                                copyFile(src, dest);
                            }
                            String dir = dest.getName().substring(0, dest.getName().lastIndexOf("."));
                            String parent=dest.getParent();
                            if(!parent.endsWith(File.separator)){
                                parent+=File.separator;
                            }
                            DeCompressUtil.deCompressFile(src, parent + dir, "", syncFilePlan);
                        } else {
                            copyFile(src, dest);
                        }
                        if (isMapDisk) {
                            saveShareFileInfo(fileInfoSrc, src, syncFilePlan);
                        } else {

                            saveFileInfo(fileInfoSrc, src, syncFilePlan);

                        }
                    }
                }
            }

        }
    }


    private void copyFile(File src,File dest) throws IOException {
        InputStream in = new FileInputStream(src);
        OutputStream out = new FileOutputStream(dest);

        byte[] buffer = new byte[1024];

        int length;

        while ((length = in.read(buffer)) > 0) {
            out.write(buffer, 0, length);
        }
        in.close();
        out.close();
    }

    public void fileChannelCopy(File s, File t) {

        FileInputStream fi = null;

        FileOutputStream fo = null;

        FileChannel in = null;

        FileChannel out = null;

        try {

            fi = new FileInputStream(s);

            fo = new FileOutputStream(t);

            in = fi.getChannel();//得到对应的文件通道

            out = fo.getChannel();//得到对应的文件通道

            in.transferTo(0, in.size(), out);//连接两个通道，并且从in通道读取，然后写入out通道

        } catch (IOException e) {

            e.printStackTrace();

        } finally {

            try {

                fi.close();

                in.close();

                fo.close();

                out.close();

            } catch (IOException e) {

                e.printStackTrace();

            }

        }

    }
    private void ftpToFtp(SyncFilePlan syncFilePlan) throws IOException {
        Server origin=syncFilePlan.getOriginServer();
        Server target=syncFilePlan.getTargetServer();
        String originPath=syncFilePlan.getOriginPath();
        String destPath=syncFilePlan.getTargetPath();
        FtpUtils ftpUtils1=new FtpUtils(origin.getIp(),Integer.parseInt(origin.getPort()),origin.getUsername(),origin.getPassword());
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
                        checkFileFtpToFtp(ftpUtils1, ftpUtils2, originPath + ftpFile.getName(), destPath + "/" + ftpFile.getName(), syncFilePlan);
                    }
                }
            }
        }
    }
    private void checkFileFtpToFtp(FtpUtils ftpUtils1, FtpUtils ftpUtils2, String originPath, String destPath, SyncFilePlan syncFilePlan) throws Exception {
        String parentDir=originPath.substring(originPath.lastIndexOf("/"));
        FilesFilter filesFilter=new FilesFilter(syncFilePlan,parentDir);

        FTPFile []ftpFiles=ftpUtils1.ftpClient.listFiles(originPath,filesFilter);
        for(FTPFile ftpFile:ftpFiles){

            if(ftpFile.isFile()){
                FileInfo fileInfoSrc;
                // if(syncFilePlan.getIsCopyAll()!=1) {
                fileInfoSrc = fileInfoService.findByFilePathAndSyncFilePlan(originPath + "/" + ftpFile.getName(), syncFilePlan);
                //  }
                if(syncFilePlan.getIsCopyAll()==1||fileInfoSrc==null){
                    if(DeCompressUtil.isCompressFile(ftpFile.getName())&&syncFilePlan.getIsDeCompress()==1) {
                        if(syncFilePlan.getIsKeepSource()==1){
                            FtpUtils.FTPToFTP(ftpUtils1,ftpUtils2,originPath,destPath,ftpFile.getName());
                        }
                        //先下载到本地解压，然后上传
                        String classPath=this.getClass().getResource("/").getPath().replace("/","\\").substring(1);
                        String dir=ftpFile.getName().substring(0,ftpFile.getName().lastIndexOf("."));
                        String tmp =  dir + "_temp";
                        ftpUtils1.downloadFile(originPath,ftpFile.getName(),classPath);
                        File depressFile=new File(classPath+ftpFile.getName());
                        if (syncFilePlan.getIsKeepDirTree() == 1) {
                            DeCompressUtil.deCompressFile(depressFile,classPath+dir,"",syncFilePlan);
                            ftpUtils2.changeWorkingDirectory(destPath);
                            uploadFileToFtp(new File(classPath+dir),ftpUtils2,destPath+"/"+dir);
                            depressFile.delete();
                            DeCompressUtil.delFolder(classPath+dir);

                        } else {


                            DeCompressUtil.deCompressFile(depressFile,classPath+dir,classPath+tmp,syncFilePlan);
                            ftpUtils2.changeWorkingDirectory(destPath);

                            uploadFileToFtp(new File(classPath+tmp),ftpUtils2,destPath+"/"+dir);
                            depressFile.delete();


                        }
                    }else {
                        FtpUtils.FTPToFTP(ftpUtils1, ftpUtils2, originPath, destPath, ftpFile.getName());
                    }
                    saveFileInfo(null,ftpFile,originPath,syncFilePlan);
                }else{
                    if(ftpFile.getTimestamp().getTimeInMillis()!=fileInfoSrc.getLastModifyTime()||ftpFile.getSize()!=fileInfoSrc.getFileSize()){

                        if(DeCompressUtil.isCompressFile(ftpFile.getName())&&syncFilePlan.getIsDeCompress()==1) {
                            if(syncFilePlan.getIsKeepSource()==1){
                                FtpUtils.FTPToFTP(ftpUtils1,ftpUtils2,originPath,destPath,ftpFile.getName());
                            }
                            //先下载到本地解压，然后上传
                            String classPath=this.getClass().getResource("/").getPath().replace("/","\\").substring(1);

                            String dir=ftpFile.getName().substring(0,ftpFile.getName().lastIndexOf("."));
                            String tmp =  dir + "_temp";
                            ftpUtils1.downloadFile(originPath,ftpFile.getName(),classPath);
                            File depressFile=new File(classPath+ftpFile.getName());
                            if (syncFilePlan.getIsKeepDirTree() == 1) {
                                DeCompressUtil.deCompressFile(depressFile,classPath+dir,"",syncFilePlan);
                                ftpUtils2.changeWorkingDirectory(destPath);
                                ftpUtils2.createDirecroty(dir);
                                uploadFileToFtp(new File(classPath+dir),ftpUtils2,destPath+"/"+dir);
                                depressFile.delete();
                                DeCompressUtil.delFolder(classPath+dir);

                            } else {


                                DeCompressUtil.deCompressFile(depressFile,classPath+dir,classPath+tmp,syncFilePlan);
                                ftpUtils2.changeWorkingDirectory(destPath);
                                ftpUtils2.createDirecroty(dir);
                                uploadFileToFtp(new File(classPath+tmp),ftpUtils2,destPath+"/"+dir);
                                depressFile.delete();


                            }
                        }else {
                            FtpUtils.FTPToFTP(ftpUtils1, ftpUtils2, originPath, destPath, ftpFile.getName());
                        }
                        saveFileInfo(fileInfoSrc, ftpFile, originPath, syncFilePlan);
                    }
                }
            }else{
                if(ftpFile.getName().equals(".")||ftpFile.getName().equals("..")){
                    continue;
                }
                else if(syncFilePlan.getIsScanSubDir()==1) {
                    checkFileFtpToFtp(ftpUtils1, ftpUtils2, originPath + "/" + ftpFile.getName(), destPath + "/" + ftpFile.getName(), syncFilePlan);
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

    public synchronized static String mapDisk(String username,String password,String host,String path) throws Exception {
        String mappingDisk="";
        for(int i=90;i>64;i--){
            File file=new File((char)i+":");
            if(!file.exists()){
                mappingDisk=(char)i+":";
                break;
            }
        }
        path=path.replace("/","\\");
        String command="net use "+mappingDisk+" \\\\"+host+path;
        if(!StringUtils.isEmpty(username)){
            command+=" "+password+" /user:"+username;
        }
        String result=CMDUtil.exec(command);
        System.out.println(command);
        if(result.equals("")){
            throw new Exception("\\\\"+host+path+"映射到本地"+mappingDisk+"出错！");
        }
        return mappingDisk;
    }




}
