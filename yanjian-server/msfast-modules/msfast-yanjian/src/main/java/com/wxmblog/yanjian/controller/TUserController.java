package com.wxmblog.yanjian.controller;

//import org.apache.shiro.authz.annotation.RequiresPermissions;

import com.wxmblog.base.common.annotation.AuthIgnore;
import com.wxmblog.base.common.constant.ParamTypeConstants;
import com.wxmblog.base.common.utils.PageResult;
import com.wxmblog.base.common.web.domain.R;
import com.wxmblog.base.common.web.domain.ServiceR;
import com.wxmblog.yanjian.common.rest.request.front.user.*;
import com.wxmblog.yanjian.common.rest.response.front.user.*;
import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.wxmblog.yanjian.service.TUserService;


/**
 * 备注
 *
 * @author crget
 * @email crget@crget.com
 * @date 2024-11-13 23:37:01
 */
@RestController
@RequestMapping("yanjian/tuser")
@Api(tags = "前台用户")
public class TUserController {

    @Autowired
    private TUserService tUserService;

    /**
     * 获取用户个人中心
     *
     * @return
     */
    @ApiOperation("我的-个人中心")
    @ApiOperationSort(value = 1)
    @GetMapping("/personalCenter")
    public R<PersonalCenterResponse> personalCenter() {
        return R.ok(tUserService.getPersonalCenter());
    }


    @ApiOperation("我的-个人中心-查看是否关注公众号")
    @ApiOperationSort(value = 1)
    @GetMapping("/useSubscribe")
    @AuthIgnore
    public R<UseSubscribeResponse> useSubscribe() {
        return R.ok(tUserService.getuseSubscribe());
    }

    @ApiOperation("编辑资料-个人信息详情")
    @ApiOperationSort(value = 2)
    @GetMapping("/info")
    public R<UserInfoResponse> info() {
        return R.ok(tUserService.info());
    }

    @ApiOperation("编辑资料-修改用户信息")
    @ApiOperationSort(value = 3)
    @PostMapping("/editUserInfo")
    public R<Void> editUserInfo(@RequestBody UserInfoEditRequest request) {
        tUserService.editUserInfo(request);
        return R.ok();
    }

    //学历审核


    @ApiOperation("学历认证申请查看")
    @ApiOperationSort(value = 5)
    @GetMapping("/eduInfo")
    public R<EduApplyResponse> eduApply() {
        return R.ok(tUserService.getEduApply());
    }

    //

    @ApiOperation("身份证认证申请查看")
    @ApiOperationSort(value = 7)
    @GetMapping("/idInfo")
    public R<IdApplyResponse> idInfo() {
        return R.ok(tUserService.getIdInfo());
    }

    @ApiOperation("获取用户认证状态查询")
    @ApiOperationSort(value = 8)
    @GetMapping("/getAuthStatus")
    public R<AuthStatusResponse> getAuthStatus() {
        return R.ok(tUserService.getAuthStatus());
    }

    @ApiImplicitParams({@ApiImplicitParam(paramType = ParamTypeConstants.requestParam, name = "pageIndex", value = "页码", defaultValue = "1"), @ApiImplicitParam(paramType = ParamTypeConstants.requestParam, name = "pageSize", value = "数量", defaultValue = "10")})
    @ApiOperation("首页最新加入")
    @ApiOperationSort(value = 9)
    @GetMapping("/page")
    @AuthIgnore
    public R<PageResult<UserInfoPageResponse>> page(UserInfoPageRequest request, @RequestParam(value = "pageIndex", required = false, defaultValue = "1") Integer pageIndex, @RequestParam(value = "pageSize", required = false, defaultValue = "10") Integer pageSize) {
        return R.ok(tUserService.getPage(request, pageIndex, pageSize));
    }


    @ApiOperation("前台用户详情")
    @ApiOperationSort(value = 10)
    @GetMapping("/detail/{id}")
    @AuthIgnore
    public R<UserDetailResponse> detail(@PathVariable String id) {
        return R.ok(tUserService.getUserDetail(id));
    }

    //收藏用户
    @ApiOperation("收藏用户")
    @ApiOperationSort(value = 11)
    @GetMapping("/star/{id}")
    public R<Boolean> star(@PathVariable String id) {
        return R.ok(tUserService.star(id));
    }

    //取消收藏
    @ApiOperation("取消收藏")
    @ApiOperationSort(value = 12)
    @GetMapping("/cancelStar/{id}")
    public R<Void> cancelStar(@PathVariable String id) {
        tUserService.cancelStar(id);
        return R.ok();
    }


    @ApiOperation("隐身状态")
    @ApiOperationSort(value = 6)
    @PostMapping("/invisible")
    public R<Void> invisible(@RequestBody InvisibleRequest request) {
        tUserService.invisible(request);
        return R.ok();
    }

