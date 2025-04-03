package com.HongyuanWang.learningforum.model.dto.questionBank;

import com.HongyuanWang.learningforum.common.PageRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.List;

/**
 * 查询question_bank请求
 *
 * @author <a href="https://github.com/HongyuanWang">Hongyuan Wang</a>
 * @from <a href="https://www.code-nav.cn">编程导航学习圈</a>
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class QuestionBankQueryRequest extends PageRequest implements Serializable {

    /**
     * id
     */
    private Long id;

    /**
     * id
     */
    private Long notId;

    /**
     * 搜索词
     */
    private String searchText;

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

    /**
     * need or not to query question list
     */
    private boolean needQueryQuestionList;

    /**
     * 创建用户 id
     */
    private Long userId;

    private static final long serialVersionUID = 1L;
}