package com.neo.web;

import com.neo.enums.FileOperType;
import com.neo.exception.BusinessException;
import com.neo.exception.ErrorEnum;
import com.neo.pojo.CloudFile;
import com.neo.service.CloudFileService;
import com.neo.util.Response;
import com.neo.util.ShiroUtil;
import com.neo.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/file")
public class CloudFileController {
    private String prefix = "file";
    @Autowired
    private CloudFileService cloudFileService;

    //首页
    @GetMapping("/cloudFile")
    public String cloudForm(Map map) throws BusinessException {
        CloudFile cloudFile = new CloudFile();
        cloudFile.setUserId(ShiroUtil.getSysUser().getUid().longValue());
        cloudFile.setFileId(1L);
        List<CloudFile> cloudFiles = cloudFileService.selectPersonalOrShareAndNameLike(cloudFile);
        map.put("parentId", cloudFile.getFileId());
        map.put("cloudFiles", cloudFiles);
        map.put("parentPaths", cloudFileService.getAllParentPaths(1L));
        map.put("loadType", "all");
        return prefix + "/cloudFile";
    }

    @RequestMapping("/fileManager")
    public String cloudForm() throws BusinessException {
        return prefix + "/fileManager";
    }

    /**
     * 进入指定文件夹
     *
     * @param cloudFile
     * @param map
     * @return
     * @throws BusinessException
     */
    @RequestMapping("/all")
    public String all(CloudFile cloudFile, Map map) throws BusinessException {

        cloudFile.setUserId(ShiroUtil.getSysUser().getUid().longValue());

        List<CloudFile> cloudFiles = cloudFileService.selectPersonalOrShareAndNameLike(cloudFile);
        //名称不为空则是搜索，此时导航路劲需要改变
        List<CloudFile> cloudFileList = new ArrayList<>();
        if (StringUtils.isNotEmpty(cloudFile.getFileName())) {
            CloudFile c = new CloudFile();
            CloudFile index = cloudFileService.selectByFileId(1L);
            c.setFileName("\"" + cloudFile.getFileName() + "\"" + "的搜索结果");
            map.put("search", "all");
            cloudFileList.add(index);
            map.put("searchValue", cloudFile.getFileName());
            cloudFileList.add(c);
        } else {
            cloudFileList = cloudFileService.getAllParentPaths(cloudFile.getFileId());
        }
        if (cloudFile.getIsShare() == 1) {
            map.put("loadType", "share");
        } else {
            map.put("loadType", "all");
        }

        map.put("parentPaths", cloudFileList);
        map.put("parentId", cloudFile.getFileId());
        map.put("cloudFiles", cloudFiles);
        return "forward:/file/fileManager";
    }

    public List<CloudFile> nav(String fileName, List list) {

        if (list == null) {
            list = new ArrayList();
        }

        CloudFile c = new CloudFile();
        c.setFileName("\"" + fileName + "\"" + "的搜索结果");
        list.add(c);
        return list;

    }

    /**
     * 分享页面的首页
     *
     * @param fileName
     * @return
     * @throws BusinessException
     */
    @GetMapping("/share")
    public String share(String fileName, Map map) throws BusinessException {

        List<CloudFile> cloudFiles = cloudFileService.selectShareFirstPage(fileName);
        map.put("loadType", "share");
        map.put("cloudFiles", cloudFiles);
        List<CloudFile> navigation = new ArrayList<>();
        CloudFile cloudFile = new CloudFile();
        cloudFile.setFileName("共享文件");
        navigation.add(cloudFile);
        if (StringUtils.isNotEmpty(fileName)) {
            map.put("searchValue", fileName);
        }
        map.put("parentPaths", navigation);
        return prefix;
    }

    /**
     * 查询每个分类可搜索
     * @param cate
     * @param fileName
     * @return
     * @throws BusinessException
     */
    @GetMapping("/{cate}")
    public String share(@PathVariable("cate") String cate, @RequestParam(required = false) String fileName, Map map) throws BusinessException {

        List<CloudFile> cateGoryFileAndNameLike = cloudFileService.getCateGoryFileAndNameLike(cate, fileName);

        map.put("loadType", cate);
        map.put("cloudFiles", cateGoryFileAndNameLike);
        List<CloudFile> navigation = new ArrayList<>();
        CloudFile cloudFile = new CloudFile();
        cloudFile.setFileName(cate);
        navigation.add(cloudFile);
        if (StringUtils.isNotEmpty(fileName)) {
            map.put("searchValue", fileName);
        }
        map.put("parentPaths", navigation);
        return "forward:/file/fileManager";

    }

