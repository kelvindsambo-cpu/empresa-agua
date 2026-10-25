# GitHub — fluxo simples

## Branches

- `main`: versão que funciona. Ninguém faz commit directo.
- Uma branch por membro: `autenticacao` (Membro 1, já existe), `facturacao`, `contratos-pagamentos`, `clientes-contadores`, `funcionarios-reclamacoes`, `interface-dashboard`.

Sem `develop`, sem branches por Issue. Issues e GitHub Projects são opcionais.

## Ciclo de trabalho

```bash
git checkout main
git pull
git checkout -b clientes-contadores      # só da primeira vez
# ... trabalhar ...
git status                               # confirmar que não vai application.properties nem target/
git add src/...
git commit -m "feat(cliente): listar clientes na JTable"
git push -u origin clientes-contadores
```

Quando uma parte funcionar (ex.: clientes completos), abrir **Pull Request para `main`**, pedir a **um** colega para rever e fazer *merge* quando compilar e funcionar.

Para apanhar o trabalho dos outros enquanto continuas na tua branch:

```bash
git checkout main && git pull
git checkout clientes-contadores
git merge main
```

## Regras

- Não fazer `push --force` em `main`.
- Não versionar `application.properties`, `target/`, dumps da BD.
- Avisar o grupo antes de mexer no `pom.xml`, no SQL ou em ficheiros de outro membro.
- Cada membro faz os seus próprios commits; não pôr nomes de pessoas ou ferramentas como autor/co-autor de trabalho que não fizeram.
