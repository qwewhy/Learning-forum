package com.HongyuanWang.learningforum.model.dto.question;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 批量删除题目请求
 *
 * @author <a href="https://github.com/HongyuanWang">Hongyuan Wang</a>

 */
@Data
public class QuestionBatchDeleteRequest implements Serializable {

    /**
     * question ID
     */
    private List<Long> questionIdList;

    private static final long serialVersionUID = 1L;
}