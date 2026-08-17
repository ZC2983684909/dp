package com.wxmblog.yanjian.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.date.DateUtil;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.wxmblog.base.auth.service.MsfConfigService;
import com.wxmblog.base.common.enums.BaseExceptionEnum;
import com.wxmblog.base.common.enums.BaseUserExceptionEnum;
import com.wxmblog.base.common.exception.JrsfException;
import com.wxmblog.base.common.service.RedisService;
import com.wxmblog.base.common.utils.DateUtils;
import com.wxmblog.base.common.utils.PageResult;
import com.wxmblog.base.common.utils.TokenUtils;
import com.wxmblog.base.common.web.domain.ServiceR;
import com.wxmblog.base.file.constant.FileConstants;
import com.wxmblog.base.file.service.MsfFileService;
import com.wxmblog.base.file.utils.FileUtils;
import com.wxmblog.yanjian.common.constant.Constants;
import com.wxmblog.yanjian.common.enums.article.PraiseTypeEnum;
import com.wxmblog.yanjian.common.exception.UserExceptionEnum;
import com.wxmblog.yanjian.common.rest.request.front.article.*;
import com.wxmblog.yanjian.common.rest.response.front.article.*;
import com.wxmblog.yanjian.common.rest.vo.WxCheckResultVo;
import com.wxmblog.yanjian.dao.*;
import com.wxmblog.yanjian.entity.*;
import com.wxmblog.yanjian.service.*;
import org.apache.commons.lang.StringUtils;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;


@Service("articleService")
public class ArticleServiceImpl extends ServiceImpl<ArticleDao, ArticleEntity> implements ArticleService {


    @Autowired
    RedissonClient redissonClient;

    @Autowired
    TUserDao frUserDao;

    @Autowired
    MsfConfigService msfConfigService;

    @Autowired
    MsfFileService fileService;

    @Autowired
    WxCheckService wxCheckService;

    @Autowired
    UserToLikeDao userToLikeDao;

    @Autowired
    private ArticleCommentDao articleCommentDao;

    @Autowired
    private ArticleToSubjectService articleToSubjectService;

    @Autowired
    private ArticleSubjectService articleSubjectService;

    @Autowired
    private SubjectStarService subjectStarService;

    @Autowired
    private MessageService messageService;
    @Autowired
    private ArticleCommentServiceImpl articleCommentService;

    @Autowired
    private ArticleWeightService articleWeightService;

    @Autowired
    private RedisService redisTemplate;
    @Autowired
    private AsyncServiceImpl asyncService;

