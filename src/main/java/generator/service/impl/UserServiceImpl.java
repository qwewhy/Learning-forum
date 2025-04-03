package generator.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.HongyuanWang.learningforum.model.entity.User;
import generator.service.UserService;
import com.HongyuanWang.learningforum.mapper.UserMapper;
import org.springframework.stereotype.Service;

/**
* @author hongyuanwang
* @description 针对表【user(users)】的数据库操作Service实现
* @createDate 2025-04-02 15:03:53
*/
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User>
    implements UserService{

}




