package com.HongyuanWang.learningforum.service.impl;

import cn.hutool.core.collection.CollUtil;
import com.HongyuanWang.learningforum.exception.BusinessException;
import com.HongyuanWang.learningforum.model.entity.Question;
import com.HongyuanWang.learningforum.model.entity.QuestionBank;
import com.HongyuanWang.learningforum.model.entity.User;
import com.HongyuanWang.learningforum.service.QuestionBankService;
import com.HongyuanWang.learningforum.service.QuestionService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.HongyuanWang.learningforum.common.ErrorCode;
import com.HongyuanWang.learningforum.constant.CommonConstant;
import com.HongyuanWang.learningforum.exception.ThrowUtils;
import com.HongyuanWang.learningforum.mapper.QuestionBankQuestionMapper;
import com.HongyuanWang.learningforum.model.dto.questionBankQuestion.QuestionBankQuestionQueryRequest;
import com.HongyuanWang.learningforum.model.entity.QuestionBankQuestion;
import com.HongyuanWang.learningforum.model.vo.QuestionBankQuestionVO;
import com.HongyuanWang.learningforum.service.QuestionBankQuestionService;
import com.HongyuanWang.learningforum.service.UserService;
import com.HongyuanWang.learningforum.utils.SqlUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.aop.framework.AopContext;
import org.springframework.context.annotation.Lazy;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;

/**
 * question_bank_question服务实现
 *
 * @author <a href="https://github.com/HongyuanWang">Hongyuan Wang</a>

 */
@Service
@Slf4j
public class QuestionBankQuestionServiceImpl extends ServiceImpl<QuestionBankQuestionMapper, QuestionBankQuestion> implements QuestionBankQuestionService {

    @Resource
    private UserService userService;

    @Resource
    private QuestionBankService questionBankService;

    @Resource
    @Lazy
    private QuestionService questionService;

    /**
     * 校验数据
     *
     * @param questionBankQuestion
     * @param add      对创建的数据进行校验
     */
    @Override
    public void validQuestionBankQuestion(QuestionBankQuestion questionBankQuestion, boolean add) {
        ThrowUtils.throwIf(questionBankQuestion == null, ErrorCode.PARAMS_ERROR);
        // 题目和题库必须存在
        Long questionId = questionBankQuestion.getQuestionId();
        if (questionId != null){
            Question question = questionService.getById(questionId);
            ThrowUtils.throwIf(question == null, ErrorCode.PARAMS_ERROR,"题目不存在");
        }
        Long questionBankId = questionBankQuestion.getQuestionBankId();
        if (questionBankId != null){
            QuestionBank questionBank = questionBankService.getById(questionBankId);
            ThrowUtils.throwIf(questionBank == null, ErrorCode.PARAMS_ERROR,"题库不存在");
        }
    }

    /**
     * 获取查询条件
     *
     * @param questionBankQuestionQueryRequest
     * @return
     */
    @Override
    public QueryWrapper<QuestionBankQuestion> getQueryWrapper(QuestionBankQuestionQueryRequest questionBankQuestionQueryRequest) {
        QueryWrapper<QuestionBankQuestion> queryWrapper = new QueryWrapper<>();
        if (questionBankQuestionQueryRequest == null) {
            return queryWrapper;
        }
        // todo 从对象中取值
        Long id = questionBankQuestionQueryRequest.getId();
        Long notId = questionBankQuestionQueryRequest.getNotId();
        String sortField = questionBankQuestionQueryRequest.getSortField();
        String sortOrder = questionBankQuestionQueryRequest.getSortOrder();
        Long questionBankId = questionBankQuestionQueryRequest.getQuestionBankId();
        Long questionId = questionBankQuestionQueryRequest.getQuestionId();
        Long userId = questionBankQuestionQueryRequest.getUserId();
        // todo 补充需要的查询条件

        // 精确查询
        queryWrapper.ne(ObjectUtils.isNotEmpty(notId), "id", notId);
        queryWrapper.eq(ObjectUtils.isNotEmpty(id), "id", id);
        queryWrapper.eq(ObjectUtils.isNotEmpty(userId), "userId", userId);
        queryWrapper.eq(ObjectUtils.isNotEmpty(questionBankId), "questionBankId", questionBankId);
        queryWrapper.eq(ObjectUtils.isNotEmpty(questionId), "questionId", questionId);


        // 排序规则
        queryWrapper.orderBy(SqlUtils.validSortField(sortField),
                sortOrder.equals(CommonConstant.SORT_ORDER_ASC),
                sortField);
        return queryWrapper;
    }

