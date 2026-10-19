# Divisão da equipa — seis opções já atribuídas

Os nomes devem ser preenchidos pelo responsável do projecto; **não trocar de módulo sem informar a equipa**. A divisão mantém **3 difíceis, 2 médias, 1 fácil**. Pretende-se compatibilizar capacidades com complexidade, não igualar a dificuldade. Carga operacional distribuída com apoio da Opção 6.

| Opção | Nível | Área principal | Responsável (preencher) |
|---|---|---|---|
| 1 | Difícil | Arquitectura, JDBC base, autenticação, controlo de acesso | **[NOME 1]** |
| 2 | Difícil | Tarifas e facturação | **[NOME 2]** |
| 3 | Difícil | Contratos e pagamentos | **[NOME 3]** |
| 4 | Médio | Clientes e contadores | **[NOME 4]** |
| 5 | Médio | Funcionários e reclamações | **[NOME 5]** |
| 6 | Fácil + apoio | MainFrame, Dashboard, testes manuais e apoio Swing | **[NOME 6]** |

**Nota:** o coordenador de arquitectura (Opção 1) não é obrigado a escrever os Models dos outros. É responsável pelo esqueleto e pelas decisões partilhadas. Cada um implementa as classes e interfaces do seu módulo.

## Entregas demonstráveis

| Opção | Demonstração obrigatória |
|---|---|
| 1 | Login ADMIN/OPERADOR, logout, permissões funcionais e ligação JDBC |
| 2 | Consultar/gerir tarifas e emitir uma factura com cálculo correcto |
| 3 | Criar/alterar contrato e registar pagamento parcial sem ultrapassar saldo |
| 4 | Registar/pesquisar cliente e instalar/trocar contador respeitando unicidade |
| 5 | Gerir funcionário e reclamação com responsável/estados válidos |
| 6 | Navegar pelos painéis e mostrar Dashboard com contagens reais + checklist de testes |

## Responsabilidade comum a TODOS

- Criar Model, DAO, Controller, View e Service (quando necessário) das suas tarefas.
- Implementar validações e mensagens de erro úteis.
- Testar dados válidos, inválidos e relações com outras tabelas.
- Documentar como testar cada funcionalidade em cada PR.
- Participar nas revisões de código e aprender a explicar a própria implementação.

## Integração entre módulos

`cliente` → `contrato` → `contador` → `fatura` → `pagamento`; tarifa e técnico leitor alimentam facturação; reclamação liga-se a cliente e funcionário; login associa-se a funcionário.

Antes de integrar, combinar: como seleccionar entidades por ID, como carregar JComboBox, quem abre o diálogo, como passar dados entre Controllers e como partilhar Connection numa transacção.
