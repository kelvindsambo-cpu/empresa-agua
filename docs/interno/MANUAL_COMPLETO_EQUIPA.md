# MANUAL CONSOLIDADO DA EQUIPA — POO II (plano simplificado)

> Versão de leitura numa só página. Os mesmos conteúdos estão nos ficheiros individuais de `docs/` e `docs/interno/`; em caso de diferença, prevalecem os ficheiros individuais.


---

<!-- Ficheiro: docs/interno/00_COMECAR_AQUI.md -->

# LEIAM PRIMEIRO — Guia interno da equipa de POO II (versão simplificada)

**Uso:** documento temporário de coordenação. Não é preciso entregá-lo com o projecto final.

> **Mudança de plano:** a equipa decidiu fazer o **mínimo apresentável, mas bem feito**. Cortámos tudo o que era avançado (concorrência, multas automáticas, gestão de vigências, Anexo C, `develop`, etc.). O que ficou está descrito aqui e em [`01_PLANO_E_DIVISAO.md`](01_PLANO_E_DIVISAO.md). Se algo não estiver nestes documentos, **não é para fazer**.

## O que estamos a construir

Uma **aplicação desktop Java Swing**, ligada por JDBC a MySQL/MariaDB, organizada em **MVC + DAO**. Negócio: fornecimento de água na Província de Maputo. Só funcionários usam a aplicação (perfis `ADMIN` e `OPERADOR`); clientes não fazem login.

Base de dados: **Anexo A de BD II, tal como está** (mais a tabela `utilizador` do login). As regras mais importantes já estão protegidas na BD por `CHECK`, `UNIQUE`, `FK` e triggers — aproveitamos isso em vez de repetir tudo em Java.

## Estado actual

| Membro | Área | Estado |
|---|---|---|
| 1 | Base do projecto, ligação JDBC, login, sessão | **Feito** (branch `autenticacao`) |
| 2 | Tarifas e facturas | Por fazer |
| 3 | Contratos e pagamentos | Por fazer |
| 4 | Clientes e contadores | Por fazer |
| 5 | Funcionários e reclamações | Por fazer |
| 6 | Janela principal (menu), Dashboard, dados de demonstração | Por fazer |

## Ler por esta ordem

1. [`01_PLANO_E_DIVISAO.md`](01_PLANO_E_DIVISAO.md) — o que cada um faz e o que fica de fora.
2. [O teu documento individual](elementos/) — tarefas e como demonstrar.
3. [`02_PADROES_CODIGO.md`](02_PADROES_CODIGO.md) — **como usar o que o Membro 1 já fez** (ligação, sessão, excepções) e um exemplo de DAO.
4. [`07_DADOS_E_REGRAS.md`](07_DADOS_E_REGRAS.md) — nomes das colunas e regras que entram.
5. [`03_CONTRATOS_INTEGRACAO.md`](03_CONTRATOS_INTEGRACAO.md) — os poucos métodos que um módulo precisa do outro.
6. [`04_GIT_GITHUB.md`](04_GIT_GITHUB.md) — branch, commit, Pull Request.
7. [`05_TAREFAS_E_FASES.md`](05_TAREFAS_E_FASES.md), [`06_TESTES_E_CRITERIOS.md`](06_TESTES_E_CRITERIOS.md), [`08_CHECKLIST_ENTREGA.md`](08_CHECKLIST_ENTREGA.md), [`09_DECISOES_PENDENTES.md`](09_DECISOES_PENDENTES.md).

## Regras que ninguém pode ignorar (só estas)

1. **Sem SQL em `JPanel`/`JFrame`/`JDialog`.** SQL só nos DAO.
2. **Sempre `PreparedStatement`** para valores vindos do utilizador.
3. **Ligação sempre por `DatabaseConnection`** (do Membro 1); nada de `DriverManager` noutros sítios.
4. **Dinheiro em `BigDecimal`**, nunca `double`.
5. Não criar a tabela `leitura`: a leitura está em `fatura`.
6. Não alterar o Anexo A nem o `pom.xml` sem avisar o grupo.
7. Não pôr passwords da BD no código nem no Git.
8. Cada um trabalha na sua branch e entra em `main` por Pull Request.

## Primeiro dia de cada membro

1. Ler o seu documento individual.
2. Fazer *pull* de `main` (depois de o Membro 1 integrar a `autenticacao`), configurar a BD local e conseguir fazer login.
3. Criar a sua branch e começar pelo **Model + DAO** da entidade mais simples do seu módulo.

---

<!-- Ficheiro: docs/interno/01_PLANO_E_DIVISAO.md -->

# Divisão da equipa — plano simplificado

Mantêm-se as mesmas áreas por membro (ninguém troca de módulo), mas **cada módulo foi reduzido ao essencial**: listar numa `JTable`, inserir/editar num formulário e, quando existe, **uma** regra de negócio que vale a pena mostrar na apresentação.

| Membro | Área | Nível (novo) | Responsável |
|---|---|---|---|
| 1 | Projecto Maven, `DatabaseConnection`, login, sessão, perfis | — (**feito**) | **[NOME 1]** |
| 2 | Tarifas + emissão de facturas | Médio | **[NOME 2]** |
| 3 | Contratos + pagamentos | Médio | **[NOME 3]** |
| 4 | Clientes + contadores | Fácil/médio | **[NOME 4]** |
| 5 | Funcionários + reclamações | Fácil/médio | **[NOME 5]** |
| 6 | `MainFrame` (menu), Dashboard, dados de demonstração, guia de execução | Fácil | **[NOME 6]** |

## O que cada um demonstra na apresentação

| Membro | Demonstração |
|---|---|
| 1 | Login válido/inválido, ADMIN vs OPERADOR, logout, ligação JDBC central |
| 2 | Listar/criar tarifa; emitir factura e mostrar o cálculo (`leitura → consumo → valor`) |
| 3 | Criar contrato e mudar estado; registar pagamento parcial e ver a factura passar a `paga` |
| 4 | Registar/pesquisar/editar cliente; registar contador num contrato |
| 5 | Registar/editar funcionário (só ADMIN); abrir reclamação e mudar estado até `resolvida` |
| 6 | Navegar entre módulos pelo menu; Dashboard com 4 números reais da BD |

## Fora do âmbito (não fazer)

