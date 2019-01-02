package com.neo.web;

import com.neo.annotation.Log;
import com.neo.entity.Server;
import com.neo.enums.OperateType;
import com.neo.service.ServerService;
import com.neo.util.Msg;
import com.neo.util.Util;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/server")
public class ServerController {
    private Logger log= LoggerFactory.getLogger(this.getClass());
    @Autowired
    public ServerService serverService;
    @GetMapping("/search")
    @ResponseBody
    public Msg findAll(Server server){

        return Msg.success().addData(serverService.findAll(server));
    }
    @GetMapping("/serverList")
    @ResponseBody
    public Page<Server> findAll(@RequestParam("pageNum") int pageNum, @RequestParam("pageSize") int pageSize, @RequestParam(value = "sortName",required = false) List<String> sortName, @RequestParam(value="sortOrder",required = false)List<Integer> type, Server server)  {

        Page<Server> page;
        if(Util.isNull(server)) {
            page = serverService.findAll(pageNum, pageSize, sortName, type);
        }else{
            page=serverService.findAll(pageNum, pageSize, sortName, type,server);
        }
        System.out.println(page.getContent());
        return page;
    }
    @GetMapping("/{id}")
    @ResponseBody
    public Msg getById(@PathVariable("id") Integer id){
        if(id==null){
            return Msg.fail().addData("no parameter id");
        }
        Server server=serverService.findById(id);
        if(server==null){
            return Msg.fail().addData("not exist server[id="+id+"]");
        }
        return Msg.success().addData(server);
    }
    @GetMapping("/all")
    @ResponseBody
    public Msg finAll()
    {

        return Msg.success().addData(serverService.findAll());
    }
    @PostMapping("/saveOrUpdate")
    @ResponseBody
    @Log(title = "服务器表",businessType = OperateType.INSERT)
    public Msg add(Server server){
        if(server.getId()==null){
            server.setStatus(1);
        }else{
            if(server.getType().equals("2")) {
                server.setPort("");
            }
        }
        log.info(server.toString());
        serverService.save(server);
        return Msg.success();
    }
    @PostMapping("/delete")
    @ResponseBody
    @Log(title = "服务器表",businessType = OperateType.DELETE)

    public Msg delete(@RequestBody List<Integer> ids){
        serverService.delete(ids);
        return Msg.success();
    }
    @PostMapping("/status")
    @ResponseBody
    @Log(title = "服务器表",businessType = OperateType.UPDATE)

    public Msg changeStatus(Integer id,Integer status){
        Server server=serverService.findById(id);
        if(server==null){
            return Msg.fail().addData("部门[id="+id+"]不存在！");
        }
        server.setStatus(status);
        serverService.save(server);
        return Msg.success();
    }

}
