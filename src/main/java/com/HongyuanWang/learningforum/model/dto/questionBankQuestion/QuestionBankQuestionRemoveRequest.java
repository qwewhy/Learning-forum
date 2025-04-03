package com.HongyuanWang.learningforum.model.dto.questionBankQuestion;
import lombok.Data;
import java.io.Serializable;


@Data
//移除题库关系请求
public class QuestionBankQuestionRemoveRequest implements Serializable {
    /**
     * id
     */
    private Long id;

    /**
     * 题库id
     */
    private Long questionBankId;

    /**
     * 题目id
     */
    private Long questionId;

    private static final long serialVersionUID = 1L;
}
