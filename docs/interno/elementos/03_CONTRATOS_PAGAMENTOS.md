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
