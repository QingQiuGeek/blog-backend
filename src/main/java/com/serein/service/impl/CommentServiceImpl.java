package com.serein.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.serein.mapper.UserMapper;
import com.serein.model.UserHolder;
import com.serein.model.dto.CommentDTO.CommentDTO;
import com.serein.model.entity.Comment;
import com.serein.model.enums.UserRoleEnum;
import com.serein.model.vo.CommentVO.CommentUserInfoVO;
import com.serein.model.vo.CommentVO.CommentVO;
import com.serein.model.vo.UserVO.LoginUserVO;
import com.serein.service.CommentService;
import com.serein.mapper.CommentMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
* @author 懒大王Smile
* @description 针对表【comment(评论表)】的数据库操作Service实现
* @createDate 2024-09-12 22:19:13
*/
@Service
public class CommentServiceImpl extends ServiceImpl<CommentMapper, Comment>
    implements CommentService{

    @Autowired
    CommentMapper commentMapper;

    @Autowired
    UserMapper userMapper;
    @Override
    public Boolean commentPassage(CommentDTO commentDTO) {
        LoginUserVO loginUserVO = UserHolder.getUser();
        if (loginUserVO==null){
            return false;
        }
        Long userId = loginUserVO.getUserId();
        Comment comment = new Comment();
        BeanUtil.copyProperties(commentDTO,comment);
        comment.setCommentUserId(userId);
        return this.save(comment);
    }

    /**
     * avatar name ipAddress
     * @param authorId
     * @param passageId
     * @return
     */
    @Override
    public List<CommentVO> getCommentByPassageId(Long authorId, Long passageId) {
        List<CommentVO> commentVOS = commentMapper.getCommentVoListByPassageId(passageId);
        if (commentVOS.isEmpty()){
            return commentVOS;
        }
        //设置评论的用户头像、ip地址、用户名
        getCommentUserInfo(commentVOS);
        //判断用户是否登录，登录就判断该用户是否可以自己的删除评论
        LoginUserVO loginUserVO = UserHolder.getUser();
        if (loginUserVO==null){
            return commentVOS;
        }
        Long userId = loginUserVO.getUserId();
        //作者拥有对自己文章的所有评论的删除权
        if(Objects.equals(authorId, userId)){
            commentVOS.forEach((commentVO -> {
                commentVO.setCanDelete(true);
            }));
            return commentVOS;
        }
        //作者拥有对所有文章的所有评论的删除权
        if( "admin".equals(loginUserVO.getRole())){
            commentVOS.forEach((commentVO -> {
                commentVO.setCanDelete(true);
            }));
            return commentVOS;
        }
        //不是管理、不是文章作者，那么只能删除自己的评论
        commentVOS.forEach((commentVO -> {
            if (Objects.equals(commentVO.getCommentUserId(), userId)){
                commentVO.setCanDelete(true);
            }
        }));
        return commentVOS;
    }

    //根据评论的commentUserId获取评论用户的头像、userName等
    private void getCommentUserInfo(List<CommentVO> commentVOS) {

        List<Long> userIdList = commentVOS.stream().map(CommentVO::getCommentUserId).collect(Collectors.toList());
        List<CommentUserInfoVO> commentUserInfoVOS = userMapper.getCommentUserInfoByUserIdList(userIdList);

        //将 commentUserInfoVOS 转换为一个以 userId 为键的 Map，这样在遍历 commentVOS 时，可以通过 Map.get(commentUserId) 快速查找对应的 CommentUserInfoVO，
        // 避免了每次都进行 O(n) 的查找操作，性能上有显著提升，尤其是数据量大的时候。
        Map<Long, CommentUserInfoVO> userInfoMap = commentUserInfoVOS.stream()
                .collect(Collectors.toMap(CommentUserInfoVO::getUserId, userInfo -> userInfo));

        commentVOS.forEach((commentVO)->{
            Long commentUserId = commentVO.getCommentUserId();

            // 从 Map 中获取对应的 CommentUserInfoVO
            CommentUserInfoVO commentUserInfoVO = userInfoMap.get(commentUserId);

            // 如果找到了对应的用户信息，复制属性
            if (commentUserInfoVO != null) {
                BeanUtil.copyProperties(commentUserInfoVO, commentVO, CopyOptions.create().ignoreNullValue());
            }
        });
    }

    @Override
    public Boolean deleteComment(Long commentId) {
        return this.removeById(commentId);
    }




}




