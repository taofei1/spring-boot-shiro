package com.neo.web;

import com.neo.dto.MailConfigDTO;
import com.neo.annotation.Log;
import com.neo.config.GlobalConfig;
import com.neo.entity.Dept;
import com.neo.entity.UserInfo;
import com.neo.enums.OperateType;
import com.neo.exception.BusinessException;
import com.neo.enums.ErrorEnum;
import com.neo.service.DeptService;
import com.neo.service.EmailService;
import com.neo.service.RoleService;
import com.neo.service.UserInfoService;
import com.neo.util.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.formula.functions.T;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;


import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;


import javax.validation.Valid;
import java.io.File;
import java.io.IOException;
import java.util.*;
@Controller
@Slf4j
public class UserInfoController {
    @Autowired
    private UserInfoService userInfoService;
    @Autowired
    private DeptService deptService;
    @Autowired
    private RoleService roleService;
    @Autowired
    private EmailService emailService;
    @Autowired
    private ApplicationEventPublisher publisher;

    @GetMapping("/userList/{pageNum}")
    @ResponseBody
    public Page<UserInfo> userList(@PathVariable("pageNum") int pageNum,@RequestParam("pageSize") int pageSize,@RequestParam(value = "sortName",required = false) String sortName,@RequestParam(value = "type",required = false) Integer type,UserInfo user)  {
             Page<UserInfo> page;
            if(Util.isNull(user)) {
               page = userInfoService.findAll(pageNum, pageSize, sortName, type);
            }else{
                page=userInfoService.findAll(pageNum, pageSize, sortName, type,user);
            }
        return page;
    }
    @RequestMapping("/add")
    @ResponseBody
    @Log(title = "�û���",businessType = OperateType.INSERT)
    public Msg addUser(@Valid UserInfo user,BindingResult result, @RequestParam(required = false, value = "dept_ids[]") List<Integer> dept_ids, @RequestParam(required = false, value = "role_ids[]")List<Integer> role_ids){

        List<String> errors;
        if(result.hasErrors()){
            errors= new ArrayList<>();
            for (ObjectError objectError:result.getAllErrors()) {
                errors.add(objectError.getDefaultMessage());
            }
            System.out.println(errors);
            return Msg.fail().addData(errors);
        }
        log.info(user.toString());
        if(dept_ids!=null) {
            List<Dept> depts=deptService.findAll(dept_ids);
            user.setDeptList(new HashSet(depts));
        }
        if(role_ids!=null){

            user.setRoleList(roleService.findByIds(role_ids));
        }
        String uuid= UUID.randomUUID().toString().replace("-","");
        //����û�����6λ������벢����������
        if(user.getUid()==null){
            String randomPassword=Util.genRandomNum(6);
            log.info("username:"+user.getUsername()+";password:"+randomPassword);
            String savePwd=Util.MD5Pwd(randomPassword,uuid,2);
            user.setState(1);
            user.setSalt(uuid);
            user.setPassword(savePwd);
            user.setIsFirst(0);
            user.setAvatar("");
            userInfoService.save(user);
            UserInfo copy = new UserInfo();
            BeanUtils.copyProperties(user, copy);
            copy.setPassword(randomPassword);
            publisher.publishEvent(copy);
//            Runnable runnable=  () -> emailService.sendHtmlEmail(user.getEmail(),"������ʾ","<p style='font:20px;font-weight:bold;'>���ĵ�½���ǣ�<span style='color:red'>"+user.getUsername()+"</span>���ߵ�ǰ����,�����ǣ�<span style='color:red'>"+randomPassword+"</span>,�״ε�½���޸����룡</p>");
            //         Thread thread=new Thread(runnable);
            //      thread.start();
        }else{
            UserInfo userInfo=userInfoService.findById(user.getUid());
            user.setPassword(userInfo.getPassword());
            user.setIsFirst(userInfo.getIsFirst());
            user.setSalt(userInfo.getSalt());
            user.setAvatar(userInfo.getAvatar());
            userInfoService.save(user);
        }

        return Msg.success();
    }
    @RequestMapping("/deleteUsers")
    @ResponseBody
    @Log(title = "�û���",businessType = OperateType.DELETE)
    public Msg<String> deleteUser(@RequestBody List<Integer> deleteIds){
        UserInfo ui=(UserInfo) SecurityUtils.getSubject().getPrincipal();
        for(Integer id:deleteIds){
            if(id==ui.getUid()){
                return Msg.fail().addData("����ɾ����ǰ�û���");
            }

        }
        userInfoService.delete(deleteIds);
        return Msg.success();
    }
    @GetMapping("/getOne")
    @ResponseBody
    public Msg findUserByid(Integer uid){
        log.info(uid.toString());
        if(uid!=null){
            UserInfo user=userInfoService.findById(uid);
            if(user==null){
              return  Msg.fail().addData("not exist user[id="+uid+"]��");
            }
            return Msg.success().addData(user);
        }else{
            return Msg.fail().addData("no parameter id");
        }
    }
    @GetMapping("/getByUsername")
    @ResponseBody
    public Msg findUserByUsername(String username){
        if(username==null){
            return Msg.fail().addData("û�д����û���!");
        }
        UserInfo user=userInfoService.findByUsername(username);
        return Msg.success().addData(user);
    }
    @GetMapping("/getByEmail")
    @ResponseBody
    public Msg findUserEmail(String email){
        if(email==null){
            return Msg.fail().addData("û�д����û���!");
        }
        UserInfo user=userInfoService.findByEmail(email);
        return Msg.success().addData(user);
    }
    @RequestMapping("/status")
    @ResponseBody
    @Log(title = "�û���",businessType = OperateType.UPDATE)
    public Msg changeStatus(Integer uid,Integer status){
        UserInfo user=userInfoService.findById(uid);
        if(user==null){
            return Msg.fail().addData("�û������ڣ�");
        }
        user.setState(status);
        userInfoService.save(user);
        return Msg.success();

    }
    @PostMapping("/modifyPwd")
    @ResponseBody
    @Log(title = "�û���",businessType = OperateType.UPDATE)
    public Msg modifyPwd(String oldPwd,String newPwd,String confirmPwd){
        UserInfo user= (UserInfo) SecurityUtils.getSubject().getPrincipal();
        if(!newPwd.equals(confirmPwd)){
          return  Msg.fail().addData("Inconsistent password entry twice��");
        }
        String pwd=Util.MD5Pwd(oldPwd,user.getSalt(),2);
        if(!pwd.equals(user.getPassword())){
            return Msg.fail().addData("Old password is incorrect��");
        }
        String saveSalt = UUID.randomUUID().toString().replace("-", "");
        String savePwd=Util.MD5Pwd(newPwd,saveSalt,2);
        user.setSalt(saveSalt);
        user.setPassword(savePwd);
        user.setIsFirst(1);
        userInfoService.save(user);
        SecurityUtils.getSubject().logout();
        return Msg.success();

    }

