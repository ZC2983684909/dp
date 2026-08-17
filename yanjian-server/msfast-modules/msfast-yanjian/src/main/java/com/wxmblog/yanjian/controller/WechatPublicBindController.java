package com.wxmblog.yanjian.controller;

//import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.wxmblog.yanjian.service.WechatPublicBindService;


/**
 * 微信公众号绑定
 *
 * @author crget
 * @email crget@crget.com
 * @date 2025-09-09 17:27:47
 */
@RestController
@RequestMapping("yanjian/wechatpublicbind")
public class WechatPublicBindController {
    @Autowired
    private WechatPublicBindService wechatPublicBindService;

}
