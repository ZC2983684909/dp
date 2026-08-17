package com.wxmblog.yanjian.controller;

//import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.wxmblog.yanjian.service.SubjectStarService;


/**
 * 话题收藏
 *
 * @author crget
 * @email crget@crget.com
 * @date 2025-05-15 22:18:54
 */
@RestController
@RequestMapping("yanjian/subjectstar")
public class SubjectStarController {
    @Autowired
    private SubjectStarService subjectStarService;

}
