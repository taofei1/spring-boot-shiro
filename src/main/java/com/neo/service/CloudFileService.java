package com.neo.service;

import com.neo.DTO.CloudFileDTO;
import com.neo.enums.FileOperType;
import com.neo.exception.BusinessException;
import com.neo.pojo.CloudFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public interface CloudFileService {
    CloudFile selectByFileId(Long id);

    List<CloudFile> getAllParentPaths(Long id, Integer isShare) throws BusinessException;
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


    void deleteBatch(List<Long> ids) throws BusinessException;
     boolean isAvailable(String name, Long parentId) throws BusinessException;

     CloudFileDTO getAllChildren(Long fileId,boolean isOnlyDir);

     List<CloudFile> getChildrenFiles(Long fileId);

    byte[] generateZip(String fileIds) throws IOException;


    String getCommonPath(List<Long> ids) throws BusinessException;

    Map<String, Object> getFilesInfo(List<Long> ids) throws BusinessException;

    int copyFile(Long src, Long parentId) throws BusinessException;
}
