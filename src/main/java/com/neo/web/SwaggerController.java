package com.neo.web;

import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiresPermissions("swagger:view")
public class SwaggerController {
    @GetMapping("/tool/swagger")
    public String index(){
        return "redirect:/swagger-ui.html";
    }
}
