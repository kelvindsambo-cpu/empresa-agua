# Convenções técnicas (simplificadas)

## Nomes e pacotes

- Package raiz: `mz.agua`. Pastas: `model`, `dao`, `service` (só Membros 2 e 3), `controller`, `view`.
- Classes: `Cliente`, `ClienteDAO`, `ClienteController`, `ClientePanel`, `ClienteDialog`.
- Métodos/campos em `camelCase` (`idCliente`, `precoPorM3`); colunas SQL iguais ao Anexo A (`id_cliente`).
- Nomes de classes sem acentos; textos mostrados ao utilizador podem ter acentos.

## Tipos

| SQL | Java |
|---|---|
| `INT` | `int` (ou `Integer` se puder ser `NULL`) |
| `VARCHAR`, `TEXT`, `ENUM` | `String` (os ENUM escolhem-se numa `JComboBox` com os valores exactos) |
| `DECIMAL` | `BigDecimal` |
| `DATE` | `LocalDate` (`rs.getObject("data_inicio", LocalDate.class)` / `ps.setObject(i, data)`) |

**Decisão:** ENUM do SQL ficam como `String` em Java — não criar enums Java (excepto `Perfil`, que já existe). A `JComboBox` garante que só se escolhem valores válidos.

## O que o Membro 1 já deixou pronto (usar, não refazer)

| Classe | Para quê |
|---|---|
| `config.DatabaseConnection` | `executar(c -> ...)` para leituras/escritas simples; `emTransaccao(c -> ...)` para operações com commit/rollback |
| `util.Sessao` | `Sessao.isAdmin()`, `Sessao.exigirAdmin()`, `Sessao.getIdFuncionarioAutenticado()` |
| `exception.ValidacaoException` | erro de regra/validação com mensagem para o utilizador |
| `exception.PersistenciaException` | erro de BD (já traz mensagem amigável) |
| `exception.AcessoNegadoException` | operação sem permissão |
| `Main.terminarSessaoEVoltarAoLogin()` | logout (o `MainFrame` chama isto) |

Configuração local: ver [guia de execução](../guia-de-execucao.md). Cada um tem o seu `application.properties` (não vai para o Git).

## Exemplo completo de DAO (copiar este estilo)

O DAO recebe a `Connection` no construtor (igual ao `UsuarioDAO`) e **não** a abre nem fecha.

```java
public class ClienteDAO {
    private final Connection ligacao;

    public ClienteDAO(Connection ligacao) { this.ligacao = ligacao; }

    public List<Cliente> pesquisar(String termo) throws SQLException {
        String sql = "SELECT * FROM cliente WHERE nome_completo LIKE ? ORDER BY nome_completo";
        try (PreparedStatement ps = ligacao.prepareStatement(sql)) {
            ps.setString(1, "%" + termo + "%");
            try (ResultSet rs = ps.executeQuery()) {
                List<Cliente> lista = new ArrayList<>();
                while (rs.next()) {
                    lista.add(mapear(rs));
                }
                return lista;
            }
        }
    }

    public void inserir(Cliente c) throws SQLException {
        String sql = "INSERT INTO cliente (nome_completo, nuit, telefone, rua, numero_casa, bairro) VALUES (?,?,?,?,?,?)";
        try (PreparedStatement ps = ligacao.prepareStatement(sql)) {
            ps.setString(1, c.getNomeCompleto());
            ps.setString(2, c.getNuit());          // pode ser null
            // ...
            ps.executeUpdate();
        }
    }

    private Cliente mapear(ResultSet rs) throws SQLException { /* rs.getInt("id_cliente"), ... */ }
}
```

## Exemplo de Controller

```java
// carregar a tabela
List<Cliente> clientes = DatabaseConnection.executar(c -> new ClienteDAO(c).pesquisar(termo));
painel.mostrarClientes(clientes);

// guardar
try {
    validar(cliente);                                  // ifs simples -> throw new ValidacaoException("...")
    DatabaseConnection.executar(c -> { new ClienteDAO(c).inserir(cliente); return null; });
    JOptionPane.showMessageDialog(painel, "Cliente registado.");
} catch (ValidacaoException | PersistenciaException | AcessoNegadoException e) {
    JOptionPane.showMessageDialog(painel, e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
}
```

## Validações: Java primeiro, BD como rede de segurança

- No Java: campos obrigatórios, números positivos, datas coerentes e a regra principal do módulo.
- A BD já tem `CHECK`/`UNIQUE`/triggers (ex.: NUIT único, um contador activo por contrato, técnico de leitura, sobreposição de tarifas). Se algo escapar, a BD recusa e o utilizador vê a mensagem da `PersistenciaException`. **Não é preciso repetir todas as regras em Java.**

## Swing

- Uma só janela principal (`MainFrame`, Membro 6) com `CardLayout`. Cada módulo entrega um `JPanel`.
- Formulários em `JDialog` modal. Listas em `JTable` dentro de `JScrollPane`.
- Mensagens com `JOptionPane`.
- Botões só de ADMIN: esconder com `Sessao.isAdmin()` **e** chamar `Sessao.exigirAdmin()` no Controller antes de gravar (uma linha).

## Git

Commits pequenos e com mensagem clara: `feat(cliente): pesquisar por nome`, `fix(fatura): vencimento no dia 10`.
