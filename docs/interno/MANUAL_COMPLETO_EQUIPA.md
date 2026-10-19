# MANUAL CONSOLIDADO DA EQUIPA — POO II

> Versão de leitura numa só página. Os mesmos conteúdos estão nos ficheiros individuais de `docs/` e `docs/interno/`; em caso de diferença, prevalecem os ficheiros individuais.


---

<!-- Ficheiro: docs/interno/00_COMECAR_AQUI.md -->

# LEIAM PRIMEIRO — Guia interno da equipa de POO II

**Uso:** documento temporário de coordenação. A equipa já escolheu/distribuiu as seis opções; preencher os nomes em [`01_PLANO_E_DIVISAO.md`](01_PLANO_E_DIVISAO.md). Não é necessário entregar esta pasta com o projecto final.

## O que estamos a construir

Uma **aplicação desktop Java Swing**, ligada por JDBC a MySQL, com MVC + DAO. Negócio: fornecimento de água na Província de Maputo. A aplicação é **só para funcionários**, divididos em perfis de acesso `ADMIN`/`OPERADOR`. Clientes são registados, mas não fazem login.

O esquema vem do Anexo A de BD II e as regras do minimundo. São oito entidades principais: `cliente`, `contrato`, `contador`, `tarifa`, `fatura`, `pagamento`, `funcionario`, `reclamacao`; duas tabelas técnicas: `log_auditoria`, `log_transacao`. A autenticação requer a nova tabela `utilizador`.

## Ler por esta ordem

1. [`01_PLANO_E_DIVISAO.md`](01_PLANO_E_DIVISAO.md) — responsabilidade de cada elemento.
2. [O teu documento individual](elementos/) — o que fazer, entregar e testar.
3. [`07_DADOS_E_REGRAS.md`](07_DADOS_E_REGRAS.md) — nomes reais das colunas e principais RN.
4. [`02_PADROES_CODIGO.md`](02_PADROES_CODIGO.md) — convenções de Java/Swing/JDBC.
5. [`03_CONTRATOS_INTEGRACAO.md`](03_CONTRATOS_INTEGRACAO.md) — métodos e dependências a acordar.
6. [`04_GIT_GITHUB.md`](04_GIT_GITHUB.md) — branches, Issues e Pull Requests.
7. [`05_TAREFAS_E_FASES.md`](05_TAREFAS_E_FASES.md) — sequência de trabalho.
8. [`06_TESTES_E_CRITERIOS.md`](06_TESTES_E_CRITERIOS.md) — o que significa terminar uma tarefa.
9. [`08_CHECKLIST_ENTREGA.md`](08_CHECKLIST_ENTREGA.md) — preparação da apresentação e limpeza final.
10. [`09_DECISOES_PENDENTES.md`](09_DECISOES_PENDENTES.md) — dúvidas a resolver **antes** de criar código incompatível.

## Regras que ninguém pode ignorar

- **Não começar por seis implementações incompatíveis:** congelar nomes de classes, tipos de dados e assinaturas de integração.
- Cada elemento entrega o módulo completo: Model + DAO + Controller + painel Swing + testes/documentação do módulo.
- O responsável pela Opção 6 apoia os outros na interface, testes e documentação; **não** substitui a implementação dos módulos.
- Nunca criar a tabela `leitura`: a leitura está em `fatura`.
- Nunca pôr SQL dentro de `JPanel`/`JFrame`.
- Nunca fazer commits directamente em `main`; seguir o processo de PR.
- Não editar Anexo A, nem aplicar Anexo C, sem decisão colectiva.
- Não versionar credenciais, ficheiros locais nem dados pessoais reais.

## O que cada pessoa deve fazer no primeiro dia

1. Confirmar a sua opção e colocar o nome no quadro de atribuições.
2. Ler o guia individual e identificar as suas dependências.
3. Configurar Git, JDK, Maven e MySQL da versão escolhida.
4. Criar as Issues que lhe pertencem, com resultados verificáveis.
5. Trabalhar primeiro nos contratos/interfaces e classes Model acordados.

**Importante:** esta documentação é um plano. Ao executar, actualizar os checklists e as decisões; não assumir que o código já existe.


---

<!-- Ficheiro: docs/interno/01_PLANO_E_DIVISAO.md -->

# Divisão da equipa — seis opções já atribuídas

Os nomes devem ser preenchidos pelo responsável do projecto; **não trocar de módulo sem informar a equipa**. A divisão mantém **3 difíceis, 2 médias, 1 fácil**. Pretende-se compatibilizar capacidades com complexidade, não igualar a dificuldade. Carga operacional distribuída com apoio da Opção 6.

| Opção | Nível | Área principal | Responsável (preencher) |
|---|---|---|---|
| 1 | Difícil | Arquitectura, JDBC base, autenticação, controlo de acesso | **[NOME 1]** |
| 2 | Difícil | Tarifas e facturação | **[NOME 2]** |
| 3 | Difícil | Contratos e pagamentos | **[NOME 3]** |
| 4 | Médio | Clientes e contadores | **[NOME 4]** |
| 5 | Médio | Funcionários e reclamações | **[NOME 5]** |
| 6 | Fácil + apoio | MainFrame, Dashboard, testes manuais e apoio Swing | **[NOME 6]** |

**Nota:** o coordenador de arquitectura (Opção 1) não é obrigado a escrever os Models dos outros. É responsável pelo esqueleto e pelas decisões partilhadas. Cada um implementa as classes e interfaces do seu módulo.

## Entregas demonstráveis

| Opção | Demonstração obrigatória |
|---|---|
| 1 | Login ADMIN/OPERADOR, logout, permissões funcionais e ligação JDBC |
| 2 | Consultar/gerir tarifas e emitir uma factura com cálculo correcto |
| 3 | Criar/alterar contrato e registar pagamento parcial sem ultrapassar saldo |
| 4 | Registar/pesquisar cliente e instalar/trocar contador respeitando unicidade |
| 5 | Gerir funcionário e reclamação com responsável/estados válidos |
| 6 | Navegar pelos painéis e mostrar Dashboard com contagens reais + checklist de testes |

## Responsabilidade comum a TODOS

- Criar Model, DAO, Controller, View e Service (quando necessário) das suas tarefas.
- Implementar validações e mensagens de erro úteis.
- Testar dados válidos, inválidos e relações com outras tabelas.
- Documentar como testar cada funcionalidade em cada PR.
- Participar nas revisões de código e aprender a explicar a própria implementação.

## Integração entre módulos

`cliente` → `contrato` → `contador` → `fatura` → `pagamento`; tarifa e técnico leitor alimentam facturação; reclamação liga-se a cliente e funcionário; login associa-se a funcionário.

Antes de integrar, combinar: como seleccionar entidades por ID, como carregar JComboBox, quem abre o diálogo, como passar dados entre Controllers e como partilhar Connection numa transacção.


---

<!-- Ficheiro: docs/arquitectura.md -->

# Arquitectura da aplicação (MVC + DAO)

## Princípios

Fluxo normal: **utilizador → View Swing → Controller → Service (se houver regras) → DAO/JDBC → MySQL**. Model representa as entidades. O Controller responde a eventos sem escrever SQL; DAO não mostra mensagens Swing; Service centraliza validações e transacções que envolvem múltiplas tabelas.

```text
src/main/java/mz/agua/
├── Main.java
├── model/             Cliente, Contrato, Contador, Tarifa, Fatura,
│                      Pagamento, Reclamacao, Funcionario, Usuario
├── dao/               Um DAO por entidade, mais DashboardDAO
├── service/           Regras de negócio e transacções multi-entidade
├── controller/        Recebe eventos da View e coordena serviços
├── view/
│   ├── LoginFrame.java
│   ├── MainFrame.java
│   ├── panels/         ClientePanel, ContratoPanel, ...
│   ├── dialogs/        Formulários para inserção/actualização
│   └── components/     Componentes gráficos reutilizáveis
├── config/            DatabaseConnection e configuração segura
├── exception/         Excepções de domínio, quando necessárias
└── util/              Utilitários realmente partilhados
```

