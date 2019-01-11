package com.neo.web;

import com.neo.enums.FileOperType;
import com.neo.exception.BusinessException;
import com.neo.pojo.CloudFile;
import com.neo.service.CloudFileService;
import com.neo.util.Response;
import com.neo.util.ShiroUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Controller
@RequestMapping("/file/cloudFile")
public class CloudFileController {
    private String prefix = "/file/cloudFile";
    @Autowired
    private CloudFileService cloudFileService;

    @GetMapping
    public String cloudForm() {
        return prefix;
    }

    @ResponseBody
    @GetMapping("/all")
    public Response all(CloudFile cloudFile) throws BusinessException {
        cloudFile.setUserId(ShiroUtil.getSysUser().getUid().longValue());
        return Response.success(cloudFileService.selectPersonalOrShareAndNameLike(cloudFile));
    }

    @ResponseBody
    @GetMapping("/sharePage")
    public Response share(String name) throws BusinessException {

        return Response.success(cloudFileService.selectShareFirstPage(name));
    }

    @ResponseBody
    @GetMapping("/{cate}")
    public Response share(@PathVariable("cate") String cate, @RequestParam(required = false) String searchName) throws BusinessException {

        return Response.success(cloudFileService.getCateGoryFileAndNameLike(cate, searchName));
    }

    @ResponseBody
    @GetMapping("/trash")
    public Response trash(@RequestParam(required = false) String searchName) {
        return Response.success(cloudFileService.selectVisibleTrash(searchName));
    }

    @ResponseBody
    @PostMapping("/createDir")
    public Response createDir(Long parentId, String dirName) throws BusinessException {
        cloudFileService.createDirectory(parentId, dirName);
        return Response.success();
    }

    @ResponseBody
    @PostMapping("/upload")
    public Response uploadFile(Long parentId, MultipartFile multipartFile) throws BusinessException, IOException {
        cloudFileService.uploadFile(parentId, multipartFile);
        return Response.success();
    }

    @ResponseBody
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

    @ResponseBody
    @PostMapping("/trash/{id}")
    public Response trashFile(@PathVariable Long id) throws BusinessException {
        cloudFileService.updateOne(id, FileOperType.TRASH);
        return Response.success();
    }

    @ResponseBody
    @PostMapping("/restore/{id}")
    public Response restoreFile(@PathVariable Long id) throws BusinessException {
        cloudFileService.updateOne(id, FileOperType.RESTORE);
        return Response.success();
    }

    @ResponseBody
    @PostMapping("/remove/{id}")
    public Response remove(@PathVariable Long id) throws BusinessException {
        cloudFileService.updateOne(id, FileOperType.DELETE);
        return Response.success();
    }

    @ResponseBody
    @PostMapping("/modifyName/{id}")
    public Response remove(@PathVariable Long id, String newName) throws BusinessException {
        cloudFileService.changeName(id, newName);
        return Response.success();
    }

    @ResponseBody
    @PostMapping("/move/{src}")
    public Response move(@PathVariable Long src, Long parentId) throws BusinessException {
        cloudFileService.moveFile(src, parentId);
        return Response.success();
    }


}