- Multas automáticas, corte e religação automáticos (RN24–31 só na parte do saldo).
- Editar ou apagar tarifas. Só se **cria** tarifa nova (que fecha automaticamente a vigente do mesmo tipo — ver Membro 2).
- Troca de contador "automática" (desactivar um e activar outro numa transacção). Basta mudar o estado à mão.
- Concorrência (`SELECT ... FOR UPDATE`, testes com duas sessões).
- Anexo C, `log_transacao`, níveis de isolamento.
- Ecrã de gestão de contas de utilizador (contas criadas pelo `CriarAdminInicial`/SQL do Membro 1).
- Branch `develop`, GitHub Projects, Issues obrigatórias.
- `SwingWorker` em todo o lado (só onde já existe, no login).
- Apagar registos (DELETE). Não há botões "Eliminar".
- Testes JUnit para tudo — só para o cálculo da factura (ver [`06_TESTES_E_CRITERIOS.md`](06_TESTES_E_CRITERIOS.md)).

## Estrutura de cada módulo (igual para todos)

Para cada entidade:

```text
model/Cliente.java            atributos + getters/setters (sem Swing, sem SQL)
dao/ClienteDAO.java           listar, buscarPorId, pesquisar, inserir, actualizar
controller/ClienteController  lê o formulário, valida o básico, chama o DAO, actualiza a tabela
view/ClientePanel.java        JTable + campo de pesquisa + botões Novo/Editar
view/ClienteDialog.java       formulário (JDialog modal)
```

Camada `service/` **só** onde há uma regra que envolve mais de uma tabela: `FaturaService` (Membro 2) e `PagamentoService` (Membro 3). Nos restantes, o Controller chama directamente o DAO.

## Ordem de integração

`cliente` → `contrato` → `contador` → `fatura` → `pagamento`. Para não ficarem à espera uns dos outros, **usem os dados de exemplo do Anexo A** (já há clientes, contratos, contadores, tarifas e funcionários) enquanto o módulo do colega não chega a `main`.

---

<!-- Ficheiro: docs/arquitectura.md -->

# Arquitectura da aplicação (MVC + DAO)

## Princípios

Fluxo: **utilizador → View Swing → Controller → (Service) → DAO → JDBC → MySQL**. O Model representa as entidades. A View não tem SQL; o DAO não mostra mensagens Swing. A camada Service só existe onde uma operação envolve várias tabelas (emissão de factura e registo de pagamento).

```text
src/main/java/mz/agua/
├── Main.java
├── model/        Cliente, Contrato, Contador, Tarifa, Fatura,
│                 Pagamento, Reclamacao, Funcionario, Usuario, Perfil
├── dao/          Um DAO por entidade, mais DashboardDAO
├── service/      AutenticacaoService, UtilizadorService, FaturaService, PagamentoService
├── controller/   Recebe eventos da View e chama DAO/Service
├── view/         LoginFrame, MainFrame, *Panel (listas), *Dialog (formulários)
├── config/       DatabaseConnection
├── exception/    ValidacaoException, PersistenciaException, AcessoNegadoException, ...
└── util/         Sessao, PasswordHasher, CriarAdminInicial
```

## Janelas

`LoginFrame` abre primeiro. Depois do login abre o `MainFrame`: menu à esquerda e área central em `CardLayout`, onde cada módulo é um `JPanel`. Formulários em `JDialog` modal; listas em `JTable`; mensagens com `JOptionPane`.

## Ligação à base de dados

- `DatabaseConnection` é o único sítio que abre ligações; as credenciais vêm de `application.properties` local ou de variáveis de ambiente.
- `DatabaseConnection.executar(c -> ...)` abre, executa e fecha. `emTransaccao(c -> ...)` faz `commit` ou `rollback`.
- Cada DAO recebe a `Connection` no construtor; assim, vários DAOs podem participar na mesma transacção.
- Todo o SQL usa `PreparedStatement`.

## Decisões técnicas

1. Esquema: Anexo A de BD II + tabela `utilizador`.
2. `DECIMAL` → `BigDecimal`; `DATE` → `LocalDate`; `ENUM` → `String` escolhida em `JComboBox`.
3. A coluna `contador.activo` é gerada pelo MySQL e nunca é escrita pela aplicação.
4. Regras simples são validadas em Java; `CHECK`, `UNIQUE`, `FK` e triggers da BD são a rede de segurança.
5. Permissões: menus de ADMIN escondidos e `Sessao.exigirAdmin()` no Controller antes de gravar.
6. Sem frameworks ORM.

---

<!-- Ficheiro: docs/casos-de-uso.md -->

# Casos de uso e controlo de acesso

**Actores:** `OPERADOR` e `ADMIN`. O ADMIN pode fazer tudo o que o OPERADOR faz.

| Código | Caso de uso | OPERADOR | ADMIN |
|---|---|:---:|:---:|
| UC01 | Iniciar e terminar sessão | ✓ | ✓ |
| UC02 | Registar, pesquisar e editar clientes | ✓ | ✓ |
| UC03 | Criar contratos e mudar o seu estado | ✓ | ✓ |
| UC04 | Registar contadores e mudar o seu estado | ✓ | ✓ |
| UC05 | Consultar tarifas | ✓ | ✓ |
| UC05-A | Criar nova tarifa | — | ✓ |
| UC06 | Registar leitura e emitir factura | ✓ | ✓ |
| UC07 | Registar pagamentos totais/parciais | ✓ | ✓ |
| UC08 | Abrir, atribuir e resolver reclamações | ✓ | ✓ |
| UC09 | Registar e editar funcionários | — | ✓ |
| UC10 | Consultar Dashboard | ✓ | ✓ |

**Cargo ≠ perfil:** o cargo do funcionário (ex.: `tecnico_leitura`) é do negócio; `ADMIN`/`OPERADOR` é o perfil de acesso à aplicação. Só um técnico de leitura pode constar como responsável pela leitura numa factura, seja qual for o perfil de quem opera o sistema.

## UC06: emitir factura

**Pré-condições:** contrato e contador activos; tarifa vigente na data da leitura; mês ainda não facturado para esse contador.

1. O funcionário escolhe o contrato; o sistema mostra o contador activo e a leitura anterior (última `leitura_atual` do contador, ou `leitura_inicial`).
2. O funcionário indica mês/ano, data da leitura, leitura actual e técnico de leitura.
3. O sistema calcula `consumo = actual - anterior` e `valor_base = max(consumo, consumo_minimo) × preco_por_m3`, e o vencimento (dia 10 do mês seguinte à emissão).
4. O funcionário confirma; a factura fica `pendente`.

**Falhas:** leitura menor que a anterior, sem tarifa vigente, mês repetido ou técnico inválido → mensagem de erro, nenhuma factura gravada.

## UC07: registar pagamento

1. O funcionário escolhe uma factura pendente; o sistema mostra o saldo (`valor_base + valor_multa - total pago`).
2. Indica valor, data, método e número de recibo.
3. Numa transacção, o sistema recusa valores acima do saldo, grava o pagamento (com `registado_por` = funcionário em `dinheiro`/`transferencia`/`cartao`, ou vazio em `mpesa`/`emola`) e marca a factura como `paga` se o saldo chegar a zero.

