package com.neo.util;

import com.neo.entity.SyncFilePlan;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.ResourceUtils;

import java.io.*;

public class WinRARUtil {
    private static final Logger log= LoggerFactory.getLogger(WinRARUtil.class);
    public static boolean deCompress(String source,String rarFile, String target,Integer isRename) throws FileNotFoundException {
        String name=rarFile.substring(rarFile.lastIndexOf("\\"),rarFile.lastIndexOf("."));
        File dir=new File(target+"\\"+name+"_temp");
        if(!dir.exists()){
            dir.mkdirs();
        }
        boolean flag=deCompress(rarFile,dir.getAbsolutePath());
        if(flag) {
            if(!source.equals(rarFile)){
                new File(rarFile).delete();
            }
            multiDeCompress(source,dir,isRename);
            return true;
        }else{
            log.info(rarFile+"Œ¥Ω‚—π≥…π¶!");
            dir.delete();
            return false;
        }

    }
    public static boolean deCompress(String source,String target,SyncFilePlan syncFilePlan) throws IOException {
        String name=source.substring(source.lastIndexOf("\\")+1,source.lastIndexOf("."));
        File dir=new File(target+"\\"+name+"_temp");
        if(!dir.exists()){
            dir.mkdir();
        }
        boolean flag=deCompress(source,source,target,syncFilePlan.getIsTransformFilename());

        if(flag&&syncFilePlan.getIsKeepDirTree()==0){
            moveFile(target+"\\"+name+"_temp",target);
        }else if(flag){
             moveFileAndKeepDir(target+"\\"+name+"_temp",target);
        }
        return flag;
    }
    public static void moveFileAndKeepDir(String folderPath,String dest) throws IOException {
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
                    String dir=dest;
                    if(!f.getName().endsWith("_temp")){
                        dir=dir+"\\"+f.getName();
                    }
                    moveFileAndKeepDir(f.getAbsolutePath(),dir);
                }
            }
            file.delete();
        }


    }
    public static void moveFile(String folderPath,String dest) throws IOException {
        File file=new File(folderPath);
        File []files=file.listFiles();
        if(files==null||files.length<1){
            file.delete();
        }else {
            for (File f : files) {
                if(f.isFile()){
                    copyFile(f,new File(dest+"\\"+f.getName()));
                    f.delete();
                }else {
                    moveFile(f.getAbsolutePath(),dest);
                }
            }
            file.delete();
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
    private static void multiDeCompress(String source,File dir,Integer isRename) throws FileNotFoundException {
        File files[]=dir.listFiles();
        for(File file:files){
            if(file.isFile()){

                deCompress(source,file.getAbsolutePath(),file.getParent(),isRename);
                if(isRename==1) {
                    String parent = source.substring(0, source.lastIndexOf("\\"));
                    String finalName = rename(parent, file.getAbsolutePath());
                    file.renameTo(new File(finalName));
                }
            }else{
                multiDeCompress(source,file,isRename);
            }
        }
    }
    private static String rename(String dir,String filePath){
        String name=filePath.substring(dir.length()+1);
        String fileName=name.substring(name.lastIndexOf(File.separator)+1);
        String dirs[]=name.split("\\\\");
        if(dirs.length>1) {
            for (int i = dirs.length - 2; i > 0; i--) {
                if (!dirs[i].endsWith("_temp")) {
                    fileName = dirs[i] + "_" +fileName;
                }
            }
        }
        return filePath.substring(0,filePath.lastIndexOf("\\")+1)+fileName;
    }
    private static boolean deCompress(String rarFile, String target) throws FileNotFoundException {
        String winrarPath= ResourceUtils.getURL("classpath:").getPath()+"winrar/WinRAR.exe";

        boolean bool = false;
        File f=new File(rarFile);
        if(!f.exists()){
            return false;
        }
        String cmd =winrarPath + " X -inul " + rarFile + " "+target;
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

    public static void main(String[] args) throws IOException {
        SyncFilePlan syncFilePlan=new SyncFilePlan();
        syncFilePlan.setIsKeepDirTree(1);
        syncFilePlan.setIsTransformFilename(1);
        boolean flag=deCompress("d:\\zip\\qq\\aa_12.sql","d:\\zip",syncFilePlan);
        System.out.println(flag);
    }
}
