package com.neo.util;
import java.io.*;
import java.nio.file.Files;
import java.util.*;

import com.neo.entity.SyncFilePlan;
import com.neo.sync.FilesFilter;
import de.innosystec.unrar.NativeStorage;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.taskdefs.Expand;

import de.innosystec.unrar.Archive;
import de.innosystec.unrar.rarfile.FileHeader;
import org.apache.tools.zip.ZipEntry;
import org.apache.tools.zip.ZipFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.ResourceUtils;

public class DeCompressUtil {
    private static final Logger log= LoggerFactory.getLogger(DeCompressUtil.class);
    private static List<String> list=new ArrayList<>();

    private static boolean unZip (String srcPath,String dstPath, String sourceFile,SyncFilePlan syncFilePlan)  {
           ZipFile zipFile;
        InputStream is = null;
        BufferedInputStream bis = null;
        FileOutputStream fos = null;
        BufferedOutputStream bos = null;
           boolean flag=true;

        try {
            zipFile = new ZipFile(srcPath, "GBK");
        } catch (IOException e) {

           e.printStackTrace();
           return false;
        }
        Enumeration emu = zipFile.getEntries();
        try {

            while (emu.hasMoreElements()) {

                    ZipEntry entry = (ZipEntry) emu.nextElement();
                    if (!entry.isDirectory()) {
                        File file;
                        String name=entry.getName().replace("/","\\");
                        if(syncFilePlan.getIsKeepDirTree()==1) {
                           file= new File(dstPath + File.separator + name);
                            File parent = file.getParentFile();
                            if (!parent.exists()) {
                                parent.mkdirs();
                            }
                        }else{
                            srcPath=srcPath.replace("/","\\");
                            String sourcename=srcPath.substring(srcPath.lastIndexOf("\\")+1);
                            if(entry.getName().contains("\\")) {

                                 name = name.substring(name.lastIndexOf("\\")+1);
                            }
                            if(syncFilePlan.getIsTransformFilename()==1){
                                if(sourcename.contains("_")){
                                    sourcename=sourcename.substring(0,sourcename.lastIndexOf("_")+1);
                                    name=sourcename+name.replace("\\","_");
                                }else {
                                    name = name.replace("\\", "_");
                                }

                            }
                            file=new File(dstPath+name);
                        }
                        is = zipFile.getInputStream(entry);
                        bis = new BufferedInputStream(is);
                        //关键:创建父文件,mkdirs是创建多层目录.

                        fos = new FileOutputStream(file);
                        bos = new BufferedOutputStream(fos, 10240);
                        byte[] buf = new byte[10240];
                        int length ;
                        while ((length = bis.read(buf, 0, 10240)) != -1) {
                            bos.write(buf, 0, length);
                            bos.flush();
                        }
                        bos.close();
                        fos.close();
                        bis.close();
                        is.close();
                        deCompressFile(file.getAbsolutePath(),dstPath,sourceFile,syncFilePlan);
                    }


                }
                zipFile.close();
                }catch (IOException e) {
                    flag=false;
                  e.printStackTrace();
                } finally {
                    try {
                        if(zipFile!=null){
                            zipFile.close();
                        }
                        if (bos != null) {
                            bos.close();
                        }
                        if (fos != null) {
                            fos.close();
                        }
                        if (bis != null) {
                            bis.close();
                        }
                        if (is != null) {
                            is.close();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            return flag;

    }
    public static boolean deCompressFile(String source,String dest,String sourceFile,SyncFilePlan syncFilePlan){
        if(!dest.endsWith(File.separator)){
            dest+=File.separator;
        }
            boolean flag=true;
            if(!unZip(source,dest,sourceFile,syncFilePlan)){
                if(!unrar(source,dest,sourceFile,syncFilePlan)) {
                    flag=false;
                    log.info(source + "未成功解压!"); }

            }
            if(flag){
                if(!source.equals(sourceFile)) {
                    new File(source).delete();
                }
            }else if(syncFilePlan.getIsFilterCompressFile()==1){
                File file=new File(source);
                if(!new FilesFilter(syncFilePlan).accept(source)){
                    file.delete();
                    file.getParentFile().delete();
                }
            }
            return flag;
    }
        /**
         * 解压zip格式压缩包
         * 对应的是ant.jar
         */
    private static void unzip(String sourceZip,String destDir)  {
        try{
            Project p = new Project();
            Expand e = new Expand();
            e.setProject(p);
            e.setSrc(new File(sourceZip));
            e.setOverwrite(false);
            e.setDest(new File(destDir));
           /*
           ant下的zip工具默认压缩编码为UTF-8编码，
           而winRAR软件压缩是用的windows默认的GBK或者GB2312编码
           所以解压缩时要制定编码格式
           */
            e.setEncoding("gbk");
            e.execute();
        }catch(Exception e){
            throw e;
        }
    }
    public static void unrar(String dir) throws FileNotFoundException {
        File dire=new File(dir);
        File files[]=dire.listFiles();
        for(File file:files){
            if(file.isFile()){
                String name=file.getName().substring(0,file.getName().lastIndexOf("."));
                File dePressDir=new File(file.getParent()+"\\"+"temp_"+name);
                if(!dePressDir.exists()){
                    dePressDir.mkdir();
                }
                unrar5(file.getAbsolutePath(),dePressDir.getAbsolutePath());
            }else{
                unrar(file.getAbsolutePath());
            }
        }
    }
    public static boolean unrar5(String rarFile, String target) throws FileNotFoundException {
        String winrarPath= ResourceUtils.getURL("classpath:").getPath()+"winrar/WinRAR.exe";

        boolean bool = false;
        File f=new File(rarFile);
        if(!f.exists()){
            return false;
        }
        String cmd =winrarPath + " X " + rarFile + " "+target;
        try {
            Process proc = Runtime.getRuntime().exec(cmd);

            if (proc.waitFor() != 0) {
                if (proc.exitValue() == 0) {
                    bool = false;
                }
            } else {
                bool = true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bool;
    }
    /**
     * 解压rar格式压缩包。
     * 对应的是java-unrar-0.3.jar，但是java-unrar-0.3.jar又会用到commons-logging-1.1.1.jar
     */
    private static boolean unrar(String sourceRar, String destDir,String sourceFile,SyncFilePlan syncFilePlan) {
        Archive a = null;
        FileOutputStream fos = null;
        boolean flag=false;

        try {
            a = new Archive(new NativeStorage(new File(sourceRar)));
            FileHeader fh = a.nextFileHeader();
            if(fh!=null){
                flag = true;
            }

            while(fh!=null){
                if(!fh.isDirectory()){
                    //1 根据不同的操作系统拿到相应的 destDirName 和 destFileName
                    String compressFileName = fh.getFileNameString();
                    sourceRar=sourceRar.replace("/","\\");
                    String sourcename=sourceRar.substring(sourceRar.lastIndexOf("\\")+1);
                    if(syncFilePlan.getIsKeepDirTree()==0){
                        if(compressFileName.contains("\\")) {
                            compressFileName = compressFileName.substring(compressFileName.lastIndexOf("\\") + 1);
                        }
                        if(syncFilePlan.getIsTransformFilename()==1){
                            if(sourcename.contains("_")){
                                sourcename=sourcename.substring(0,sourcename.lastIndexOf("_")+1);
                                compressFileName=sourcename+fh.getFileNameString().replace("\\","_");
                            }else {
                                compressFileName = fh.getFileNameString().replace("\\", "_");
                            }
                        }
                    }
                    String destFileName ;
                    String destDirName ;

                    //非windows系统

                    destFileName = destDir + compressFileName.replaceAll("/", "\\");
                    destDirName = destFileName.substring(0, destFileName.lastIndexOf("\\"));

                    //2创建文件夹
                    File dir = new File(destDirName);
                    if (!dir.exists() || !dir.isDirectory()) {
                            dir.mkdirs();
                    }
                    //3解压缩文件
                    fos = new FileOutputStream(new File(destFileName));
                    a.extractFile(fh, fos);
                    fos.close();
                    deCompressFile(destFileName,destDirName, sourceFile, syncFilePlan);

                }
                fh = a.nextFileHeader();
            }
            a.close();

        }catch(Exception e){
            e.printStackTrace();
        }finally{
            if(fos!=null){
                try{fos.close();}catch(Exception e){e.printStackTrace();}
            }
            if(a!=null){
                try{a.close();}catch(Exception e){e.printStackTrace();}
            }
            return flag;
        }
    }
    /**
     * 解压缩
     */
    private static void deCompress(String sourceFile,String destDir,String dest,SyncFilePlan syncFilePlan) throws Exception{
        //保证文件夹路径最后是"/"或者"\"
        char lastChar = destDir.charAt(destDir.length()-1);
        if(lastChar!='/'&&lastChar!='\\'){
            destDir += File.separator;
        }
        //根据类型，进行相应的解压缩
        String type = sourceFile.substring(sourceFile.lastIndexOf(".")+1);
        if(type.equals("zip")){
            DeCompressUtil.unzip(sourceFile, destDir);
        }else if(type.equals("rar")){
         //   DeCompressUtil.unrar(sourceFile, destDir,sourceFile,syncFilePlan);
        }else{
            throw new Exception("只支持zip和rar格式的压缩包！");
        }
        if(syncFilePlan.getIsFilterCompressFile()==1){
            filter(destDir,syncFilePlan);
        }
        File file=new File(destDir);
        multiDeCompress(file,dest,syncFilePlan);

    }
    public static boolean isCompressFile(File file){
        String type=file.getName().substring(file.getName().lastIndexOf(".")+1);
        return type.equalsIgnoreCase("rar")||type.equalsIgnoreCase("zip");
    }
    public static boolean isCompressFile(String fileName){
        String type=fileName.substring(fileName.lastIndexOf(".")+1);
        return type.equalsIgnoreCase("rar")||type.equalsIgnoreCase("zip");
    }
    private static void multiDeCompress(File file,String dest,SyncFilePlan syncFilePlan) throws Exception {
        ;
            File files[]=file.listFiles();

        for(File f:files) {
                    if (f.isFile() && isCompressFile(f)) {
                        String dir = f.getParent() + File.separator + f.getName().substring(0, f.getName().lastIndexOf("."));
                        deCompress(f.getAbsolutePath(), dir,dest, syncFilePlan);
                        f.delete();
                        multiDeCompress(new File(dir), dest,syncFilePlan);

                    } else if (f.isDirectory()) {
                        multiDeCompress(f, dest,syncFilePlan);
                    } else {

                        if (syncFilePlan.getIsTransformFilename() == 1) {
                            if(!list.contains(f.getAbsolutePath())) {
                             //   String parent = dest.substring(0, dest.lastIndexOf(File.separator));

                                String dir = dest.substring(dest.lastIndexOf(File.separator) + 1);
                                // String parent=destDir.substring(0,destDir.length()-1).substring(destDir.lastIndexOf("\\")+1);
                                dir = dir + f.getAbsolutePath().replace(dest, "").replace(File.separator, "_");

                                f.renameTo(new File(f.getParent() + File.separator + dir));
                                list.add(f.getParent() + File.separator + dir);
                            }
                        }
                    }
            }
        }
    public static void deCompressFile(File source,String dest,String newDir,SyncFilePlan syncFilePlan) throws Exception {
        deCompress(source.getAbsolutePath(),dest,dest,syncFilePlan);

        if(syncFilePlan.getIsKeepDirTree()==0){
            String destp=dest.substring(0,dest.lastIndexOf(File.separator));
            if("".equals(newDir)) {
                delFolder(dest, destp);
            }else{
                File file=new File(newDir);
                if(!file.exists()){
                    file.mkdir();
                }
                delFolder(dest,newDir);
            }

        }

    }
    private static void filter(String dir,SyncFilePlan syncFilePlan){
        File fs=new File(dir);

        File all[]=fs.listFiles();

        for(File file:all){
            if(file.isFile()) {
                File filters[]=fs.listFiles(new FilesFilter(syncFilePlan));
                boolean flag = false;
                for (File filter : filters) {
                    if (filter.getAbsolutePath().equals(file.getAbsolutePath())) {
                        flag = true;
                        break;
                    }
                }
                if (!flag) {
                    file.delete();
                    file.getParentFile().delete();
                }
            }else{
                filter(file.getAbsolutePath(),syncFilePlan);
            }
        }



    }
    private static void copyFile(File src,File dest) throws IOException {
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
    //文件从临时目录移动到一级目录不保留目录格式

    public  static void delFolder(String folderPath,String dest) throws IOException {
        File file=new File(folderPath);

        File []files=file.listFiles();
        if(files==null||files.length<1){
            file.delete();
        }else {
            for (File f : files) {
                if(f.isFile()){
                    copyFile(f,new File(dest+File.separator+f.getName()));
                    f.delete();
                }else {
                    delFolder(f.getAbsolutePath(),dest);
                }
            }
            file.delete();
        }
    }

    public  static void delFolder(String folderPath) throws IOException {
        File file=new File(folderPath);
        File []files=file.listFiles();
        if(files==null||files.length<1){
            file.delete();
        }else {
            for (File f : files) {
                if(f.isFile()){
                    f.delete();
                }else {
                    delFolder(f.getAbsolutePath());
                }
            }
            file.delete();
        }
    }
    //文件从临时目录移动到一级目录保留目录格式
    public static void moveFile(String folderPath,String dest) throws IOException {
        File file=new File(folderPath);
        File []files=file.listFiles();
        if(files==null||files.length<1){
            file.delete();
        }else {
            for (File f : files) {
                if(f.isFile()){
                    File dir=new File(dest);
                    if(!dir.exists()){
                        dir.mkdirs();
                    }
                    copyFile(f,new File(dir,f.getName()));
                    f.delete();
                }else {
                    moveFile(f.getAbsolutePath(),dest+File.separator+f.getName());
                }
            }
            file.delete();
        }
    }

    public static void main(String[] args) throws FileNotFoundException {
        //  deCompress("d:\\backup\\11.rar","d:\\backup",syncFilePlan);
        boolean flag=unrar5("d:\\backup\\22.txt","d:\\backup");
        System.out.println(flag);

    }
}