package com.HongyuanWang.learningforum.job.cycle;

import cn.hutool.core.collection.CollUtil;
import com.HongyuanWang.learningforum.esdao.QuestionEsDao;
import com.HongyuanWang.learningforum.mapper.QuestionMapper;
import com.HongyuanWang.learningforum.model.dto.question.QuestionEsDTO;
import com.HongyuanWang.learningforum.model.entity.Question;
import org.springframework.beans.factory.annotation.Value;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import java.text.SimpleDateFormat;
import java.util.TimeZone;
/**
 * 增量同步题目到 es
 *
 * @author <a href="https://github.com/liyupi">程序员鱼皮</a>
 * @from <a href="https://yupi.icu">编程导航知识星球</a>
 */
// todo 取消注释开启任务
@Component
@Slf4j
public class IncSyncQuestionToEs {

    @Value("${app.timezone:UTC}")
    private String appTimezone;

    @Resource
    private QuestionMapper questionMapper;

    @Resource
    private QuestionEsDao questionEsDao;

    /**
     * 每分钟执行一次
     */
    @Scheduled(fixedRate = 60 * 1000)
    public void run() {
        // 查询近 5 分钟内的数据
        long FIVE_MINUTES = 5 * 60 * 1000L;
        // 使用配置的时区
        TimeZone configuredTimeZone = TimeZone.getTimeZone(appTimezone);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        sdf.setTimeZone(configuredTimeZone);

        Date currentTime = new Date();
        Date fiveMinutesAgoDate = new Date(currentTime.getTime() - FIVE_MINUTES);

        log.info("Current time: {}, Searching for records updated after: {}",
                sdf.format(currentTime), sdf.format(fiveMinutesAgoDate));
        List<Question> questionList = questionMapper.listQuestionWithDelete(fiveMinutesAgoDate);
        if (CollUtil.isEmpty(questionList)) {
            log.info("no inc question");
            return;
        }
        List<QuestionEsDTO> questionEsDTOList = questionList.stream()
                .map(QuestionEsDTO::objToDto)
                .collect(Collectors.toList());
        final int pageSize = 500;
        int total = questionEsDTOList.size();
        log.info("IncSyncQuestionToEs start, total {}", total);
        for (int i = 0; i < total; i += pageSize) {
            int end = Math.min(i + pageSize, total);
            log.info("sync from {} to {}", i, end);
            questionEsDao.saveAll(questionEsDTOList.subList(i, end));
        }
        log.info("IncSyncQuestionToEs end, total {}", total);
    }
}