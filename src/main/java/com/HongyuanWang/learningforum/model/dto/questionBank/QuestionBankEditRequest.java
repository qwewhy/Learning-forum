package com.HongyuanWang.learningforum.model.dto.questionBank;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 编辑question_bank请求
 *
 * @author <a href="https://github.com/HongyuanWang">Hongyuan Wang</a>

 */
@Data
public class QuestionBankEditRequest implements Serializable {

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