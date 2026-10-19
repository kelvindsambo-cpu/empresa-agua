# Base de dados: origem, versões e cuidados

## Fontes oficiais do negócio

Os anexos do projecto de BD II estão em [`bdii/`](bdii/), **sem alterações** (não editar):

- [`bdii/Anexo_A_Esquema.sql`](bdii/Anexo_A_Esquema.sql) — **esquema físico de referência**, com tabelas, constraints, triggers e dados de exemplo.
- [`bdii/Anexo_B_Transacoes.sql`](bdii/Anexo_B_Transacoes.sql) — transacções T1–T12 (referência para os Services e DAOs).
- [`bdii/Anexo_C_Otimizacao.sql`](bdii/Anexo_C_Otimizacao.sql) — **alterações opcionais** de optimização do projecto de BD II.

As regras **RN01–RN42** constam do documento *Minimundo e Regras de Negócio* de BD II; o resumo do que interessa à implementação está em [`docs/interno/07_DADOS_E_REGRAS.md`](../docs/interno/07_DADOS_E_REGRAS.md).

## Escolha de versão para POO II

**Proposta inicial de desenvolvimento:** usar o **Anexo A sem aplicar o Anexo C**, até a equipa congelar a versão do esquema. Todos os membros devem apontar para a mesma variante. Não aplicar optimização parcialmente.

Diferenças relevantes:

| Conceito | Só Anexo A | Anexo A + C |
|---|---|---|
| `fatura.valor_pago` | Não existe | Coluna armazenada, mantida por trigger |
| `fatura.consumo_m3` | Calculado na consulta/Java | Coluna gerada VIRTUAL |
| `fatura.valor_total` | Calculado | Coluna gerada VIRTUAL |
| `fatura.saldo_em_divida` | Calculado com soma de pagamentos | Coluna gerada VIRTUAL |
| Índices/partições adicionais | Não aplicados | Incluídos no Anexo C |

**ATENÇÃO:** se a equipa optar por Anexo A + C, rever **todas** as consultas SQL que lêem saldo, as transacções de pagamento e os testes. Não inserir/actualizar colunas geradas. Nenhum módulo pode decidir individualmente aplicar o Anexo C.

## Scripts recomendados para o repositório

```text
database/
  01_esquema_bdii.sql     # cópia revista de Anexo_A_Esquema.sql
  02_autenticacao.sql     # extensão nova, ligada a funcionario
  03_dados_teste.sql      # dados adicionais apenas para testes (opcional)
  README.md
```

**Atenção:** o Anexo A começa por `DROP DATABASE IF EXISTS fornecimento_agua`, eliminando integralmente a base com esse nome! Executar apenas sobre uma base de desenvolvimento cuja eliminação seja aceitável, depois de verificar backups. Nunca executá-lo como rotina de arranque da aplicação.

## Extensão de autenticação

`utilizador` não consta do Anexo A. A Opção 1 proporá uma tabela extra com FK única para `funcionario`, `username` único, `password_hash`, `perfil` e `activo`. Os tipos, índices e mecanismo de hash serão revistos antes da criação do script. Não colocar passwords de demonstração em claro no SQL versionado.

## Log e auditoria

`log_auditoria` recebe alterações por triggers existentes. A aplicação deverá identificar o utilizador na ligação apropriada via `SET @app_utilizador = ...` quando este mecanismo for adoptado; essa variável é **de sessão MySQL**, não global. `log_transacao` tem finalidade de monitorização de operações de BD II: a instrumentação da app deverá ser combinada entre os elementos antes de a implementar indiscriminadamente.
