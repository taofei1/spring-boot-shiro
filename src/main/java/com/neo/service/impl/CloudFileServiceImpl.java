package com.neo.service.impl;

import com.neo.DTO.CloudFileDTO;
import com.neo.config.GlobalConfig;
import com.neo.enums.FileOperType;
import com.neo.enums.FileType;
import com.neo.exception.BusinessException;
import com.neo.exception.ErrorEnum;
import com.neo.mapper.CloudFileMapper;
import com.neo.pojo.CloudFile;
import com.neo.service.CloudFileService;
import com.neo.util.DateUtils;
import com.neo.util.ShiroUtil;
import com.neo.util.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import org.springframework.web.multipart.MultipartFile;


import javax.transaction.Transactional;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;


@Service
@Slf4j
public class CloudFileServiceImpl implements CloudFileService {
    @Autowired
    private CloudFileMapper cloudFileMapper;

    @Override
    public CloudFile selectByFileId(Long id) {
        return cloudFileMapper.selectByFileId(id);
    }

    @Override
    public List<CloudFile> getAllParentPaths(Long id) throws BusinessException {
        CloudFile cloudFile = cloudFileMapper.selectByFileId(id);
        if (StringUtils.isNull(cloudFile)) {
            throw new BusinessException(ErrorEnum.DATA_NOT_FOUND, "id=" + id + "的文件或文件夹未找到");
        }

        List<CloudFile> cloudFiles = new ArrayList<>();
        cloudFiles.add(cloudFile);
        while (cloudFile.getFileId() != 1L && cloudFile.getParentId() != 0) {
            cloudFile = cloudFileMapper.selectByFileId(cloudFile.getParentId());
            cloudFiles.add(cloudFile);
        }
        Collections.reverse(cloudFiles);
        return cloudFiles;

    }

    @Override
    public List<CloudFile> selectPersonalOrShareAndNameLike(CloudFile cloudFile) throws BusinessException {
        if (ObjectUtils.isEmpty(cloudFile)) {
            throw new BusinessException(ErrorEnum.PARAM_ERROR);
        }
        return cloudFileMapper.selectAllCurrentPage(cloudFile);
    }

