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
