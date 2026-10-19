# GitHub — fluxo de colaboração

## Estrutura do repositório

```text
fornecimento-agua-poo2/
├── README.md
├── pom.xml                        # adicionado pela Opção 1
├── .gitignore
├── database/
│   ├── README.md
│   ├── 01_esquema_bdii.sql        # a adicionar após revisão
│   ├── 02_autenticacao.sql        # a adicionar após revisão
│   └── 03_dados_teste.sql         # opcional, sem dados reais
├── docs/
│   ├── arquitectura.md
│   ├── casos-de-uso.md
│   ├── guia-de-execucao.md
│   └── interno/                   # trabalho da equipa; remover na entrega se desejado
├── src/
│   ├── main/
│   │   ├── java/mz/agua/
│   │   │   ├── Main.java
│   │   │   ├── model/
│   │   │   ├── dao/
│   │   │   ├── service/
│   │   │   ├── controller/
│   │   │   ├── view/{panels,dialogs,components}/
│   │   │   ├── config/
│   │   │   ├── exception/
│   │   │   └── util/
│   │   └── resources/
│   └── test/java/mz/agua/
└── .github/
    ├── PULL_REQUEST_TEMPLATE.md
    └── ISSUE_TEMPLATE/feature.md
```

## Branches

- `main`: versão estável e demonstrável.
- `develop`: integração de funcionalidades revistas.
- `feature/autenticacao`, `feature/facturacao-tarifas`, `feature/contratos-pagamentos`, `feature/clientes-contadores`, `feature/funcionarios-reclamacoes`, `feature/dashboard-interface`: branches de trabalho propostas.

Em equipas com várias Issues por módulo, preferir branches curtas por Issue, por exemplo `feature/42-criar-tarifa-dao`. **Não trabalhar todos sobre a mesma branch.**

## Ciclo de trabalho

1. Abrir/atribuir uma Issue com requisitos, dependências e critérios de aceitação.
2. Actualizar a branch a partir de `develop`.
3. Implementar uma funcionalidade pequena, compilar e testar localmente.
4. Fazer commits próprios; nunca inserir autores/coautores que não tenham contribuído.
5. Abrir Pull Request da branch para `develop`, usar o template e anexar screenshots da UI se necessário.
6. Conseguir revisão de pelo menos outro elemento (para mudanças estruturais: Opção 1).
7. Corrigir comentários; integrar somente quando o projecto compila e a funcionalidade funciona.
8. Periodicamente promover `develop` para `main` após teste completo.

### Comandos de referência

```bash
git checkout develop
git pull origin develop
git checkout -b feature/42-criar-tarifa-dao
# ... desenvolver, testar, adicionar ficheiros necessários ...
git add .
git commit -m "feat(tarifa): consultar tarifa vigente"
git push -u origin feature/42-criar-tarifa-dao
```

`git add .` deve ser precedido por `git status` para garantir que não inclui ficheiros secretos ou gerados.

## GitHub Projects

Estados recomendados: `Todo`, `In Progress`, `In Review`, `Done`. Uma Issue em `Done` deve ter PR integrado, testes e critérios de aceitação cumpridos. Etiquetas: `opcao-1` ... `opcao-6`, `swing`, `jdbc`, `bug`, `integration`, `documentation`.

## Regras adicionais

- Não editar `main` directamente.
- Não fazer `force push` em branches partilhadas sem decisão colectiva.
- Nunca publicar `application.properties` com passwords, dumps reais ou pastas `target/`.
- Combinar mudança de `pom.xml`, esquema SQL e interfaces partilhadas.
- Quando duas pessoas editam a mesma classe, definir quem é o responsável principal pelo ficheiro.
