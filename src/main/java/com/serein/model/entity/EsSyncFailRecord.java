package com.serein.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * mysql同步数据到ES失败记录表
 * @TableName es_sync_fail_record
 */
@TableName(value ="es_sync_fail_record")
@Data
public class EsSyncFailRecord implements Serializable {
    /**
     * 同步失败的文章id
     */
    @TableId(type = IdType.AUTO)
    private Long passageId;

    /**
     * 
     */
    private Date createTime;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}