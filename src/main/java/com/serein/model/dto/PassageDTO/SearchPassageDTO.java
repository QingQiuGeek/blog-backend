package com.serein.model.dto.PassageDTO;

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

  //* todo 修改了passage的标签，这里也要改

  private List<String> pTags;
}
