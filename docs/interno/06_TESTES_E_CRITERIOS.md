# Testes e definição de concluído

## Uma tarefa está concluída quando...

- [ ] Compila e a aplicação abre.
- [ ] Os dados vêm da BD por JDBC (nada de listas inventadas no código).
- [ ] A `JTable` actualiza depois de inserir/editar.
- [ ] Um caso de sucesso e um caso de erro foram testados à mão.
- [ ] O Pull Request diz como testar.

## Testes automáticos (JUnit)

Só onde há cálculo, para não gastar tempo:

- Membro 2: cálculo do consumo, do valor-base (com consumo mínimo) e da data de vencimento.
- Membro 1: já tem testes de `PasswordHasher`, `Sessao`, `Perfil` e `UtilizadorService`.

Os outros módulos testam-se manualmente com a tabela abaixo.

## Testes manuais (para fazer antes da apresentação)

| ID | Módulo | Teste | Esperado |
|---|---|---|---|
| T01 | Login | password errada / certa | erro / entra |
| T02 | Perfil | OPERADOR abre o menu | não vê Funcionários nem "Nova tarifa" |
| T03 | Cliente | NUIT repetido | recusado, cliente não duplicado |
| T04 | Contrato | passar a `terminado` sem data de fim | recusado |
| T05 | Contador | segundo contador activo no mesmo contrato | recusado |
| T06 | Tarifa | nova tarifa com início anterior ao da vigente | recusada |
| T06b | Tarifa | nova tarifa válida | a anterior fica com `data_fim` = véspera do início da nova |
| T07 | Factura | leitura actual menor que a anterior | não emitida |
| T08 | Factura | consumo abaixo do mínimo | cobra o consumo mínimo |
| T09 | Factura | segunda factura no mesmo mês para o mesmo contador | recusada |
| T10 | Pagamento | valor maior que o saldo | recusado |
| T11 | Pagamento | pagamento que liquida o saldo | factura passa a `paga` |
| T12 | Reclamação | `resolvida` | data de fecho preenchida |
| T13 | Dashboard | inserir um cliente e voltar ao Dashboard | número aumenta |

**Exemplo de cálculo para T08:** leitura anterior 120, actual 123, mínimo 5, preço 45 → consumo 3, cobra 5 → `valor_base = 225.00 MT`.

## Dados de teste

O Anexo A já traz dados de exemplo e faz `DROP DATABASE` ao ser executado: usar só numa BD de desenvolvimento. Para recomeçar do zero, basta voltar a correr o Anexo A + `02_autenticacao.sql` + script de demonstração.