**Falhas:** valor inválido ou acima do saldo, recibo repetido → nada é gravado.

## Limites do projecto

Sem portal de clientes, sem app móvel, sem integração real com M-Pesa/e-Mola. Fora do âmbito: multas, corte/religação automáticos, estado `vencida`, gestão de contas de utilizador por ecrã, eliminação de registos.

---

<!-- Ficheiro: docs/interno/07_DADOS_E_REGRAS.md -->

# Dicionário curto do Anexo A e regras que entram

**Fontes:** [`database/bdii/Anexo_A_Esquema.sql`](../../database/bdii/Anexo_A_Esquema.sql) e o *Minimundo e Regras de Negócio* de BD II. Usamos **só o Anexo A** (sem Anexo C).

## Tabelas e colunas

| Tabela | Colunas |
|---|---|
| `cliente` | `id_cliente`, `nome_completo`, `nuit` (opcional, único), `telefone`, `rua`, `numero_casa`, `bairro` |
| `funcionario` | `id_funcionario`, `nome`, `telefone`, `cargo` |
| `tarifa` | `id_tarifa`, `tipo_contrato`, `preco_por_m3`, `consumo_minimo`, `data_inicio`, `data_fim` (NULL = vigente) |
| `contrato` | `id_contrato`, `tipo_contrato`, `estado_contrato`, `data_inicio`, `data_fim`, `rua`, `numero_porta`, `quarteirao`, `bairro`, `id_cliente` |
| `contador` | `id_contador`, `numero_contador`, `data_instalacao`, `data_remocao`, `leitura_inicial`, `estado_contador`, `activo` (**gerada pela BD — nunca escrever**), `id_contrato` |
| `fatura` | `id_fatura`, `mes_referencia`, `ano_referencia`, `data_leitura`, `leitura_anterior`, `leitura_atual`, `data_emissao`, `data_vencimento`, `valor_base`, `valor_multa`, `estado_fatura`, `id_contador`, `id_tarifa`, `id_funcionario` |
| `pagamento` | `id_pagamento`, `data_pagamento`, `valor`, `metodo_pagamento`, `numero_recibo` (único), `id_fatura`, `registado_por` |
| `reclamacao` | `id_reclamacao`, `tipo_reclamacao`, `descricao`, `data_abertura`, `data_fecho`, `estado_reclamacao`, `id_cliente`, `id_funcionario` |
| `utilizador` | tabela do login (Membro 1) — os outros não mexem |

`log_auditoria` e `log_transacao` existem mas **ninguém precisa de as usar** (os triggers preenchem a auditoria sozinhos).

## Valores ENUM (copiar exactamente para as `JComboBox`)

- `tipo_contrato`: `residencial`, `comercial`
- `estado_contrato`: `activo`, `suspenso`, `cortado`, `terminado`
- `estado_contador`: `activo`, `inactivo`, `avariado`
- `cargo`: `tecnico_leitura`, `tecnico_manutencao`, `atendimento`, `supervisor`, `administrador`
- `estado_fatura`: `pendente`, `paga`, `vencida`
- `metodo_pagamento`: `dinheiro`, `transferencia`, `mpesa`, `emola`, `cartao`
- `tipo_reclamacao`: `fuga`, `avaria_contador`, `falta_agua`, `qualidade_agua`, `faturacao`, `atendimento`, `outro`
- `estado_reclamacao`: `aberta`, `em_tratamento`, `resolvida`

## Relações

```text
cliente (1) ── (N) contrato (1) ── (N) contador (1) ── (N) fatura (1) ── (N) pagamento
   └── (N) reclamacao                          tarifa (1) ──┘
funcionario: técnico da fatura; responsável da reclamacao; registado_por do pagamento
```

## Regras que entram (e quem já as garante)

| Regra | O quê | Java | BD já garante |
|---|---|:---:|:---:|
| RN01 | NUIT opcional e único | — | ✓ `UNIQUE` |
| RN05 | contrato `terminado` exige `data_fim` | ✓ | ✓ `CHECK` |
| RN09 | um contador activo por contrato | — | ✓ `UNIQUE` |
| RN11/17 | leitura anterior = última `leitura_atual` do contador, senão `leitura_inicial` | ✓ | — |
| RN12 | preço e mínimo > 0 | ✓ | ✓ `CHECK` |
| RN13 | tarifas do mesmo tipo sem sobreposição | fechar a vigente | ✓ trigger |
| RN15 | só factura contrato/contador activos | ✓ | — |
| RN16 | uma factura por contador e mês | — | ✓ `UNIQUE` |
| RN18 | leitura actual ≥ anterior | ✓ | ✓ `CHECK` |
| RN19 | leitura feita por `tecnico_leitura` | combo só com técnicos | ✓ trigger |
| RN20 | `valor_base = max(consumo, mínimo) × preço` | ✓ | — |
| RN22 | vencimento dia 10 do mês seguinte à emissão | ✓ | — |
| RN24 | não pagar acima do saldo | ✓ | — |
| RN34/35 | reclamação: `resolvida` ⇔ `data_fecho`; `em_tratamento` exige responsável | ✓ | ✓ `CHECK` |
| RN38 | `registado_por` NULL em `mpesa`/`emola`, preenchido nos outros | ✓ | ✓ `CHECK` |

**Ficam de fora:** multas (RN25–28), corte/religação automáticos (RN29–31), log de transacções (RN41–42).

## Cálculos

```text
consumo         = leitura_atual - leitura_anterior
consumo_cobrado = MAX(consumo, consumo_minimo)
valor_base      = consumo_cobrado * preco_por_m3          (BigDecimal, 2 casas)
total_pago      = SUM(pagamento.valor) da factura
saldo           = valor_base + valor_multa - total_pago   (valor_multa fica sempre 0)
```

---

<!-- Ficheiro: docs/interno/02_PADROES_CODIGO.md -->

# Convenções técnicas (simplificadas)

## Nomes e pacotes

- Package raiz: `mz.agua`. Pastas: `model`, `dao`, `service` (só Membros 2 e 3), `controller`, `view`.
- Classes: `Cliente`, `ClienteDAO`, `ClienteController`, `ClientePanel`, `ClienteDialog`.
- Métodos/campos em `camelCase` (`idCliente`, `precoPorM3`); colunas SQL iguais ao Anexo A (`id_cliente`).
- Nomes de classes sem acentos; textos mostrados ao utilizador podem ter acentos.

## Tipos