    @Transactional
    @Override
    public void addArticle(ArticleAddRequest request) {

        if (StringUtils.isBlank(request.getContent()) && CollectionUtil.isEmpty(request.getImg())) {
            throw new JrsfException(UserExceptionEnum.ARTICLE_IS_EMPTY);
        }

        if (CollectionUtil.isNotEmpty(request.getImg())) {
            long countPic = request.getImg().stream().filter(FileUtils::isPicture).count();
            long countVideo = request.getImg().stream().filter(p -> !FileUtils.isPicture(p)).count();
            if (countPic > 0 && countVideo > 0) {
                throw new JrsfException(UserExceptionEnum.IMG_VIDEO_NOT_EXIST);
            }
        }

        TUserEntity frUserEntity = frUserDao.selectById(TokenUtils.getOwnerId());
        if (frUserEntity == null) {
            throw new JrsfException(BaseUserExceptionEnum.USER_NOT_EXIST_EXCEPTION);
        }
        /*String applyCheck = msfConfigService.getValueByCode("applyCheck");
        if (StringUtils.isNotBlank(applyCheck)) {
            JSONObject jsonObject = JSONObject.parseObject(applyCheck);
            if (Boolean.TRUE.equals(jsonObject.getBoolean("idAuth")) && !"3".equals(frUserEntity.getIdAuth())) {
                throw new JrsfException(UserExceptionEnum.ID_AUTH_NOT_PASS_EXCEPTION);
            }
        }*/

        //检测内容合法性
        if (StringUtils.isNotBlank(request.getContent()) && StringUtils.isNotBlank(request.getCode())) {
            ServiceR<WxCheckResultVo> wxCheckResultVoServiceR = wxCheckService.msgSecCheck(request.getContent(), 3, request.getCode());
            if (ServiceR.isError(wxCheckResultVoServiceR)) {
                throw new JrsfException(BaseExceptionEnum.UNKNOWN_EXCEPTION).setMsg(wxCheckResultVoServiceR.getMsg());
            }
            WxCheckResultVo wxCheckResultVo = wxCheckResultVoServiceR.getData();
            if (Boolean.FALSE.equals(wxCheckResultVo.getResult())) {
                throw new JrsfException(BaseExceptionEnum.UNKNOWN_EXCEPTION).setMsg(wxCheckResultVo.getMsg());
            }
        }

        if (StringUtils.isNotBlank(request.getContent())) {
            String banWord = msfConfigService.getValueByCode("banWord");
            if (StringUtils.isNotBlank(banWord)) {
                String[] banWordList = banWord.split(",");
                for (String banWordModel : banWordList) {
                    if (request.getContent().contains(banWordModel)) {
                        //涉及平台未允许的内容：线下陪玩等服务
                        throw new JrsfException(UserExceptionEnum.CONTENT_CONTAIN_ILLEGAL_CHARACTER).setMsg("涉及微信平台未允许的内容:" + banWordModel + ",我们鼓励大家积极分享自己的生活等社交内容，请不要发布涉及线下陪玩等微信平台不允许或其他营销内容，谢谢！");
                    }
                }
            }
        }


        ArticleEntity article = new ArticleEntity();
        BeanUtils.copyProperties(request, article);
        article.setUserId(TokenUtils.getOwnerId());
        article.setOpen(1);
        article.setCity(frUserEntity.getCity());
        this.baseMapper.insert(article);

        redisTemplate.setCacheObject(Constants.ARTICLE_ID + article.getUserId(), article.getId(), 1L, TimeUnit.DAYS);

        //添加话题
        if (CollectionUtil.isNotEmpty(request.getSubjectIdList())) {
            request.getSubjectIdList().forEach(subjectId -> {
                ArticleToSubjectEntity articleToSubjectEntity = new ArticleToSubjectEntity();
                articleToSubjectEntity.setArticleId(article.getId());
                articleToSubjectEntity.setSubjectId(subjectId);
                articleToSubjectService.save(articleToSubjectEntity);
            });
            articleSubjectService.addDiscussById(request.getSubjectIdList());
        }

        //设置动态可见
        asyncService.setArticleRecommend(article);

        // 清除权重缓存，因为新增了文章
        // clearArticleWeightCache();
    }


    @Transactional
    @Override
    public Long praise(PraiseRequest request) {
        Wrapper<UserToLikeEntity> wrapper = new QueryWrapper<UserToLikeEntity>().lambda()
                .eq(UserToLikeEntity::getUserId, TokenUtils.getOwnerId())
                .eq(UserToLikeEntity::getTargetId, request.getId())
                .eq(UserToLikeEntity::getPraiseType, request.getPraiseType());
        Long praiseCount = this.userToLikeDao.selectCount(wrapper);
        if (praiseCount == 0) {
            UserToLikeEntity userToLikeEntity = new UserToLikeEntity();
            userToLikeEntity.setPraiseType(request.getPraiseType());
            userToLikeEntity.setTargetId(request.getId());
            userToLikeEntity.setUserId(TokenUtils.getOwnerId());
            userToLikeDao.insert(userToLikeEntity);
        }

        Wrapper<UserToLikeEntity> wrapperTotal = new QueryWrapper<UserToLikeEntity>().lambda().eq(UserToLikeEntity::getTargetId, request.getId()).eq(UserToLikeEntity::getPraiseType, request.getPraiseType());
        Long praiseTotal = this.userToLikeDao.selectCount(wrapperTotal);
        updatePraise(request, Integer.parseInt(praiseTotal.toString()));

        if (PraiseTypeEnum.ARTICLE.equals(request.getPraiseType())) {
            ArticleEntity articleEntity = this.baseMapper.selectById(request.getId());
            if (articleEntity != null) {
                AddMessageRequest messageRequest = new AddMessageRequest();
                messageRequest.setUserId(articleEntity.getUserId());
                messageRequest.setArticleId(request.getId());
                messageRequest.setType("3");
                messageRequest.setSendUserId(TokenUtils.getOwnerId());
                messageService.addMessage(messageRequest);
            }

        } else if (PraiseTypeEnum.COMMENT.equals(request.getPraiseType())) {
            ArticleCommentEntity commentEntity = articleCommentDao.selectById(request.getId());
            if (commentEntity != null) {
                AddMessageRequest messageRequest = new AddMessageRequest();
                messageRequest.setUserId(commentEntity.getUserId());
                messageRequest.setArticleId(commentEntity.getArticleId());
                messageRequest.setCommentId(request.getId());
                messageRequest.setType("4");
                messageRequest.setSendUserId(TokenUtils.getOwnerId());
                messageService.addMessage(messageRequest);
            }
        }

        return praiseTotal;
    }

