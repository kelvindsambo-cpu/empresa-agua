# Membro 2 — Tarifas e facturas

**Nível:** médio  
**Responsável:** [NOME]  
**Branch:** `facturacao`  
**Estado:** feito. Testes unitários a passar e testado contra a BD (T06–T09, rollback, auditoria). Falta ligar ao `MainFrame` quando o Membro 6 o publicar.

## Resultado

Ver e criar tarifas; emitir uma factura a partir de uma leitura, com o valor calculado correctamente; listar facturas.

## Classes

```text
model/Tarifa.java, model/Fatura.java
dao/TarifaDAO.java, dao/FaturaDAO.java
service/FaturaService.java            cálculos + emissão
controller/TarifaController.java, controller/FaturaController.java
view/TarifaPanel.java, view/TarifaDialog.java
view/FaturaPanel.java, view/EmissaoFaturaDialog.java
test/.../FaturaServiceTest.java
util/Formatos.java                    ler/mostrar datas dd/mm/aaaa e números dos formulários (pode ser usado por todos)
test/.../view/ExperimentarFacturacao  TEMPORÁRIO: abre Tarifas e Facturas sem MainFrame; apagar depois da integração
```

## Tarefas

### Tarifas

- [x] `TarifaPanel`: `JTable` com todas as tarifas (tipo, preço, mínimo, início, fim).
- [x] Botão "Nova tarifa" (só ADMIN): tipo, preço, mínimo, data de início.
- [x] Ao gravar, numa transacção (`DatabaseConnection.emTransaccao`): fechar a tarifa vigente do mesmo tipo (`data_fim = início_nova - 1 dia`) e inserir a nova. Recusar se a data de início não for posterior à da vigente.
  - Porquê: o trigger do Anexo A recusa qualquer tarifa nova enquanto a vigente tiver `data_fim = NULL`.
- [x] `TarifaDAO.buscarVigente(tipoContrato, data)`.

### Facturas

- [x] `EmissaoFaturaDialog`: escolher contrato activo → mostrar contador activo e **leitura anterior** → escrever mês/ano, data da leitura, leitura actual → escolher técnico (`tecnico_leitura`) → botão "Calcular" mostra consumo e valor → "Emitir".
- [x] `FaturaService`:
  - leitura anterior = última `leitura_atual` do contador (`ORDER BY ano_referencia DESC, mes_referencia DESC LIMIT 1`), senão `leitura_inicial`;
  - `valor_base = max(actual - anterior, consumo_minimo) × preco_por_m3` (`BigDecimal`, 2 casas);
  - `data_emissao` = hoje; `data_vencimento` = dia 10 do mês seguinte à emissão;
  - validar: leitura actual ≥ anterior; existe tarifa vigente para o tipo do contrato na data da leitura.
- [x] `FaturaDAO.inserir(...)` com `estado_fatura = 'pendente'`, `valor_multa = 0`.
- [x] `FaturaPanel`: `JTable` com facturas (nº, cliente, contador, mês/ano, valor, estado).
- [x] `FaturaDAO.listarPendentes()` e `buscarPorId(int)` para o Membro 3.
- [x] JUnit: consumo normal, consumo abaixo do mínimo, leitura decrescente, vencimento (incluindo emissão em Dezembro → 10 de Janeiro).

A BD já recusa: factura duplicada no mesmo mês, técnico que não é `tecnico_leitura`, leitura decrescente.

## Precisa de

Membro 3: `ContratoDAO.listarActivos()`; Membro 4: `ContadorDAO.buscarActivoPorContrato()`; Membro 5: `FuncionarioDAO.listarPorCargo("tecnico_leitura")`. Até estarem em `main`, o `FaturaDAO` tem consultas próprias temporárias (secção "TEMPORÁRIO") sobre os dados do Anexo A.

## Demonstração

Criar uma tarifa nova e mostrar que a anterior foi fechada; emitir factura com leitura 120 → 123 (mínimo 5, preço 45) e mostrar `225.00 MT`; tentar leitura menor que a anterior.

**Fora do âmbito:** editar tarifas, multas, estado `vencida`, simulação de várias tarifas por período.
