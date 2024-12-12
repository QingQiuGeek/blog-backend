package com.serein.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.serein.constants.ErrorCode;
import com.serein.constants.ErrorInfo;
import com.serein.exception.BusinessException;
import com.serein.mapper.CommentMapper;
import com.serein.mapper.PassageMapper;
import com.serein.mapper.UserMapper;
import com.serein.model.AdminCommentPageRequest;
import com.serein.model.UserHolder;
import com.serein.model.dto.CommentDTO.CommentDTO;
import com.serein.model.dto.CommentDTO.DeleteCommentDTO;
import com.serein.model.entity.Comment;
import com.serein.model.vo.CommentVO.CommentUserInfoVO;
import com.serein.model.vo.CommentVO.CommentVO;
import com.serein.model.vo.UserVO.LoginUserVO;
import com.serein.service.CommentService;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author 懒大王Smile
 * @description 针对表【comment(评论表)】的数据库操作Service实现
 * @createDate 2024-09-12 22:19:13
 */
@Slf4j
@Service
public class CommentServiceImpl extends ServiceImpl<CommentMapper, Comment>
    implements CommentService {

  @Autowired
  CommentMapper commentMapper;

  @Autowired
  UserMapper userMapper;

  @Autowired
  PassageMapper passageMapper;

  @Override
  public Long commentPassage(CommentDTO commentDTO) {
    LoginUserVO loginUserVO = UserHolder.getUser();
    if (loginUserVO == null) {
      return null;
    }
    Long userId = loginUserVO.getUserId();
    Comment comment = new Comment();
    BeanUtil.copyProperties(commentDTO, comment);
    comment.setPassageId(Long.valueOf(commentDTO.getPassageId()));
    comment.setCommentUserId(userId);
    comment.setCommentTime(new Date(commentDTO.getCommentTime()));
    //todo 事务一致性
    commentMapper.insertComment(comment);
    if (comment.getCommentId() == null) {
      throw new BusinessException(ErrorCode.OPERATION_ERROR, ErrorInfo.COMMENT_ERROR);
    }
    Boolean b = passageMapper.addCommentNum(comment.getPassageId());
    if (!b) {
      throw new BusinessException(ErrorCode.OPERATION_ERROR, ErrorInfo.UPDATE_ERROR);
    }
    log.info("insert comment：" + comment);
    log.info("update passage_Table viewNum+1");
    //拿到生成的commentId
    return comment.getCommentId();
  }

  /**
   * avatar name ipAddress
   *
   * @param authorId
   * @param passageId
   * @return
   */
  @Override
  public List<CommentVO> getCommentByPassageId(Long authorId, Long passageId) {
    List<Comment> comments = commentMapper.selectList(
        new LambdaQueryWrapper<Comment>().eq(Comment::getPassageId, passageId)
            .eq(Comment::getAuthorId, authorId));
    List<CommentVO> commentVOList = getCommentVOList(comments);
    if (commentVOList.isEmpty()) {
      return commentVOList;
    }
    //设置评论的用户头像、ip地址、用户名
    getCommentUserInfo(commentVOList);
    //判断用户是否登录，登录就判断该用户是否可以自己的删除评论
    LoginUserVO loginUserVO = UserHolder.getUser();
    if (loginUserVO == null) {
      return commentVOList;
    }
    Long userId = loginUserVO.getUserId();
    //作者拥有对自己文章的所有评论的删除权
    if (Objects.equals(authorId, userId)) {
      commentVOList.forEach((commentVO -> {
        commentVO.setCanDelete(true);
      }));
      return commentVOList;
    }
    //管理员拥有对所有文章的所有评论的删除权
    if ("admin".equals(loginUserVO.getRole())) {
      commentVOList.forEach((commentVO -> {
        commentVO.setCanDelete(true);
      }));
      return commentVOList;
    }
    //不是管理、不是文章作者，那么只能删除自己的评论
    commentVOList.forEach((commentVO -> {
      if (Objects.equals(commentVO.getCommentUserId(), userId)) {
        commentVO.setCanDelete(true);
      }
    }));
    return commentVOList;
  }

  //根据评论的commentUserId获取评论用户的头像、userName等
  public void getCommentUserInfo(List<CommentVO> commentVOS) {
    List<Long> userIdList = commentVOS.stream().map(CommentVO::getCommentUserId)
        .collect(Collectors.toList());
    List<CommentUserInfoVO> commentUserInfoVOS = userMapper.getCommentUserInfoByUserIdList(
        userIdList);
    //将 commentUserInfoVOS 转换为一个以 userId 为键的 Map，这样在遍历 commentVOS 时，可以通过 Map.get(commentUserId) 快速查找对应的 CommentUserInfoVO，
    // 避免了每次都进行 O(n) 的查找操作，性能上有显著提升，尤其是数据量大的时候。
    Map<Long, CommentUserInfoVO> userInfoMap = commentUserInfoVOS.stream()
        .collect(Collectors.toMap(CommentUserInfoVO::getUserId, userInfo -> userInfo));

    commentVOS.forEach((commentVO) -> {
      Long commentUserId = commentVO.getCommentUserId();

      // 从 Map 中获取对应的 CommentUserInfoVO
      CommentUserInfoVO commentUserInfoVO = userInfoMap.get(commentUserId);

      // 如果找到了对应的用户信息，复制属性
      if (commentUserInfoVO != null) {
        BeanUtil.copyProperties(commentUserInfoVO, commentVO,
            CopyOptions.create().ignoreNullValue());
      }
    });
  }

  //todo 事务
  @Override
  public Boolean deleteComment(DeleteCommentDTO deleteCommentDTO) {
    Long commentId = deleteCommentDTO.getCommentId();
    String passageId = deleteCommentDTO.getPassageId();

    int i = commentMapper.deleteById(commentId);
    if (i == 0) {
      throw new BusinessException(ErrorCode.OPERATION_ERROR, ErrorInfo.DELETE_ERROR);
    }
    Boolean b = passageMapper.subCommentNum(Long.valueOf(passageId));
    if (b) {
      return true;
    }
    throw new BusinessException(ErrorCode.OPERATION_ERROR, ErrorInfo.DELETE_ERROR);
  }

  @Override
  public Page<List<CommentVO>> getComments(AdminCommentPageRequest adminCommentPageRequest) {
    int currentPage = adminCommentPageRequest.getCurrentPage();
    int pageSize = adminCommentPageRequest.getPageSize();
    Long commentId = adminCommentPageRequest.getCommentId();
    Long commentUserId = adminCommentPageRequest.getCommentUserId();
    Long authorId = adminCommentPageRequest.getAuthorId();
    Long passageId = adminCommentPageRequest.getPassageId();
    Date endTime = adminCommentPageRequest.getEndTime();
    Date startTime = adminCommentPageRequest.getStartTime();
    Page<Comment> commentPage = new Page<>(currentPage, pageSize);
    Page<Comment> page = page(commentPage,
        new LambdaQueryWrapper<Comment>().eq(commentId != null, Comment::getCommentId, commentId)
            .eq(commentUserId != null, Comment::getCommentUserId, commentUserId)
            .eq(authorId != null, Comment::getAuthorId, authorId)
            .eq(passageId != null, Comment::getPassageId, passageId)
            .lt(endTime != null, Comment::getCommentTime, endTime)
            .gt(startTime != null, Comment::getCommentTime, startTime)
    );
    List<Comment> records = page.getRecords();
    List<CommentVO> commentVOList = getCommentVOList(records);
    if (!commentVOList.isEmpty()) {
      getCommentUserInfo(commentVOList);
    }
    long total = page.getTotal();
    Page<List<CommentVO>> listPage = new Page<>(currentPage, pageSize);
    listPage.setRecords(Collections.singletonList(commentVOList));
    listPage.setTotal(total);
    return listPage;
  }

  public List<CommentVO> getCommentVOList(List<Comment> records) {
    List<CommentVO> commentVOList = new ArrayList<>();
    records.forEach(comment -> {
      CommentVO commentVO = new CommentVO();
      BeanUtils.copyProperties(comment, commentVO);
      commentVOList.add(commentVO);
    });
    return commentVOList;
  }


}




