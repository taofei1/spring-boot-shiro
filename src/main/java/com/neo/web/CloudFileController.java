package com.neo.web;

import com.neo.enums.FileOperType;
import com.neo.enums.FileType;
import com.neo.exception.BusinessException;
import com.neo.exception.ErrorEnum;
import com.neo.model.Progress;
import com.neo.pojo.CloudFile;
import com.neo.service.CloudFileService;
import com.neo.util.Response;
import com.neo.util.ShiroUtil;
import com.neo.util.StringUtils;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/file")
public class CloudFileController {
    private String prefix = "file";
    @Autowired
    private CloudFileService cloudFileService;

    @GetMapping("/cloudFile")
    public String cloudForm(Map map) throws BusinessException {
        CloudFile cloudFile = new CloudFile();
        cloudFile.setUserId(ShiroUtil.getSysUser().getUid().longValue());
        cloudFile.setFileId(1L);
        List<CloudFile> cloudFiles = cloudFileService.selectPersonalOrShareAndNameLike(cloudFile);
        map.put("parentId", cloudFile.getFileId());
        map.put("cloudFiles", cloudFiles);
        map.put("parentPaths", cloudFileService.getAllParentPaths(1L, 0));
        map.put("loadType", "all");
        return prefix + "/cloudFile";
    }

    @RequestMapping("/fileManager")
    public String cloudForm() throws BusinessException {
        return prefix + "/fileManager";
    }

