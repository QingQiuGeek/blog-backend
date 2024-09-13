create database if not exists blog;
use blog;
create table if not exists user
(
    userId     bigint comment '用户ID' primary key auto_increment,
    avatarUrl  varchar(255) default 'https://tse1-mm.cn.bing.net/th/id/OIP-C.tDbFzIxORkvKYnfklqqA6QHaHa?w=196&h=196&c=7&r=0&o=5&dpr=2&pid=1.7' comment '头像URL',
    sex        tinyint          not null default 2 comment '性别(0女,1男,2未知)',
    profiles   varchar(1024) comment '用户简介',
    userName   varchar(255) not null comment '用户名,可用于登录',
    password   varchar(255) not null comment '密码',
    mail       varchar(100) comment '邮箱',
    phone      varchar(50) comment '电话,预留字段',
    role       tinyint  default 0 not null comment '角色(0普通用户,1管理员)',
    accessKey  varchar(100) comment '预留字段',
    secretKey  varchar(100) comment '预留字段',
    level      tinyint          not null default 0 comment '预留字段,用户等级',
    createTime datetime  default CURRENT_TIMESTAMP   not null comment '创建时间',
    updateTime datetime  default CURRENT_TIMESTAMP on update CURRENT_TIMESTAMP  not null comment '修改时间',
    status     tinyint         not null default 1 comment '用户状态(0禁用,1正常)',
    isDelete   tinyint          not null default 1 comment '0逻辑删除'
) comment ='用户表';

create table if not exists user_collects
(
    userId        bigint comment '用户id' primary key auto_increment,
    passageId     bigint comment '文章id',
    collectStatus tinyint default 1 comment '0取消收藏'
) comment ='用户-收藏表';

create table if not exists passage
(
    passageId  bigint comment '文章id' primary key auto_increment,
    authorId   bigint          not null comment '作者id,逻辑关联用户表',
    authorName varchar(255) not null comment '作者名，逻辑关联用户表',
    title      varchar(255) not null comment '文章标题',
    content    text         not null comment '文章内容',
    thumbnail  varchar(255) comment '预览图URL',
    summary    varchar(512) not null comment '内容摘要',
    categoryId tinyint not null comment '文章所属类别',
    viewNum    int          not null default 0 comment '浏览量',
    commentNum int          not null default 0 comment '评论数量',
    thumbNum   int          not null default 0 comment '点赞数量',
    collectNum int          not null default 0 comment '收藏数量',
    createTime datetime default CURRENT_TIMESTAMP   not null comment '发布时间',
    updateTime datetime default CURRENT_TIMESTAMP on update CURRENT_TIMESTAMP comment '修改时间',
    accessTime datetime default null comment '审核通过时间',
    status     tinyint          not null default 0 comment '文章状态(0草稿,1待审核,2已发布)',
    isDelete   tinyint          not null default 1 comment '0逻辑删除'
) comment ='文章表';

create table if not exists tag
(
    tagId        bigint auto_increment primary key comment '标签id',
    tagName      varchar(20) not null comment '标签名',
    parentCategoryId tinyint not null  comment '标签所属类别id',
    createUserId bigint comment '创建标签的用户id',
    createTime   datetime  default CURRENT_TIMESTAMP  not null comment '发布时间',
    updateTime   datetime default CURRENT_TIMESTAMP on update CURRENT_TIMESTAMP comment '修改时间',
    isDelete     tinyint         not null default 1 comment '0逻辑删除'
) comment '标签表';

create table if not exists category
(
    categoryId       bigint auto_increment primary key comment '类别id',
    categoryName     varchar(100) not null comment '类别名',
    createTime       datetime default CURRENT_TIMESTAMP     not null comment '创建时间',
    updateTime       datetime default CURRENT_TIMESTAMP on update CURRENT_TIMESTAMP comment '修改时间',
    isDelete         tinyint          not null default 1 comment '0逻辑删除'
) comment ='类别表';

create table if not exists category_tag
(
    categoryId bigint primary key auto_increment comment '类别id',
    tagId     bigint not null comment '标签id'
) comment ='类别-标签表';

create table if not exists comment
(
    commentId       bigint primary key auto_increment comment '评论id',
    content         varchar(512) not null comment '评论的内容',
    commentUserId   bigint          not null comment '评论的用户id',
    passageId       bigint          not null comment '评论的文章id ',
    toCommentId     bigint comment '回复目标评论id',
    toCommentUserId bigint          not null comment '回复目标用户id',
    commentTime      datetime default CURRENT_TIMESTAMP    not null comment '评论时间',
    isDelete        tinyint default 1 comment '0逻辑删除'
) comment ='评论表';

create table if not exists userFollow
(
    id       bigint primary key auto_increment comment '关注id',
    userId   bigint comment '关注的用户id',
    toUserId bigint comment '被关注的用户id',
    followTime   datetime default CURRENT_TIMESTAMP comment '关注时间',
    isDelete tinyint default 1 comment '0逻辑删除取消关注'
) comment ='用户关注表';

create table if not exists letter
(
    id       bigint primary key auto_increment comment '私信id',
    userId   bigint comment '私信的用户id',
    toUserId bigint comment '被私信的用户id',
    letterTime   datetime default CURRENT_TIMESTAMP comment '私信时间',
    content  varchar(512) comment '私信内容'
) comment ='私信表';