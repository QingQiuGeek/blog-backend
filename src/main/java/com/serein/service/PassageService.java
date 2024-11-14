package com.serein.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.serein.model.dto.passageDTO.AddPassageDTO;
import com.serein.model.dto.passageDTO.SearchPassageDTO;
import com.serein.model.dto.passageDTO.UpdatePassageDTO;
import com.serein.model.entity.Passage;
import com.serein.model.vo.PassageVO.PassageVO;
import com.serein.utils.BaseResponse;

import java.util.List;

/**
* @author 懒大王Smile
* @description 针对表【passage(文章表)】的数据库操作Service
* @createDate 2024-09-12 22:19:13
*/
public interface PassageService extends IService<Passage> {

    List<PassageVO> getIndexPassageList(int current);

    List<PassageVO> searchFromESByText(SearchPassageDTO searchPassageDTO);

    List<PassageVO> getPassageByUserId(Long userId);

    Long addPassage(AddPassageDTO addPassageDTO);

    Boolean updatePassage(UpdatePassageDTO updatePassageDTO);

    PassageVO getPassageByPassageId(Long passageId);

    Boolean thumbPassage(Long passageId);

    Boolean collectPassage(Long passageId);


    List<PassageVO> getTopCollects();

    BaseResponse<Integer> getCollectNums();




    PassageVO getPassageContentByPassageId(Long pid);
}
