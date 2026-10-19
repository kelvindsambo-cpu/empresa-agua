# Casos de uso e controlo de acesso

**Actores:** `OPERADOR` e `ADMIN`. O ADMIN acumula permissões operacionais.

| Código | Caso de uso | OPERADOR | ADMIN |
|---|---|:---:|:---:|
| UC01 | Iniciar e terminar sessão | ✓ | ✓ |
| UC02 | Registar, pesquisar, listar e actualizar clientes | ✓ | ✓ |
| UC03 | Consultar contratos de um cliente | ✓ | ✓ |
| UC04 | Criar, pesquisar e alterar estados de contratos | ✓ | ✓ |
| UC05 | Instalar, consultar e trocar contadores | ✓ | ✓ |
| UC06 | Consultar tarifas | ✓ | ✓ |
| UC06-A | Criar tarifas e gerir a vigência | — | ✓ |
| UC07 | Registar leitura e emitir factura | ✓ | ✓ |
| UC08 | Consultar facturas e saldos | ✓ | ✓ |
| UC09 | Registar pagamentos totais/parciais | ✓ | ✓ |
| UC10 | Abrir, atribuir e resolver reclamações | ✓ | ✓ |
| UC11 | Gerir funcionários | — | ✓ |
| UC12 | Criar, activar e desactivar contas de utilizador | — | ✓ |
| UC13 | Consultar Dashboard | ✓ | ✓ |

**O cargo do funcionário** (ex.: `tecnico_leitura`) não é o mesmo que o perfil de acesso (`ADMIN`/`OPERADOR`). Só um técnico de leitura pode constar como responsável pela leitura na `fatura`, independentemente do perfil de quem opera o sistema.

## UC07: emissão de factura

**Pré-condições:** sessão válida; contrato e contador activos; referência mensal ainda não facturada para o contador; técnico com cargo `tecnico_leitura`; tarifa em vigor na `data_leitura`.

1. Funcionário selecciona contrato e contador.
2. Sistema consulta leitura anterior (última `leitura_atual` do contador, senão `leitura_inicial`).
3. Funcionário informa mês/ano, data e leitura actual, e identifica técnico leitor.
4. Sistema valida sequência de leitura, vigência da tarifa e duplicação por período.
5. Calcula `consumo = leitura_atual - leitura_anterior` e `valor_base = max(consumo, consumo_minimo) * preco_por_m3`.
6. Calcula vencimento para o dia **10 do mês seguinte à emissão**; apresenta resumo para confirmação.
7. Regista `fatura` com `id_tarifa` e `valor_base` fixados na emissão.

**Falhas:** leitura menor que a anterior, tarifa inexistente, técnico inadequado, período duplicado ou concorrência → mensagem adequada, nenhuma factura parcial.

## UC09: registar pagamento

**Pré-condições:** sessão válida, factura existente e não paga.

1. Seleccionar factura e consultar saldo actualizado.
2. Informar valor, data, método e recibo único.
3. Aplicar RN38: para `dinheiro`, `transferencia` ou `cartao`, guardar funcionário em `registado_por`; para `mpesa`/`emola`, guardar `NULL`.
4. Numa mesma transacção, bloquear a factura, recalcular o saldo e impedir exceder o saldo ou pagar factura já liquidada.
5. Inserir pagamento e marcar factura como `paga` quando saldo zero.
6. Confirmar; actualizar a listagem.

**Falhas:** recibo duplicado, valor inválido ou tentativa concorrente → `rollback`, comunicação clara ao utilizador.

## UC12: criar conta

1. ADMIN escolhe funcionário existente sem conta.
2. Define username único, perfil, e palavra-passe inicial.
3. Aplicação gera hash de password (bcrypt/Argon2id, não texto simples).
4. Guarda conta ligada ao `id_funcionario` e informa resultado sem mostrar hash ou password em logs.

## Limites do projecto

Sem portal de clientes, sem app móvel, sem integração real com M-Pesa/e-Mola, sem emissão de leitura autónoma. O Dashboard deve apresentar dados reais, não números fictícios. O comportamento de multas e corte/religação deve obedecer ao minimundo e ao âmbito decidido na equipa.
