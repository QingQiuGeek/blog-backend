create database if not exists blog;
use blog;
create table if not exists user
(
    userId     bigint comment '用户ID' primary key auto_increment,
    avatarUrl  varchar(255) default 'https://tse1-mm.cn.bing.net/th/id/OIP-C.tDbFzIxORkvKYnfklqqA6QHaHa?w=196&h=196&c=7&r=0&o=5&dpr=2&pid=1.7' comment '头像URL',
    userName   varchar(255) not null comment '用户名',
    sex        tinyint          not null default 2 comment '性别(0女,1男,2未知)',
    profiles   varchar(1024) comment '用户简介',
    interestTag varchar(255) comment '预留字段，用户兴趣标签',
    userAccount varchar(255) not null comment '账户,可用于登录',
    password   varchar(255) not null default '77f9e4a38efa891de9274594304597fc' comment '密码,默认123456，前提是salt不变',
    mail       varchar(255) comment '邮箱',
    phone      varchar(100) comment '电话,预留字段',
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
    thumbnail  varchar(255) default 'https://tse4-mm.cn.bing.net/th/id/OIP-C.z2_cjOXPEO-KHj0luhz1rwHaHT?w=224&h=181&c=7&r=0&o=5&dpr=2&pid=1.7' comment '预览图URL',
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

insert into passage(authorId,authorName,title,content,summary,categoryId,accessTime,status)values
            (2,'Serein','hahah','hahahhhhh','hahahahha',2,now(),2),
            (2,'Serein','hahah','hahahhhhh','hahahahha',2,now(),2),
            (2,'Serein','hahah','hahahhhhh','hahahahha',2,now(),2),
            (2,'Serein','hahah','hahahhhhh','hahahahha',2,now(),2),
            (2,'Serein','hahah','hahahhhhh','hahahahha',2,now(),2)


create table if not exists tag
(
    tagId        bigint auto_increment primary key comment '标签id',
    tagName      varchar(20) not null comment '标签名，字数限制',
    parentCategoryId tinyint not null  comment '标签所属类别id',
    createUserId bigint comment '创建标签的用户id',
    createTime   datetime  default CURRENT_TIMESTAMP  not null comment '发布时间',
    updateTime   datetime default CURRENT_TIMESTAMP on update CURRENT_TIMESTAMP comment '修改时间',
    isDelete     tinyint         not null default 1 comment '0逻辑删除'
) comment '标签表';

create table if not exists category
(
    categoryId       bigint auto_increment primary key comment '类别id',
    categoryName     varchar(100) not null comment '类别名，字数限制',
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
    content  varchar(512) comment '私信内容',
    isDelete tinyint default 1 comment '0逻辑删除私信'
) comment ='私信表';


INSERT INTO passage (authorId, authorName, title, content, thumbnail, summary, categoryId, viewNum, commentNum, thumbNum, collectNum, createTime, updateTime, accessTime, status) VALUES
   (1, '张三', '第三十篇文章', '这是第一篇文章的内容，介绍了很多内容。', 'http://example.com/thumb1.jpg', '这是第一篇文章的摘要。', 1, 100, 5, 10, 15, '2024-09-01 10:00:00', '2024-09-01 10:00:00', null, 2),
   (2, '李四', '第三十一篇文章', '这是第二篇文章的内容，深入讨论了相关话题。', 'http://example.com/thumb2.jpg', '这是第二篇文章的摘要。', 2, 200, 10, 20, 25, '2024-09-02 11:00:00', '2024-09-02 11:00:00', '2024-09-03 12:00:00', 2),
   (3, '王五', '第三十二篇文章', '这是第三篇文章的内容，讲述了很多趣闻。', 'http://example.com/thumb3.jpg', '这是第三篇文章的摘要。', 1, 150, 7, 15, 18, '2024-09-03 12:30:00', '2024-09-03 12:30:00', null, 1),
   (4, '赵六', '第三十三篇文章', '这是第四篇文章的内容，提供了一些见解。', 'http://example.com/thumb4.jpg', '这是第四篇文章的摘要。', 3, 300, 12, 30, 35, '2024-09-04 14:00:00', '2024-09-04 14:00:00', '2024-09-05 15:00:00', 2),
   (5, '周七', '第三十四篇文章', '这是第五篇文章的内容，探讨了有趣的话题。', 'http://example.com/thumb5.jpg', '这是第五篇文章的摘要。', 2, 50, 3, 5, 10, '2024-09-05 16:00:00', '2024-09-05 16:00:00', null, 0),
   (1, '张三', '第三十五篇文章', '这是第一篇文章的内容，详细介绍了各种技术的应用，如人工智能、机器学习以及它们在现代生活中的重要性。通过案例分析，我们可以看到这些技术如何改变我们的工作方式。', 'http://example.com/thumb1.jpg', '深入探讨技术应用的文章。', 1, 120, 6, 12, 20, '2024-09-06 09:00:00', '2024-09-06 09:00:00', null, 2),
   (2, '李四', '第三十六篇文章', '第二篇文章主要探讨了环保的重要性。我们分析了全球变暖的原因，以及个人如何通过日常生活中的小改变为保护环境做出贡献。', 'http://example.com/thumb2.jpg', '探讨环保的意义与实践。', 2, 250, 15, 25, 30, '2024-09-07 10:00:00', '2024-09-07 10:00:00', '2024-09-08 11:00:00', 2),
   (3, '王五', '第三十七篇文章', '这篇文章讲述了旅行的魅力，分享了不同文化的独特之处，以及旅行如何开阔我们的视野。通过作者的亲身经历，我们感受到世界的多样性。', 'http://example.com/thumb3.jpg', '分享旅行经历与文化的文章。', 1, 180, 8, 18, 22, '2024-09-08 11:30:00', '2024-09-08 11:30:00', null, 1),
   (2, '赵六', '第三十八篇文章', '在这篇文章中，我们将分析经济发展的趋势，探讨数字经济如何影响传统产业。通过数据和图表，读者将获得深入的见解。', 'http://example.com/thumb4.jpg', '经济发展趋势的深度分析。', 3, 320, 20, 40, 45, '2024-09-09 14:00:00', '2024-09-09 14:00:00', '2024-09-10 15:00:00', 2),
   (1, '周七', '第三十九篇文章', '本文探讨了心理健康的重要性，分析了压力管理的方法，并分享了一些有效的放松技巧，帮助读者在快节奏的生活中找到内心的平静。', 'http://example.com/thumb5.jpg', '心理健康与压力管理的实用指南。', 2, 75, 5, 10, 15, '2024-09-10 16:00:00', '2024-09-10 16:00:00', null, 0),
   (5, '钱八', '第四十篇文章', '这篇文章介绍了现代科技对教育的影响，探讨了在线学习和传统学习的优缺点，以及未来教育的可能发展方向。', 'http://example.com/thumb6.jpg', '现代科技与教育的结合探讨。', 1, 210, 12, 15, 25, '2024-09-11 09:00:00', '2024-09-11 09:00:00', '2024-09-12 10:30:00', 2),
   (4, '孙九', '第四十一篇文章', '本文围绕健康饮食展开，分析了不同饮食方式对身体的影响，提供了一些实用的健康食谱和饮食建议。', 'http://example.com/thumb7.jpg', '健康饮食的重要性与实用建议。', 2, 95, 7, 20, 12, '2024-09-12 11:00:00', '2024-09-12 11:00:00', '2024-09-12 14:00:00', 1),
   (3, '周十', '第四十二篇文章', '这篇文章讲述了艺术在生活中的角色，分析了不同艺术形式对人们情感的影响，以及艺术如何激励创新思维。', 'http://example.com/thumb8.jpg', '艺术对生活与创新的影响。', 3, 145, 10, 18, 22, '2024-09-13 08:30:00', '2024-09-13 08:30:00', '2024-09-14 09:00:00', 2),
   (2, '吴十一', '第四十三篇文章', '本文探讨了社交媒体对人际关系的影响，分析了网络社交的优缺点，并提供了一些维护良好关系的建议。', 'http://example.com/thumb9.jpg', '社交媒体与人际关系的深度分析。', 1, 310, 25, 35, 40, '2024-09-14 15:00:00', '2024-09-14 15:00:00', '2024-09-15 16:00:00', 2),
   (1, '郑十二', '第四十四篇文章', '这篇文章介绍了如何提高工作效率，分析了时间管理和任务优先级的策略，帮助读者在快节奏的工作中更好地规划自己的时间。', 'http://example.com/thumb10.jpg', '提升工作效率的实用技巧。', 2, 160, 9, 30, 20, '2024-09-15 10:00:00', '2024-09-15 10:00:00', '2024-09-16 11:30:00', 0),
   (1, '李四', '第十一篇文章', '讨论可持续发展的重要性与实践案例。', 'http://example.com/thumb11.jpg', '可持续发展案例分析。', 1, 120, 15, 10, 5, '2024-09-16 10:00:00', '2024-09-16 10:00:00', '2024-09-17 11:00:00', 1),
   (2, '张三', '第十二篇文章', '介绍个人理财的基本知识和投资建议。', 'http://example.com/thumb12.jpg', '个人理财与投资建议。', 2, 180, 20, 15, 10, '2024-09-17 09:00:00', '2024-09-17 09:00:00', '2024-09-18 14:00:00', 1),
   (3, '王五', '第十三篇文章', '探索科技在医疗领域的应用与前景。', 'http://example.com/thumb13.jpg', '科技与医疗的结合。', 1, 220, 25, 20, 15, '2024-09-18 11:00:00', '2024-09-18 11:00:00', '2024-09-19 12:00:00', 2),
   (4, '赵六', '第十四篇文章', '分析心理健康的重要性及应对策略。', 'http://example.com/thumb14.jpg', '心理健康与应对策略。', 2, 95, 10, 25, 5, '2024-09-19 08:30:00', '2024-09-19 08:30:00', '2024-09-20 09:30:00', 1),
   (5, '钱八', '第十五篇文章', '探讨气候变化对生态环境的影响。', 'http://example.com/thumb15.jpg', '气候变化与生态环境。', 3, 130, 8, 18, 7, '2024-09-20 09:00:00', '2024-09-20 09:00:00', '2024-09-21 10:00:00', 2),
   (1, '李四', '第十六篇文章', '分享有效的学习方法与技巧。', 'http://example.com/thumb16.jpg', '学习方法与技巧。', 1, 150, 12, 12, 8, '2024-09-21 11:00:00', '2024-09-21 11:00:00', '2024-09-22 11:30:00', 0),
   (2, '张三', '第十七篇文章', '探讨企业管理中的创新策略。', 'http://example.com/thumb17.jpg', '企业管理与创新。', 2, 210, 14, 30, 12, '2024-09-22 10:00:00', '2024-09-22 10:00:00', '2024-09-23 12:00:00', 1),
   (3, '王五', '第十八篇文章', '解析网络安全的威胁与防护措施。', 'http://example.com/thumb18.jpg', '网络安全分析与防护。', 1, 170, 18, 20, 15, '2024-09-23 09:00:00', '2024-09-23 09:00:00', '2024-09-24 14:00:00', 2),
   (4, '赵六', '第十九篇文章', '讨论文化多样性的重要性及其影响。', 'http://example.com/thumb19.jpg', '文化多样性与影响。', 2, 190, 16, 22, 11, '2024-09-24 10:30:00', '2024-09-24 10:30:00', '2024-09-25 09:00:00', 1),
   (5, '钱八', '第二十篇文章', '分析未来工作趋势与职场技能要求。', 'http://example.com/thumb20.jpg', '未来工作与职场技能。', 3, 250, 21, 35, 20, '2024-09-25 11:00:00', '2024-09-25 11:00:00', '2024-09-26 10:30:00', 2),
   (1, '李四', '第二十一篇文章', '探讨社会企业的概念与实例。', 'http://example.com/thumb21.jpg', '社会企业概念与实例。', 1, 300, 30, 25, 15, '2024-09-26 09:00:00', '2024-09-26 09:00:00', '2024-09-27 11:00:00', 0),
   (2, '张三', '第二十二篇文章', '分析人工智能对各行业的影响。', 'http://example.com/thumb22.jpg', '人工智能行业影响分析。', 2, 160, 10, 28, 14, '2024-09-27 10:00:00', '2024-09-27 10:00:00', '2024-09-28 12:00:00', 1),
   (3, '王五', '第二十三篇文章', '讨论健康科技的未来发展趋势。', 'http://example.com/thumb23.jpg', '健康科技未来趋势。', 1, 140, 5, 10, 6, '2024-09-28 08:30:00', '2024-09-28 08:30:00', '2024-09-29 09:30:00', 2),
   (4, '赵六', '第二十四篇文章', '探讨环境保护与可持续发展的关系。', 'http://example.com/thumb24.jpg', '环境保护与可持续发展。', 2, 180, 12, 15, 9, '2024-09-29 11:00:00', '2024-09-29 11:00:00', '2024-09-30 10:00:00', 1),
   (5, '钱八', '第二十五篇文章', '分析教育科技的最新发展动态。', 'http://example.com/thumb25.jpg', '教育科技发展动态。', 3, 210, 15, 22, 18, '2024-09-30 09:00:00', '2024-09-30 09:00:00', '2024-10-01 14:00:00', 0),
   (1, '李四', '第二十六篇文章', '探讨城市化对社会结构的影响。', 'http://example.com/thumb26.jpg', '城市化与社会结构影响。', 1, 230, 20, 20, 12, '2024-10-01 10:00:00', '2024-10-01 10:00:00', '2024-10-02 12:00:00', 1),
   (2, '张三', '第二十七篇文章', '分析未来食品科技的潜力与挑战。', 'http://example.com/thumb27.jpg', '未来食品科技分析。', 2, 190, 14, 17, 10, '2024-10-02 11:00:00', '2024-10-02 11:00:00', '2024-10-03 09:00:00', 2),
   (3, '王五', '第二十八篇文章', '探讨旅游业的可持续发展策略。', 'http://example.com/thumb28.jpg', '旅游业可持续发展策略。', 1, 250, 18, 25, 15, '2024-10-03 10:30:00', '2024-10-03 10:30:00', '2024-10-04 11:00:00', 1),
   (4, '赵六', '第二十九篇文章', '分析数字化转型对企业的影响。', 'http://example.com/thumb29.jpg', '数字化转型与企业影响。', 2, 210, 12, 30, 20, '2024-10-04 11:00:00', '2024-10-04 11:00:00', '2024-10-05 10:00:00', 0);


INSERT INTO user (avatarUrl, userName, sex, profiles, interestTag, userAccount, password, mail, phone, role, accessKey, secretKey, level, createTime, updateTime, status, isDelete) VALUES
    ('https://tse1-mm.cn.bing.net/th/id/OIP-C.tDbFzIxORkvKYnfklqqA6QHaHa?w=196&h=196&c=7&r=0&o=5&dpr=2&pid=1.7', 'Alice', 0, '热爱旅行与摄影', '旅游,摄影', 'alice@example.com', 'hashed_password_1', 'alice@example.com', '12345678901', 0, NULL, NULL, 1, NOW(), NOW(), 1, 1),
    ('https://tse1-mm.cn.bing.net/th/id/OIP-C.tDbFzIxORkvKYnfklqqA6QHaHa?w=196&h=196&c=7&r=0&o=5&dpr=2&pid=1.7', 'Bob', 1, '技术爱好者', '编程,游戏', 'bob@example.com', 'hashed_password_2', 'bob@example.com', '12345678902', 0, NULL, NULL, 2, NOW(), NOW(), 1, 1),
    ('https://tse1-mm.cn.bing.net/th/id/OIP-C.tDbFzIxORkvKYnfklqqA6QHaHa?w=196&h=196&c=7&r=0&o=5&dpr=2&pid=1.7', 'Charlie', 2, '健身爱好者', '健身,阅读', 'charlie@example.com', 'hashed_password_3', 'charlie@example.com', '12345678903', 0, NULL, NULL, 1, NOW(), NOW(), 1, 1),
    ('https://tse1-mm.cn.bing.net/th/id/OIP-C.tDbFzIxORkvKYnfklqqA6QHaHa?w=196&h=196&c=7&r=0&o=5&dpr=2&pid=1.7', 'David', 1, '喜欢音乐与舞蹈', '音乐,舞蹈', 'david@example.com', 'hashed_password_4', 'david@example.com', '12345678904', 0, NULL, NULL, 1, NOW(), NOW(), 1, 1),
    ('https://tse1-mm.cn.bing.net/th/id/OIP-C.tDbFzIxORkvKYnfklqqA6QHaHa?w=196&h=196&c=7&r=0&o=5&dpr=2&pid=1.7', 'Eva', 0, '美食达人', '美食,烹饪', 'eva@example.com', 'hashed_password_5', 'eva@example.com', '12345678905', 0, NULL, NULL, 1, NOW(), NOW(), 1, 1),
    ('https://tse1-mm.cn.bing.net/th/id/OIP-C.tDbFzIxORkvKYnfklqqA6QHaHa?w=196&h=196&c=7&r=0&o=5&dpr=2&pid=1.7', 'Frank', 1, '电影爱好者', '电影,戏剧', 'frank@example.com', 'hashed_password_6', 'frank@example.com', '12345678906', 0, NULL, NULL, 1, NOW(), NOW(), 1, 1),
    ('https://tse1-mm.cn.bing.net/th/id/OIP-C.tDbFzIxORkvKYnfklqqA6QHaHa?w=196&h=196&c=7&r=0&o=5&dpr=2&pid=1.7', 'Grace', 0, '绘画与艺术', '艺术,绘画', 'grace@example.com', 'hashed_password_7', 'grace@example.com', '12345678907', 0, NULL, NULL, 1, NOW(), NOW(), 1, 1),
    ('https://tse1-mm.cn.bing.net/th/id/OIP-C.tDbFzIxORkvKYnfklqqA6QHaHa?w=196&h=196&c=7&r=0&o=5&dpr=2&pid=1.7', 'Henry', 1, '科技迷', '科技,创新', 'henry@example.com', 'hashed_password_8', 'henry@example.com', '12345678908', 0, NULL, NULL, 1, NOW(), NOW(), 1, 1),
    ('https://tse1-mm.cn.bing.net/th/id/OIP-C.tDbFzIxORkvKYnfklqqA6QHaHa?w=196&h=196&c=7&r=0&o=5&dpr=2&pid=1.7', 'Ivy', 2, '热爱自然', '户外,环保', 'ivy@example.com', 'hashed_password_9', 'ivy@example.com', '12345678909', 0, NULL, NULL, 1, NOW(), NOW(), 1, 1),
    ('https://tse1-mm.cn.bing.net/th/id/OIP-C.tDbFzIxORkvKYnfklqqA6QHaHa?w=196&h=196&c=7&r=0&o=5&dpr=2&pid=1.7', 'Jack', 1, '运动爱好者', '跑步,足球', 'jack@example.com', 'hashed_password_10', 'jack@example.com', '12345678910', 0, NULL, NULL, 1, NOW(), NOW(), 1, 1),
    ('https://tse1-mm.cn.bing.net/th/id/OIP-C.tDbFzIxORkvKYnfklqqA6QHaHa?w=196&h=196&c=7&r=0&o=5&dpr=2&pid=1.7', 'Kathy', 0, '书籍爱好者', '阅读,写作', 'kathy@example.com', 'hashed_password_11', 'kathy@example.com', '12345678911', 0, NULL, NULL, 1, NOW(), NOW(), 1, 1),
    ('https://tse1-mm.cn.bing.net/th/id/OIP-C.tDbFzIxORkvKYnfklqqA6QHaHa?w=196&h=196&c=7&r=0&o=5&dpr=2&pid=1.7', 'Leo', 1, '动漫迷', '动漫,游戏', 'leo@example.com', 'hashed_password_12', 'leo@example.com', '12345678912', 0, NULL, NULL, 1, NOW(), NOW(), 1, 1),
    ('https://tse1-mm.cn.bing.net/th/id/OIP-C.tDbFzIxORkvKYnfklqqA6QHaHa?w=196&h=196&c=7&r=0&o=5&dpr=2&pid=1.7', 'Mia', 2, '动物爱好者', '动物,保护', 'mia@example.com', 'hashed_password_13', 'mia@example.com', '12345678913', 0, NULL, NULL, 1, NOW(), NOW(), 1, 1),
    ('https://tse1-mm.cn.bing.net/th/id/OIP-C.tDbFzIxORkvKYnfklqqA6QHaHa?w=196&h=196&c=7&r=0&o=5&dpr=2&pid=1.7', 'Nina', 0, '时尚达人', '时尚,购物', 'nina@example.com', 'hashed_password_14', 'nina@example.com', '12345678914', 0, NULL, NULL, 1, NOW(), NOW(), 1, 1),
    ('https://tse1-mm.cn.bing.net/th/id/OIP-C.tDbFzIxORkvKYnfklqqA6QHaHa?w=196&h=196&c=7&r=0&o=5&dpr=2&pid=1.7', 'Oscar', 1, '摄影爱好者', '摄影,旅行', 'oscar@example.com', 'hashed_password_15', 'oscar@example.com', '12345678915', 0, NULL, NULL, 1, NOW(), NOW(), 1, 1),
    ('https://tse1-mm.cn.bing.net/th/id/OIP-C.tDbFzIxORkvKYnfklqqA6QHaHa?w=196&h=196&c=7&r=0&o=5&dpr=2&pid=1.7', 'Penny', 0, '创意设计', '设计,艺术', 'penny@example.com', 'hashed_password_16', 'penny@example.com', '12345678916', 0, NULL, NULL, 1, NOW(), NOW(), 1, 1),
    ('https://tse1-mm.cn.bing.net/th/id/OIP-C.tDbFzIxORkvKYnfklqqA6QHaHa?w=196&h=196&c=7&r=0&o=5&dpr=2&pid=1.7', 'Alice', 0, '热爱旅行与摄影', '旅游,摄影', 'alice@example.com', 'hashed_password_1', 'alice@example.com', '12345678901', 0, NULL, NULL, 1, NOW(), NOW(), 1, 1),
    ('https://tse1-mm.cn.bing.net/th/id/OIP-C.tDbFzIxORkvKYnfklqqA6QHaHa?w=196&h=196&c=7&r=0&o=5&dpr=2&pid=1.7', 'Bob', 1, '技术爱好者', '编程,游戏', 'bob@example.com', 'hashed_password_2', 'bob@example.com', '12345678902', 0, NULL, NULL, 2, NOW(), NOW(), 1, 1),
    ('https://tse1-mm.cn.bing.net/th/id/OIP-C.tDbFzIxORkvKYnfklqqA6QHaHa?w=196&h=196&c=7&r=0&o=5&dpr=2&pid=1.7', 'Charlie', 2, '健身爱好者', '健身,阅读', 'charlie@example.com', 'hashed_password_3', 'charlie@example.com', '12345678903', 0, NULL, NULL, 1, NOW(), NOW(), 1, 1),
    ('https://tse1-mm.cn.bing.net/th/id/OIP-C.tDbFzIxORkvKYnfklqqA6QHaHa?w=196&h=196&c=7&r=0&o=5&dpr=2&pid=1.7', 'David', 1, '喜欢音乐与舞蹈', '音乐,舞蹈', 'david@example.com', 'hashed_password_4', 'david@example.com', '12345678904', 0, NULL, NULL, 1, NOW(), NOW(), 1, 1),
    ('https://tse1-mm.cn.bing.net/th/id/OIP-C.tDbFzIxORkvKYnfklqqA6QHaHa?w=196&h=196&c=7&r=0&o=5&dpr=2&pid=1.7', 'Eva', 0, '美食达人', '美食,烹饪', 'eva@example.com', 'hashed_password_5', 'eva@example.com', '12345678905', 0, NULL, NULL, 1, NOW(), NOW(), 1, 1),
    ('https://tse1-mm.cn.bing.net/th/id/OIP-C.tDbFzIxORkvKYnfklqqA6QHaHa?w=196&h=196&c=7&r=0&o=5&dpr=2&pid=1.7', 'Frank', 1, '电影爱好者', '电影,戏剧', 'frank@example.com', 'hashed_password_6', 'frank@example.com', '12345678906', 0, NULL, NULL, 1, NOW(), NOW(), 1, 1),
    ('https://tse1-mm.cn.bing.net/th/id/OIP-C.tDbFzIxORkvKYnfklqqA6QHaHa?w=196&h=196&c=7&r=0&o=5&dpr=2&pid=1.7', 'Grace', 0, '绘画与艺术', '艺术,绘画', 'grace@example.com', 'hashed_password_7', 'grace@example.com', '12345678907', 0, NULL, NULL, 1, NOW(), NOW(), 1, 1),
    ('https://tse1-mm.cn.bing.net/th/id/OIP-C.tDbFzIxORkvKYnfklqqA6QHaHa?w=196&h=196&c=7&r=0&o=5&dpr=2&pid=1.7', 'Henry', 1, '科技迷', '科技,创新', 'henry@example.com', 'hashed_password_8', 'henry@example.com', '12345678908', 0, NULL, NULL, 1, NOW(), NOW(), 1, 1),
    ('https://tse1-mm.cn.bing.net/th/id/OIP-C.tDbFzIxORkvKYnfklqqA6QHaHa?w=196&h=196&c=7&r=0&o=5&dpr=2&pid=1.7', 'Ivy', 2, '热爱自然', '户外,环保', 'ivy@example.com', 'hashed_password_9', 'ivy@example.com', '12345678909', 0, NULL, NULL, 1, NOW(), NOW(), 1, 1),
    ('https://tse1-mm.cn.bing.net/th/id/OIP-C.tDbFzIxORkvKYnfklqqA6QHaHa?w=196&h=196&c=7&r=0&o=5&dpr=2&pid=1.7', 'Jack', 1, '运动爱好者', '跑步,足球', 'jack@example.com', 'hashed_password_10', 'jack@example.com', '12345678910', 0, NULL, NULL, 1, NOW(), NOW(), 1, 1),
    ('https://tse1-mm.cn.bing.net/th/id/OIP-C.tDbFzIxORkvKYnfklqqA6QHaHa?w=196&h=196&c=7&r=0&o=5&dpr=2&pid=1.7', 'Kathy', 0, '书籍爱好者', '阅读,写作', 'kathy@example.com', 'hashed_password_11', 'kathy@example.com', '12345678911', 0, NULL, NULL, 1, NOW(), NOW(), 1, 1),
    ('https://tse1-mm.cn.bing.net/th/id/OIP-C.tDbFzIxORkvKYnfklqqA6QHaHa?w=196&h=196&c=7&r=0&o=5&dpr=2&pid=1.7', 'Leo', 1, '动漫迷', '动漫,游戏', 'leo@example.com', 'hashed_password_12', 'leo@example.com', '12345678912', 0, NULL, NULL, 1, NOW(), NOW(), 1, 1),
    ('https://tse1-mm.cn.bing.net/th/id/OIP-C.tDbFzIxORkvKYnfklqqA6QHaHa?w=196&h=196&c=7&r=0&o=5&dpr=2&pid=1.7', 'Mia', 2, '动物爱好者', '动物,保护', 'mia@example.com', 'hashed_password_13', 'mia@example.com', '12345678913', 0, NULL, NULL, 1, NOW(), NOW(), 1, 1),
    ('https://tse1-mm.cn.bing.net/th/id/OIP-C.tDbFzIxORkvKYnfklqqA6QHaHa?w=196&h=196&c=7&r=0&o=5&dpr=2&pid=1.7', 'Nina', 0, '时尚达人', '时尚,购物', 'nina@example.com', 'hashed_password_14', 'nina@example.com', '12345678914', 0, NULL, NULL, 1, NOW(), NOW(), 1, 1),
    ('https://tse1-mm.cn.bing.net/th/id/OIP-C.tDbFzIxORkvKYnfklqqA6QHaHa?w=196&h=196&c=7&r=0&o=5&dpr=2&pid=1.7', 'Oscar', 1, '摄影爱好者', '摄影,旅行', 'oscar@example.com', 'hashed_password_15', 'oscar@example.com', '12345678915', 0, NULL, NULL, 1, NOW(), NOW(), 1, 1),
    ('https://tse1-mm.cn.bing.net/th/id/OIP-C.tDbFzIxORkvKYnfklqqA6QHaHa?w=196&h=196&c=7&r=0&o=5&dpr=2&pid=1.7', 'Penny', 0, '创意设计', '设计,艺术', 'penny@example.com', 'hashed_password_16', 'penny@example.com', '12345678916', 0, NULL, NULL, 1, NOW(), NOW(), 1, 1);
