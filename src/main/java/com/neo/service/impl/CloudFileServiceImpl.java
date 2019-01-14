package com.neo.service.impl;

import com.neo.config.GlobalConfig;
import com.neo.enums.FileOperType;
import com.neo.enums.Suffix;
import com.neo.exception.BusinessException;
import com.neo.exception.ErrorEnum;
import com.neo.mapper.CloudFileMapper;
import com.neo.pojo.CloudFile;
import com.neo.service.CloudFileService;
import com.neo.util.ShiroUtil;
import com.neo.util.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import org.springframework.web.multipart.MultipartFile;


import javax.transaction.Transactional;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


@Service
@Slf4j
public class CloudFileServiceImpl implements CloudFileService {
    @Autowired
    private CloudFileMapper cloudFileMapper;

    @Override
    public List<CloudFile> selectPersonalOrShareAndNameLike(CloudFile cloudFile) throws BusinessException {
        if (ObjectUtils.isEmpty(cloudFile) || cloudFile.getFileId() == null) {
            throw new BusinessException(ErrorEnum.PARAM_ERROR);
        }
        //cloudFile.setUserId(getCurrentUserId());
        return cloudFileMapper.selectAllCurrentPage(cloudFile);
    }

    @Override
    public List<CloudFile> getCateGoryFileAndNameLike(String cateGory, String fileName) throws BusinessException {
        List<String> fileSuffix;
        switch (cateGory) {
            case "image":
                fileSuffix = Suffix.IMAGE.getSuffix();
                break;
            case "video":
                fileSuffix = Suffix.VIDEO.getSuffix();
                break;
            case "audio":
                fileSuffix = Suffix.AUDIO.getSuffix();
                break;
            case "document":
                fileSuffix = Suffix.DOCUMENT.getSuffix();
                break;
            case "zip":
                fileSuffix = Suffix.ZIP.getSuffix();
                break;
            default:
                throw new BusinessException(ErrorEnum.PARAM_ERROR, "文件分类不存在！");
        }
        return cloudFileMapper.selectCateGory(getCurrentUserId(), fileSuffix, fileName);

    }

    @Override
    public List<CloudFile> selectVisibleTrash(String fileName) {
        return cloudFileMapper.selectSeeTrash(getCurrentUserId(), fileName);
    }

    @Override
    public List<CloudFile> selectAll(boolean isTrash) {
        return null;
    }


    @Override
    public int insertOne(CloudFile cloudFile) throws BusinessException {
        if (ObjectUtils.isEmpty(cloudFile) || cloudFile.getFileId() == null) {
            throw new BusinessException(ErrorEnum.PARAM_ERROR);
        }
        return cloudFileMapper.insertOne(cloudFile);
    }

    /**
     * 修改文件
     * 1).修改名字
     * 2).变成垃圾或从垃圾箱复原，如果是文件直接操作，如果是文件夹，遍历子文件并进行操作
     * 3).共享和取消共享，逻辑同上
     *
     * @param id
     * @param fileOperType
     * @return
     * @throws BusinessException
     */
    @Transactional
    @Override
    public int updateOne(Long id, FileOperType fileOperType) throws BusinessException {
        CloudFile cf = cloudFileMapper.selectByFileId(id);
        checkAccess(cf);
        //还原的时候需要检测原目录是否有重名文件
        if (fileOperType == FileOperType.RESTORE) {
            List<CloudFile> cls = cloudFileMapper.selectAllByUserIdAndParentId(getCurrentUserId(), cf.getParentId(), 0);
            for (CloudFile cloudFile1 : cls) {
                if (cloudFile1.getFileName().equals(cf.getFileName())) {
                    throw new BusinessException(ErrorEnum.DUPLICATE_FILENAME);
                }
            }
        }
        doUpdateFileByOperType(cf, fileOperType, true);
        traverse(cf, fileOperType);
        return cloudFileMapper.updateOne(cf);
    }

