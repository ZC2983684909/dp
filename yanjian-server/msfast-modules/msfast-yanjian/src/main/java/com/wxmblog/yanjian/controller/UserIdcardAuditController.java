package com.wxmblog.yanjian.controller;

//import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.wxmblog.yanjian.service.UserIdcardAuditService;


/**
 * 备注
 *
 * @author crget
 * @email crget@crget.com
 * @date 2024-11-17 00:32:20
 */
@RestController
@RequestMapping("yanjian/useridcardaudit")
public class UserIdcardAuditController {
    @Autowired
    private UserIdcardAuditService userIdcardAuditService;

}
