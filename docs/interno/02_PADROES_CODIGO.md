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
