package com.wxmblog.yanjian.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.wxmblog.base.common.utils.PageResult;
import com.wxmblog.base.common.web.domain.ServiceR;
import com.wxmblog.yanjian.common.rest.request.admin.statistic.UserRegisterStatisticRequest;
import com.wxmblog.yanjian.common.rest.request.admin.user.*;
import com.wxmblog.yanjian.common.rest.request.front.auth.IdAuthRequest;
import com.wxmblog.yanjian.common.rest.request.front.user.*;
import com.wxmblog.yanjian.common.rest.response.admin.statistic.OutlineResponse;
import com.wxmblog.yanjian.common.rest.response.admin.statistic.ProportionResponse;
import com.wxmblog.yanjian.common.rest.response.admin.user.*;
import com.wxmblog.yanjian.common.rest.response.front.user.*;
import com.wxmblog.yanjian.common.rest.response.front.user.UserInfoPageResponse;
import com.wxmblog.yanjian.entity.TUserEntity;
import org.springframework.scheduling.annotation.Async;

import java.util.List;


/**
 * 备注
 *
 * @author crget
 * @email crget@crget.com
 * @date 2024-11-13 23:37:01
 */
public interface TUserService extends IService<TUserEntity> {

    TUserEntity getFrUserByOpenId(String openId);

    TUserEntity getFrUserByUnionId(String unionId);

    TUserEntity getFrUserByIdCard(String idCardNumber);

    PersonalCenterResponse getPersonalCenter();

    UserInfoResponse info();

    void editUserInfo(UserInfoEditRequest request);


    EduApplyResponse getEduApply();


    IdApplyResponse getIdInfo();

    AuthStatusResponse getAuthStatus();

    PageResult<UserInfoPageResponse> getPage(UserInfoPageRequest request, Integer pageIndex, Integer pageSize);

    UserDetailResponse getUserDetail(String id);

    Boolean star(String id);

    void cancelStar(String id);

    @Async
    void updateLatelyTime(String userId);


    void invisible(InvisibleRequest request);

    void photoEdit(PhotoEditRequest request);

    ServiceR<Void> applyWx(ApplyWxRequest request);

    void removeUser();

    MessageReadResponse getReadStatus();

    String idAuth(IdAuthRequest request);

    PageResult<UserPageResponse> examinePage(UserPageRequest request, Integer pageIndex, Integer pageSize);

    void examine(UserExamineRequest request);

    UserExamineInfoResponse getExamineInfo(String id);

    PageResult<UserIdentityPageResponse> identityPage(UserPageRequest request, Integer pageIndex, Integer pageSize);

    IdentityExamineInfoResponse identityExamine(Integer id);

    void identityExamine(UserExamineRequest request);

    PageResult<UserEducationPageResponse> userEducationPage(UserPageRequest request, Integer pageIndex, Integer pageSize);

    EducationExamineInfoResponse educationExamine(String id);

    void educationExamine(UserExamineRequest request);

    PageResult<UserInfoAdminPageResponse> userPage(UserInfoRequest request, Integer pageIndex, Integer pageSize);

    UserAdminInfoResponse userAdminInfo(String id);

    void updateUser(UserAdminInfoAddRequest request);

    void deleteUser(String id);

    OutlineResponse outline();

    List<ProportionResponse> userRegister(UserRegisterStatisticRequest request);

    void executeGiftPoint();

    EidAuthResultResponse idAuthCheck(String eidToken);

    void checkUser(UserAdminCheckRequest request);

    PageResult<ComplaintPageResponse> complaintPage(UserPageRequest request, Integer pageIndex, Integer pageSize);

    UserComplaintResponse complaintInfo(String id);

    void postComplaint(UserAdminCheckRequest request);

    String getWechatPhone(String code);

    String setWechat();

    void photoAuth();

    Boolean getUserInvalidInfoStatus();

    CenterMessageResponse getCenterMessageResponse();

    ShareResponse getShare();

    PageResult<SharePageResponse> sharePage(Integer pageIndex, Integer pageSize);

    PageResult<SharePageResponse> sharePersonPage(Integer pageIndex, Integer pageSize);

    void pay(UserPayRequest request);

    @Async
    void updateLocation(LocationRequest request);

    ServiceR<String> compareFace(String a, String b);

    ServiceR<UserBaseIndoResponse> isRegister(String code);

    UseSubscribeResponse getuseSubscribe();

    void executeReminderApply();

    void executeReminderVisit();
}

