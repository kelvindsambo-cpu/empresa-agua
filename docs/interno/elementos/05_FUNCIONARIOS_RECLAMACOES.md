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
