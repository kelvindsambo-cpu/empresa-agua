# Tarefas e fases

## Fase 0 — arranque (todos, meio dia)

- [ ] Membro 1 integra a branch `autenticacao` em `main`, com `database/02_autenticacao.sql` e `application.properties.example` (ver [`elementos/01_ARQUITECTURA_AUTENTICACAO.md`](elementos/01_ARQUITECTURA_AUTENTICACAO.md)).
- [ ] Cada membro: importa o Anexo A + `02_autenticacao.sql`, cria o admin inicial e consegue fazer login.
- [ ] Membro 6 publica cedo um `MainFrame` vazio com o método `adicionarModulo(...)`, para os outros encaixarem os painéis.
- [ ] Preencher os nomes em [`01_PLANO_E_DIVISAO.md`](01_PLANO_E_DIVISAO.md).

## Fase 1 — o CRUD simples de cada um (em paralelo)

- [ ] Membro 2: `Tarifa` (listar + criar).
- [ ] Membro 3: `Contrato` (listar, criar, mudar estado).
- [ ] Membro 4: `Cliente` (listar, pesquisar, criar, editar).
- [ ] Membro 5: `Funcionario` (listar, criar, editar; só ADMIN).
- [ ] Membro 6: `MainFrame` com menu + Dashboard com 4 contagens.

Ao terminar: Pull Request para `main`.

## Fase 2 — a segunda entidade de cada um

- [ ] Membro 2: emitir `Fatura` + listar facturas.
- [ ] Membro 3: registar `Pagamento` + ver saldo.
- [ ] Membro 4: registar `Contador` num contrato + mudar estado.
- [ ] Membro 5: `Reclamacao` (abrir, atribuir responsável, resolver).
- [ ] Membro 6: script de dados de demonstração + guia de execução testado.

Ao terminar: Pull Request para `main`.

## Fase 3 — integração e ensaio (todos)

- [ ] Percorrer o fluxo: login → cliente → contrato → contador → factura → pagamento → Dashboard.
- [ ] Correr os testes manuais de [`06_TESTES_E_CRITERIOS.md`](06_TESTES_E_CRITERIOS.md).
- [ ] `mvn clean test` passa em `main`.
- [ ] Cada membro ensaia a sua demonstração (2–3 minutos).

Não há durações fixas: uma fase pode começar antes de outra acabar. Usem os dados de exemplo do Anexo A para não ficarem à espera dos colegas.
