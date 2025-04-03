package com.HongyuanWang.learningforum.model.dto.questionBank;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 更新question_bank请求
 *
 * @author <a href="https://github.com/HongyuanWang">Hongyuan Wang</a>
 * @from <a href="https://www.code-nav.cn">编程导航学习圈</a>
 */
@Data
public class QuestionBankUpdateRequest implements Serializable {

    /**
     * id
     */
    private Long id;

    /**
     * 标题
     */
    private String title;

    /**
     * question description
     */
    private String description;

    /**
     * question picture
     */
    private String picture;


    private static final long serialVersionUID = 1L;
}