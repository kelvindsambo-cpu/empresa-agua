# O que cada módulo precisa dos outros (versão curta)

Regra geral: **se precisas de dados de outra tabela só para preencher uma `JComboBox`, pede ao dono o método abaixo.** Se o método ainda não estiver em `main`, escreve temporariamente a consulta no teu DAO e troca-a quando o colega integrar — não fiques à espera.

Todos os DAO recebem a `Connection` no construtor (ver [`02_PADROES_CODIGO.md`](02_PADROES_CODIGO.md)).

| Dono | Método | Quem usa |
|---|---|---|
| Membro 4 | `ClienteDAO.listar()` / `buscarPorId(int)` | 3 (contrato), 5 (reclamação) |
| Membro 3 | `ContratoDAO.listarActivos()` / `buscarPorId(int)` | 2 (factura), 4 (contador) |
| Membro 4 | `ContadorDAO.buscarActivoPorContrato(int idContrato)` | 2 (factura) |
| Membro 5 | `FuncionarioDAO.listarPorCargo(String cargo)` | 2 (técnicos `tecnico_leitura`), 5 |
| Membro 2 | `TarifaDAO.buscarVigente(String tipoContrato, LocalDate data)` | 2 |
| Membro 2 | `FaturaDAO.listarPendentes()` / `buscarPorId(int)` | 3 (pagamento) |
| Membro 3 | `PagamentoDAO.totalPago(int idFatura)` | 3 |
| Membro 6 | `MainFrame.adicionarModulo(String nome, JPanel painel, boolean soAdmin)` | todos |
| Membro 1 | `Sessao.isAdmin()`, `Sessao.exigirAdmin()`, `Sessao.getIdFuncionarioAutenticado()` | todos |

Os métodos devolvem `List<Modelo>` ou o `Modelo` (ou `null`/`Optional` se não existir — o dono escolhe e documenta no Javadoc).

## Três factos que evitam confusões

- **Tarifa vigente:** `tipo_contrato = ?` e `data_inicio <= data` e `(data_fim IS NULL OR data_fim >= data)`.
- **Saldo de uma factura:** `valor_base + valor_multa - SUM(pagamento.valor)`. Não existe coluna `saldo` nem `valor_pago` no Anexo A.
- **Cargo ≠ perfil:** `funcionario.cargo` (ex.: `tecnico_leitura`) é do negócio; `ADMIN`/`OPERADOR` é do login.
