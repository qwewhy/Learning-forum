package com.HongyuanWang.learningforum.mapper;

import com.HongyuanWang.learningforum.model.entity.Subscription;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author Hongyuan Wang
 * @description 针对表【subscription】的数据库操作Mapper
 * @createDate 2023-10-27 00:00:00 villages
 * @Entity com.HongyuanWang.learningforum.model.entity.Subscription
 */
@Mapper
public interface SubscriptionMapper extends BaseMapper<Subscription> {

} 