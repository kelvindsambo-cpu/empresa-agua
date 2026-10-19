# Checklist final e limpeza de documentação interna

## Antes da entrega

- [ ] `main` tem uma versão estável e demonstrável (sem bugs bloqueantes).
- [ ] `mvn clean test` funciona em máquina limpa com JDK/Maven definidos.
- [ ] Existe procedimento reproduzível para preparar a base de desenvolvimento.
- [ ] Login, clientes, contratos, contadores, tarifas, facturas, pagamentos, reclamações e funcionários cumprem o âmbito acordado.
- [ ] A separação MVC/DAO é verificável no código.
- [ ] `SwingWorker` ou abordagem equivalente impede travamentos nos fluxos lentos.
- [ ] Dados de apresentação são fictícios e não contêm credenciais reais.
- [ ] README, guia de execução e arquitectura reflectem o código realmente entregue.
- [ ] Cada elemento sabe demonstrar o seu módulo e explicar JDBC, eventos Swing e classes.

## O que pode ser removido

**Pasta temporária opcional:** `docs/interno/` (distribuição de tarefas, progressos, regras internas e notas de organização). A `.github/ISSUE_TEMPLATE` pode ficar ou ser removida; os Issue/PR históricos podem manter-se no GitHub.

**O que normalmente deve ficar:** `README.md`, `docs/arquitectura.md`, `docs/casos-de-uso.md`, `docs/guia-de-execucao.md`, scripts SQL realmente necessários, código-fonte, `pom.xml` e `.gitignore`.

### Exemplo de remoção na fase final

```bash
git rm -r docs/interno
git commit -m "docs: remover notas internas de coordenação"
```

**Isto só retira os ficheiros do estado actual da branch; eles continuam acessíveis no histórico Git.** Se houver informações sensíveis, não as publicar desde o início. Não reescrever histórico por iniciativa própria sem acordo e sem compreender o efeito na equipa.

## Preparação da defesa

- Cada elemento apresenta um caso de uso do seu módulo.
- Explicar o caminho View → Controller → Service → DAO → JDBC → MySQL.
- Mostrar validação no Java e protecção na BD (CHECK, UNIQUE, FK, trigger) quando aplicável.
- Explicar como os `PreparedStatement` evitam concatenação de SQL.
- Demonstrar erros controlados (não apenas casos felizes).
- Mostrar que não há valores falsos no Dashboard.

## Revisão de autoria e colaboração

Cada membro é responsável pelos próprios commits e alterações. Não inserir nomes de pessoas ou ferramentas como autor/coautor sem contribuição real; não incluir marcas ou menções a assistentes no código e na documentação final. Manter histórico íntegro da colaboração e respeitar os requisitos académicos aplicáveis.
