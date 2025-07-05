# Bounty Board: Aplicativo de Gestão de Comissões

## Sobre o Projeto
O Bounty Board é um aplicativo Android, desenvolvido como parte da disciplina de Programação para Dispositivos Móveis, focado na gestão e cálculo de comissões para equipes de vendas. O sistema foi projetado para se integrar com a plataforma CRM Rubeus, automatizando processos e fornecendo transparência tanto para gestores quanto para consultores.

O aplicativo atende a dois perfis de usuário distintos:

- **Supervisor:** Possui uma visão gerencial da equipe, podendo acompanhar o desempenho geral, gerenciar consultores, criar e atribuir regras de comissão.

- **Consultor:** Tem acesso ao seu desempenho individual, visualizando vendas, comissões acumuladas e o progresso em relação às suas metas.

O objetivo final é otimizar a eficiência e a clareza no controle de comissões, facilitando o acompanhamento de metas e resultados financeiros.

## Principais Funcionalidades
- **Dashboards de Desempenho:** Telas separadas para Supervisores e Consultores com métricas visuais (gráficos e barras de progresso) sobre vendas, comissões e metas.

- **Gerenciamento de Consultores (Supervisor):** Funcionalidade completa para adicionar, editar e remover consultores da equipe, incluindo a atribuição de regras de comissão e metas específicas para cada um.

- **Criação e Gestão de Regras:** Um sistema para criar regras de comissão baseadas em processos e etapas específicas do funil de vendas (integrado ao Rubeus).

- **Visualização de Regras e Metas:** Telas dedicadas para que tanto supervisores quanto consultores possam visualizar as regras e metas que lhes são aplicáveis.

- **Geração de Relatórios:** Ambos os perfis podem exportar relatórios de desempenho detalhados em formato CSV.

- **Filtragem de Dados:** Os dashboards permitem a filtragem de dados por períodos de tempo (todo o período, mês atual, últimos 3 ou 6 meses).

- **Autenticação de Usuários:** Sistema de login que direciona o usuário para a interface correta com base em seu perfil.

## Informações de Código

- **Linguagem:** Java

- **Networking:** Retrofit & Gson.

- **Gerenciamento de Dados:**

  - **API Híbrida:** O app consome dados de duas fontes: a API real do CRM Rubeus (para usuários, processos) e uma API Mock em PHP (para regras, metas e CRUD de usuários).

  - **DataCache Singleton:** Uma classe central que armazena em memória todos os dados carregados das APIs, servindo como uma única fonte de verdade para todo o aplicativo. Isso reduz a latência e o número de chamadas de rede.

## Configuração e Execução
Para executar o projeto localmente, é necessário configurar tanto a API mock quanto o aplicativo Android.

### Pré-requisitos
- Java JDK (versão 11 ou superior)

- Android Studio

- XAMPP (ou qualquer outro servidor local com suporte a PHP e MySQL)

**1. Configuração da API Mock**
A API mock é responsável por fornecer os dados de regras e metas.

1. Instale o XAMPP e inicie os serviços Apache e MySQL.

2. Acesse o repositório da API: `https://github.com/ojuanfreire/apimock.git` e baixe ou clone o conteúdo.

3. Navegue até o diretório de instalação do XAMPP e encontre a pasta ``htdocs``.

4. Dentro de ``htdocs``, crie uma nova pasta chamada ``api-comissao``.

5. Cole todos os arquivos da API que você baixou no passo 2 dentro da pasta ``api-comissao``. A API agora deve estar acessível em ``http://localhost/api-comissao/``.

**2. Configuração do App Android**
1. Clone este repositório e abra o projeto no Android Studio.

2. Aguarde o Gradle sincronizar todas as dependências.

3. Ajuste o IP de conexão:

  - Abra o arquivo ``app/src/main/java/com/grupo6/appdecomissao/remote/MockApiClient.java``.

  - Altere a ``BASE_URL`` para o endereço IP do seu computador na rede local. Por exemplo: ``private static final String BASE_URL = "http://192.168.1.10/api-comissao/";``.

  - Se estiver usando um emulador Android (AVD), o IP padrão para acessar o ``localhost`` da sua máquina é ``10.0.2.2``. A URL seria: ``http://10.0.2.2/api-comissao/``.

4. Permita o tráfego de rede local:

  - Abra o arquivo ``app/src/main/res/xml/network_security_config.xml``.

  - Adicione uma nova entrada ``<domain includeSubdomains="true">SEU_IP_AQUI</domain>`` com o mesmo IP que você configurou no passo anterior. Isso é necessário para que o Android permita a conexão com um endereço HTTP local.

**3. Execução**
1. Conecte um dispositivo Android físico (na mesma rede Wi-Fi que o seu computador) ou inicie um emulador.

2. Execute o aplicativo a partir do Android Studio.


## Integrantes do Grupo 6
- **Carolina Sarah Pacelli Ferreira**

- **Juan Freire**

- **Pedro Henrique Barbosa Carvalho**

- **Pedro Henrique de Moura**
