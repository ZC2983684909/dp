package com.wxmblog.yanjian.controller;

//import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.wxmblog.yanjian.service.UserProfileService;


/**
 * 弹窗记录
 *
 * @author crget
 * @email crget@crget.com
 * @date 2025-07-21 16:40:54
 */
@RestController
@RequestMapping("yanjian/userprofile")
public class UserProfileController {

    @Autowired
    private UserProfileService userProfileService;

}
