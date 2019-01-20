package com.neo.web;

import com.neo.enums.FileOperType;
import com.neo.enums.FileType;
import com.neo.exception.BusinessException;
import com.neo.exception.ErrorEnum;
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

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
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

    //��ҳ
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
     * ����ָ���ļ���
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
        //���Ʋ�Ϊ��������������ʱ����·����Ҫ�ı�
        List<CloudFile> cloudFileList = new ArrayList<>();
        if (StringUtils.isNotEmpty(cloudFile.getFileName())) {

            CloudFile index = cloudFileService.selectByFileId(1L);

            map.put("search", "all");
            cloudFileList.add(index);
            map.put("searchValue", cloudFile.getFileName());
            cloudFileList = nav(cloudFile.getFileName(), cloudFileList);
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
        c.setFileName("\"" + fileName + "\"" + "���������");
        list.add(c);
        return list;

    }

    /**
     * ����ҳ�����ҳ
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
        cloudFile.setFileName("�����ļ�");
        navigation.add(cloudFile);
        if (StringUtils.isNotEmpty(fileName)) {
            navigation = nav(fileName, navigation);
            map.put("searchValue", fileName);
        }
        map.put("parentPaths", navigation);
        return prefix + "/fileManager";
    }

    /**
     * ��ѯÿ�����������
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
     * ��ѯ�ɼ�������
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
        cloudFile.setFileName("����");
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

    @RequestMapping("/download/{id}")
    public void download(@PathVariable("id") Long fileId, HttpServletResponse response) throws IOException {
        List<CloudFile> files=cloudFileService.getChildrenFiles(fileId);
        response.setContentType("application/octet-stream");
        FileInputStream fis = null;
        try {
            for (CloudFile file : files) {
                fis = new FileInputStream(new File(file.getFilePath()));
             /*   byte[] b=new byte[1024];
                int i;
                while((i=fis.read(b))!=-1){
                    response.getOutputStream().write(b,0,i);
                }*/
                response.setHeader("Content-Disposition", "attachment; filename=" + file.getFileName());
                IOUtils.copy(fis, response.getOutputStream());

            }
            response.flushBuffer();

        }finally {
            if(fis!=null){
                fis.close();
            }
        }
    }


}
