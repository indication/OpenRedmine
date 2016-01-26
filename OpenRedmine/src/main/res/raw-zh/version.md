OpenRedmine
===========
发布历史

待办事项
===========
- Check date to update bug (#83)
-任务内添加分享按钮(#18)

下一发布版本
===========
-StevenGape 添加中文翻译(#171)

v3.17 - 50 - 2015/12/31
===========
- etcho 添加 Portuguese-Brazil 翻译 (#169)
- 修复项目列表查询 (#168)
- 增加维基列表查询 (#168)
- 在目录增加 webview (#167)

v3.16 - 49 - 2015/11/18
===========
- 支持日期格式 "yyyy-MM-dd HH:mm:ss Z" (#157)
- 支持在任务视图推送刷新时间事件 (#157)
- 修复任务编辑时的NumberFormatException (#161) / NullPointerException (#162)
- 减少权限 (#160)
- 合并 appcompat-v7，移除 actionbarsherlock，actionbarpulltorefresh。 (#144)

v3.15 - 48 - 2015/02/07
===========
- markusr 增加德语翻译
- 支持日志关联 (#151)

v3.14 - 47 - 2014/10/29
===========
- 获取观察者(#132)
- Fix misc (#142)
 - Support new API on WebView fragment
 - 关闭连接
 - 删除无用代码
 - 支持 android studio 0.9.1


v3.13 - 45 - 2014/10/04
===========
- Put jump area to issue  on the project list (#130)
- Supported attachment provider (#131, #135)
- Add recently viewed issue list (#137)
- 支持删除连接 (#138)


v3.12 - 44 - 2014/09/06
===========
- Support parse Project Status on Redmine 2.5.0 (#71)
- 显示 N 天以前 ...等等 (#28)

v3.11 - 43 - 2014/08/16
===========
- 显示项目新闻 (#17,#118)
- 增加项目页(#116)
-  Refactor wiki (#114)
 - Support for issue id with brackets
 - Use factory method about XmlPullParser
 -  Add parent page
 - Refactor wiki
- 修复构建问题 (#115,#119)

v3.10 - 42 - 2014/06/28
===========
- Add kanban view by long tap project (#108)
- Fix crashed when tap the recorded time (#103)
- Category list is not applied theme (#102)
- Fix not fetch issue detail from remote by pulling ... and more minor bug fix (#112)
- Allow input certification fingerprint to connection (#112)

v3.9 - 41 - 2014/05/17
===========
- 修复任务视图图标问题
- Fix wiki link expressions
- Improved performance by changing issue detail from WebView to TextView

v3.8 - 40 - 2014/03/27
===========
- 在任务和项目增加查询接口
- (Internal changes) Update Android Studio from 0.4.2 to 0.5.1
- 修复在 android 2.2 崩溃的Bug (#79,#56)
- 修复显示日志变更的 Bug (#81)
- Reduce URL validation on add connection (#84)

v3.7 - 39 - 2014/02/28
===========
- Fix crash on fetch remote first time (#68)
- (Internal changes) Move DAO into adapter (#61)
- Add URL validation (start with schema) to avoid to crash (#67)

v3.6 - 38 - 2014/02/15
===========
- Add russian translation by box789
- Add project favorites list
- Fix appearance on edit connection

v3.5 - 37 - 2014/02/02
===========
- Fetch wiki when there is no item
- Open activity on select issue
- 修复添加新任务问题

v3.5 - 36 - 2014/01/24
===========
- 支持维基视图
- Add tabs

v3.4 - 35 - 2013/12/10
===========
- 修复 Android 2.3 版本崩溃问题

v3.4 - 34 - 2013/12/09
===========
- 点击开关列表
- 支持推送刷新
- (Internal changes) Port to Android Studio

v3.3 - 33 - 2013/11/06
===========
- Download file related with issues
- Fixes crash on showing unknown relation type

v3.2 - 32 - 2013/09/09
===========
- Fix crashes on fetching issue from remote - relative issue reference was wrong
- Update submodule - android-form-edittext

v3.1 - 31 - 2013/09/05
===========
- Add sticky view on issue
- Renewal issue list view

v3.0 - 30 - 2013/08/12
===========
- Support fragment (internal codes)
- Fix timezone when fetch items
- 修复任务视图居中问题
- 在项目列表显示当前用户

v2.5 - 29 - 2013/07/03
===========
- 总是显示说明区域

v2.4 - 28 - 2013/06/06
===========
- 修复同步任务问题(永远加载)
- Fix posting in android 2.2 (v1.XmlPullParser support)

v2.3 - 27 - 2013/05/29
===========
- Add post notes to issue
- Fix edit issue about version/estimated time
- 增加排序键
 - start/due/close date
 - 优先级/状态/跟踪者
 - fixed_version/category
 - 指派给/作者
 - done rate

v2.2 - 26 - 2013/05/25
===========
- 增加发送或编辑任务
- 修复任务列表问题

v2.1 - 23 - 2013/05/20
===========
- 修复通过http同步项目问题

v2.0 - 22 - 2013/05/14
===========
- 增加发送或编辑时间条目
- 获取所有项目

v1.14 - 21 - 2013/05/01
===========
- 增加排序功能
- 在日志显示变更
- 在任务详情显示连接(URLs)

v1.13 - 20 - 2013/04/19
===========
- 修复获取任务问题
- 从项目列表跳转到任务
- 在链接页面增加 url 输入帮助

v1.12 - 19 - 2013/04/17
===========
- 修复更新任务问题
- 从描述或者日志跳转到任务
- 增加跟踪者在任务列表
- 刷新图标

v1.11 - 18 - 2013/04/08
===========
- 增加时间条目

v1.10 - 17 - 2013/03/31
===========
- 修复更新任务问题

v1.9 - 15 - 2013/03/25
===========
- 增加关于 优先级/作者/指派给 的过滤
-  Fix update issue attributes

v1.8 - 14 - 2013/03/17
===========
- 增加 设置
- Fetch all issues(closed issues) by setting. By default fetches only unclosed issues.
- 增加 主题切换

v1.7 - 13 - 2013/03/14
===========
- Supports textile in issue detail

v1.6 - 12 - 2013/02/27
===========
- 任务列表保留滚动点
- 修复过滤无内容问题
- 升级 android api 等级

v1.5 - 10 - 2013/02/23
===========
- 支持日志
- Changed fetching issues from remote

v1.4 - 9 - 2013/01/14
===========
- 减少写入sd卡权限

v1.3 - 8 - 2012/12/01
===========
- Fix transfer authentications on getting information via web site
- 增加过滤功能

v1.2 - 7 - 2012/12/01
===========
- 修复崩溃。(构建失败)

v1.1 - 6 - 2012/12/01
===========
- 重写 HTTP 传输
- 通过 gzip 连接
- 读取项目获取版本
- Add footer on ConnectionList
- Fix parse error on timezones
- Reconfigure splash activity

v1.0 - 1 - 2012/10/31
===========
- 修复崩溃
- 修复保存连接时的按钮
- 创建新闻。