package com.serein.controller;

import com.serein.model.vo.tagVO.TagVO;
import com.serein.service.TagsService;
import com.serein.util.BR;
import com.serein.util.R;
import jakarta.annotation.Resource;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Author:懒大王Smile
 * @Date: 2024/12/8
 * @Time: 16:33
 * @Description: 标签Controller
 */

@RestController
@RequestMapping("/tag")
public class TagController {

  @Resource
  private  TagsService tagsService;


  @GetMapping("/getRandomTags")
  public BR<List<TagVO>> getRandomTags() {
    return R.ok(tagsService.getRandomTags());
  }

}
