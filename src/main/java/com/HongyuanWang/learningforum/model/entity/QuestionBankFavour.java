package com.HongyuanWang.learningforum.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

/**
 * posts
 * @TableName post
 */
@TableName(value ="post")
@Data
public class QuestionBankFavour {
    /**
     * id
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * title
     */
    private String title;

    /**
     * content
     */
    private String content;

    /**
     * tags(json arrays
     */
    private String tags;

    /**
     * thumb count
     */
    private Integer thumbNum;

    /**
     * favour count
     */
    private Integer favourNum;

    /**
     * create user id
     */
    private Long userId;

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
    private Integer isDelete;
}