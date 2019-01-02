package com.neo.web;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletResponse;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.neo.annotation.Log;
import com.neo.enums.OperateType;
import com.neo.exception.BusinessException;
import com.neo.pojo.CreateTable;
import com.neo.pojo.ColumnInfo;
import com.neo.pojo.TableInfo;
import com.neo.service.TableInfoService;
import com.neo.util.Converter;
import com.neo.util.GenUtils;
import com.neo.util.Response;
import org.apache.commons.io.IOUtils;
import org.apache.poi.ss.formula.functions.T;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

/**
 * 代码生成 操作处理
 *
 * @author ruoyi
 */
@Controller
@RequestMapping("/tool/tableInfo")
public class TableInfoController
{
    private String prefix = "tool";

    @Autowired
    private TableInfoService tableInfoService;

    @GetMapping()
    public String gen()
    {
        return prefix + "/tableInfo";
    }

    @GetMapping("/form")
    public String genForm(Map<String,String> map,String tableName)
    {
        map.put("tableName",tableName);
        return prefix + "/tableInfo_form";
    }
    @GetMapping("/list")
    @ResponseBody
    public Response list(@RequestParam(value = "pageNum",defaultValue = "1",required =false) int pageNum, @RequestParam(value = "pageSize",defaultValue = "10",required = false) int pageSize, TableInfo tableInfo)
    {
        String orderBy="";
        Map<String, Object> map=tableInfo.getParams();
        if(map.get("sort")!=null){
            orderBy+= GenUtils.fieldToColumn((String) map.get("sort"));
            if(map.get("order")!=null){
                orderBy+=" "+map.get("order");
            }
        }
        PageHelper.startPage(pageNum,pageSize,orderBy);
        List<TableInfo> list = tableInfoService.selectTableList(tableInfo);
         return Response.success(new PageInfo<>(list));
    }
    @GetMapping("/{tableName}")
    @ResponseBody
    public Response getColumnsByTablename(@PathVariable("tableName") String tableName) throws BusinessException {
        List<ColumnInfo> columnInfoList=tableInfoService.getColumnsByTableName(tableName);
        CreateTable create=tableInfoService.selectCreateTable(tableName);
        TableInfo tableInfo=new TableInfo();
        tableInfo.setTableName(tableName);
        tableInfo.setColumns(columnInfoList);
        tableInfo.setCreateTable(create);
        return Response.success(tableInfo);
    }
    /**
     * 生成代码
     */
    @GetMapping("/genCode/{tableName}")

    @Log(title = "数据表",businessType = OperateType.GENCODE)
    public void genCode(HttpServletResponse response, @PathVariable("tableName") String tableName) throws IOException
    {
        byte[] data = tableInfoService.generatorCode(tableName);
        response.reset();
        response.setHeader("Content-Disposition", "attachment; filename=\"ruoyi.zip\"");
        response.addHeader("Content-Length", "" + data.length);
        response.setContentType("application/octet-stream; charset=UTF-8");
        IOUtils.write(data, response.getOutputStream());
    }

    /**
     * 批量生成代码
     */
    @GetMapping("/batchGenCode")
    @ResponseBody
    @Log(title = "数据表",businessType = OperateType.GENCODE)
    public void batchGenCode(HttpServletResponse response, String tables) throws IOException
    {
        String[] tableNames = Converter.toStrArray(tables);
        byte[] data = tableInfoService.generatorCode(tableNames);
        response.reset();
        response.setHeader("Content-Disposition", "attachment; filename=\"ruoyi.zip\"");
        response.addHeader("Content-Length", "" + data.length);
        response.setContentType("application/octet-stream; charset=UTF-8");

        IOUtils.write(data, response.getOutputStream());
    }
}