| SQL | Java |
|---|---|
| `INT` | `int` (ou `Integer` se puder ser `NULL`) |
| `VARCHAR`, `TEXT`, `ENUM` | `String` (os ENUM escolhem-se numa `JComboBox` com os valores exactos) |
| `DECIMAL` | `BigDecimal` |
| `DATE` | `LocalDate` (`rs.getObject("data_inicio", LocalDate.class)` / `ps.setObject(i, data)`) |

**Decisão:** ENUM do SQL ficam como `String` em Java — não criar enums Java (excepto `Perfil`, que já existe). A `JComboBox` garante que só se escolhem valores válidos.

## O que o Membro 1 já deixou pronto (usar, não refazer)

| Classe | Para quê |
|---|---|
| `config.DatabaseConnection` | `executar(c -> ...)` para leituras/escritas simples; `emTransaccao(c -> ...)` para operações com commit/rollback |
| `util.Sessao` | `Sessao.isAdmin()`, `Sessao.exigirAdmin()`, `Sessao.getIdFuncionarioAutenticado()` |
| `exception.ValidacaoException` | erro de regra/validação com mensagem para o utilizador |
| `exception.PersistenciaException` | erro de BD (já traz mensagem amigável) |
| `exception.AcessoNegadoException` | operação sem permissão |
| `Main.terminarSessaoEVoltarAoLogin()` | logout (o `MainFrame` chama isto) |

Configuração local: ver [guia de execução](../guia-de-execucao.md). Cada um tem o seu `application.properties` (não vai para o Git).

## Exemplo completo de DAO (copiar este estilo)

O DAO recebe a `Connection` no construtor (igual ao `UsuarioDAO`) e **não** a abre nem fecha.

```java
public class ClienteDAO {
    private final Connection ligacao;

    public ClienteDAO(Connection ligacao) { this.ligacao = ligacao; }

    public List<Cliente> pesquisar(String termo) throws SQLException {
        String sql = "SELECT * FROM cliente WHERE nome_completo LIKE ? ORDER BY nome_completo";
        try (PreparedStatement ps = ligacao.prepareStatement(sql)) {
            ps.setString(1, "%" + termo + "%");
            try (ResultSet rs = ps.executeQuery()) {
                List<Cliente> lista = new ArrayList<>();
                while (rs.next()) {
                    lista.add(mapear(rs));
                }
                return lista;
            }
        }
    }

    public void inserir(Cliente c) throws SQLException {
        String sql = "INSERT INTO cliente (nome_completo, nuit, telefone, rua, numero_casa, bairro) VALUES (?,?,?,?,?,?)";
        try (PreparedStatement ps = ligacao.prepareStatement(sql)) {
            ps.setString(1, c.getNomeCompleto());
            ps.setString(2, c.getNuit());          // pode ser null
            // ...
            ps.executeUpdate();
        }
    }

    private Cliente mapear(ResultSet rs) throws SQLException { /* rs.getInt("id_cliente"), ... */ }
}
```

## Exemplo de Controller

```java
// carregar a tabela
List<Cliente> clientes = DatabaseConnection.executar(c -> new ClienteDAO(c).pesquisar(termo));
painel.mostrarClientes(clientes);

// guardar
try {
    validar(cliente);                                  // ifs simples -> throw new ValidacaoException("...")
    DatabaseConnection.executar(c -> { new ClienteDAO(c).inserir(cliente); return null; });
    JOptionPane.showMessageDialog(painel, "Cliente registado.");
} catch (ValidacaoException | PersistenciaException | AcessoNegadoException e) {
    JOptionPane.showMessageDialog(painel, e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
}
```

## Validações: Java primeiro, BD como rede de segurança

- No Java: campos obrigatórios, números positivos, datas coerentes e a regra principal do módulo.
- A BD já tem `CHECK`/`UNIQUE`/triggers (ex.: NUIT único, um contador activo por contrato, técnico de leitura, sobreposição de tarifas). Se algo escapar, a BD recusa e o utilizador vê a mensagem da `PersistenciaException`. **Não é preciso repetir todas as regras em Java.**

## Swing

- Uma só janela principal (`MainFrame`, Membro 6) com `CardLayout`. Cada módulo entrega um `JPanel`.
- Formulários em `JDialog` modal. Listas em `JTable` dentro de `JScrollPane`.
- Mensagens com `JOptionPane`.
- Botões só de ADMIN: esconder com `Sessao.isAdmin()` **e** chamar `Sessao.exigirAdmin()` no Controller antes de gravar (uma linha).

## Git

Commits pequenos e com mensagem clara: `feat(cliente): pesquisar por nome`, `fix(fatura): vencimento no dia 10`.

---

<!-- Ficheiro: docs/interno/03_CONTRATOS_INTEGRACAO.md -->

# O que cada módulo precisa dos outros (versão curta)

Regra geral: **se precisas de dados de outra tabela só para preencher uma `JComboBox`, pede ao dono o método abaixo.** Se o método ainda não estiver em `main`, escreve temporariamente a consulta no teu DAO e troca-a quando o colega integrar — não fiques à espera.

Todos os DAO recebem a `Connection` no construtor (ver [`02_PADROES_CODIGO.md`](02_PADROES_CODIGO.md)).

| Dono | Método | Quem usa |
|---|---|---|
| Membro 4 | `ClienteDAO.listar()` / `buscarPorId(int)` | 3 (contrato), 5 (reclamação) |
| Membro 3 | `ContratoDAO.listarActivos()` / `buscarPorId(int)` | 2 (factura), 4 (contador) |
| Membro 4 | `ContadorDAO.buscarActivoPorContrato(int idContrato)` | 2 (factura) |
| Membro 5 | `FuncionarioDAO.listarPorCargo(String cargo)` | 2 (técnicos `tecnico_leitura`), 5 |
| Membro 2 | `TarifaDAO.buscarVigente(String tipoContrato, LocalDate data)` | 2 |
| Membro 2 | `FaturaDAO.listarPendentes()` / `buscarPorId(int)` | 3 (pagamento) |
| Membro 3 | `PagamentoDAO.totalPago(int idFatura)` | 3 |
| Membro 6 | `MainFrame.adicionarModulo(String nome, JPanel painel, boolean soAdmin)` | todos |
| Membro 1 | `Sessao.isAdmin()`, `Sessao.exigirAdmin()`, `Sessao.getIdFuncionarioAutenticado()` | todos |

Os métodos devolvem `List<Modelo>` ou o `Modelo` (ou `null`/`Optional` se não existir — o dono escolhe e documenta no Javadoc).

## Três factos que evitam confusões

