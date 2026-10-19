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
