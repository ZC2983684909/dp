package com.wxmblog.yanjian.controller;

//import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.wxmblog.yanjian.service.UserNatureVisitService;


/**
 * 用户浏览记录
 *
 * @author crget
 * @email crget@crget.com
 * @date 2025-09-03 16:57:08
 */
@RestController
@RequestMapping("yanjian/usernaturevisit")
public class UserNatureVisitController {

    @Autowired
    private UserNatureVisitService userNatureVisitService;

}
