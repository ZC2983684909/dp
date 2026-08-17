package com.wxmblog.yanjian.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import com.wxmblog.yanjian.common.rest.vo.PhotoResultVo;
import com.wxmblog.yanjian.entity.TUserEntity;
import com.wxmblog.yanjian.entity.UserProfileEntity;
import com.wxmblog.yanjian.service.TUserService;
import com.wxmblog.yanjian.service.UserProfileService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wxmblog.yanjian.dao.TUserTempDao;
import com.wxmblog.yanjian.entity.TUserTempEntity;
import com.wxmblog.yanjian.service.TUserTempService;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;


@Service("tUserTempService")
public class TUserTempServiceImpl extends ServiceImpl<TUserTempDao, TUserTempEntity> implements TUserTempService {

    @Autowired
    TUserService tUserService;

    @Autowired
    UserProfileService userProfileService;

    @Transactional
    @Override
    public void saveUserTemp() {

        List<TUserTempEntity> list = this.list();
        for (TUserTempEntity tUserTempEntity : list) {

            TUserEntity tUserEntity = new TUserEntity();
            BeanUtils.copyProperties(tUserTempEntity, tUserEntity);
            UserProfileEntity userProfileEntity = new UserProfileEntity();
            BeanUtils.copyProperties(tUserTempEntity, userProfileEntity);
            if (CollectionUtil.isNotEmpty(tUserTempEntity.getPersonalPhoto())) {
                List<PhotoResultVo> personalPhoto = new ArrayList<>();
                for (String url : tUserTempEntity.getPersonalPhoto()) {
                    PhotoResultVo photoResultVo = new PhotoResultVo();
                    photoResultVo.setUrl(url);
                    photoResultVo.setSimilarity(0f);
                    photoResultVo.setIsMatch(false);
                    personalPhoto.add(photoResultVo);
                }
                tUserEntity.setPersonalPhoto(personalPhoto);
            }
            userProfileService.save(userProfileEntity);
            tUserEntity.setProfileId(userProfileEntity.getId());
            tUserService.save(tUserEntity);

        }
    }
}
