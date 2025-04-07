package com.HongyuanWang.learningforum.model.dto.questionBankQuestion;

import lombok.Data;

import java.io.Serializable;

/**
 * 创建question_bank请求
 *
 * @author <a href="https://github.com/HongyuanWang">Hongyuan Wang</a>

 */
@Data
public class QuestionBankQuestionAddRequest implements Serializable {

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