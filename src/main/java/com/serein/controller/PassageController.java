package com.serein.controller;

import com.serein.constants.ErrorCode;
import com.serein.constants.ErrorInfo;
import com.serein.domain.dto.AddPassageDTO;
import com.serein.domain.dto.PassageDTO;
import com.serein.domain.entity.Passage;
import com.serein.exception.BusinessException;
import com.serein.service.PassageService;
import com.serein.utils.ResultUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @Author: 懒大王Smile
 * @Date: 2024/9/12
 * @Time: 22:24
 * @Description:
 */

@Api(tags = "文章模块")
@RestController
@RequestMapping("/passage")
public class PassageController {

    @Autowired
    PassageService passageService;

    @ApiOperation(value = "获取最新文章列表")
    @GetMapping("/hotList/{current}")
    public ResultUtils GetNewPassageList(@PathVariable Long current){
        return passageService.getNewPassageList(current);
    }

    @ApiOperation(value = "关键字搜索文章")
    @GetMapping("/search/{searchText}")
    public ResultUtils SearchPassageByText(@PathVariable String searchText){
        return passageService.searchPassageByText(searchText);
    }

    @ApiOperation(value = "根据文章id查询文章")
    @GetMapping("/{passageId}")
    public ResultUtils GetPassageByPassageId(@PathVariable Long passageId){
        return passageService.getPassageByPassageId(passageId);
    }

    @ApiOperation(value = "根据用户id查询文章")
    @GetMapping("/")
    public ResultUtils GetPassageByUserId(@RequestParam Long userId){
        return passageService.getPassageByUserId(userId);
    }

    @ApiOperation(value = "发布文章")
    @PostMapping("/add")
    public ResultUtils AddPassage(@RequestBody AddPassageDTO addpassageDTO){
        return passageService.addPassage(addpassageDTO);
    }

    @ApiOperation(value = "删除文章")
    @DeleteMapping("/delete/{passageId}")
    public ResultUtils DeleteByPassageId(@PathVariable Long passageId){
        boolean b = passageService.removeById(passageId);
        if (b){
            return ResultUtils.ok("删除文章成功");
        }
        throw new BusinessException(ErrorCode.PARAMS_ERROR, ErrorInfo.DELETE_ERROR);
    }


    @ApiOperation(value = "更新文章")
    @PostMapping("/update")
    public ResultUtils UpdateByPassageId(@RequestBody PassageDTO passageDTO){
        return passageService.updatePassage(passageDTO);
    }



}
