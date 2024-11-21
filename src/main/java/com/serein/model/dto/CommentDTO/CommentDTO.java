package com.serein.model.dto.CommentDTO;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Date;


@Data
public class CommentDTO implements Serializable {


    /**
     * 评论的内容
     */
    private String content;

    /**
     * 评论的文章id 
     */
    private String passageId;

    /**
     * 评论时间
     */
    private Long commentTime;


    private static final long serialVersionUID = 1L;
}