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