    private void updatePraise(PraiseRequest request, Integer praiseTotal) {

        RLock lock = redissonClient.getLock(Constants.PRAISE + request.getPraiseType().name() + request.getId());
        try {
            lock.lock();
            if (PraiseTypeEnum.ARTICLE.equals(request.getPraiseType())) {

                ArticleEntity articleEntity = this.baseMapper.selectById(request.getId());
                if (articleEntity != null) {
                    articleEntity.setLikeCount(praiseTotal);
                }
                this.baseMapper.updateById(articleEntity);
            } else if (PraiseTypeEnum.COMMENT.equals(request.getPraiseType())) {
                ArticleCommentEntity commentEntity = articleCommentDao.selectById(request.getId());
                if (commentEntity != null) {
                    commentEntity.setLikeCount(praiseTotal);
                }
                articleCommentDao.updateById(commentEntity);
            }
        } finally {
            lock.unlock();
        }
    }

    @Transactional
    @Override
    public Long cancelPraise(PraiseRequest request) {
        Wrapper<UserToLikeEntity> wrapper = new QueryWrapper<UserToLikeEntity>().lambda()
                .eq(UserToLikeEntity::getUserId, TokenUtils.getOwnerId())
                .eq(UserToLikeEntity::getTargetId, request.getId())
                .eq(UserToLikeEntity::getPraiseType, request.getPraiseType());
        this.userToLikeDao.delete(wrapper);

        Wrapper<UserToLikeEntity> wrapperTotal = new QueryWrapper<UserToLikeEntity>().lambda().eq(UserToLikeEntity::getTargetId, request.getId()).eq(UserToLikeEntity::getPraiseType, request.getPraiseType());
        Long praiseTotal = this.userToLikeDao.selectCount(wrapperTotal);
        updatePraise(request, Integer.parseInt(praiseTotal.toString()));

        // 如果是文章取消点赞，清除权重缓存
        if (PraiseTypeEnum.ARTICLE.equals(request.getPraiseType())) {
            clearArticleWeightCache();
        }

        return praiseTotal;
    }

    @Transactional
    @Override
    public void deleteArticle(String id) {

        ArticleEntity articleEntity = this.getBaseMapper().selectById(id);
        if (articleEntity == null) {
            throw new JrsfException(BaseExceptionEnum.UNKNOWN_EXCEPTION).setMsg("动态不存在");
        }
        String adminPhoneList = msfConfigService.getValueByCode("adminPhoneList");

        if (!Objects.equals(TokenUtils.getOwnerId(), articleEntity.getUserId()) && (StringUtils.isNotBlank(adminPhoneList) && StringUtils.isNotBlank(TokenUtils.getOwnerId()) && !adminPhoneList.contains(TokenUtils.getOwnerId()))) {
            throw new JrsfException(BaseExceptionEnum.UNKNOWN_EXCEPTION).setMsg("没有权限");
        }
        this.getBaseMapper().deleteById(id);
    }

    @Transactional
    @Override
    public void openStatus(OpenStatusRequest request) {

        ArticleEntity articleEntity = this.getBaseMapper().selectById(request.getId());
        if (articleEntity == null) {
            throw new JrsfException(BaseExceptionEnum.UNKNOWN_EXCEPTION).setMsg("动态不存在");
        }
        String adminPhoneList = msfConfigService.getValueByCode("adminPhoneList");

        if (!Objects.equals(TokenUtils.getOwnerId(), articleEntity.getUserId()) && (StringUtils.isNotBlank(adminPhoneList) && StringUtils.isNotBlank(TokenUtils.getOwnerId()) && !adminPhoneList.contains(TokenUtils.getOwnerId()))) {
            throw new JrsfException(BaseExceptionEnum.UNKNOWN_EXCEPTION).setMsg("没有权限");
        }

        articleEntity.setOpen(request.getOpen());
        this.getBaseMapper().updateById(articleEntity);
    }

