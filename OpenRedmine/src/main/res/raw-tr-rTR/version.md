OpenRedmine
===========
Sürüm geçmişi

v$current_version$ - $current_build$
-----------
- Request permission on Download (#228)
- Support for showing images on download form (#229)
- Update ORMLite (#230)
- Update translations
    - German by markusr
    - Portuguese(BR) by etcho


v3.23 - 665 - 2019/10/08
-----------
- Upgrade target SDK version to 28
- Try to fix get token is failed (#218)
- Try to fix crash on Android 8 (#219)
- Add czech translation by Mongata (#220)
- Add dutch translation by PanderMusubi (#224)

v3.22 - 652 - 2018/09/28
-----------
- Simgeleri, wikide ve işlerdeki "Başlığı göster" butonunu güncelle (#207)
- Markdown desteği
- Update translations
    - Portuguese(BR) by etcho
    - Russian by roman.yagodin

v3.21 - 56 - 2017/07/28
-----------
- Simgeleri güncelle
- Show title on issue or wiki
- Add issue filter to limit open or closed issues (#199)
- Update translations
    - German by Atalanttore
    - Portuguese(BR) by etcho
- Spelling fixes by ka7 (#198)
- Fix crash on FileDownload (#202)
- Try to fix infrator exception
- Fix several bugs

v3.20 - 53 - 2017/04/01
-----------
- Add journal id to header and performance up (#195)
- Add favorite project list to connection (#194)
- Add recent issue list to connection (#194)
- Update translations
    - German by Atalanttore
    - Portuguese(BR) by etcho
- Add french translations by MagicFab (#193)
- Add Turkish (Turkey) translations by halis.simsek (#193)
- Add Spanish (Spain) translations by Bernat13 (#193)
- Update minimum API version from 8 to 9 (Android 2.2 no more supported by support-library)
- Fix little mistake by elmanytas (#191)

v3.19 - 52 - 2016/06/30
-----------
- Changed connection method to android default (Remove org.apache.http.legacy)
- Fix crash on update issue/time entry
- Fix remove cookies when access via webclient (#180)
- Fix Google Play Warning: SSL Error Handler Vulnerability (#174)
- Update translations
    - German by Atalanttore

v3.18 - 51 - 2016/01/31
-----------
- Update translations
    - Portuguese(BR) by etcho
- Add Chinese translation by StevenGape (#171)

v3.17 - 50 - 2015/12/31
-----------
- Add Portuguese-Brazil translation by etcho (#169)
- Fix search on project list (#168)
- Add search on wiki list (#168)
- Add webview to menu (#167)

v3.16 - 49 - 2015/11/18
-----------
- Support date time style "yyyy-MM-dd HH:mm:ss Z" (#157)
- Support pull-to-refresh-event on IssueView (#157)
- Fix NumberFormatException (#161) / NullPointerException on IssueEdit (#162)
- Reduce permissions (#160)
- Migrate to appcompat-v7. Remove actionbarsherlock, actionbarpulltorefresh, Thank you for ever. (#144)

v3.15 - 48 - 2015/02/07
-----------
- Add German translation by markusr
- Support journal relations (#151)

v3.14 - 47 - 2014/10/29
-----------
- Fetch watchers (#132)
- Fix misc (#142)
 - Support new API on WebView fragment
 - Close connection
 - Delete unused code
 - Support android studio 0.9.1


v3.13 - 45 - 2014/10/04
-----------
- Put jump area to issue  on the project list (#130)
- Supported attachment provider (#131, #135)
- Add recently viewed issue list (#137)
- Supported deleting connection (#138)


v3.12 - 44 - 2014/09/06
-----------
- Support parse Project Status on Redmine 2.5.0 (#71)
- Show Ndays before ... etc (#28)

v3.11 - 43 - 2014/08/16
-----------
- Show project news (#17,#118)
-  Add projectpage (#116)
-  Refactor wiki (#114)
 - Support for issue id with brackets
 - Use factory method about XmlPullParser
 -  Add parent page
 - Refactor wiki
- Fix builds (#115,#119)

v3.10 - 42 - 2014/06/28
-----------
- Add kanban view by long tap project (#108)
- Fix crashed when tap the recorded time (#103)
- Category list is not applied theme (#102)
- Fix not fetch issue detail from remote by pulling ... and more minor bug fix (#112)
- Allow input certification fingerprint to connection (#112)

v3.9 - 41 - 2014/05/17
-----------
- Fix issue view icons
- Fix wiki link expressions
- Improved performance by changing issue detail from WebView to TextView

v3.8 - 40 - 2014/03/27
-----------
- Add search interface on issues, projects
- (Internal changes) Update Android Studio from 0.4.2 to 0.5.1
- Bug fix crash on android 2.2 (#79,#56)
- Bug fix show journal changes (#81)
- Reduce URL validation on add connection (#84)

v3.7 - 39 - 2014/02/28
-----------
- Fix crash on fetch remote first time (#68)
- (Internal changes) Move DAO into adapter (#61)
- Add URL validation (start with schema) to avoid to crash (#67)

v3.6 - 38 - 2014/02/15
-----------
- Add russian translation by box789
- Add project favorites list
- Fix appearance on edit connection

v3.5 - 37 - 2014/02/02
-----------
- Fetch wiki when there is no item
- Open activity on select issue
- Fix add new issue

v3.5 - 36 - 2014/01/24
-----------
- Support wiki view
- Add tabs

v3.4 - 35 - 2013/12/10
-----------
- Fix crash on Android 2.3

v3.4 - 34 - 2013/12/09
-----------
- Switch list via swipe
- Support pull to refresh
- (Internal changes) Port to Android Studio

v3.3 - 33 - 2013/11/06
-----------
- Download file related with issues
- Fixes crash on showing unknown relation type

v3.2 - 32 - 2013/09/09
-----------
- Fix crashes on fetching issue from remote - relative issue reference was wrong
- Update submodule - android-form-edittext

v3.1 - 31 - 2013/09/05
-----------
- Add sticky view on issue
- Renewal issue list view

v3.0 - 30 - 2013/08/12
-----------
- Support fragment (internal codes)
- Fix timezone when fetch items
- Fix issue view to align to center
- Show current user on project list

v2
===========

v2.5 - 29 - 2013/07/03
-----------
- Show comment area always

v2.4 - 28 - 2013/06/06
-----------
- Fix sync issues (loop forever)
- Fix posting in android 2.2 (v1.XmlPullParser support)

v2.3 - 27 - 2013/05/29
-----------
- Add post notes to issue
- Fix edit issue about version/estimated time
- Add sort key
 - start/due/close date
 - priority/status/tracker
 - fixed_version/category
 - assigned to/author
 - done rate

v2.2 - 26 - 2013/05/25
-----------
- Add post or modify issue
- Fix issue list

v2.1 - 23 - 2013/05/20
-----------
- Fix sync project via http

v2.0 - 22 - 2013/05/14
-----------
- Add post or modify time entry
- Fetch all of projects

v1
===========

v1.14 - 21 - 2013/05/01
-----------
- Add sort feature
- Show changes on journals
- Show link (URLs) on issue details

v1.13 - 20 - 2013/04/19
-----------
- Fix fetch issue
- Jump to issue from project list
- Add url input helper on connection

v1.12 - 19 - 2013/04/17
-----------
- Fix update issue
- Jump to issue from description or journal
- Add tracker to issues list
- Refresh icons

v1.11 - 18 - 2013/04/08
-----------
- Add time entries

v1.10 - 17 - 2013/03/31
-----------
- Fix update issue

v1.9 - 15 - 2013/03/25
-----------
- Add filter about Priority/Author/Assined
-  Fix update issue attributes

v1.8 - 14 - 2013/03/17
-----------
- Add settings
- Fetch all issues(closed issues) by setting. By default fetches only unclosed issues.
- Add theme switcher

v1.7 - 13 - 2013/03/14
-----------
- Supports textile in issue detail

v1.6 - 12 - 2013/02/27
-----------
- Issue list keep scroll point.
- Fix filter shows nothing on the list.
- Upgrade android api level.

v1.5 - 10 - 2013/02/23
-----------
- Support journals
- Changed fetching issues from remote

v1.4 - 9 - 2013/01/14
-----------
- Reduce permission to write sd card.

v1.3 - 8 - 2012/12/01
-----------
- Fix transfer authentications on getting information via web site
- Add feature filter

v1.2 - 7 - 2012/12/01
-----------
- Fixes crash on startup.(build was failed)

v1.1 - 6 - 2012/12/01
-----------
- Rewrite HTTP transport
- Connection via gzip
- Get versions on loading projects
- Add footer on ConnectionList
- Fix parse error on timezones
- Reconfigure splash activity

v1.0 - 1 - 2012/10/31
-----------
- Fixes crash on startup.
- Fixes buttons when save connection.
- Creates new.