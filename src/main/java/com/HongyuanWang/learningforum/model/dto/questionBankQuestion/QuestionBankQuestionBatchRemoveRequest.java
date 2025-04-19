package com.HongyuanWang.learningforum.model.dto.questionBankQuestion;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 批量移除题目关联请求
 *
 * @author <a href="https://github.com/HongyuanWang">Hongyuan Wang</a>
 */
@Data
public class QuestionBankQuestionBatchRemoveRequest implements Serializable {

    /**
     * question bank id
     */
    private Long questionBankId;

    /**
     * question id 列表
     */
    private List<Long> questionIdList;

    private static final long serialVersionUID = 1L;
}