    @Override
    public PageResult<ArticlePageResponse> articlePage(ArticlePageRequest request, Integer pageIndex, Integer pageSize) {

        request.setOwnerId(TokenUtils.getOwnerId());

        if ("comp".equals(request.getSortType())) {
            request.setWeek(DateUtil.beginOfWeek(new Date()));
        }

        if (StringUtils.isNotBlank(request.getOwnerId()) && StringUtils.isNotBlank(request.getSex())) {
            request.setArticleId("0");
            String articleId = redisTemplate.getCacheObject(Constants.ARTICLE_ID + request.getOwnerId());
            if (StringUtils.isNotBlank(articleId)) {
                request.setArticleId(articleId);
            }
        }
        //
        /*if (StringUtils.isNotBlank(request.getOwnerId()) && StringUtils.isBlank(request.getSex())) {
            TUserEntity tUserEntity = frUserDao.selectById(request.getOwnerId());
            request.setSex("男".equals(tUserEntity.getSex()) ? "女" : "男");
        }*/

        Page<ArticlePageResponse> page = PageHelper.startPage(pageIndex, pageSize);
        this.getBaseMapper().getArticlePage(request);
        PageResult<ArticlePageResponse> result = new PageResult<>(page);

        List<String> articleIdList = result.getRows().stream().map(ArticlePageResponse::getId).collect(Collectors.toList());
        List<ArticleToSubjectEntity> articleToSubjectEntityList;
        List<ArticleSubjectEntity> articleSubjectEntityList;
        if (CollectionUtil.isNotEmpty(articleIdList)) {
            Wrapper<ArticleToSubjectEntity> wrapper = new QueryWrapper<ArticleToSubjectEntity>().lambda()
                    .in(ArticleToSubjectEntity::getArticleId, articleIdList);
            articleToSubjectEntityList = articleToSubjectService.list(wrapper);
            if (CollectionUtil.isNotEmpty(articleToSubjectEntityList)) {
                List<String> subjectIdList = articleToSubjectEntityList.stream().map(ArticleToSubjectEntity::getSubjectId).collect(Collectors.toList());
                if (CollectionUtil.isNotEmpty(subjectIdList)) {
                    Wrapper<ArticleSubjectEntity> wrapper1 = new QueryWrapper<ArticleSubjectEntity>().lambda()
                            .in(ArticleSubjectEntity::getId, subjectIdList);
                    articleSubjectEntityList = articleSubjectService.list(wrapper1);
                } else {
                    articleSubjectEntityList = Collections.emptyList();
                }

            } else {
                articleSubjectEntityList = Collections.emptyList();
            }
            articleSubjectService.addVisit(articleIdList);
        } else {
            articleSubjectEntityList = Collections.emptyList();
            articleToSubjectEntityList = Collections.emptyList();
        }

        String adminPhoneList = msfConfigService.getValueByCode("adminPhoneList");
        result.getRows().forEach(model -> {
            model.setTime(DateUtils.getChineseTime(model.getCreateTime()));
            model.setCreateTime(null);
            model.setIsDelete(model.getUserId().equals(request.getOwnerId()));
            List<String> infomationList = new ArrayList<>();
            if (model.getBirthDate() != null) {
                infomationList.add(DateUtils.getAgeByBirth(model.getBirthDate()) + "岁");
                infomationList.add(DateUtils.getConstellation(model.getBirthDate()));
            }
            if (StringUtils.isNotBlank(model.getJobMes())) {
                infomationList.add(model.getJobMes());
            }
            model.setInformation(String.join("·", infomationList));


            if (StringUtils.isNotBlank(adminPhoneList) && StringUtils.isNotBlank(request.getOwnerId()) && adminPhoneList.contains(request.getOwnerId())) {
                model.setIsDelete(true);
            }

            List<ArticleToSubjectEntity> articleToSubjectEntityList1 = articleToSubjectEntityList.stream().filter(item -> item.getArticleId().equals(model.getId())).collect(Collectors.toList());
            if (CollectionUtil.isNotEmpty(articleToSubjectEntityList1)) {
                List<SubjectPageResponse> subjectList = new ArrayList<>();
                model.setSubjectList(subjectList);
                articleToSubjectEntityList1.forEach(item -> {
                    ArticleSubjectEntity articleSubjectEntity = articleSubjectEntityList.stream()
                            .filter(item1 -> item1.getId().equals(item.getSubjectId())).findFirst().orElse(null);
                    if (articleSubjectEntity != null) {
                        SubjectPageResponse subjectPageResponse = new SubjectPageResponse();
                        BeanUtils.copyProperties(articleSubjectEntity, subjectPageResponse);
                        subjectList.add(subjectPageResponse);
                    }
                });
            }

            if (StringUtils.isNotBlank(model.getCity()) && model.getCity().length() > 9) {
                model.setCity(model.getCity().substring(0, 8) + ".");

            }
        });
        return result;
    }

