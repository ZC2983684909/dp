package com.wxmblog.yanjian.controller;

//import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.wxmblog.yanjian.service.UserVisitService;


/**
 * 用户浏览记录
 *
 * @author crget
 * @email crget@crget.com
 * @date 2024-11-21 22:16:00
 */
@RestController
@RequestMapping("yanjian/uservisit")
public class UserVisitController {
    @Autowired
    private UserVisitService userVisitService;

}
