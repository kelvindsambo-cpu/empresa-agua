# Decisões

Com a simplificação, as decisões que estavam pendentes ficam **fechadas** assim (o grupo pode mudar, mas avisa todos):

| Decisão | Fica |
|---|---|
| Variante de BD | **Anexo A puro** + `utilizador` (sem Anexo C) |
| JDK / build | JDK 21, Maven (já no `pom.xml` do Membro 1) |
| Base de dados | MySQL 8+ ou MariaDB (driver MariaDB já incluído) |
| Ligação JDBC | `DatabaseConnection.executar` / `emTransaccao`; DAO recebe `Connection` no construtor |
| Erros | `ValidacaoException` (regras), `PersistenciaException` (BD), `AcessoNegadoException` (permissões) |
| ENUM do SQL em Java | `String` + `JComboBox` com os valores exactos (sem enums Java novos) |
| Camada Service | só `FaturaService` e `PagamentoService` |
| Primeiro contrato de um cliente | duas acções separadas: registar cliente (Membro 4), depois criar contrato escolhendo o cliente (Membro 3) |
| Técnico da factura | escolhido numa combo com funcionários `tecnico_leitura` (pode não ser quem está logado) |
| Multas, corte, religação | **fora do âmbito**; `valor_multa` = 0 |
| Estado `vencida` da factura | fora do âmbito (só `pendente` → `paga`) |
| Contas de utilizador | criadas pelo `CriarAdminInicial`/SQL; sem ecrã próprio |
| `log_transacao` | não usado |
| Estilo visual | o do `LoginFrame` serve de referência; Membro 6 dá cores/tamanhos comuns |
| Branches | `main` + uma branch por membro; PR com 1 revisor |

## Ainda por confirmar

- [ ] Nomes dos responsáveis em [`01_PLANO_E_DIVISAO.md`](01_PLANO_E_DIVISAO.md).
- [ ] Data do ensaio geral.
