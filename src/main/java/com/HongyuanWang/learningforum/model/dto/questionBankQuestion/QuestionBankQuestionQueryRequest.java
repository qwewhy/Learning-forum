package com.HongyuanWang.learningforum.model.dto.questionBankQuestion;

import com.HongyuanWang.learningforum.common.PageRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * 查询question_bank请求
 *
 * @author <a href="https://github.com/HongyuanWang">Hongyuan Wang</a>
 * @from <a href="https://www.code-nav.cn">编程导航学习圈</a>
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class QuestionBankQuestionQueryRequest extends PageRequest implements Serializable {

    /**
     * id
     */
    private Long id;

    /**
     * id
     */
    private Long notId;

    /**
     * question bank id
     */
    private Long questionBankId;

    /**
     * question id
     */
    private Long questionId;
    /**
     * create user id
     */
    private Long userId;

    private static final long serialVersionUID = 1L;
}