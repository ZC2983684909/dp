package com.wxmblog.yanjian.controller;

//import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.wxmblog.yanjian.service.TUserTempService;


/**
 * 备注
 *
 * @author crget
 * @email crget@crget.com
 * @date 2025-07-28 22:47:49
 */
@RestController
@RequestMapping("yanjian/tusertemp")
public class TUserTempController {
    @Autowired
    private TUserTempService tUserTempService;

}
