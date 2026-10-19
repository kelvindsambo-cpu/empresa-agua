# Contratos entre módulos (API interna proposta)

**Objectivo:** evitar que seis elementos inventem assinaturas incompatíveis. Estes métodos são **propostas a validar numa reunião curta antes de programar**; após aprovação, qualquer mudança deve passar por revisão.

## Princípio de colaboração

As Views trabalham com IDs seleccionados e objectos Model, não com SQL de outro módulo. O Controller usa Service/DAO do dono do módulo. O DAO pode receber uma `Connection` criada pela camada de serviço, permitindo que vários DAOs participem na mesma transacção JDBC.

**Convenção proposta de ciclo de ligação:** `DatabaseConnection.getConnection()` devolve uma conexão nova. Serviços de operações compostas obtêm a conexão, criam os DAOs necessários com ela, controlam commit/rollback e garantem o fecho. Serviços simples podem chamar um DAO com Connection própria. **Não criar diferentes modelos de gestão de Connection por módulo.**

## Interfaces de leitura que outros módulos precisam

| Dono | Quem consome | Operação (assinatura conceptual) |
|---|---|---|
| Opção 4 (Clientes) | 3, 5, 6 | `buscarClientePorId(int id)`; `pesquisarClientes(String termo)` |
| Opção 3 (Contratos) | 2, 4 | `buscarContratoPorId(int id)`; `listarContratosActivos()`; `listarPorCliente(int idCliente)` |
| Opção 4 (Contadores) | 2, 3 | `buscarContadorActivo(int idContrato)`; `buscarContadorPorId(int id)` |
| Opção 5 (Funcionários) | 1, 2, 3 | `buscarFuncionarioPorId(int id)`; `listarTecnicosLeitura()` |
| Opção 2 (Tarifas) | 2 (Factura) | `buscarTarifaVigente(String tipoContrato, LocalDate dataLeitura)` |
| Opção 2 (Facturas) | 3, 6 | `buscarFaturaPorId(int id)`; `listarFaturasPorContrato(int idContrato)`; `obterLeituraAnterior(int idContador)` |
| Opção 3 (Pagamentos) | 2, 6 | `obterTotalPago(int idFatura)`; `obterSaldo(int idFatura)` |
| Opção 1 (Sessão) | Todos | `getFuncionarioAutenticado()`; `isAdmin()`; `terminarSessao()` |
| Opção 6 (Navegação) | Todos | `abrirModulo(String chave)` e convenção dos IDs de cartões |

As assinaturas finais devem indicar tipos de retorno (`Optional<T>`, `List<T>`, `BigDecimal`, etc.), tipos de excepção e onde o `Connection` é recebido. **Não tratar esta tabela como código pronto para compilar.**

## Contratos de dados críticos

### Tarifa

`tipo_contrato` é `'residencial'` ou `'comercial'`. A tarifa válida em `data_leitura` satisfaz `data_inicio <= data` e `(data_fim IS NULL OR data_fim >= data)`. `data_fim` é inclusiva; não criar sobreposição de intervalos. Tarifas históricas usadas em facturas não mudam retroactivamente.

### Factura

A emissão precisa de contrato activo, contador activo, período e técnico de leitura; a leitura anterior vem da factura precedente ou da leitura inicial. Não existem `consumo_m3`, `valor_pago`, `valor_total` nem `saldo_em_divida` persistidos no esquema-base. A Opção 2 disponibiliza dados necessários para a Opção 3 pagar a factura.

### Pagamento

O saldo deve ser recalculado dentro da transacção após bloquear a factura (`SELECT ... FOR UPDATE`). Nunca confiar só no saldo mostrado anteriormente na UI. No Anexo A, soma de `pagamento.valor` alimenta o saldo, mas o Anexo C muda a estratégia — consultar [`database/README.md`](../../database/README.md).

### Funcionário vs Utilizador

`funcionario.cargo` determina funções de negócio como técnico de leitura. `utilizador.perfil` determina autorização para aceder à aplicação. São conceitos independentes.

## Regras de integração

1. Abrir Issue para dependência ainda não definida.
2. Publicar exemplos de entradas, saídas e erros.
3. Rever com o responsável consumidor **antes** de mudar interface já usada.
4. Preferir IDs/Models estáveis; não passar referências a componentes Swing entre módulos.
5. Integrar alterações pequenas, não esperar pelos seis módulos terminados.
