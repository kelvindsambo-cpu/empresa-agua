# Opção 3 — Contratos e pagamentos

**Nível:** difícil  
**Responsável:** [NOME]  
**Branch sugerida:** `feature/contratos-pagamentos`

## Resultado esperado

Criar/consultar contratos e efectuar pagamentos totais ou parciais respeitando os estados, as FKs e a concorrência.

## Classes e ficheiros

```text
model/Contrato.java
model/Pagamento.java
dao/ContratoDAO.java
dao/PagamentoDAO.java
service/ContratoService.java
service/PagamentoService.java
controller/ContratoController.java
controller/PagamentoController.java
view/panels/ContratoPanel.java
view/panels/PagamentoPanel.java
view/dialogs/ContratoDialog.java
view/dialogs/RegistoPagamentoDialog.java
```

## Tarefas

- [ ] Criar Models exactamente do Anexo A.
- [ ] Implementar CRUD operacional de contratos (criar, listar, pesquisar, alterar estados permitidos).
- [ ] Validar `tipo_contrato`, datas e RN05: contrato `terminado` tem `data_fim` e não volta a activo.
- [ ] Coordenar primeira criação cliente+contrato com Opção 4.
- [ ] Implementar selecção de factura, apresentação de valor, total já pago e saldo.
- [ ] Implementar registo de pagamento JDBC **transaccional**: bloquear factura `SELECT ... FOR UPDATE`, recalcular saldo, validar, inserir `pagamento`, actualizar estado se pago, `commit`; em erro `rollback`.
- [ ] Implementar RN38: `registado_por = NULL` para `mpesa` e `emola`, preenchido nos outros métodos.
- [ ] Garantir número único de recibo e proibir pagamento acima do saldo ou de factura paga.
- [ ] Discutir com Opção 2 a origem do saldo e diferenças entre Anexo A e C.
- [ ] Testar pagamentos parciais, recibo duplicado, pagamento exacto e concorrente.
- [ ] Definir com a equipa o alcance das multas e da mudança automática de estados.

## Integrações

- Opção 4: clientes/contadores; Opção 2: facturas e tarifas; Opção 5: funcionário que regista; Opção 1: sessão JDBC; Opção 6: menu.

## Critérios de aceitação

- [ ] Contratos associados a clientes válidos, datas consistentes e estados legais.
- [ ] Pagamentos parciais mantêm saldo positivo; último pagamento fecha saldo e marca factura paga.
- [ ] Duas operações concorrentes nunca ultrapassam o saldo.
- [ ] Métodos móveis/canais ao balcão cumprem o CHECK RN38.
- [ ] O histórico de pagamentos existe e não é apagado nem alterado (RN40).
- [ ] Sem SQL na View nem consultas bloqueantes na EDT.

**Cuidado:** `saldo_em_divida` não é uma coluna em Anexo A; só existe como coluna gerada se a equipa aplicar Anexo C.
