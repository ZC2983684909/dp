package com.wxmblog.yanjian.controller;

//import org.apache.shiro.authz.annotation.RequiresPermissions;

import com.wxmblog.base.common.web.domain.R;
import com.wxmblog.base.common.web.domain.ServiceR;
import com.wxmblog.yanjian.common.rest.request.front.user.ApplyWxRequest;
import com.wxmblog.yanjian.common.rest.response.front.apply.ApplyWechatPreResponse;
import com.wxmblog.yanjian.common.rest.response.front.chat.ApplyChatPreResponse;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiOperationSort;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.wxmblog.yanjian.service.UserChatService;


/**
 * 用户聊天解锁
 *
 * @author crget
 * @email crget@crget.com
 * @date 2025-09-01 17:38:50
 */
@RestController
@RequestMapping("yanjian/userchat")
@Api(tags = "用户聊天")
public class UserChatController {

    @Autowired
    private UserChatService userChatService;


    @ApiOperation("申请私信预览")
    @ApiOperationSort(value = 1)
    @GetMapping("/pre")
    public R<ApplyChatPreResponse> chatPre(@RequestParam String userId) {
        ServiceR<ApplyChatPreResponse> ret = userChatService.chatPre(userId);
        if (ServiceR.isError(ret)) {
            return R.fail(ret.getMsg());
        }
        return R.ok(ret.getData());
    }

    @ApiOperation("解锁用户私信")
    @ApiOperationSort(value = 2)
    @PostMapping("/applyChat")
    public R<Void> applyChat(@RequestBody ApplyWxRequest request) {
        ServiceR<Void> serviceR = userChatService.applyChat(request);
        if (ServiceR.isError(serviceR)) {
            return R.fail(serviceR.getException());
        }
        return R.ok();
    }
}
