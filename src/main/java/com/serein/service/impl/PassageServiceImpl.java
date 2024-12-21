package com.serein.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jd.platform.hotkey.client.callback.JdHotKeyStore;
import com.serein.constants.Common;
import com.serein.constants.ErrorCode;
import com.serein.constants.ErrorInfo;
import com.serein.exception.BusinessException;
import com.serein.mapper.CategoryMapper;
import com.serein.mapper.CommentMapper;
import com.serein.mapper.PassageMapper;
import com.serein.mapper.PassageTagMapper;
import com.serein.mapper.TagsMapper;
import com.serein.mapper.UserCollectsMapper;
import com.serein.mapper.UserMapper;
import com.serein.mapper.UserThumbsMapper;
import com.serein.model.QueryPageRequest;
import com.serein.model.UserHolder;
import com.serein.model.dto.PassageDTO.PassageDTO;
import com.serein.model.dto.PassageDTO.PassageESDTO;
import com.serein.model.entity.Passage;
import com.serein.model.entity.PassageTag;
import com.serein.model.entity.Tags;
import com.serein.model.entity.User;
import com.serein.model.entity.UserCollects;
import com.serein.model.entity.UserThumbs;
import com.serein.model.request.PassageRequest.AdminPassageQueryPageRequest;
import com.serein.model.request.SearchPassageRequest;
import com.serein.model.vo.PassageVO.AdminPassageVO;
import com.serein.model.vo.PassageVO.PassageContentVO;
import com.serein.model.vo.PassageVO.PassageInfoVO;
import com.serein.model.vo.PassageVO.PassageTitleVO;
import com.serein.model.vo.UserVO.LoginUserVO;
import com.serein.service.PassageService;
import com.serein.service.PassageTagService;
import com.serein.util.FileUtil;
import com.serein.util.IPUtil;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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

  @Autowired
  PassageTagMapper passageTagMapper;

  @Autowired
  UserMapper userMapper;

  @Autowired
  PassageTagService passageTagService;
  @Autowired
  TagsMapper tagsMapper;

  @Autowired
  CommentMapper commentMapper;

  @Autowired
  CategoryMapper categoryMapper;

  @Override
  public Page<List<PassageInfoVO>> getHomePassageList(QueryPageRequest queryPageRequest) {
    //判断刷子用户
    IPUtil.isHotIp();
    int currentPage = queryPageRequest.getCurrentPage();
    int pageSize = queryPageRequest.getPageSize();
    //首页加载文章列表时，不加载content，减少数据传输压力，提高加载速度
    Page<Passage> passagePage = new Page<>(currentPage, pageSize);
    Page<Passage> pageDesc = page(passagePage,
        new LambdaQueryWrapper<Passage>().eq(Passage::getStatus, 2)
            .orderByDesc(Passage::getAccessTime).
            select(Passage::getPassageId, Passage::getTitle, Passage::getViewNum,
                Passage::getAuthorId,
                Passage::getThumbnail, Passage::getSummary,
                Passage::getCommentNum, Passage::getCollectNum, Passage::getThumbNum,
                Passage::getAccessTime));
    //当前页的数据
    List<Passage> passageList = pageDesc.getRecords();
    long total = pageDesc.getTotal();
    if (passageList.isEmpty()) {
      throw new BusinessException(ErrorCode.PARAMS_ERROR, "获取文章列表失败");
    }
    log.info("passageList：" + passageList);
    List<PassageInfoVO> pageInfoVOList = getPassageInfoVOList(passageList);
    log.info("passageInfoVOList：" + pageInfoVOList);

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
          isThumbCollect(passageInfoVO);
          User user = userMapper.getAuthorInfo(passage.getAuthorId());
          passageInfoVO.setAuthorName(user.getUserName());
          passageInfoVO.setAvatarUrl(user.getAvatarUrl());
          List<PassageTag> passageTags = passageTagMapper.selectTagIdByPassageId(
              passage.getPassageId());
          List<Long> tagIdList = passageTags.stream().map(PassageTag::getTagId)
              .collect(Collectors.toList());
          if (!tagIdList.isEmpty()) {
            log.info("tagsId：" + tagIdList);
            Map<Long, String> tagStrList = getTagMaps(tagIdList);
            passageInfoVO.setPTagsMap(tagStrList);
          }
          log.info("passageInfoVO：" + passageInfoVO);
          return passageInfoVO;
        }
    ).collect(Collectors.toList());

    return collect;
  }

  /**
   * @param tagIdList
   * @return key=tagId，value=tagName
   * @Description: 根据json格式的字符串id获取标签列表
   */
  public Map<Long, String> getTagMaps(List<Long> tagIdList) {
    log.info("tagsIdlist：" + tagIdList);
    HashMap<Long, String> map = new HashMap<>();
    tagIdList.forEach(tagId -> {
      Tags tags = tagsMapper.selectById(tagId);
      if (tags != null) {
        String tagName = tags.getTagName();
        map.put(tagId, tagName);
      }
    });
    log.info("tagsMap：" + map);
    return map;
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


  @Override
  public Page<List<PassageInfoVO>> searchPassageFromES(SearchPassageRequest searchPassageRequest) {
    String searchText = searchPassageRequest.getSearchText();
    if (StringUtils.isBlank(searchText)) {
      throw new BusinessException(ErrorCode.PARAMS_ERROR, ErrorInfo.PARAMS_ERROR);
    }
    // 构造ES分页查询条件
    Pageable pageable = PageRequest.of(searchPassageRequest.getCurrentPage(),
        searchPassageRequest.getPageSize());
    //拼接查询条件
    BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
    if (StringUtils.isNotBlank(searchText)) {
      boolQueryBuilder.should(QueryBuilders.matchQuery("title", searchText));
      boolQueryBuilder.should(QueryBuilders.matchQuery("content", searchText));
      boolQueryBuilder.should(QueryBuilders.matchQuery("summary", searchText));
      boolQueryBuilder.should(QueryBuilders.termQuery("authorName", searchText));
      boolQueryBuilder.should(QueryBuilders.matchQuery("tagStr", searchText));

      //确保至少有1个“should”条件需要匹配。
      boolQueryBuilder.minimumShouldMatch(1);
    }
    // 构造查询
    SearchRequest searchRequest = new SearchRequest("passage_v2"); // 指定索引名称
    NativeSearchQuery searchQuery = new NativeSearchQueryBuilder().withQuery(boolQueryBuilder)
        .withPageable(pageable)
        .build();
    log.info("ES查询语句:{}", searchQuery.getQuery().toString());
    SearchHits<PassageESDTO> searchHits = elasticsearchRestTemplate.search(searchQuery,
        PassageESDTO.class);
    List<Passage> resourceList = new ArrayList<>();
    List<SearchHit<PassageESDTO>> searchHitList = searchHits.getSearchHits();
    if (searchHitList.isEmpty()) {
      Page<List<PassageInfoVO>> passageInfoVOPage = new Page<>();
      passageInfoVOPage.setRecords(Collections.singletonList(Collections.emptyList()));
      passageInfoVOPage.setTotal(0);
      return passageInfoVOPage;
    }
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
    List<PassageInfoVO> passageInfoVOList = getPassageInfoVOList(resourceList);
    Page<List<PassageInfoVO>> listPage = new Page<List<PassageInfoVO>>(
        searchPassageRequest.getCurrentPage(),
        searchPassageRequest.getPageSize()).setTotal(resourceList.size())
        .setRecords(Collections.singletonList(passageInfoVOList));

    return listPage;
  }


  @Override
  public Page<List<PassageInfoVO>> searchPassageByCategory(
      SearchPassageRequest searchPassageRequest) {
    Long categoryId = searchPassageRequest.getId();
    if (categoryId == null) {
      throw new BusinessException(ErrorCode.PARAMS_ERROR, ErrorInfo.PARAMS_ERROR);
    }
    //从tags查找满足categoryId的tag
    List<Tags> tags = tagsMapper.selectList(
        new LambdaQueryWrapper<Tags>().eq(Tags::getCategoryId, categoryId));
    Page<List<PassageInfoVO>> passageInfoVOPage = new Page<>();
    if (tags.isEmpty()) {
      passageInfoVOPage.setTotal(0);
      passageInfoVOPage.setRecords(Collections.emptyList());
      return passageInfoVOPage;
    }
    //获取tag的所有tagIdList，用tagIdList从passage_tag表查找满足的passageId
    List<Long> tagsIdlist = tags.stream().map(Tags::getTagId).collect(Collectors.toList());
    Integer pageSize = searchPassageRequest.getPageSize();
    Integer currentPage = searchPassageRequest.getCurrentPage();
    Page<PassageTag> passageTagPage = passageTagMapper.selectPage(new Page<>(currentPage, pageSize),
        new LambdaQueryWrapper<PassageTag>().in(PassageTag::getTagId, tagsIdlist)
            .orderByDesc(PassageTag::getCreateTime));
    if (passageTagPage.getTotal() == 0) {
      passageInfoVOPage.setTotal(0);
      passageInfoVOPage.setRecords(Collections.emptyList());
      return passageInfoVOPage;
    }
    List<PassageTag> passageTagList = passageTagPage.getRecords();
    List<Long> passageIdList = passageTagList.stream().map(PassageTag::getPassageId)
        .collect(Collectors.toList());
    List<Passage> passageList = listByIds(passageIdList);
    if (passageList.isEmpty()) {
      passageInfoVOPage.setTotal(0);
      passageInfoVOPage.setRecords(Collections.emptyList());
      return passageInfoVOPage;
    }
    List<PassageInfoVO> passageInfoVOList = getPassageInfoVOList(passageList);
    passageInfoVOPage.setTotal(passageTagPage.getTotal());
    passageInfoVOPage.setRecords(Collections.singletonList(passageInfoVOList));
    return passageInfoVOPage;
  }


  @Override
  public Page<List<PassageInfoVO>> searchPassageByTag(SearchPassageRequest searchPassageRequest) {
    Long tagId = searchPassageRequest.getId();
    if (tagId == null) {
      throw new BusinessException(ErrorCode.PARAMS_ERROR, ErrorInfo.PARAMS_ERROR);
    }
    Integer pageSize = searchPassageRequest.getPageSize();
    Integer currentPage = searchPassageRequest.getCurrentPage();
    Page<PassageTag> passageTagPage = passageTagMapper.selectPage(new Page<>(currentPage, pageSize),
        new LambdaQueryWrapper<PassageTag>().eq(PassageTag::getTagId, tagId)
            .orderByDesc(PassageTag::getCreateTime));
    Page<List<PassageInfoVO>> passageInfoVOPage = new Page<>();
    if (passageTagPage.getTotal() == 0) {
      passageInfoVOPage.setTotal(0);
      passageInfoVOPage.setRecords(Collections.emptyList());
      return passageInfoVOPage;
    }
    List<PassageTag> passageTagList = passageTagPage.getRecords();
    List<Long> passageIdList = passageTagList.stream().map(PassageTag::getPassageId)
        .collect(Collectors.toList());
    List<Passage> passageList = listByIds(passageIdList);
    if (passageList.isEmpty()) {
      passageInfoVOPage.setTotal(0);
      passageInfoVOPage.setRecords(Collections.emptyList());
      return passageInfoVOPage;
    }
    List<PassageInfoVO> passageInfoVOList = getPassageInfoVOList(passageList);
    passageInfoVOPage.setTotal(passageTagPage.getTotal());
    passageInfoVOPage.setRecords(Collections.singletonList(passageInfoVOList));
    return passageInfoVOPage;
  }

  @Override
  public List<PassageTitleVO> getPassageByUserId(Long userId) {
    if (userId == null) {
      throw new BusinessException(ErrorCode.PARAMS_ERROR, ErrorInfo.PARAMS_ERROR);
    }
    IPUtil.isHotIp();

    List<Passage> list = passageMapper.selectOtherPassageByUserId(userId);
    if (list.isEmpty()) {
      return Collections.emptyList();
    }
    ArrayList<PassageTitleVO> passageTitleVOS = new ArrayList<>();
    list.forEach(passage -> {
      PassageTitleVO passageTitleVO = new PassageTitleVO();
      BeanUtils.copyProperties(passage, passageTitleVO);
      passageTitleVOS.add(passageTitleVO);
    });
    return passageTitleVOS;
  }

  //todo 测试
  @Transactional
  @Override
  public Long addPassage(PassageDTO addPassageDTO) {
    Passage passage = getPassage(addPassageDTO);
    //status 0草稿  1待审核 2已发布
    //前期默认已发布
    passage.setStatus(2);
    //保存文章
    passageMapper.insertPassage(passage);
    Long newPassageId = passage.getPassageId();
    if (newPassageId == null) {
      throw new BusinessException(ErrorCode.OPERATION_ERROR, ErrorInfo.RELEASED_ERROR);
    }
    List<Long> tagIdList = addPassageDTO.getTagIdList();
    if (tagIdList == null) {
      throw new BusinessException(ErrorCode.OPERATION_ERROR, ErrorInfo.RELEASED_ERROR);
    }
    //保存文章标签
    boolean b = passageTagMapper.insertPassageTags(tagIdList, newPassageId);
    if (!b) {
      throw new BusinessException(ErrorCode.OPERATION_ERROR, ErrorInfo.RELEASED_ERROR);
    }
    return passage.getPassageId();
  }

  @Override
  public Long updatePassage(PassageDTO updatePassageDTO) {
    Passage passage = getPassage(updatePassageDTO);
    //更新文章时，审核通过时间在数据库中自动更新
    boolean b1 = this.updateById(passage);
    List<Long> tagIdList = updatePassageDTO.getTagIdList();
    if (tagIdList == null) {
      throw new BusinessException(ErrorCode.OPERATION_ERROR, ErrorInfo.RELEASED_ERROR);
    }
    //更新passage_tag之前要删除老的数据
    passageTagMapper.deleteTagByPassageId(passage.getPassageId());
    //保存文章标签
    boolean b2 = passageTagMapper.insertPassageTags(tagIdList, passage.getPassageId());
    if (!b1 && !b2) {
      throw new BusinessException(ErrorCode.OPERATION_ERROR, ErrorInfo.RELEASED_ERROR);
    }
    return passage.getPassageId();
  }

  public Passage getPassage(PassageDTO passageDTO) {
    Passage passage = new Passage();
    BeanUtil.copyProperties(passageDTO, passage);
//    if (passageDTO.getClass() == UpdatePassageDTO.class) {
//      passage.setPassageId(Long.valueOf(((UpdatePassageDTO) passageDTO).getPassageId()));
//    }
    //前端传过来的passageId是string类型
    if (passageDTO.getPassageId() != null) {
      passage.setPassageId(Long.valueOf(passageDTO.getPassageId()));
    }
    LoginUserVO loginUserVO = UserHolder.getUser();
    if (loginUserVO == null) {
      throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR, ErrorInfo.NOT_LOGIN_ERROR);
    }
    passage.setAuthorId(loginUserVO.getUserId());
    return passage;
  }


  @Transactional
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
      boolean b = passageMapper.addThumbNum(passageId);
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
      boolean b = passageMapper.subThumbNum(passageId);
      //删除用户点赞表
      LambdaQueryWrapper<UserThumbs> queryWrapper = new LambdaQueryWrapper<>();
      queryWrapper.eq(UserThumbs::getUserId, userId).eq(UserThumbs::getPassageId, passageId);
      int delete = userThumbsMapper.delete(queryWrapper);
      if (b && delete == 1) {
        Long remove = stringRedisTemplate.opsForZSet().remove(key, userId.toString());
        if (remove != 1) {
          throw new BusinessException(ErrorCode.OPERATION_ERROR, ErrorInfo.REDIS_UPDATE_ERROR);
        }
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
  @Transactional
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
      Boolean b = passageMapper.addCollectNum(passageId);
      //先插入mysql用户收藏表
      UserCollects userCollects = UserCollects.builder().userId(userId).passageId(passageId)
          .build();
      int insert = userCollectsMapper.insert(userCollects);
      if (b && insert == 1) {
        //写入redis
        Boolean add = stringRedisTemplate.opsForZSet()
            .add(key, userId.toString(), System.currentTimeMillis());
        if (Boolean.FALSE.equals(add)) {
          throw new BusinessException(ErrorCode.OPERATION_ERROR, ErrorInfo.REDIS_UPDATE_ERROR);
        }
      } else {
        throw new BusinessException(ErrorCode.OPERATION_ERROR, ErrorInfo.UPDATE_ERROR);
      }
    } else {
      boolean b = passageMapper.subCollectNum(passageId);
      //删除用户收藏表
      LambdaQueryWrapper<UserCollects> queryWrapper = new LambdaQueryWrapper<>();
      queryWrapper.eq(UserCollects::getUserId, userId).eq(UserCollects::getPassageId, passageId);
      int delete = userCollectsMapper.delete(queryWrapper);
//            userCollectsMapper.deleteById(userCollects);
      if (b && delete == 1) {
        Long remove = stringRedisTemplate.opsForZSet().remove(key, userId.toString());
        if (remove != 1) {
          throw new BusinessException(ErrorCode.OPERATION_ERROR, ErrorInfo.REDIS_UPDATE_ERROR);
        }
      } else {
        throw new BusinessException(ErrorCode.OPERATION_ERROR, ErrorInfo.UPDATE_ERROR);
      }
    }
    return true;
  }


  /**
   * todo top10
   *
   * @return
   * @Description:
   */
  @Override
  public List<PassageTitleVO> getTopPassages() {
    //根据viewNum降序获取前10
    IPUtil.isHotIp();
    Page<Passage> page = new Page<>(1, 10);
    LambdaQueryWrapper<Passage> queryWrapper = new LambdaQueryWrapper<>();
    queryWrapper.orderByDesc(Passage::getViewNum);
    Page<Passage> passagePage = passageMapper.selectPage(page, queryWrapper);
    List<Passage> records = passagePage.getRecords();
    List<PassageTitleVO> passageTitleVOS = new ArrayList<>();
    records.forEach(passage -> {
      PassageTitleVO passageTitleVO = new PassageTitleVO();
      BeanUtils.copyProperties(passage, passageTitleVO);
      passageTitleVOS.add(passageTitleVO);
    });
    return passageTitleVOS;
  }

  @Override
  public PassageContentVO getPassageContentByPassageId(Long uid, Long pid) {
    if (pid == null) {
      throw new BusinessException(ErrorCode.PARAMS_ERROR, ErrorInfo.PARAMS_ERROR);
    }
    //判断是否是hotip
    IPUtil.isHotIp();

    //浏览量+1
    passageMapper.updateViewNum(pid);
    PassageContentVO passageContent;
    String hotKey = Common.HOT_PASSAGE_KEY + pid;
    Object hotPassage = JdHotKeyStore.getValue(hotKey);
    if (hotPassage == null) {
      passageContent = passageMapper.getPassageContentByPid(uid, pid);
      JdHotKeyStore.smartSet(hotKey, passageContent);
      return passageContent;
    }
    return (PassageContentVO) hotPassage;

//    return  passageContent;
    //使用缓存好的value即可
//    boolean isHot = JdHotKeyStore.isHotKey(hotKey);
//    //获取hotKey并上报etcd统计次数
//    if (isHot) {
//      //是hotKey就从redis取出来
//      Map<Object, Object> map = stringRedisTemplate.opsForHash().entries(hotKey);
//      if (!map.isEmpty()) {
//        passageContent = BeanUtil.fillBeanWithMap(map, new PassageContentVO(), false);
//        log.info("{}", passageContent);
//        return passageContent;
//      }
//    }
//    log.info("no hot key");
//    passageContent = passageMapper.getPassageContentByPid(uid, pid);
//    //刚才已经上报过一次，这里再判断，是hotKey就保存在redis
//    boolean isHot2 = JdHotKeyStore.isHotKey(hotKey);
//    if (isHot2) {
//      Map<String, String> stringMap = getStringMap(passageContent);
//      stringRedisTemplate.opsForHash().putAll(hotKey, stringMap);
//      stringRedisTemplate.expire(hotKey, Common.HOT_PASSAGE_DURATION, TimeUnit.MINUTES);
//    }
//    return passageContent;
  }


  private static Map<String, String> getStringMap(PassageContentVO passageContent) {
    Map<String, Object> map = BeanUtil.beanToMap(passageContent, false, true);
    Map<String, String> stringMap = new HashMap<>();
    // 遍历原始 map，将所有值转换为字符串
    for (Map.Entry<String, Object> entry : map.entrySet()) {
      String key = entry.getKey();
      Object value = entry.getValue();
      // 如果值是 Long 类型，转换为字符串
      if (value instanceof Long) {
        stringMap.put(key, value.toString());
      } else {
        stringMap.put(key, String.valueOf(value));
        // 对于其他类型，直接转换为字符串
      }
    }
    return stringMap;
  }

  @Override
  public String uploadPassageCover(MultipartFile img) {
    String imgUrl = FileUtil.uploadImageLocal(img);
    log.info("img url：" + imgUrl);
    return imgUrl;
  }


  @Override
  public String uploadPassageImg(MultipartFile img) {
    return FileUtil.uploadImageLocal(img);
  }

  @Override
  public Page<List<AdminPassageVO>> getPassageList(
      AdminPassageQueryPageRequest adminPassageQueryPageRequest) {
    IPUtil.isHotIp();
    Long passageId = adminPassageQueryPageRequest.getPassageId();
    int currentPage = adminPassageQueryPageRequest.getCurrentPage();
    int pageSize = adminPassageQueryPageRequest.getPageSize();
    String title = adminPassageQueryPageRequest.getTitle();
    Long authorId = adminPassageQueryPageRequest.getAuthorId();
    Date endTime = adminPassageQueryPageRequest.getEndTime();
    Date startTime = adminPassageQueryPageRequest.getStartTime();

    Page<Passage> passagePage = new Page<>(currentPage, pageSize);
    Page<Passage> pageDesc = page(passagePage,
        new LambdaQueryWrapper<Passage>().orderByDesc(Passage::getAccessTime)
            .gt(startTime != null, Passage::getCreateTime, startTime)
            .lt(endTime != null, Passage::getCreateTime, endTime)
            .eq(authorId != null, Passage::getAuthorId, authorId)
            .eq(passageId != null, Passage::getPassageId, passageId)
            .like(StringUtils.isNotBlank(title), Passage::getTitle, title)
            .select(Passage::getPassageId, Passage::getStatus,
                Passage::getTitle,
                Passage::getAccessTime, Passage::getCommentNum,
                Passage::getViewNum, Passage::getCollectNum, Passage::getThumbNum,
                Passage::getAuthorId)
    );
    List<Passage> records = pageDesc.getRecords();
    long total = pageDesc.getTotal();
    Page<List<AdminPassageVO>> listPage = new Page<>(currentPage, pageSize);
    if (records.isEmpty()) {
      //包装成单一的list
      listPage.setRecords(Collections.singletonList(Collections.emptyList()));
      //总数据数量
      listPage.setTotal(0);
      return listPage;
    }
    List<AdminPassageVO> adminPassageVOListByPassageList = getAdminPassageVOListByPassageList(
        records);
    //包装成单一的list
    listPage.setRecords(Collections.singletonList(adminPassageVOListByPassageList));
    //总数据数量
    listPage.setTotal(total);
    return listPage;
  }

  private List<AdminPassageVO> getAdminPassageVOListByPassageList(List<Passage> records) {
    return records.stream().map(passage -> {
      AdminPassageVO adminPassageVO = new AdminPassageVO();
      BeanUtils.copyProperties(passage, adminPassageVO);
      List<PassageTag> passageTags = passageTagMapper.selectTagIdByPassageId(
          passage.getPassageId());
      List<Long> tagIdlist = passageTags.stream().map(PassageTag::getTagId)
          .collect(Collectors.toList());
      if (!tagIdlist.isEmpty()) {
        Map<Long, String> tagMaps = getTagMaps(tagIdlist);
        adminPassageVO.setPTagsMap(tagMaps);
      }
      return adminPassageVO;
    }).collect(Collectors.toList());
  }

  @Override
  public Boolean rejectPassage(Long passageId) {
    LambdaUpdateWrapper<Passage> passageQueryWrapper = new LambdaUpdateWrapper<>();
    passageQueryWrapper.eq(Passage::getPassageId, passageId).set(Passage::getStatus, 3);
    boolean b = this.update(passageQueryWrapper);
    if (b) {
      return b;
    } else {
      throw new BusinessException(ErrorCode.OPERATION_ERROR, ErrorInfo.UPDATE_ERROR);
    }
  }

  @Override
  public Boolean publishPassage(Long passageId) {
    //todo 枚举
    LambdaUpdateWrapper<Passage> passageQueryWrapper = new LambdaUpdateWrapper<>();
    passageQueryWrapper.eq(Passage::getPassageId, passageId).set(Passage::getStatus, 2);
    boolean b = this.update(passageQueryWrapper);
    if (b) {
      return b;
    } else {
      throw new BusinessException(ErrorCode.OPERATION_ERROR, ErrorInfo.UPDATE_ERROR);
    }
  }

  /**
   * 删除文章
   *
   * @param passageId
   * @return
   */
  @Override
  @Transactional
  public boolean deleteByPassageId(Long passageId) {
    boolean b1 = removeById(passageId);
    boolean b2 = commentMapper.deleteByPassageId(passageId);
    boolean b3 = passageTagMapper.deleteByPassageId(passageId);
    if (b1 && b2 && b3) {
      return true;
    }
    throw new BusinessException(ErrorCode.OPERATION_ERROR, ErrorInfo.DELETE_ERROR);
  }


}




