package mz.agua.dao;

import mz.agua.model.Fatura;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Acesso à tabela fatura (só SQL e mapeamento; sem Swing).
 * Recebe a Connection de fora e não a abre nem fecha.
 */
public class FaturaDAO {

    /** Dados de um contrato necessários para emitir a factura. */
    public record ContratoResumo(int idContrato, String nomeCliente, String tipoContrato, String estadoContrato) {
        @Override
        public String toString() {
            return "Contrato " + idContrato + " — " + nomeCliente + " (" + tipoContrato + ")";
        }
    }

    /** Contador activo de um contrato. */
    public record ContadorResumo(int idContador, String numeroContador, BigDecimal leituraInicial) { }

    /** Funcionário com cargo tecnico_leitura. */
    public record TecnicoResumo(int idFuncionario, String nome) {
        @Override
        public String toString() {
            return nome;
        }
    }

    private static final String SELECT_BASE = """
            SELECT f.id_fatura, f.mes_referencia, f.ano_referencia, f.data_leitura,
                   f.leitura_anterior, f.leitura_atual, f.data_emissao, f.data_vencimento,
                   f.valor_base, f.valor_multa, f.estado_fatura,
                   f.id_contador, f.id_tarifa, f.id_funcionario,
                   cd.numero_contador, cl.nome_completo
              FROM fatura f
              JOIN contador cd ON cd.id_contador = f.id_contador
              JOIN contrato ct ON ct.id_contrato = cd.id_contrato
              JOIN cliente  cl ON cl.id_cliente  = ct.id_cliente
            """;

    private static final String ORDEM = " ORDER BY f.ano_referencia DESC, f.mes_referencia DESC, f.id_fatura DESC";

    private final Connection ligacao;

    public FaturaDAO(Connection ligacao) {
        this.ligacao = ligacao;
    }

    // ------------------------------------------------------------------ facturas

    public List<Fatura> listar() throws SQLException {
        try (PreparedStatement ps = ligacao.prepareStatement(SELECT_BASE + ORDEM)) {
            return lista(ps);
        }
    }

    /** Facturas por pagar (usado pelos pagamentos, Membro 3). */
    public List<Fatura> listarPendentes() throws SQLException {
        String sql = SELECT_BASE + " WHERE f.estado_fatura = 'pendente'" + ORDEM;
        try (PreparedStatement ps = ligacao.prepareStatement(sql)) {
            return lista(ps);
        }
    }

    public Optional<Fatura> buscarPorId(int idFatura) throws SQLException {
        String sql = SELECT_BASE + " WHERE f.id_fatura = ?";
        try (PreparedStatement ps = ligacao.prepareStatement(sql)) {
            ps.setInt(1, idFatura);
            List<Fatura> encontradas = lista(ps);
            return encontradas.isEmpty() ? Optional.empty() : Optional.of(encontradas.get(0));
        }
    }

