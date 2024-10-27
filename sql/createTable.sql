drop database if exists blog;
create database blog;
use blog;

create table category
(
    categoryId   bigint auto_increment comment '类别id'
        primary key,
    categoryName varchar(100)                       not null comment '类别名',
    createTime   datetime default CURRENT_TIMESTAMP not null comment '创建时间',
    updateTime   datetime default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP comment '修改时间',
    isDelete     tinyint  default 1                 not null comment '0逻辑删除'
)
    comment '类别表';

create table category_tag
(
    categoryId bigint auto_increment comment '类别id'
        primary key,
    tagId      bigint not null comment '标签id'
)
    comment '类别-标签表';

create table comment
(
    commentId       bigint auto_increment comment '评论id'
        primary key,
    content         varchar(512)                       not null comment '评论的内容',
    commentUserId   bigint                             not null comment '评论的用户id',
    passageId       bigint                             not null comment '评论的文章id ',
    toCommentId     bigint                             null comment '回复目标评论id',
    toCommentUserId bigint                             not null comment '回复目标用户id',
    commentTime     datetime default CURRENT_TIMESTAMP not null comment '评论时间',
    isDelete        tinyint  default 1                 null comment '0逻辑删除'
)
    comment '评论表';

create table letter
(
    id         bigint auto_increment comment '私信id'
        primary key,
    userId     bigint                             null comment '私信的用户id',
    toUserId   bigint                             null comment '被私信的用户id',
    letterTime datetime default CURRENT_TIMESTAMP null comment '私信时间',
    content    varchar(512)                       null comment '私信内容',
    isDelete   tinyint  default 1                 null comment '0逻辑删除私信'
)
    comment '私信表';

create table passage
(
    passageId  bigint auto_increment comment '文章id'
        primary key,
    authorId   bigint                             not null comment '作者id,逻辑关联用户表',
    authorName varchar(255)                       not null comment '作者名，逻辑关联用户表',
    title      varchar(255)                       not null comment '文章标题',
    content    text                               not null comment '文章内容',
    thumbnail  varchar(255)                       null comment '预览图URL',
    summary    varchar(512)                       not null comment '内容摘要',
    categoryId tinyint  default 1                 not null comment '文章所属类别',
    pTags      varchar(512)                       null comment '文章标签(json数组)',
    viewNum    int      default 0                 not null comment '浏览量',
    commentNum int      default 0                 not null comment '评论数量',
    thumbNum   int      default 0                 not null comment '点赞数量',
    collectNum int      default 0                 not null comment '收藏数量',
    createTime datetime default CURRENT_TIMESTAMP not null comment '发布时间',
    updateTime datetime default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP comment '修改时间',
    accessTime datetime default (now())           null comment '审核通过时间',
    status     tinyint  default 0                 not null comment '文章状态(0草稿,1待审核,2已发布)',
    isDelete   tinyint  default 1                 not null comment '0逻辑删除'
)
    comment '文章表';



create table tag
(
    tagId            bigint auto_increment comment '标签id'
        primary key,
    tagName          varchar(20)                        not null comment '标签名',
    parentCategoryId tinyint                            not null comment '标签所属类别id',
    createUserId     bigint                             null comment '创建标签的用户id',
    createTime       datetime default CURRENT_TIMESTAMP not null comment '发布时间',
    updateTime       datetime default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP comment '修改时间',
    isDelete         tinyint  default 1                 not null comment '0逻辑删除'
)
    comment '标签表';

use blog;
create table user_collects
(
    id bigint not null auto_increment comment '主键id' primary key ,
    userId       bigint not null  comment '用户id',
    passageId     bigint           not null comment '文章id',
    collectTime datetime default CURRENT_TIMESTAMP null comment '收藏时间'
)comment '用户-收藏表';

create table user_thumbs
(
    id bigint not null auto_increment comment '主键id' primary key ,
    userId       bigint not null comment '用户id',
    passageId     bigint           not null comment '文章id',
    thumbTime datetime default CURRENT_TIMESTAMP null comment '点赞时间'
)comment '用户-点赞表';


create table user_follow
(
    id bigint not null auto_increment comment '主键id' primary key ,
    userId     bigint  not null  comment '关注的用户id'  ,
    toUserId   bigint                  not           null comment '被关注的用户id',
    followTime datetime default CURRENT_TIMESTAMP null comment '关注时间'
) comment '用户关注表';



create table user
(
    userId      bigint auto_increment comment '用户ID'
        primary key,
    avatarUrl   varchar(255)  default 'https://ooo.0x0.ooo/2024/10/19/ODGg2t.jpg' null comment '头像URL',
    userName    varchar(255)                                                      not null comment '用户名',
    sex         tinyint       default 2                                           not null comment '性别(0女,1男,2未知)',
    profiles    varchar(1024) default '这个人很神秘，什么也没有留下'               null comment '用户简介',
    interestTag varchar(255)  default '["java", "大数据"]'                        null comment '预留字段，用户兴趣标签（json字段）',
    userAccount varchar(255)                                                      null comment '账户,可用于登录',
    password    varchar(255)                                                      not null comment '密码',
    mail        varchar(255)                                                      not null comment '邮箱',
    phone       varchar(100)                                                      null comment '电话,预留字段',
    role        tinyint       default 0                                           not null comment '角色(0普通用户,1管理员)',
    accessKey   varchar(100)                                                      null comment '预留字段',
    secretKey   varchar(100)                                                      null comment '预留字段',
    level       tinyint       default 0                                           not null comment '预留字段,用户等级',
    createTime  datetime      default CURRENT_TIMESTAMP                           not null comment '创建时间',
    updateTime  datetime      default CURRENT_TIMESTAMP                           not null on update CURRENT_TIMESTAMP comment '修改时间',
    status      tinyint       default 1                                           not null comment '用户状态(0禁用,1正常)',
    isDelete    tinyint       default 1                                           not null comment '0逻辑删除'
)
    comment '用户表';



use blog;

select * from passage ;
select * from blog.passage where updateTime>=5 and isDelete=1 and status=2;


