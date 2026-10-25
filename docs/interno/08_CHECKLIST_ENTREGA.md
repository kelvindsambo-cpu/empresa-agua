# Checklist final

## Antes da entrega

- [ ] `main` compila (`mvn clean test`) e a aplicação abre.
- [ ] Guia de execução testado numa máquina de outro membro (BD do zero até ao login).
- [ ] O fluxo login → cliente → contrato → contador → factura → pagamento → Dashboard funciona.
- [ ] Testes manuais T01–T13 feitos ([`06_TESTES_E_CRITERIOS.md`](06_TESTES_E_CRITERIOS.md)).
- [ ] Não há SQL nas Views; não há passwords no código nem no Git.
- [ ] README e `docs/` descrevem o que foi realmente feito.
- [ ] Dados de demonstração fictícios.

## Apresentação (cada membro, 2–3 minutos)

1. Mostrar a sua funcionalidade na aplicação (um caso que funciona + um erro controlado).
2. Abrir o código e explicar o caminho **View → Controller → DAO → JDBC → MySQL** com um exemplo.
3. Explicar porque usa `PreparedStatement`.
4. Membros 2 e 3: explicar o cálculo/regra e a transacção (`DatabaseConnection.emTransaccao`).

## Limpeza (opcional)

`docs/interno/` pode ser removida no fim (`git rm -r docs/interno`). Isto não apaga o histórico do Git, por isso nunca versionar dados sensíveis.

## Autoria

Cada membro é responsável pelos próprios commits. Não inserir nomes de pessoas ou ferramentas como autor/co-autor sem contribuição real, nem marcas de ferramentas no código ou na documentação final.
