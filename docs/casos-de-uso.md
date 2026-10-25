# Casos de uso e controlo de acesso

**Actores:** `OPERADOR` e `ADMIN`. O ADMIN pode fazer tudo o que o OPERADOR faz.

| Código | Caso de uso | OPERADOR | ADMIN |
|---|---|:---:|:---:|
| UC01 | Iniciar e terminar sessão | ✓ | ✓ |
| UC02 | Registar, pesquisar e editar clientes | ✓ | ✓ |
| UC03 | Criar contratos e mudar o seu estado | ✓ | ✓ |
| UC04 | Registar contadores e mudar o seu estado | ✓ | ✓ |
| UC05 | Consultar tarifas | ✓ | ✓ |
| UC05-A | Criar nova tarifa | — | ✓ |
| UC06 | Registar leitura e emitir factura | ✓ | ✓ |
| UC07 | Registar pagamentos totais/parciais | ✓ | ✓ |
| UC08 | Abrir, atribuir e resolver reclamações | ✓ | ✓ |
| UC09 | Registar e editar funcionários | — | ✓ |
| UC10 | Consultar Dashboard | ✓ | ✓ |

**Cargo ≠ perfil:** o cargo do funcionário (ex.: `tecnico_leitura`) é do negócio; `ADMIN`/`OPERADOR` é o perfil de acesso à aplicação. Só um técnico de leitura pode constar como responsável pela leitura numa factura, seja qual for o perfil de quem opera o sistema.

## UC06: emitir factura

**Pré-condições:** contrato e contador activos; tarifa vigente na data da leitura; mês ainda não facturado para esse contador.

1. O funcionário escolhe o contrato; o sistema mostra o contador activo e a leitura anterior (última `leitura_atual` do contador, ou `leitura_inicial`).
2. O funcionário indica mês/ano, data da leitura, leitura actual e técnico de leitura.
3. O sistema calcula `consumo = actual - anterior` e `valor_base = max(consumo, consumo_minimo) × preco_por_m3`, e o vencimento (dia 10 do mês seguinte à emissão).
4. O funcionário confirma; a factura fica `pendente`.

**Falhas:** leitura menor que a anterior, sem tarifa vigente, mês repetido ou técnico inválido → mensagem de erro, nenhuma factura gravada.

## UC07: registar pagamento

1. O funcionário escolhe uma factura pendente; o sistema mostra o saldo (`valor_base + valor_multa - total pago`).
2. Indica valor, data, método e número de recibo.
3. Numa transacção, o sistema recusa valores acima do saldo, grava o pagamento (com `registado_por` = funcionário em `dinheiro`/`transferencia`/`cartao`, ou vazio em `mpesa`/`emola`) e marca a factura como `paga` se o saldo chegar a zero.

**Falhas:** valor inválido ou acima do saldo, recibo repetido → nada é gravado.

## Limites do projecto

Sem portal de clientes, sem app móvel, sem integração real com M-Pesa/e-Mola. Fora do âmbito: multas, corte/religação automáticos, estado `vencida`, gestão de contas de utilizador por ecrã, eliminação de registos.
