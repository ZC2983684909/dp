package com.wxmblog.yanjian.controller;

//import org.apache.shiro.authz.annotation.RequiresPermissions;

import com.wxmblog.base.common.constant.ParamTypeConstants;
import com.wxmblog.base.common.utils.PageResult;
import com.wxmblog.base.common.web.domain.R;
import com.wxmblog.yanjian.common.rest.request.front.distribution.DistributionAmountRequest;
import com.wxmblog.yanjian.common.rest.response.front.distribution.DistributionAmountResponse;
import com.wxmblog.yanjian.common.rest.response.front.distribution.WithdrawRecordResponse;
import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.wxmblog.yanjian.service.DistributionAmountService;


/**
 * 用户分销金额
 *
 * @author crget
 * @email crget@crget.com
 * @date 2025-08-21 16:46:20
 */
@RestController
@RequestMapping("yanjian/distributionamount")
@Api(tags = "用户分销金额")
public class DistributionAmountController {

    @Autowired
    private DistributionAmountService distributionAmountService;

    /*
     * 分销明细字段有 用户头像（模糊），奖励金额，奖励时间，消费描述（颜币充值，vip充值）
     *  */
    @ApiImplicitParams({@ApiImplicitParam(paramType = ParamTypeConstants.requestParam, name = "pageIndex", value = "页码", defaultValue = "1"),
            @ApiImplicitParam(paramType = ParamTypeConstants.requestParam, name = "pageSize", value = "数量", defaultValue = "10")})
    @ApiOperation("分销明细")
    @ApiOperationSort(value = 1)
    @GetMapping("/page")
    public R<PageResult<DistributionAmountResponse>> distributionAmountPage(DistributionAmountRequest request, @RequestParam(value = "pageIndex", required = false, defaultValue = "1") Integer pageIndex,
                                                                            @RequestParam(value = "pageSize", required = false, defaultValue = "10") Integer pageSize) {
        return R.ok(distributionAmountService.distributionAmountPage(request,pageIndex, pageSize));
    }

    @ApiImplicitParams({@ApiImplicitParam(paramType = ParamTypeConstants.requestParam, name = "pageIndex", value = "页码", defaultValue = "1"),
            @ApiImplicitParam(paramType = ParamTypeConstants.requestParam, name = "pageSize", value = "数量", defaultValue = "10")})
    @ApiOperation("提现记录")
    @ApiOperationSort(value = 1)
    @GetMapping("/withdrawRecord")
    public R<PageResult<WithdrawRecordResponse>> withdrawRecordPage(@RequestParam(value = "pageIndex", required = false, defaultValue = "1") Integer pageIndex,
                                                                        @RequestParam(value = "pageSize", required = false, defaultValue = "10") Integer pageSize) {
        return R.ok(distributionAmountService.withdrawRecordPage(pageIndex, pageSize));
    }


}
