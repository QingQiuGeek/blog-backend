package com.serein.controller;

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

@Api(value = "文章模块")
@RestController
@RequestMapping("/passage")
public class PassageController {

    @Autowired
    PassageService passageService;

    @ApiOperation(value = "根据id查询文章")
    @GetMapping("/{id}")
    public ResultUtils GetPassageById(@RequestParam Long id){
        return ResultUtils.ok("根据id获取文章成功",passageService.getById(id));
    }

    @ApiOperation(value = "获取最新文章列表")
    @GetMapping("/hotList")
    public ResultUtils GetNewPassageList(){
        return passageService.getNewPassageList();
    }
}
