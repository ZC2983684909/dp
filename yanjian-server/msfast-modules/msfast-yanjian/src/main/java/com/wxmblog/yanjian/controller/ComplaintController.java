package com.wxmblog.yanjian.controller;

//import org.apache.shiro.authz.annotation.RequiresPermissions;
import com.wxmblog.base.common.web.domain.R;
import com.wxmblog.yanjian.common.rest.request.front.user.ComplaintRequest;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiOperationSort;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.wxmblog.yanjian.service.ComplaintService;


/**
 * 投诉
 *
 * @author crget
 * @email crget@crget.com
 * @date 2024-12-09 17:22:36
 */
@RestController
@RequestMapping("yanjian/complaint")
@Api(tags = "投诉")
public class ComplaintController {
    @Autowired
    private ComplaintService complaintService;


    @ApiOperation("用户投诉")
    @ApiOperationSort(value = 1)
    @PostMapping("/add")
    public R<Void> add(@RequestBody ComplaintRequest request) {

        complaintService.add(request);
        return R.ok();
    }
}
