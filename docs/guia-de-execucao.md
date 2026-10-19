# Guia de instalação e execução (a validar na Fase 1)

Este documento deve ser completado com **comandos testados**, depois da criação do projecto Maven.

## Pré-requisitos

- JDK: **versão a fixar no `pom.xml`** (recomendação: JDK 21 LTS se disponível para todos).
- Maven: versão compatível com o JDK escolhido.
- MySQL 8+ local.
- IDE: IntelliJ IDEA, Eclipse ou VS Code com suporte a Java.

## Preparar a base de dados

1. Criar cópia de segurança de qualquer base importante: o Anexo A contém `DROP DATABASE IF EXISTS fornecimento_agua`.
2. Executar o Anexo A **apenas numa instalação/base de desenvolvimento apropriada**.
3. Executar o script `02_autenticacao.sql` quando estiver revisto e integrado.
4. **Não misturar** Anexo A com alterações de optimização do Anexo C sem a decisão descrita em [`database/README.md`](../database/README.md).
5. Verificar uma consulta simples à tabela `cliente` e a existência das tabelas esperadas.

## Configuração JDBC

- O projecto deve fornecer `src/main/resources/application.properties.example` **sem credenciais reais**.
- A configuração local deve ficar fora do controlo de versões (ou usar variáveis de ambiente).
- A classe `DatabaseConnection` não deve codificar utilizador/palavra-passe no código-fonte.

## Executar (só após validar a configuração)

```bash
mvn clean test
```

A execução da aplicação a partir da linha de comandos dependerá da configuração do `pom.xml` e da classe `mz.agua.Main`. A equipa deve escrever aqui **o comando realmente testado**, sem inventar instruções que ainda não foram configuradas.

## Problemas frequentes

- *Access denied*: credenciais/permissões MySQL incorrectas.
- *Unknown database/table*: SQL ainda não aplicado ou esquema diferente.
- *No suitable driver*: dependência JDBC em falta no `pom.xml`.
- *Communications link failure*: MySQL parado, porta/host errados.
- *SQLIntegrityConstraintViolationException*: validar chaves estrangeiras, UNIQUE e CHECK.

Para reproduzir um erro, guardar texto da mensagem e passos executados, mas **não expor passwords nem dados sensíveis**.
