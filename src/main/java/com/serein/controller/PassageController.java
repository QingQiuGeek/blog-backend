package com.serein.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.serein.model.QueryPageRequest;
import com.serein.model.dto.PassageDTO.AddPassageDTO;
import com.serein.model.dto.PassageDTO.SearchPassageDTO;
import com.serein.model.dto.PassageDTO.UpdatePassageDTO;
import com.serein.model.vo.PassageVO.PassageContentVO;
import com.serein.model.vo.PassageVO.PassageInfoVO;
import com.serein.model.vo.PassageVO.PassageTitleVO;
import com.serein.service.PassageService;
import com.serein.util.BaseResponse;
import com.serein.util.ResultUtil;
import java.util.List;
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

  @Autowired
  PassageService passageService;


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
  public BaseResponse<Boolean> collectPassage(@PathVariable String passageId) {
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
  @PostMapping("/search/text")
  public BaseResponse<List<PassageInfoVO>> searchFromESByText(
      @RequestBody SearchPassageDTO searchPassageDTO) {
    List<PassageInfoVO> passageVOList = passageService.searchFromESByText(searchPassageDTO);
    return ResultUtil.success(passageVOList);
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
   * 根据用户id搜索文章列表 todo 分页查询
   *
   * @param uid
   * @return
   */
  @GetMapping("/otherPassages/{uid}")
  public BaseResponse<List<PassageTitleVO>> getPassageByUserId(@PathVariable Long uid) {
    List<PassageTitleVO> PassageTitleVOList = passageService.getPassageByUserId(uid);
    return ResultUtil.success(PassageTitleVOList);
  }

  /*
  * 获取文章信息 collectNum,
   viewNum,
   commentNum,
   thumbNum,
   isCollect,
   isThumb,
   accessTime
   title,summary这些数据在主页已经存在，那么传给passageDetails复用
   */
  @GetMapping("/passageInfo/{pid}")
  public BaseResponse<PassageInfoVO> getPassageInfo(@PathVariable String pid) {
    PassageInfoVO passageInfo = passageService.getPassageInfoByPassageId(Long.valueOf(pid));
    return ResultUtil.success(passageInfo);
  }

  /**
   * 添加文章
   *
   * @param addpassageDTO
   * @return
   */
  @PostMapping("/add")
  public BaseResponse<Long> addPassage(@RequestBody AddPassageDTO addpassageDTO) {
    Long passageId = passageService.addPassage(addpassageDTO);
    return ResultUtil.success(passageId);
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
   * 根据文章id删除文章
   * todo 删除了文章还要删除评论，收藏、点赞等，删除数据库和redis
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
   * 用户可以更新自己的文章内容 根据文章id更新文章
   *
   * @param updatePassageDTO
   * @return
   */
  @PostMapping("/update")
  public BaseResponse<Boolean> updateByPassageId(@RequestBody UpdatePassageDTO updatePassageDTO) {
    Boolean aBoolean = passageService.updatePassage(updatePassageDTO);
    return ResultUtil.success(aBoolean);
  }

}
