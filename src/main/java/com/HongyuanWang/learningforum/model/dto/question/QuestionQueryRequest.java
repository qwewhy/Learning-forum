package com.HongyuanWang.learningforum.model.dto.question;

import com.HongyuanWang.learningforum.common.PageRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.List;

/**
 * 查询question请求
 *
 * @author <a href="https://github.com/HongyuanWang">Hongyuan Wang</a>
 * @from <a href="https://www.code-nav.cn">编程导航学习圈</a>
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class QuestionQueryRequest extends PageRequest implements Serializable {

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
     * 内容
     */
    private String content;

    /**
     * 标签列表
     */
    private List<String> tags;

    /**
     * 创建用户 id
     */
    private Long userId;

    /**
     * recommended answer
     */
    private String answer;

    /**
     * question bank id
     */
    private Long questionBankId;


    private static final long serialVersionUID = 1L;
}