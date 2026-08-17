package com.wxmblog.yanjian.controller;

//import org.apache.shiro.authz.annotation.RequiresPermissions;

import com.wxmblog.base.common.annotation.AuthIgnore;
import com.wxmblog.base.common.constant.ParamTypeConstants;
import com.wxmblog.base.common.utils.PageResult;
import com.wxmblog.base.common.web.domain.R;
import com.wxmblog.yanjian.common.rest.request.front.article.MessagePageRequest;
import com.wxmblog.yanjian.common.rest.response.front.article.MessagePageResponse;
import com.wxmblog.yanjian.common.rest.response.front.article.UserMessageReadResponse;
import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.wxmblog.yanjian.service.MessageService;


/**
 * 消息 1-回复评论 2-评论动态 3-点赞动态 4-点赞评论
 *
 * @author crget
 * @email crget@crget.com
 * @date 2025-06-14 16:20:16
 */
@Api(tags = "消息列表")
@RestController
@RequestMapping("yanjian/message")
public class MessageController {
    @Autowired
    private MessageService messageService;

    @ApiOperation("未读消息数")
    @ApiOperationSort(value = 2)
    @GetMapping("/unreadCount")
    @AuthIgnore
    public R<UserMessageReadResponse> unreadCount() {
        return R.ok(messageService.unreadCount());
    }

}
