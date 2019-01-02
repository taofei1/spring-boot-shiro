package com.neo.web;

import com.neo.annotation.Log;
import com.neo.entity.CodeTable;
import com.neo.enums.OperateType;
import com.neo.service.CodeTableService;
import com.neo.util.Msg;
import com.neo.util.RequestUtil;
import com.neo.util.Util;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Controller
@RequestMapping("/codeTable")
public class CodeTableController {
    private   final Logger log= LoggerFactory.getLogger(this.getClass());
    @Autowired
    public CodeTableService codeTableService;
    @GetMapping("/search")
    @ResponseBody

    public Msg findAll(CodeTable codeTable){

        return Msg.success().addData(codeTableService.findAll(codeTable));
    }
    @GetMapping("/cate/{cate}")
    @ResponseBody
    public Msg findCate(@PathVariable("cate") String cate){
        CodeTable codeTable=new CodeTable();
        codeTable.setCate(cate);
        return Msg.success().addData(codeTableService.findAll(codeTable));
    }
    @GetMapping("/codeTableList")
    @ResponseBody
    public Page<CodeTable> findAll(@RequestParam("pageNum") int pageNum, @RequestParam("pageSize") int pageSize, @RequestParam(value = "sortName",required = false) List<String> sortName, @RequestParam(value="sortOrder",required = false)List<Integer> type, CodeTable codeTable)  {
        Page<CodeTable> page;
        if(Util.isNull(codeTable)) {
            page = codeTableService.findAll(pageNum, pageSize, sortName, type);
        }else{
            page=codeTableService.findAll(pageNum, pageSize, sortName, type,codeTable);
        }
        return page;
    }
    @GetMapping("/{id}")
    @ResponseBody
    @Log(title = "代码表")
    public Msg getById(@PathVariable("id") Integer id){
        if(id==null){
            return Msg.fail().addData("no parameter id");
        }
        CodeTable codeTable=codeTableService.findById(id);
        if(codeTable==null){
            return Msg.fail().addData("not exist codeTable[id="+id+"]");
        }
        return Msg.success().addData(codeTable);
    }
    @GetMapping("/all")
    @ResponseBody
    public Msg finAll()
    {

        return Msg.success().addData(codeTableService.findAll());
    }
    @PostMapping("/saveOrUpdate")
    @ResponseBody
    @Log(title = "代码表",businessType = OperateType.INSERT)
    public Msg add(CodeTable codeTable){
        if(codeTable.getId()==null){
            codeTable.setStatus(1);
        }
        log.info(codeTable.toString());
        codeTableService.save(codeTable);
        return Msg.success();
    }
    @PostMapping("/delete")
    @ResponseBody
    @Log(title = "代码表",businessType = OperateType.DELETE)
    public Msg delete(@RequestBody List<Integer> ids){
        codeTableService.delete(ids);
        return Msg.success();
    }
    @PostMapping("/status")
    @ResponseBody
    @Log(title = "代码表",businessType = OperateType.UPDATE)
    public Msg changeStatus(Integer id,Integer status){
        CodeTable codeTable=codeTableService.findById(id);
        if(codeTable==null){
            return Msg.fail().addData("not exist code[id="+id+"]！");
        }
        codeTable.setStatus(status);
        codeTableService.save(codeTable);
        return Msg.success();
    }
}
