package com.neo.service;

import com.neo.enums.FileOperType;
import com.neo.exception.BusinessException;
import com.neo.pojo.CloudFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface CloudFileService {
    CloudFile selectByFileId(Long id);

    List<CloudFile> getAllParentPaths(Long id) throws BusinessException;
    List<CloudFile> selectPersonalOrShareAndNameLike(CloudFile cloudFile) throws BusinessException;

    List<CloudFile> getCateGoryFileAndNameLike(String cateGory, String fileName) throws BusinessException;

    List<CloudFile> selectVisibleTrash(String fileName);

    List<CloudFile> selectAll(boolean isTrash);

    int insertOne(CloudFile cloudFile) throws BusinessException;

    void updateOne(Long id, FileOperType fileOperType) throws BusinessException;

    void uploadFile(Long parentDirectoryId, MultipartFile multipartFile) throws BusinessException, IOException;

    void deleteOne(Long fileId) throws BusinessException;

    void createDirectory(Long parentId, String directoryName) throws BusinessException;

    int changeName(Long fileId, String fileName) throws BusinessException;

    List<CloudFile> selectShareFirstPage(String searchName);

    int moveFile(Long fileId, Long parentId) throws BusinessException;

    boolean nameCanUse(String name, Long parentId) throws BusinessException;

    void deleteBatch(List<Long> ids) throws BusinessException;

}