- **Tarifa vigente:** `tipo_contrato = ?` e `data_inicio <= data` e `(data_fim IS NULL OR data_fim >= data)`.
- **Saldo de uma factura:** `valor_base + valor_multa - SUM(pagamento.valor)`. Não existe coluna `saldo` nem `valor_pago` no Anexo A.
- **Cargo ≠ perfil:** `funcionario.cargo` (ex.: `tecnico_leitura`) é do negócio; `ADMIN`/`OPERADOR` é do login.

---

<!-- Ficheiro: docs/interno/04_GIT_GITHUB.md -->

# GitHub — fluxo simples

## Branches

- `main`: versão que funciona. Ninguém faz commit directo.
- Uma branch por membro: `autenticacao` (Membro 1, já existe), `facturacao`, `contratos-pagamentos`, `clientes-contadores`, `funcionarios-reclamacoes`, `interface-dashboard`.

Sem `develop`, sem branches por Issue. Issues e GitHub Projects são opcionais.

## Ciclo de trabalho

```bash
git checkout main
git pull
git checkout -b clientes-contadores      # só da primeira vez
# ... trabalhar ...
git status                               # confirmar que não vai application.properties nem target/
git add src/...
git commit -m "feat(cliente): listar clientes na JTable"
git push -u origin clientes-contadores
```

Quando uma parte funcionar (ex.: clientes completos), abrir **Pull Request para `main`**, pedir a **um** colega para rever e fazer *merge* quando compilar e funcionar.

Para apanhar o trabalho dos outros enquanto continuas na tua branch:

```bash
git checkout main && git pull
git checkout clientes-contadores
git merge main
```

## Regras

- Não fazer `push --force` em `main`.
- Não versionar `application.properties`, `target/`, dumps da BD.
- Avisar o grupo antes de mexer no `pom.xml`, no SQL ou em ficheiros de outro membro.
- Cada membro faz os seus próprios commits; não pôr nomes de pessoas ou ferramentas como autor/co-autor de trabalho que não fizeram.

---

<!-- Ficheiro: docs/interno/05_TAREFAS_E_FASES.md -->

# Tarefas e fases

## Fase 0 — arranque (todos, meio dia)

- [ ] Membro 1 integra a branch `autenticacao` em `main`, com `database/02_autenticacao.sql` e `application.properties.example` (ver [`elementos/01_ARQUITECTURA_AUTENTICACAO.md`](elementos/01_ARQUITECTURA_AUTENTICACAO.md)).
- [ ] Cada membro: importa o Anexo A + `02_autenticacao.sql`, cria o admin inicial e consegue fazer login.
- [ ] Membro 6 publica cedo um `MainFrame` vazio com o método `adicionarModulo(...)`, para os outros encaixarem os painéis.
- [ ] Preencher os nomes em [`01_PLANO_E_DIVISAO.md`](01_PLANO_E_DIVISAO.md).

## Fase 1 — o CRUD simples de cada um (em paralelo)

- [ ] Membro 2: `Tarifa` (listar + criar).
- [ ] Membro 3: `Contrato` (listar, criar, mudar estado).
- [ ] Membro 4: `Cliente` (listar, pesquisar, criar, editar).
- [ ] Membro 5: `Funcionario` (listar, criar, editar; só ADMIN).
- [ ] Membro 6: `MainFrame` com menu + Dashboard com 4 contagens.

Ao terminar: Pull Request para `main`.

## Fase 2 — a segunda entidade de cada um

- [ ] Membro 2: emitir `Fatura` + listar facturas.
- [ ] Membro 3: registar `Pagamento` + ver saldo.
- [ ] Membro 4: registar `Contador` num contrato + mudar estado.
- [ ] Membro 5: `Reclamacao` (abrir, atribuir responsável, resolver).
- [ ] Membro 6: script de dados de demonstração + guia de execução testado.

Ao terminar: Pull Request para `main`.

## Fase 3 — integração e ensaio (todos)

- [ ] Percorrer o fluxo: login → cliente → contrato → contador → factura → pagamento → Dashboard.
- [ ] Correr os testes manuais de [`06_TESTES_E_CRITERIOS.md`](06_TESTES_E_CRITERIOS.md).
- [ ] `mvn clean test` passa em `main`.
- [ ] Cada membro ensaia a sua demonstração (2–3 minutos).

Não há durações fixas: uma fase pode começar antes de outra acabar. Usem os dados de exemplo do Anexo A para não ficarem à espera dos colegas.

---

<!-- Ficheiro: docs/interno/06_TESTES_E_CRITERIOS.md -->

# Testes e definição de concluído

## Uma tarefa está concluída quando...

- [ ] Compila e a aplicação abre.
- [ ] Os dados vêm da BD por JDBC (nada de listas inventadas no código).
- [ ] A `JTable` actualiza depois de inserir/editar.
- [ ] Um caso de sucesso e um caso de erro foram testados à mão.
- [ ] O Pull Request diz como testar.

## Testes automáticos (JUnit)

Só onde há cálculo, para não gastar tempo:

- Membro 2: cálculo do consumo, do valor-base (com consumo mínimo) e da data de vencimento.
- Membro 1: já tem testes de `PasswordHasher`, `Sessao`, `Perfil` e `UtilizadorService`.

Os outros módulos testam-se manualmente com a tabela abaixo.

## Testes manuais (para fazer antes da apresentação)

| ID | Módulo | Teste | Esperado |
|---|---|---|---|
| T01 | Login | password errada / certa | erro / entra |
| T02 | Perfil | OPERADOR abre o menu | não vê Funcionários nem "Nova tarifa" |
| T03 | Cliente | NUIT repetido | recusado, cliente não duplicado |
| T04 | Contrato | passar a `terminado` sem data de fim | recusado |
| T05 | Contador | segundo contador activo no mesmo contrato | recusado |
| T06 | Tarifa | nova tarifa com início anterior ao da vigente | recusada |
| T06b | Tarifa | nova tarifa válida | a anterior fica com `data_fim` = véspera do início da nova |
| T07 | Factura | leitura actual menor que a anterior | não emitida |
| T08 | Factura | consumo abaixo do mínimo | cobra o consumo mínimo |
| T09 | Factura | segunda factura no mesmo mês para o mesmo contador | recusada |
| T10 | Pagamento | valor maior que o saldo | recusado |
| T11 | Pagamento | pagamento que liquida o saldo | factura passa a `paga` |
| T12 | Reclamação | `resolvida` | data de fecho preenchida |
| T13 | Dashboard | inserir um cliente e voltar ao Dashboard | número aumenta |

**Exemplo de cálculo para T08:** leitura anterior 120, actual 123, mínimo 5, preço 45 → consumo 3, cobra 5 → `valor_base = 225.00 MT`.

