package generator.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.HongyuanWang.learningforum.model.entity.PostFavour;
import generator.service.PostFavourService;
import com.HongyuanWang.learningforum.mapper.PostFavourMapper;
import org.springframework.stereotype.Service;

/**
* @author hongyuanwang
* @description 针对表【post_favour(post favourites)】的数据库操作Service实现
* @createDate 2025-04-02 15:03:53
*/
@Service
public class PostFavourServiceImpl extends ServiceImpl<PostFavourMapper, PostFavour>
    implements PostFavourService{

}