    /**
     *
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
        //?????????????????????????·????????
        List<CloudFile> cloudFileList = new ArrayList<>();
        if (StringUtils.isNotEmpty(cloudFile.getFileName())) {

            CloudFile index = cloudFileService.selectByFileId(1L);

            map.put("search", "all");
            cloudFileList.add(index);
            map.put("searchValue", cloudFile.getFileName());
            cloudFileList = nav(cloudFile.getFileName(), cloudFileList);
        } else {
            cloudFileList = cloudFileService.getAllParentPaths(cloudFile.getFileId(), cloudFile.getIsShare());
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
     *共享文件首页
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
        cloudFile.setFileName("共享");
        navigation.add(cloudFile);
        if (StringUtils.isNotEmpty(fileName)) {
            navigation = nav(fileName, navigation);
            map.put("searchValue", fileName);
        }
        map.put("parentPaths", navigation);
        return prefix + "/fileManager";
    }

    /**
     * 分类插叙你可搜索
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
        cloudFile.setFileName(FileType.valueOf(cate.toUpperCase()).getName());
        navigation.add(cloudFile);
        if (StringUtils.isNotEmpty(fileName)) {
            navigation = nav(fileName, navigation);
            map.put("searchValue", fileName);
        }
        map.put("parentPaths", navigation);
        return "forward:/file/fileManager";

    }

    /**
     * 垃圾
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
        cloudFile.setFileName("回收站");
        navigation.add(cloudFile);
        if (StringUtils.isNotEmpty(fileName)) {
            map.put("searchValue", fileName);
        }
        map.put("parentPaths", navigation);
        return prefix + "/fileManager";
    }

    @PostMapping("/verifyName")
    @ResponseBody
    public Response verifyName(String name, Long parentId) throws BusinessException {
        if (!cloudFileService.isAvailable(name, parentId)) {
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
    @ResponseBody
    public Response uploadFile(Long parentId, MultipartFile multipartFile, Map map) throws BusinessException, IOException {
        if(StringUtils.isNull(multipartFile)){
            return Response.fail(ErrorEnum.EMPTY_FILE);
        }
        cloudFileService.uploadFile(parentId, multipartFile);
        CloudFile cloudFile = new CloudFile();
        cloudFile.setParentId(parentId);
        cloudFile.setUserId(ShiroUtil.getSysUser().getUid().longValue());
        map.put("cloudFile", cloudFile);
        return Response.success();
    }


    @PostMapping("/share")
    public Response shareFile(@RequestParam("ids[]") List<Long> ids) throws BusinessException {
        for (Long id : ids) {
            cloudFileService.updateOne(id, FileOperType.SHARE);
        }
        return Response.success();
    }

    @ResponseBody
    @PostMapping("/cancelShare")
    public Response notShareFile(@RequestParam("ids[]") List<Long> ids) throws BusinessException {
        for (Long id : ids) {
            cloudFileService.updateOne(id, FileOperType.CANCELSHARE);
        }
        return Response.success();
    }

    @PostMapping("/trash")
    @ResponseBody
    public Response trashFile(@RequestParam("ids[]") List<Long> ids) throws BusinessException {
        for (Long id : ids) {
            cloudFileService.updateOne(id, FileOperType.TRASH);
        }
        return Response.success();
    }

    @PostMapping("/restore")
    @ResponseBody
    public Response restoreFile(@RequestParam("ids[]") List<Long> ids) throws BusinessException {
        for (Long id : ids) {
            cloudFileService.updateOne(id, FileOperType.RESTORE);
        }
        return Response.success();
    }

    @PostMapping("/remove")
    @ResponseBody
    public Response remove(@RequestParam("ids[]") List<Long> ids) throws BusinessException {
        for (Long id : ids) {
            cloudFileService.updateOne(id, FileOperType.DELETE);
        }

        return Response.success();
    }

    @PostMapping("/rename/{id}")
    @ResponseBody
    public Response rename(@PathVariable("id") Long id, String newName) throws BusinessException {
        CloudFile cloudFile = cloudFileService.selectByFileId(id);
        if (cloudFile == null) {
            return Response.fail(ErrorEnum.DATA_NOT_FOUND);
        }
        if (cloudFile.getFileName().equals(newName)) {
            return Response.success();
        }
        boolean isAvailable = cloudFileService.isAvailable(newName, cloudFile.getParentId());
        cloudFileService.changeName(id, newName);
        if (isAvailable) {
            return Response.success();
        } else {
            return Response.fail(ErrorEnum.DUPLICATE_FILENAME);
        }
    }

    @PostMapping("/move/{src}/to/{parentId}")
    @ResponseBody
    public Response move(@PathVariable("src") Long src, @PathVariable("parentId") Long parentId) throws BusinessException {
        cloudFileService.moveFile(src, parentId);

        return Response.success();
    }

    @PostMapping("/copy/{src}/to/{parentId}")
    @ResponseBody
    public Response copy(@PathVariable("src") Long src, @PathVariable("parentId") Long parentId) throws BusinessException {
        cloudFileService.copyFile(src, parentId);

        return Response.success();
    }

    @GetMapping(value = "/uploadProgress")
    @ResponseBody
    public Response getProgress(HttpServletRequest request) {
        HttpSession session = request.getSession();
        Progress progress = (Progress) session.getAttribute("status");
        System.out.println(progress + "controller");
        return Response.success(progress);
    }

    @PostMapping()
    @RequestMapping("/download/{id}")
    public void download(@PathVariable("id") String fileId, HttpServletResponse response) throws IOException {
        String[] ids = fileId.split(",");
        response.setContentType("application/octet-stream");
        FileInputStream fis = null;
        try {
            //如果是单个文件直接下载否则包装成zip
            if (ids.length == 1 && cloudFileService.selectByFileId(Long.valueOf(ids[0])).getIsDirectory() == 0) {
                CloudFile cloudFile = cloudFileService.selectByFileId(Long.valueOf(ids[0]));
                response.setHeader("Content-Disposition", "attachment; filename=" + URLEncoder.encode(cloudFileService.selectByFileId(Long.valueOf(ids[0])).getFileName(), "UTF-8"));
                fis = new FileInputStream(cloudFile.getFilePath());
                IOUtils.copy(fis, response.getOutputStream());
                response.flushBuffer();
            } else {

                byte[] data = cloudFileService.generateZip(fileId);
                response.setHeader("Content-Disposition", "attachment; filename=" + URLEncoder.encode("下载.zip", "UTF-8"));
                response.addHeader("Content-Length", "" + data.length);
                IOUtils.write(data, response.getOutputStream());
            }
        }finally {
            if(fis!=null){
                fis.close();
            }
        }
    }

    @GetMapping("/commonPath")
    @ResponseBody
    public Response getCommonPath(@RequestParam("ids[]") List<Long> ids) throws BusinessException {
        return Response.success(cloudFileService.getCommonPath(ids));
    }

    /**
     * 获取文件详细信息
     *
     * @param ids
     * @return
     */
    @GetMapping("/filesInfo")
    @ResponseBody
    public Response getFilesInfo(@RequestParam("ids[]") List<Long> ids) throws BusinessException {
        return Response.success(cloudFileService.getFilesInfo(ids));
    }


}
