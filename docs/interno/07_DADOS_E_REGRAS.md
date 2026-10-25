# Dicionário curto do Anexo A e regras que entram

**Fontes:** [`database/bdii/Anexo_A_Esquema.sql`](../../database/bdii/Anexo_A_Esquema.sql) e o *Minimundo e Regras de Negócio* de BD II. Usamos **só o Anexo A** (sem Anexo C).

## Tabelas e colunas

| Tabela | Colunas |
|---|---|
| `cliente` | `id_cliente`, `nome_completo`, `nuit` (opcional, único), `telefone`, `rua`, `numero_casa`, `bairro` |
| `funcionario` | `id_funcionario`, `nome`, `telefone`, `cargo` |
| `tarifa` | `id_tarifa`, `tipo_contrato`, `preco_por_m3`, `consumo_minimo`, `data_inicio`, `data_fim` (NULL = vigente) |
| `contrato` | `id_contrato`, `tipo_contrato`, `estado_contrato`, `data_inicio`, `data_fim`, `rua`, `numero_porta`, `quarteirao`, `bairro`, `id_cliente` |
| `contador` | `id_contador`, `numero_contador`, `data_instalacao`, `data_remocao`, `leitura_inicial`, `estado_contador`, `activo` (**gerada pela BD — nunca escrever**), `id_contrato` |
| `fatura` | `id_fatura`, `mes_referencia`, `ano_referencia`, `data_leitura`, `leitura_anterior`, `leitura_atual`, `data_emissao`, `data_vencimento`, `valor_base`, `valor_multa`, `estado_fatura`, `id_contador`, `id_tarifa`, `id_funcionario` |
| `pagamento` | `id_pagamento`, `data_pagamento`, `valor`, `metodo_pagamento`, `numero_recibo` (único), `id_fatura`, `registado_por` |
| `reclamacao` | `id_reclamacao`, `tipo_reclamacao`, `descricao`, `data_abertura`, `data_fecho`, `estado_reclamacao`, `id_cliente`, `id_funcionario` |
| `utilizador` | tabela do login (Membro 1) — os outros não mexem |

`log_auditoria` e `log_transacao` existem mas **ninguém precisa de as usar** (os triggers preenchem a auditoria sozinhos).

## Valores ENUM (copiar exactamente para as `JComboBox`)

- `tipo_contrato`: `residencial`, `comercial`
- `estado_contrato`: `activo`, `suspenso`, `cortado`, `terminado`
- `estado_contador`: `activo`, `inactivo`, `avariado`
- `cargo`: `tecnico_leitura`, `tecnico_manutencao`, `atendimento`, `supervisor`, `administrador`
- `estado_fatura`: `pendente`, `paga`, `vencida`
- `metodo_pagamento`: `dinheiro`, `transferencia`, `mpesa`, `emola`, `cartao`
- `tipo_reclamacao`: `fuga`, `avaria_contador`, `falta_agua`, `qualidade_agua`, `faturacao`, `atendimento`, `outro`
- `estado_reclamacao`: `aberta`, `em_tratamento`, `resolvida`

## Relações

```text
cliente (1) ── (N) contrato (1) ── (N) contador (1) ── (N) fatura (1) ── (N) pagamento
   └── (N) reclamacao                          tarifa (1) ──┘
funcionario: técnico da fatura; responsável da reclamacao; registado_por do pagamento
```

## Regras que entram (e quem já as garante)

| Regra | O quê | Java | BD já garante |
|---|---|:---:|:---:|
| RN01 | NUIT opcional e único | — | ✓ `UNIQUE` |
| RN05 | contrato `terminado` exige `data_fim` | ✓ | ✓ `CHECK` |
| RN09 | um contador activo por contrato | — | ✓ `UNIQUE` |
| RN11/17 | leitura anterior = última `leitura_atual` do contador, senão `leitura_inicial` | ✓ | — |
| RN12 | preço e mínimo > 0 | ✓ | ✓ `CHECK` |
| RN13 | tarifas do mesmo tipo sem sobreposição | fechar a vigente | ✓ trigger |
| RN15 | só factura contrato/contador activos | ✓ | — |
| RN16 | uma factura por contador e mês | — | ✓ `UNIQUE` |
| RN18 | leitura actual ≥ anterior | ✓ | ✓ `CHECK` |
| RN19 | leitura feita por `tecnico_leitura` | combo só com técnicos | ✓ trigger |
| RN20 | `valor_base = max(consumo, mínimo) × preço` | ✓ | — |
| RN22 | vencimento dia 10 do mês seguinte à emissão | ✓ | — |
| RN24 | não pagar acima do saldo | ✓ | — |
| RN34/35 | reclamação: `resolvida` ⇔ `data_fecho`; `em_tratamento` exige responsável | ✓ | ✓ `CHECK` |
| RN38 | `registado_por` NULL em `mpesa`/`emola`, preenchido nos outros | ✓ | ✓ `CHECK` |

**Ficam de fora:** multas (RN25–28), corte/religação automáticos (RN29–31), log de transacções (RN41–42).

## Cálculos

```text
consumo         = leitura_atual - leitura_anterior
consumo_cobrado = MAX(consumo, consumo_minimo)
valor_base      = consumo_cobrado * preco_por_m3          (BigDecimal, 2 casas)
total_pago      = SUM(pagamento.valor) da factura
saldo           = valor_base + valor_multa - total_pago   (valor_multa fica sempre 0)
```
