# Membro 1 — Base do projecto, JDBC e autenticação

**Estado:** feito (branch `autenticacao`)  
**Responsável:** [NOME]

## O que já existe

```text
pom.xml (JDK 21, driver MariaDB, bcrypt, JUnit 5)
Main.java                        arranque, login, logout (terminarSessaoEVoltarAoLogin)
config/DatabaseConnection.java   getConnection, executar, emTransaccao, testarLigacao
model/Usuario.java, Perfil.java
dao/UsuarioDAO.java
service/AutenticacaoService.java, UtilizadorService.java
controller/LoginController.java
view/LoginFrame.java
util/Sessao.java, PasswordHasher.java, CriarAdminInicial.java
exception/ValidacaoException, PersistenciaException, AcessoNegadoException, AutenticacaoException
testes: PerfilTest, PasswordHasherTest, SessaoTest, UtilizadorServiceTest
```

## Falta para os outros conseguirem arrancar

O código refere ficheiros que **não estão no repositório**. Sem eles ninguém consegue fazer login:

- [ ] `database/02_autenticacao.sql` — `CREATE TABLE utilizador (...)` usado pelo `UsuarioDAO`.
- [ ] `application.properties.example` — modelo com `db.url`, `db.user`, `db.password` (sem valores reais).
- [ ] Instruções no [guia de execução](../../guia-de-execucao.md): como correr `CriarAdminInicial` para criar o primeiro ADMIN.
- [ ] Apagar `pom21312.xml` se for um ficheiro de teste esquecido.
- [ ] Abrir Pull Request `autenticacao` → `main`.

## Depois disso

Apoiar os colegas com dúvidas de JDBC/`DatabaseConnection` e rever Pull Requests que toquem em `pom.xml` ou SQL. Não é preciso fazer mais funcionalidades.

## Demonstração

Login válido e inválido; ADMIN vê menus que o OPERADOR não vê; logout; explicar `DatabaseConnection` (ligação nova por operação, credenciais fora do código) e o hash da password.
