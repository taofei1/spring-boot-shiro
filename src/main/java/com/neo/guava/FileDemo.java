package com.neo.guava;

import com.google.common.base.Charsets;
import com.google.common.collect.ImmutableList;
import com.google.common.io.Files;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * @Auther: 13965
 * @Date: 2018/11/6 15:56
 * @Description:
 * @Version: 1.0
 */
public class FileDemo {
    public static void main(String[] args) throws IOException {
        File file=new File("d:\\demo.txt");
        FileDemo fd=new FileDemo();
        fd.writeFile("1e12看的积分京津冀大方",file);
    }

    //写文件
    private void writeFile(String content,File file) throws IOException {
        if (!file.exists()){
            file.createNewFile();
        }
        Files.write(content.getBytes(Charsets.ISO_8859_1),file);
    }

    //读文件
    private List<String> readFile(File file) throws IOException {
        if (!file.exists()){
            return ImmutableList.of(); //避免返回null
        }
        return Files.readLines(file,Charsets.UTF_8);
    }

    //文件复制
    private void copyFile(File from,File to) throws IOException {
        if (!from.exists()){
            return;
        }
        if (!to.exists()){
            to.createNewFile();
        }
        Files.copy(from,to);
    }

}