    @GetMapping("/getCurrentEmailConfig")
    @ResponseBody
    public Msg<T> index(){
        //��ȡ�û���Ϣ
        Subject subject= SecurityUtils.getSubject();
        UserInfo ui=(UserInfo)subject.getPrincipal();
        UserInfo u=userInfoService.findById(ui.getUid());
        MailConfigDTO mc=new MailConfigDTO();
         mc= (MailConfigDTO)Dto2Entity.populate(u,mc);
         log.info(mc.toString());
        return Msg.success().addData(mc);

    }
    @PostMapping("/updateEmailSetting/{uid}")
    @ResponseBody
    @Log(title = "�û���",businessType = OperateType.UPDATE)
    public Msg<T> updateUserEmailSetting(@PathVariable("uid") Integer uid,MailConfigDTO mc)
    {
        UserInfo ui=userInfoService.findById(uid);
        if(ui==null){
            return new Msg<>(ErrorEnum.DATA_NOT_FOUND);
        }
        ui= (UserInfo) Dto2Entity.populate(mc,ui);
        userInfoService.save(ui);
        return Msg.success();
    }

    @GetMapping("/avatar/{id}")
    public String getAvatar(@PathVariable Integer id, ModelMap map) {
        map.put("user", userInfoService.findById(id));
        return "userInfo/avatar";
    }

    @PostMapping("/avatar/update/{id}")
    @ResponseBody
    public Response updateAvatar(@PathVariable Integer id, @RequestParam("avatar") MultipartFile multipartFile) throws BusinessException, IOException {
        UserInfo ui = userInfoService.findById(id);
        if (ui == null) {
            throw new BusinessException(ErrorEnum.DATA_NOT_FOUND, "�û�Ϊ�ҵ�");
        }
        if (multipartFile != null) {
            String name = UUID.randomUUID().toString();
            log.info("type:{},name:{},original{}", multipartFile.getContentType(),
                    multipartFile.getName(), multipartFile.getOriginalFilename());
            String type = multipartFile.getContentType();
            String sufix = type.substring(type.indexOf("/") + 1);
            multipartFile.transferTo(new File(GlobalConfig.getAvatarPath() + name + "." + sufix));

            ui.setAvatar(name + "." + sufix);
            userInfoService.save(ui);

        }
        return Response.success();
    }
}