## Dados de teste

O Anexo A já traz dados de exemplo e faz `DROP DATABASE` ao ser executado: usar só numa BD de desenvolvimento. Para recomeçar do zero, basta voltar a correr o Anexo A + `02_autenticacao.sql` + script de demonstração.

---

<!-- Ficheiro: docs/interno/elementos/01_ARQUITECTURA_AUTENTICACAO.md -->

# Membro 1 — Base do projecto, JDBC e autenticação

**Estado:** feito (branch `autenticacao`)  
**Responsável:** [NOME]

## O que já existe

```text
pom.xml (JDK 21, driver MariaDB, bcrypt, JUnit 5)
Main.java                        arranque, login, logout (terminarSessaoEVoltarAoLogin)
config/DatabaseConnection.java   getConnection, executar, emTransaccao, testarLigacao
model/Usuario.java, Perfil.java
dao/UsuarioDAO.java
service/AutenticacaoService.java, UtilizadorService.java
controller/LoginController.java
view/LoginFrame.java
util/Sessao.java, PasswordHasher.java, CriarAdminInicial.java
exception/ValidacaoException, PersistenciaException, AcessoNegadoException, AutenticacaoException
testes: PerfilTest, PasswordHasherTest, SessaoTest, UtilizadorServiceTest
```

## Falta para os outros conseguirem arrancar

O código refere ficheiros que **não estão no repositório**. Sem eles ninguém consegue fazer login:

- [ ] `database/02_autenticacao.sql` — `CREATE TABLE utilizador (...)` usado pelo `UsuarioDAO`.
- [ ] `application.properties.example` — modelo com `db.url`, `db.user`, `db.password` (sem valores reais).
- [ ] Instruções no [guia de execução](../../guia-de-execucao.md): como correr `CriarAdminInicial` para criar o primeiro ADMIN.
- [ ] Apagar `pom21312.xml` se for um ficheiro de teste esquecido.
- [ ] Abrir Pull Request `autenticacao` → `main`.

## Depois disso

Apoiar os colegas com dúvidas de JDBC/`DatabaseConnection` e rever Pull Requests que toquem em `pom.xml` ou SQL. Não é preciso fazer mais funcionalidades.

## Demonstração

Login válido e inválido; ADMIN vê menus que o OPERADOR não vê; logout; explicar `DatabaseConnection` (ligação nova por operação, credenciais fora do código) e o hash da password.

---

<!-- Ficheiro: docs/interno/elementos/02_TARIFAS_FACTURACAO.md -->

# Membro 2 — Tarifas e facturas

**Nível:** médio  
**Responsável:** [NOME]  
**Branch:** `facturacao`  
**Estado:** código feito e testes unitários a passar; falta testar com a BD (testes manuais T06–T09).

## Resultado

Ver e criar tarifas; emitir uma factura a partir de uma leitura, com o valor calculado correctamente; listar facturas.

## Classes

```text
model/Tarifa.java, model/Fatura.java
dao/TarifaDAO.java, dao/FaturaDAO.java
service/FaturaService.java            cálculos + emissão
controller/TarifaController.java, controller/FaturaController.java
view/TarifaPanel.java, view/TarifaDialog.java
view/FaturaPanel.java, view/EmissaoFaturaDialog.java
test/.../FaturaServiceTest.java
util/Formatos.java                    ler/mostrar datas dd/mm/aaaa e números dos formulários (pode ser usado por todos)
test/.../view/ExperimentarFacturacao  TEMPORÁRIO: abre Tarifas e Facturas sem MainFrame; apagar depois da integração
```

## Tarefas

### Tarifas

- [x] `TarifaPanel`: `JTable` com todas as tarifas (tipo, preço, mínimo, início, fim).
- [x] Botão "Nova tarifa" (só ADMIN): tipo, preço, mínimo, data de início.
- [x] Ao gravar, numa transacção (`DatabaseConnection.emTransaccao`): fechar a tarifa vigente do mesmo tipo (`data_fim = início_nova - 1 dia`) e inserir a nova. Recusar se a data de início não for posterior à da vigente.
  - Porquê: o trigger do Anexo A recusa qualquer tarifa nova enquanto a vigente tiver `data_fim = NULL`.
- [x] `TarifaDAO.buscarVigente(tipoContrato, data)`.

### Facturas

- [x] `EmissaoFaturaDialog`: escolher contrato activo → mostrar contador activo e **leitura anterior** → escrever mês/ano, data da leitura, leitura actual → escolher técnico (`tecnico_leitura`) → botão "Calcular" mostra consumo e valor → "Emitir".
- [x] `FaturaService`:
  - leitura anterior = última `leitura_atual` do contador (`ORDER BY ano_referencia DESC, mes_referencia DESC LIMIT 1`), senão `leitura_inicial`;
  - `valor_base = max(actual - anterior, consumo_minimo) × preco_por_m3` (`BigDecimal`, 2 casas);
  - `data_emissao` = hoje; `data_vencimento` = dia 10 do mês seguinte à emissão;
  - validar: leitura actual ≥ anterior; existe tarifa vigente para o tipo do contrato na data da leitura.
- [x] `FaturaDAO.inserir(...)` com `estado_fatura = 'pendente'`, `valor_multa = 0`.
- [x] `FaturaPanel`: `JTable` com facturas (nº, cliente, contador, mês/ano, valor, estado).
- [x] `FaturaDAO.listarPendentes()` e `buscarPorId(int)` para o Membro 3.
- [x] JUnit: consumo normal, consumo abaixo do mínimo, leitura decrescente, vencimento (incluindo emissão em Dezembro → 10 de Janeiro).

A BD já recusa: factura duplicada no mesmo mês, técnico que não é `tecnico_leitura`, leitura decrescente.

## Precisa de

Membro 3: `ContratoDAO.listarActivos()`; Membro 4: `ContadorDAO.buscarActivoPorContrato()`; Membro 5: `FuncionarioDAO.listarPorCargo("tecnico_leitura")`. Até estarem em `main`, o `FaturaDAO` tem consultas próprias temporárias (secção "TEMPORÁRIO") sobre os dados do Anexo A.

## Demonstração

Criar uma tarifa nova e mostrar que a anterior foi fechada; emitir factura com leitura 120 → 123 (mínimo 5, preço 45) e mostrar `225.00 MT`; tentar leitura menor que a anterior.

**Fora do âmbito:** editar tarifas, multas, estado `vencida`, simulação de várias tarifas por período.

---

<!-- Ficheiro: docs/interno/elementos/03_CONTRATOS_PAGAMENTOS.md -->

# Membro 3 — Contratos e pagamentos

