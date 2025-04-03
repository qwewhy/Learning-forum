package generator.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.HongyuanWang.learningforum.model.entity.Post;
import generator.service.PostService;
import com.HongyuanWang.learningforum.mapper.PostMapper;
import org.springframework.stereotype.Service;

/**
* @author hongyuanwang
* @description 针对表【post(posts)】的数据库操作Service实现
* @createDate 2025-04-02 15:03:53
*/
@Service
public class PostServiceImpl extends ServiceImpl<PostMapper, Post>
    implements PostService{

}




