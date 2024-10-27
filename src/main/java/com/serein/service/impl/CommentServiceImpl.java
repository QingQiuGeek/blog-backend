package com.serein.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.serein.model.entity.Comment;
import com.serein.service.CommentService;
import com.serein.mapper.CommentMapper;
import org.springframework.stereotype.Service;

/**
* @author 懒大王Smile
* @description 针对表【comment(评论表)】的数据库操作Service实现
* @createDate 2024-09-12 22:19:13
*/
@Service
public class CommentServiceImpl extends ServiceImpl<CommentMapper, Comment>
    implements CommentService{

}




