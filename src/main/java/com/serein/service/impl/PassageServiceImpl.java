package com.serein.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.serein.constants.Common;
import com.serein.constants.ErrorCode;
import com.serein.constants.ErrorInfo;
import com.serein.exception.BusinessException;
import com.serein.mapper.PassageMapper;
import com.serein.mapper.UserCollectsMapper;
import com.serein.mapper.UserThumbsMapper;
import com.serein.model.QueryPageRequest;
import com.serein.model.UserHolder;
import com.serein.model.dto.passageDTO.AddPassageDTO;
import com.serein.model.dto.passageDTO.PassageDTO;
import com.serein.model.dto.passageDTO.PassageESDTO;
import com.serein.model.dto.passageDTO.SearchPassageDTO;
import com.serein.model.dto.passageDTO.UpdatePassageDTO;
import com.serein.model.entity.Passage;
import com.serein.model.entity.UserCollects;
import com.serein.model.entity.UserThumbs;
import com.serein.model.vo.PassageVO.PassageContentVO;
import com.serein.model.vo.PassageVO.PassageInfoVO;
import com.serein.model.vo.UserVO.LoginUserVO;
import com.serein.service.PassageService;
import com.serein.utils.FileUtil;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author 懒大王Smile
 * @description 针对表【passage(文章表)】的数据库操作Service实现
 * @createDate 2024-09-12 22:19:13
 */