## Janelas

`LoginFrame` abre antes da janela principal. `MainFrame` utiliza `BorderLayout` (menu + conteúdo central); o conteúdo usa `CardLayout` para alternar entre `JPanel`. Formulários são `JDialog` modais ou painéis dedicados, conforme necessidade. A equipa deve usar as mesmas convenções de cores, nomes e tamanhos. Não criar um `JFrame` para cada operação.

## Quem faz o quê

- `model`: atributos do esquema e objectos de domínio; não conhece Swing/JDBC.
- `view`: recolhe valores e apresenta resultados; pode validar formato básico, mas **não** persistir directamente.
- `controller`: recebe a intenção do utilizador, obtém dados dos formulários, chama serviços e actualiza a View.
- `service`: regras do minimundo, cálculos, permissões e coordenação de operações atómicas.
- `dao`: `PreparedStatement`, `ResultSet`, conversão de dados e SQL. Sem SQL nas Views.
- `config`: fornece uma `Connection` nova para cada unidade de trabalho; fechar recursos adequadamente.

## Decisões técnicas a manter

1. Classes em `PascalCase`; atributos e métodos em `camelCase`; nomes SQL como no Anexo A.
2. Em Java, `DECIMAL` → `BigDecimal`; `DATE` → `LocalDate`; IDs `INT` → `int`/`Integer`.
3. Tipos `ENUM` SQL serão tratados de forma consistente (enums Java ou conversão validada, a decidir antes de integrar).
4. Coluna `contador.activo` é **gerada pelo MySQL**: não deve ser escrita com INSERT/UPDATE.
5. Emissão de factura e registo de pagamentos podem exigir transacções JDBC explícitas, com `commit`/`rollback`.
6. Consultas demoradas com `SwingWorker`; actualizações da interface apenas na EDT.
7. Não acrescentar frameworks ORM nem outras tecnologias sem aprovação da equipa.

## Dependências

- Opção 1: ligação JDBC, sessão e autenticação.
- Opção 3: contratos são consumidos pelas Opções 2 e 4; pagamentos usam facturas da Opção 2.
- Opção 4: contadores são consumidos pela facturação.
- Opção 5: técnicos de leitura são consultados pela facturação; funcionários são ligados a utilizadores da Opção 1.
- Opção 6: navegação e componentes comuns são consumidos por todos.

Consultar os **contratos entre módulos** antes de mudar nomes ou assinaturas: [`docs/interno/03_CONTRATOS_INTEGRACAO.md`](interno/03_CONTRATOS_INTEGRACAO.md).


---

<!-- Ficheiro: docs/casos-de-uso.md -->

# Casos de uso e controlo de acesso

**Actores:** `OPERADOR` e `ADMIN`. O ADMIN acumula permissões operacionais.

| Código | Caso de uso | OPERADOR | ADMIN |
|---|---|:---:|:---:|
| UC01 | Iniciar e terminar sessão | ✓ | ✓ |
| UC02 | Registar, pesquisar, listar e actualizar clientes | ✓ | ✓ |
| UC03 | Consultar contratos de um cliente | ✓ | ✓ |
| UC04 | Criar, pesquisar e alterar estados de contratos | ✓ | ✓ |
| UC05 | Instalar, consultar e trocar contadores | ✓ | ✓ |
| UC06 | Consultar tarifas | ✓ | ✓ |
| UC06-A | Criar tarifas e gerir a vigência | — | ✓ |
| UC07 | Registar leitura e emitir factura | ✓ | ✓ |
| UC08 | Consultar facturas e saldos | ✓ | ✓ |
| UC09 | Registar pagamentos totais/parciais | ✓ | ✓ |
| UC10 | Abrir, atribuir e resolver reclamações | ✓ | ✓ |
| UC11 | Gerir funcionários | — | ✓ |
| UC12 | Criar, activar e desactivar contas de utilizador | — | ✓ |
| UC13 | Consultar Dashboard | ✓ | ✓ |

**O cargo do funcionário** (ex.: `tecnico_leitura`) não é o mesmo que o perfil de acesso (`ADMIN`/`OPERADOR`). Só um técnico de leitura pode constar como responsável pela leitura na `fatura`, independentemente do perfil de quem opera o sistema.

## UC07: emissão de factura

**Pré-condições:** sessão válida; contrato e contador activos; referência mensal ainda não facturada para o contador; técnico com cargo `tecnico_leitura`; tarifa em vigor na `data_leitura`.

1. Funcionário selecciona contrato e contador.
2. Sistema consulta leitura anterior (última `leitura_atual` do contador, senão `leitura_inicial`).
3. Funcionário informa mês/ano, data e leitura actual, e identifica técnico leitor.
4. Sistema valida sequência de leitura, vigência da tarifa e duplicação por período.
5. Calcula `consumo = leitura_atual - leitura_anterior` e `valor_base = max(consumo, consumo_minimo) * preco_por_m3`.
6. Calcula vencimento para o dia **10 do mês seguinte à emissão**; apresenta resumo para confirmação.
7. Regista `fatura` com `id_tarifa` e `valor_base` fixados na emissão.

**Falhas:** leitura menor que a anterior, tarifa inexistente, técnico inadequado, período duplicado ou concorrência → mensagem adequada, nenhuma factura parcial.

## UC09: registar pagamento

**Pré-condições:** sessão válida, factura existente e não paga.

1. Seleccionar factura e consultar saldo actualizado.
2. Informar valor, data, método e recibo único.
3. Aplicar RN38: para `dinheiro`, `transferencia` ou `cartao`, guardar funcionário em `registado_por`; para `mpesa`/`emola`, guardar `NULL`.
4. Numa mesma transacção, bloquear a factura, recalcular o saldo e impedir exceder o saldo ou pagar factura já liquidada.
5. Inserir pagamento e marcar factura como `paga` quando saldo zero.
6. Confirmar; actualizar a listagem.

**Falhas:** recibo duplicado, valor inválido ou tentativa concorrente → `rollback`, comunicação clara ao utilizador.

## UC12: criar conta

1. ADMIN escolhe funcionário existente sem conta.
2. Define username único, perfil, e palavra-passe inicial.
3. Aplicação gera hash de password (bcrypt/Argon2id, não texto simples).
4. Guarda conta ligada ao `id_funcionario` e informa resultado sem mostrar hash ou password em logs.

## Limites do projecto

Sem portal de clientes, sem app móvel, sem integração real com M-Pesa/e-Mola, sem emissão de leitura autónoma. O Dashboard deve apresentar dados reais, não números fictícios. O comportamento de multas e corte/religação deve obedecer ao minimundo e ao âmbito decidido na equipa.


---

<!-- Ficheiro: docs/interno/07_DADOS_E_REGRAS.md -->

# Dicionário curto do Anexo A e regras essenciais

**Fontes:** `database/bdii/Anexo_A_Esquema.sql` e o documento *Minimundo e Regras de Negócio* de BD II. Este resumo é específico do **Anexo A sem optimização** e não substitui o SQL.

## Tabelas e colunas (Anexo A)

