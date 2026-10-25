package mz.agua.model;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Tarifa de água (tabela tarifa). Uma tarifa sem data de fim é a que está em vigor.
 * Não conhece Swing nem JDBC.
 */
public class Tarifa {

    private final int idTarifa;
    private final String tipoContrato;       // 'residencial' ou 'comercial'
    private final BigDecimal precoPorM3;
    private final BigDecimal consumoMinimo;  // m³ cobrados mesmo que se consuma menos
    private final LocalDate dataInicio;
    private final LocalDate dataFim;         // null = em vigor

    public Tarifa(int idTarifa, String tipoContrato, BigDecimal precoPorM3, BigDecimal consumoMinimo,
                  LocalDate dataInicio, LocalDate dataFim) {
        this.idTarifa = idTarifa;
        this.tipoContrato = tipoContrato;
        this.precoPorM3 = precoPorM3;
        this.consumoMinimo = consumoMinimo;
        this.dataInicio = dataInicio;
        this.dataFim = dataFim;
    }

    public int getIdTarifa() { return idTarifa; }
    public String getTipoContrato() { return tipoContrato; }
    public BigDecimal getPrecoPorM3() { return precoPorM3; }
    public BigDecimal getConsumoMinimo() { return consumoMinimo; }
    public LocalDate getDataInicio() { return dataInicio; }
    public LocalDate getDataFim() { return dataFim; }

    public boolean isEmVigor() {
        return dataFim == null;
    }

    @Override
    public String toString() {
        return "Tarifa{id=" + idTarifa + ", tipo=" + tipoContrato + ", preco=" + precoPorM3
                + ", inicio=" + dataInicio + ", fim=" + dataFim + "}";
    }
}