    /*@ApiOperation("我的-个人中心-资料编辑-编辑相册")
    @ApiOperationSort(value = 6)
    @PutMapping("/photoEdit")
    public R<Void> photoEdit(@RequestBody PhotoEditRequest request) {
        tUserService.photoEdit(request);
        return R.ok();
    }*/

    @ApiOperation("解锁用户微信")
    @ApiOperationSort(value = 16)
    @PostMapping("/applyWX")
    public R<Void> applyWx(@RequestBody ApplyWxRequest request) {
        ServiceR<Void> serviceR = tUserService.applyWx(request);
        if (ServiceR.isError(serviceR)) {
            return R.fail(serviceR.getException());
        }
        return R.ok();
    }

    //删除用户
    @ApiOperation("删除用户")
    @ApiOperationSort(value = 17)
    @DeleteMapping("/remove")
    public R<Void> remove() {
        tUserService.removeUser();
        return R.ok();
    }

    /*@ApiOperation("消息阅读状态")
    @ApiOperationSort(value = 18)
    @GetMapping("/getReadStatus")
    public R<MessageReadResponse> getReadStatus() {
        return R.ok(tUserService.getReadStatus());
    }*/

    @ApiOperation("用户身份证认证检测")
    @ApiOperationSort(value = 16)
    @GetMapping("/idAuthCheck")
    @AuthIgnore
    public R<EidAuthResultResponse> idAuthCheck(@RequestParam String eidToken) {
        return R.ok(tUserService.idAuthCheck(eidToken));
    }

    @ApiOperation("获取用户手机号")
    @ApiOperationSort(value = 18)
    @GetMapping("/getWechatPhone")
    @AuthIgnore
    public R<String> getWechatPhone(@RequestParam String code) {
        return R.ok(tUserService.getWechatPhone(code));
    }


    @ApiOperation("设置用户伪造微信号")
    @ApiOperationSort(value = 19)
    @GetMapping("/setWechat")
    @AuthIgnore
    public R<String> setWechat() {
        return R.ok(tUserService.setWechat());
    }

    //相册认证申请
    @ApiOperation("相册认证申请")
    @ApiOperationSort(value = 20)
    @PostMapping("/photoAuth")
    public R<Void> photoAuth() {
        tUserService.photoAuth();
        return R.ok();
    }

    @ApiOperation("获取用户无效信息状态")
    @ApiOperationSort(value = 21)
    @GetMapping("/hasInvalidInfo")
    public R<Boolean> getInvalidInfoStatus() {
        return R.ok(tUserService.getUserInvalidInfoStatus());
    }

    /*@ApiOperation("用户个人中心消息")
    @ApiOperationSort(value = 22)
    @GetMapping("/getCenterMessageResponse")
    public R<CenterMessageResponse> getCenterMessageResponse() {
        return R.ok(tUserService.getCenterMessageResponse());
    }*/

    @ApiOperation("分享奖励结果")
    @ApiOperationSort(value = 23)
    @GetMapping("/getShare")
    public R<ShareResponse> getShare() {
        return R.ok(tUserService.getShare());
    }

    @ApiImplicitParams({@ApiImplicitParam(paramType = ParamTypeConstants.requestParam, name = "pageIndex", value = "页码", defaultValue = "1"), @ApiImplicitParam(paramType = ParamTypeConstants.requestParam, name = "pageSize", value = "数量", defaultValue = "10")})
    @ApiOperation("我的邀请列表")
    @ApiOperationSort(value = 24)
    @GetMapping("/share/page")
    public R<PageResult<SharePageResponse>> sharePage(@RequestParam(value = "pageIndex", required = false, defaultValue = "1") Integer pageIndex, @RequestParam(value = "pageSize", required = false, defaultValue = "10") Integer pageSize) {
        return R.ok(tUserService.sharePage(pageIndex, pageSize));
    }

    @ApiImplicitParams({@ApiImplicitParam(paramType = ParamTypeConstants.requestParam, name = "pageIndex", value = "页码", defaultValue = "1"), @ApiImplicitParam(paramType = ParamTypeConstants.requestParam, name = "pageSize", value = "数量", defaultValue = "10")})
    @ApiOperation("邀请人邀请列表")
    @ApiOperationSort(value = 25)
    @GetMapping("/sharePerson/page")
    public R<PageResult<SharePageResponse>> sharePersonPage(@RequestParam(value = "pageIndex", required = false, defaultValue = "1") Integer pageIndex, @RequestParam(value = "pageSize", required = false, defaultValue = "10") Integer pageSize) {
        return R.ok(tUserService.sharePersonPage(pageIndex, pageSize));
    }

    //用户是否注册
    @ApiOperation("用户是否注册")
    @ApiOperationSort(value = 26)
    @GetMapping("/isRegister")
    @AuthIgnore
    public R<UserBaseIndoResponse> isRegister(@RequestParam String code) {
        ServiceR<UserBaseIndoResponse> ret = tUserService.isRegister(code);
        if (ServiceR.isError(ret))
            return R.fail(ret.getMsg());
        return R.ok(ret.getData());
    }
}