    @Override
    public void uploadFile(Long parentDirectoryId, MultipartFile multipartFile) throws BusinessException, IOException {
        if (ObjectUtils.isEmpty(multipartFile)) {
            return;
        }
        if (parentDirectoryId == null) {
            throw new BusinessException(ErrorEnum.PARAM_ERROR, "缺少文件父目录ID");
        }
        Long size = multipartFile.getSize();
        if (size > GlobalConfig.getMaxFileSizeByte()) {
            throw new BusinessException(ErrorEnum.FILE_TO_LARGE);
        }
        String suffix = FilenameUtils.getExtension(multipartFile.getOriginalFilename());
        String uploadPath = GlobalConfig.getCloudFilePath();
        String newFileName = UUID.randomUUID().toString().toLowerCase() + "." + suffix;
        File targetFile = new File(uploadPath, newFileName);
        multipartFile.transferTo(targetFile);
        CloudFile cloudFile = new CloudFile();
        cloudFile.setUserId(getCurrentUserId());
        cloudFile.setContentType(multipartFile.getContentType());
        cloudFile.setFileName(multipartFile.getOriginalFilename());
        cloudFile.setFileOrder(getMaxOrderByUserAndIsDirectory(parentDirectoryId, 0));
        cloudFile.setSuffix(suffix);
        cloudFile.setIsDirectory(0);
        cloudFile.setFilePath(targetFile.getAbsolutePath().replace("\\", "/").replace("D:", ""));
        cloudFile.setParentId(parentDirectoryId);
        cloudFile.setSize(multipartFile.getSize());

        insertOne(cloudFile);
    }


    @Override
    public int changeName(Long fileId, String fileName) throws BusinessException {
        CloudFile cl = cloudFileMapper.selectByFileId(fileId);
        checkAccess(cl);

        CloudFile parent = cloudFileMapper.selectByFileId(cl.getParentId());
        parent.setFileName(null);
        //判断当前目录下是否有同名文件

        List<CloudFile> cloudFiles = cloudFileMapper.selectAllCurrentPage(parent);
        for (CloudFile c : cloudFiles) {
            if (c.getFileName().equals(fileName)) {
                throw new BusinessException(ErrorEnum.DUPLICATE_FILENAME);
            }
        }
        cl.setFileName(fileName);
        String extension = FilenameUtils.getExtension(fileName);
        cl.setSuffix(extension);

        return cloudFileMapper.updateOne(cl);
    }

    @Override
    public List<CloudFile> selectShareFirstPage(String searchName) {
        List<CloudFile> cloudFiles = cloudFileMapper.selectAllShares();
        List<CloudFile> firstPage = new ArrayList<>();
        //当file没有父目录或者父目录没有共享的时候就在第一页显示
        cloudFiles.forEach(cloudFile -> {
            if (cloudFile.getParentId() == 0 || cloudFileMapper.selectByFileId(cloudFile.getParentId()).getIsShare() == 0L) {
                //共享首页搜索
                if (StringUtils.isEmpty(searchName) || cloudFile.getFileName().contains(searchName)) {
                    firstPage.add(cloudFile);
                }
            }
        });
        return firstPage;
    }

    /**
     * 移动文件
     * 移动到的文件夹下不能有同名文件
     *
     * @param fileId
     * @param newParentId
     * @return
     * @throws BusinessException
     */
    @Override
    public int moveFile(Long fileId, Long newParentId) throws BusinessException {
        CloudFile cl = cloudFileMapper.selectByFileId(fileId);
        checkAccess(cl);
        List<CloudFile> cloudFiles = cloudFileMapper.selectAllByUserIdAndParentId(getCurrentUserId(), newParentId, 0);
        for (CloudFile c : cloudFiles) {
            if (cl.getFileName().equals(c.getFileName())) {
                throw new BusinessException(ErrorEnum.DUPLICATE_FILENAME);
            }
        }
        CloudFile parent = cloudFileMapper.selectByFileId(fileId);
        if (parent.getIsShare() == 1) {
            traverse(cl, FileOperType.SHARE);
        }
        cl.setParentId(newParentId);

        return cloudFileMapper.updateOne(cl);
    }

    @Override
    public void deleteBatch(List<Long> ids) throws BusinessException {
        for (Long id : ids) {
            CloudFile cloudFile = cloudFileMapper.selectByFileId(id);
            checkAccess(cloudFile);
        }
        cloudFileMapper.deleteBatch(ids);
        for (Long id : ids) {
            deleteOne(id);
        }
    }

