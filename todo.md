* jsoup
* 图片压缩webp格式、缩略图
* 删除文章的同时删除oss的图片
* 部署后文章编辑页加载bug
* 插入图片时先保存在本地，不上传oss，第一次保存时会生成文章id，此时把图片上传oss，生成的图片名以文章id开头，当删除文章时就可以根据文章id方便的删除OSS的文章图片
* 文章详情页页面布局过大
* 管理页不显示邮箱（已加密）
* 文章详情页增加编辑器的目录功能
* 创作页的编辑器和编辑页的编辑器复用
* 搜索优化+站外https://cn.bing.com/search?q=
* 首页个人介绍放关于页，首页 热门文章viewNum + 站外热点资讯
* 搜索推荐
# TODO

* 首页查询所有文章时判断当前用户是否点赞 √/
* 文章详情判断是否点赞 √/
* 收藏、点赞文章有关联表 √/

我的收藏、我的点赞 √/  [ 从redis的zset查询我的收藏关注文章，并修改用户-收藏、用户-点赞表；或者从用户-收藏、用户-点赞表查询]
* 查询文章收藏点赞前10篇，降序 √/
* 我的文章总收藏量√/
* 我的文章数量√/
* 用户关注、取消关注 、我关注的、我的粉丝列表、其他用户info √/
* 数据库连接池 √/
* 权限校验√/
    * 拦截器中未开放的接口，需要登录才能访问 √
    * 对于需要管理才能访问的接口，首先肯定不予开放，其次要添加@AuthCheck注解，检查登录用户的权限 √

* passage表的passageId和authorId为联合索引 √
* url路径设置,/passage/passageDetails/${authorId}/${passageId},这样修改了passageId或者authorId任意一个参数都搜不到文章√
* url传参改为组件传参×
* 个人主页的数据从各个组件中获取，把个人信息的数据放入dva，从dva中获取×，AES加密存到localStorage √

* authorInfo的作者信息和其他文章组件分开，实现跳转到其他文章，只刷新文章详情 √

* 后端接口返回数据精细化 √
* 首页跳转详情页，url太长
* 实现文章点赞收藏功能√
* 实现用户关注功能√
* 实现个人主页 的收藏点赞、文章跳转功能 √
* 文章详情页作者其他文章跳转URL参数缺少 √

* 评论功能
    * 评论内容限制在200字以内√
    * commentID和passageId联合索引
    * userName,avatar、ip地址根据根据评论者id从用户表查询，authorId从前端传 √
    * 评论根据时间降序排序 √
    *
    评论加上删除字段，作者可以删除所有评论，评论者可删除自己的评论（commentVO加上canDelete字段默认false，如果commentUserId=登录用户id，为true；如果authorId=登录用户id，那么commentVO全为true，即文章作者对评论有所有权）√
    * 前端的评论区可以通过 commentUserId?:number进入用户主页
    * 标签属于类别，一个类别有多个标签，一篇文章多个类别，但是最多5个标签，标签可不选 √
    * 标签表、类别表、文章表（存储标签id）
    * passage表建立了联合索引
    * 管理页增加类别、标签的管理（增删改）√

# todo


* 评论滑动加载 √
* oss防盗链√
* 博文
    * 保存博客只需要有标题和内容，发布博客还需要有标签、摘要，封面可以没有
    * 前端编辑器save操作只对编辑器内容有效，对operation操作栏无效
    * 上传文章封面，封面链接保存在passage表
    * 修改文章，改哪个字段传哪个字段
    * 上传封面时必须有标题和内容，这样上传封面后自动保存文章，把文章封面保存
    * 创作页面每隔5min自动保存
    * 点击定时发布弹出modal
    * 博客详情页的目录功能
* 用户可以修改用户信息，头像、标签、用户名编辑 修改功能,用弹框modal实现 √
* 短信异步 √
* 后端搜索接口 前端autoComplete组件 √
* 增量失败 重试机制 guava retrying库 √
* 前端点赞收藏关注防抖lodash √
* 接入hotkey，实现缓存热文和黑名单IP拦截√
* 事务 √
* 接入站点统计baidu google 管理页数据管理引入Echarts等
* IP地址解析 √
* RBAC
* ai √
* ES搜索优化 √
* 文章管理bug
* 定时发布redisson 延迟消息队列 √
* 登录时检查账户是否禁用 √
* 替换druid连接池 √
* 私信功能
* 用户登录信息不保存在前端，前端仅保存token

