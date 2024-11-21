use blog;

INSERT INTO comment (content, passageId, authorId, commentUserId) VALUES
('这篇文章写得很有意思，学习了很多新的观点。', 1849451260659367936, 1, 3),
('作者的观点很有深度，我有一些不同的看法。', 1849451260659367937, 2, 7),
('文章结构清晰，表达很流畅，推荐阅读！', 1849451260659367938, 3, 5),
('这篇文章给了我很多启发，感谢作者的分享！', 1849451260659367939, 4, 9),
('文章很不错，但某些地方还可以再拓展一下。', 1849451260659367940, 5, 2),
('文章观点很新颖，但论证不够充分。', 1849451260659367936, 1, 6),
('作者的语言简洁明了，读起来很舒服。', 1849451260659367937, 2, 1),
('对这篇文章的部分论点有些质疑，希望能进一步探讨。', 1849451260659367938, 3, 10),
('这篇文章内容有趣，但可以提供更多实证数据。', 1849451260659367939, 4, 4),
('非常喜欢这篇文章，给我带来了不少启示。', 1849451260659367940, 5, 11),
('文章写得很好，能看到作者的深思熟虑。', 1849451260659367936, 1, 12),
('我完全同意文章中的结论，非常认同作者的观点。', 1849451260659367937, 2, 13),
('我觉得文章的某些观点可以进一步完善。', 1849451260659367938, 3, 8),
('这篇文章对于我理解这个问题很有帮助。', 1849451260659367939, 4, 14),
 ('文章内容非常实用，我会继续关注作者的其他作品。', 1849451260659367940, 5, 15),
 ('希望能看到更多关于这个话题的深入分析。', 1849451260659367936, 1, 16),
 ('文章的结论让我很震惊，非常有意义。', 1849451260659367937, 2, 17),
 ('内容精彩，但有一些地方论证不够严谨。', 1849451260659367938, 3, 18),
 ('我在这篇文章中找到了很多与自己经历相似的地方。', 1849451260659367939, 4, 19),
 ('这篇文章的见解让我对这个话题有了更深的理解。', 1849451260659367940, 5, 20),
 ('文章论点很有创意，但某些部分需要更多的例证。', 1849451260659367936, 1, 21),
 ('对这篇文章有些不同意见，但也能理解作者的观点。', 1849451260659367937, 2, 3),
 ('文章的结构安排很合理，观点也很清晰。', 1849451260659367938, 3, 6),
 ('觉得这篇文章有点过于理论化，缺少实际案例支持。', 1849451260659367939, 4, 7),
 ('作者的分析很有深度，文章很有启发性。', 1849451260659367940, 5, 5),
 ('这篇文章非常适合深度阅读，值得反复琢磨。', 1849451260659367936, 1, 9),
 ('看到这篇文章，我对这个话题有了全新的认识。', 1849451260659367937, 2, 4),
 ('这篇文章写得很好，期待作者能够继续更新。', 1849451260659367938, 3, 11),
 ('文章的内容很有启发性，帮助我解决了实际问题。', 1849451260659367939, 4, 13),
 ('我很喜欢文章的讨论方式，轻松易懂。', 1849451260659367940, 5, 14),
 ('文章的某些观点让我有些不太认同，但很有价值。', 1849451260659367936, 1, 17),
 ('这篇文章的角度很独特，值得大家阅读。', 1849451260659367937, 2, 8),
 ('文章简洁明了，适合快速了解相关话题。', 1849451260659367938, 3, 12),
 ('很喜欢这篇文章，虽然部分观点我有不同看法。', 1849451260659367939, 4, 10),
 ('作者写得很有逻辑，内容很有条理。', 1849451260659367940, 5, 16);


