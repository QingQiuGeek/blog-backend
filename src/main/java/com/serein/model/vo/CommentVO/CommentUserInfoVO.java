package com.serein.model.vo.CommentVO;

import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;

import java.io.Serializable;

/**
 * @Author:懒大王Smile
 * @Date: 2024/11/21
 * @Time: 12:28
 * @Description:
 */

@Data
public class CommentUserInfoVO implements Serializable {

    /**
     * 评论的用户名
     */
    private String userName;

    /**
     * 评论的用户id
     */
    private Long userId;

    /**
     * 评论的用户头像
     */
    private String avatarUrl;

    /**
     * 评论的用户ip
     */

    private String ipAddress;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}
