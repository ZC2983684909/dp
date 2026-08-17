package com.wxmblog.yanjian.controller;

//import org.apache.shiro.authz.annotation.RequiresPermissions;

import com.wxmblog.base.common.web.domain.R;
import com.wxmblog.yanjian.common.rest.response.front.user.SignInResponse;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiOperationSort;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.wxmblog.yanjian.service.SignInService;


/**
 * 用户签到
 *
 * @author crget
 * @email crget@crget.com
 * @date 2025-06-23 16:38:05
 */
@RestController
@RequestMapping("yanjian/signin")
@Api(tags = "用户签到")
public class SignInController {

    @Autowired
    private SignInService signInService;

    @ApiOperation("签到列表")
    @ApiOperationSort(value = 1)
    @GetMapping("/getStatus")
    public R<SignInResponse> getStatus() {
        return R.ok(signInService.getStatus());
    }

    //兑换
    @ApiOperation("兑换")
    @ApiOperationSort(value = 3)
    @GetMapping("/exchange")
    public R<Void> exchange() {
        signInService.exchange();
        return R.ok();
    }
}