# bug

* 前端验证码逻辑 √
* 前端登陆后进入个人主页获取登录用户信息异常 √
* 注册邮箱加密 √
* 图片上传minio
* 点赞使用bitmap,passageId为key，userId作为偏移量。因为passageId是很长的long类型，如果作为偏移量存到bitmap会占用更多内存√
* 
* 登陆成功进入个人主页报错 √
* 个人主页修改头像后，兴趣标签会消失，需要重新设置，前后端都需要改
* 个人主页点赞取关数量，数字不变 
* 个人主页修改信息时，modal显示性别是数字,标签、性别、头像无法正确修改√
* 个人主页和文章的部分评论会重复展示，导致删除失败√
* 前端管理页url后退问题，无法解决 
* 搜索页url后退
* 详情页的评论滚动加载，重复显示√
* commentModel 一篇文章会显示另一篇文章的评论 √
* 个人编辑文章，发布保存按钮√

* 首页文章推荐，展示文章浏览量top 10的文章√
* 管理页类别和标签的增删改查。类别描述限制40字，类别名和标签名5字 √


* 前端缓存查询结果

* 个人主页-我的全部改成分页请求，而不是全量获取 √
* 我的消息显示评论√

* 展示性别 √
* 管理员后端改成多条件联合查询√
* 修改管理获取用户接口，返回的字段和前端对应，不返回多余数据 √
* 上传的图片由userId组成 √

* 分类功能 √
* 文章二维码分享√

* 一人被上万个用户关注，一个用户关注数量相对来说没有那么多
* 我的点赞、我的收藏页，文章解构的数据及传输 √
* 首页在进入第二页的文章详情，返回后回到第一页而不是第二页√实现分页优化后就解决了
* 关注、点赞、收藏事务问题，redis和mysql
* 前端url路径传参会不会遭到恶意修改参数,传参加密
* 发布文章时要拿到登录用户的头像url设置到passage表√
*
博客首页加载文章列表，请求后端时不查询出文章content，节省加载时间和数据传输量，当用户进入文章详情时才加载content，并且在文章列表已经加载好的viewNum等字段直接传给文章详情组件，不再从后端请求√
* 文章详情加载时给文章表的浏览量+1 √
* 前端请求文章分页√
* 用户登陆后刷新首页（前端登录态正常）√
* 用户登录后返回的loginUserVO中包含token，那么就把JSON.stringify(loginUserVO)
  AES加密保存在sessionStorage，每次请求时从loginUserVO中取出token √
* dva,useModel,initialState √


* 管理模块完善

* 用户个人简介限制在30字内！！！
* 数据库字段大小根据实际进行修改
* 写后端接口完善：用户的粉丝数量√
* 用户名正则：仅支持中文、英文、数字、下划线，长度2-6 √
* 密码正则：必须包含字母和数字，不能使用特殊字符，长度6-10 √

** 文章头像显示问题，显示自己的而不是默认写死的 √
** passage表加上作者的头像url，显示在首页list √
** 修改注册功能，使用邮箱验证码stmp √

* 我收藏的文章数量和我的文章收藏数量√
* 获取登录用户的信息getLoginUser √

* 首页博主介绍、博客介绍 √
* 跳转到文章详情页的同时获取用户id的其他文章√

* 公告
* passage表+评论数量，首页展示√
* 首页展示文章列表时也展示作者头像，不是文章缩略图 √
* http消息转换器
* CommandLineRunner
* 网关检测
* 共同关注
* out_link表、events表、ip_request_info表
* 违规禁词检检测
* ES+Doris  https://www.selectdb.com/blog/1037
* 用户签到 bitmap+nextSetBit 前端设置缓存避免多次签到请求 localStorge