| Tabela | Colunas existentes |
|---|---|
| `cliente` | `id_cliente`, `nome_completo`, `nuit`, `telefone`, `rua`, `numero_casa`, `bairro` |
| `funcionario` | `id_funcionario`, `nome`, `telefone`, `cargo` |
| `tarifa` | `id_tarifa`, `tipo_contrato`, `preco_por_m3`, `consumo_minimo`, `data_inicio`, `data_fim` |
| `contrato` | `id_contrato`, `tipo_contrato`, `estado_contrato`, `data_inicio`, `data_fim`, `rua`, `numero_porta`, `quarteirao`, `bairro`, `id_cliente` |
| `contador` | `id_contador`, `numero_contador`, `data_instalacao`, `data_remocao`, `leitura_inicial`, `estado_contador`, **`activo` (coluna gerada!)**, `id_contrato` |
| `fatura` | `id_fatura`, `mes_referencia`, `ano_referencia`, `data_leitura`, `leitura_anterior`, `leitura_atual`, `data_emissao`, `data_vencimento`, `valor_base`, `valor_multa`, `estado_fatura`, `id_contador`, `id_tarifa`, `id_funcionario` |
| `pagamento` | `id_pagamento`, `data_pagamento`, `valor`, `metodo_pagamento`, `numero_recibo`, `id_fatura`, `registado_por` |
| `reclamacao` | `id_reclamacao`, `tipo_reclamacao`, `descricao`, `data_abertura`, `data_fecho`, `estado_reclamacao`, `id_cliente`, `id_funcionario` |
| `log_auditoria` | `id_log`, `usuario`, `acao`, `tabela_afetada`, `registro_id`, `detalhe`, `data_hora` |
| `log_transacao` | `id_log`, `transacao`, `utilizador`, `tipo_operacao`, `inicio`, `fim`, `estado`, `codigo_erro` |

**Apenas colunas do Anexo A:** `valor_pago`, `consumo_m3`, `valor_total`, `saldo_em_divida` **não existem** fisicamente na `fatura`. O Anexo C muda isso; ver [`database/README.md`](../../database/README.md).

## Valores ENUM exactos

- `contrato.tipo_contrato`, `tarifa.tipo_contrato`: `residencial`, `comercial`.
- `contrato.estado_contrato`: `activo`, `suspenso`, `cortado`, `terminado`.
- `contador.estado_contador`: `activo`, `inactivo`, `avariado`.
- `funcionario.cargo`: `tecnico_leitura`, `tecnico_manutencao`, `atendimento`, `supervisor`, `administrador`.
- `fatura.estado_fatura`: `pendente`, `paga`, `vencida`.
- `pagamento.metodo_pagamento`: `dinheiro`, `transferencia`, `mpesa`, `emola`, `cartao`.
- `reclamacao.tipo_reclamacao`: `fuga`, `avaria_contador`, `falta_agua`, `qualidade_agua`, `faturacao`, `atendimento`, `outro`.
- `reclamacao.estado_reclamacao`: `aberta`, `em_tratamento`, `resolvida`.

## Relações principais

```text
cliente (1) ─── (N) contrato (1) ─── (N) contador (1) ─── (N) fatura
    |                                                              |
    └─────── (N) reclamacao                        tarifa (1) ───────┤
                                               funcionario (1) ─────┤
                                                              pagamento (N)
funcionario (1) ─── (N) reclamacao [responsável, opcional]
funcionario (1) ─── (N) pagamento [registado_por, opcional]
```

As referências cruzadas são representadas pelas FKs no esquema; consultar MER para leitura rigorosa de cardinalidades.

## Regras relevantes para implementação

- **RN01–06:** cliente/contrato. NUIT opcional e único; contrato pode ser terminado uma única vez e deve ter data de fim; local de fornecimento pode diferir da morada do cliente.
- **RN07–11:** contador. Número de série único; apenas um activo por contrato; `leitura_inicial` alimenta a primeira factura.
- **RN12–14:** tarifa. Preço/mínimo positivos; intervalos sem sobreposição; tarifa vigente depende da data e do tipo do contrato; não editar tarifa antiga usada.
- **RN15–23:** factura. Apenas contrato/contador activos; no máximo 1 factura por contador e mês; técnico leitor obrigatório; cálculo do valor-base; vencimento a 10 do mês seguinte.
- **RN24–31:** pagamentos, multas, corte e religação. Não pagar além do saldo; multa de 25% uma vez quando aplicável; saldo devedores inclui multa.
- **RN32–35:** reclamações. Ligadas a cliente, não a contador/contrato; responsável exigido ao iniciar tratamento; resolver exige data de fecho.
- **RN36–38:** cargos válidos; manter histórico; `registado_por` NULL nos métodos `mpesa` e `emola`, preenchido nos demais.
- **RN39–42:** auditoria e registo de transacções.

## Cálculos

```text
consumo_m3   = leitura_atual - leitura_anterior
consumo_cobrado = MAX(consumo_m3, consumo_minimo)
valor_base   = consumo_cobrado * preco_por_m3
valor_total  = valor_base + valor_multa
valor_pago   = SUM(pagamento.valor da fatura)
saldo        = valor_total - valor_pago
```

O valor da factura deve ser tratado com `BigDecimal`. Consultas SQL devem respeitar a variante de BD congelada.

## Atenção a detalhes do SQL

1. A validação de sobreposição de tarifa ocorre no trigger `trg_tarifa_before_insert`; a UI também deve prevenir uma má entrada.
2. O trigger `trg_fatura_before_insert` verifica se o funcionário tem cargo `tecnico_leitura`.
3. Os CHECKs e UNIQUE protegem parte do modelo, mas **não substituem** validações de fluxo na aplicação.
4. O Anexo A inclui dados de exemplo e `DROP DATABASE`: executar com cuidado.
5. A versão optimizada de BD II cria dados derivados na factura que **não estão** no esquema-base; nunca os assumir sem decidir qual variante será usada.


---

<!-- Ficheiro: docs/interno/02_PADROES_CODIGO.md -->

# Convenções técnicas obrigatórias

## Linguagem e estrutura

- Package raiz: `mz.agua`.
- Classes: `Cliente`, `FaturaDAO`, `FaturaController`, `FaturaPanel`.
- Métodos/campos: `listarClientes()`, `idCliente`, `precoPorM3`.
- Nomes persistidos em MySQL permanecem iguais ao Anexo A (`id_fatura`, `estado_contrato`, etc.).
- Nomes de classes sem acentos; textos apresentados ao utilizador podem ter acentos.
- Evitar criar classes, interfaces e heranças sem responsabilidade real.
- Todas as operações sensíveis devem ter mensagens de sucesso/erro claras.

## Tipos

| SQL | Java |
|---|---|
| `INT`, `SMALLINT`, `TINYINT` numérico | `int` / `Integer` (conforme nulidade) |
| `VARCHAR`, `TEXT`, `ENUM` | `String` validada, ou enum Java padronizado |
| `DECIMAL(...)` | `BigDecimal` |
| `DATE` | `LocalDate` |
| `DATETIME`, `TIMESTAMP` | `LocalDateTime` (se necessários) |
| `TINYINT` para booleano de `utilizador` | `boolean` ou `Boolean` |

Nunca calcular valores monetários com `double`; usar operações e arredondamento explícito de `BigDecimal`.

## MVC e Swing

- `View`: componentes Swing, apresentação, leitura de campos e eventos; **sem SQL**.
- `Controller`: liga eventos a serviços/DAOs; devolve respostas para a View.
- `Service`: valida regras complexas, calcula valores e coordena transacções.
- `DAO`: apenas SQL, mapeamento de resultados e persistência.
- Usar `JTable` para listas; mensagens por `JOptionPane` ou componente definido pela equipa.
- `MainFrame` partilhado, navegação com `CardLayout`.
- Consultas potencialmente lentas devem correr em `SwingWorker`; nunca actualizar Swing fora da EDT.

## JDBC

- Sempre `PreparedStatement` para dados de utilizador; nunca construir WHERE com concatenação de entradas.
- Fechar `ResultSet`, `Statement` e `Connection` após o uso; nunca manter conexão global eternamente aberta.
- A fábrica de ligação é central, não duplicada em cada módulo.
- Para pagamentos/geração de factura, estabelecer transacções claras e `rollback` em falha.
- Erros SQL técnicos podem ser registados sem credenciais; ao utilizador mostrar uma mensagem compreensível.
- Se for usado `SET @app_utilizador = ...` para os triggers de auditoria, fazê-lo **em cada Connection relevante**, porque a variável é específica da sessão MySQL.

