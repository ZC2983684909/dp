package com.wxmblog.yanjian.service.impl;

import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wxmblog.yanjian.dao.SubjectStarDao;
import com.wxmblog.yanjian.entity.SubjectStarEntity;
import com.wxmblog.yanjian.service.SubjectStarService;


@Service("subjectStarService")
public class SubjectStarServiceImpl extends ServiceImpl<SubjectStarDao, SubjectStarEntity> implements SubjectStarService {

}
