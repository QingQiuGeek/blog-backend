package com.serein.domain;

import lombok.Data;
import org.springframework.context.annotation.Bean;

import java.util.List;

/**
 * @Author:懒大王Smile
 * @Date: 2024/9/18
 * @Time: 12:33
 * @Description: 自定义分页，我们可以直接使用mybatis-plus的分页查询返回数据给前端，
 * 但是如果分页查询出的数据需要二次加工处理才能返回给前端，那么最好把处理过的数据也包装成分页的而不是直接返回，因此需要一个自定义分页类包装数据
 */

@Data
public class CustomPage<T> {
    private Long current; // 当前页码
    private Long size; // 每页显示的记录数
    private Long total; // 总记录数
    private List<T> records; // 数据列表

    public CustomPage(Long current, Long size, Long total, List<T> records) {
        this.current = current;
        this.size = size;
        this.total = total;
        this.records = records;
    }

}
