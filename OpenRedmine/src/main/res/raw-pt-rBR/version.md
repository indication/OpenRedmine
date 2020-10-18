OpenRedmine
===========
Histórico de releases

v$current_version$ - $current_build$
-----------
- Solicitar permissão ao baixar (#228)
- Suporte a exibição de imagens no formulário de download (#229)
- Atualizando o ORMLite (#230)
- Atualizando traduções
    - Alemão por markusr
    - Português(BR) por etcho


v3.23 - 665 - 08/10/2019
-----------
- Atualizando a versão do SDK para 28
- Tentando corrigir erro na obtenção do token (#218)
- Tentando corrigir crash no Android 8 (#219)
- Adicionada tradução para o tcheco por Mongata (#220)
- Adicionando tradução para o holandês feita por PanderMusubi (#224)

v3.22 - 652 - 28/09/2018
-----------
-  Ícones atualizados e agora é exibido o título na tarefa ou wiki (#207)
- Suporte a markdown
- Atualizando traduções
    - Português(BR) por etcho
    - Russo por roman.yagodin

v3.21 - 56 - 28/07/2017
-----------
- Atualização de ícones
- Mostrando título na tarefa ou wiki
- Adicionado filtro de tarefas para limitar tarefas fechadas e abertas (#199)
- Atualizando traduções
    - Alemão por Atalanttore
    - Português(BR) por etcho
- Correções de ortografia feitas por ka7 (#198)
- Correção de travamento em FileDownload (#202)
- Tentando corrigir a infrator exception
- Correção de vários bugs

v3.20 - 53 - 01/04/2017
-----------
- Adicionado id do journal no cabeçalho e melhorias de perfórmance (#195)
- Adicionada lista de projetos favoritos na conexão (#194)
- Adicionada lista de tarefas recentes na conexão (#194)
- Atualizando traduções
    - Alemão por Atalanttore
    - Português(BR) por etcho
- Adicionada tradução para francês por MagicFab (#193)
- Adicionada tradução para turco (Turquia) por halis.simsek (#193)
- Adicionada tradução para espanhol por Bernat13 (#193)
- Atualizada a versão da API mínima necessária de 8 para 9 (Android 2.2 não possui mais suporte)
- Corrigindo pequeno erro feito por elmanytas (#191)

v3.19 - 52 - 30/06/2016
-----------
- Alterado o método de conexão para o padrão android (removido org.apache.http.legacy)
- Corrigido bug ao atualizar tarefa/registro de tempo
- Corrigido remoção de cookies ao acessar via webclient (#180)
- Corrigido Alerta do Google Play: SSL Error Handler Vulnerability (#174)
- Atualizando traduções
    - Alemão por Atalanttore

v3.18 - 51 - 31/01/2016
-----------
- Atualizando traduções
    - Português(BR) por etcho
- Adicionada tradução para chinês feita por StevenGape (#171)

v3.17 - 50 - 31/12/2015
-----------
- Adicionada tradução para português do Brasil feita por etcho (#169)
- Corrigida a busca na lista de projetos (#168)
- Adicionada busca na lista de wikis (#168)
- Adicionado webview no menu (#167)

v3.16 - 49 - 18/11/2015
-----------
- Suporte ao estilo de exibição de data e hora "yyyy-MM-dd HH:mm:ss Z" (#157)
- Suporte a pull-to-refresh-event na Visualização de Tarefa (#157)
- Corrigida NumberFormatException (#161) / NullPointerException em IssueEdit (#162)
- Reduzir permissões (#160)
- Migrar para appcompat-v7. Remover actionbarsherlock, actionbarpulltorefresh. Obrigado eterno. (#144)

v3.15 - 48 - 07/02/2015
-----------
- Adicionada tradução para Alemão feita por markusr
- Suporte a relações entre diários (#151)

v3.14 - 47 - 29/10/2014
-----------
- Obter observadores (#132)
- Corrigir misc (#142)
 - Suporte à nova API no fragmento WebView
 - Fechar conexão
 - Remover código não utilizado
 - Suporte ao android studio 0.9.1


v3.13 - 45 - 04/10/2014
-----------
- Colocar acesso fácil à tarefas na lista de projetos (#130)
- Adicionado suporte para informar o provedor do anexo (#131, #135)
- Adicionada lista de tarefas vistas recentemente (#137)
- Adicionado suporte a remoção de conexão (#138)


v3.12 - 44 - 06/09/2014
-----------
- Adicionado suporte ao acesso dos Status de Projeto no Redmine 2.5.0 (#71)
- Exibir Ndias atrás ... etc (#28)

v3.11 - 43 - 16/08/2014
-----------
- Mostrar notícias do projeto (#17,#118)
-  Adicionada página do projeto (#116)
-  Refatoração da wiki (#114)
 - Adicionado suporte a id de tarefas com colchetes
 - Uso de factory method no que diz respeito a XmlPullParser
 - Adicionada página pai
 - Refatoração da wiki
- Correção de builds (#115, #119)

v3.10 - 42 - 28/06/2014
-----------
- Adicionada visualização do kanban ao pressionar e segurar um projeto (#108)
- Corrigido crash que ocorria ao pressionar a hora gravada (#103)
- Lista de categorias não sofre efeito do tema (#102)
- Corrigido bug que não trazia os detalhes de tarefas remotas ao fazer um pulling ... e outras correções menores (#112)
- Permitido informar certificação fingerprint para as conexões (#112)

v3.9 - 41 - 17/05/2014
-----------
- Corrigidos ícones da visualização da tarefa
- Corrigidas expressões de link da wiki
- Performance melhorada ao substituir os detalhes da tarefa de WebView por TextView

v3.8 - 40 - 27/03/2014
-----------
- Adicionada interface de busca em tarefas e projetos
- (Mudanças internas) Atualização do Android Studio de 0.4.2 para 0.5.1
- Corrigido bug de crash no android 2.2 (#79, #56)
- Corrigido bug na exibição de mudanças no journal (#81)
- Redução da validação de URL na tela de adicionar conexão (#84)

v3.7 - 39 - 28/02/2014
-----------
- Corrigido bug de crash ao obter dados remotos pela primeira vez (#68)
- (Mudanças internas) DAO foi movido para adapter (#61)
- Adicionada validação de URL (inicia com schema) para evitar crash (#67)

v3.6 - 38 - 15/02/2014
-----------
- Adicionada tradução para russo por box789
- Adicionada lista de projetos favoritos
- Correções de aparência na edição de conexão

v3.5 - 37 - 02/02/2014
-----------
- Obtém a wiki quando não houver itens
- Abrir atividade na tarefa selecionada
- Correção na tela de criar tarefa

v3.5 - 36 - 24/01/2014
-----------
- Suporte a visualização de wiki
- Adição de abas

v3.4 - 35 - 10/12/2013
-----------
- Correção de crash no Android 2.3

v3.4 - 34 - 09/12/2013
-----------
- Alternar entre listas usando swipe
- Suporte a "puxe para atualizar"
- (Mudanças internas) Porte para o Android Studio

v3.3 - 33 - 06/11/2013
-----------
- Download de arquivos relacionados às tarefas
- Correção de crash ao exibir tipo de relação desconhecido

v3.2 - 32 - 09/09/2013
-----------
- Correção de crashes ao obter uma tarefa remota - referências à tarefas relativas estavam erradas
- Atualização de submódulo - android-form-edittext

v3.1 - 31 - 05/09/2013
-----------
- Adicionado suporte a sticky view na tarefa
- Melhorias na exibição da lista de tarefas

v3.0 - 30 - 12/08/2013
-----------
- Suporte a fragmentos (códigos internos)
- Correção de timezone ao obter itens
- Correção do alinhamento na tela de visualização de tarefa para centralizado
- Mostra o usuário atual na lista de projetos

v2
===========

v2.5 - 29 - 03/07/2013
-----------
- Área de comentários agora é exibida sempre

v2.4 - 28 - 06/06/2013
-----------
- Correção na sincronização de tarefas (loop eterno)
- Correção nas postagens no android 2.2 (suporte a v1.XmlPullParser)

v2.3 - 27 - 29/05/2013
-----------
- Adicionado suporte a postagem de notas nas tarefas
- Correção na tela de edição de tarefa na parte de versão e tempo estimado
- Adicionada chave de ordenação
 - Suporte a datas de início/prazo/fechamento
 - Suporte a prioridade, situação e rastreadores de tarefas
 - Correção de versão/categoria
 - Suporte a atribuído para e autor
 - Suporte a percentual de conclusão

v2.2 - 26 - 25/05/2013
-----------
- Adicionado suporte a postar ou modificar tarefas
- Correção na lista de tarefas

v2.1 - 23 - 20/05/2013
-----------
- Correção na sincronização de projetos via http

v2.0 - 22 - 14/05/2013
-----------
- Adicionado suporte a postar ou modificar registros de tempo
- Adicionada opção de obter todos os projetos

v1
===========

v1.14 - 21 - 01/05/2013
-----------
- Adicionada função de ordenação
- Suporte a exibição de alterações e journals
- Suporte a exibição de links (URLs) nos detalhes da tarefa

v1.13 - 20 - 19/04/2013
-----------
- Correção na obtenção de tarefa
- Adicionada função de ir para a tarefa a partir da lista de projetos
- A inserção de url na conexão foi facilitada

v1.12 - 19 - 17/04/2013
-----------
- Correção na atualização de tarefas
- Adicionada função de ir para a tarefa a partir de sua descrição ou journal
- Suporte a adicionar rastreador na lista de tarefas
- Atualização de ícones

v1.11 - 18 - 08/04/2013
-----------
- Suporte a adição de registros de tempo

v1.10 - 17 - 31/03/2013
-----------
- Correção na atualização de tarefas

v1.9 - 15 - 25/03/2013
-----------
- Adicionado filtro por Prioridade/Autor/Atribuído para
- Correção na atualização de atributos das tarefas

v1.8 - 14 - 17/03/2013
-----------
- Adicionada tela de configurações
- Suporte a configuração para obter todas as tarefas(tarefas fechadas) ou não. Por padrão serão obtidas somente tarefas que não estiverem fechadas.
- Adiciona opção para mudança de tema

v1.7 - 13 - 14/03/2013
-----------
- Suporte a textile nos detalhes da tarefa

v1.6 - 12 - 27/02/2013
-----------
- A lista de tarefas mantém a localização da rolagem.
- Correção de filtros que não exibem nada na lista.
- Atualização do nível da api do android.

v1.5 - 10 - 23/02/2013
-----------
- Suporte a journals
- Alteração no processo de obtenção de tarefas remotas

v1.4 - 9 - 14/01/2013
-----------
- Simplificação de permissões para escrita no cartão sd.

v1.3 - 8 - 01/12/2012
-----------
- Correção nas autenticações de transferência durante a obtenção de informações via website
- Adicionada função de filtro

v1.2 - 7 - 01/12/2012
-----------
- Correções de crash na inicialização.(houve falha na build)

v1.1 - 6 - 01/12/2012
-----------
- Sobrescrita do HTTP transport
- Suporte a conexão via gzip
- Suporte a obter versões no carregamento de projetos
- Adicionado rodapé na Lista de Conexões
- Correção de erros na manipulação de timezones
- Reconfiguração da atividade splash

v1.0 - 1 - 31/10/2012
-----------
- Correção de crash na inicialização.
- Correções nos botões ao salvar a conexão.
- Criação de novas.