package com.HongyuanWang.learningforum.service;

import com.HongyuanWang.learningforum.model.entity.User;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.HongyuanWang.learningforum.model.dto.questionBankQuestion.QuestionBankQuestionQueryRequest;
import com.HongyuanWang.learningforum.model.entity.QuestionBankQuestion;
import com.HongyuanWang.learningforum.model.vo.QuestionBankQuestionVO;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * question_bank_question服务
 *
 * @author <a href="https://github.com/HongyuanWang">Hongyuan Wang</a>

 */
public interface QuestionBankQuestionService extends IService<QuestionBankQuestion> {

    /**
     * 校验数据
     *
     * @param questionBankQuestion
     * @param add 对创建的数据进行校验
     */
    void validQuestionBankQuestion(QuestionBankQuestion questionBankQuestion, boolean add);

    /**
     * 获取查询条件
     *
     * @param questionBankQuestionQueryRequest
     * @return
     */
    QueryWrapper<QuestionBankQuestion> getQueryWrapper(QuestionBankQuestionQueryRequest questionBankQuestionQueryRequest);
    
    /**
     * 获取question_bank_question封装
     *
     * @param questionBankQuestion
     * @param request
     * @return
     */
    QuestionBankQuestionVO getQuestionBankQuestionVO(QuestionBankQuestion questionBankQuestion, HttpServletRequest request);

    /**
     * 分页获取question_bank_question封装
     *
     * @param questionBankQuestionPage
     * @param request
     * @return
     */
    Page<QuestionBankQuestionVO> getQuestionBankQuestionVOPage(Page<QuestionBankQuestion> questionBankQuestionPage, HttpServletRequest request);

    /**
     * 批量添加题目到题库
     *
     * @param questionIdList
     * @param questionBankId
     * @param loginUser
     * @return
     */
    void batchAddQuestionBankToBank(List<Long> questionIdList, Long questionBankId, User loginUser);

    /**
     * 批量添加题目到题库 仅内部调用的事务接口
     * @param questionBankQuestions
     */
    @Transactional(rollbackFor = Exception.class)
    void batchAddQuestionsToBankInner(List<QuestionBankQuestion> questionBankQuestions);

    void batchRemoveQuestionBankFromBank(List<Long> questionIdList, Long questionBankId, User loginUser);
}
