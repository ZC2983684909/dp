package com.wxmblog.yanjian.controller;

//import org.apache.shiro.authz.annotation.RequiresPermissions;

import com.wxmblog.base.common.constant.ParamTypeConstants;
import com.wxmblog.base.common.utils.PageResult;
import com.wxmblog.base.common.web.domain.R;
import com.wxmblog.base.common.web.domain.ServiceR;
import com.wxmblog.yanjian.common.rest.request.front.star.UserStarRequest;
import com.wxmblog.yanjian.common.rest.request.front.user.UserApplyAuditRequest;
import com.wxmblog.yanjian.common.rest.response.front.apply.ApplyWechatPreResponse;
import com.wxmblog.yanjian.common.rest.response.front.user.UserApplyPageResponse;
import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.wxmblog.yanjian.service.UserApplyService;


/**
 * 备注
 *
 * @author crget
 * @email crget@crget.com
 * @date 2024-11-17 16:39:00
 */
@RestController
@RequestMapping("yanjian/userapply")
@Api(tags = "好友申请")
public class UserApplyController {

    @Autowired
    private UserApplyService userApplyService;


    //申请审核 UserApplyAuditRequest
    @ApiOperation("申请审核")
    @ApiOperationSort(value = 3)
    @PostMapping("/audit")
    public R<Void> audit(@RequestBody UserApplyAuditRequest request) {
        userApplyService.audit(request);
        return R.ok();
    }

    @ApiOperation("申请我的已读")
    @ApiOperationSort(value = 3)
    @GetMapping("/read/{id}")
    public R<Void> applyRead(@PathVariable String id) {
        userApplyService.applyRead(id);
        return R.ok();
    }

   /* @ApiOperation("我的申请已读")
    @ApiOperationSort(value = 3)
    @GetMapping("/readwait/{id}")
    public R<Void> readwait(@PathVariable String id) {
        userApplyService.readwait(id);
        return R.ok();
    }*/

    //发送消息
    /*@ApiOperation("发送消息")
    @ApiOperationSort(value = 3)
    @PostMapping("/send")
    public R<Void> send(@RequestBody SendMessageRequest request) {
        userApplyService.send(request);
        return R.ok();
    }*/

    @ApiOperation("申请微信预览")
    @ApiOperationSort(value = 4)
    @GetMapping("/wechat/pre")
    public R<ApplyWechatPreResponse> wechatPre(@RequestParam String userId) {
        ServiceR<ApplyWechatPreResponse> ret = userApplyService.wechatPre(userId);
        if (ServiceR.isError(ret)) {
            return R.fail(ret.getMsg());
        }
        return R.ok(ret.getData());
    }
}
