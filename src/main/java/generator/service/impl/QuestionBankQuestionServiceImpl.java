package generator.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.HongyuanWang.learningforum.model.entity.QuestionBankQuestion;
import generator.service.QuestionBankQuestionService;
import com.HongyuanWang.learningforum.mapper.QuestionBankQuestionMapper;
import org.springframework.stereotype.Service;

/**
* @author hongyuanwang
* @description 针对表【question_bank_question(question bank questions)】的数据库操作Service实现
* @createDate 2025-04-02 15:03:53
*/
@Service
public class QuestionBankQuestionServiceImpl extends ServiceImpl<QuestionBankQuestionMapper, QuestionBankQuestion>
    implements QuestionBankQuestionService{

}