    @Transactional
    @Override
    public void addComment(CommentAddRequest request) {

        TUserEntity frUserEntity = frUserDao.selectById(TokenUtils.getOwnerId());
        if (frUserEntity == null) {
            throw new JrsfException(BaseUserExceptionEnum.USER_NOT_EXIST_EXCEPTION);
        }
       /* String applyCheck = msfConfigService.getValueByCode("applyCheck");
        if (StringUtils.isNotBlank(applyCheck)) {
            JSONObject jsonObject = JSONObject.parseObject(applyCheck);
            if (Boolean.TRUE.equals(jsonObject.getBoolean("idAuth")) && !"3".equals(frUserEntity.getIdAuth())) {
                throw new JrsfException(UserExceptionEnum.ID_AUTH_NOT_PASS_EXCEPTION);
            }
            if (Boolean.TRUE.equals(jsonObject.getBoolean("eduAuth")) && !"3".equals(frUserEntity.getEduAuth())) {
                if (Boolean.TRUE.equals(jsonObject.getBoolean("photoAuth")) && !"3".equals(frUserEntity.getPhotoAuth())) {
                    throw new JrsfException(UserExceptionEnum.COMMENT_LEAST_TWO_AUTH);
                }
            }
        }*/

        ArticleEntity articleEntityComment = baseMapper.selectById(request.getArticleId());
        if (articleEntityComment == null) {
            throw new JrsfException(BaseExceptionEnum.UNKNOWN_EXCEPTION).setMsg("动态已删除！");
        }

        if (StringUtils.isNotBlank(request.getCommentId())) {
            ArticleCommentEntity commentEntity = articleCommentDao.selectById(request.getCommentId());
            if (commentEntity == null) {
                throw new JrsfException(BaseExceptionEnum.UNKNOWN_EXCEPTION).setMsg("评论已删除！");
            }

        }

        ArticleCommentEntity commentEntity = new ArticleCommentEntity();
        BeanUtils.copyProperties(request, commentEntity);
        commentEntity.setUserId(TokenUtils.getOwnerId());
        articleCommentDao.insert(commentEntity);
        addCommentCount(request.getArticleId());

        articleSubjectService.addDiscuss(ListUtil.of(request.getArticleId()));

      /*  // 清除权重缓存，因为新增了评论
        clearArticleWeightCache();*/

        //添加消息
        if (StringUtils.isNotBlank(request.getCommentId())) {
            //回复评论
            AddMessageRequest addMessageRequest = new AddMessageRequest();
            ArticleCommentEntity commentEntity1 = articleCommentDao.selectById(request.getCommentId());
            if (commentEntity1 != null) {
                addMessageRequest.setUserId(commentEntity1.getUserId());
                addMessageRequest.setArticleId(commentEntity1.getArticleId());
                addMessageRequest.setCommentId(commentEntity.getId());
                addMessageRequest.setType("1");
                addMessageRequest.setSendUserId(TokenUtils.getOwnerId());
                messageService.addMessage(addMessageRequest);
                ArticleEntity articleEntity = this.baseMapper.selectById(request.getArticleId());
                if (articleEntity != null) {
                    if (!articleEntity.getUserId().equals(commentEntity1.getUserId())) {
                        AddMessageRequest addMessageRequest1 = new AddMessageRequest();
                        addMessageRequest1.setUserId(articleEntity.getUserId());
                        addMessageRequest1.setArticleId(articleEntity.getId());
                        addMessageRequest1.setCommentId(commentEntity.getId());
                        addMessageRequest1.setType("2");
                        addMessageRequest.setSendUserId(TokenUtils.getOwnerId());
                        messageService.addMessage(addMessageRequest1);
                    }
                }

            }

        } else {
            //评论动态
            AddMessageRequest addMessageRequest = new AddMessageRequest();
            ArticleEntity articleEntity = this.baseMapper.selectById(request.getArticleId());
            if (articleEntity != null) {
                addMessageRequest.setUserId(articleEntity.getUserId());
                addMessageRequest.setArticleId(articleEntity.getId());
                addMessageRequest.setCommentId(commentEntity.getId());
                addMessageRequest.setType("2");
                addMessageRequest.setSendUserId(TokenUtils.getOwnerId());
                messageService.addMessage(addMessageRequest);
            }
        }
    }