    /**
     * 回收站彻底删除
     *
     * @param fileId
     * @return
     * @throws BusinessException
     */
    @Override
    public void deleteOne(Long fileId) throws BusinessException {
        CloudFile cl = cloudFileMapper.selectByFileId(fileId);
        checkAccess(cl);
        //如果是文件，直接删除
        if (cl.getIsDirectory() == 0) {
            int result = cloudFileMapper.deleteOne(fileId);
            if (result < 0) {
                throw new BusinessException(ErrorEnum.DATABASE_OPER_ERROR, "删除失败！" + cl.getFileName());
            }
        }
        //如果是文件夹，遍历所有子文件删除
        else {
            traverse(cl, FileOperType.DELETE);
        }

    }

    /**
     * 遍历文件夹根据选项进行操作
     *
     * @param cloudFile
     * @param type
     * @throws BusinessException
     */
    private void traverse(CloudFile cloudFile, FileOperType type) throws BusinessException {
        List<CloudFile> cloudFileList = doSelectByOperType(cloudFile, type);
        for (CloudFile cloud : cloudFileList) {
            doUpdateFileByOperType(cloudFile, type, false);
            if (cloud.getIsDirectory() != 0) {
                traverse(cloud, type);
            }
        }
    }

    private List<CloudFile> doSelectByOperType(CloudFile cloudFile, FileOperType type) {
        List<CloudFile> result = null;
        switch (type) {
            case TRASH:
            case SHARE:
            case NOTSHARE:
                result = cloudFileMapper.selectAllByUserIdAndParentId(getCurrentUserId(), cloudFile.getFileId(), 0);
                break;
            case RESTORE:
            case DELETE:
                result = cloudFileMapper.selectAllByUserIdAndParentId(getCurrentUserId(), cloudFile.getFileId(), 1);
                break;
        }
        return result;
    }

    private void doUpdateFileByOperType(CloudFile cloudFile, FileOperType type, boolean isVisibleTrash) {
        switch (type) {
            case TRASH:
                if (isVisibleTrash) {
                    cloudFile.setIsTrash(1);
                } else {
                    cloudFile.setIsTrash(2);
                }
                cloudFile.setIsShare(0);
                break;
            case RESTORE:
                cloudFile.setIsTrash(0);
                break;
            case SHARE:
                cloudFile.setIsShare(1);
                break;
            case NOTSHARE:
                cloudFile.setIsShare(0);
                break;
            case DELETE:
                cloudFileMapper.deleteOne(cloudFile.getFileId());
                return;
        }
        cloudFileMapper.updateOne(cloudFile);
    }

    private void checkAccess(CloudFile cl) throws BusinessException {
        if (StringUtils.isNull(cl)) {
            throw new BusinessException(ErrorEnum.DATA_NOT_FOUND, "要操作的文件不存在！" + cl.getFileName());
        } else if (cl.getUserId() != getCurrentUserId()) {
            throw new BusinessException(ErrorEnum.PARAM_ERROR, "无法操作非当前用户的文件！");
        } else if (cl.getIsTrash() != 0) {
            throw new BusinessException(ErrorEnum.PARAM_ERROR, "回收站文件无法操作!");
        }
    }

    @Override
    public void createDirectory(Long parentId, String directoryName) throws BusinessException {
        if (StringUtils.isNull(parentId) || StringUtils.isEmpty(directoryName)) {
            throw new BusinessException(ErrorEnum.PARAM_ERROR);
        }
        CloudFile cloudFile = new CloudFile();
        cloudFile.setParentId(parentId);
        cloudFile.setFileName(directoryName);
        cloudFile.setFilePath("");
        cloudFile.setSize(0L);
        cloudFile.setSuffix("");
        cloudFile.setIsDirectory(1);
        cloudFile.setFileOrder(getMaxOrderByUserAndIsDirectory(cloudFile.getParentId(), 1));
        cloudFile.setContentType("directory");
        cloudFile.setUserId(getCurrentUserId());
        insertOne(cloudFile);

    }


    public int getMaxOrderByUserAndIsDirectory(Long parentDirectoryId, Integer isDrectory) {
        return cloudFileMapper.selectMaxIndexCurrentPage(parentDirectoryId, getCurrentUserId(), isDrectory);
    }

    public Long getCurrentUserId() {
        return ShiroUtil.getSysUser().getUid().longValue();
    }


}
