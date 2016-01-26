OpenRedmine
===========
Histórico de releases

Coisas a serem feitas
===========
- Verificar data para o bug de atualização (#83)
- Adicionar botão de compartilhamento na tarefa (#18)

Próxima Release
===========
- Adicionada tradução para chinês feita por StevenGape (#171)

v3.17 - 50 - 31/12/2015
===========
- Adicionada tradução para português do Brasil feita por etcho (#169)
- Corrigida a busca na lista de projetos (#168)
- Adicionada busca na lista de wikis (#168)
- Adicionado webview no menu (#167)

v3.16 - 49 - 18/11/2015
===========
- Suporte ao estilo de exibição de data e hora "yyyy-MM-dd HH:mm:ss Z" (#157)
- Suporte a pull-to-refresh-event na Visualização de Tarefa (#157)
- Corrigida NumberFormatException (#161) / NullPointerException em IssueEdit (#162)
- Reduzir permissões (#160)
- Migrar para appcompat-v7. Remover actionbarsherlock, actionbarpulltorefresh. Obrigado eterno. (#144)

v3.15 - 48 - 07/02/2015
===========
- Adicionada tradução para Alemão feita por markusr
- Suporte a relações entre diários (#151)

v3.14 - 47 - 29/10/2014
===========
- Obter observadores (#132)
- Corrigir misc (#142)
 - Suporte à nova API no fragmento WebView
 - Fechar conexão
 - Remover código não utilizado
 - Suporte ao android studio 0.9.1


v3.13 - 45 - 04/10/2014
===========
- Colocar acesso fácil à tarefas na lista de projetos (#130)
- Adicionado suporte para informar o provedor do anexo (#131, #135)
- Adicionada lista de tarefas vistas recentemente (#137)
- Adicionado suporte a remoção de conexão (#138)


v3.12 - 44 - 06/09/2014
===========
- Adicionado suporte ao acesso dos Status de Projeto no Redmine 2.5.0 (#71)
- Exibir Ndias atrás ... etc (#28)

v3.11 - 43 - 16/08/2014
===========
- Mostrar notícias do projeto (#17,#118)
-  Adicionada página do projeto (#116)
-  Refatoração da wiki (#114)
 - Adicionado suporte a id de tarefas com colchetes
 - Uso de factory method no que diz respeito a XmlPullParser
 - Adicionada página pai
 - Refactor wiki
- Fix builds (#115,#119)

v3.10 - 42 - 2014/06/28
===========
- Add kanban view by long tap project (#108)
- Fix crashed when tap the recorded time (#103)
- Category list is not applied theme (#102)
- Fix not fetch issue detail from remote by pulling ... and more minor bug fix (#112)
- Allow input certification fingerprint to connection (#112)

v3.9 - 41 - 2014/05/17
===========
- Fix issue view icons
- Fix wiki link expressions
- Improved performance by changing issue detail from WebView to TextView

v3.8 - 40 - 2014/03/27
===========
- Add search interface on issues, projects
- (Internal changes) Update Android Studio from 0.4.2 to 0.5.1
- Bug fix crash on android 2.2 (#79,#56)
- Bug fix show journal changes (#81)
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
- Fix add new issue

v3.5 - 36 - 2014/01/24
===========
- Support wiki view
- Add tabs

v3.4 - 35 - 2013/12/10
===========
- Fix crash on Android 2.3

v3.4 - 34 - 2013/12/09
===========
- Switch list via swipe
- Support pull to refresh
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
- Fix issue view to align to center
- Show current user on project list

v2.5 - 29 - 2013/07/03
===========
- Show comment area always

v2.4 - 28 - 2013/06/06
===========
- Fix sync issues (loop forever)
- Fix posting in android 2.2 (v1.XmlPullParser support)

v2.3 - 27 - 2013/05/29
===========
- Add post notes to issue
- Fix edit issue about version/estimated time
- Add sort key
 - start/due/close date
 - priority/status/tracker
 - fixed_version/category
 - assigned to/author
 - done rate

v2.2 - 26 - 2013/05/25
===========
- Add post or modify issue
- Fix issue list

v2.1 - 23 - 2013/05/20
===========
- Fix sync project via http

v2.0 - 22 - 2013/05/14
===========
- Add post or modify time entry
- Fetch all of projects

v1.14 - 21 - 2013/05/01
===========
- Add sort feature
- Show changes on journals
- Show link (URLs) on issue details

v1.13 - 20 - 2013/04/19
===========
- Fix fetch issue
- Jump to issue from project list
- Add url input helper on connection

v1.12 - 19 - 2013/04/17
===========
- Fix update issue
- Jump to issue from description or journal
- Add tracker to issues list
- Refresh icons

v1.11 - 18 - 2013/04/08
===========
- Add time entries

v1.10 - 17 - 2013/03/31
===========
- Fix update issue

v1.9 - 15 - 2013/03/25
===========
- Add filter about Priority/Author/Assined
-  Fix update issue attributes

v1.8 - 14 - 2013/03/17
===========
- Add settings
- Fetch all issues(closed issues) by setting. By default fetches only unclosed issues.
- Add theme switcher

v1.7 - 13 - 2013/03/14
===========
- Supports textile in issue detail

v1.6 - 12 - 2013/02/27
===========
- Issue list keep scroll point.
- Fix filter shows nothing on the list.
- Upgeade android api level.

v1.5 - 10 - 2013/02/23
===========
- Support journals
- Changed fetching issues from remote

v1.4 - 9 - 2013/01/14
===========
- Reduce permission to write sd card.

v1.3 - 8 - 2012/12/01
===========
- Fix transfer authentications on getting information via web site
- Add feature filter

v1.2 - 7 - 2012/12/01
===========
- Fixes crash on startup.(build was failed)

v1.1 - 6 - 2012/12/01
===========
- Rewrite HTTP transport
- Connection via gzip
- Get versions on loading projects
- Add footer on ConnectionList
- Fix parse error on timezones
- Reconfigure splash activity

v1.0 - 1 - 2012/10/31
===========
- Fixes crash on startup.
- Fixes buttons when save connection.
- Creates new.