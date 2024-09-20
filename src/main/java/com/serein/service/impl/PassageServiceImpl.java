package com.serein.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.date.DateTime;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.serein.constants.Common;
import com.serein.constants.ErrorCode;
import com.serein.constants.ErrorInfo;
import com.serein.domain.UserHolder;
import com.serein.domain.dto.AddPassageDTO;
import com.serein.domain.dto.PassageDTO;
import com.serein.domain.entity.Passage;
import com.serein.domain.entity.User;
import com.serein.domain.vo.PassageVO;
import com.serein.exception.BusinessException;
import com.serein.service.PassageService;
import com.serein.mapper.PassageMapper;
import com.serein.utils.ResultUtils;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author 懒大王Smile
 * @description 针对表【passage(文章表)】的数据库操作Service实现
 * @createDate 2024-09-12 22:19:13
 */
@Service
public class PassageServiceImpl extends ServiceImpl<PassageMapper, Passage>
        implements PassageService {

    @Override
    public ResultUtils getNewPassageList(Long current) {

        Page<Passage> passagePage = new Page<>(current, Common.PAGE_SIZE);
        Page<Passage> pageDesc = page(passagePage, new QueryWrapper<Passage>().eq("status",2).orderByDesc("accessTime"));

        List<Passage> passageList = pageDesc.getRecords();
        if (passageList.isEmpty()) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "查询文章列表失败");
        }
        List<PassageVO> collect = passageList.stream().map(passage -> {
                    PassageVO passageVO = new PassageVO();
                    BeanUtil.copyProperties(passage, passageVO);
                    return passageVO;
                }
        ).collect(Collectors.toList());

        return ResultUtils.ok("获取最新文章列表成功",collect,pageDesc.getTotal());
    }

    //todo 文本搜索文章 待完善
    @Override
    public ResultUtils searchPassageByText(String searchText) {
        return null;
    }

    @Override
    public ResultUtils getPassageByUserId(Long userId) {
        QueryWrapper<Passage> passageQueryWrapper = new QueryWrapper<>();
        passageQueryWrapper.eq("authorId", userId);
        List<Passage> list = this.list(passageQueryWrapper);
        if (list.isEmpty()){
            return ResultUtils.ok("根据用户id查询文章失败");
        }
        List<PassageVO> collect = list.stream().map(passage ->
                {
                    PassageVO passageVO = new PassageVO();
                    BeanUtil.copyProperties(passage, passageVO);
                    return passageVO;
                }
        ).collect(Collectors.toList());
        return ResultUtils.ok("根据用户id查询文章成功",collect,Long.valueOf(collect.size()));
    }


    //todo 待测试 文章审核通过时间 √
    @Override
    public ResultUtils addPassage(AddPassageDTO addpassageDTO) {
        Passage passage = new Passage();
        BeanUtil.copyProperties(addpassageDTO,passage);
        passage.setAuthorId(UserHolder.getUser().getUserId());
        passage.setAuthorName(UserHolder.getUser().getUserName());
        //status 0草稿  1待审核 2已发布
        //前期默认已发布
        passage.setStatus(2);
        boolean save = this.save(passage);
        if (save){
            return ResultUtils.ok("发布文章成功，待审核");
        }
        throw new BusinessException(ErrorCode.RELEASED_ERROR, ErrorInfo.RELEASED_ERROR);
    }


    //todo 更新文章 待测试 √
    // todo 更新dto，专类专用 √
    // todo saveOrUpdate
    //todo 数据库连接池
    @Transactional
    @Override
    public ResultUtils updatePassage(PassageDTO passageDTO) {
        Passage passage = new Passage();
        BeanUtil.copyProperties(passageDTO,passage);
        passage.setAuthorId(UserHolder.getUser().getUserId());
        passage.setAuthorName(UserHolder.getUser().getUserName());
        //更新文章时，审核通过时间也更新
        passage.setAccessTime(new Date(System.currentTimeMillis()));
        boolean b = this.saveOrUpdate(passage);
        PassageVO passageVO = new PassageVO();
        BeanUtil.copyProperties(passage,passageVO);
        if (b){
            return ResultUtils.ok("文章更新成功",passageVO);
        }
        throw new BusinessException(ErrorCode.UPDATE_ERROR,ErrorInfo.UPDATE_ERROR);
    }

    @Override
    public ResultUtils getPassageByPassageId(Long passageId) {
        Passage passage = this.getById(passageId);
        if (passage==null){
            return ResultUtils.ok("根据文章id查询文章失败");
        }
        PassageVO passageVO = new PassageVO();
        BeanUtil.copyProperties(passage,passageVO);
        return ResultUtils.ok("根据文章id查询文章成功",passageVO);
    }

}




