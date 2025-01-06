package com.serein.service.impl;

import static com.serein.constants.Common.TIME_PUBLISH_KEY;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jd.platform.hotkey.client.callback.JdHotKeyStore;
import com.serein.constants.Common;
import com.serein.constants.ErrorCode;
import com.serein.constants.ErrorInfo;
import com.serein.constants.OperationPassageType;
import com.serein.exception.BusinessException;
import com.serein.mapper.CommentMapper;
import com.serein.mapper.PassageMapper;
import com.serein.mapper.PassageTagMapper;
import com.serein.mapper.TagsMapper;
import com.serein.mapper.UserCollectsMapper;
import com.serein.mapper.UserMapper;
import com.serein.mapper.UserThumbsMapper;
import com.serein.model.dto.passageDTO.ParentPassageDTO;
import com.serein.model.dto.passageDTO.PassageESDTO;
import com.serein.model.entity.Passage;
import com.serein.model.entity.PassageTag;
import com.serein.model.entity.Tags;
import com.serein.model.entity.User;
import com.serein.model.entity.UserCollects;
import com.serein.model.entity.UserThumbs;
import com.serein.model.request.PassageRequest.AdminPassageQueryPageRequest;
import com.serein.model.request.QueryPageRequest;
import com.serein.model.request.SearchPassageRequest;
import com.serein.model.vo.passageVO.AdminPassageVO;
import com.serein.model.vo.passageVO.EditPassageVO;
import com.serein.model.vo.passageVO.PassageContentVO;
import com.serein.model.vo.passageVO.PassageInfoVO;
import com.serein.model.vo.passageVO.PassageTitleVO;
import com.serein.model.vo.userVO.LoginUserVO;
import com.serein.service.PassageService;
import com.serein.util.FileUtil;
import com.serein.util.IPUtil;
import com.serein.util.UserHolder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.index.query.QueryBuilders;
import org.redisson.api.RBlockingDeque;
import org.redisson.api.RDelayedQueue;
import org.redisson.api.RedissonClient;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
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
  private ElasticsearchRestTemplate elasticsearchRestTemplate;


  @Autowired
  private StringRedisTemplate stringRedisTemplate;

  @Autowired
  private UserThumbsMapper userThumbsMapper;

  @Autowired
  private UserCollectsMapper userCollectsMapper;

  @Autowired
  private PassageMapper passageMapper;

  @Autowired
  private PassageTagMapper passageTagMapper;

  @Autowired
  private UserMapper userMapper;

  @Autowired
  private TagsMapper tagsMapper;

  @Autowired
  private CommentMapper commentMapper;

  @Autowired
  private RedissonClient redissonClient;

  @Override
  public Page<List<PassageInfoVO>> getHomePassageList(QueryPageRequest queryPageRequest) {
    //判断刷子用户
    IPUtil.isHotIp();
    int currentPage = queryPageRequest.getCurrentPage();
    int pageSize = queryPageRequest.getPageSize();
    //首页加载文章列表时，不加载content，减少数据传输压力，提高加载速度
    Page<Passage> passagePage = new Page<>(currentPage, pageSize);
    Page<Passage> pageDesc = page(passagePage,
        new LambdaQueryWrapper<Passage>().eq(Passage::getStatus, 2).eq(Passage::getIsPrivate, 1)
            .orderByDesc(Passage::getAccessTime).
            select(Passage::getPassageId, Passage::getTitle, Passage::getViewNum,
                Passage::getAuthorId,
                Passage::getThumbnail, Passage::getSummary,
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
          int thumbsCount = userThumbsMapper.count(passage.getPassageId());
          int collectCount = userCollectsMapper.count(passage.getPassageId());
          int commentCount = commentMapper.count(passage.getPassageId());
          passageInfoVO.setCollectNum(collectCount);
          passageInfoVO.setThumbNum(thumbsCount);
          passageInfoVO.setCommentNum(commentCount);
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
    NativeSearchQueryBuilder searchQuery = new NativeSearchQueryBuilder();
    if (StringUtils.isNotBlank(searchText)) {
//      // 构建 bool 查询
      searchQuery.withQuery(QueryBuilders.boolQuery()
          .should(QueryBuilders.matchQuery("title", searchText))
          .should(QueryBuilders.matchQuery("content", searchText))
          .should(QueryBuilders.matchQuery("summary", searchText))
          .should(QueryBuilders.matchQuery("tagStr", searchText))
          .should(QueryBuilders.matchQuery("authorName", searchText))
      );
    }
    log.info("ES查询语句:{}", searchQuery);
    SearchHits<PassageESDTO> searchHits = null;

    try {
      searchHits = elasticsearchRestTemplate.search(searchQuery.build(),
          PassageESDTO.class);
    } catch (Exception e) {
      log.error("ES查询出错{}", e);
      throw new RuntimeException(e);
    }

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
    List<Passage> passageList = passageMapper.selectList(
        new LambdaQueryWrapper<Passage>().eq(Passage::getIsPrivate, 1)
            .in(Passage::getPassageId, passageIdList));
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
//    List<Passage> passageList = listByIds(passageIdList);
    List<Passage> passageList = list(
        new LambdaQueryWrapper<Passage>().in(Passage::getPassageId, passageIdList)
            .eq(Passage::getIsPrivate, 1));
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
    List<Passage> passageList = list(
        new LambdaQueryWrapper<Passage>().in(Passage::getPassageId, passageIdList)
            .eq(Passage::getIsPrivate, 1));
//    List<Passage> passageList = listByIds(passageIdList);
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
  public boolean setPassagePrivate(Long passageId) {
    boolean b = passageMapper.setPassagePrivate(passageId);
    if (!b) {
      throw new BusinessException(ErrorCode.OPERATION_ERROR, ErrorInfo.OPERATION_ERROR);
    }
    return true;
  }

  @Override
  public EditPassageVO getEditPassageByPassageId(Long passageId) {
    LoginUserVO user = UserHolder.getUser();
    if (user == null) {
      throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR, ErrorInfo.NOT_LOGIN_ERROR);
    }
    Passage passage = passageMapper.getEditPassageByPassageId(passageId, user.getUserId());
    if (passage == null) {
      throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, ErrorInfo.NO_DB_DATA);
    }
    EditPassageVO editPassageVO = new EditPassageVO();
    BeanUtils.copyProperties(passage, editPassageVO);
    List<PassageTag> passageTags = passageTagMapper.selectTagIdByPassageId(passageId);
    List<Long> tagIdList = passageTags.stream().map(PassageTag::getTagId)
        .collect(Collectors.toList());
    editPassageVO.setPTags(tagIdList);
    return editPassageVO;
  }

  @Override
  public List<PassageTitleVO> getOtherPassagesByUserId(Long userId) {
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

  @Transactional
  @Override
  public Long addPassage(ParentPassageDTO addParentPassageDTO) {
    Passage passage = getPassage(addParentPassageDTO);
    passageMapper.insertPassage(passage);
    Long newPassageId = passage.getPassageId();
    if (newPassageId == null) {
      throw new BusinessException(ErrorCode.OPERATION_ERROR, ErrorInfo.PUBLISH_ERROR);
    }
    if (addParentPassageDTO.getType() == OperationPassageType.TIME_PUBLISH) {
      //添加任务到延迟队列
      long delay = addParentPassageDTO.getPublishTime() - System.currentTimeMillis();
      addDelayQueue(newPassageId, delay, TimeUnit.MILLISECONDS, TIME_PUBLISH_KEY);
    }
    List<Long> tagIdList = addParentPassageDTO.getTagIdList();
    if (tagIdList == null) {
      throw new BusinessException(ErrorCode.OPERATION_ERROR, ErrorInfo.PUBLISH_ERROR);
    }
    //保存文章标签
    boolean b = passageTagMapper.insertPassageTags(tagIdList, newPassageId);
    if (!b) {
      throw new BusinessException(ErrorCode.OPERATION_ERROR, ErrorInfo.PUBLISH_ERROR);
    }
    return passage.getPassageId();
  }

  @Override
  public Long updatePassage(ParentPassageDTO updateParentPassageDTO) {
    Passage passage = getPassage(updateParentPassageDTO);
    //更新文章时，审核通过时间在数据库中自动更新
    int b1 = passageMapper.updatePassage(passage);
    if (updateParentPassageDTO.getType() == OperationPassageType.TIME_PUBLISH) {
      long delay = updateParentPassageDTO.getPublishTime() - System.currentTimeMillis();
      addDelayQueue(Long.valueOf(updateParentPassageDTO.getPassageId()), delay,
          TimeUnit.MILLISECONDS, TIME_PUBLISH_KEY);
    }
    List<Long> tagIdList = updateParentPassageDTO.getTagIdList();
    if (tagIdList == null) {
      throw new BusinessException(ErrorCode.OPERATION_ERROR, ErrorInfo.PUBLISH_ERROR);
    }
    //更新passage_tag之前要删除老的数据
    passageTagMapper.deleteTagByPassageId(passage.getPassageId());
    //保存文章标签
    boolean b2 = passageTagMapper.insertPassageTags(tagIdList, passage.getPassageId());
    if (b1 != 1 && !b2) {
      throw new BusinessException(ErrorCode.OPERATION_ERROR, ErrorInfo.PUBLISH_ERROR);
    }
    return passage.getPassageId();
  }

  /**
   * 向延迟队列中添加文章id
   *
   * @param passageId
   * @param delay
   * @param timeUnit
   * @param queueKey
   */
  public void addDelayQueue(Long passageId, long delay, TimeUnit timeUnit, String queueKey) {
    try {
      RBlockingDeque<Object> blockingDeque = redissonClient.getBlockingDeque(queueKey);
      RDelayedQueue<Object> delayedQueue = redissonClient.getDelayedQueue(blockingDeque);
      //向延迟队列中添加任务
      delayedQueue.offer(passageId, delay, timeUnit);
      log.info("添加延时队列成功 队列键：{}，队列值：{}，延迟时间：{}", queueKey, passageId,
          timeUnit.toSeconds(delay) + "秒");
    } catch (Exception e) {
      log.error("添加延时队列失败 {}", e.getMessage());
      throw new RuntimeException("添加延时队列失败");
    }
  }

  /**
   * 从延迟队列中获取passageId
   *
   * @param queuekey
   * @return
   * @throws InterruptedException
   */
  public Long getDelayQueue(String queuekey) throws InterruptedException {
    RBlockingDeque<Long> blockingDeque = redissonClient.getBlockingDeque(queuekey);
    return blockingDeque.take();
  }

  public Passage getPassage(ParentPassageDTO parentPassageDTO) {
    Passage passage = new Passage();
    BeanUtil.copyProperties(parentPassageDTO, passage);
    String passageId = parentPassageDTO.getPassageId();
    if (StringUtils.isNotBlank(passageId)) {
      passage.setPassageId(Long.valueOf(passageId));
    }
    LoginUserVO loginUserVO = UserHolder.getUser();
    if (loginUserVO == null) {
      throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR, ErrorInfo.NOT_LOGIN_ERROR);
    }
    passage.setAuthorId(loginUserVO.getUserId());
    int type = parentPassageDTO.getType();
    switch (type) {
      case OperationPassageType.SAVE:
//        passage.setStatus(0);
        break;
      case OperationPassageType.PUBLISH:
        passage.setStatus(2);
        break;
      case OperationPassageType.TIME_PUBLISH:
        //定时发布就先作为草稿保存，等定时任务修改status
        passage.setStatus(0);
        break;
    }
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
      //插入用户点赞表
      UserThumbs userThumbs = UserThumbs.builder().userId(userId).passageId(passageId).build();
      int insert = userThumbsMapper.insert(userThumbs);
      //文章表和用户点赞表同时更新成功
      if (insert == 1) {
        stringRedisTemplate.opsForZSet().add(key, userId.toString(), System.currentTimeMillis());
      } else {
        throw new BusinessException(ErrorCode.OPERATION_ERROR, ErrorInfo.UPDATE_ERROR);
      }
    } else {
      //删除用户点赞表
      LambdaQueryWrapper<UserThumbs> queryWrapper = new LambdaQueryWrapper<>();
      queryWrapper.eq(UserThumbs::getUserId, userId).eq(UserThumbs::getPassageId, passageId);
      int delete = userThumbsMapper.delete(queryWrapper);
      if (delete == 1) {
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
    //TODO 改成set集合
    String key = Common.PASSAGE_COLLECT_KEY + passageId;
    Double score = stringRedisTemplate.opsForZSet().score(key, userId.toString());
    if (score == null) {
      //先插入mysql用户收藏表
      UserCollects userCollects = UserCollects.builder().userId(userId).passageId(passageId)
          .build();
      int insert = userCollectsMapper.insert(userCollects);
      if (insert == 1) {
        //写入redis
        boolean add = stringRedisTemplate.opsForZSet()
            .add(key, userId.toString(), System.currentTimeMillis());
        // 增加文章的收藏量
        stringRedisTemplate.opsForZSet()
            .incrementScore(Common.TOP_COLLECT_PASSAGE, String.valueOf(passageId), 1);
        if (!add) {
          throw new BusinessException(ErrorCode.OPERATION_ERROR, ErrorInfo.REDIS_UPDATE_ERROR);
        }
      } else {
        throw new BusinessException(ErrorCode.OPERATION_ERROR, ErrorInfo.UPDATE_ERROR);
      }
    } else {
      //删除用户收藏表
      LambdaQueryWrapper<UserCollects> queryWrapper = new LambdaQueryWrapper<>();
      queryWrapper.eq(UserCollects::getUserId, userId).eq(UserCollects::getPassageId, passageId);
      int delete = userCollectsMapper.delete(queryWrapper);
      if (delete == 1) {
        Long remove = stringRedisTemplate.opsForZSet().remove(key, userId.toString());
        stringRedisTemplate.opsForZSet()
            .incrementScore(Common.TOP_COLLECT_PASSAGE, String.valueOf(passageId), -1);
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
    Set<String> passageIdSet = stringRedisTemplate.opsForZSet()
        .reverseRange(Common.TOP_COLLECT_PASSAGE, 0, 9);
    List<Long> idlist = passageIdSet.stream().map(passageId -> Long.valueOf(passageId))
        .collect(Collectors.toList());
    if(idlist.isEmpty()){
      return Collections.emptyList();
    }
    List<Passage> passageList = listByIds(idlist);
    // 为了保证文章收藏量从高到低，创建一个 Map 来存储 passageId 与 Passage 对象的映射关系
    Map<Long, Passage> passageMap = passageList.stream()
        .collect(Collectors.toMap(Passage::getPassageId, passage -> passage));

    List<PassageTitleVO> passageTitleVOS = new ArrayList<>();

    //按照 idlist 顺序将 Passage 对象转换为 PassageTitleVO
    idlist.forEach(id -> {
      Passage passage = passageMap.get(id);  // 根据 id 获取对应的 Passage
      if (passage != null) {
        PassageTitleVO passageTitleVO = new PassageTitleVO();
        BeanUtils.copyProperties(passage, passageTitleVO);
        passageTitleVOS.add(passageTitleVO);
      }
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
            .eq(Passage::getIsPrivate, 1)
            .eq(passageId != null, Passage::getPassageId, passageId)
            .like(StringUtils.isNotBlank(title), Passage::getTitle, title)
            .select(Passage::getPassageId, Passage::getStatus,
                Passage::getTitle,
                Passage::getAccessTime,
                Passage::getViewNum,
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
      int thumbsCount = userThumbsMapper.count(passage.getPassageId());
      int collectCount = userCollectsMapper.count(passage.getPassageId());
      int commentCount = commentMapper.count(passage.getPassageId());
      adminPassageVO.setCollectNum(collectCount);
      adminPassageVO.setThumbNum(thumbsCount);
      adminPassageVO.setCommentNum(commentCount);
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
    int i = passageMapper.publishPassage(passageId);
    if (i != 0) {
      return true;
    } else {
      throw new BusinessException(ErrorCode.OPERATION_ERROR, ErrorInfo.UPDATE_ERROR);
    }
  }

  /**
   * 删除文章
   * todo redis删除
   *
   * @param passageId
   * @return
   */
  @Override
  @Transactional
  public boolean deleteByPassageId(Long passageId) {
    boolean b1 = removeById(passageId);
    commentMapper.deleteByPassageId(passageId);
    userCollectsMapper.deleteByPassageId(passageId);
    userThumbsMapper.deleteByPassageId(passageId);
    boolean b3 = passageTagMapper.deleteByPassageId(passageId);
    stringRedisTemplate.opsForZSet().remove(Common.TOP_COLLECT_PASSAGE, String.valueOf(passageId));

    if (b1 && b3) {
      return true;
    }
    throw new BusinessException(ErrorCode.OPERATION_ERROR, ErrorInfo.DELETE_ERROR);
  }


}