**Nível:** médio  
**Responsável:** [NOME]  
**Branch:** `contratos-pagamentos`

## Resultado

Criar contratos e mudar o seu estado; registar pagamentos (totais ou parciais) de facturas, sem ultrapassar o saldo.

## Classes

```text
model/Contrato.java, model/Pagamento.java
dao/ContratoDAO.java, dao/PagamentoDAO.java
service/PagamentoService.java
controller/ContratoController.java, controller/PagamentoController.java
view/ContratoPanel.java, view/ContratoDialog.java
view/PagamentoPanel.java, view/PagamentoDialog.java
```

## Tarefas

### Contratos

- [ ] `ContratoPanel`: `JTable` (nº, cliente, tipo, estado, início, bairro) + filtro por estado.
- [ ] `ContratoDialog` "Novo": escolher cliente (combo), tipo, data de início, morada de fornecimento (rua, nº porta, quarteirão, bairro). Estado inicial `activo`.
- [ ] Botão "Mudar estado": combo `activo`/`suspenso`/`cortado`/`terminado`. Se `terminado`, pedir data de fim. Contrato já `terminado` não muda mais.
- [ ] `ContratoDAO.listarActivos()` e `buscarPorId(int)` para os Membros 2 e 4.

### Pagamentos

- [ ] `PagamentoPanel`: lista de facturas pendentes (do `FaturaDAO` do Membro 2) com valor, total pago e saldo; por baixo, os pagamentos da factura seleccionada.
- [ ] `PagamentoDialog`: valor, data, método (combo), número de recibo.
- [ ] `PagamentoService.registar(...)` dentro de `DatabaseConnection.emTransaccao`:
  1. calcular saldo = `valor_base + valor_multa - SUM(pagamento.valor)`;
  2. recusar se valor ≤ 0 ou valor > saldo;
  3. `registado_por` = `Sessao.getIdFuncionarioAutenticado()` para `dinheiro`/`transferencia`/`cartao`, `NULL` para `mpesa`/`emola`;
  4. inserir pagamento;
  5. se o saldo ficar 0, `UPDATE fatura SET estado_fatura = 'paga'`.
- [ ] `PagamentoDAO.totalPago(int idFatura)`.

A BD já recusa: recibo repetido, valor ≤ 0, `registado_por` errado para o método.

## Precisa de

Membro 4: `ClienteDAO.listar()`; Membro 2: `FaturaDAO.listarPendentes()`. Até estarem em `main`, usar consultas próprias temporárias.

## Demonstração

Criar contrato para um cliente; tentar terminar sem data de fim; pagar parte de uma factura, depois o resto, e ver a factura passar a `paga`; tentar pagar acima do saldo.

**Fora do âmbito:** `SELECT ... FOR UPDATE`/concorrência, multas, corte/religação automáticos, alterar/apagar pagamentos.

---

<!-- Ficheiro: docs/interno/elementos/04_CLIENTES_CONTADORES.md -->

# Membro 4 — Clientes e contadores

**Nível:** fácil/médio  
**Responsável:** [NOME]  
**Branch:** `clientes-contadores`

## Resultado

Registar, pesquisar e editar clientes; registar contadores nos contratos e mudar o seu estado.

## Classes

```text
model/Cliente.java, model/Contador.java
dao/ClienteDAO.java, dao/ContadorDAO.java
controller/ClienteController.java, controller/ContadorController.java
view/ClientePanel.java, view/ClienteDialog.java
view/ContadorPanel.java, view/ContadorDialog.java
```

## Tarefas

### Clientes

- [ ] `ClientePanel`: `JTable` + campo de pesquisa por nome (ou NUIT) + botões Novo/Editar.
- [ ] `ClienteDialog`: nome completo, NUIT (opcional, 9 dígitos), telefone (≥ 8 caracteres), rua, nº casa, bairro.
- [ ] Validar obrigatórios no Java. NUIT duplicado: a BD recusa (`UNIQUE`); mostrar a mensagem.
- [ ] `ClienteDAO`: `listar()`, `pesquisar(termo)`, `buscarPorId(int)`, `inserir`, `actualizar`.

### Contadores

- [ ] `ContadorPanel`: escolher contrato (combo) → `JTable` com os contadores desse contrato (nº série, instalação, leitura inicial, estado).
- [ ] `ContadorDialog` "Novo": nº de série, data de instalação, leitura inicial (≥ 0).
- [ ] Botão "Mudar estado": `activo`/`inactivo`/`avariado`; ao sair de `activo`, pedir data de remoção.
- [ ] `ContadorDAO.buscarActivoPorContrato(int idContrato)` para o Membro 2.
- [ ] Nunca escrever a coluna `activo` (é gerada pela BD).

A BD já recusa: nº de série repetido, segundo contador activo no mesmo contrato, leitura inicial negativa.

## Precisa de

Membro 3: `ContratoDAO.listarActivos()` (até lá, consulta temporária).

## Demonstração

Registar e pesquisar cliente; tentar NUIT repetido; registar contador; tentar um segundo contador activo no mesmo contrato; trocar de contador (pôr o antigo `inactivo` e registar o novo).

**Fora do âmbito:** apagar clientes, troca automática de contador numa transacção, histórico de moradas.

---

<!-- Ficheiro: docs/interno/elementos/05_FUNCIONARIOS_RECLAMACOES.md -->

# Membro 5 — Funcionários e reclamações

**Nível:** fácil/médio  
**Responsável:** [NOME]  
**Branch:** `funcionarios-reclamacoes`

## Resultado

O ADMIN regista e edita funcionários; qualquer funcionário abre reclamações de clientes e acompanha-as até `resolvida`.

## Classes

```text
model/Funcionario.java, model/Reclamacao.java
dao/FuncionarioDAO.java, dao/ReclamacaoDAO.java
controller/FuncionarioController.java, controller/ReclamacaoController.java
view/FuncionarioPanel.java, view/FuncionarioDialog.java
view/ReclamacaoPanel.java, view/ReclamacaoDialog.java
```

## Tarefas

### Funcionários (só ADMIN)

- [ ] `FuncionarioPanel`: `JTable` (nome, telefone, cargo) + Novo/Editar.
- [ ] `FuncionarioDialog`: nome, telefone (opcional), cargo (combo com os 5 valores).
- [ ] `Sessao.exigirAdmin()` no Controller antes de gravar.
- [ ] `FuncionarioDAO.listarPorCargo(String cargo)` para o Membro 2 (técnicos de leitura).

### Reclamações

