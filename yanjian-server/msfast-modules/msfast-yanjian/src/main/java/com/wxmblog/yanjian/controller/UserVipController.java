package com.wxmblog.yanjian.controller;

//import org.apache.shiro.authz.annotation.RequiresPermissions;

import com.wxmblog.base.common.web.domain.R;
import com.wxmblog.base.common.web.domain.ServiceR;
import com.wxmblog.yanjian.common.rest.response.front.user.vip.UserVipDescResponse;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiOperationSort;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.wxmblog.yanjian.service.UserVipService;


/**
 * 用户vip
 *
 * @author crget
 * @email crget@crget.com
 * @date 2025-08-03 14:23:42
 */
@RestController
@RequestMapping("yanjian/uservip")
@Api(tags = "用户vip")
public class UserVipController {
    @Autowired
    private UserVipService userVipService;

    @ApiOperation("列表")
    @ApiOperationSort(value = 1)
    @GetMapping("/priceList")
    public R<UserVipDescResponse> getPriceList() {
        ServiceR<UserVipDescResponse> serviceR = userVipService.getPriceList();
        if (ServiceR.isError(serviceR)) {
            return R.fail(serviceR.getMsg());
        }
        return R.ok(serviceR.getData());

    }

    @ApiOperation("是否vip")
    @ApiOperationSort(value = 1)
    @GetMapping("/isVip")
    public R<UserVipDescResponse> getIsVip() {
        ServiceR<UserVipDescResponse> serviceR = userVipService.getIsVip();
        if (ServiceR.isError(serviceR)) {
            return R.fail(serviceR.getMsg());
        }
        return R.ok(serviceR.getData());

    }

    @ApiOperation("用户登陆访问")
    @ApiOperationSort(value = 2)
    @GetMapping("/isLogin")
    public R<Void> isLogin() {
        return R.ok();
    }
}