## Segurança

- Password apenas como `password_hash`, com mecanismo apropriado (bcrypt/Argon2id) acordado.
- Não confiar só em esconder botões: os Controllers/Services também verificam permissões.
- Não hardcode passwords JDBC ou dados de autenticação nos ficheiros Java.
- Não registar informações sensíveis em logs de depuração.

## Git

- Commits pequenos que descrevam alterações: `feat(fatura): calcular consumo minimo`, `fix(cliente): validar NUIT opcional`.
- Não formatar o projecto todo numa PR de apenas um módulo.
- Não mudar esquemas e dependências do Maven sem informar a equipa.

## Excepções, testes e documentação

- Erros de validação devem ser distinguíveis de erros de ligação SQL.
- Um erro não deve deixar uma transacção parcialmente aplicada.
- Criar testes de serviços/cálculos sem necessidade de Swing sempre que possível.
- Testes JDBC de integração devem usar base de testes descartável e dados controlados.


---

<!-- Ficheiro: docs/interno/03_CONTRATOS_INTEGRACAO.md -->

# Contratos entre módulos (API interna proposta)

**Objectivo:** evitar que seis elementos inventem assinaturas incompatíveis. Estes métodos são **propostas a validar numa reunião curta antes de programar**; após aprovação, qualquer mudança deve passar por revisão.

## Princípio de colaboração

As Views trabalham com IDs seleccionados e objectos Model, não com SQL de outro módulo. O Controller usa Service/DAO do dono do módulo. O DAO pode receber uma `Connection` criada pela camada de serviço, permitindo que vários DAOs participem na mesma transacção JDBC.

**Convenção proposta de ciclo de ligação:** `DatabaseConnection.getConnection()` devolve uma conexão nova. Serviços de operações compostas obtêm a conexão, criam os DAOs necessários com ela, controlam commit/rollback e garantem o fecho. Serviços simples podem chamar um DAO com Connection própria. **Não criar diferentes modelos de gestão de Connection por módulo.**

## Interfaces de leitura que outros módulos precisam

| Dono | Quem consome | Operação (assinatura conceptual) |
|---|---|---|
| Opção 4 (Clientes) | 3, 5, 6 | `buscarClientePorId(int id)`; `pesquisarClientes(String termo)` |
| Opção 3 (Contratos) | 2, 4 | `buscarContratoPorId(int id)`; `listarContratosActivos()`; `listarPorCliente(int idCliente)` |
| Opção 4 (Contadores) | 2, 3 | `buscarContadorActivo(int idContrato)`; `buscarContadorPorId(int id)` |
| Opção 5 (Funcionários) | 1, 2, 3 | `buscarFuncionarioPorId(int id)`; `listarTecnicosLeitura()` |
| Opção 2 (Tarifas) | 2 (Factura) | `buscarTarifaVigente(String tipoContrato, LocalDate dataLeitura)` |
| Opção 2 (Facturas) | 3, 6 | `buscarFaturaPorId(int id)`; `listarFaturasPorContrato(int idContrato)`; `obterLeituraAnterior(int idContador)` |
| Opção 3 (Pagamentos) | 2, 6 | `obterTotalPago(int idFatura)`; `obterSaldo(int idFatura)` |
| Opção 1 (Sessão) | Todos | `getFuncionarioAutenticado()`; `isAdmin()`; `terminarSessao()` |
| Opção 6 (Navegação) | Todos | `abrirModulo(String chave)` e convenção dos IDs de cartões |

As assinaturas finais devem indicar tipos de retorno (`Optional<T>`, `List<T>`, `BigDecimal`, etc.), tipos de excepção e onde o `Connection` é recebido. **Não tratar esta tabela como código pronto para compilar.**

## Contratos de dados críticos

### Tarifa

`tipo_contrato` é `'residencial'` ou `'comercial'`. A tarifa válida em `data_leitura` satisfaz `data_inicio <= data` e `(data_fim IS NULL OR data_fim >= data)`. `data_fim` é inclusiva; não criar sobreposição de intervalos. Tarifas históricas usadas em facturas não mudam retroactivamente.

### Factura

A emissão precisa de contrato activo, contador activo, período e técnico de leitura; a leitura anterior vem da factura precedente ou da leitura inicial. Não existem `consumo_m3`, `valor_pago`, `valor_total` nem `saldo_em_divida` persistidos no esquema-base. A Opção 2 disponibiliza dados necessários para a Opção 3 pagar a factura.

### Pagamento

O saldo deve ser recalculado dentro da transacção após bloquear a factura (`SELECT ... FOR UPDATE`). Nunca confiar só no saldo mostrado anteriormente na UI. No Anexo A, soma de `pagamento.valor` alimenta o saldo, mas o Anexo C muda a estratégia — consultar [`database/README.md`](../../database/README.md).

### Funcionário vs Utilizador

`funcionario.cargo` determina funções de negócio como técnico de leitura. `utilizador.perfil` determina autorização para aceder à aplicação. São conceitos independentes.

## Regras de integração

1. Abrir Issue para dependência ainda não definida.
2. Publicar exemplos de entradas, saídas e erros.
3. Rever com o responsável consumidor **antes** de mudar interface já usada.
4. Preferir IDs/Models estáveis; não passar referências a componentes Swing entre módulos.
5. Integrar alterações pequenas, não esperar pelos seis módulos terminados.


---

<!-- Ficheiro: docs/interno/04_GIT_GITHUB.md -->

# GitHub — fluxo de colaboração

## Estrutura do repositório

```text
fornecimento-agua-poo2/
├── README.md
├── pom.xml                        # adicionado pela Opção 1
├── .gitignore
├── database/
│   ├── README.md
│   ├── 01_esquema_bdii.sql        # a adicionar após revisão
│   ├── 02_autenticacao.sql        # a adicionar após revisão
│   └── 03_dados_teste.sql         # opcional, sem dados reais
├── docs/
│   ├── arquitectura.md
│   ├── casos-de-uso.md
│   ├── guia-de-execucao.md
│   └── interno/                   # trabalho da equipa; remover na entrega se desejado
├── src/
│   ├── main/
│   │   ├── java/mz/agua/
│   │   │   ├── Main.java
│   │   │   ├── model/
│   │   │   ├── dao/
│   │   │   ├── service/
│   │   │   ├── controller/
│   │   │   ├── view/{panels,dialogs,components}/
│   │   │   ├── config/
│   │   │   ├── exception/
│   │   │   └── util/
│   │   └── resources/
│   └── test/java/mz/agua/
└── .github/
    ├── PULL_REQUEST_TEMPLATE.md
    └── ISSUE_TEMPLATE/feature.md
```

## Branches

- `main`: versão estável e demonstrável.
- `develop`: integração de funcionalidades revistas.
- `feature/autenticacao`, `feature/facturacao-tarifas`, `feature/contratos-pagamentos`, `feature/clientes-contadores`, `feature/funcionarios-reclamacoes`, `feature/dashboard-interface`: branches de trabalho propostas.

Em equipas com várias Issues por módulo, preferir branches curtas por Issue, por exemplo `feature/42-criar-tarifa-dao`. **Não trabalhar todos sobre a mesma branch.**

## Ciclo de trabalho

1. Abrir/atribuir uma Issue com requisitos, dependências e critérios de aceitação.
2. Actualizar a branch a partir de `develop`.
3. Implementar uma funcionalidade pequena, compilar e testar localmente.
4. Fazer commits próprios; nunca inserir autores/coautores que não tenham contribuído.
5. Abrir Pull Request da branch para `develop`, usar o template e anexar screenshots da UI se necessário.
6. Conseguir revisão de pelo menos outro elemento (para mudanças estruturais: Opção 1).
7. Corrigir comentários; integrar somente quando o projecto compila e a funcionalidade funciona.
8. Periodicamente promover `develop` para `main` após teste completo.

