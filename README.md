# Sistema de Gestão de Fornecimento de Água — POO II

Aplicação **desktop em Java** para funcionários de uma empresa de fornecimento de água na **Província de Maputo (exclui a Cidade de Maputo)**. O negócio e o esquema relacional têm origem no projecto de Base de Dados II.

> **Estado:** em preparação/desenvolvimento. Este repositório não corresponde ainda a uma aplicação concluída. As funcionalidades abaixo são o **âmbito planeado**, não uma afirmação de implementação.

## Tecnologias e objectivo

- Java e Java Swing: interface desktop (JFrame, JPanel, JTable, JDialog, eventos e navegação).
- JDBC e MySQL 8+: persistência real de dados, consultas parametrizadas e transacções.
- MVC + DAO; camada Service para regras e operações envolvendo mais de uma entidade.
- Maven: construção e dependências; SwingWorker para consultas lentas fora da Event Dispatch Thread (EDT).

**Utilizadores da aplicação:** apenas funcionários, com perfis `ADMIN` e `OPERADOR` (perfis aplicacionais, diferentes dos cargos existentes na tabela `funcionario`). Clientes não fazem login.

## Funcionalidades previstas

Autenticação; clientes; contratos; contadores; tarifas; facturação; pagamentos; reclamações; funcionários/contas; Dashboard. As leituras são atributos da `fatura`, não uma tabela separada. Pagamentos por M-Pesa/e-Mola são **registos** de pagamentos, não integração com os provedores.

## Documentação

- [Arquitectura e pacotes](docs/arquitectura.md)
- [Casos de uso e permissões](docs/casos-de-uso.md)
- [Guia de execução](docs/guia-de-execucao.md)
- [Informação sobre a base de dados](database/README.md) e [anexos SQL de BD II](database/bdii/)
- [Guia interno da equipa (temporário)](docs/interno/00_COMECAR_AQUI.md)
- [Manual completo da equipa numa só página](docs/interno/MANUAL_COMPLETO_EQUIPA.md)

## Iniciar o projecto (quando a configuração estiver concluída)

1. Instalar o JDK e Maven nas versões que a equipa fixar no `pom.xml`.
2. Instalar MySQL 8+; preparar **uma base de dados local de desenvolvimento**, separada de qualquer base importante.
3. Seguir a [configuração dos scripts SQL](database/README.md).
4. Copiar `application.properties.example` para o local definido pela equipa e configurar credenciais **apenas localmente**, nunca no Git.
5. Abrir o `pom.xml` no IntelliJ IDEA ou noutra IDE e seguir os comandos indicados em [guia de execução](docs/guia-de-execucao.md).

Os comandos específicos da classe principal e das dependências só serão indicados como definitivos **depois** de `pom.xml`, `Main.java` e os testes de arranque estarem presentes.

## Equipa

Seis elementos, responsáveis por módulos funcionais. Atribuição nominal e tarefas encontram-se na documentação interna durante o desenvolvimento.

## Notas de segurança

Não publicar palavras-passe, credenciais de bases de dados, backups com dados pessoais ou ficheiros de configuração locais. Os logs não devem guardar passwords. A eliminação de ficheiros do repositório **não apaga automaticamente o histórico de commits**.