    /**
     * 查询可见的垃圾
     * @param fileName
     * @return
     */
    @GetMapping("/trash")
    public String trash(String fileName, Map map) {
        List<CloudFile> cloudFiles = cloudFileService.selectVisibleTrash(fileName);
        map.put("loadType", "trash");
        map.put("cloudFiles", cloudFiles);
        List<CloudFile> navigation = new ArrayList<>();
        CloudFile cloudFile = new CloudFile();
        cloudFile.setFileName("垃圾");
        navigation.add(cloudFile);
        if (StringUtils.isNotEmpty(fileName)) {
            map.put("searchValue", fileName);
        }
        map.put("parentPaths", navigation);
        return prefix;
    }

    @PostMapping("/verifyName")
    @ResponseBody
    public Response verfiyName(String name, Long parentId) throws BusinessException {
        if (!cloudFileService.nameCanUse(name, parentId)) {
            return Response.fail(ErrorEnum.DUPLICATE_FILENAME);
        }
        return Response.success();
    }

    @PostMapping("/createDir")
    public String createDir(Long fileId, String dirName) throws BusinessException {
        cloudFileService.createDirectory(fileId, dirName);
        return "forward:/" + prefix+"/all";
    }


    @PostMapping("/upload")
    public String uploadFile(Long parentId, MultipartFile multipartFile, Map map) throws BusinessException, IOException {
        cloudFileService.uploadFile(parentId, multipartFile);
        CloudFile cloudFile = new CloudFile();
        cloudFile.setParentId(parentId);
        cloudFile.setUserId(ShiroUtil.getSysUser().getUid().longValue());
        map.put("cloudFile", cloudFile);
        return "forward:/" +prefix+"/all";
    }


    @PostMapping("/share/{id}")
    public Response shareFile(@PathVariable Long id) throws BusinessException {
        cloudFileService.updateOne(id, FileOperType.SHARE);
        return Response.success();
    }

    @ResponseBody
    @PostMapping("/notshare/{id}")
    public Response notShareFile(@PathVariable Long id) throws BusinessException {
        cloudFileService.updateOne(id, FileOperType.NOTSHARE);
        return Response.success();
    }

    @PostMapping("/trash/{id}")
    public String trashFile(@PathVariable Long id, Long parentId, Map map) throws BusinessException {
        cloudFileService.updateOne(id, FileOperType.TRASH);

        CloudFile cloudFile = new CloudFile();
        cloudFile.setParentId(parentId);
        cloudFile.setUserId(ShiroUtil.getSysUser().getUid().longValue());
        map.put("cloudFile", cloudFile);
        return "forward:/" +prefix+"/all";
    }

    @PostMapping("/restore/{id}")
    public String restoreFile(@PathVariable Long id) throws BusinessException {
        cloudFileService.updateOne(id, FileOperType.RESTORE);
        return "forward:/" + prefix+"trash";
    }

    @PostMapping("/remove/{id}")
    public String remove(@PathVariable Long id) throws BusinessException {
        cloudFileService.updateOne(id, FileOperType.DELETE);
        return "forward:/" + prefix+"trash";
    }

    @PostMapping("/modifyName/{id}")
    public String remove(@PathVariable Long id, String newName, Long parentId, Map map) throws BusinessException {
        cloudFileService.changeName(id, newName);
        CloudFile cloudFile = new CloudFile();
        cloudFile.setFileId(parentId);
        cloudFile.setUserId(ShiroUtil.getSysUser().getUid().longValue());
        map.put("cloudFile", cloudFile);
        return "forward:/" +prefix+"/all";
    }

    @PostMapping("/move/{src}")
    public String move(@PathVariable Long src, Long srcParentId, Long parentId, Map map) throws BusinessException {
        cloudFileService.moveFile(src, parentId);
        CloudFile cloudFile = new CloudFile();
        cloudFile.setFileId(parentId);
        cloudFile.setUserId(ShiroUtil.getSysUser().getUid().longValue());
        map.put("cloudFile", cloudFile);
        return "forward:/" +prefix+"/all";
    }


}
