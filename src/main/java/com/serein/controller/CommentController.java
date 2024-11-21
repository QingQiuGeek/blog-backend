package com.serein.controller;

import com.serein.model.dto.CommentDTO.CommentDTO;
import com.serein.model.vo.CommentVO.CommentVO;
import com.serein.service.CommentService;
import com.serein.service.PassageService;
import com.serein.utils.BaseResponse;
import com.serein.utils.ResultUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @Author:懒大王Smile
 * @Date: 2024/9/12
 * @Time: 22:25
 * @Description:
 */

@RestController
@RequestMapping("/comment")
public class CommentController {


   @Autowired
    CommentService commentService;
    /*
     * 评论文章
     * */
    @PostMapping("")
    public BaseResponse<Long> commentPassage(@RequestBody CommentDTO commentDTO){
        Long cid = commentService.commentPassage(commentDTO);
        return  ResultUtils.success(cid);
    }

    /*
     * 获取文章评论
     * */
    @GetMapping("/{authorId}/{passageId}")
    public BaseResponse<List<CommentVO>> getCommentByPassageId(@PathVariable Long authorId, @PathVariable String passageId){
        List<CommentVO> commentVOList  = commentService.getCommentByPassageId(authorId,Long.valueOf(passageId));
        return  ResultUtils.success(commentVOList);
    }

    /*
     * 删除评论
     * */
    @PutMapping("/delete/{commentId}")
    public BaseResponse<Boolean> deleteComment(@PathVariable Long commentId){
        Boolean aBoolean = commentService.deleteComment(commentId);
        return  ResultUtils.success(aBoolean);
    }
}
