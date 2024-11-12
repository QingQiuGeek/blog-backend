package com.serein.controller;

import com.serein.annotation.AuthCheck;
import com.serein.constants.ErrorCode;
import com.serein.constants.ErrorInfo;
import com.serein.model.dto.passageDTO.AddAndUpdatePassageDTO;
import com.serein.exception.BusinessException;
import com.serein.model.dto.passageDTO.SearchPassageDTO;
import com.serein.model.vo.PassageVO.PassageVO;
import com.serein.service.PassageService;
import com.serein.utils.BaseResponse;
import com.serein.utils.ResultUtils;
import net.bytebuddy.asm.Advice;
import org.elasticsearch.action.search.SearchRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.instrument.classloading.ReflectiveLoadTimeWeaver;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
    * 我的文章，list.size即为我的文章数量
    * */
    @AuthCheck
    @GetMapping("/myPassages")
    public BaseResponse<List<PassageVO>> myPassages(){
        List<PassageVO> passageVOList = passageService.myPassages();
        return ResultUtils.success(passageVOList);
    }

    /*
    * 我的文章总收藏量
    * */
    @GetMapping("/CollectNums")
    public BaseResponse<Integer> getCollectNums(){
        return passageService.getCollectNums();
    }

    /*
    * top7 爆款文章
    * */
    @GetMapping("/topCollects")
    public BaseResponse<List<PassageVO>> getTopCollects(){
        List<PassageVO> hotCollects = passageService.getTopCollects();
        return ResultUtils.success(hotCollects);
    }

    /*
    * 点赞文章
    * */
    @PutMapping("/thumb/{passageId}")
    public BaseResponse<Boolean> thumbPassage(@PathVariable Long passageId){
        Boolean aBoolean = passageService.thumbPassage(passageId);
        return  ResultUtils.success(aBoolean);
    }

    /*
    * 收藏文章
    * */
    @PutMapping("/collect/{passageId}")
    public BaseResponse<Boolean> collectPassage(@PathVariable Long passageId){
        Boolean aBoolean = passageService.collectPassage(passageId);
        return  ResultUtils.success(aBoolean);
    }

    /**
     * 博客首页获取文章列表 todo 分页
     * @param current
     * @return
     */

    @GetMapping("/indexPassageList/{current}")
    public BaseResponse<List<PassageVO>> getIndexPassageList(@PathVariable int current){
        List<PassageVO> newPassageList = passageService.getIndexPassageList(current);
        return ResultUtils.success(newPassageList);
    }

    /**
     * 搜索文章
     * @param
     * @return
     */
    @PostMapping("/search/text")
    public BaseResponse<List<PassageVO>> searchFromESByText(@RequestBody SearchPassageDTO searchPassageDTO){
        List<PassageVO> passageVOList = passageService.searchFromESByText(searchPassageDTO);
        return ResultUtils.success(passageVOList);
    }

    /**
     * 根据文章id搜索文章
     * @param pid
     * @return
     */
    @GetMapping("/search/pid/{pid}")
    public BaseResponse<PassageVO> getPassageByPassageId(@PathVariable Long pid){
        PassageVO passageVO = passageService.getPassageByPassageId(pid);
        return ResultUtils.success(passageVO);
    }

    /**
     * 根据用户id搜索文章列表 todo 分页查询
     * @param uid
     * @return
     */
    @GetMapping("/search/uid/{uid}")
    public BaseResponse<List<PassageVO>> getPassageByUserId(@PathVariable Long uid){
        List<PassageVO> passageVOList = passageService.getPassageByUserId(uid);
        return ResultUtils.success(passageVOList);
    }

   /*
   * 获取文章详情
   * */
    @GetMapping("/passageDetails/{pid}")
    public BaseResponse<PassageVO> getPassageDetails(@PathVariable String pid) {
        PassageVO passageDetails = passageService.getPassageByPassageId(Long.valueOf(pid));
        return ResultUtils.success(passageDetails);
    }

    /**
     * 添加文章
     * @param addpassageDTO
     * @return
     */
    @PostMapping("/add")
    public BaseResponse<Long> addPassage(@RequestBody AddAndUpdatePassageDTO addpassageDTO){
        Long passageId = passageService.addPassage(addpassageDTO);
        return ResultUtils.success(passageId);

    }

    /**
     * 根据文章id删除文章
     * @param passageId
     * @return
     */
    @DeleteMapping("/delete/{passageId}")
    public BaseResponse<Boolean> deleteByPassageId(@PathVariable Long passageId){
        boolean b = passageService.removeById(passageId);
        if (b){
            return ResultUtils.success(b);
        }
        throw new BusinessException(ErrorCode.OPERATION_ERROR, ErrorInfo.DELETE_ERROR);
    }


    /**
     * 用户可以更新自己的文章内容
     * 根据文章id更新文章
     * @param updatePassageDTO
     * @return
     */
    @PostMapping("/update")
    public BaseResponse<Boolean> updateByPassageId(@RequestBody AddAndUpdatePassageDTO updatePassageDTO){
        Boolean aBoolean = passageService.updatePassage(updatePassageDTO);
        return  ResultUtils.success(aBoolean);
    }

}
