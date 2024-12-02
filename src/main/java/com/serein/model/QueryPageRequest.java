package com.serein.model;

import lombok.Data;

/**
 * @Author:懒大王Smile
 * @Date: 2024/11/17
 * @Time: 22:19
 * @Description:
 */

@Data
public class QueryPageRequest {
    //默认的页码和页大小
    private int currentPage=1;
    private int pageSize=5;
}
