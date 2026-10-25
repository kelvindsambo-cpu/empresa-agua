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
