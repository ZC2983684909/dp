package com.wxmblog.yanjian.controller;

//import org.apache.shiro.authz.annotation.RequiresPermissions;

import com.wxmblog.base.common.annotation.AuthIgnore;
import com.wxmblog.base.common.web.domain.R;
import com.wxmblog.yanjian.common.rest.request.front.user.EduRequest;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiOperationSort;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.wxmblog.yanjian.service.UserEduAuditService;


/**
 * 备注
 *
 * @author crget
 * @email crget@crget.com
 * @date 2024-11-16 21:00:20
 */
@RestController
@RequestMapping("yanjian/usereduaudit")
@Api(tags = "学历校验")
public class UserEduAuditController {

    @Autowired
    private UserEduAuditService userEduAuditService;

    @ApiOperation("学历校验")
    @ApiOperationSort(value = 1)
    @GetMapping("/eduCheck")
    @AuthIgnore
    public R<EduRequest> getReadStatus(@RequestParam String onlineVerificationCode){

        return userEduAuditService.checkEdu(onlineVerificationCode);
    }
}