    @Override
    public List<CloudFile> getCateGoryFileAndNameLike(String cateGory, String fileName) throws BusinessException {
        List<String> fileSuffix;
        switch (cateGory.toLowerCase()) {
            case "image":
                fileSuffix = FileType.IMAGE.getSuffix();
                break;
            case "video":
                fileSuffix = FileType.VIDEO.getSuffix();
                break;
            case "audio":
                fileSuffix = FileType.AUDIO.getSuffix();
                break;
            case "document":
                fileSuffix = FileType.DOCUMENT.getSuffix();
                break;
            case "zip":
                fileSuffix = FileType.ZIP.getSuffix();
                break;
            default:
                throw new BusinessException(ErrorEnum.PARAM_ERROR, "文件分类不存在！:" + cateGory);
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
    public void updateOne(Long id, FileOperType fileOperType) throws BusinessException {
        CloudFile cf = cloudFileMapper.selectByFileId(id);
        checkAccess(cf, fileOperType);
        //还原的时候需要检测原目录是否有重名文件
        if (fileOperType == FileOperType.RESTORE) {
            if (!isAvailable(cf.getFileName(), cf.getParentId())) {
                throw new BusinessException(ErrorEnum.DUPLICATE_FILENAME);
            }
        }
        doUpdateFileByOperType(cf, fileOperType, true);
        traverse(cf, fileOperType);
    }

    @Override
    public void uploadFile(Long parentDirectoryId, MultipartFile multipartFile) throws BusinessException, IOException {
        if (ObjectUtils.isEmpty(multipartFile)) {
            return;
        }
        if (parentDirectoryId == null || parentDirectoryId < 0) {
            parentDirectoryId = 1L;
        }
        Long size = multipartFile.getSize();
        if (size > GlobalConfig.getMaxFileSizeByte()) {
            throw new BusinessException(ErrorEnum.FILE_TO_LARGE);
        }
        String suffix = FilenameUtils.getExtension(multipartFile.getOriginalFilename()).toLowerCase();
        String uploadPath = GlobalConfig.getCloudFilePath();

        String newFileName = DateUtils.dateTimeNow() + UUID.randomUUID().toString().substring(0, 5).toLowerCase() + "." + suffix;
        File targetFile = new File(uploadPath, newFileName);
        multipartFile.transferTo(targetFile);
        CloudFile cloudFile = new CloudFile();
        cloudFile.setUserId(getCurrentUserId());
        cloudFile.setContentType(multipartFile.getContentType());
        cloudFile.setFileName(getAvailableName(multipartFile.getOriginalFilename(),parentDirectoryId));
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
        checkAccess(cl, null);

        CloudFile parent = cloudFileMapper.selectByFileId(cl.getParentId());
        parent.setFileName(null);
        //判断当前目录下是否有同名文件

        if (!isAvailable(fileName, parent.getFileId())) {
            throw new BusinessException(ErrorEnum.DUPLICATE_FILENAME);
        }
        cl.setFileName(fileName);
        String extension = FilenameUtils.getExtension(fileName);
        cl.setSuffix(extension);

        return cloudFileMapper.updateOne(cl);
    }

    @Override
    public List<CloudFile> selectShareFirstPage(String searchName) {
        List<CloudFile> cloudFiles = cloudFileMapper.selectAllShares();
        //不为空则将搜索结果直接返回
        if (StringUtils.isNotEmpty(searchName)) {
            return cloudFiles;
        }
        //仅返回首页数据
        else {

            List<CloudFile> firstPage = new ArrayList<>();
            //当file没有父目录或者父目录没有共享的时候就在第一页显示
            cloudFiles.forEach(cloudFile -> {
                if (cloudFile.getParentId() == 0 || cloudFileMapper.selectByFileId(cloudFile.getParentId()).getIsShare() == 0L) {

                    firstPage.add(cloudFile);
                }
            });
            return firstPage;
        }
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
        checkAccess(cl, null);
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
            checkAccess(cloudFile, null);
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
        checkAccess(cl, null);
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
            doUpdateFileByOperType(cloud, type, false);
            if (cloud.getIsDirectory() != 0) {
                traverse(cloud, type);
            }
        }
    }

    private List<CloudFile> doSelectByOperType(CloudFile cloudFile, FileOperType type) {
        List<CloudFile> result = null;

        if (type == FileOperType.TRASH || type == FileOperType.SHARE || type == FileOperType.NOTSHARE) {
            result = cloudFileMapper.selectAllByUserIdAndParentId(getCurrentUserId(), cloudFile.getFileId(), 0);
        } else if (type == FileOperType.RESTORE || type == FileOperType.DELETE) {

            result = cloudFileMapper.selectAllByUserIdAndParentId(getCurrentUserId(), cloudFile.getFileId(), 1);

        }
        return result;
    }

    private CloudFile createCloudFile(Long fileId) {
        CloudFile cf = new CloudFile();
        cf.setIsTrash(null);
        cf.setIsShare(null);
        cf.setFileId(fileId);
        return cf;

    }
    private void doUpdateFileByOperType(CloudFile cloudFile, FileOperType type, boolean isVisibleTrash) {
        CloudFile cf = createCloudFile(cloudFile.getFileId());
        switch (type) {
            case TRASH:
                if (isVisibleTrash) {
                    cf.setIsTrash(1);
                } else {
                    cf.setIsTrash(2);
                }
                cf.setIsShare(0);
                break;
            case RESTORE:
                cf.setIsTrash(0);
                break;
            case SHARE:
                cf.setIsShare(1);
                break;
            case NOTSHARE:
                cf.setIsShare(0);
                break;
            case DELETE:
                cloudFileMapper.deleteOne(cloudFile.getFileId());
                return;
        }
        cloudFileMapper.updateOne(cf);
    }

    private void checkAccess(CloudFile cl, FileOperType operType) throws BusinessException {
        if (StringUtils.isNull(cl)) {
            throw new BusinessException(ErrorEnum.DATA_NOT_FOUND, "要操作的文件或文件夹不存在！" + cl.getFileName());
        } else if (cl.getUserId() != getCurrentUserId() && cl.getFileId() != 1L) {
            throw new BusinessException(ErrorEnum.PARAM_ERROR, "无法操作非当前用户的文件！");
        } else if (cl.getIsTrash() != 0 && operType != FileOperType.DELETE && operType != FileOperType.RESTORE) {
            throw new BusinessException(ErrorEnum.PARAM_ERROR, "回收站文件无法进行此操作!");

        }
    }

    @Override
    public void createDirectory(Long parentId, String directoryName) throws BusinessException {
        if (StringUtils.isNull(parentId) || StringUtils.isEmpty(directoryName)) {
            throw new BusinessException(ErrorEnum.PARAM_ERROR);
        }
        //如果名称不可用寻找新名称:新建文件夹(num)
        directoryName=getAvailableName(directoryName,parentId);

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
    public String getAvailableName(String name,Long parentId) throws BusinessException {
        int num=1;

        String srcName=name;
        while (!isAvailable(srcName, parentId)) {
            if(name.contains(".")){
                srcName=name.substring(0,name.lastIndexOf("."))+ "(" + (num++) + ")"+name.substring(name.lastIndexOf("."));
            }else {
                srcName = name + "(" + (num++) + ")";
            }

        }
        return srcName;
    }
    @Override
    public boolean isAvailable(String name, Long parentId) throws BusinessException {
        CloudFile cl = cloudFileMapper.selectByFileId(parentId);
        checkAccess(cl, null);
        List<CloudFile> cls = cloudFileMapper.selectAllByUserIdAndParentId(getCurrentUserId(), parentId, 0);
        for (CloudFile cloudFile1 : cls) {
            if (cloudFile1.getFileName().equals(name)) {
                return false;
            }
        }
        return true;


    }

    /**
     * 获取文件夹树形结构
     * @param fileId
     * @param isOnlyDir false:都是要  true：要文件夹
     * @return
     */
    @Override
    public CloudFileDTO getAllChildren(Long fileId,boolean isOnlyDir) {
        CloudFile cf=cloudFileMapper.selectByFileId(fileId);
        CloudFileDTO cfd=new CloudFileDTO();
        BeanUtils.copyProperties(cf,cfd);
        if(cf.getIsDirectory()==0){
            return cfd;
        }else{

            recursion(fileId,cfd,isOnlyDir);
        }
        return cfd;

    }

    @Override
    public List<CloudFile> getChildrenFiles(Long fileId) {
        CloudFile cloudFile = selectByFileId(fileId);

        List<CloudFile> files=new ArrayList<>();
        if(cloudFile.getIsDirectory()==0){
            files.add(cloudFile);
            return files;
        }else{
            CloudFileDTO cfd=getAllChildren(fileId,false);
            getAllChildrenFiles(cfd,files);
        }
        return files;
    }

    private void getAllChildrenFiles(CloudFileDTO cloudFileDTO, List<CloudFile> files) {
        List<CloudFileDTO> children = cloudFileDTO.getChildren();
        if(!StringUtils.isEmpty(children)){
            children.forEach(c->{
                if(c.getIsDirectory()==0){
                    files.add(c);
                }else{
                    getAllChildrenFiles(c,files);
                }
            });
        }
    }


    /**
     * 遍历获得
     * @param parentId
     * @param cfd
     */
    public void recursion(Long parentId,CloudFileDTO cfd,boolean isOnlyDir){
        List<CloudFile> cloudFiles=cloudFileMapper.selectAllByUserIdAndParentId(getCurrentUserId(),parentId,0);
        cfd.setChildren(translate(cloudFiles,isOnlyDir));
        for(CloudFileDTO cloudFileDTO:cfd.getChildren()){
             if(cloudFileDTO.getIsDirectory()==1){
                 recursion(cloudFileDTO.getFileId(),cloudFileDTO,isOnlyDir);
             }
        }


    }
    public List<CloudFileDTO> translate(List<CloudFile> src,boolean isOnlyDir){
        List<CloudFileDTO> tarlist=new ArrayList<>();
        src.forEach(a->{
            if(isOnlyDir==false||a.getIsDirectory()==1) {
                CloudFileDTO cfd = new CloudFileDTO();
                BeanUtils.copyProperties(a, cfd);
                tarlist.add(cfd);
            }


        });
        return tarlist;
    }
    public Long getMaxOrderByUserAndIsDirectory(Long parentDirectoryId, Integer isDirectory) {
        Long maxOrder = cloudFileMapper.selectMaxIndexCurrentPage(parentDirectoryId, getCurrentUserId(), isDirectory);
        if (maxOrder == null) {
            return 1L;
        } else {
            return maxOrder + 1;
        }
    }

    public Long getCurrentUserId() {
        return ShiroUtil.getSysUser().getUid().longValue();
    }


}