    private void addCommentCount(String articleId) {
        RLock lock = redissonClient.getLock(Constants.ADD_COMMENT + articleId);
        try {
            lock.lock();
            ArticleEntity articleEntity = this.baseMapper.selectById(articleId);
            if (articleEntity != null) {
                articleEntity.setCommentCount(articleEntity.getCommentCount() + 1);
            }
            this.baseMapper.updateById(articleEntity);
        } finally {
            lock.unlock();
        }
    }

    @Transactional
    @Override
    public void deleteComment(String id) {

        ArticleCommentEntity articleCommentEntity = articleCommentDao.selectById(id);
        if (articleCommentEntity == null) {
            throw new JrsfException(BaseExceptionEnum.UNKNOWN_EXCEPTION).setMsg("评论不存在");
        }
        String adminPhoneList = msfConfigService.getValueByCode("adminPhoneList");

        ArticleEntity articleEntity = this.baseMapper.selectById(articleCommentEntity.getArticleId());
        if (!articleCommentEntity.getUserId().equals(TokenUtils.getOwnerId()) && !articleEntity.getUserId().equals(TokenUtils.getOwnerId()) && (StringUtils.isNotBlank(adminPhoneList) && StringUtils.isNotBlank(TokenUtils.getOwnerId()) && !adminPhoneList.contains(TokenUtils.getOwnerId()))) {
            throw new JrsfException(BaseExceptionEnum.UNKNOWN_EXCEPTION).setMsg("没有权限");
        }
        articleCommentDao.deleteById(id);
    }

    @Override
    public PageResult<SubjectPageResponse> subjectPage(Integer pageIndex, Integer pageSize) {

        Page<SubjectPageResponse> page = PageHelper.startPage(pageIndex, pageSize);
        this.getBaseMapper().subjectPage();
        PageResult<SubjectPageResponse> result = new PageResult<>(page);
        result.getRows().forEach(model -> {
            model.setVisitCountDesc(convertToW(model.getVisitCount()));
            model.setDiscussCountDesc(convertToW(model.getDiscussCount()));
        });
        return result;
    }

    @Override
    public List<SubjectPageResponse> homeSubject() {

        String homeSubject = msfConfigService.getValueByCode("homeSubject");
        if (StringUtils.isNotBlank(homeSubject)) {
            List<String> homeSubjectList = JSONObject.parseArray(homeSubject, String.class);
            Wrapper<ArticleSubjectEntity> wrapper = new QueryWrapper<ArticleSubjectEntity>().lambda()
                    .in(ArticleSubjectEntity::getId, homeSubjectList)
                    .orderByAsc(ArticleSubjectEntity::getSort);
            List<ArticleSubjectEntity> list = articleSubjectService.list(wrapper);
            return list.stream().map(entity -> {
                SubjectPageResponse response = new SubjectPageResponse();
                BeanUtils.copyProperties(entity, response);
                response.setVisitCountDesc(convertToW(response.getVisitCount()));
                response.setDiscussCountDesc(convertToW(response.getDiscussCount()));
                return response;
            }).collect(Collectors.toList());
        }
        return Collections.emptyList();
    }