    /**
     * 获取question_bank_question封装
     *
     * @param questionBankQuestion
     * @param request
     * @return
     */
    @Override
    public QuestionBankQuestionVO getQuestionBankQuestionVO(QuestionBankQuestion questionBankQuestion, HttpServletRequest request) {
        // 对象转封装类
        QuestionBankQuestionVO questionBankQuestionVO = QuestionBankQuestionVO.objToVo(questionBankQuestion);

        // todo 可以根据需要为封装对象补充值，不需要的内容可以删除
        return questionBankQuestionVO;
    }

    /**
     * 分页获取question_bank_question封装
     *
     * @param questionBankQuestionPage
     * @param request
     * @return
     */
    @Override
    public Page<QuestionBankQuestionVO> getQuestionBankQuestionVOPage(Page<QuestionBankQuestion> questionBankQuestionPage, HttpServletRequest request) {
        List<QuestionBankQuestion> questionBankQuestionList = questionBankQuestionPage.getRecords();
        Page<QuestionBankQuestionVO> questionBankQuestionVOPage = new Page<>(questionBankQuestionPage.getCurrent(), questionBankQuestionPage.getSize(), questionBankQuestionPage.getTotal());
        if (CollUtil.isEmpty(questionBankQuestionList)) {
            return questionBankQuestionVOPage;
        }
        // 对象列表 => 封装对象列表
        List<QuestionBankQuestionVO> questionBankQuestionVOList = questionBankQuestionList.stream().map(questionBankQuestion -> {
            return QuestionBankQuestionVO.objToVo(questionBankQuestion);
        }).collect(Collectors.toList());

        // todo 可以根据需要为封装对象补充值，不需要的内容可以删除
        questionBankQuestionVOPage.setRecords(questionBankQuestionVOList);
        return questionBankQuestionVOPage;
    }