### Comandos de referência

```bash
git checkout develop
git pull origin develop
git checkout -b feature/42-criar-tarifa-dao
# ... desenvolver, testar, adicionar ficheiros necessários ...
git add .
git commit -m "feat(tarifa): consultar tarifa vigente"
git push -u origin feature/42-criar-tarifa-dao
```

`git add .` deve ser precedido por `git status` para garantir que não inclui ficheiros secretos ou gerados.

## GitHub Projects

Estados recomendados: `Todo`, `In Progress`, `In Review`, `Done`. Uma Issue em `Done` deve ter PR integrado, testes e critérios de aceitação cumpridos. Etiquetas: `opcao-1` ... `opcao-6`, `swing`, `jdbc`, `bug`, `integration`, `documentation`.

## Regras adicionais

- Não editar `main` directamente.
- Não fazer `force push` em branches partilhadas sem decisão colectiva.
- Nunca publicar `application.properties` com passwords, dumps reais ou pastas `target/`.
- Combinar mudança de `pom.xml`, esquema SQL e interfaces partilhadas.
- Quando duas pessoas editam a mesma classe, definir quem é o responsável principal pelo ficheiro.


---

<!-- Ficheiro: docs/interno/05_TAREFAS_E_FASES.md -->

# Tarefas, fases e dependências

## Etapa 0 — decisões comuns (toda a equipa)

- [ ] Preencher nomes/responsabilidades.
- [ ] Fixar JDK, Maven, MySQL e política de configuração local.
- [ ] Decidir se a aplicação usa **Anexo A puro** (recomendação inicial) ou A+C.
- [ ] Congelar nomes, tipos e assinaturas dos métodos entre módulos.
- [ ] Definir `utilizador`/perfis e acesso inicial de teste.
- [ ] Estabelecer padrões de Swing (look & feel, dimensões, formulários).

## Etapa 1 — base compilável (Opção 1 + apoio 6)

- [ ] Criar repositório, `pom.xml`, `.gitignore` e packages.
- [ ] Implementar `Main`, `DatabaseConnection` e teste básico JDBC.
- [ ] Criar Models/assinaturas acordadas, sem multiplicar lógica duplicada.
- [ ] Preparar `MainFrame`/navegação com painéis vazios temporários.

## Etapa 2 — primeiros casos de uso em paralelo

- [ ] Opção 1: login/logout, perfis e sessão.
- [ ] Opção 4: CRUD cliente com JTable e validações.
- [ ] Opção 5: CRUD funcionário e reclamações simples.
- [ ] Opção 6: Dashboard com consultas reais disponíveis e auxílio aos formulários.
- [ ] Opção 2: `Tarifa`, `TarifaDAO`, gestão e testes de vigência.
- [ ] Opção 3: `Contrato`, `ContratoDAO`, consultas e alterações de estado.

## Etapa 3 — relações e operações complexas

- [ ] Opção 4: contador ligado ao contrato; troca de contador.
- [ ] Opção 2: `FaturaService`/`FaturaDAO`, cálculo de consumo e emissão.
- [ ] Opção 3: pagamento transaccional e saldo consistente.
- [ ] Opção 5: atribuir e resolver reclamações.
- [ ] Opção 1: autorização real em Controller/Service e suporte técnico à integração.

## Etapa 4 — integração e testes

- [ ] Fazer login → seleccionar cliente → consultar contrato → contador → factura → pagamento.
- [ ] Verificar restrições de autenticação, tarifa, estados, leituras e pagamentos.
- [ ] Testar erros SQL sem bloquear Swing.
- [ ] Testar concorrência de pagamentos, com duas sessões/threads se o grupo tiver ambiente adequado.
- [ ] Rever permissões do Dashboard e menus.

## Etapa 5 — entrega

- [ ] Confirmar `mvn clean test`, arranque e execução em máquina limpa.
- [ ] Actualizar README e documentação de execução.
- [ ] Preparar dados fictícios reproduzíveis de demonstração.
- [ ] Remover documentação interna temporária **se essa for a decisão da equipa**.
- [ ] Rever histórico para evitar segredos; remover ficheiros não apaga commits antigos.
- [ ] Tag/release estável em `main` e ensaio de apresentação por cada elemento.

## Prioridade, não duração fixa

Uma etapa pode começar antes de outra terminar se os contratos estiverem acordados. Nenhum elemento deve esperar pelos outros para construir a sua própria View ou escrever testes unitários. Integrações pequenas e frequentes são preferíveis a uma junção final de seis branches enormes.


---

<!-- Ficheiro: docs/interno/06_TESTES_E_CRITERIOS.md -->

# Plano de testes e definição de concluído

## Uma tarefa só está concluída se...

- [ ] O código compila e respeita os packages/nomenclatura combinados.
- [ ] A funcionalidade usa o MySQL por JDBC, sem simular dados numa lista fixa (excepto fixtures de teste).
- [ ] Casos de sucesso e pelo menos um caso de erro foram testados.
- [ ] Os dados apresentados na JTable são recarregados após actualizar.
- [ ] A interface não fica inutilizável durante consultas lentas.
- [ ] As regras de negócio aplicáveis são respeitadas.
- [ ] Há instruções de teste suficientes no Pull Request.
- [ ] O código foi revisto e integrado em `develop`.

## Matriz mínima de testes

| ID | Módulo | Teste | Esperado |
|---|---|---|---|
| T01 | Login | credenciais válidas / inválidas | entra ou mostra erro, sem revelar password |
| T02 | Perfil | OPERADOR tenta gerir utilizadores | operação recusada também na lógica |
| T03 | Cliente | NUIT duplicado | validação/erro por UNIQUE, sem inserção duplicada |
| T04 | Contrato | terminar sem data de fim | não permitido |
| T05 | Contador | segundo contador activo no mesmo contrato | recusado pela regra de negócio/UNIQUE |
| T06 | Tarifa | períodos sobrepostos para o mesmo tipo | não permitido |
| T07 | Tarifa | consultar tarifa de data passada | tarifa histórica correcta |
| T08 | Factura | leitura inferior à anterior | não emitida |
| T09 | Factura | período já faturado no mesmo contador | não emitida |
| T10 | Factura | técnico que não é `tecnico_leitura` | não emitida |
| T11 | Factura | consumo real inferior ao mínimo | factura pelo consumo mínimo |
| T12 | Pagamento | valor supera saldo | nenhum pagamento inserido |
| T13 | Pagamento | duas tentativas simultâneas de pagar o mesmo saldo | total pago não ultrapassa saldo |
| T14 | Pagamento | M-Pesa com `registado_por` preenchido | não permitido |
| T15 | Reclamação | `em_tratamento` sem responsável | não permitido |
| T16 | Reclamação | `resolvida` sem data de fecho | não permitido |
| T17 | Histórico | nova tarifa após factura antiga | valor-base antigo não muda |
| T18 | Dashboard | resumo após inserir dados | números actualizados, obtidos da BD |

## Dados para demonstração

O Anexo A já contém dados **de exemplo**. Nunca testar inserções destrutivas numa base importante; usar instância de desenvolvimento separada. Evitar assumir que IDs começam sempre por 1 ou que uma factura específica estará presente. Recriar estado de teste quando necessário, lembrando que o Anexo A apaga a base de dados com `DROP DATABASE`.

## Tipos de teste

- **Unitário:** cálculos do consumo e do valor-base, validações independentes da BD.
- **Integração:** DAO ↔ MySQL, transacções, constraints e triggers.
- **Interface:** botões, JTable, JComboBox, diálogos e navegação.
- **Aceitação:** percorrer os casos de uso como um funcionário faria.

