package com.HongyuanWang.learningforum.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
@TableName("verification_token")
public class VerificationToken implements Serializable {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String token;

    private Long userId;

    private String tokenType;

    private Date expiresAt;

    private Date createdAt;

    private Date usedAt;

    private static final long serialVersionUID = 1L;
}