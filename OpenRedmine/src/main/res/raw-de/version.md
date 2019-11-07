OpenRedmine
===========
Veröffentlichungschronik

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
- Update icons and Show title on issue or wiki (#207)
- Markdown-Unterstützung
- Update translations
    - Portuguese(BR) by etcho
    - Russian by roman.yagodin

v3.21 - 56 - 28.07.2017
-----------
- Update icons
- Show title on issue or wiki
- Add issue filter to limit open or closed issues (#199)
- Update translations
    - German by Atalanttore
    - Portuguese(BR) by etcho
- Spelling fixes by ka7 (#198)
- Fix crash on FileDownload (#202)
- Try to fix infrator exception
- Fix several bugs

v3.20 - 53 - 01.04.2017
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

v3.19 - 52 - 30.06.2016
-----------
- Changed connection method to android default (Remove org.apache.http.legacy)
- Fix crash on update issue/time entry
- Fix remove cookies when access via webclient (#180)
- Fix Google Play Warning: SSL Error Handler Vulnerability (#174)
- Update translations
    - German by Atalanttore

v3.18 - 51 - 31.01.2016
-----------
- Update translations
    - Portuguese(BR) by etcho
- Chinesische Übersetzung von StevenGape hinzufügen (#171)

v3.17 - 50 - 31.12.2015
-----------
- Übersetzung für Brasilianisches Portugiesisch von etcho hinzufügen (#169)
- Suche in Projektliste reparieren (#168)
- Suche in Wikiliste hinzufügen (#168)
- Webansicht zu Menü hinzufügen (#167)

v3.16 - 49 - 18.11.2015
-----------
- Datumszeitstil "jjjj-MM-tt HH:mm:ss Z" unterstützen (#157)
- Support pull-to-refresh-event on IssueView (#157)
- Fix NumberFormatException (#161) / NullPointerException on IssueEdit (#162)
- Berechtigungen reduzieren (#160)
- Migrate to appcompat-v7. Remove actionbarsherlock, actionbarpulltorefresh, Thank you for ever. (#144)

v3.15 - 48 - 07.02.2015
-----------
- Deutsche Übersetzung von markusr hinzufügen
- Journal-Relationen unterstützen (#151)

v3.14 - 47 - 29.10.2014
-----------
- Abrufbeobachter (#132)
- Verschiedenes reparieren (#142)
 - Neue API in WebAnsicht-Fragment unterstützen
 - enge Verbindung
 - Nicht verwendeten Code löschen
 - Android Studio 0.9.1 unterstützen


v3.13 - 45 - 04.10.2014
-----------
- Put jump area to issue  on the project list (#130)
- Unterstützte Anhangsanbieter (#131, #135)
- Add recently viewed issue list (#137)
- Supported deleting connection (#138)


v3.12 - 44 - 06.09.2014
-----------
- Support parse Project Status on Redmine 2.5.0 (#71)
- Show Ndays before ... etc (#28)

v3.11 - 43 - 16.08.2014
-----------
- Projektneuigkeiten anzeigen (#17,#118)
-  Projektseite hinzufügen (#116)
-  Refactor wiki (#114)
 - Support for issue id with brackets
 - Use factory method about XmlPullParser
 -  Übergeordnete Seite hinzufügen
 - Refactor wiki
- Fix builds (#115,#119)

v3.10 - 42 - 28.06.2014
-----------
- Add kanban view by long tap project (#108)
- Fix crashed when tap the recorded time (#103)
- Category list is not applied theme (#102)
- Fix not fetch issue detail from remote by pulling ... and more minor bug fix (#112)
- Allow input certification fingerprint to connection (#112)

v3.9 - 41 - 17.05.2014
-----------
- Problemansichtssymbole reparieren
- Wikilinkausdrücke reparieren
- Improved performance by changing issue detail from WebView to TextView

v3.8 - 40 - 27.03.2014
-----------
- Suchoberflächen bei Problemen, Projekten hinzufügen
- (Interne Änderungen) Android Studio von 0.4.2 auf 0.5.1 aktualisieren
- Absturz auf Android 2.2 Fehler reparieren (#79,#56)
- Bug fix show journal changes (#81)
- Reduce URL validation on add connection (#84)

v3.7 - 39 - 28.02.2014
-----------
- Fix crash on fetch remote first time (#68)
- (Internal changes) Move DAO into adapter (#61)
- Add URL validation (start with schema) to avoid to crash (#67)

v3.6 - 38 - 15.02.2014
-----------
- Russische Übersetzung von box789 hinzufügen
- Projektfavoritenliste hinzufügen
- Erscheinungsbild bei Verbindung bearbeiten reparieren

v3.5 - 37 - 02.02.2014
-----------
- Fetch wiki when there is no item
- Open activity on select issue
- Fix add new issue

v3.5 - 36 - 24.01.2014
-----------
- Wikiansicht unterstützen
- Tabs hinzufügen

v3.4 - 35 - 10.12.2013
-----------
- Absturz auf Android 2.3 reparieren

v3.4 - 34 - 09.12.2013
-----------
- Liste durch Wischen wechseln
- Support pull to refresh
- (Interne Änderungen) Auf Android Studio portieren

v3.3 - 33 - 06.11.2013
-----------
- Download file related with issues
- Fixes crash on showing unknown relation type

v3.2 - 32 - 09.09.2013
-----------
- Fix crashes on fetching issue from remote - relative issue reference was wrong
- Update submodule - android-form-edittext

v3.1 - 31 - 05.09.2013
-----------
- Add sticky view on issue
- Renewal issue list view

v3.0 - 30 - 12.08.2013
-----------
- Support fragment (internal codes)
- Fix timezone when fetch items
- Fix issue view to align to center
- Show current user on project list

v2
===========

v2.5 - 29 - 03.07.2013
-----------
- Kommentarbereich immer anzeigen

v2.4 - 28 - 06.06.2013
-----------
- Fix sync issues (loop forever)
- Fix posting in android 2.2 (v1.XmlPullParser support)

v2.3 - 27 - 29.05.2013
-----------
- Add post notes to issue
- Fix edit issue about version/estimated time
- Sortierungsschlüssel hinzufügen
 - start/due/close date
 - priority/status/tracker
 - fixed_version/category
 - assigned to/author
 - done rate

v2.2 - 26 - 25.05.2013
-----------
- Add post or modify issue
- Problemliste reparieren

v2.1 - 23 - 20.05.2013
-----------
- Projekt über http synchronisieren reparieren

v2.0 - 22 - 14.05.2013
-----------
- Add post or modify time entry
- Alles der Projekte abrufen

v1
===========

v1.14 - 21 - 01.05.2013
-----------
- Sortierungsfunktion hinzufügen
- Show changes on journals
- Show link (URLs) on issue details

v1.13 - 20 - 19.04.2013
-----------
- Abrufproblem reparieren
- Jump to issue from project list
- Add url input helper on connection

v1.12 - 19 - 17.04.2013
-----------
- Aktualisierungsproblem reparieren
- Jump to issue from description or journal
- Add tracker to issues list
- Symbole aktualisieren

v1.11 - 18 - 08.04.2013
-----------
- Zeiteinträge hinzufügen

v1.10 - 17 - 31.03.2013
-----------
- Aktualisierungsproblem reparieren

v1.9 - 15 - 25.03.2013
-----------
- Add filter about Priority/Author/Assined
-  Fix update issue attributes

v1.8 - 14 - 17.03.2013
-----------
- Einstellungen hinzufügen
- Fetch all issues(closed issues) by setting. By default fetches only unclosed issues.
- Add theme switcher

v1.7 - 13 - 14.03.2013
-----------
- Supports textile in issue detail

v1.6 - 12 - 27.02.2013
-----------
- Issue list keep scroll point.
- Fix filter shows nothing on the list.
- Upgrade android api level.

v1.5 - 10 - 23.02.2013
-----------
- Support journals
- Changed fetching issues from remote

v1.4 - 9 - 14.01.2013
-----------
- Reduce permission to write sd card.

v1.3 - 8 - 01.12.2012
-----------
- Fix transfer authentications on getting information via web site
- Funktionsfilter hinzugefügt

v1.2 - 7 - 01.12.2012
-----------
- Fixes crash on startup.(build was failed)

v1.1 - 6 - 01.12.2012
-----------
- HTTP-Übertragung neu schreiben
- Verbindung über gzip
- Get versions on loading projects
- Add footer on ConnectionList
- Fix parse error on timezones
- Reconfigure splash activity

v1.0 - 1 - 31.10.2012
-----------
- Repariert Abstürze beim Start.
- Fixes buttons when save connection.
- Creates new.