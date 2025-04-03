package com.HongyuanWang.learningforum.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.util.Date;
import lombok.Data;

/**
 * post favourites
 * @TableName post_favour
 */
@TableName(value ="post_favour")
@Data
public class PostFavour {
    /**
     * id
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * post id
     */
    private Long postId;

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
}