    /**
     * 批量添加题目
     * @param questionIdList
     * @param questionBankId
     * @param loginUser
     */
    @Override
    public void batchAddQuestionBankToBank(List<Long> questionIdList, Long questionBankId, User loginUser) {
        // 校验数据
        ThrowUtils.throwIf(CollUtil.isEmpty(questionIdList), ErrorCode.PARAMS_ERROR,"题目List不能为空");
        ThrowUtils.throwIf(questionBankId <= 0, ErrorCode.PARAMS_ERROR,"题库id非法");
        ThrowUtils.throwIf(loginUser == null, ErrorCode.PARAMS_ERROR);
        // 检查题目id是否存在
        LambdaQueryWrapper<Question> questionLambdaQueryWrapper = Wrappers.lambdaQuery(Question.class)
                .select(Question::getId)
                .in(Question::getId,questionIdList);
        List<Long> validQuestionIdList = questionService.listObjs(questionLambdaQueryWrapper, obj->(Long) obj);
        // 合法的题目id列表为空
        //检查哪些题目还不存在于题库里避免重复插入
        LambdaQueryWrapper<QuestionBankQuestion> lambdaQueryWrapper = Wrappers.lambdaQuery(QuestionBankQuestion.class)
                .eq(QuestionBankQuestion::getQuestionBankId, questionBankId)
                .notIn(QuestionBankQuestion::getQuestionId, validQuestionIdList);
        List<QuestionBankQuestion> notExistQuestionList = this.list(lambdaQueryWrapper);
        validQuestionIdList = notExistQuestionList.stream()
                .map(QuestionBankQuestion::getId)
                .collect(Collectors.toList());
        ThrowUtils.throwIf(CollUtil.isEmpty(validQuestionIdList), ErrorCode.PARAMS_ERROR, "所有题目都已经在题目中了");
        // 检查题库id是否存在
        QuestionBank questionBank = questionBankService.getById(questionBankId);
        ThrowUtils.throwIf(questionBank == null, ErrorCode.PARAMS_ERROR,"题库不存在");
        // 自定义线程池 （IO 密集型）
        ThreadPoolExecutor customExecutor = new ThreadPoolExecutor(
                20, //核心线程数
                50, //最大线程数
                60L, //线程空闲存活时间
                TimeUnit.SECONDS, //存活时间单位
                new LinkedBlockingDeque<>(10000), //阻塞队列数量
                new ThreadPoolExecutor.AbortPolicy() //拒绝策略
        );
        //保存所有批次任务
        List<CompletableFuture<Void>> futures = new ArrayList<>();

        // 分批处理，避免长事务，假设每次处理1000条数据
        int batchSize = 1000;
        int totalQuestionListSize = validQuestionIdList.size();
        for (int i = 0; i< totalQuestionListSize; i+=batchSize) {
            List<Long> subList =  validQuestionIdList.subList(i, Math.min(i+batchSize, totalQuestionListSize));
            List<QuestionBankQuestion> questionBankQuestions = subList.stream()
                    .map(questionId -> {
                    QuestionBankQuestion questionBankQuestion = new QuestionBankQuestion();
                    questionBankQuestion.setQuestionBankId(questionBankId);
                    questionBankQuestion.setQuestionId(questionId);
                    questionBankQuestion.setUserId(loginUser.getId());
                    return questionBankQuestion;
                    }).collect(Collectors.toList());
            // 使用事务处理每批数据
            // 获取代理
            QuestionBankQuestionService questionBankQuestionService = (QuestionBankQuestionServiceImpl) AopContext.currentProxy();
            // 异步处理每一批数据，将任务添加到异步任务列表
            CompletableFuture<Void> future = CompletableFuture.runAsync(()->{
                questionBankQuestionService.batchAddQuestionsToBankInner(questionBankQuestions);
            }, customExecutor);
            futures.add(future);
        }
        //等待所有的批次完成操作
        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
        //关闭线程池
        customExecutor.shutdown();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void batchAddQuestionsToBankInner(List<QuestionBankQuestion> questionBankQuestions) {
        try {
            boolean result = this.saveBatch(questionBankQuestions);
            ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR, "向题库添加题目失败");
        } catch (DataIntegrityViolationException e) {
            log.error("数据库唯一键冲突或违反其他完整性约束, 错误信息: {}", e.getMessage());
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "题目已存在于该题库，无法重复添加");
        } catch (DataAccessException e) {
            log.error("数据库连接问题、事务问题等导致操作失败, 错误信息: {}", e.getMessage());
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "数据库操作失败");
        } catch (Exception e) {
            // 捕获其他异常，做通用处理
            log.error("添加题目到题库时发生未知错误，错误信息: {}", e.getMessage());
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "向题库添加题目失败");
        }
    }



    @Override
    @Transactional(rollbackFor = Exception.class)
    public void batchRemoveQuestionBankFromBank(List<Long> questionIdList, Long questionBankId, User loginUser) {
        // 校验数据
        ThrowUtils.throwIf(CollUtil.isEmpty(questionIdList), ErrorCode.PARAMS_ERROR,"要删除的题目List不能为空");
        ThrowUtils.throwIf(questionBankId <= 0, ErrorCode.PARAMS_ERROR,"删除的题目所在题库id非法");

        // 执行插入
        for (long questionId : questionIdList){
            //
            LambdaQueryWrapper<QuestionBankQuestion> lambdaQueryWrapper = Wrappers.lambdaQuery(QuestionBankQuestion.class)
                    .eq(QuestionBankQuestion::getQuestionId, questionId)
                    .eq(QuestionBankQuestion::getQuestionBankId, questionId);

            boolean result = this.remove(lambdaQueryWrapper);
            ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR,"删除题目失败");
        }
    }

}
