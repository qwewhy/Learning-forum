package com.HongyuanWang.learningforum.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.TableField;
import java.util.Date;
import lombok.Data;

/**
 * question
 * @TableName question
 */
@TableName(value ="question")
@Data
public class Question {
    /**
     * id
     */
    @TableId(type = IdType.ASSIGN_ID)
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
     * tag list (json array)
     */
    private String tags;

    /**
     * recommended answer
     */
    private String answer;

    /**
     * creator user id
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
     * is deleted
     */
    @TableLogic
    private Integer isDelete;

    /**
     * status: 0-pending review, 1-approved, 2-rejected
     */
    private Integer reviewStatus;

    /**
     * review message
     */
    private String reviewMessage;

    /**
     * reviewer id
     */
    private Long reviewerId;

    /**
     * review time
     */
    private Date reviewTime;

    /**
     * view count
     */
    private Integer viewNum;

    /**
     * thumb count
     */
    private Integer thumbNum;

    /**
     * favourite count
     */
    private Integer favourNum;

    /**
     * priority
     */
    private Integer priority;

    /**
     * question source
     */
    private String source;

    /**
     * vip only (1 means vip only)
     */
    private Integer needVip;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}