package com.serein.domain.dto;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

/**
 * @Author:懒大王Smile
 * @Date: 2024/9/19
 * @Time: 17:47
 * @Description:
 */

@NoArgsConstructor
@Data
public class PassageDTO implements Serializable {

    /**
     * 文章id
     */
    @TableId(type = IdType.AUTO)
    private Long passageId;

    /**
     * 文章标题
     */
    private String title;

    /**
     * 文章内容
     */
    private String content;

    /**
     * 预览图URL
     */
    private String thumbnail;

    /**
     * 内容摘要
     */
    private String summary;

    /**
     * 文章所属类别
     */
    private Integer categoryId;


    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}
