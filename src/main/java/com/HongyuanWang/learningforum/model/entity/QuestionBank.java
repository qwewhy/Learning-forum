package com.HongyuanWang.learningforum.model.entity;

import com.baomidou.mybatisplus.annotation.*;

import java.util.Date;
import lombok.Data;

/**
 * questions
 * @TableName question_bank
 */
@TableName(value ="question_bank")
@Data
public class QuestionBank {
    /**
     * id
     */
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    /**
     * question title
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
     * create user id
     */
    private Long userId;

    /**
     * edit time
     */
    private Date editTime;

    /**
     * create time
     */
    private Date createTime;

    /**
     * update time
     */
    private Date updateTime;

    /**
     * is delete (0: no, 1: yes)
     */
    @TableLogic
    private Integer isDelete;

    /**
     * priority
     */
    private Integer priority;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}