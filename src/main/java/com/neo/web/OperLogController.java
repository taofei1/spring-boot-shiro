package com.neo.web;

import com.github.pagehelper.PageInfo;
import com.neo.annotation.Log;
import com.neo.enums.OperateType;
import com.neo.exception.BusinessException;
import com.neo.pojo.OperLog;
import com.neo.service.OperLogService;
import com.neo.util.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/monitor/operLog")
public class OperLogController {
    String prefix="monitor/operLog";
    @Autowired
    private OperLogService operLogService;
    @GetMapping
    public String get(){
        return prefix;
    }
    @PostMapping("/delete")
    @ResponseBody
    public Response deleteOperLog(String ids) throws BusinessException {
        operLogService.deleteOperLogByIds(ids);
        return Response.success();
    }
    @PostMapping("/clean")
    @ResponseBody
    @Log(title = "²Ù×÷ÈÕÖ¾",businessType = OperateType.CLEAN)
    public Response cleanOperLog(){
        operLogService.cleanOperLog();
        return Response.success();
    }
    @GetMapping("/list")
    @ResponseBody
    public Response getList(@RequestParam(value = "pageNum",defaultValue = "1",required =false) Integer pageNum, @RequestParam(value = "pageSize",defaultValue = "10",required = false) Integer pageSize, OperLog operLog){
        List<OperLog> list= operLogService.selectList(pageNum,pageSize,operLog);
        return Response.success(new PageInfo<>(list));
    }
    @GetMapping("/{id}")
    public String getOne(@PathVariable("id") Long id,Map map){
        map.put("operLog",operLogService.getOne(id));
       return prefix+"_form" ;
    }

}
