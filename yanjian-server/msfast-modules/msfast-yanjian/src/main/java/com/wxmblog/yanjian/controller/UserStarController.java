package com.wxmblog.yanjian.controller;

//import org.apache.shiro.authz.annotation.RequiresPermissions;

import com.wxmblog.base.common.constant.ParamTypeConstants;
import com.wxmblog.base.common.utils.PageResult;
import com.wxmblog.base.common.web.domain.R;
import com.wxmblog.yanjian.common.rest.request.front.star.UserStarRequest;
import com.wxmblog.yanjian.common.rest.response.front.user.UserStarResponse;
import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.wxmblog.yanjian.service.UserStarService;


/**
 * 备注
 *
 * @author crget
 * @email crget@crget.com
 * @date 2024-11-17 17:45:51
 */
@RestController
@RequestMapping("yanjian/userstar")
@Api(tags = "用户收藏")
public class UserStarController {
    @Autowired
    private UserStarService userStarService;

    /**
     * 列表
     */

    @ApiOperation("收藏已读")
    @ApiOperationSort(value = 3)
    @GetMapping("/read/{id}")
    public R<Void> stareadr(@PathVariable String id) {
        userStarService.read(id);
        return R.ok();
    }
}