## Bugs no GitHub

Issue de bug deve conter ambiente, passos para reproduzir, resultado esperado, resultado obtido e evidência (sem informações sensíveis). Não encerrar bugs com apenas uma explicação textual: corrigir e retestar.


---

<!-- Ficheiro: docs/interno/elementos/01_ARQUITECTURA_AUTENTICACAO.md -->

# Opção 1 — Arquitectura, JDBC e autenticação

**Nível:** difícil  
**Responsável:** [NOME]  
**Branch sugerida:** `feature/autenticacao`

## Resultado esperado

Projecto Maven compila e arranca; JDBC comunica com MySQL; funcionários autorizados fazem login; ADMIN e OPERADOR vêem/acedem apenas às operações permitidas; existe um ponto central para conexão e sessão.

## Classes e ficheiros sob a tua responsabilidade

```text
pom.xml
src/main/java/mz/agua/Main.java
src/main/java/mz/agua/config/DatabaseConnection.java
src/main/java/mz/agua/model/Usuario.java
src/main/java/mz/agua/dao/UsuarioDAO.java
src/main/java/mz/agua/controller/LoginController.java
src/main/java/mz/agua/service/AutenticacaoService.java
src/main/java/mz/agua/view/LoginFrame.java
src/main/java/mz/agua/util/Sessao.java
# componentes de autorização/perfis, se necessários
database/02_autenticacao.sql
```

## Tarefas

- [ ] Criar `pom.xml`, packages, `.gitignore`, `Main.java` e dependência JDBC.
- [ ] Definir como criar e fechar Connection e como gerir transacções multi-DAO.
- [ ] Criar extensão SQL `utilizador`, ligada a `funcionario` por FK única; `username` único, hash de password, perfil e estado.
- [ ] Implementar autenticação segura (hash apropriado), conta inactiva e saída da sessão.
- [ ] Implementar controlo de acesso nos Controllers/Services; não apenas ocultar botões.
- [ ] Confirmar com a Opção 5 métodos de consulta de funcionário.
- [ ] Definir e publicar contratos de integração com todas as opções.
- [ ] Rever PRs estruturais (`pom.xml`, schema, packages, Connection).
- [ ] Testar login válido, inválido, conta inactiva e permissões.

## Dependências

Opção 5 fornece `FuncionarioDAO`/modelo; Opção 6 fornece `MainFrame` e navegação. Tu forneces ligação JDBC e sessão a todos.

## Critérios de aceitação

- Login consulta efectivamente o MySQL.
- Password não está em texto simples no esquema, no código ou nos logs.
- ADMIN/OPERADOR bloqueiam operações mesmo se se tentar invocar o Controller directamente.
- `DatabaseConnection` é utilizado pelos restantes módulos sem configurações duplicadas.
- Maven compila a estrutura inicial; não existem credenciais versionadas.

**Não fazer:** concentrar todas as funcionalidades num único Controller, alterar entidades de outros membros sem acordo ou criar classes fictícias apenas para a interface parecer funcional.


---

<!-- Ficheiro: docs/interno/elementos/02_TARIFAS_FACTURACAO.md -->

# Opção 2 — Tarifas e facturação

**Nível:** difícil  
**Responsável:** [NOME]  
**Branch sugerida:** `feature/facturacao-tarifas`

## Resultado esperado

Consultar/gerir tarifas sem sobreposição e emitir facturas correctas, com cálculos baseados nos dados reais do contador, contrato, data e tarifa.

## Classes e ficheiros

```text
model/Tarifa.java
model/Fatura.java
dao/TarifaDAO.java
dao/FaturaDAO.java
service/TarifaService.java
service/FaturaService.java
controller/TarifaController.java
controller/FaturaController.java
view/panels/TarifaPanel.java
view/panels/FaturaPanel.java
view/dialogs/TarifaDialog.java
view/dialogs/EmissaoFaturaDialog.java
```

## Regras do minimundo

- **RN12–14:** `preco_por_m3 > 0`, `consumo_minimo > 0` (default 5); vigências por tipo sem sobreposição; procurar tarifa conforme `tipo_contrato` e `data_leitura`.
- **RN15:** emitir apenas para contrato **e** contador activos.
- **RN16:** máximo uma factura por `(id_contador, ano_referencia, mes_referencia)`.
- **RN17–18:** leitura anterior = última leitura actual do contador, ou `leitura_inicial`; leitura actual >= anterior.
- **RN19:** técnico da leitura tem cargo `tecnico_leitura` (trigger já existe).
- **RN20:** `valor_base = max(leitura_atual - leitura_anterior, consumo_minimo) * preco_por_m3`.
- **RN21:** conservar tarifa e valor-base históricos; não recalcular faturas antigas com preço novo.
- **RN22:** vencimento no dia 10 do mês seguinte à **emissão**, não à leitura.
- **RN23:** estado inicial `pendente`.

## Tarefas ordenadas

- [ ] Mapear atributos de `tarifa` e `fatura` exactamente do Anexo A.
- [ ] Implementar `TarifaDAO`: listar, pesquisar por tipo/data, criar tarifa, fechar vigência (com validação de RN13).
- [ ] Implementar `TarifaPanel` e `TarifaDialog`; edição de preço histórico usada em facturas **proibida**.
- [ ] Implementar `FaturaService` com `BigDecimal`, cálculo e testes unitários.
- [ ] Acordar com Opção 3 obtenção de contrato e com Opção 4 contador activo/leitura inicial.
- [ ] Acordar com Opção 5 listagem de técnicos `tecnico_leitura`.
- [ ] Implementar `FaturaDAO` e emissão transaccional, tratando factura duplicada.
- [ ] Implementar `FaturaPanel`/`EmissaoFaturaDialog`: seleccionar dados, simular valor-base, confirmar emissão e listar.
- [ ] Consultar facturas por cliente/contrato, via caminho `fatura → contador → contrato → cliente`.
- [ ] Testar RN12–23 e histórico de tarifas.

## Dados de `fatura` que deves persistir

`mes_referencia`, `ano_referencia`, `data_leitura`, `leitura_anterior`, `leitura_atual`, `data_emissao`, `data_vencimento`, `valor_base`, `valor_multa`, `estado_fatura`, `id_contador`, `id_tarifa`, `id_funcionario`. `id_fatura` é auto-incrementado. `valor_pago`, `consumo_m3`, `valor_total`, `saldo_em_divida` **não são colunas do Anexo A**: o Anexo C muda isso; consultar a decisão da equipa.

## Dependências

- Opção 1: JDBC e sessão.
- Opção 3: contratos e integração com pagamentos (tua responsabilidade não é registar pagamentos).
- Opção 4: contadores activos e leitura inicial.
- Opção 5: técnicos de leitura.
- Opção 6: menu e consistência visual.

## Aceitação e testes

- [ ] Tarifa vigente correcta por tipo/data e erro em caso de período sobreposto.
- [ ] Exemplo: leitura anterior 120, actual 123, mínimo 5, preço 45 → `valor_base = 225.00 MT`.
- [ ] Não emite factura para contrato cortado ou contador inactivo.
- [ ] Não emite leitura decrescente, duplicada ou de técnico não autorizado.
- [ ] Data de vencimento e valor-base persistidos correctamente.
- [ ] A criação de uma tarifa nova **não altera** facturas antigas.
- [ ] Interface não executa SQL e não bloqueia o utilizador em consultas demoradas.

**Cuidado:** não criar classe/tabela `Leitura` autónoma e não implementar `PagamentoDAO` por conta própria.


---

<!-- Ficheiro: docs/interno/elementos/03_CONTRATOS_PAGAMENTOS.md -->

# Opção 3 — Contratos e pagamentos

**Nível:** difícil  
**Responsável:** [NOME]  
**Branch sugerida:** `feature/contratos-pagamentos`

