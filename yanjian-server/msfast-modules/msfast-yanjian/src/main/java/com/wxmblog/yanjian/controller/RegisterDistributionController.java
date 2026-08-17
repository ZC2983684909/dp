package com.wxmblog.yanjian.controller;

//import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.wxmblog.yanjian.service.RegisterDistributionService;


/**
 * 用户推广
 *
 * @author crget
 * @email crget@crget.com
 * @date 2025-04-17 11:08:10
 */
@RestController
@RequestMapping("yanjian/registerdistribution")
public class RegisterDistributionController {
    @Autowired
    private RegisterDistributionService registerDistributionService;

}
