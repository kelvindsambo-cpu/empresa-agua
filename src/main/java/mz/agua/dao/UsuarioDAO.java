package mz.agua.dao;

import mz.agua.exception.PersistenciaException;
import mz.agua.exception.ValidacaoException;
import mz.agua.model.Perfil;
import mz.agua.model.Usuario;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * Acesso à tabela utilizador (só SQL, mapeamento e tradução de erros; sem Swing).
 * Recebe a Connection do Service, para participar na mesma transacção que outros DAOs.
 * Não abre nem fecha a ligação.
 */
public class UsuarioDAO {

    /** Dados mínimos de funcionario necessários para criar contas (ver nota abaixo). */
    public record FuncionarioBasico(int idFuncionario, String nome, String cargo) { }

    private static final int ERRO_DUPLICADO = 1062;
    private static final int ERRO_FK = 1452;
    private static final int ERRO_TABELA_INEXISTENTE = 1146;

    private static final String SELECT_BASE = """
            SELECT u.id_utilizador, u.username, u.password_hash, u.perfil, u.activo, u.id_funcionario,
                   f.nome AS nome_funcionario, f.cargo AS cargo_funcionario
              FROM utilizador u
              JOIN funcionario f ON f.id_funcionario = u.id_funcionario
            """;

    private final Connection ligacao;

    public UsuarioDAO(Connection ligacao) {
        this.ligacao = Objects.requireNonNull(ligacao, "ligacao");
    }

    // ------------------------------------------------------------------ consultas

    /** Conta pelo username (comparação sem distinção de maiúsculas, pela collation da BD). */
    public Optional<Usuario> buscarPorUsername(String username) {
        return buscarUm(SELECT_BASE + " WHERE u.username = ?", ps -> ps.setString(1, username), "buscar conta");
    }

    public Optional<Usuario> buscarPorId(int idUtilizador) {
        return buscarUm(SELECT_BASE + " WHERE u.id_utilizador = ?", ps -> ps.setInt(1, idUtilizador), "buscar conta");
    }

    public Optional<Usuario> buscarPorFuncionario(int idFuncionario) {
        return buscarUm(SELECT_BASE + " WHERE u.id_funcionario = ?", ps -> ps.setInt(1, idFuncionario), "buscar conta");
    }

