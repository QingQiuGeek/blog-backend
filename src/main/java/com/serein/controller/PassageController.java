package com.serein.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.serein.constants.ErrorCode;
import com.serein.constants.ErrorInfo;
import com.serein.constants.SearchType;
import com.serein.exception.BusinessException;
import com.serein.model.request.QueryPageRequest;
import com.serein.model.dto.passageDTO.ParentPassageDTO;
import com.serein.model.request.SearchPassageRequest;
import com.serein.model.vo.passageVO.EditPassageVO;
import com.serein.model.vo.passageVO.PassageContentVO;
import com.serein.model.vo.passageVO.PassageInfoVO;
import com.serein.model.vo.passageVO.PassageTitleVO;
import com.serein.service.PassageService;
import com.serein.util.BaseResponse;
import com.serein.util.IPUtil;
import com.serein.util.ResultUtil;
import java.util.List;
import javax.annotation.Resource;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
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
 * @Description:
 */

@RestController
@RequestMapping("/passage")
public class PassageController {

  @Resource
  private  PassageService passageService;


  /*
   * top7 爆款文章
   * */
  @GetMapping("/topPassages")
  public BaseResponse<List<PassageTitleVO>> getTopPassages() {
    List<PassageTitleVO> topPassages = passageService.getTopPassages();
    return ResultUtil.success(topPassages);
  }

  /*
   * 点赞文章
   * */
  @PutMapping("/thumb/{passageId}")
  public BaseResponse<Boolean> thumbPassage(@PathVariable String passageId) {
    Boolean aBoolean = passageService.thumbPassage(Long.valueOf(passageId));
    return ResultUtil.success(aBoolean);
  }

  /*
   * 收藏文章
   * */
  @PutMapping("/collect/{passageId}")
  public BaseResponse<Boolean>collectPassage(@PathVariable String passageId) {
    Boolean aBoolean = passageService.collectPassage(Long.valueOf(passageId));
    return ResultUtil.success(aBoolean);
  }

  /**
   * @param queryPageRequest
   * @return
   */
  @PostMapping("/homePassageList")
  public BaseResponse<Page<List<PassageInfoVO>>> getHomePassageList(
      @RequestBody QueryPageRequest queryPageRequest) {
    Page<List<PassageInfoVO>> newPassageList = passageService.getHomePassageList(queryPageRequest);
    return ResultUtil.success(newPassageList);
  }


  /**
   * 搜索文章
   *
   * @param
   * @return
   */
  @PostMapping("/search")
  public BaseResponse<Page<List<PassageInfoVO>>> searchPassage(
      @RequestBody SearchPassageRequest searchPassageRequest) {
    IPUtil.isHotIp();
    String searchType = searchPassageRequest.getSearchType();
    if (searchType.isBlank()) {
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
        listPage = passageService.searchPassageFromES(searchPassageRequest);
        break;
    }
    return ResultUtil.success(listPage);
  }


  /**
   * 根据文章id搜索文章Content
   *
   * @param pid
   * @return
   */
  @GetMapping("/content/{uid}/{pid}")
  public BaseResponse<PassageContentVO> getPassageContentByPassageId(@PathVariable Long uid,
      @PathVariable String pid) {
    PassageContentVO passageContent = passageService.getPassageContentByPassageId(uid,
        Long.valueOf(pid));
    return ResultUtil.success(passageContent);
  }

  /**
   * 根据用户id搜索文章列表，获取用户其他文章
   *
   * @param uid
   * @return
   */
  @GetMapping("/otherPassages/{uid}")
  public BaseResponse<List<PassageTitleVO>> getOtherPassagesByUserId(@PathVariable Long uid) {
    List<PassageTitleVO> PassageTitleVOList = passageService.getOtherPassagesByUserId(uid);
    return ResultUtil.success(PassageTitleVOList);
  }

  /**
   * 文章详情页 获取文章点赞收藏等信息
   *
   * @param pid
   * @return
   */
  @GetMapping("/passageInfo/{pid}")
  public BaseResponse<PassageInfoVO> getPassageInfo(@PathVariable String pid) {
    PassageInfoVO passageInfo = passageService.getPassageInfoByPassageId(Long.valueOf(pid));
    return ResultUtil.success(passageInfo);
  }

  /**
   * 获取文章的编辑内容，比如编辑器页面刷新，重新获取文章内容
   * @param pid
   * @return
   */
  @GetMapping("/editPassage/{pid}")
  public BaseResponse<EditPassageVO> getEditPassage(@PathVariable String pid) {
    EditPassageVO editPassageVO = passageService.getEditPassageByPassageId(Long.valueOf(pid));
    return ResultUtil.success(editPassageVO);
  }

  /**
   * 文章状态status 0草稿  1待审核  2已发布  3驳回
   * <p>
   * 文章操作type 0初次保存和修改  2立刻发布  4定时发布
   *
   * @param parentPassageDTO
   * @return
   */
  @PostMapping("/save")
  public BaseResponse<String> addPassage(@RequestBody ParentPassageDTO parentPassageDTO) {
    Long passageId = null;
//    有passageId说明就是更新，那么就进行更新
    if (StringUtils.isNotBlank(parentPassageDTO.getPassageId())) {
        passageId = passageService.updatePassage(parentPassageDTO);
        return ResultUtil.success(passageId.toString());
    }
    //没有passageId那么就是初次保存、立刻发布、定时发布，具体哪个根据type判断
    passageId = passageService.addPassage(parentPassageDTO);
    return ResultUtil.success(passageId.toString());
  }


  /**
   * 添加文章封面
   *
   * @param
   * @return
   */
  @PostMapping("/uploadPassageCover")
  public BaseResponse<String> uploadPassageCover(@RequestParam("file") MultipartFile file) {
    String coverUrl = passageService.uploadPassageCover(file);
    return ResultUtil.success(coverUrl);
  }

  /**
   * 添加文章内容图片
   *
   * @param
   * @return
   */
  @PostMapping("/uploadPassageImg")
  public BaseResponse<String> uploadPassageImg(@RequestParam("file") MultipartFile file) {
    String imgUrl = passageService.uploadPassageImg(file);
    return ResultUtil.success(imgUrl);
  }


  /**
   * 根据文章id删除文章,管理和用户公用
   *
   * @param passageId
   * @return
   */
  @DeleteMapping("/delete/{passageId}")
  public BaseResponse<Boolean> deleteByPassageId(@PathVariable Long passageId) {
    boolean b = passageService.deleteByPassageId(passageId);
    return ResultUtil.success(b);
  }


  /**
   * 返回文章私密状态 0私密 1公开
   * @param passageId
   * @return
   */
  @GetMapping("/setPrivate/{passageId}")
  public BaseResponse<Boolean> setPassagePrivate(@PathVariable Long passageId){
    boolean b=passageService.setPassagePrivate(passageId);
    return ResultUtil.success(b);
  }

}