    /** Leitura actual da factura mais recente do contador (vazio se ainda não tiver facturas). */
    public Optional<BigDecimal> buscarUltimaLeitura(int idContador) throws SQLException {
        String sql = """
                SELECT leitura_atual FROM fatura
                 WHERE id_contador = ?
                 ORDER BY ano_referencia DESC, mes_referencia DESC
                 LIMIT 1
                """;
        try (PreparedStatement ps = ligacao.prepareStatement(sql)) {
            ps.setInt(1, idContador);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? Optional.of(rs.getBigDecimal(1)) : Optional.empty();
            }
        }
    }

    /** true se o contador já tem factura para esse mês (RN16). */
    public boolean existeFatura(int idContador, int ano, int mes) throws SQLException {
        String sql = "SELECT 1 FROM fatura WHERE id_contador = ? AND ano_referencia = ? AND mes_referencia = ?";
        try (PreparedStatement ps = ligacao.prepareStatement(sql)) {
            ps.setInt(1, idContador);
            ps.setInt(2, ano);
            ps.setInt(3, mes);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    /** Insere a factura e devolve o id gerado. */
    public int inserir(Fatura f) throws SQLException {
        String sql = """
                INSERT INTO fatura (mes_referencia, ano_referencia, data_leitura, leitura_anterior, leitura_atual,
                                    data_emissao, data_vencimento, valor_base, valor_multa, estado_fatura,
                                    id_contador, id_tarifa, id_funcionario)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                """;
        try (PreparedStatement ps = ligacao.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, f.getMesReferencia());
            ps.setInt(2, f.getAnoReferencia());
            ps.setObject(3, f.getDataLeitura());
            ps.setBigDecimal(4, f.getLeituraAnterior());
            ps.setBigDecimal(5, f.getLeituraAtual());
            ps.setObject(6, f.getDataEmissao());
            ps.setObject(7, f.getDataVencimento());
            ps.setBigDecimal(8, f.getValorBase());
            ps.setBigDecimal(9, f.getValorMulta());
            ps.setString(10, f.getEstadoFatura());
            ps.setInt(11, f.getIdContador());
            ps.setInt(12, f.getIdTarifa());
            ps.setInt(13, f.getIdFuncionario());
            ps.executeUpdate();
            try (ResultSet chaves = ps.getGeneratedKeys()) {
                chaves.next();
                return chaves.getInt(1);
            }
        }
    }

    // ------------------------------------------------------------------ TEMPORÁRIO
    // Consultas a tabelas de outros membros. Quando os DAO deles estiverem em main, trocar por:
    //   listarContratosActivos / buscarContrato -> ContratoDAO (Membro 3)
    //   buscarContadorActivo                   -> ContadorDAO (Membro 4)
    //   listarTecnicosLeitura                  -> FuncionarioDAO.listarPorCargo("tecnico_leitura") (Membro 5)

    private static final String SELECT_CONTRATO = """
            SELECT ct.id_contrato, cl.nome_completo, ct.tipo_contrato, ct.estado_contrato
              FROM contrato ct
              JOIN cliente cl ON cl.id_cliente = ct.id_cliente
            """;

    public List<ContratoResumo> listarContratosActivos() throws SQLException {
        String sql = SELECT_CONTRATO + " WHERE ct.estado_contrato = 'activo' ORDER BY ct.id_contrato";
        try (PreparedStatement ps = ligacao.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            List<ContratoResumo> contratos = new ArrayList<>();
            while (rs.next()) {
                contratos.add(mapearContrato(rs));
            }
            return contratos;
        }
    }

    public Optional<ContratoResumo> buscarContrato(int idContrato) throws SQLException {
        try (PreparedStatement ps = ligacao.prepareStatement(SELECT_CONTRATO + " WHERE ct.id_contrato = ?")) {
            ps.setInt(1, idContrato);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? Optional.of(mapearContrato(rs)) : Optional.empty();
            }
        }
    }

    public Optional<ContadorResumo> buscarContadorActivo(int idContrato) throws SQLException {
        String sql = """
                SELECT id_contador, numero_contador, leitura_inicial
                  FROM contador
                 WHERE id_contrato = ? AND estado_contador = 'activo'
                """;
        try (PreparedStatement ps = ligacao.prepareStatement(sql)) {
            ps.setInt(1, idContrato);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) {
                    return Optional.empty();
                }
                return Optional.of(new ContadorResumo(
                        rs.getInt("id_contador"), rs.getString("numero_contador"), rs.getBigDecimal("leitura_inicial")));
            }
        }
    }

    public List<TecnicoResumo> listarTecnicosLeitura() throws SQLException {
        String sql = "SELECT id_funcionario, nome FROM funcionario WHERE cargo = 'tecnico_leitura' ORDER BY nome";
        try (PreparedStatement ps = ligacao.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            List<TecnicoResumo> tecnicos = new ArrayList<>();
            while (rs.next()) {
                tecnicos.add(new TecnicoResumo(rs.getInt("id_funcionario"), rs.getString("nome")));
            }
            return tecnicos;
        }
    }

    // ------------------------------------------------------------------ mapeamento

    private static List<Fatura> lista(PreparedStatement ps) throws SQLException {
        try (ResultSet rs = ps.executeQuery()) {
            List<Fatura> faturas = new ArrayList<>();
            while (rs.next()) {
                faturas.add(mapear(rs));
            }
            return faturas;
        }
    }

    private static Fatura mapear(ResultSet rs) throws SQLException {
        Fatura f = new Fatura();
        f.setIdFatura(rs.getInt("id_fatura"));
        f.setMesReferencia(rs.getInt("mes_referencia"));
        f.setAnoReferencia(rs.getInt("ano_referencia"));
        f.setDataLeitura(rs.getObject("data_leitura", LocalDate.class));
        f.setLeituraAnterior(rs.getBigDecimal("leitura_anterior"));
        f.setLeituraAtual(rs.getBigDecimal("leitura_atual"));
        f.setDataEmissao(rs.getObject("data_emissao", LocalDate.class));
        f.setDataVencimento(rs.getObject("data_vencimento", LocalDate.class));
        f.setValorBase(rs.getBigDecimal("valor_base"));
        f.setValorMulta(rs.getBigDecimal("valor_multa"));
        f.setEstadoFatura(rs.getString("estado_fatura"));
        f.setIdContador(rs.getInt("id_contador"));
        f.setIdTarifa(rs.getInt("id_tarifa"));
        f.setIdFuncionario(rs.getInt("id_funcionario"));
        f.setNumeroContador(rs.getString("numero_contador"));
        f.setNomeCliente(rs.getString("nome_completo"));
        return f;
    }

    private static ContratoResumo mapearContrato(ResultSet rs) throws SQLException {
        return new ContratoResumo(rs.getInt("id_contrato"), rs.getString("nome_completo"),
                rs.getString("tipo_contrato"), rs.getString("estado_contrato"));
    }
}
