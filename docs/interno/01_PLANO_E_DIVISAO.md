# Divisão da equipa — plano simplificado

Mantêm-se as mesmas áreas por membro (ninguém troca de módulo), mas **cada módulo foi reduzido ao essencial**: listar numa `JTable`, inserir/editar num formulário e, quando existe, **uma** regra de negócio que vale a pena mostrar na apresentação.

| Membro | Área | Nível (novo) | Responsável |
|---|---|---|---|
| 1 | Projecto Maven, `DatabaseConnection`, login, sessão, perfis | — (**feito**) | **[NOME 1]** |
| 2 | Tarifas + emissão de facturas | Médio | **[NOME 2]** |
| 3 | Contratos + pagamentos | Médio | **[NOME 3]** |
| 4 | Clientes + contadores | Fácil/médio | **[NOME 4]** |
| 5 | Funcionários + reclamações | Fácil/médio | **[NOME 5]** |
| 6 | `MainFrame` (menu), Dashboard, dados de demonstração, guia de execução | Fácil | **[NOME 6]** |

## O que cada um demonstra na apresentação

| Membro | Demonstração |
|---|---|
| 1 | Login válido/inválido, ADMIN vs OPERADOR, logout, ligação JDBC central |
| 2 | Listar/criar tarifa; emitir factura e mostrar o cálculo (`leitura → consumo → valor`) |
| 3 | Criar contrato e mudar estado; registar pagamento parcial e ver a factura passar a `paga` |
| 4 | Registar/pesquisar/editar cliente; registar contador num contrato |
| 5 | Registar/editar funcionário (só ADMIN); abrir reclamação e mudar estado até `resolvida` |
| 6 | Navegar entre módulos pelo menu; Dashboard com 4 números reais da BD |

## Fora do âmbito (não fazer)

- Multas automáticas, corte e religação automáticos (RN24–31 só na parte do saldo).
- Editar ou apagar tarifas. Só se **cria** tarifa nova (que fecha automaticamente a vigente do mesmo tipo — ver Membro 2).
- Troca de contador "automática" (desactivar um e activar outro numa transacção). Basta mudar o estado à mão.
- Concorrência (`SELECT ... FOR UPDATE`, testes com duas sessões).
- Anexo C, `log_transacao`, níveis de isolamento.
- Ecrã de gestão de contas de utilizador (contas criadas pelo `CriarAdminInicial`/SQL do Membro 1).
- Branch `develop`, GitHub Projects, Issues obrigatórias.
- `SwingWorker` em todo o lado (só onde já existe, no login).
- Apagar registos (DELETE). Não há botões "Eliminar".
- Testes JUnit para tudo — só para o cálculo da factura (ver [`06_TESTES_E_CRITERIOS.md`](06_TESTES_E_CRITERIOS.md)).

## Estrutura de cada módulo (igual para todos)

Para cada entidade:

```text
model/Cliente.java            atributos + getters/setters (sem Swing, sem SQL)
dao/ClienteDAO.java           listar, buscarPorId, pesquisar, inserir, actualizar
controller/ClienteController  lê o formulário, valida o básico, chama o DAO, actualiza a tabela
view/ClientePanel.java        JTable + campo de pesquisa + botões Novo/Editar
view/ClienteDialog.java       formulário (JDialog modal)
```

Camada `service/` **só** onde há uma regra que envolve mais de uma tabela: `FaturaService` (Membro 2) e `PagamentoService` (Membro 3). Nos restantes, o Controller chama directamente o DAO.

## Ordem de integração

`cliente` → `contrato` → `contador` → `fatura` → `pagamento`. Para não ficarem à espera uns dos outros, **usem os dados de exemplo do Anexo A** (já há clientes, contratos, contadores, tarifas e funcionários) enquanto o módulo do colega não chega a `main`.
