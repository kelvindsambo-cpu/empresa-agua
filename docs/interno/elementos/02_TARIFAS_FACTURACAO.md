# Opção 2 — Tarifas e facturação

**Nível:** difícil  
**Responsável:** [NOME]  
**Branch sugerida:** `feature/facturacao-tarifas`

## Resultado esperado

Consultar/gerir tarifas sem sobreposição e emitir facturas correctas, com cálculos baseados nos dados reais do contador, contrato, data e tarifa.

## Classes e ficheiros

```text
model/Tarifa.java
model/Fatura.java
dao/TarifaDAO.java
dao/FaturaDAO.java
service/TarifaService.java
service/FaturaService.java
controller/TarifaController.java
controller/FaturaController.java
view/panels/TarifaPanel.java
view/panels/FaturaPanel.java
view/dialogs/TarifaDialog.java
view/dialogs/EmissaoFaturaDialog.java
```

## Regras do minimundo

- **RN12–14:** `preco_por_m3 > 0`, `consumo_minimo > 0` (default 5); vigências por tipo sem sobreposição; procurar tarifa conforme `tipo_contrato` e `data_leitura`.
- **RN15:** emitir apenas para contrato **e** contador activos.
- **RN16:** máximo uma factura por `(id_contador, ano_referencia, mes_referencia)`.
- **RN17–18:** leitura anterior = última leitura actual do contador, ou `leitura_inicial`; leitura actual >= anterior.
- **RN19:** técnico da leitura tem cargo `tecnico_leitura` (trigger já existe).
- **RN20:** `valor_base = max(leitura_atual - leitura_anterior, consumo_minimo) * preco_por_m3`.
- **RN21:** conservar tarifa e valor-base históricos; não recalcular faturas antigas com preço novo.
- **RN22:** vencimento no dia 10 do mês seguinte à **emissão**, não à leitura.
- **RN23:** estado inicial `pendente`.

## Tarefas ordenadas

- [ ] Mapear atributos de `tarifa` e `fatura` exactamente do Anexo A.
- [ ] Implementar `TarifaDAO`: listar, pesquisar por tipo/data, criar tarifa, fechar vigência (com validação de RN13).
- [ ] Implementar `TarifaPanel` e `TarifaDialog`; edição de preço histórico usada em facturas **proibida**.
- [ ] Implementar `FaturaService` com `BigDecimal`, cálculo e testes unitários.
- [ ] Acordar com Opção 3 obtenção de contrato e com Opção 4 contador activo/leitura inicial.
- [ ] Acordar com Opção 5 listagem de técnicos `tecnico_leitura`.
- [ ] Implementar `FaturaDAO` e emissão transaccional, tratando factura duplicada.
- [ ] Implementar `FaturaPanel`/`EmissaoFaturaDialog`: seleccionar dados, simular valor-base, confirmar emissão e listar.
- [ ] Consultar facturas por cliente/contrato, via caminho `fatura → contador → contrato → cliente`.
- [ ] Testar RN12–23 e histórico de tarifas.

## Dados de `fatura` que deves persistir

`mes_referencia`, `ano_referencia`, `data_leitura`, `leitura_anterior`, `leitura_atual`, `data_emissao`, `data_vencimento`, `valor_base`, `valor_multa`, `estado_fatura`, `id_contador`, `id_tarifa`, `id_funcionario`. `id_fatura` é auto-incrementado. `valor_pago`, `consumo_m3`, `valor_total`, `saldo_em_divida` **não são colunas do Anexo A**: o Anexo C muda isso; consultar a decisão da equipa.

## Dependências

- Opção 1: JDBC e sessão.
- Opção 3: contratos e integração com pagamentos (tua responsabilidade não é registar pagamentos).
- Opção 4: contadores activos e leitura inicial.
- Opção 5: técnicos de leitura.
- Opção 6: menu e consistência visual.

## Aceitação e testes

- [ ] Tarifa vigente correcta por tipo/data e erro em caso de período sobreposto.
- [ ] Exemplo: leitura anterior 120, actual 123, mínimo 5, preço 45 → `valor_base = 225.00 MT`.
- [ ] Não emite factura para contrato cortado ou contador inactivo.
- [ ] Não emite leitura decrescente, duplicada ou de técnico não autorizado.
- [ ] Data de vencimento e valor-base persistidos correctamente.
- [ ] A criação de uma tarifa nova **não altera** facturas antigas.
- [ ] Interface não executa SQL e não bloqueia o utilizador em consultas demoradas.

**Cuidado:** não criar classe/tabela `Leitura` autónoma e não implementar `PagamentoDAO` por conta própria.
