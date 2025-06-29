package com.serein.service.impl;

import static com.serein.constants.Common.BLOG_CACHE_PREFIX;
import static com.serein.constants.Common.TIME_PUBLISH_KEY;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.serein.constants.Common;
import com.serein.constants.ErrorCode;
import com.serein.constants.ErrorInfo;
import com.serein.exception.BusinessException;
import com.serein.mapper.CommentMapper;
import com.serein.mapper.esMapper.PassageEsMapper;
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
import com.serein.util.AliOssUtil;
import com.serein.util.IPUtil;
import com.serein.util.UserContext;
import jakarta.annotation.Resource;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.redisson.api.RBlockingDeque;
import org.redisson.api.RDelayedQueue;
import org.redisson.api.RedissonClient;
import org.springframework.beans.BeanUtils;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.core.RedisCallback;
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
public class PassageServiceImpl extends ServiceImpl<PassageMapper, Passage> implements PassageService {

  @Resource
  private StringRedisTemplate stringRedisTemplate;

  @Resource
  private UserThumbsMapper userThumbsMapper;

  @Resource
  private UserCollectsMapper userCollectsMapper;

  @Resource
  private PassageMapper passageMapper;

  @Resource
  private PassageTagMapper passageTagMapper;

  @Resource
  private UserMapper userMapper;

  @Resource
  private TagsMapper tagsMapper;

  @Resource
  private CommentMapper commentMapper;

  @Resource
  private RedissonClient redissonClient;


  @Cacheable(cacheNames = BLOG_CACHE_PREFIX
      + "getHomePassageList", key = "#p0.currentPage")
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

