OpenRedmine
===========

OpenRedmine is an android Redmine client.
This project is open source.

Features
==========
* Allow to connect UNSAFE SSL sites powered by transdroid
* View issues offline

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



Version
==========
Current commit is alpha version.
Some feature is not complete or incorrect.


Using Libraries
==========
- android-form-edittext (MIT) - needs verification about inputs
-- https://github.com/vekexasia/android-form-edittext

- Circular Buffer (GPL) - needs to parse async.
-- http://ostermiller.org/utils/CircularByteBuffer.html

- Transdroid (GPL) - needs to access untrusted ca.
-- http://transdroid.googlecode.com/hg/lib/src/org/transdroid/daemon/util/FakeSocketFactory.java	r1de55ccfce7b
-- http://transdroid.googlecode.com/hg/lib/src/org/transdroid/daemon/util/FakeTrustManager.java	rfa98b5bb5624
