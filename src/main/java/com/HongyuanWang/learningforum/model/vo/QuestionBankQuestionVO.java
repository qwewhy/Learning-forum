package com.HongyuanWang.learningforum.model.vo;

import cn.hutool.json.JSONUtil;
import com.HongyuanWang.learningforum.model.entity.QuestionBankQuestion;
import lombok.Data;
import org.springframework.beans.BeanUtils;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * question_bank_question视图
 *
 * @author <a href="https://github.com/HongyuanWang">Hongyuan Wang</a>

 */
@Data
public class QuestionBankQuestionVO implements Serializable {

    /**
     * id
     */
    private Long id;

    /**
     * question bank id
     */
    private Long questionBankId;

    /**
     * question id
     */
    private Long questionId;

    /**
     * 创建用户 id
     */
    private Long userId;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;

    /**
     * 标签列表
     */
    private List<String> tagList;

    /**
     * 创建用户信息
     */
    private UserVO user;

    /**
     * 封装类转对象
     *
     * @param questionBankQuestionVO
     * @return
     */
    public static QuestionBankQuestion voToObj(QuestionBankQuestionVO questionBankQuestionVO) {
        if (questionBankQuestionVO == null) {
            return null;
        }
        QuestionBankQuestion questionBankQuestion = new QuestionBankQuestion();
        BeanUtils.copyProperties(questionBankQuestionVO, questionBankQuestion);
        List<String> tagList = questionBankQuestionVO.getTagList();
        return questionBankQuestion;
    }

    /**
     * 对象转封装类
     *
     * @param questionBankQuestion
     * @return
     */
    public static QuestionBankQuestionVO objToVo(QuestionBankQuestion questionBankQuestion) {
        if (questionBankQuestion == null) {
            return null;
        }
        QuestionBankQuestionVO questionBankQuestionVO = new QuestionBankQuestionVO();
        BeanUtils.copyProperties(questionBankQuestion, questionBankQuestionVO);
        return questionBankQuestionVO;
    }
}
