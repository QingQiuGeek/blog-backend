package com.serein.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.serein.model.QueryPageRequest;
import com.serein.model.dto.PassageDTO.AddPassageDTO;
import com.serein.model.dto.PassageDTO.SearchPassageDTO;
import com.serein.model.dto.PassageDTO.UpdatePassageDTO;
import com.serein.model.entity.Passage;
import com.serein.model.request.PassageRequest.AdminPassageQueryPageRequest;
import com.serein.model.vo.PassageVO.AdminPassageVO;
import com.serein.model.vo.PassageVO.PassageContentVO;
import com.serein.model.vo.PassageVO.PassageInfoVO;
import com.serein.model.vo.PassageVO.PassageTitleVO;
import java.util.List;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author 懒大王Smile
 * @description 针对表【passage(文章表)】的数据库操作Service
 * @createDate 2024-09-12 22:19:13
 */
public interface PassageService extends IService<Passage> {

  Page<List<PassageInfoVO>> getHomePassageList(QueryPageRequest queryPageRequest);

  List<PassageInfoVO> searchFromESByText(SearchPassageDTO searchPassageDTO);

  List<PassageTitleVO> getPassageByUserId(Long userId);

  Long addPassage(AddPassageDTO addPassageDTO);

  Boolean updatePassage(UpdatePassageDTO updatePassageDTO);

  PassageInfoVO getPassageInfoByPassageId(Long passageId);

  Boolean thumbPassage(Long passageId);

  Boolean collectPassage(Long passageId);

  List<PassageTitleVO> getTopPassages();

  PassageContentVO getPassageContentByPassageId(Long uid, Long pid);


  String uploadPassageCover(MultipartFile img);

  String uploadPassageImg(MultipartFile img);

  Page<List<AdminPassageVO>> getPassageList(
      AdminPassageQueryPageRequest adminPassageQueryPageRequest);

  Boolean rejectPassage(Long passageId);

  Boolean publishPassage(Long passageId);

  boolean deleteByPassageId(Long passageId);
}