INSERT INTO user (userName, sex, profiles, interestTag, userAccount, password, mail, phone, role, level, status, isDelete) VALUES
 ('张三', 1, '热爱编程，喜欢分享', '["编程", "科技"]', 'zhangsan', 'password123', 'zhangsan@example.com', '13800138001', 0, 0, 1, 1),
 ('李四', 0, '喜欢绘画，热爱生活', '["绘画", "旅游"]', 'lisi', 'password123', 'lisi@example.com', '13800138002', 0, 0, 1, 1),
 ('王五', 1, '热爱运动，喜欢健身', '["健身", "篮球"]', 'wangwu', 'password123', 'wangwu@example.com', '13800138003', 0, 0, 1, 1),
 ('赵六', 0, '学生，喜欢阅读', '["文学", "历史"]', 'zhaoliu', 'password123', 'zhaoliu@example.com', '13800138004', 0, 0, 1, 1),
 ('周七', 1, '全栈工程师', '["编程", "区块链"]', 'zhouqi', 'password123', 'zhouqi@example.com', '13800138005', 0, 0, 1, 1),
 ('吴八', 0, '摄影爱好者', '["摄影", "旅行"]', 'wuba', 'password123', 'wuba@example.com', '13800138006', 0, 0, 1, 1),
 ('郑九', 1, '产品经理，热爱创新', '["产品", "设计"]', 'zhengjiu', 'password123', 'zhengjiu@example.com', '13800138007', 0, 0, 1, 1),
 ('钱十', 0, '喜欢下厨，热爱美食', '["烹饪", "美食"]', 'qianshi', 'password123', 'qianshi@example.com', '13800138008', 0, 0, 1, 1),
 ('孙十一', 1, '游戏开发者', '["游戏", "编程"]', 'sunshiyi', 'password123', 'sunshiyi@example.com', '13800138009', 0, 0, 1, 1),
 ('李十二', 0, '热爱音乐，喜欢吉他', '["音乐", "吉他"]', 'lishier', 'password123', 'lishier@example.com', '13800138010', 0, 0, 1, 1),
 ('王十三', 1, '热爱学习，技术控', '["学习", "技术"]', 'wangshisan', 'password123', 'wangshisan@example.com', '13800138011', 0, 0, 1, 1),
 ('赵十四', 0, '热爱手工，喜欢DIY', '["手工", "DIY"]', 'zhaoshisi', 'password123', 'zhaoshisi@example.com', '13800138012', 0, 0, 1, 1),
 ('周十五', 1, '科技爱好者，喜欢写作', '["科技", "写作"]', 'zhoushiwu', 'password123', 'zhoushiwu@example.com', '13800138013', 0, 0, 1, 1),
 ('郑十六', 0, '热爱健身，追求健康生活', '["健身", "健康"]', 'zhengshiliu', 'password123', 'zhengshiliu@example.com', '13800138014', 0, 0, 1, 1),
 ('钱十七', 1, '创业者，热爱挑战', '["创业", "挑战"]', 'qianshiyi', 'password123', 'qianshiyi@example.com', '13800138015', 0, 0, 1, 1);

