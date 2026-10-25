package mz.agua.dao;

import mz.agua.model.Tarifa;

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
 * Acesso à tabela tarifa (só SQL e mapeamento; sem Swing).
 * Recebe a Connection de fora e não a abre nem fecha.
 */
public class TarifaDAO {

    private static final String SELECT_BASE = """
            SELECT id_tarifa, tipo_contrato, preco_por_m3, consumo_minimo, data_inicio, data_fim
              FROM tarifa
            """;

    private final Connection ligacao;

    public TarifaDAO(Connection ligacao) {
        this.ligacao = ligacao;
    }

    /** Todas as tarifas, as mais recentes primeiro dentro de cada tipo. */
    public List<Tarifa> listar() throws SQLException {
        String sql = SELECT_BASE + " ORDER BY tipo_contrato, data_inicio DESC";
        try (PreparedStatement ps = ligacao.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            List<Tarifa> tarifas = new ArrayList<>();
            while (rs.next()) {
                tarifas.add(mapear(rs));
            }
            return tarifas;
        }
    }

    /** Tarifa que estava em vigor numa data, para um tipo de contrato (RN14). */
    public Optional<Tarifa> buscarVigente(String tipoContrato, LocalDate data) throws SQLException {
        String sql = SELECT_BASE + """
                 WHERE tipo_contrato = ?
                   AND data_inicio <= ?
                   AND (data_fim IS NULL OR data_fim >= ?)
                 ORDER BY data_inicio DESC
                 LIMIT 1
                """;
        try (PreparedStatement ps = ligacao.prepareStatement(sql)) {
            ps.setString(1, tipoContrato);
            ps.setObject(2, data);
            ps.setObject(3, data);
            return primeira(ps);
        }
    }

    /** Tarifa do tipo indicado que ainda não tem data de fim (a que está em vigor). */
    public Optional<Tarifa> buscarEmVigor(String tipoContrato) throws SQLException {
        String sql = SELECT_BASE + " WHERE tipo_contrato = ? AND data_fim IS NULL";
        try (PreparedStatement ps = ligacao.prepareStatement(sql)) {
            ps.setString(1, tipoContrato);
            return primeira(ps);
        }
    }

    /** Termina a vigência de uma tarifa (data_fim é inclusiva). */
    public void fechar(int idTarifa, LocalDate dataFim) throws SQLException {
        String sql = "UPDATE tarifa SET data_fim = ? WHERE id_tarifa = ?";
        try (PreparedStatement ps = ligacao.prepareStatement(sql)) {
            ps.setObject(1, dataFim);
            ps.setInt(2, idTarifa);
            ps.executeUpdate();
        }
    }

    /** Insere uma tarifa nova, sem data de fim. Devolve o id gerado. */
    public int inserir(Tarifa tarifa) throws SQLException {
        String sql = "INSERT INTO tarifa (tipo_contrato, preco_por_m3, consumo_minimo, data_inicio) VALUES (?, ?, ?, ?)";
        try (PreparedStatement ps = ligacao.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, tarifa.getTipoContrato());
            ps.setBigDecimal(2, tarifa.getPrecoPorM3());
            ps.setBigDecimal(3, tarifa.getConsumoMinimo());
            ps.setObject(4, tarifa.getDataInicio());
            ps.executeUpdate();
            try (ResultSet chaves = ps.getGeneratedKeys()) {
                chaves.next();
                return chaves.getInt(1);
            }
        }
    }

    private Optional<Tarifa> primeira(PreparedStatement ps) throws SQLException {
        try (ResultSet rs = ps.executeQuery()) {
            return rs.next() ? Optional.of(mapear(rs)) : Optional.empty();
        }
    }

    private static Tarifa mapear(ResultSet rs) throws SQLException {
        return new Tarifa(
                rs.getInt("id_tarifa"),
                rs.getString("tipo_contrato"),
                rs.getBigDecimal("preco_por_m3"),
                rs.getBigDecimal("consumo_minimo"),
                rs.getObject("data_inicio", LocalDate.class),
                rs.getObject("data_fim", LocalDate.class));
    }
}
