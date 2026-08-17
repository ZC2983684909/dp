package com.wxmblog.yanjian.controller;

//import org.apache.shiro.authz.annotation.RequiresPermissions;

import com.wxmblog.base.common.annotation.AuthIgnore;
import com.wxmblog.base.common.web.domain.R;
import com.wxmblog.base.common.web.domain.ServiceR;
import com.wxmblog.yanjian.common.rest.request.front.area.UnlimitedQRequest;
import com.wxmblog.yanjian.common.rest.response.front.home.UnlimitedQResponse;
import com.wxmblog.yanjian.common.rest.response.front.home.WechatSceneResponse;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.wxmblog.yanjian.service.WechatSceneService;


/**
 * 微信小程序码
 *
 * @author crget
 * @email crget@crget.com
 * @date 2025-08-25 10:35:40
 */
@RestController
@RequestMapping("yanjian/wechatscene")
@Api(tags = "微信小程序码")
public class WechatSceneController {

    @Autowired
    private WechatSceneService wechatSceneService;

    @ApiOperation("生成小程序码")
    @PostMapping("/wx/code")
    @AuthIgnore
    public R<UnlimitedQResponse> unlimitedQrCode(@RequestBody UnlimitedQRequest request) {

        ServiceR<UnlimitedQResponse> serviceR = wechatSceneService.unlimitedQrCode(request);
        if (ServiceR.isError(serviceR)) {
            return R.fail(serviceR.getMsg());
        }
        return R.ok(serviceR.getData());
    }

    @ApiOperation("查询参数")
    @GetMapping("/getScene/{id}")
    @AuthIgnore
    public R<WechatSceneResponse> getScene(@PathVariable String id) {

        ServiceR<WechatSceneResponse> serviceR = wechatSceneService.getScene(id);
        if (ServiceR.isError(serviceR))
            return R.fail(serviceR.getMsg());
        return R.ok(serviceR.getData());
    }

}
