# Dicionário curto do Anexo A e regras essenciais

**Fontes:** [`database/bdii/Anexo_A_Esquema.sql`](../../database/bdii/Anexo_A_Esquema.sql) e o documento *Minimundo e Regras de Negócio* de BD II. Este resumo é específico do **Anexo A sem optimização** e não substitui o SQL.

## Tabelas e colunas (Anexo A)

| Tabela | Colunas existentes |
|---|---|
| `cliente` | `id_cliente`, `nome_completo`, `nuit`, `telefone`, `rua`, `numero_casa`, `bairro` |
| `funcionario` | `id_funcionario`, `nome`, `telefone`, `cargo` |
| `tarifa` | `id_tarifa`, `tipo_contrato`, `preco_por_m3`, `consumo_minimo`, `data_inicio`, `data_fim` |
| `contrato` | `id_contrato`, `tipo_contrato`, `estado_contrato`, `data_inicio`, `data_fim`, `rua`, `numero_porta`, `quarteirao`, `bairro`, `id_cliente` |
| `contador` | `id_contador`, `numero_contador`, `data_instalacao`, `data_remocao`, `leitura_inicial`, `estado_contador`, **`activo` (coluna gerada!)**, `id_contrato` |
| `fatura` | `id_fatura`, `mes_referencia`, `ano_referencia`, `data_leitura`, `leitura_anterior`, `leitura_atual`, `data_emissao`, `data_vencimento`, `valor_base`, `valor_multa`, `estado_fatura`, `id_contador`, `id_tarifa`, `id_funcionario` |
| `pagamento` | `id_pagamento`, `data_pagamento`, `valor`, `metodo_pagamento`, `numero_recibo`, `id_fatura`, `registado_por` |
| `reclamacao` | `id_reclamacao`, `tipo_reclamacao`, `descricao`, `data_abertura`, `data_fecho`, `estado_reclamacao`, `id_cliente`, `id_funcionario` |
| `log_auditoria` | `id_log`, `usuario`, `acao`, `tabela_afetada`, `registro_id`, `detalhe`, `data_hora` |
| `log_transacao` | `id_log`, `transacao`, `utilizador`, `tipo_operacao`, `inicio`, `fim`, `estado`, `codigo_erro` |

**Apenas colunas do Anexo A:** `valor_pago`, `consumo_m3`, `valor_total`, `saldo_em_divida` **não existem** fisicamente na `fatura`. O Anexo C muda isso; ver [`database/README.md`](../../database/README.md).

## Valores ENUM exactos

- `contrato.tipo_contrato`, `tarifa.tipo_contrato`: `residencial`, `comercial`.
- `contrato.estado_contrato`: `activo`, `suspenso`, `cortado`, `terminado`.
- `contador.estado_contador`: `activo`, `inactivo`, `avariado`.
- `funcionario.cargo`: `tecnico_leitura`, `tecnico_manutencao`, `atendimento`, `supervisor`, `administrador`.
- `fatura.estado_fatura`: `pendente`, `paga`, `vencida`.
- `pagamento.metodo_pagamento`: `dinheiro`, `transferencia`, `mpesa`, `emola`, `cartao`.
- `reclamacao.tipo_reclamacao`: `fuga`, `avaria_contador`, `falta_agua`, `qualidade_agua`, `faturacao`, `atendimento`, `outro`.
- `reclamacao.estado_reclamacao`: `aberta`, `em_tratamento`, `resolvida`.

## Relações principais

```text
cliente (1) ─── (N) contrato (1) ─── (N) contador (1) ─── (N) fatura
    |                                                              |
    └─────── (N) reclamacao                        tarifa (1) ───────┤
                                               funcionario (1) ─────┤
                                                              pagamento (N)
funcionario (1) ─── (N) reclamacao [responsável, opcional]
funcionario (1) ─── (N) pagamento [registado_por, opcional]
```

As referências cruzadas são representadas pelas FKs no esquema; consultar MER para leitura rigorosa de cardinalidades.

## Regras relevantes para implementação

- **RN01–06:** cliente/contrato. NUIT opcional e único; contrato pode ser terminado uma única vez e deve ter data de fim; local de fornecimento pode diferir da morada do cliente.
- **RN07–11:** contador. Número de série único; apenas um activo por contrato; `leitura_inicial` alimenta a primeira factura.
- **RN12–14:** tarifa. Preço/mínimo positivos; intervalos sem sobreposição; tarifa vigente depende da data e do tipo do contrato; não editar tarifa antiga usada.
- **RN15–23:** factura. Apenas contrato/contador activos; no máximo 1 factura por contador e mês; técnico leitor obrigatório; cálculo do valor-base; vencimento a 10 do mês seguinte.
- **RN24–31:** pagamentos, multas, corte e religação. Não pagar além do saldo; multa de 25% uma vez quando aplicável; saldo devedores inclui multa.
- **RN32–35:** reclamações. Ligadas a cliente, não a contador/contrato; responsável exigido ao iniciar tratamento; resolver exige data de fecho.
- **RN36–38:** cargos válidos; manter histórico; `registado_por` NULL nos métodos `mpesa` e `emola`, preenchido nos demais.
- **RN39–42:** auditoria e registo de transacções.

## Cálculos

```text
consumo_m3   = leitura_atual - leitura_anterior
consumo_cobrado = MAX(consumo_m3, consumo_minimo)
valor_base   = consumo_cobrado * preco_por_m3
valor_total  = valor_base + valor_multa
valor_pago   = SUM(pagamento.valor da fatura)
saldo        = valor_total - valor_pago
```

O valor da factura deve ser tratado com `BigDecimal`. Consultas SQL devem respeitar a variante de BD congelada.

## Atenção a detalhes do SQL

1. A validação de sobreposição de tarifa ocorre no trigger `trg_tarifa_before_insert`; a UI também deve prevenir uma má entrada.
2. O trigger `trg_fatura_before_insert` verifica se o funcionário tem cargo `tecnico_leitura`.
3. Os CHECKs e UNIQUE protegem parte do modelo, mas **não substituem** validações de fluxo na aplicação.
4. O Anexo A inclui dados de exemplo e `DROP DATABASE`: executar com cuidado.
5. A versão optimizada de BD II cria dados derivados na factura que **não estão** no esquema-base; nunca os assumir sem decidir qual variante será usada.
