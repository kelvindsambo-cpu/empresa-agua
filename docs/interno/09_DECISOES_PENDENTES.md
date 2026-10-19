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
