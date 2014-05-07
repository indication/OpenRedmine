OpenRedmine
===========

OpenRedmine is an android Redmine client.
This project is open source.

Features
==========
* Allow to connect UNSAFE SSL sites powered by transdroid
* View issues offline
* Get API key from web site
* Filter (Status/Tracker/Category/Priority)
* Download file
* Wiki


Version
==========
Current commit is alpha version.
Some feature is not complete or incorrect.


License
==========
This program is free software; you can redistribute it and/or
modify it under the terms of the GNU General Public License
as published by the Free Software Foundation; either version 2
of the License, or (at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program; if not, write to the Free Software
Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.

Contributors
==========
*  [insink71](https://twitter.com/insink71/statuses/425297982078996480) - Promotion in [redmine wiki](http://www.redmine.org/projects/redmine/wiki/ThirdPartyTools)
*  [box789](https://github.com/box789) - _Add russian translation

Used references
==========
- DefaultHttpClient and Deflate settings and guide to better code inherit from "DenkiYagi"
  http://terurou.hateblo.jp/entry/20110702/1309541200

- Expanded list ideas from "My life with Android :-)"
  http://mylifewithandroid.blogspot.jp/2010/12/expandable-list-and-checkboxes.html

- ListView crashes while adding data background task ideas from "A Notepad blog"
  http://www.mumei-himazin.info/blog/?p=96

- Textile useage from "Diary of tanamon"
  http://d.hatena.ne.jp/tanamon/20090723/1248322655

- Table layout(timeentryitem) usecase from "Setagaya Enginner Blog"
  http://suka4.blogspot.jp/2011/03/android-tablelayout.html

- URI handling on TextileHelper from "Perl note"
  http://www.din.or.jp/~ohzaki/perl.htm#URI

- Detect MIME type on opening file from "moplog"
  http://ac-mopp.blogspot.jp/2011/12/android-mime-type.html

- Open file via intent from "PG Senki hikaruright"
  http://d.hatena.ne.jp/hikaruright/20120119/1326965685

- Support gradle release from "ogaclejapan"
  http://ogaclejapan.com/android/2013/07/07/new-build-system-part2-for-gradle/

- Add feature about FragmentBreadCrumbs from "techbooster"
  http://techbooster.org/android/application/15499/

- Support Link on TextView from "StackOverFlow"
  http://stackoverflow.com/questions/12418279/android-textview-with-clickable-links-how-to-capture-clicks

Using Libraries
==========
- android-form-edittext (MIT) - needs verification about inputs
 https://github.com/vekexasia/android-form-edittext

- Transdroid (GPL) - needs to access untrusted ca.
 http://transdroid.googlecode.com/hg/lib/src/org/transdroid/daemon/util/FakeSocketFactory.java	r1de55ccfce7b
 http://transdroid.googlecode.com/hg/lib/src/org/transdroid/daemon/util/FakeTrustManager.java	rfa98b5bb5624

- MyWebViewClient (unknown) - needs to access redmine by webview
 https://github.com/potaka001/WebViewBasicAuthTest/blob/master/src/com/webviewbasicauthtest/MyWebViewClient.java

- LRUCache (unknown) from "Play History of development" - cache access by listview
  http://ttimez.blogspot.jp/2011/07/java.html

- textile-j (LGPL) - show the descriptions in rich text
  http://java.net/projects/textile-j/sources/svn/show/trunk/www/builds/net.java.textilej/latest

- httpmime (Apache License, Version 2.0) - This module provides support for MIME multipart encoded entities
  http://hc.apache.org/httpcomponents-client-ga/httpmime/

- StickyListHeaders (Apache License, Version 2.0) - section headers that stick to the top
  https://github.com/emilsjolander/StickyListHeaders

- ActionBarSherlock (Apache License, Version 2.0) - support actionbar
  https://github.com/JakeWharton/ActionBarSherlock

- actionbarpulltorefresh (Apache License, Version 2.0) - support pull to refresh
  https://github.com/chrisbanes/ActionBar-PullToRefresh

- PtrStickyListHeadersListView (unknown)  - support pull to refresh on StickyListHeaders
  http://stackoverflow.com/questions/20143008/is-it-possible-to-merge-stickylistviewheader-with-crisbanes-pulltorefresh

Using Icons
==========
- redmine (by-sa/2.5)
 http://www.redmine.org/projects/redmine/wiki/Logo
 Redmine Logo is Copyright (C) 2009 Martin Herr and is licensed under the Creative Commons Attribution-Share Alike 2.5 Generic license.
 See http://creativecommons.org/licenses/by-sa/2.5/ for more details.

- Action Bar Icon Pack (by/2.5)
 http://developer.android.com/design/downloads/
 See http://creativecommons.org/licenses/by/2.5/ for more details.
