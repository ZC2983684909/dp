package com.wxmblog.yanjian.controller;

//import org.apache.shiro.authz.annotation.RequiresPermissions;
import com.wxmblog.base.common.constant.ParamTypeConstants;
import com.wxmblog.base.common.utils.PageResult;
import com.wxmblog.base.common.web.domain.R;
import com.wxmblog.yanjian.common.rest.response.front.user.UserInfoPageResponse;
import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.wxmblog.yanjian.service.UserShieldService;


/**
 * 备注
 *
 * @author crget
 * @email crget@crget.com
 * @date 2024-11-27 22:49:44
 */
@RestController
@RequestMapping("yanjian/usershield")
@Api(tags = "用户屏蔽")
public class UserShieldController {

    @Autowired
    private UserShieldService userShieldService;

    @ApiImplicitParams({@ApiImplicitParam(paramType = ParamTypeConstants.requestParam, name = "pageIndex", value = "页码", defaultValue = "1"), @ApiImplicitParam(paramType = ParamTypeConstants.requestParam, name = "pageSize", value = "数量", defaultValue = "10")})
    @ApiOperation("已屏蔽用户")
    @ApiOperationSort(value = 1)
    @GetMapping("/page")
    public R<PageResult<UserInfoPageResponse>> getPage(@RequestParam(value = "pageIndex", required = false, defaultValue = "1") Integer pageIndex,
                                                       @RequestParam(value = "pageSize", required = false, defaultValue = "10") Integer pageSize) {
        return R.ok(userShieldService.getPage(pageIndex, pageSize));
    }

    @ApiOperation("屏蔽用户")
    @ApiOperationSort(value = 2)
    @GetMapping("/shield/{id}")
    public R<Void> shield(@PathVariable String id) {
        userShieldService.shield(id);
        return R.ok();
    }

    @ApiOperation("取消屏蔽")
    @ApiOperationSort(value = 3)
    @GetMapping("/cancel/{id}")
    public R<Void> cancel(@PathVariable String id) {
        userShieldService.cancel(id);
        return R.ok();
    }

}
