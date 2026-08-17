package com.wxmblog.yanjian.controller;

//import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.wxmblog.yanjian.service.DistributionPersonService;


/**
 * 用户推广人
 *
 * @author crget
 * @email crget@crget.com
 * @date 2025-04-17 11:19:38
 */
@RestController
@RequestMapping("yanjian/distributionperson")
public class DistributionPersonController {
    @Autowired
    private DistributionPersonService distributionPersonService;

}
