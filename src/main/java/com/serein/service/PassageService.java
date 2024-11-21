package com.serein.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.serein.model.PageQueryPassage;
import com.serein.model.dto.CommentDTO.CommentDTO;
import com.serein.model.dto.passageDTO.AddPassageDTO;
import com.serein.model.dto.passageDTO.SearchPassageDTO;
import com.serein.model.dto.passageDTO.UpdatePassageDTO;
import com.serein.model.entity.Passage;
import com.serein.model.vo.CommentVO.CommentVO;
import com.serein.model.vo.PassageVO.PassageContentVO;
import com.serein.model.vo.PassageVO.PassageInfoVO;

import java.util.List;

/**
* @author 懒大王Smile
* @description 针对表【passage(文章表)】的数据库操作Service
* @createDate 2024-09-12 22:19:13
*/
public interface PassageService extends IService<Passage> {

    Page<List<PassageInfoVO>> getIndexPassageList(PageQueryPassage pageQueryPassage);

    List<PassageInfoVO> searchFromESByText(SearchPassageDTO searchPassageDTO);

    List<PassageInfoVO> getPassageByUserId(Long userId);

    Long addPassage(AddPassageDTO addPassageDTO);

    Boolean updatePassage(UpdatePassageDTO updatePassageDTO);

    PassageInfoVO getPassageInfoByPassageId(Long passageId);

    Boolean thumbPassage(Long passageId);

    Boolean collectPassage(Long passageId);

    List<PassageInfoVO> getTopCollects();

    PassageContentVO getPassageContentByPassageId(Long uid, Long pid);


}
