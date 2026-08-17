package com.wxmblog.yanjian.controller;

//import org.apache.shiro.authz.annotation.RequiresPermissions;

import com.wxmblog.base.common.constant.ParamTypeConstants;
import com.wxmblog.base.common.utils.PageResult;
import com.wxmblog.base.common.web.domain.R;
import com.wxmblog.base.common.web.domain.ServiceR;
import com.wxmblog.yanjian.common.rest.response.front.pay.AddBalanceResponse;
import com.wxmblog.yanjian.common.rest.response.front.pay.BalancePageResponse;
import com.wxmblog.yanjian.common.rest.response.front.user.UserInfoPageResponse;
import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.wxmblog.yanjian.service.UserAccountService;


/**
 * 用户钱包
 *
 * @author crget
 * @email crget@crget.com
 * @date 2025-08-03 13:57:13
 */
@RestController
@RequestMapping("yanjian/useraccount")
@Api(tags = "用户钱包")
public class UserAccountController {
    @Autowired
    private UserAccountService userAccountService;


    @ApiOperation("充值颜币预览")
    @ApiOperationSort(value = 1)
    @GetMapping("/amount/pre")
    public R<AddBalanceResponse> getBalancePre() {

        ServiceR<AddBalanceResponse> ret = userAccountService.getBalancePre();
        if (ServiceR.isError(ret)) {
            return R.fail(ret.getMsg());
        }
        return R.ok(ret.getData());
    }
    //BalancePageResponse

    @ApiImplicitParams({@ApiImplicitParam(paramType = ParamTypeConstants.requestParam, name = "pageIndex", value = "页码", defaultValue = "1"),
            @ApiImplicitParam(paramType = ParamTypeConstants.requestParam, name = "pageSize", value = "数量", defaultValue = "10")})
    @ApiOperation("金额明细")
    @ApiOperationSort(value = 2)
    @GetMapping("/page")
    public R<PageResult<BalancePageResponse>> page(@RequestParam(value = "pageIndex", required = false, defaultValue = "1") Integer pageIndex,
                                                   @RequestParam(value = "pageSize", required = false, defaultValue = "10") Integer pageSize) {
        return R.ok(userAccountService.accountPage(pageIndex, pageSize));
    }
}