## Resultado esperado

Criar/consultar contratos e efectuar pagamentos totais ou parciais respeitando os estados, as FKs e a concorrência.

## Classes e ficheiros

```text
model/Contrato.java
model/Pagamento.java
dao/ContratoDAO.java
dao/PagamentoDAO.java
service/ContratoService.java
service/PagamentoService.java
controller/ContratoController.java
controller/PagamentoController.java
view/panels/ContratoPanel.java
view/panels/PagamentoPanel.java
view/dialogs/ContratoDialog.java
view/dialogs/RegistoPagamentoDialog.java
```

## Tarefas

- [ ] Criar Models exactamente do Anexo A.
- [ ] Implementar CRUD operacional de contratos (criar, listar, pesquisar, alterar estados permitidos).
- [ ] Validar `tipo_contrato`, datas e RN05: contrato `terminado` tem `data_fim` e não volta a activo.
- [ ] Coordenar primeira criação cliente+contrato com Opção 4.
- [ ] Implementar selecção de factura, apresentação de valor, total já pago e saldo.
- [ ] Implementar registo de pagamento JDBC **transaccional**: bloquear factura `SELECT ... FOR UPDATE`, recalcular saldo, validar, inserir `pagamento`, actualizar estado se pago, `commit`; em erro `rollback`.
- [ ] Implementar RN38: `registado_por = NULL` para `mpesa` e `emola`, preenchido nos outros métodos.
- [ ] Garantir número único de recibo e proibir pagamento acima do saldo ou de factura paga.
- [ ] Discutir com Opção 2 a origem do saldo e diferenças entre Anexo A e C.
- [ ] Testar pagamentos parciais, recibo duplicado, pagamento exacto e concorrente.
- [ ] Definir com a equipa o alcance das multas e da mudança automática de estados.

## Integrações

- Opção 4: clientes/contadores; Opção 2: facturas e tarifas; Opção 5: funcionário que regista; Opção 1: sessão JDBC; Opção 6: menu.

## Critérios de aceitação

- [ ] Contratos associados a clientes válidos, datas consistentes e estados legais.
- [ ] Pagamentos parciais mantêm saldo positivo; último pagamento fecha saldo e marca factura paga.
- [ ] Duas operações concorrentes nunca ultrapassam o saldo.
- [ ] Métodos móveis/canais ao balcão cumprem o CHECK RN38.
- [ ] O histórico de pagamentos existe e não é apagado nem alterado (RN40).
- [ ] Sem SQL na View nem consultas bloqueantes na EDT.

**Cuidado:** `saldo_em_divida` não é uma coluna em Anexo A; só existe como coluna gerada se a equipa aplicar Anexo C.


---

<!-- Ficheiro: docs/interno/elementos/04_CLIENTES_CONTADORES.md -->

# Opção 4 — Clientes e contadores

**Nível:** médio  
**Responsável:** [NOME]  
**Branch sugerida:** `feature/clientes-contadores`

## Resultado esperado

CRUD completo de clientes, pesquisa, consulta dos contratos e gestão de contadores ligados aos contratos, com no máximo um activo.

## Classes e ficheiros

```text
model/Cliente.java
model/Contador.java
dao/ClienteDAO.java
dao/ContadorDAO.java
service/ContadorService.java
controller/ClienteController.java
controller/ContadorController.java
view/panels/ClientePanel.java
view/panels/ContadorPanel.java
view/dialogs/ClienteDialog.java
view/dialogs/ContadorDialog.java
```

## Tarefas

- [ ] Mapear os sete campos de `cliente`, sem inventar campos de morada.
- [ ] Fazer `ClienteDAO` para inserir, procurar por nome/ID, listar e actualizar.
- [ ] Criar `JTable` com pesquisa e formulários com validação.
- [ ] Validar `nome_completo`, `telefone`, rua, número e bairro obrigatórios; `nuit` opcional e único.
- [ ] Implementar `ContadorDAO`: criar contador, procurar por contrato, actualizar estado e data de remoção.
- [ ] Garantir número de série único, leitura inicial não negativa e datas consistentes.
- [ ] Fazer troca de contador com desactivação do antigo e activação do novo de forma consistente.
- [ ] Acordar com Opção 3 como escolher contratos válidos/activos.
- [ ] Entregar métodos para Opção 2 consultar contador activo e leitura inicial.
- [ ] Testar erro para segundo contador activo no mesmo contrato (`uq_contador_activo`).

## Critérios de aceitação

- [ ] Inserção/pesquisa/actualização reflectidas em MySQL e JTable.
- [ ] NUIT duplicado não cria cliente novo.
- [ ] Não é possível associar dois contadores activos a um contrato.
- [ ] Troca de contador preserva histórico e data de remoção.
- [ ] `contador.activo` (gerado pelo MySQL) **não** é preenchido por Java.
- [ ] Métodos de consulta usados pela facturação estão prontos e documentados.

**Cuidado:** a primeira contratação liga cliente+contrato; combinar com Opção 3 um fluxo coerente com o minimundo.


---

<!-- Ficheiro: docs/interno/elementos/05_FUNCIONARIOS_RECLAMACOES.md -->

# Opção 5 — Funcionários e reclamações

**Nível:** médio  
**Responsável:** [NOME]  
**Branch sugerida:** `feature/funcionarios-reclamacoes`

## Resultado esperado

Gestão de funcionários pelo ADMIN e tratamento de reclamações por funcionários, incluindo atribuição e encerramento.

## Classes e ficheiros

```text
model/Funcionario.java
model/Reclamacao.java
dao/FuncionarioDAO.java
dao/ReclamacaoDAO.java
controller/FuncionarioController.java
controller/ReclamacaoController.java
service/ReclamacaoService.java
view/panels/FuncionarioPanel.java
view/panels/ReclamacaoPanel.java
view/dialogs/FuncionarioDialog.java
view/dialogs/ReclamacaoDialog.java
```

## Tarefas

- [ ] CRUD operacional de funcionários (sem apagar os associados a facturas/reclamações).
- [ ] Validar `cargo` nos valores exactos do esquema: `tecnico_leitura`, `tecnico_manutencao`, `atendimento`, `supervisor`, `administrador`.
- [ ] Disponibilizar consulta/listagem de técnicos `tecnico_leitura` à Opção 2.
- [ ] Coordenação com Opção 1 para ligar `funcionario` a `utilizador`; não criar password em `funcionario`.
- [ ] Criar reclamação associada apenas a `cliente` (não a contrato/contador).
- [ ] Gerir atribuição de funcionário, estados (`aberta`, `em_tratamento`, `resolvida`) e datas.
- [ ] Validar que `em_tratamento` requer responsável e `resolvida` requer data de fecho.
- [ ] Implementar pesquisa por cliente, estado e funcionário responsável.
- [ ] Testar restrições e permissões (gestão de funcionário apenas ADMIN).

## Critérios de aceitação

- [ ] Funcionário criado e pesquisável; cargo válido.
- [ ] Funcionário com histórico ligado não é apagado.
- [ ] Reclamação aberta pode estar sem responsável; em tratamento não pode.
- [ ] Resolver preenche data de fecho coerente.
- [ ] Opção 2 consegue listar técnicos leitores.
- [ ] Gestão de funcionários está protegida por permissões no Controller/Service.

**Cuidado:** `funcionario.cargo` não é o mesmo que o perfil de acesso `ADMIN`/`OPERADOR`.


---

<!-- Ficheiro: docs/interno/elementos/06_MAINFRAME_DASHBOARD_APOIO.md -->

# Opção 6 — MainFrame, Dashboard, testes e apoio à equipa

**Nível:** fácil (na lógica de negócio) + responsabilidade transversal de apoio  
**Responsável:** [NOME]  
**Branch sugerida:** `feature/dashboard-interface`

