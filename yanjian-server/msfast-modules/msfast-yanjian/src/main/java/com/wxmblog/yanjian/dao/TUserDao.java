package com.wxmblog.yanjian.dao;

import com.wxmblog.yanjian.common.rest.request.admin.statistic.UserRegisterStatisticRequest;
import com.wxmblog.yanjian.common.rest.request.admin.user.UserInfoRequest;
import com.wxmblog.yanjian.common.rest.request.admin.user.UserPageRequest;
import com.wxmblog.yanjian.common.rest.request.front.user.DistanceRequest;
import com.wxmblog.yanjian.common.rest.request.front.user.UserInfoPageRequest;
import com.wxmblog.yanjian.common.rest.response.admin.statistic.ProportionResponse;
import com.wxmblog.yanjian.common.rest.response.admin.user.*;
import com.wxmblog.yanjian.common.rest.response.front.user.PersonalCenterResponse;
import com.wxmblog.yanjian.common.rest.response.front.user.SharePageResponse;
import com.wxmblog.yanjian.common.rest.response.front.user.ShareResponse;
import com.wxmblog.yanjian.common.rest.response.front.user.UserInfoPageResponse;
import com.wxmblog.yanjian.entity.TUserEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

import java.math.BigDecimal;
import java.util.List;

/**
 * 备注
 *
 * @author crget
 * @email crget@crget.com
 * @date 2024-11-13 23:37:01
 */
@Mapper
public interface TUserDao extends BaseMapper<TUserEntity> {

    List<UserInfoPageResponse> getPage(UserInfoPageRequest request);

    List<UserInfoPageResponse> browsePage(UserInfoPageRequest request);

    Long browsePageCount(String ownerId);

    List<UserInfoPageResponse> visitPage(UserInfoPageRequest request);

    Long visitPageCount(String ownerId);

    List<UserInfoPageResponse> applyPage(String ownerId);

    Long applyPageCount(String ownerId);

    List<UserPageResponse> getExaminePage(UserPageRequest request);

    List<UserIdentityPageResponse> getIdentityPage(UserPageRequest request);

    List<UserEducationPageResponse> getUserEducationPage(UserPageRequest request);

    List<UserInfoAdminPageResponse> getUserInfoPage(UserInfoRequest request);

    List<ProportionResponse> getGenderPie();

    List<ProportionResponse> getCityBar();

    List<ProportionResponse> getUserRegisterStatistic(UserRegisterStatisticRequest request);

    List<String> getGiftUserList();

    List<String> getApplyReminderList();

    List<String> getVisitReminderList();

    List<ComplaintPageResponse> complaintPage(UserPageRequest request);

    long visitUnreadCount(String ownerId);

    List<SharePageResponse> sharePage(String ownerId);

    List<SharePageResponse> sharePersonPage(String ownerId);

    PersonalCenterResponse getUserNumber(String ownerId);

    BigDecimal getDistance(DistanceRequest distanceRequest);

    ShareResponse getShareCount(String ownerId);
}
