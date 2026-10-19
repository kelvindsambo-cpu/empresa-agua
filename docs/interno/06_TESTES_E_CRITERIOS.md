# Plano de testes e definição de concluído

## Uma tarefa só está concluída se...

- [ ] O código compila e respeita os packages/nomenclatura combinados.
- [ ] A funcionalidade usa o MySQL por JDBC, sem simular dados numa lista fixa (excepto fixtures de teste).
- [ ] Casos de sucesso e pelo menos um caso de erro foram testados.
- [ ] Os dados apresentados na JTable são recarregados após actualizar.
- [ ] A interface não fica inutilizável durante consultas lentas.
- [ ] As regras de negócio aplicáveis são respeitadas.
- [ ] Há instruções de teste suficientes no Pull Request.
- [ ] O código foi revisto e integrado em `develop`.

## Matriz mínima de testes

| ID | Módulo | Teste | Esperado |
|---|---|---|---|
| T01 | Login | credenciais válidas / inválidas | entra ou mostra erro, sem revelar password |
| T02 | Perfil | OPERADOR tenta gerir utilizadores | operação recusada também na lógica |
| T03 | Cliente | NUIT duplicado | validação/erro por UNIQUE, sem inserção duplicada |
| T04 | Contrato | terminar sem data de fim | não permitido |
| T05 | Contador | segundo contador activo no mesmo contrato | recusado pela regra de negócio/UNIQUE |
| T06 | Tarifa | períodos sobrepostos para o mesmo tipo | não permitido |
| T07 | Tarifa | consultar tarifa de data passada | tarifa histórica correcta |
| T08 | Factura | leitura inferior à anterior | não emitida |
| T09 | Factura | período já faturado no mesmo contador | não emitida |
| T10 | Factura | técnico que não é `tecnico_leitura` | não emitida |
| T11 | Factura | consumo real inferior ao mínimo | factura pelo consumo mínimo |
| T12 | Pagamento | valor supera saldo | nenhum pagamento inserido |
| T13 | Pagamento | duas tentativas simultâneas de pagar o mesmo saldo | total pago não ultrapassa saldo |
| T14 | Pagamento | M-Pesa com `registado_por` preenchido | não permitido |
| T15 | Reclamação | `em_tratamento` sem responsável | não permitido |
| T16 | Reclamação | `resolvida` sem data de fecho | não permitido |
| T17 | Histórico | nova tarifa após factura antiga | valor-base antigo não muda |
| T18 | Dashboard | resumo após inserir dados | números actualizados, obtidos da BD |

## Dados para demonstração

O Anexo A já contém dados **de exemplo**. Nunca testar inserções destrutivas numa base importante; usar instância de desenvolvimento separada. Evitar assumir que IDs começam sempre por 1 ou que uma factura específica estará presente. Recriar estado de teste quando necessário, lembrando que o Anexo A apaga a base de dados com `DROP DATABASE`.

## Tipos de teste

- **Unitário:** cálculos do consumo e do valor-base, validações independentes da BD.
- **Integração:** DAO ↔ MySQL, transacções, constraints e triggers.
- **Interface:** botões, JTable, JComboBox, diálogos e navegação.
- **Aceitação:** percorrer os casos de uso como um funcionário faria.

## Bugs no GitHub

Issue de bug deve conter ambiente, passos para reproduzir, resultado esperado, resultado obtido e evidência (sem informações sensíveis). Não encerrar bugs com apenas uma explicação textual: corrigir e retestar.
