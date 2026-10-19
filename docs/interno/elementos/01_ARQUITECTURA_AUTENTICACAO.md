# Opção 1 — Arquitectura, JDBC e autenticação

**Nível:** difícil  
**Responsável:** [NOME]  
**Branch sugerida:** `feature/autenticacao`

## Resultado esperado

Projecto Maven compila e arranca; JDBC comunica com MySQL; funcionários autorizados fazem login; ADMIN e OPERADOR vêem/acedem apenas às operações permitidas; existe um ponto central para conexão e sessão.

## Classes e ficheiros sob a tua responsabilidade

```text
pom.xml
src/main/java/mz/agua/Main.java
src/main/java/mz/agua/config/DatabaseConnection.java
src/main/java/mz/agua/model/Usuario.java
src/main/java/mz/agua/dao/UsuarioDAO.java
src/main/java/mz/agua/controller/LoginController.java
src/main/java/mz/agua/service/AutenticacaoService.java
src/main/java/mz/agua/view/LoginFrame.java
src/main/java/mz/agua/util/Sessao.java
# componentes de autorização/perfis, se necessários
database/02_autenticacao.sql
```

## Tarefas

- [ ] Criar `pom.xml`, packages, `.gitignore`, `Main.java` e dependência JDBC.
- [ ] Definir como criar e fechar Connection e como gerir transacções multi-DAO.
- [ ] Criar extensão SQL `utilizador`, ligada a `funcionario` por FK única; `username` único, hash de password, perfil e estado.
- [ ] Implementar autenticação segura (hash apropriado), conta inactiva e saída da sessão.
- [ ] Implementar controlo de acesso nos Controllers/Services; não apenas ocultar botões.
- [ ] Confirmar com a Opção 5 métodos de consulta de funcionário.
- [ ] Definir e publicar contratos de integração com todas as opções.
- [ ] Rever PRs estruturais (`pom.xml`, schema, packages, Connection).
- [ ] Testar login válido, inválido, conta inactiva e permissões.

## Dependências

Opção 5 fornece `FuncionarioDAO`/modelo; Opção 6 fornece `MainFrame` e navegação. Tu forneces ligação JDBC e sessão a todos.

## Critérios de aceitação

- Login consulta efectivamente o MySQL.
- Password não está em texto simples no esquema, no código ou nos logs.
- ADMIN/OPERADOR bloqueiam operações mesmo se se tentar invocar o Controller directamente.
- `DatabaseConnection` é utilizado pelos restantes módulos sem configurações duplicadas.
- Maven compila a estrutura inicial; não existem credenciais versionadas.

**Não fazer:** concentrar todas as funcionalidades num único Controller, alterar entidades de outros membros sem acordo ou criar classes fictícias apenas para a interface parecer funcional.
