package com.wxmblog.yanjian.controller;

//import org.apache.shiro.authz.annotation.RequiresPermissions;

import com.wxmblog.base.common.annotation.AuthIgnore;
import com.wxmblog.base.common.constant.ParamTypeConstants;
import com.wxmblog.base.common.utils.PageResult;
import com.wxmblog.base.common.web.domain.R;
import com.wxmblog.yanjian.common.rest.request.front.article.*;
import com.wxmblog.yanjian.common.rest.response.front.article.*;
import com.wxmblog.yanjian.service.ArticleService;
import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;


/**
 * 用户动态
 *
 * @author wanglei
 * @email 378526425@qq.com
 * @date 2024-01-31 15:23:10
 */
@RestController
@RequestMapping("yanjian/article")
@Api(tags = "前台-朋友圈")
public class ArticleController {
    @Autowired
    private ArticleService articleService;

    @ApiOperation("发布朋友圈")
    @ApiOperationSort(value = 1)
    @PostMapping("/add")
    public R<Void> addArticle(@RequestBody ArticleAddRequest request) {
        articleService.addArticle(request);
        return R.ok();
    }


    @ApiOperation("点赞")
    @ApiOperationSort(value = 8)
    @PutMapping("/praise")
    public R<Long> praise(@RequestBody PraiseRequest request) {
        return R.ok(articleService.praise(request));
    }

    @ApiOperation("取消点赞")
    @ApiOperationSort(value = 9)
    @PutMapping("/cancel/praise")
    public R<Long> cancelPraise(@RequestBody PraiseRequest request) {

        return R.ok(articleService.cancelPraise(request));
    }


    @ApiOperation("删除动态")
    @ApiOperationSort(value = 12)
    @DeleteMapping("/article/{id}")
    public R<Void> deleteArticle(@PathVariable String id) {
        articleService.deleteArticle(id);
        return R.ok();
    }

    @ApiOperation("修改动态公开")
    @ApiOperationSort(value = 13)
    @PutMapping("/open")
    public R<Void> openStatus(@RequestBody OpenStatusRequest request) {
        articleService.openStatus(request);
        return R.ok();
    }

    @ApiImplicitParams({@ApiImplicitParam(paramType = ParamTypeConstants.requestParam, name = "pageIndex", value = "页码", defaultValue = "1"), @ApiImplicitParam(paramType = ParamTypeConstants.requestParam, name = "pageSize", value = "数量", defaultValue = "10")})
    @ApiOperation("社区朋友圈列表")
    @ApiOperationSort(value = 2)
    @GetMapping("/page")
    @AuthIgnore
    public R<PageResult<ArticlePageResponse>> articlePage(ArticlePageRequest request, @RequestParam(value = "pageIndex", required = false, defaultValue = "1") Integer pageIndex, @RequestParam(value = "pageSize", required = false, defaultValue = "10") Integer pageSize) {
        return R.ok(articleService.articlePage(request, pageIndex, pageSize));
    }

    @ApiOperation("动态评论")
    @ApiOperationSort(value = 4)
    @PostMapping("/comment/add")
    public R<Void> addComment(@RequestBody CommentAddRequest request) {
        articleService.addComment(request);
        return R.ok();
    }


    @ApiOperation("删除评论")
    @ApiOperationSort(value = 13)
    @DeleteMapping("/comment/{id}")
    public R<Void> deleteComment(@PathVariable String id) {
        articleService.deleteComment(id);
        return R.ok();
    }

    @ApiImplicitParams({@ApiImplicitParam(paramType = ParamTypeConstants.requestParam, name = "pageIndex", value = "页码", defaultValue = "1"), @ApiImplicitParam(paramType = ParamTypeConstants.requestParam, name = "pageSize", value = "数量", defaultValue = "10")})
    @ApiOperation("动态话题列表")
    @ApiOperationSort(value = 14)
    @GetMapping("/subject/page")
    @AuthIgnore
    public R<PageResult<SubjectPageResponse>> subjectPage(@RequestParam(value = "pageIndex", required = false, defaultValue = "1") Integer pageIndex,
                                                          @RequestParam(value = "pageSize", required = false, defaultValue = "10") Integer pageSize) {
        return R.ok(articleService.subjectPage(pageIndex, pageSize));
    }

    @ApiOperation("首页话题列表")
    @ApiOperationSort(value = 15)
    @GetMapping("/homeSubject")
    @AuthIgnore
    public R<List<SubjectPageResponse>> homeSubject() {
        return R.ok(articleService.homeSubject());
    }

    @ApiOperation("话题详情")
    @ApiOperationSort(value = 16)
    @GetMapping("/subject/info/{id}")
    @AuthIgnore
    public R<SubjectDetailResponse> subjectDetail(@PathVariable String id) {
        return R.ok(articleService.subjectDetail(id));
    }


    @ApiOperation("话题关注")
    @ApiOperationSort(value = 17)
    @PutMapping("/subject/star")
    public R<Long> subjectStar(@RequestBody SubjectStarRequest request) {

        articleService.subjectStar(request);
        return R.ok();
    }

    @ApiOperation("取消话题关注")
    @ApiOperationSort(value = 18)
    @PutMapping("/cancel/subject/star")
    public R<Long> cancelsubjectStar(@RequestBody SubjectStarRequest request) {

        articleService.cancelsubjectStar(request);
        return R.ok();
    }

    @ApiOperation("清除文章权重缓存")
    @ApiOperationSort(value = 19)
    @DeleteMapping("/clear/weight/cache")
    public R<Void> clearArticleWeightCache() {
        articleService.clearArticleWeightCache();
        return R.ok();
    }

}
