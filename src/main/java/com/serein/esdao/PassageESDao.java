package com.serein.esdao;

import com.serein.model.dto.passageDTO.PassageESDTO;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * @Author:懒大王Smile
 * @Date: 2024/10/24
 * @Time: 22:41
 * @Description: ES DAO层
 */

public interface PassageESDao extends ElasticsearchRepository<PassageESDTO, Long> {

}
