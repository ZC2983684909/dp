package com.wxmblog.yanjian.controller;

//import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.wxmblog.yanjian.service.WithdrawalRecordService;


/**
 * 提现记录
 *
 * @author crget
 * @email crget@crget.com
 * @date 2025-06-23 15:22:35
 */
@RestController
@RequestMapping("yanjian/withdrawalrecord")
public class WithdrawalRecordController {
    @Autowired
    private WithdrawalRecordService withdrawalRecordService;

}