登录态保存到本地线程而不是session
根据uid生成token返回给前端，前端每次请求头带上token。
token保存到redis，拦截器校验token，通过则从redis取出token，若取不出说明token已经过时要重新登录

* token有效期和redis有效期不同
* 用户登陆后长时间不操作，authorization的token失效，那么redis到期也自动删除，拦截器仅检验token是否失效不用查redis
* 登陆后一直操作，redis要刷新，但是token不刷新

* 未登录 -> 登录 ->vo 存到redis，并保存到本地线程。下次访问，刷新redis的token有效期
* value=token
* 管理可在每个类别下面创建标签，类别和标签列表展示在前端创作台
* 一篇文章必须有category，可以没有pTags，pTags最多5个
* passage表 categoryID pTags(json数组)
* category表 cTags(json数组) 不需要category-tag
* 用户创作时，为文章选定cTags，cTags内容直接作为pTags
* mysql向es迁移文章时只迁移审核通过且没有删除的文章，增量同步、全量同步！！！√
* 用户可以根据文章ptags搜索文章 √

![img.png](img.png)

# 登录拦截及权限校验

* 前端路由不展示的界面后端还有必要做权限校验吗?
* 客户端可以被绕过：前端路由的限制可以很容易被绕过。用户可以通过**直接访问URL** 或使用工具（如
  Postman）绕过前端界面，从而尝试直接请求受保护的资源或接口。如果后端没有做权限校验，恶意用户可以未授权地访问这些资源。
* 保护后端资源：即使前端不展示某些界面，后端资源（例如 API 接口、数据库记录等）仍然需要保护，*
  *以防止未授权访问或数据泄露**。

* 登录拦截器设置拦截路径，拦截需要登陆的路径，2种情况
    * 1：不需要登陆，放行
    * 2：需要登陆，登录后放行
      例：未登录用户放行后，要收藏某篇文章，此时路径就是需要登陆才可以访问的
* AOP切面+注解检查身份（使用在类和方法上，核验身份）
    * 如果是放行的未登录用户，则无权进行某些操作，需要登陆
    * 如果是放行的已登录用户，检查身份来决定能否进行某些操作

类名和标签名都有字数限制 10

管理员审核开关，可以开启文章审核或者关闭审核
苍穹外卖P31 自动字段填充用户名，减少冗余代码
苍穹外卖P93 spring cache Spring EL语法->  #开头：#user.id
![img_1.png](img_1.png)

# 需求分析

* 登陆注册 p0
* 用户模块
    * 增删改查：p0 管理员可以禁用用户、删除用户、添加用户

* 文章模块
    * 查看文章 p0
    * 发布文章 p0
    * 删除文章：p0 普通用户只能删除自己的文章，管理员可以删除任意文章。通过控制层接口区分，管理员可以拿到所有文章，而用户只能拿到自己的
    * 修改文章
    * 审核文章：仅管理
    * 评论文章
    * 点赞文章
    * 收藏文章


* 标签模块：分为文章标签和用户的兴趣标签
    * 添加标签：用户也可以添加
    * 删除标签：仅管理员

* 类别模块：文章必须要有类别，可以没标签
    * 类别只能由管理员创建
    * 类别和文章是一对多，类别和标签是多对多，标签属于某个类别
    * 一个文章只能有一个类别，但可以有多个标签
    * 一个类别可以有多个文章
    * 一个类别可以有多个标签

* 管理页可视化统计，PV、UV

# 高级功能

* 文章草稿箱
* 签到等级功能 BitMap
* ES搜索 增量同步
* 查看留言私信功能
* 关注推送
* Feed流大数据推送
* 文章定时发布
* AI生成文章摘要summary
* 公告功能

# 系统设计

## 表设计[最初，不是最新的]

# 功能设计

不管是后端返回给前端的数据还是全局异常处理器返回给前端的异常统一都是BaseResponse类型，这是返回基类
异常处理 ErrorCode 自定义异常BusinessException ->全局异常处理器GlobalExceptionHandler(
全局异常处理器要返回给前端，返回BaseResponse类型)
返回前端 ResultUtils包 最终给前端返回的也是BaseResponse类型