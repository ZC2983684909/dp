package com.wxmblog.yanjian.controller;

//import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.wxmblog.yanjian.service.ArticleSubjectService;


/**
 * 话题
 *
 * @author crget
 * @email crget@crget.com
 * @date 2025-05-13 17:54:41
 */
@RestController
@RequestMapping("yanjian/articlesubject")
public class ArticleSubjectController {
    @Autowired
    private ArticleSubjectService articleSubjectService;

}