## Resultado esperado

Interface principal navegável, padrões visuais consistentes, Dashboard com dados reais e contributo activo para testes e correcções de usabilidade.

## Classes e ficheiros

```text
view/MainFrame.java
view/panels/DashboardPanel.java
controller/DashboardController.java
dao/DashboardDAO.java
view/components/               # componentes reutilizáveis aprovados
# documentação de screenshots e testes, quando aplicável
```

## Tarefas

- [ ] Construir `MainFrame` com menu lateral e conteúdo central em `CardLayout`.
- [ ] Criar pontos de navegação para os nove módulos, com placeholders apenas enquanto o módulo ainda não estiver pronto.
- [ ] Garantir que ADMIN/OPERADOR vêem menus adequados à sessão, recebida da Opção 1.
- [ ] Criar `DashboardPanel` com resumos SQL reais: contagem de clientes, contratos activos, facturas pendentes, reclamações abertas (ajustar consultas ao esquema congelado).
- [ ] Definir padrões comuns de botões, filtros, tamanhos, tabelas e diálogos.
- [ ] Ajudar os colegas a ajustar GridLayout, BorderLayout, JTable, JScrollPane, formulários e feedback visual.
- [ ] Executar testes manuais dos módulos integrados e abrir Issues com resultados reproduzíveis.
- [ ] Organizar screenshots ilustrativos e verificar documentação de execução.
- [ ] Rever visualmente os painéis para consistência, sem alterar regras de negócio dos responsáveis.

## Como apoiar os outros

Combinar com cada colega pequenas subtarefas: arrumar formulário, criar modelo de `JTable`, melhorar filtros, configurar `JScrollPane`, corrigir layout ou documentar casos de teste. Fazer mudanças em PRs atribuídos e coordenados; nunca substituir a autoria dos colegas sem acordo.

## Critérios de aceitação

- [ ] Troca de painéis não abre dezenas de JFrames.
- [ ] Dashboard não contém números fictícios fixos no código.
- [ ] Menus correspondem ao perfil e actualizam após autenticação.
- [ ] `SwingWorker` (quando necessário) não bloqueia interface.
- [ ] Pelo menos um registo de teste documentado em cada módulo integrado.
- [ ] Guia de execução e lista de problemas conhecidos estão actualizados.

**Cuidado:** embora seja a opção mais fácil, o Dashboard exige consultas JDBC e coordenação com os outros módulos.


---

<!-- Ficheiro: docs/interno/09_DECISOES_PENDENTES.md -->

# Decisões que a equipa precisa de confirmar

Este documento identifica **questões ainda não resolvidas**, para não confundir propostas de arquitectura com o esquema de BD II.

| Decisão | Recomendação inicial | Quem aprova | Estado |
|---|---|---|---|
| Variante de BD | Anexo A sem Anexo C no primeiro incremento | Todos + Opção 1 | Pendente |
| Versão do JDK | JDK 21 LTS, se todos dispuserem | Todos | Pendente |
| Versão do MySQL | MySQL 8+ | Todos | Pendente |
| Modelo de `utilizador` | Tabela separada com FK única para `funcionario`, hash e perfil | Opções 1 e 5 | Pendente |
| Estrutura final dos DAOs | Uma convenção comum de Connection e excepções | Todos + Opção 1 | Pendente |
| Enums Java | Enums com mapeamento para valores SQL | Todos | Pendente |
| Primeira criação cliente+contrato | Fluxo coordenado, respeitando o minimundo | Opções 3 e 4 | Pendente |
| Multas e corte/religação automáticos | Delimitar o que é do POO II; não inventar job sem acordo | Opções 2 e 3 | Pendente |
| Estilo visual Swing | Tema comum de formulários e janelas | Opções 1 e 6 | Pendente |
| Instrumentação de `log_transacao` | Definir escopo antes de incluir em cada operação | Opção 1 + Todos | Pendente |

## Divergência a resolver

O minimundo discute a **desnormalização de BD II** (`valor_pago` materializado e outras colunas geradas) em parte do texto. No entanto, o **Anexo A isolado** ainda não contém essas colunas. Só o Anexo C as introduz. Portanto, os DAOs precisam de ser escritos para a variante escolhida e testada — não combinar trechos SQL de versões diferentes.

## Questões funcionais

- A criação de um cliente no sistema deve ser seguida de um contrato; o minimundo pressupõe que clientes se registam ao celebrar o primeiro contrato. Decidir se o fluxo de primeira contratação será um assistente/transacção ou duas acções coordenadas.
- O utilizador logado pode ter cargo de atendimento e registar uma factura **em nome do técnico leitor**, mas a `fatura.id_funcionario` deve apontar para funcionário com cargo `tecnico_leitura`.
- O módulo de pagamentos deve implementar exactamente a RN38 dos canais móveis, sem simular ligações reais às carteiras digitais.
- Sobre multas: a regra RN28 prevê execução diária na lógica transaccional de BD II; a aplicação POO II precisa de explicitar se haverá opção manual de administração, simulação controlada ou agendamento real.

Preencher decisão, responsável e data após a discussão. Decisões fechadas passam a orientações normativas nos documentos de arquitectura e testes.


---

<!-- Ficheiro: docs/interno/08_CHECKLIST_ENTREGA.md -->

# Checklist final e limpeza de documentação interna

## Antes da entrega

- [ ] `main` tem uma versão estável e demonstrável (sem bugs bloqueantes).
- [ ] `mvn clean test` funciona em máquina limpa com JDK/Maven definidos.
- [ ] Existe procedimento reproduzível para preparar a base de desenvolvimento.
- [ ] Login, clientes, contratos, contadores, tarifas, facturas, pagamentos, reclamações e funcionários cumprem o âmbito acordado.
- [ ] A separação MVC/DAO é verificável no código.
- [ ] `SwingWorker` ou abordagem equivalente impede travamentos nos fluxos lentos.
- [ ] Dados de apresentação são fictícios e não contêm credenciais reais.
- [ ] README, guia de execução e arquitectura reflectem o código realmente entregue.
- [ ] Cada elemento sabe demonstrar o seu módulo e explicar JDBC, eventos Swing e classes.

## O que pode ser removido

**Pasta temporária opcional:** `docs/interno/` (distribuição de tarefas, progressos, regras internas e notas de organização). A `.github/ISSUE_TEMPLATE` pode ficar ou ser removida; os Issue/PR históricos podem manter-se no GitHub.

**O que normalmente deve ficar:** `README.md`, `docs/arquitectura.md`, `docs/casos-de-uso.md`, `docs/guia-de-execucao.md`, scripts SQL realmente necessários, código-fonte, `pom.xml` e `.gitignore`.

### Exemplo de remoção na fase final

```bash
git rm -r docs/interno
git commit -m "docs: remover notas internas de coordenação"
```

**Isto só retira os ficheiros do estado actual da branch; eles continuam acessíveis no histórico Git.** Se houver informações sensíveis, não as publicar desde o início. Não reescrever histórico por iniciativa própria sem acordo e sem compreender o efeito na equipa.

## Preparação da defesa

- Cada elemento apresenta um caso de uso do seu módulo.
- Explicar o caminho View → Controller → Service → DAO → JDBC → MySQL.
- Mostrar validação no Java e protecção na BD (CHECK, UNIQUE, FK, trigger) quando aplicável.
- Explicar como os `PreparedStatement` evitam concatenação de SQL.
- Demonstrar erros controlados (não apenas casos felizes).
- Mostrar que não há valores falsos no Dashboard.

## Revisão de autoria e colaboração

Cada membro é responsável pelos próprios commits e alterações. Não inserir nomes de pessoas ou ferramentas como autor/coautor sem contribuição real; não incluir marcas ou menções a assistentes no código e na documentação final. Manter histórico íntegro da colaboração e respeitar os requisitos académicos aplicáveis.
