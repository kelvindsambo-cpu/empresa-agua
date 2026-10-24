package mz.agua.config;

import mz.agua.exception.PersistenciaException;
import mz.agua.util.Sessao;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Ponto ÚNICO de acesso à base de dados. Todos os módulos obtêm ligações aqui.
 *
 * <p>Regras:
 * <ul>
 *   <li>{@link #getConnection()} devolve sempre uma ligação NOVA; quem a pede fecha-a.
 *       Não existe ligação global aberta.</li>
 *   <li>As credenciais nunca estão no código: vêm de variáveis de ambiente
 *       (AGUA_DB_URL, AGUA_DB_USER, AGUA_DB_PASSWORD) ou de application.properties
 *       (ficheiro local, ignorado pelo Git). As variáveis de ambiente têm prioridade.</li>
 *   <li>Se houver sessão iniciada, cada ligação recebe {@code SET @app_utilizador = <username>},
 *       que os triggers de auditoria do Anexo A/02 usam (a variável é por ligação MySQL).</li>
 * </ul>
 *
 * <p>Para operações compostas use {@link #emTransaccao(Trabalho)}: abre, faz commit/rollback e
 * fecha. As transacções de escrita usam READ COMMITTED, como nos Anexos B e C.
 */
public final class DatabaseConnection {

    private static final Logger LOG = Logger.getLogger(DatabaseConnection.class.getName());

    private static final String ENV_URL = "AGUA_DB_URL";
    private static final String ENV_USER = "AGUA_DB_USER";
    private static final String ENV_PASSWORD = "AGUA_DB_PASSWORD";
    private static final String ENV_CONFIG = "AGUA_CONFIG";          // caminho de um ficheiro .properties
    private static final String PROP_CONFIG = "agua.config";         // idem, via -Dagua.config=...
    private static final String FICHEIRO_PADRAO = "application.properties";

    private static final String MSG_ERRO_BD =
            "Não foi possível concluir a operação na base de dados. Tente novamente.";

    /** Trabalho a executar com uma ligação (pode lançar SQLException). */
    @FunctionalInterface
    public interface Trabalho<T> {
        T executar(Connection ligacao) throws SQLException;
    }

    private record Configuracao(String url, String utilizador, String password) { }

    private static Configuracao configuracao;   // carregada uma vez, protegida por "this" da classe

    private DatabaseConnection() { }

    // ------------------------------------------------------------------ API pública

    /** Ligação nova (autocommit activo). O chamador é responsável por a fechar. */
    public static Connection getConnection() {
        Configuracao cfg = configuracao();
        try {
            Connection ligacao = DriverManager.getConnection(cfg.url(), cfg.utilizador(), cfg.password());
            try {
                definirUtilizadorDeAuditoria(ligacao);
            } catch (SQLException | RuntimeException e) {
                fechar(ligacao);
                throw e;
            }
            return ligacao;
        } catch (SQLException e) {
            throw new PersistenciaException(
                    "Não foi possível ligar à base de dados. Verifique a configuração e se o servidor MariaDB/MySQL está activo.", e);
        }
    }

    /** Executa leituras ou uma única instrução em autocommit; abre e fecha a ligação. */
    public static <T> T executar(Trabalho<T> trabalho) {
        Connection ligacao = getConnection();
        try {
            return trabalho.executar(ligacao);
        } catch (SQLException e) {
            throw new PersistenciaException(MSG_ERRO_BD, e);
        } finally {
            fechar(ligacao);
        }
    }

    /** Transacção com READ COMMITTED (nível usado nas transacções de escrita dos Anexos B e C). */
    public static <T> T emTransaccao(Trabalho<T> trabalho) {
        return emTransaccao(Connection.TRANSACTION_READ_COMMITTED, trabalho);
    }

    /**
     * Transacção com o nível de isolamento indicado (constantes de {@link Connection}).
     * Commit se o trabalho terminar; rollback se lançar qualquer excepção (que é relançada,
     * sendo SQLException convertida em {@link PersistenciaException}).
     */
    public static <T> T emTransaccao(int isolamento, Trabalho<T> trabalho) {
        Connection ligacao = getConnection();
        try {
            ligacao.setTransactionIsolation(isolamento);
            ligacao.setAutoCommit(false);
            T resultado = trabalho.executar(ligacao);
            ligacao.commit();
            return resultado;
        } catch (SQLException e) {
            reverter(ligacao);
            throw new PersistenciaException(MSG_ERRO_BD, e);
        } catch (RuntimeException e) {
            reverter(ligacao);
            throw e;
        } finally {
            fechar(ligacao);
        }
    }

    /** Teste simples de ligação (SELECT 1). Lança PersistenciaException se falhar. */
    public static boolean testarLigacao() {
        return executar(c -> {
            try (Statement st = c.createStatement(); ResultSet rs = st.executeQuery("SELECT 1")) {
                return rs.next() && rs.getInt(1) == 1;
            }
        });
    }

    // ------------------------------------------------------------------ interno

    private static void definirUtilizadorDeAuditoria(Connection ligacao) throws SQLException {
        String username = Sessao.getUsernameOuNull();
        if (username != null) {
            try (PreparedStatement ps = ligacao.prepareStatement("SET @app_utilizador = ?")) {
                ps.setString(1, username);
                ps.execute();
            }
        }
    }

    private static synchronized Configuracao configuracao() {
        if (configuracao == null) {
            configuracao = carregarConfiguracao();
        }
        return configuracao;
    }

    private static Configuracao carregarConfiguracao() {
        Properties props = new Properties();
        carregarFicheiro(props);

        String url = valor(ENV_URL, props.getProperty("db.url"));
        String utilizador = valor(ENV_USER, props.getProperty("db.user"));
        String password = valor(ENV_PASSWORD, props.getProperty("db.password"));

        if (url == null || url.isBlank() || utilizador == null || utilizador.isBlank()) {
            throw new PersistenciaException(
                    "Configuração da base de dados em falta. Copie application.properties.example para "
                            + "application.properties e preencha db.url, db.user e db.password "
                            + "(ou defina AGUA_DB_URL, AGUA_DB_USER e AGUA_DB_PASSWORD).", null);
        }
        return new Configuracao(url.trim(), utilizador.trim(), password == null ? "" : password);
    }

    /** Procura, por ordem: -Dagua.config, AGUA_CONFIG, ./application.properties, classpath. */
    private static void carregarFicheiro(Properties props) {
        String explicito = valor(ENV_CONFIG, System.getProperty(PROP_CONFIG));
        try {
            if (explicito != null && !explicito.isBlank()) {
                try (InputStream in = Files.newInputStream(Path.of(explicito.trim()))) {
                    props.load(in);
                }
                return;
            }
            Path local = Path.of(FICHEIRO_PADRAO);
            if (Files.isRegularFile(local)) {
                try (InputStream in = Files.newInputStream(local)) {
                    props.load(in);
                }
                return;
            }
            try (InputStream in = DatabaseConnection.class.getResourceAsStream("/" + FICHEIRO_PADRAO)) {
                if (in != null) {
                    props.load(in);
                }
            }
        } catch (IOException e) {
            // Não regista o conteúdo do ficheiro (pode ter credenciais), só o facto.
            throw new PersistenciaException("Não foi possível ler o ficheiro de configuração.", e);
        }
    }

    private static String valor(String variavelAmbiente, String alternativa) {
        String env = System.getenv(variavelAmbiente);
        return (env != null && !env.isBlank()) ? env : alternativa;
    }

    private static void reverter(Connection ligacao) {
        try {
            ligacao.rollback();
        } catch (SQLException e) {
            LOG.log(Level.WARNING, "Falha ao fazer rollback", e);
        }
    }

    private static void fechar(Connection ligacao) {
        try {
            ligacao.close();
        } catch (SQLException e) {
            LOG.log(Level.WARNING, "Falha ao fechar a ligação", e);
        }
    }
}
