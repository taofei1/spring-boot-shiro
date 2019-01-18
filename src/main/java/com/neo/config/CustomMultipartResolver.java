package com.neo.config;

import com.neo.listener.FileUploadProgressListener;


import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUpload;
import org.apache.commons.fileupload.FileUploadBase;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.multipart.MultipartException;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

public class CustomMultipartResolver extends CommonsMultipartResolver {
    // 注入第二步写的FileUploadProgressListener
    @Autowired
    private FileUploadProgressListener progressListener;


    @Override
    public MultipartParsingResult parseRequest(HttpServletRequest request) throws MultipartException {
        String encoding = determineEncoding(request);
        FileUpload fileUpload = prepareFileUpload(encoding);
        progressListener.setSession(request.getSession());// 问文件上传进度监听器设置session用于存储上传进度
        fileUpload.setProgressListener(progressListener);// 将文件上传进度监听器加入到 fileUpload 中
        try {
            List<FileItem> fileItems = ((ServletFileUpload) fileUpload).parseRequest(request);
            return parseFileItems(fileItems, encoding);
        } catch (FileUploadBase.SizeLimitExceededException ex) {
            throw new MaxUploadSizeExceededException(fileUpload.getSizeMax(), ex);
        } catch (FileUploadException ex) {
            throw new MultipartException("Could not parse multipart servlet request", ex);
        }
    }
}