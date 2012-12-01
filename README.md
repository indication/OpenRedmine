OpenRedmine
===========

OpenRedmine is an android Redmine client.
This project is open source.

Features
==========
* Allow to connect UNSAFE SSL sites powered by transdroid
* View issues offline
* Get API key from web site


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

Used references
==========
- DefaultHttpClient and Deflate settings inherit from "DenkiYagi"
  http://terurou.hateblo.jp/entry/20110702/1309541200

- Expanded list ideas from "My life with Android :-)"
  http://mylifewithandroid.blogspot.jp/2010/12/expandable-list-and-checkboxes.html

Using Libraries
==========
- android-form-edittext (MIT) - needs verification about inputs
 https://github.com/vekexasia/android-form-edittext
 To build this project, needs to checkout android-form-edittext parent dir.

- Transdroid (GPL) - needs to access untrusted ca.
 http://transdroid.googlecode.com/hg/lib/src/org/transdroid/daemon/util/FakeSocketFactory.java	r1de55ccfce7b
 http://transdroid.googlecode.com/hg/lib/src/org/transdroid/daemon/util/FakeTrustManager.java	rfa98b5bb5624

- MyWebViewClient (unknown) - needs to access redmine by webview
 https://github.com/potaka001/WebViewBasicAuthTest/blob/master/src/com/webviewbasicauthtest/MyWebViewClient.java

Using Icons
==========
- redmine (by-sa/2.5)
- http://www.redmine.org/projects/redmine/wiki/Logo
 Redmine Logo is Copyright (C) 2009 Martin Herr and is licensed under the Creative Commons Attribution-Share Alike 2.5 Generic license.
 See http://creativecommons.org/licenses/by-sa/2.5/ for more details.

- Glyphish (by/3.0/us)
 Great icons from Glyphish 
 Created by Joseph Wain, 2012
 Web: http://glyphish.com or http://penandthink.com
 This work is licensed under the Creative Commons Attribution 3.0 United States License. To view a copy of this license,
 visit http://creativecommons.org/licenses/by/3.0/us/ .
