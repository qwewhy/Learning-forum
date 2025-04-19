package com.HongyuanWang.learningforum.model.dto.questionBankQuestion;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 创建question_bank请求
 *
 * @author <a href="https://github.com/HongyuanWang">Hongyuan Wang</a>

 */
@Data
public class QuestionBankQuestionBatchAddRequest implements Serializable {

    /**
     * question bank id
     */
    private Long questionBankId;

    /**
     * question id
     */
    private List<Long> questionIdList;

    private static final long serialVersionUID = 1L;
}