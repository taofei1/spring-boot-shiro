package com.neo.service.impl;

import com.neo.constants.GenerateCodeSettings;
import com.neo.exception.BusinessException;
import com.neo.enums.ErrorEnum;
import com.neo.mapper.GenMapper;
import com.neo.pojo.CreateTable;
import com.neo.pojo.ColumnInfo;
import com.neo.pojo.TableInfo;
import com.neo.service.TableInfoService;
import com.neo.util.*;
import com.neo.yml.EntityConfig;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;

import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Slf4j
@Service
public class TableInfoServcieImpl implements TableInfoService {

    @Autowired
    private GenMapper genMapper;
    @Autowired
    private EntityConfig entityConfig;
    @Override
    public List<TableInfo> selectTableList(TableInfo tableInfo) {
        List<TableInfo> list= genMapper.selectTableList(tableInfo);
        list.forEach((tableInfo1)->tableInfo1.setClassName(GenUtils.tableToJava(tableInfo1.getTableName())));
        return list;
    }

    @Override
    public CreateTable selectCreateTable(String tableName) {
        return genMapper.selectCreateTable(tableName);
    }

    @Override
    public List<ColumnInfo> getColumnsByTableName(String tableName) throws BusinessException {
        if(StringUtils.isEmpty(tableName)){
            throw new BusinessException(ErrorEnum.PARAM_ERROR,"表名不能为空");
        }
        List<ColumnInfo> columnInfoList = genMapper.selectTableColumnsByName(tableName);
        columnInfoList.forEach(columnInfo -> {
            try {
                columnInfo.setColumnComment(columnInfo.getColumnComment());
            } catch (Exception e) {
                e.printStackTrace();
            }
            columnInfo.setAttrname(GenUtils.columnToField(columnInfo.getColumnName()));
            if("pri".equals(columnInfo.getIsPrimaryKey().toLowerCase())){
                columnInfo.setIsPrimaryKey("YES");
            }else{
                columnInfo.setIsPrimaryKey("NO");
            }


            String fileType= "";
            try {
                fileType = getFieldType(entityConfig.getPackages(),tableName,GenUtils.columnToField(columnInfo.getColumnName()));
            } catch (IOException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
            if(StringUtils.isEmpty(fileType)){
               fileType=GenUtils.javaTypeMap.get(columnInfo.getDataType());
           }

            columnInfo.setAttrType(fileType);
        });
        return columnInfoList;
    }
    //寻找该字段的Java类型
    public static String getFieldType(List<String> packages,String tableName,String fieldName) throws IOException, ClassNotFoundException {
        if(ObjectUtils.isEmpty(packages)||StringUtils.isEmpty(tableName)||StringUtils.isEmpty(fieldName)){
            return "";
        }
        List<String> list=new ArrayList<>();
        String fieldTypeSimpleName="";
        //扫描配置的包集合
        for(String p:packages){
            list.addAll(ClassUtils.getClassName(p,false));
        }
        String javaName=GenUtils.tableToJava(tableName);
        for(String p:list){
            //获取简单java名
            String simpleName=p.substring(p.lastIndexOf(".")+1);
            //若找到该类则遍历字段
            if(simpleName.equalsIgnoreCase(javaName)){
                Class c=Class.forName(p);
                fieldTypeSimpleName=ReflectUtil.getFieldType(c,fieldName);
            }
        }
        return fieldTypeSimpleName;


    }
    @Override
    public byte[] generatorCode(String tableName) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        ZipOutputStream zip = new ZipOutputStream(outputStream);
        // 查询表信息
        TableInfo table = genMapper.selectTableByName(tableName);
        // 查询列信息
        List<ColumnInfo> columns = genMapper.selectTableColumnsByName(tableName);
        // 生成代码
        generatorCode(table, columns, zip);
        try {
            zip.close();
        } catch (IOException e) {
            log.error("zip输出流关闭失败!",e);
        }
        return outputStream.toByteArray();
    }

    @Override
    public byte[] generatorCode(String[] tableNames) {
         ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        ZipOutputStream zip = new ZipOutputStream(outputStream);
        for (String tableName : tableNames)
        {
            // 查询表信息
            TableInfo table = genMapper.selectTableByName(tableName);
            // 查询列信息
            List<ColumnInfo> columns = genMapper.selectTableColumnsByName(tableName);
            // 生成代码
            generatorCode(table, columns, zip);
        }
        try {
            zip.close();
        } catch (IOException e) {
            log.error("zip输出流关闭失败!",e);
        }
        return outputStream.toByteArray();
    }
    /**
     * 生成代码
     */
    public void generatorCode(TableInfo table, List<ColumnInfo> columns, ZipOutputStream zip)
    {
        // 表名转换成Java属性名
        String className = GenUtils.tableToJava(table.getTableName());
        table.setClassName(className);
        table.setClassname(StringUtils.uncapitalize(className));
        // 列信息
        table.setColumns(GenUtils.transColums(columns));
        // 设置主键
        table.setPrimaryKey(table.getColumnsLast());

        VelocityInitializer.initVelocity();

        String packageName = GenerateCodeSettings.PACKAGE_NAME;
        String moduleName = GenUtils.getModuleName(packageName);

        VelocityContext context = GenUtils.getVelocityContext(table);

        // 获取模板列表
        List<String> templates = GenUtils.getTemplates();
        for (String template : templates)
        {
            // 渲染模板
            StringWriter sw = new StringWriter();
            Template tpl = Velocity.getTemplate(template, CharsetKit.UTF_8);

            //将模板内容渲染进sw中
            tpl.merge(context, sw);
            try
            {
                // 添加到zip，此时的zipentry的参数并不是一个真实的文件，只是生成的文件路劲名
                zip.putNextEntry(new ZipEntry(GenUtils.getFileName(template, table, moduleName)));
                //将模板内容写入zip中
                IOUtils.write(sw.toString(), zip, CharsetKit.UTF_8);
                sw.close();
                zip.closeEntry();

            }
            catch (IOException e)
            {
                log.error("渲染模板失败，表名：" + table.getTableName(), e);
            }

        }
    }

}