    @Override
    public SubjectDetailResponse subjectDetail(String id) {

        SubjectDetailResponse response = new SubjectDetailResponse();
        ArticleSubjectEntity articleSubjectEntity = articleSubjectService.getBaseMapper().selectById(id);
        if (articleSubjectEntity != null) {
            BeanUtils.copyProperties(articleSubjectEntity, response);
            response.setVisitCountDesc(convertToW(response.getVisitCount()));
            response.setDiscussCountDesc(convertToW(response.getDiscussCount()));
        }
        Wrapper<SubjectStarEntity> wrapper1 = new QueryWrapper<SubjectStarEntity>().lambda()
                .eq(SubjectStarEntity::getMemberId, TokenUtils.getOwnerId())
                .eq(SubjectStarEntity::getSubjectId, id);
        response.setIsStar(subjectStarService.count(wrapper1) > 0);
        articleSubjectService.addVisitById(ListUtil.of(id));
        return response;
    }

    @Transactional
    @Override
    public void subjectStar(SubjectStarRequest request) {
        Wrapper<SubjectStarEntity> wrapper = new QueryWrapper<SubjectStarEntity>().lambda()
                .eq(SubjectStarEntity::getMemberId, TokenUtils.getOwnerId())
                .eq(SubjectStarEntity::getSubjectId, request.getId());
        if (subjectStarService.count(wrapper) == 0) {
            SubjectStarEntity starEntity = new SubjectStarEntity();
            starEntity.setMemberId(TokenUtils.getOwnerId());
            starEntity.setSubjectId(request.getId());
            subjectStarService.save(starEntity);
        }
    }

    @Transactional
    @Override
    public void cancelsubjectStar(SubjectStarRequest request) {
        Wrapper<SubjectStarEntity> wrapper = new QueryWrapper<SubjectStarEntity>().lambda()
                .eq(SubjectStarEntity::getMemberId, TokenUtils.getOwnerId())
                .eq(SubjectStarEntity::getSubjectId, request.getId());
        subjectStarService.remove(wrapper);
    }

    @Override
    public void clearArticleWeightCache() {
        // 清除所有权重相关的缓存
        String cacheKey = generateCacheKey(new ArticlePageRequest());
        articleWeightService.clearWeightCache(cacheKey);
    }

    @Override
    public List<ArticlePageResponse> getWeightedArticleList(ArticlePageRequest request) {
        String cacheKey = generateCacheKey(request);

        // 尝试从缓存获取
        List<ArticlePageResponse> cachedArticles = articleWeightService.getCachedWeightedArticles(cacheKey);
        if (CollectionUtil.isNotEmpty(cachedArticles)) {
            // 应用随机化策略
            return articleWeightService.applyRandomization(cachedArticles);
        }

        // 缓存未命中，重新计算权重
        List<ArticlePageResponse> allArticles = getAllArticles(request);
        if (CollectionUtil.isEmpty(allArticles)) {
            return Collections.emptyList();
        }

        // 计算权重并排序
        List<ArticlePageResponse> weightedArticles = articleWeightService.calculateAndSortByWeight(allArticles);

        // 缓存结果
        articleWeightService.cacheWeightedArticles(cacheKey, weightedArticles);

        // 应用随机化策略
        return articleWeightService.applyRandomization(weightedArticles);
    }

    /**
     * 获取所有符合条件的文章（不分页）
     */
    private List<ArticlePageResponse> getAllArticles(ArticlePageRequest request) {
        // 使用一个较大的pageSize来获取所有数据
        Page<ArticlePageResponse> page = PageHelper.startPage(1, 10000);
        this.getBaseMapper().getArticlePage(request);
        return page.getResult();
    }

    /**
     * 生成缓存键
     */
    private String generateCacheKey(ArticlePageRequest request) {
        StringBuilder keyBuilder = new StringBuilder("ARTICLE_WEIGHT:");
        keyBuilder.append(request.getCity() != null ? request.getCity() : "ALL");
        keyBuilder.append(":");
        keyBuilder.append(request.getSubjectId() != null ? request.getSubjectId() : "ALL");
        keyBuilder.append(":");
        keyBuilder.append(request.getSortType() != null ? request.getSortType() : "DEFAULT");
        return keyBuilder.toString();
    }

    private String convertToW(Integer number) {
        if (number == null) {
            return "0";
        }
        if (number >= 10000) {
            return String.format("%.1fw", number / 10000.0);
        }
        return number.toString();
    }

}
