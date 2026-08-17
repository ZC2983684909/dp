package com.wxmblog.yanjian.controller;

//import org.apache.shiro.authz.annotation.RequiresPermissions;

import com.wxmblog.base.common.annotation.AuthIgnore;
import com.wxmblog.base.common.web.domain.R;
import com.wxmblog.base.common.web.domain.ServiceR;
import com.wxmblog.yanjian.common.rest.response.front.home.PopUpResponse;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiOperationSort;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.wxmblog.yanjian.service.PopupRecordsService;

import java.util.List;


/**
 * 系统配置
 *
 * @author crget
 * @email crget@crget.com
 * @date 2025-07-03 15:53:38
 */
@RestController
@RequestMapping("yanjian/popuprecords")
@Api(tags = "弹窗")
public class PopupRecordsController {

    @Autowired
    private PopupRecordsService popupRecordsService;


    @ApiOperation("查询弹窗")
    @ApiOperationSort(value = 1)
    @GetMapping("/popup")
    @AuthIgnore
    public R<List<PopUpResponse>> popup(@RequestParam String location) {
        ServiceR<List<PopUpResponse>> serviceR = popupRecordsService.popup(location);
        if (ServiceR.isError(serviceR)) {
            return R.fail(serviceR.getMsg());
        }
        return R.ok(serviceR.getData());
    }

}