    /** Todas as contas, SEM o hash. */
    public List<Usuario> listar() {
        List<Usuario> contas = new ArrayList<>();
        try (PreparedStatement ps = ligacao.prepareStatement(SELECT_BASE + " ORDER BY u.username");
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                contas.add(mapear(rs).semPasswordHash());
            }
            return contas;
        } catch (SQLException e) {
            throw traduzir("listar contas", e);
        }
    }

    public long contarContas() {
        try (Statement st = ligacao.createStatement();
             ResultSet rs = st.executeQuery("SELECT COUNT(*) FROM utilizador")) {
            rs.next();
            return rs.getLong(1);
        } catch (SQLException e) {
            throw traduzir("contar contas", e);
        }
    }

    /*
     * NOTA DE INTEGRAÇÃO: a consulta de funcionário pertence à Opção 5 (FuncionarioDAO,
     * buscarFuncionarioPorId). Este método existe só para a autenticação não depender de uma
     * classe ainda inexistente; quando FuncionarioDAO estiver pronto, deve ser substituído.
     */
    public Optional<FuncionarioBasico> buscarFuncionario(int idFuncionario) {
        try (PreparedStatement ps = ligacao.prepareStatement(
                "SELECT id_funcionario, nome, cargo FROM funcionario WHERE id_funcionario = ?")) {
            ps.setInt(1, idFuncionario);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next()
                        ? Optional.of(new FuncionarioBasico(rs.getInt("id_funcionario"),
                                rs.getString("nome"), rs.getString("cargo")))
                        : Optional.empty();
            }
        } catch (SQLException e) {
            throw traduzir("buscar funcionário", e);
        }
    }

    // ------------------------------------------------------------------ escrita

    /** Insere a conta (o hash já vem calculado). Devolve o id_utilizador gerado. */
    public int inserir(Usuario conta) {
        String sql = "INSERT INTO utilizador (username, password_hash, perfil, activo, id_funcionario) "
                + "VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement ps = ligacao.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, conta.getUsername());
            ps.setString(2, conta.getPasswordHash());
            ps.setString(3, conta.getPerfil().getValorBd());
            ps.setBoolean(4, conta.isActivo());
            ps.setInt(5, conta.getIdFuncionario());
            ps.executeUpdate();
            try (ResultSet chaves = ps.getGeneratedKeys()) {
                chaves.next();
                return chaves.getInt(1);
            }
        } catch (SQLException e) {
            throw traduzir("criar conta", e);
        }
    }

    public void actualizarActivo(int idUtilizador, boolean activo) {
        executarActualizacao("UPDATE utilizador SET activo = ? WHERE id_utilizador = ?",
                ps -> { ps.setBoolean(1, activo); ps.setInt(2, idUtilizador); }, "actualizar estado da conta");
    }

    public void actualizarPasswordHash(int idUtilizador, String novoHash) {
        executarActualizacao("UPDATE utilizador SET password_hash = ? WHERE id_utilizador = ?",
                ps -> { ps.setString(1, novoHash); ps.setInt(2, idUtilizador); }, "redefinir palavra-passe");
    }

    // ------------------------------------------------------------------ interno

    @FunctionalInterface
    private interface Parametros {
        void aplicar(PreparedStatement ps) throws SQLException;
    }

    private Optional<Usuario> buscarUm(String sql, Parametros parametros, String acao) {
        try (PreparedStatement ps = ligacao.prepareStatement(sql)) {
            parametros.aplicar(ps);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? Optional.of(mapear(rs)) : Optional.empty();
            }
        } catch (SQLException e) {
            throw traduzir(acao, e);
        }
    }

    private void executarActualizacao(String sql, Parametros parametros, String acao) {
        try (PreparedStatement ps = ligacao.prepareStatement(sql)) {
            parametros.aplicar(ps);
            if (ps.executeUpdate() == 0) {
                throw new ValidacaoException("A conta de utilizador indicada não existe.");
            }
        } catch (SQLException e) {
            throw traduzir(acao, e);
        }
    }

    private static Usuario mapear(ResultSet rs) throws SQLException {
        return new Usuario(
                rs.getInt("id_utilizador"),
                rs.getString("username"),
                rs.getString("password_hash"),
                Perfil.deValorBd(rs.getString("perfil")),
                rs.getBoolean("activo"),
                rs.getInt("id_funcionario"),
                rs.getString("nome_funcionario"),
                rs.getString("cargo_funcionario"));
    }

    /** Erros de dados → ValidacaoException; os restantes → PersistenciaException. */
    private static RuntimeException traduzir(String acao, SQLException e) {
        String detalhe = e.getMessage() == null ? "" : e.getMessage();
        switch (e.getErrorCode()) {
            case ERRO_DUPLICADO:
                if (detalhe.contains("uq_utilizador_username")) {
                    return new ValidacaoException("Já existe uma conta com esse nome de utilizador.");
                }
                if (detalhe.contains("uq_utilizador_funcionario")) {
                    return new ValidacaoException("Este funcionário já tem uma conta de utilizador.");
                }
                return new ValidacaoException("Já existe uma conta com esses dados.");
            case ERRO_FK:
                return new ValidacaoException("O funcionário indicado não existe.");
            case ERRO_TABELA_INEXISTENTE:
                return new PersistenciaException(
                        "A tabela 'utilizador' não existe. Execute database/02_autenticacao.sql.", e);
            default:
                return new PersistenciaException("Não foi possível " + acao + ".", e);
        }
    }
}
