package com.wxmblog.yanjian.tasks;

import com.wxmblog.base.common.utils.DateUtils;
import com.wxmblog.base.common.utils.NumberUtils;
import com.wxmblog.yanjian.dao.UserApplyDao;
import com.wxmblog.yanjian.service.TUserService;
import com.wxmblog.yanjian.service.UserApplyService;
import com.wxmblog.yanjian.service.UserNatureVisitService;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RBucket;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Date;

@EnableScheduling
@Component
@Slf4j
public class TaskImpl {

    @Autowired
    private TUserService tUserService;

    @Autowired
    private UserApplyService userApplyService;

    @Autowired
    RedissonClient redissonClient;

    @Autowired
    private UserNatureVisitService userNatureVisitService;

    /**
     * 赠送次数
     */
    //@Scheduled(cron = "0 0 12 ? * THU")
    public void executeGiftPoint() {
        log.info("进入赠送次数任务");
        LocalDateTime endOfDay = LocalDateTime.now().with(LocalTime.MAX);
        long executeNum = NumberUtils.getValueOpsByCode("executeGiftPointTask" + DateUtils.dateToStr("yyyyMMdd", new Date()), endOfDay);
        if (executeNum == 1) {
            log.info("执行赠送次数任务");
            tUserService.executeGiftPoint();
        }
    }

    //凌晨0点一分执行
    @Scheduled(cron = "0 1 0 * * ?")
    public void executeUpdateStatus() {
        LocalDateTime endOfDay = LocalDateTime.now().with(LocalTime.MAX);
        long executeNum = NumberUtils.getValueOpsByCode("updateApplyTimeOutStatusTask" + DateUtils.dateToStr("yyyyMMdd", new Date()), endOfDay);
        if (executeNum == 1) {
            log.info("修改超过七天未处理申请");
            userApplyService.executeUpdateStatus();
        }
    }

    //每周一凌晨0点2分执行
    @Scheduled(cron = "0 10 0 ? * MON")
    public void executeDeleteNatureVisit() {

        LocalDateTime endOfDay = LocalDateTime.now().with(LocalTime.MAX);
        long executeNum = NumberUtils.getValueOpsByCode("executeDeleteNatureVisitTask" + DateUtils.dateToStr("yyyyMMdd", new Date()), endOfDay);
        log.info("进入定时任务，排序规则重置:{}", executeNum);
        if (executeNum == 1) {
            log.info("首次执行排序规则重置序");
            userNatureVisitService.executeDeleteNatureVisit();
        }
    }

    //每天12点处理 提醒用户处理申请
    @Scheduled(cron = "0 10 12 * * ?")
    public void executeReminderApply() {
        log.info("进入提醒用户处理申请任务");
        LocalDateTime endOfDay = LocalDateTime.now().with(LocalTime.MAX);
        long executeNum = NumberUtils.getValueOpsByCode("executeReminderApply" + DateUtils.dateToStr("yyyyMMdd", new Date()), endOfDay);
        if (executeNum == 1) {
            log.info("执行提醒用户处理申请任务");
            tUserService.executeReminderApply();
        }
    }

    //每天12点处理 提醒用户访问
    @Scheduled(cron = "0 20 12 * * ?")
    public void executeReminderVisit() {
        log.info("进入提醒用户访问任务");
        LocalDateTime endOfDay = LocalDateTime.now().with(LocalTime.MAX);
        long executeNum = NumberUtils.getValueOpsByCode("executeReminderVisit" + DateUtils.dateToStr("yyyyMMdd", new Date()), endOfDay);
        if (executeNum == 1) {
            log.info("执行提醒用户访问任务");
            tUserService.executeReminderVisit();
        }
    }
}
