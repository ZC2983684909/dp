package com.wxmblog.yanjian.controller;

//import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.wxmblog.yanjian.service.PopupService;


/**
 * 首页弹窗
 *
 * @author crget
 * @email crget@crget.com
 * @date 2025-09-12 14:37:29
 */
@RestController
@RequestMapping("yanjian/popup")
public class PopupController {
    @Autowired
    private PopupService popupService;

}
