package com.HongyuanWang.learningforum.mapper;

import com.HongyuanWang.learningforum.model.entity.Question;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Select;
import java.util.Date;
import java.util.List;

/**
* @author hongyuanwang
* @description 针对表【question(question)】的数据库操作Mapper
* @createDate 2025-04-02 15:03:53
* @Entity com.HongyuanWang.learningforum.model.entity.Question
*/
public interface QuestionMapper extends BaseMapper<Question> {
    @Select("select * from question where updateTime >= #{minUpdateTime}")
    List<Question> listQuestionWithDelete(Date minUpdateTime);
}




