package com.serein.model.dto.passageDTO;

import java.util.List;
import lombok.Data;

/**
 * @Author:懒大王Smile
 * @Date: 2024/10/24
 * @Time: 21:09
 * @Description:
 */
@Data
public class SearchPassageDTO {

  private String searchText;

  private List<String> pTags;
}
