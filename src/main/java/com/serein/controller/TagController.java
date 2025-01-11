package com.serein.controller;

import com.serein.model.vo.tagVO.TagVO;
import com.serein.service.TagsService;
import com.serein.util.BaseResponse;
import com.serein.util.ResultUtil;
import java.util.List;
import javax.annotation.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Author:懒大王Smile
 * @Date: 2024/12/8
 * @Time: 16:33
 * @Description:
 */

@RestController
@RequestMapping("/tag")
public class TagController {

  @Resource
  private  TagsService tagsService;


  @GetMapping("/getRandomTags")
  public BaseResponse<List<TagVO>> getRandomTags() {
    List<TagVO> tagVOList = tagsService.getRandomTags();
    return ResultUtil.success(tagVOList);
  }

}
