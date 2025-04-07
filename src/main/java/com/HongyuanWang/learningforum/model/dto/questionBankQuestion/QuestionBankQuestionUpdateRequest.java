package com.HongyuanWang.learningforum.model.dto.questionBankQuestion;

import lombok.Data;

import java.io.Serializable;

/**
 * 更新question_bank请求
 *
 * @author <a href="https://github.com/HongyuanWang">Hongyuan Wang</a>

 */
@Data
public class QuestionBankQuestionUpdateRequest implements Serializable {

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

    private static final long serialVersionUID = 1L;
}