INSERT INTO passage (passageId, authorId, authorName, title, content, thumbnail, summary, categoryId, pTags, viewNum, commentNum, thumbNum, collectNum, status, isDelete)
VALUES
    (1849451260659367936, 1, '张三', '关于学习的思考', '学习是一种长期的投资...', NULL, '学习的意义', 1, '["学习", "思考"]', 100, 5, 10, 20, 2, 1),
    (1849451260659367937, 2, '李四', '编程的乐趣', '编程是一种解决问题的艺术...', NULL, '编程与生活', 1, '["编程", "乐趣"]', 150, 10, 15, 25, 2, 1),
    (1849451260659367938, 3, '王五', '健身的必要性', '健身不仅能改善身体健康...', NULL, '健康与生活', 1, '["健身", "健康"]', 200, 15, 20, 30, 2, 1),
    (1849451260659367939, 4, '赵六', '旅行的意义', '旅行是探索世界的一种方式...', NULL, '旅行分享', 1, '["旅行", "分享"]', 300, 8, 25, 35, 2, 1),
    (1849451260659367940, 5, '钱七', '阅读的重要性', '阅读能开阔我们的视野...', NULL, '阅读与学习', 1, '["阅读", "重要性"]', 400, 12, 30, 40, 2, 1),
    (1849451260659367941, 6, '孙八', '如何提高效率', '效率是实现目标的关键...', NULL, '效率管理', 1, '["效率", "管理"]', 250, 5, 18, 28, 2, 1),
    (1849451260659367942, 7, '周九', '投资理财的技巧', '投资理财是一种智慧的选择...', NULL, '理财知识', 1, '["投资", "理财"]', 180, 9, 22, 32, 2, 1),
    (1849451260659367943, 8, '吴十', '摄影的艺术', '摄影是一种捕捉瞬间的艺术...', NULL, '摄影技巧', 1, '["摄影", "艺术"]', 160, 3, 14, 24, 2, 1),
    (1849451260659367944, 9, '郑十一', '音乐的治愈力', '音乐能够治愈人心...', NULL, '音乐分享', 1, '["音乐", "治愈"]', 220, 7, 27, 37, 2, 1),
    (1849451260659367945, 10, '冯十二', '电影的魅力', '电影是一种视觉与听觉的盛宴...', NULL, '电影评论', 1, '["电影", "评论"]', 210, 4, 29, 39, 2, 1),
    (1849451260659367946, 1, '张三', '如何写好文章', '写作是一种表达思想的方式...', NULL, '写作技巧', 1, '["写作", "技巧"]', 350, 6, 11, 21, 2, 1),
    (1849451260663562240, 2, '李四', '数据分析基础', '数据分析能够帮助我们做出更好的决策...', NULL, '数据分析', 1, '["数据", "分析"]', 400, 2, 16, 26, 2, 1),
    (1849451260663562241, 3, '王五', '心理学的奥秘', '心理学能够解读人类行为...', NULL, '心理学', 1, '["心理", "行为"]', 290, 11, 23, 33, 2, 1),
    (1849451260663562242, 4, '赵六', '自我提升的路径', '自我提升是一个长期的过程...', NULL, '自我提升', 1, '["自我提升", "成长"]', 280, 9, 19, 29, 2, 1),
    (1849451260663562243, 5, '钱七', '美食的探索', '美食是文化的一部分...', NULL, '美食分享', 1, '["美食", "文化"]', 330, 8, 15, 25, 2, 1),
    (1849451260663562244, 6, '孙八', '网络安全的重要性', '网络安全是每个人都需要关注的...', NULL, '网络安全', 1, '["安全", "网络"]', 300, 10, 20, 30, 2, 1),
    (1849451260663562245, 7, '周九', '职场生存技巧', '职场如战场，生存是第一要务...', NULL, '职场', 1, '["职场", "生存"]', 370, 5, 12, 22, 2, 1),
    (1849451260663562246, 8, '吴十', '环境保护的重要性', '保护环境是每个人的责任...', NULL, '环境保护', 1, '["环保", "责任"]', 240, 4, 17, 27, 2, 1),
    (1849451260663562247, 9, '郑十一', '如何进行有效的沟通', '沟通是理解的桥梁...', NULL, '沟通技巧', 1, '["沟通", "技巧"]', 310, 3, 13, 23, 2, 1),
    (1849451260663562248, 10, '冯十二', '领导力的培养', '领导力是成功的重要因素...', NULL, '领导力', 1, '["领导力", "成功"]', 340, 6, 21, 31, 2, 1),
    (1849451260663562249, 1, '张三', '亲子教育的重要性', '亲子教育是家庭教育的基础...', NULL, '教育', 1, '["亲子教育", "家庭"]', 260, 5, 14, 24, 2, 1),
    (1849451260663562250, 2, '李四', '职业规划的必要性', '职业规划是实现人生目标的重要一步...', NULL, '职业规划', 1, '["职业规划", "目标"]', 230, 3, 15, 25, 2, 1),
    (1849451260663562251, 3, '王五', '人工智能的未来', '人工智能正在改变我们的生活...', NULL, '科技', 1, '["人工智能", "科技"]', 190, 4, 18, 28, 2, 1),
    (1849451260663562252, 4, '赵六', '数字营销的趋势', '数字营销是现代商业的重要组成部分...', NULL, '营销', 1, '["数字营销", "商业"]', 220, 8, 26, 36, 2, 1),
    (1849451260663562253, 5, '钱七', '传统文化的传承', '传统文化是民族的根...', NULL, '文化', 1, '["传统文化", "传承"]', 310, 9, 29, 39, 2, 1),
    (1849451260663562254, 6, '孙八', '科技创新的重要性', '科技创新是推动社会进步的动力...', NULL, '科技创新', 1, '["科技创新", "进步"]', 350, 11, 22, 32, 2, 1),
    (1849451260663562255, 7, '周九', '社交网络的影响', '社交网络改变了我们的交往方式...', NULL, '社交', 1, '["社交网络", "影响"]', 400, 15, 20, 30, 2, 1),
    (1849451260663562256, 8, '吴十', '时间管理技巧', '有效的时间管理可以提升效率...', NULL, '时间管理', 1, '["时间管理", "效率"]', 380, 12, 19, 29, 2, 1);