- [ ] `ReclamacaoPanel`: `JTable` (nº, cliente, tipo, estado, abertura, responsável) + filtro por estado.
- [ ] `ReclamacaoDialog` "Nova": cliente (combo), tipo (combo), descrição. Data de abertura = hoje, estado `aberta`.
- [ ] Botão "Atribuir": escolher funcionário responsável → estado `em_tratamento`.
- [ ] Botão "Resolver": estado `resolvida`, `data_fecho` = hoje.
- [ ] Validar: `em_tratamento` exige responsável; `resolvida` exige data de fecho.

A BD já recusa as mesmas combinações inválidas (`CHECK`).

## Precisa de

Membro 4: `ClienteDAO.listar()` (até lá, consulta temporária).

## Demonstração

Entrar como ADMIN e registar funcionário; mostrar que o OPERADOR não vê o menu; abrir reclamação, atribuir e resolver.

**Fora do âmbito:** apagar funcionários, criar contas de login, pesquisa avançada.

---

<!-- Ficheiro: docs/interno/elementos/06_MAINFRAME_DASHBOARD_APOIO.md -->

# Membro 6 — Janela principal, Dashboard e demonstração

**Nível:** fácil  
**Responsável:** [NOME]  
**Branch:** `interface-dashboard`

## Resultado

Uma janela principal com menu para todos os módulos, um Dashboard com números reais e uma base de dados de demonstração pronta a usar.

## Classes / ficheiros

```text
view/MainFrame.java
view/DashboardPanel.java
dao/DashboardDAO.java
database/03_dados_demo.sql
docs/guia-de-execucao.md (actualizar)
```

## Tarefas

- [ ] **Primeiro e cedo:** `MainFrame` com menu lateral (botões) + área central em `CardLayout`, e o método `adicionarModulo(String nome, JPanel painel, boolean soAdmin)`. Publicar em `main` mesmo com painéis vazios, para os colegas encaixarem os seus.
- [ ] Topo da janela: nome do funcionário e perfil (`Sessao.getUsuarioAutenticado()`) + botão "Sair" que fecha a janela e chama `Main.terminarSessaoEVoltarAoLogin()`.
- [ ] Ligar o login ao `MainFrame`: em `Main.aoAutenticar` trocar o `JOptionPane` temporário (há um `TODO`) por `new MainFrame().setVisible(true)` — combinar com o Membro 1.
- [ ] Esconder módulos `soAdmin` quando `!Sessao.isAdmin()`.
- [ ] `DashboardPanel` com 4 cartões: total de clientes, contratos activos, facturas pendentes, reclamações abertas. Botão "Actualizar". Consultas `SELECT COUNT(*)` no `DashboardDAO`.
- [ ] `03_dados_demo.sql`: alguns clientes, contratos, contadores e funcionários a mais (fictícios) para a apresentação.
- [ ] Actualizar o guia de execução com os passos realmente testados (BD → config → admin → `mvn` → login).
- [ ] Na Fase 3, correr os testes manuais T01–T13 e avisar os colegas dos problemas.

## Demonstração

Login → MainFrame; navegar pelos módulos; ADMIN vs OPERADOR; inserir um cliente e ver o Dashboard mudar; logout.

**Fora do âmbito:** gráficos, temas configuráveis, componentes Swing personalizados para os outros módulos.

---

<!-- Ficheiro: docs/interno/09_DECISOES_PENDENTES.md -->

# Decisões

Com a simplificação, as decisões que estavam pendentes ficam **fechadas** assim (o grupo pode mudar, mas avisa todos):

| Decisão | Fica |
|---|---|
| Variante de BD | **Anexo A puro** + `utilizador` (sem Anexo C) |
| JDK / build | JDK 21, Maven (já no `pom.xml` do Membro 1) |
| Base de dados | MySQL 8+ ou MariaDB (driver MariaDB já incluído) |
| Ligação JDBC | `DatabaseConnection.executar` / `emTransaccao`; DAO recebe `Connection` no construtor |
| Erros | `ValidacaoException` (regras), `PersistenciaException` (BD), `AcessoNegadoException` (permissões) |
| ENUM do SQL em Java | `String` + `JComboBox` com os valores exactos (sem enums Java novos) |
| Camada Service | só `FaturaService` e `PagamentoService` |
| Primeiro contrato de um cliente | duas acções separadas: registar cliente (Membro 4), depois criar contrato escolhendo o cliente (Membro 3) |
| Técnico da factura | escolhido numa combo com funcionários `tecnico_leitura` (pode não ser quem está logado) |
| Multas, corte, religação | **fora do âmbito**; `valor_multa` = 0 |
| Estado `vencida` da factura | fora do âmbito (só `pendente` → `paga`) |
| Contas de utilizador | criadas pelo `CriarAdminInicial`/SQL; sem ecrã próprio |
| `log_transacao` | não usado |
| Estilo visual | o do `LoginFrame` serve de referência; Membro 6 dá cores/tamanhos comuns |
| Branches | `main` + uma branch por membro; PR com 1 revisor |

## Ainda por confirmar

- [ ] Nomes dos responsáveis em [`01_PLANO_E_DIVISAO.md`](01_PLANO_E_DIVISAO.md).
- [ ] Data do ensaio geral.

---

<!-- Ficheiro: docs/interno/08_CHECKLIST_ENTREGA.md -->

# Checklist final

## Antes da entrega

- [ ] `main` compila (`mvn clean test`) e a aplicação abre.
- [ ] Guia de execução testado numa máquina de outro membro (BD do zero até ao login).
- [ ] O fluxo login → cliente → contrato → contador → factura → pagamento → Dashboard funciona.
- [ ] Testes manuais T01–T13 feitos ([`06_TESTES_E_CRITERIOS.md`](06_TESTES_E_CRITERIOS.md)).
- [ ] Não há SQL nas Views; não há passwords no código nem no Git.
- [ ] README e `docs/` descrevem o que foi realmente feito.
- [ ] Dados de demonstração fictícios.

## Apresentação (cada membro, 2–3 minutos)

1. Mostrar a sua funcionalidade na aplicação (um caso que funciona + um erro controlado).
2. Abrir o código e explicar o caminho **View → Controller → DAO → JDBC → MySQL** com um exemplo.
3. Explicar porque usa `PreparedStatement`.
4. Membros 2 e 3: explicar o cálculo/regra e a transacção (`DatabaseConnection.emTransaccao`).

## Limpeza (opcional)

`docs/interno/` pode ser removida no fim (`git rm -r docs/interno`). Isto não apaga o histórico do Git, por isso nunca versionar dados sensíveis.

## Autoria

Cada membro é responsável pelos próprios commits. Não inserir nomes de pessoas ou ferramentas como autor/co-autor sem contribuição real, nem marcas de ferramentas no código ou na documentação final.