@Slf4j
@Service
public class PassageServiceImpl extends ServiceImpl<PassageMapper, Passage>
    implements PassageService {

  @Autowired
  ElasticsearchRestTemplate elasticsearchRestTemplate;

  @Autowired
  StringRedisTemplate stringRedisTemplate;

  @Autowired
  UserThumbsMapper userThumbsMapper;

  @Autowired
  UserCollectsMapper userCollectsMapper;

  @Autowired
  PassageMapper passageMapper;

  @Override
  public Page<List<PassageInfoVO>> getIndexPassageList(QueryPageRequest queryPageRequest) {
    int currentPage = queryPageRequest.getCurrentPage();
    int pageSize = queryPageRequest.getPageSize();
    //首页加载文章列表时，不加载content，减少数据传输压力，提高加载速度
    Page<Passage> passagePage = new Page<>(currentPage, pageSize);
    Page<Passage> pageDesc = page(passagePage,
        new QueryWrapper<Passage>().eq("status", 2).orderByDesc("accessTime").
            select("passageId", "title", "viewNum", "authorId", "authorName", "avatarUrl",
                "thumbnail", "summary", "pTags", "commentNum", "collectNum", "thumbNum",
                "accessTime"));
    //当前页的数据
    List<Passage> passageList = pageDesc.getRecords();
    long total = pageDesc.getTotal();
    if (passageList.isEmpty()) {
      throw new BusinessException(ErrorCode.PARAMS_ERROR, "获取文章列表失败");
    }
    List<PassageInfoVO> pageInfoVOList = getPassageInfoVOList(passageList);
    // 创建一个 Page 对象返回，封装分页信息（总记录数、页码等）和当前页的数据
    Page<List<PassageInfoVO>> listPage = new Page<>(currentPage, pageSize);
    //包装成单一的list
    listPage.setRecords(Collections.singletonList(pageInfoVOList));
    //总数据数量
    listPage.setTotal(total);
    return listPage;
  }

  //主页显示
  public List<PassageInfoVO> getPassageInfoVOList(List<Passage> passageList) {
    List<PassageInfoVO> collect = passageList.stream().map(passage -> {
          PassageInfoVO passageInfoVO = new PassageInfoVO();
          BeanUtil.copyProperties(passage, passageInfoVO);
          if (!StringUtils.isBlank(passage.getPTags())) {
            //把数据库中string类型的json转换成list<String>
            List<String> pTagList = JSONUtil.toList(passage.getPTags(), String.class);
            passageInfoVO.setPTags(pTagList);
            //判断当前用户是否点赞、收藏
          }
          isThumbCollect(passageInfoVO);
          return passageInfoVO;
        }
    ).collect(Collectors.toList());
    return collect;
  }

  @Override
  public PassageInfoVO getPassageInfoByPassageId(Long passageId) {
    Passage passageInfo = passageMapper.getPassageInfo(passageId);
    List<Passage> passage = new ArrayList<>();
    passage.add(passageInfo);
    List<PassageInfoVO> passageInfoVOList = getPassageInfoVOList(passage);
    PassageInfoVO passageInfoVO = passageInfoVOList.get(0);
    isThumbCollect(passageInfoVO);
    return passageInfoVO;
  }

  //判断当前用户是否点赞或收藏该文章
  private void isThumbCollect(PassageInfoVO passageInfoVO) {
    LoginUserVO loginUserVO = UserHolder.getUser();
    if (loginUserVO == null) {
      return;
    }
    Long userId = loginUserVO.getUserId();
    String passageId = passageInfoVO.getPassageId().toString();
    String keyThumb = Common.PASSAGE_THUMB_KEY + passageId;
    Double score1 = stringRedisTemplate.opsForZSet().score(keyThumb, userId.toString());
    passageInfoVO.setIsThumb(score1 != null);
    String keyCollect = Common.PASSAGE_COLLECT_KEY + passageId;
    Double score2 = stringRedisTemplate.opsForZSet().score(keyCollect, userId.toString());
    passageInfoVO.setIsCollect(score2 != null);
  }

  //todo es搜索优化是否要passageInfoVO
  @Override
  public List<PassageInfoVO> searchFromESByText(SearchPassageDTO searchPassageDTO) {
    String searchText = searchPassageDTO.getSearchText();
    List<String> pTags = searchPassageDTO.getPTags();

    //拼接查询条件
    BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
    if (StringUtils.isNotBlank(searchText)) {
      boolQueryBuilder.should(QueryBuilders.matchQuery("title", searchText));
      boolQueryBuilder.should(QueryBuilders.matchQuery("content", searchText));
      boolQueryBuilder.should(QueryBuilders.matchQuery("summary", searchText));
      //确保至少有1个“should”条件需要匹配。
      boolQueryBuilder.minimumShouldMatch(1);
    }
    if (CollUtil.isNotEmpty(pTags)) {

      /** term：tags查询使用精确匹配，而上面的title、content、summary是analyzed搜索分析
       用于精确匹配，适合未分析（not analyzed）字段或关键词字段。
       直接查找与查询完全匹配的值，不会对输入进行分析。*/
      BoolQueryBuilder tagBoolQueryBuilder = QueryBuilders.boolQuery();
      for (String pTag : pTags) {
        tagBoolQueryBuilder.should(QueryBuilders.termQuery("pTags", pTag));
      }
      tagBoolQueryBuilder.minimumShouldMatch(1);
      boolQueryBuilder.filter(tagBoolQueryBuilder);
    }
    // 构造查询
    NativeSearchQuery searchQuery = new NativeSearchQueryBuilder().withQuery(boolQueryBuilder)
        .build();
    SearchHits<PassageESDTO> searchHits = elasticsearchRestTemplate.search(searchQuery,
        PassageESDTO.class);

    List<Passage> resourceList = new ArrayList<>();
    //从根据ES结果数据库查询
    if (searchHits.hasSearchHits()) {
      List<SearchHit<PassageESDTO>> searchHitList = searchHits.getSearchHits();
      List<Long> passageIdList = searchHitList.stream()
          .map(searchHit -> searchHit.getContent().getPassageId()).collect(Collectors.toList());
      List<Passage> passageList = baseMapper.selectBatchIds(passageIdList);
      if (passageList != null) {
        //从es中查出的数据在数据库中也要存在，否则就是无效数据
        //核对es和数据库中的数据，根据id检查es中是否有失效的数据
        Map<Long, List<Passage>> idPassageMap = passageList.stream()
            .collect(Collectors.groupingBy(Passage::getPassageId));
        passageIdList.forEach(passageId -> {
          if (idPassageMap.containsKey(passageId)) {
            resourceList.add(idPassageMap.get(passageId).get(0));
          } else {
            // 从 es 清空 db 已删除的数据
            String delete = elasticsearchRestTemplate.delete(String.valueOf(passageId),
                PassageESDTO.class);
            log.info("Delete outdated passage on ES:  {}", delete);
          }
        });
      }
    }
    return getPassageInfoVOList(resourceList);
  }

  @Override
  public List<PassageInfoVO> getPassageByUserId(Long userId) {
//    QueryWrapper<Passage> passageQueryWrapper = new QueryWrapper<>();
    LambdaQueryWrapper<Passage> passageQueryWrapper = new LambdaQueryWrapper<>();
    passageQueryWrapper.eq(Passage::getAuthorId, userId);
    List<Passage> list = this.list(passageQueryWrapper);
    if (list.isEmpty()) {
      return Collections.emptyList();
    }
    List<PassageInfoVO> collect = getPassageInfoVOList(list);
    return collect;
  }

  @Override
  public Long addPassage(AddPassageDTO addPassageDTO) {
    Passage passage = getPassage(addPassageDTO);
    //status 0草稿  1待审核 2已发布
    //前期默认已发布
    passage.setStatus(2);
    boolean save = this.save(passage);
    if (save) {
      return passage.getPassageId();
    }
    throw new BusinessException(ErrorCode.OPERATION_ERROR, ErrorInfo.RELEASED_ERROR);
  }

  @Transactional
  @Override
  public Boolean updatePassage(UpdatePassageDTO updatePassageDTO) {
    Passage passage = getPassage(updatePassageDTO);

    //更新文章时，审核通过时间在数据库中自动更新
    boolean b = this.updateById(passage);
    if (b) {
      return true;
    }
    throw new BusinessException(ErrorCode.OPERATION_ERROR, ErrorInfo.UPDATE_ERROR);
  }

  public Passage getPassage(PassageDTO passageDTO) {
    Passage passage = new Passage();
    //把list<String>标签转换成json
    BeanUtil.copyProperties(passageDTO, passage);
    if (passageDTO.getClass() == UpdatePassageDTO.class) {
      passage.setPassageId(Long.valueOf(((UpdatePassageDTO) passageDTO).getPassageId()));
    }
    List<String> tags = passageDTO.getPTags();
    if (tags != null) {
      passage.setPTags(JSONUtil.toJsonStr(tags));
    }
    passage.setAuthorId(UserHolder.getUser().getUserId());
    passage.setAuthorName(UserHolder.getUser().getUserName());
    String authorAvatar = passageMapper.getAuthorAvatar(passage.getAuthorId());
    passage.setAvatarUrl(authorAvatar);
    return passage;
  }


  @Override
  public Boolean thumbPassage(Long passageId) {

    LoginUserVO loginUserVO = UserHolder.getUser();
    if (loginUserVO == null) {
      return false;
    }
    Long userId = loginUserVO.getUserId();
    /*
     * 同一个用户对一篇文章只能点赞一次，不能重复点赞，取消点赞亦然
     * 以passageId作为key，userId为value，存入redis 的zSet集合，利用set集合元素唯一不重复的特性，存储用户是否点赞
     * */
    String key = Common.PASSAGE_THUMB_KEY + passageId;
    Double score = stringRedisTemplate.opsForZSet().score(key, userId.toString());
    if (score == null) {
      boolean b = update().setSql("thumbNum=thumbNum+1").eq("passageId", passageId).update();
      //插入用户点赞表
      UserThumbs userThumbs = UserThumbs.builder().userId(userId).passageId(passageId).build();
      int insert = userThumbsMapper.insert(userThumbs);
      //文章表和用户点赞表同时更新成功
      if (b && insert == 1) {
        stringRedisTemplate.opsForZSet().add(key, userId.toString(), System.currentTimeMillis());
      } else {
        throw new BusinessException(ErrorCode.OPERATION_ERROR, ErrorInfo.UPDATE_ERROR);
      }
    } else {
      boolean b = update().setSql("thumbNum=thumbNum-1").eq("passageId", passageId).update();
      //删除用户点赞表
      LambdaQueryWrapper<UserThumbs> queryWrapper = new LambdaQueryWrapper<>();
      queryWrapper.eq(UserThumbs::getUserId, userId).eq(UserThumbs::getPassageId, passageId);
      int delete = userThumbsMapper.delete(queryWrapper);
      if (b && delete == 1) {
        stringRedisTemplate.opsForZSet().remove(key, userId.toString());
      } else {
        throw new BusinessException(ErrorCode.OPERATION_ERROR, ErrorInfo.UPDATE_ERROR);
      }
    }
    return true;
  }

  /**
   * @param passageId
   * @return
   * @Description: 以passageId作为key，收藏该文章的userId为value存入redis
   */
  @Override
  public Boolean collectPassage(Long passageId) {
    LoginUserVO loginUserVO = UserHolder.getUser();
    if (loginUserVO == null) {
      return false;
    }
    Long userId = loginUserVO.getUserId();

    /*
     * 同一个用户对一篇文章只能收藏一次，不能重复收藏，取消收藏亦然
     * 以passageId作为key，userId为value，存入redis 的zSet集合，利用set集合元素唯一不重复的特性，存储用户是否收藏该文章
     * */
    String key = Common.PASSAGE_COLLECT_KEY + passageId;
    Double score = stringRedisTemplate.opsForZSet().score(key, userId.toString());
    if (score == null) {
      // todo 事务一致性
      boolean b = update().setSql("collectNum=collectNum+1").eq("passageId", passageId).update();
      //先插入mysql用户收藏表
      UserCollects userCollects = UserCollects.builder().userId(userId).passageId(passageId)
          .build();
      int insert = userCollectsMapper.insert(userCollects);
      if (b && insert == 1) {
        //写入redis
        stringRedisTemplate.opsForZSet().add(key, userId.toString(), System.currentTimeMillis());
      } else {
        throw new BusinessException(ErrorCode.OPERATION_ERROR, ErrorInfo.UPDATE_ERROR);
      }
    } else {
      boolean b = update().setSql("collectNum=collectNum-1").eq("passageId", passageId).update();
      //删除用户收藏表
      LambdaQueryWrapper<UserCollects> queryWrapper = new LambdaQueryWrapper<>();
      queryWrapper.eq(UserCollects::getUserId, userId).eq(UserCollects::getPassageId, passageId);
      int delete = userCollectsMapper.delete(queryWrapper);
//            userCollectsMapper.deleteById(userCollects);
      if (b && delete == 1) {
        stringRedisTemplate.opsForZSet().remove(key, userId.toString());
      } else {
        throw new BusinessException(ErrorCode.OPERATION_ERROR, ErrorInfo.UPDATE_ERROR);
      }
    }
    return true;
  }


  /**
   * @return
   * @Description: 从 Redis 查询收藏量前 7 的博客
   */
  @Override
  public List<PassageInfoVO> getTopCollects() {

    // 获取所有相关的 passageId 键
    Set<String> keys = stringRedisTemplate.keys(Common.PASSAGE_COLLECT_KEY + "*");
    // 用于存储 passageId 和对应用户数量的列表
    List<Map.Entry<String, Long>> passageNum = new ArrayList<>();
    // 遍历每个key，获取该key收藏用户的数量
    if (CollUtil.isNotEmpty(keys)) {
      for (String key : keys) {
        Long collectNum = stringRedisTemplate.opsForZSet().size(key);
        passageNum.add(new AbstractMap.SimpleEntry<>(key, collectNum != null ? collectNum : 0));
      }
    }
    // 按照用户数量降序排序
    passageNum.sort((entry1, entry2) -> Long.compare(entry2.getValue(), entry1.getValue()));
    List<Long> sortedPassageIds = new ArrayList<>();
    for (Map.Entry<String, Long> entry : passageNum) {
      int lastIndexOf = entry.getKey().lastIndexOf(":") + 1;
      String passageId = entry.getKey().substring(lastIndexOf);
      sortedPassageIds.add(Long.valueOf(passageId));
    }
    List<Passage> passageList = listByIds(sortedPassageIds);
    return getPassageInfoVOList(passageList);
  }

  @Override
  public PassageContentVO getPassageContentByPassageId(Long uid, Long pid) {
    //浏览量+1
    passageMapper.updateViewNum(pid);
    return passageMapper.getPassageContentByPid(uid, pid);

  }

  @Override
  public String uploadPassageCover(MultipartFile img) {
    String imgUrl = FileUtil.uploadImageLocal(img);
    //todo 写入数据库
    log.info("img url：" + imgUrl);
    return imgUrl;
  }


  @Override
  public String uploadPassageImg(MultipartFile img) {
    return FileUtil.uploadImageLocal(img);
  }


}




