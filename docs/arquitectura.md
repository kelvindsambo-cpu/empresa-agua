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
