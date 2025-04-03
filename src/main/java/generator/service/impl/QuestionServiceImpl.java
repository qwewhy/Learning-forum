package generator.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.HongyuanWang.learningforum.model.entity.Question;
import generator.service.QuestionService;
import com.HongyuanWang.learningforum.mapper.QuestionMapper;
import org.springframework.stereotype.Service;

/**
* @author hongyuanwang
* @description 针对表【question(question)】的数据库操作Service实现
* @createDate 2025-04-02 15:03:53
*/
@Service
public class QuestionServiceImpl extends ServiceImpl<QuestionMapper, Question>
    implements QuestionService{

}




