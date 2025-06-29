package com.serein.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.serein.constants.ErrorCode;
import com.serein.constants.ErrorInfo;
import com.serein.constants.SearchType;
import com.serein.exception.BusinessException;
import com.serein.model.dto.passageDTO.ParentPassageDTO;
import com.serein.model.request.QueryPageRequest;
import com.serein.model.request.SearchPassageRequest;
import com.serein.model.vo.passageVO.EditPassageVO;
import com.serein.model.vo.passageVO.PassageContentVO;
import com.serein.model.vo.passageVO.PassageInfoVO;
import com.serein.model.vo.passageVO.PassageTitleVO;
import com.serein.service.PassageService;
import com.serein.util.BR;
import com.serein.util.IPUtil;
import com.serein.util.R;
import jakarta.annotation.Resource;
import java.util.List;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/**
 * @Author: 懒大王Smile
 * @Date: 2024/9/12
 * @Time: 22:24
 * @Description: 文章Controller
 */

@RestController
@RequestMapping("/passage")
public class PassageController {

  @Resource
  private PassageService passageService;


  /*
   * top7 爆款文章
   * */
  @GetMapping("/topPassages")
  public BR<List<PassageTitleVO>> getTopPassages() {
    return R.ok(passageService.getTopPassages());
  }

  /*
   * 点赞文章
   * */
  @PutMapping("/thumb/{passageId}")
  public BR<Boolean> thumbPassage(@PathVariable("passageId") String passageId) {
    return R.ok(passageService.thumbPassage(Long.valueOf(passageId)));
  }

  /*
   * 收藏文章
   * */
  @PutMapping("/collect/{passageId}")
  public BR<Boolean> collectPassage(@PathVariable("passageId") String passageId) {
    return R.ok(passageService.collectPassage(Long.valueOf(passageId)));
  }

  /**
   * @param queryPageRequest
   * @return
   */
  @PostMapping("/homePassageList")
  public BR<Page<List<PassageInfoVO>>> getHomePassageList(
      @RequestBody QueryPageRequest queryPageRequest) {
    return R.ok( passageService.getHomePassageList(queryPageRequest));
  }


  /**
   * 搜索文章
   *
   * @param
   * @return
   */
  @PostMapping("/search")
  public BR<Page<List<PassageInfoVO>>> searchPassage(
      @RequestBody SearchPassageRequest searchPassageRequest) {
    IPUtil.isHotIp();
    String searchType = searchPassageRequest.getSearchType();
    if (StringUtils.isBlank(searchType)) {
      throw new BusinessException(ErrorCode.PARAMS_ERROR, ErrorInfo.PARAMS_ERROR);
    }
    Page<List<PassageInfoVO>> listPage = new Page<>();
    switch (searchType) {
      //根据类别搜索，查数据库
      case SearchType.CATEGORY:
        listPage = passageService.searchPassageByCategory(searchPassageRequest);
        break;
      //根据标签搜索，查数据库
      case SearchType.TAG:
        listPage = passageService.searchPassageByTag(searchPassageRequest);
        break;
      //根据文本搜索，查ES
      case SearchType.SEARCH:
        //云服务器带不动es，就换成mysql分词了
        listPage = passageService.searchPassageFromMySQL(searchPassageRequest);
        break;
    }
    return R.ok(listPage);
  }


  /**
   * 根据文章id搜索文章Content
   *
   * @param pid
   * @return
   */
  @GetMapping("/content/{uid}/{pid}")
  public BR<PassageContentVO> getPassageContentByPassageId(@PathVariable("uid") Long uid,
      @PathVariable("pid") String pid) {
    return R.ok(passageService.getPassageContentByPassageId(uid,
        Long.valueOf(pid)));
  }

  /**
   * 根据用户id搜索文章列表，获取用户其他文章
   *
   * @param uid
   * @return
   */
  @GetMapping("/otherPassages/{uid}")
  public BR<List<PassageTitleVO>> getOtherPassagesByUserId(@PathVariable("uid") Long uid) {
    return R.ok( passageService.getOtherPassagesByUserId(uid));
  }

  /**
   * 文章详情页 获取文章点赞收藏等信息
   *
   * @param pid
   * @return
   */
  @GetMapping("/passageInfo/{pid}")
  public BR<PassageInfoVO> getPassageInfo(@PathVariable("pid") String pid) {
    return R.ok(passageService.getPassageInfoByPassageId(Long.valueOf(pid)));
  }

  /**
   * 获取文章的编辑内容，比如编辑器页面刷新，重新获取文章内容
   *
   * @param pid
   * @return
   */
  @GetMapping("/editPassage/{pid}")
  public BR<EditPassageVO> getEditPassage(@PathVariable("pid") String pid) {
    return R.ok(passageService.getEditPassageByPassageId(Long.valueOf(pid)));
  }

  /**
   * 文章状态status 0草稿  1待审核  2已发布  3驳回 立刻发布
   *
   * @param parentPassageDTO
   * @return
   */
  @PostMapping("/nowPublish")
  public BR<Boolean> nowPublish(@RequestBody ParentPassageDTO parentPassageDTO) {
      return R.ok(passageService.nowPublish(parentPassageDTO));
  }

  /**
   * 文章状态status 0草稿  1待审核  2已发布  3驳回 保存
   *
   * @param parentPassageDTO
   * @return
   */
  @PostMapping("/save")
  public BR<String> savePassage(@RequestBody ParentPassageDTO parentPassageDTO) {
   return R.ok(passageService.savePassage(parentPassageDTO).toString());

  }

  /**
   * 文章状态status 0草稿  1待审核  2已发布  3驳回 定时发布
   *
   * @param parentPassageDTO
   * @return
   */
  @PostMapping("/timePublish")
  public BR<Boolean> timePublish(@RequestBody ParentPassageDTO parentPassageDTO) {
    return R.ok(passageService.timePublish(parentPassageDTO));
  }


  /**
   * 添加文章封面
   *
   * @param
   * @return
   */
  @PostMapping("/uploadPassageCover")
  public BR<String> uploadPassageCover(@RequestParam("file") MultipartFile file) {
    return R.ok(passageService.uploadPassageCover(file));
  }

  /**
   * 添加文章内容图片
   *
   * @param
   * @return
   */
  @PostMapping("/uploadPassageImg")
  public BR<String> uploadPassageImg(@RequestParam("file") MultipartFile file) {
    return R.ok(passageService.uploadPassageImg(file));
  }


  /**
   * 根据文章id删除文章,管理和用户公用
   *
   * @param passageId
   * @return
   */
  @DeleteMapping("/delete/{passageId}")
  public BR<Boolean> deleteByPassageId(@PathVariable("passageId") Long passageId) {
    return R.ok(passageService.deleteByPassageId(passageId));
  }


  /**
   * 返回文章私密状态 0私密 1公开
   *
   * @param passageId
   * @return
   */
  @GetMapping("/setPrivate/{passageId}")
  public BR<Boolean> setPassagePrivate(@PathVariable("passageId") Long passageId) {
    return R.ok(passageService.setPassagePrivate(passageId));
  }

}
