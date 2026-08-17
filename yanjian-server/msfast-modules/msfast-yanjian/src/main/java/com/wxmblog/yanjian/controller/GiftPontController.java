package com.wxmblog.yanjian.controller;

//import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.wxmblog.yanjian.service.GiftPontService;


/**
 * 备注
 *
 * @author crget
 * @email crget@crget.com
 * @date 2025-01-06 23:05:29
 */
@RestController
@RequestMapping("yanjian/giftpont")
public class GiftPontController {
    @Autowired
    private GiftPontService giftPontService;

}
