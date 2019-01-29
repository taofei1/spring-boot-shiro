package com.neo.web;

import com.neo.dto.LoginLogDTO;
import com.neo.annotation.Log;
import com.neo.entity.LoginLog;
import com.neo.enums.OperateType;
import com.neo.exception.BusinessException;
import com.neo.enums.ErrorEnum;
import com.neo.service.UserLoginLogService;
import com.neo.util.*;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/loginLogs")
public class LoginLogController {
    @Autowired
    private UserLoginLogService userLoginLogService;
    @GetMapping("/")
    @ResponseBody
    public Response findAll(@RequestParam("pageNum") int pageNum, @RequestParam("pageSize") int pageSize, @RequestParam(value = "sortName",required = false) String sortName, @RequestParam(value="sortOrder",required = false)Integer type, LoginLogDTO log)  {
        Page<LoginLogDTO> page;
        pageNum--;
        if(Util.isNull(log)) {
            page = userLoginLogService.findAll(pageNum, pageSize, sortName, type);
        }else{
            LoginLog loginLog=new LoginLog();
            BeanUtils.copyProperties(log,loginLog);
            page=userLoginLogService.findAll(pageNum, pageSize, sortName, type,loginLog);
        }

        return Response.success(page);
    }
    @GetMapping("/{id}")
    @ResponseBody
    public Response getOne(@PathVariable Integer id){
        LoginLogDTO log=userLoginLogService.findById(id);
        if(log==null){
            return Response.fail(ErrorEnum.DATA_NOT_FOUND);
        }
        return Response.success(log);
    }
    @PostMapping("/delete")
    @ResponseBody
    @Log(title = "登陆日志表",businessType = OperateType.DELETE)
    public Response deleteOne(String ids) throws BusinessException {
        userLoginLogService.delete(ids);
        return Response.success();
    }
    @PostMapping("/clean")
    @ResponseBody
    @Log(title = "登陆日志表",businessType = OperateType.CLEAN)
    public Response clean(){
        userLoginLogService.clean();
        return Response.success();
    }
    @PostMapping("/delete/ids")
    @ResponseBody
    @Log(title = "登陆日志表",businessType = OperateType.DELETE)
    public Response deleteBatch(@RequestBody List<Integer> ids) throws BusinessException {
        userLoginLogService.delete(ids);
        return Response.success();
    }
}