    return passageList.stream().map(passage -> {
          PassageInfoVO passageInfoVO = new PassageInfoVO();
          BeanUtil.copyProperties(passage, passageInfoVO);
          isThumbCollect(passageInfoVO);
          User user = userMapper.getAuthorInfo(passage.getAuthorId());
          passageInfoVO.setAuthorName(user.getUserName());
          passageInfoVO.setAvatarUrl(user.getAvatarUrl());
          Long passageId = passage.getPassageId();
          List<PassageTag> passageTags = passageTagMapper.selectTagIdByPassageId(passageId);
          List<Long> tagIdList = passageTags.stream().map(PassageTag::getTagId)
              .collect(Collectors.toList());
          if (!tagIdList.isEmpty()) {
            Map<Long, String> tagStrList = getTagMaps(tagIdList);
            passageInfoVO.setPTagsMap(tagStrList);
          }
          int thumbsCount = Objects.requireNonNull(
              stringRedisTemplate.execute((RedisCallback<Long>) connection -> {
                return connection.bitCount((Common.PASSAGE_THUMB_KEY + passageId).getBytes());
              })).intValue();
          int collectCount = Objects.requireNonNull(
              stringRedisTemplate.execute((RedisCallback<Long>) connection -> {
                return connection.bitCount((Common.PASSAGE_COLLECT_KEY + passageId).getBytes());
              })).intValue();
//      int thumbsCount = userThumbsMapper.count(passage.getPassageId());
//          int collectCount = userCollectsMapper.count(passage.getPassageId());
          int commentCount = commentMapper.countCommentNum(passageId);
          passageInfoVO.setCollectNum(collectCount);
          passageInfoVO.setThumbNum(thumbsCount);
          passageInfoVO.setCommentNum(commentCount);
          return passageInfoVO;
        }
    ).collect(Collectors.toList());
  }

  /**
   * @param tagIdList
   * @return key=tagId，value=tagName
   * @Description: 根据json格式的字符串id获取标签列表
   */
  public Map<Long, String> getTagMaps(List<Long> tagIdList) {
    HashMap<Long, String> map = new HashMap<>();
    tagIdList.forEach(tagId -> {
      Tags tags = tagsMapper.selectById(tagId);
      if (tags != null) {
        String tagName = tags.getTagName();
        map.put(tagId, tagName);
      }
    });
    return map;
  }

  //passageInfo中包含浏览量点赞等信息，变化频繁，不放入redis缓存了
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
    Long userId = UserContext.getUser();
    if (userId == null) {
      return;
    }
    String passageId = passageInfoVO.getPassageId().toString();
    String keyThumb = Common.PASSAGE_THUMB_KEY + passageId;
    passageInfoVO.setIsThumb(stringRedisTemplate.opsForValue().getBit(keyThumb, userId.intValue()));
    String keyCollect = Common.PASSAGE_COLLECT_KEY + passageId;
    passageInfoVO.setIsCollect(
        stringRedisTemplate.opsForValue().getBit(keyCollect, userId.intValue()));
  }

  @Override
  public Page<List<PassageInfoVO>> searchPassageFromES(SearchPassageRequest searchPassageRequest) {
   /* String searchText = searchPassageRequest.getSearchText();
    if (StringUtils.isBlank(searchText)) {
      throw new BusinessException(ErrorCode.PARAMS_ERROR, ErrorInfo.PARAMS_ERROR);
    }
    LambdaEsQueryWrapper<PassageESDTO> passageEsWrapper = new LambdaEsQueryWrapper<>();
    passageEsWrapper.like(PassageESDTO::getContent,searchText)
        .like(PassageESDTO::getTitle,searchText)
        .like(PassageESDTO::getSummary,searchText)
        .like(PassageESDTO::getTagStr,searchText)
        .like(PassageESDTO::getAuthorName,searchText);
    List<PassageESDTO> passageESList = passageEsMapper.selectList(passageEsWrapper);

    List<Passage> resourceList = new ArrayList<>();
    if (CollUtil.isNotEmpty(passageESList)) {
      Page<List<PassageInfoVO>> passageInfoVOPage = new Page<>();
      passageInfoVOPage.setRecords(Collections.singletonList(Collections.emptyList()));
      passageInfoVOPage.setTotal(0);
      return passageInfoVOPage;
    }
    List<Long> passageIdList = passageESList.stream()
        .map(PassageESDTO::getPassageId).collect(Collectors.toList());
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
          LambdaEsQueryWrapper<PassageESDTO> passageESEsWrapper = new LambdaEsQueryWrapper<>();
          Integer deleteNum = passageEsMapper.delete(
              passageESEsWrapper.eq(PassageESDTO::getPassageId, passageId.toString()));
          log.info("Delete outdated passage on ES:  {}", deleteNum);
        }
      });
    }
    List<PassageInfoVO> passageInfoVOList = getPassageInfoVOList(resourceList);

    return new Page<List<PassageInfoVO>>(
        searchPassageRequest.getCurrentPage(),
        searchPassageRequest.getPageSize()).setTotal(resourceList.size())
        .setRecords(Collections.singletonList(passageInfoVOList));*/
    return null;
  }

  @Cacheable(cacheNames = BLOG_CACHE_PREFIX
      + "searchPassageFromMySQL", key = "#p0.searchText")
  @Override
  public Page<List<PassageInfoVO>> searchPassageFromMySQL(
      SearchPassageRequest searchPassageRequest) {
    String searchText = searchPassageRequest.getSearchText();
    if (StringUtils.isBlank(searchText)) {
      throw new BusinessException(ErrorCode.PARAMS_ERROR, ErrorInfo.PARAMS_ERROR);
    }
    List<Passage> passageList = passageMapper.searchPassageFromMySQL(searchText);
    List<PassageInfoVO> passageInfoVOList = getPassageInfoVOList(passageList);
    return new Page<List<PassageInfoVO>>(
        searchPassageRequest.getCurrentPage(),
        searchPassageRequest.getPageSize()).setTotal(passageList.size())
        .setRecords(Collections.singletonList(passageInfoVOList));
  }


  @Cacheable(cacheNames = BLOG_CACHE_PREFIX
      + "searchPassageByCategory", key = "#p0.id")
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

  @Cacheable(cacheNames = BLOG_CACHE_PREFIX
      + "searchPassageByTag", key = "#p0.id")
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
    Long userId = UserContext.getUser();
    if (userId == null) {
      throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR, ErrorInfo.NOT_LOGIN_ERROR);
    }
    Passage passage = passageMapper.getEditPassageByPassageId(passageId, userId);
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

  @Transactional
  @Override
  public boolean timePublish(ParentPassageDTO parentPassageDTO) {
    String passageId = parentPassageDTO.getPassageId();
    if (StringUtils.isNotBlank(passageId)) {
      //id已经存在，那么更新数据库并添加到队列中,不包括status
      updatePassage(parentPassageDTO);
      log.info("currentTimeMillis :{}", System.currentTimeMillis());
      log.info("parentPassageDTO.getPublishTime() :{}", parentPassageDTO.getPublishTime());
      long delay = parentPassageDTO.getPublishTime() - System.currentTimeMillis();
      log.info("delay: {}", delay);
      addDelayQueue(Long.valueOf(passageId), delay, TimeUnit.MILLISECONDS, TIME_PUBLISH_KEY);
      return true;
    } else {
      //id不存在，说明是新文章，先插入数据库再添加到队列
      Long newPassageId = insertPassage(parentPassageDTO);
      List<Long> tagIdList = parentPassageDTO.getTagIdList();
      if (tagIdList == null) {
        throw new BusinessException(ErrorCode.OPERATION_ERROR, ErrorInfo.TIME_PUBLISH_ERROR);
      }
      //保存文章标签
      boolean b2 = passageTagMapper.insertPassageTags(tagIdList, newPassageId);
      if (!b2) {
        throw new BusinessException(ErrorCode.OPERATION_ERROR, ErrorInfo.PUBLISH_ERROR);
      }
      long delay = parentPassageDTO.getPublishTime() - System.currentTimeMillis();
      addDelayQueue(Long.valueOf(newPassageId), delay, TimeUnit.MILLISECONDS, TIME_PUBLISH_KEY);
      return true;
    }
  }


  @Transactional
  public Long insertPassage(ParentPassageDTO parentPassageDTO) {
    Passage passage = new Passage();
    BeanUtils.copyProperties(parentPassageDTO, passage);
    Long userId = UserContext.getUser();
    if (userId == null) {
      throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR, ErrorInfo.NOT_LOGIN_ERROR);
    }
    passage.setAuthorId(userId);
    int i = passageMapper.insertPassage(passage);
    if (i != 1) {
      throw new BusinessException(ErrorCode.OPERATION_ERROR, ErrorInfo.TIME_PUBLISH_ERROR);
    }
    List<Long> tagIdList = parentPassageDTO.getTagIdList();
    if (tagIdList == null) {
      throw new BusinessException(ErrorCode.OPERATION_ERROR, ErrorInfo.PUBLISH_ERROR);
    }
    //保存文章标签
    boolean b2 = passageTagMapper.insertPassageTags(tagIdList, passage.getPassageId());
    if (!b2) {
      throw new BusinessException(ErrorCode.OPERATION_ERROR, ErrorInfo.PUBLISH_ERROR);
    }
    return passage.getPassageId();
  }

  @Transactional
  @Override
  public Long savePassage(ParentPassageDTO parentPassageDTO) {
    String passageId = parentPassageDTO.getPassageId();
    if (StringUtils.isNotBlank(passageId)) {
      //id已经存在，那么仅更新数据库内容，不包括status
      updatePassage(parentPassageDTO);
      return Long.valueOf(passageId);
    } else {
      //id不存在，说明是新文章，先插入数据库 （status默认为0）
      return insertPassage(parentPassageDTO);
    }
  }


  @Transactional
  @Override
  public boolean nowPublish(ParentPassageDTO parentPassageDTO) {
    String passageId = parentPassageDTO.getPassageId();
    if (StringUtils.isNotBlank(passageId)) {
      List<Long> tagIdList = parentPassageDTO.getTagIdList();
      if (tagIdList == null) {
        throw new BusinessException(ErrorCode.OPERATION_ERROR, ErrorInfo.PUBLISH_ERROR);
      }
      //更新passage_tag之前要删除老的数据
      passageTagMapper.deleteTagByPassageId(Long.valueOf(passageId));
      //保存文章标签
      boolean b2 = passageTagMapper.insertPassageTags(tagIdList, Long.valueOf(passageId));
      if (!b2) {
        throw new BusinessException(ErrorCode.OPERATION_ERROR, ErrorInfo.PUBLISH_ERROR);
      }
      //id已经存在，那么更新数据库，status=2
      int i = passageMapper.publishPassage(Long.valueOf(passageId));
      if (i != 1) {
        throw new BusinessException(ErrorCode.OPERATION_ERROR, ErrorInfo.OPERATION_ERROR);
      }
      return true;
    } else {
      //id不存在，说明是新文章，先插入数据库， status=2
      parentPassageDTO.setStatus(2);
      Long newPassageId = insertPassage(parentPassageDTO);
      if (newPassageId == null) {
        throw new BusinessException(ErrorCode.OPERATION_ERROR, ErrorInfo.OPERATION_ERROR);
      }
      List<Long> tagIdList = parentPassageDTO.getTagIdList();
      if (tagIdList == null) {
        throw new BusinessException(ErrorCode.OPERATION_ERROR, ErrorInfo.PUBLISH_ERROR);
      }
      //保存文章标签
      boolean b2 = passageTagMapper.insertPassageTags(tagIdList, newPassageId);
      if (!b2) {
        throw new BusinessException(ErrorCode.OPERATION_ERROR, ErrorInfo.PUBLISH_ERROR);
      }
      return true;
    }
  }

  @Cacheable(cacheNames = BLOG_CACHE_PREFIX + "otherPassages", key = "#p0")
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
    ArrayList<PassageTitleVO> passageTitleVOList = new ArrayList<>();
    list.forEach(passage -> {
      PassageTitleVO passageTitleVO = new PassageTitleVO();
      BeanUtils.copyProperties(passage, passageTitleVO);
      passageTitleVOList.add(passageTitleVO);
    });
    return passageTitleVOList;
  }

  @Transactional
  public void updatePassage(ParentPassageDTO updateParentPassageDTO) {
    Passage passage = new Passage();
    BeanUtils.copyProperties(updateParentPassageDTO, passage);
    passage.setPassageId(Long.valueOf(updateParentPassageDTO.getPassageId()));
    int b1 = passageMapper.updatePassage(passage);
    if (b1 != 1) {
      throw new BusinessException(ErrorCode.OPERATION_ERROR, ErrorInfo.PUBLISH_ERROR);
    }
    List<Long> tagIdList = updateParentPassageDTO.getTagIdList();
    if (tagIdList == null) {
      throw new BusinessException(ErrorCode.OPERATION_ERROR, ErrorInfo.PUBLISH_ERROR);
    }
    //更新passage_tag之前要删除老的数据
    passageTagMapper.deleteTagByPassageId(passage.getPassageId());
    //保存文章标签
    boolean b2 = passageTagMapper.insertPassageTags(tagIdList, passage.getPassageId());
    if (!b2) {
      throw new BusinessException(ErrorCode.OPERATION_ERROR, ErrorInfo.PUBLISH_ERROR);
    }
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
      //移除之前就存在的
      delayedQueue.remove(passageId);
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
   * @param queueKey
   * @return
   * @throws InterruptedException
   */
  public Long getDelayQueue(String queueKey) throws InterruptedException {
    RBlockingDeque<Long> blockingDeque = redissonClient.getBlockingDeque(queueKey);
    return blockingDeque.take();
  }


  //使用set集合存储点赞信息
  @Transactional
  public Boolean thumb(Long passageId) {

    Long userId = UserContext.getUser();
    if (userId == null) {
      return false;
    }
    /*
     * 同一个用户对一篇文章只能点赞一次，不能重复点赞，取消点赞亦然
     * 以passageId作为key，userId为value，存入redis 的zSet集合，利用set集合元素唯一不重复的特性，存储用户是否点赞
     * */

    String key = Common.PASSAGE_THUMB_KEY + passageId;
    Boolean isMember = stringRedisTemplate.opsForSet().isMember(key, userId.toString());
    if (Boolean.FALSE.equals(isMember)) {
      //插入用户点赞表
      UserThumbs userThumbs = UserThumbs.builder().userId(userId).passageId(passageId).build();
      int insert = userThumbsMapper.insert(userThumbs);
      //文章表和用户点赞表同时更新成功
      if (insert == 1) {
        Long add = stringRedisTemplate.opsForSet().add(key, userId.toString());
        if (add == 0L) {
          throw new BusinessException(ErrorCode.OPERATION_ERROR, ErrorInfo.REDIS_UPDATE_ERROR);
        }
      } else {
        throw new BusinessException(ErrorCode.OPERATION_ERROR, ErrorInfo.UPDATE_ERROR);
      }
    } else {
      //删除用户点赞表
      LambdaQueryWrapper<UserThumbs> queryWrapper = new LambdaQueryWrapper<>();
      queryWrapper.eq(UserThumbs::getUserId, userId).eq(UserThumbs::getPassageId, passageId);
      int delete = userThumbsMapper.delete(queryWrapper);
      if (delete == 1) {
        Long remove = stringRedisTemplate.opsForSet().remove(key, userId.toString());
        if (remove != 1) {
          throw new BusinessException(ErrorCode.OPERATION_ERROR, ErrorInfo.REDIS_UPDATE_ERROR);
        }
      } else {
        throw new BusinessException(ErrorCode.OPERATION_ERROR, ErrorInfo.UPDATE_ERROR);
      }
    }
    return true;
  }

  //使用bitmap存储点赞信息
  @Transactional
  @Override
  public Boolean thumbPassage(Long passageId) {
    Long userId = UserContext.getUser();
    if (userId == null) {
      return false;
    }
    String key = Common.PASSAGE_THUMB_KEY + passageId;
    Boolean isThumb = stringRedisTemplate.opsForValue().getBit(key, userId.intValue());
    if (Boolean.FALSE.equals(isThumb)) {
      //插入用户点赞表
      UserThumbs userThumbs = UserThumbs.builder().userId(userId).passageId(passageId).build();
      int insert = userThumbsMapper.insert(userThumbs);
      //文章表和用户点赞表同时更新成功
      if (insert == 1) {
//        返回false：表示该位置之前的值是 0（即该位置之前被设置为 false）。
        stringRedisTemplate.opsForValue().setBit(key, userId, true);
      } else {
        throw new BusinessException(ErrorCode.OPERATION_ERROR, ErrorInfo.UPDATE_ERROR);
      }
    } else {
      //删除用户点赞表
      LambdaQueryWrapper<UserThumbs> queryWrapper = new LambdaQueryWrapper<>();
      queryWrapper.eq(UserThumbs::getUserId, userId).eq(UserThumbs::getPassageId, passageId);
      int delete = userThumbsMapper.delete(queryWrapper);
      if (delete == 1) {
        stringRedisTemplate.opsForValue().setBit(key, userId, false);
      } else {
        throw new BusinessException(ErrorCode.OPERATION_ERROR, ErrorInfo.UPDATE_ERROR);
      }
    }
    return true;
  }


  @Transactional
  public Boolean collect(Long passageId) {
    Long userId = UserContext.getUser();
    if (userId == null) {
      return false;
    }
    String key = Common.PASSAGE_COLLECT_KEY + passageId;
    Boolean member = stringRedisTemplate.opsForSet().isMember(key, userId.toString());
    if (Boolean.FALSE.equals(member)) {
      //先插入mysql用户收藏表
      UserCollects userCollects = UserCollects.builder().userId(userId).passageId(passageId)
          .build();
      int insert = userCollectsMapper.insert(userCollects);
      if (insert == 1) {
        //写入redis
        Long add = stringRedisTemplate.opsForSet().add(key, userId.toString());
        // 增加文章的收藏量
        stringRedisTemplate.opsForZSet()
            .incrementScore(Common.TOP_COLLECT_PASSAGE, String.valueOf(passageId), 1);
        if (add == 0L) {
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
        Long remove = stringRedisTemplate.opsForSet().remove(key, userId.toString());
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

  @Transactional
  @Override
  public Boolean collectPassage(Long passageId) {
    Long userId = UserContext.getUser();
    if (userId == null) {
      return false;
    }
    String key = Common.PASSAGE_COLLECT_KEY + passageId;
    Boolean isCollect = stringRedisTemplate.opsForValue().getBit(key, userId.intValue());
    if (Boolean.FALSE.equals(isCollect)) {
      //先插入mysql用户收藏表
      UserCollects userCollects = UserCollects.builder().userId(userId).passageId(passageId)
          .build();
      int insert = userCollectsMapper.insert(userCollects);
      if (insert == 1) {
        stringRedisTemplate.opsForValue().setBit(key, userId, true);
        // 增加文章的收藏量
        stringRedisTemplate.opsForZSet()
            .incrementScore(Common.TOP_COLLECT_PASSAGE, String.valueOf(passageId), 1);
      } else {
        throw new BusinessException(ErrorCode.OPERATION_ERROR, ErrorInfo.UPDATE_ERROR);
      }
    } else {
      //删除用户收藏表
      LambdaQueryWrapper<UserCollects> queryWrapper = new LambdaQueryWrapper<>();
      queryWrapper.eq(UserCollects::getUserId, userId).eq(UserCollects::getPassageId, passageId);
      int delete = userCollectsMapper.delete(queryWrapper);
      if (delete == 1) {
        stringRedisTemplate.opsForValue().setBit(key, userId, false);
        stringRedisTemplate.opsForZSet()
            .incrementScore(Common.TOP_COLLECT_PASSAGE, String.valueOf(passageId), -1);
      } else {
        throw new BusinessException(ErrorCode.OPERATION_ERROR, ErrorInfo.UPDATE_ERROR);
      }
    }
    return true;
  }

  /**
   * top10
   *
   * @return
   * @Description:
   */
  @Cacheable(cacheNames = BLOG_CACHE_PREFIX + "topPassages", key = "'topPassagesCache'")
  @Override
  public List<PassageTitleVO> getTopPassages() {
    //根据viewNum降序获取前10
    IPUtil.isHotIp();
    Set<String> passageIdSet = stringRedisTemplate.opsForZSet()
        .reverseRange(Common.TOP_COLLECT_PASSAGE, 0, 9);
    List<Long> idlist = passageIdSet.stream().map(passageId -> Long.valueOf(passageId))
        .collect(Collectors.toList());
    if (idlist.isEmpty()) {
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

  @Cacheable(cacheNames = BLOG_CACHE_PREFIX + "passageContent", key = "#p0")
  @Override
  public PassageContentVO getPassageContentByPassageId(Long uid, Long pid) {
    if (pid == null) {
      throw new BusinessException(ErrorCode.PARAMS_ERROR, ErrorInfo.PARAMS_ERROR);
    }
    //判断是否是hotip
    IPUtil.isHotIp();
    //浏览量+1
    passageMapper.updateViewNum(pid);
//    PassageContentVO passageContent;
//    String hotKey = Common.HOT_PASSAGE_KEY + pid;
//    Object hotPassage = JdHotKeyStore.getValue(hotKey);
//    if (hotPassage == null) {
//      passageContent = passageMapper.getPassageContentByPid(uid, pid);
//      JdHotKeyStore.smartSet(hotKey, passageContent);
//      return passageContent;
//    }
    return passageMapper.getPassageContentByPid(uid, pid);

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


  @Override
  public String uploadPassageCover(MultipartFile img) {
    String imgUrl = AliOssUtil.uploadImageOSS(img);
    log.info("img url：" + imgUrl);
    return imgUrl;
  }


  @Override
  public String uploadPassageImg(MultipartFile img) {
    return AliOssUtil.uploadImageOSS(img);
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
      int commentCount = commentMapper.countCommentNum(passage.getPassageId());
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
   *
   * @param passageId
   * @return
   */
  @Override
  @Transactional
  public boolean deleteByPassageId(Long passageId) {
    int i=passageMapper.deleteById(passageId);
    if (i!=1) {
      throw new BusinessException(ErrorCode.OPERATION_ERROR, ErrorInfo.DELETE_ERROR);
    }
    commentMapper.deleteByPassageId(passageId);
    userCollectsMapper.deleteByPassageId(passageId);
    userThumbsMapper.deleteByPassageId(passageId);
    passageTagMapper.deleteByPassageId(passageId);
    stringRedisTemplate.opsForZSet().remove(Common.TOP_COLLECT_PASSAGE, String.valueOf(passageId));
    stringRedisTemplate.delete(Common.PASSAGE_COLLECT_KEY + passageId);
    stringRedisTemplate.delete(Common.PASSAGE_THUMB_KEY + passageId);
    return true;
  }


}




