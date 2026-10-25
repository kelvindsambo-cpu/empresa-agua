package mz.agua.model;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Factura mensal de um contador (tabela fatura). A leitura do contador faz parte da factura:
 * não existe tabela de leituras. Não conhece Swing nem JDBC.
 *
 * <p>Consumo e valor total não são colunas do Anexo A: calculam-se a partir das outras.
 */
public class Fatura {

    private int idFatura;
    private int mesReferencia;
    private int anoReferencia;
    private LocalDate dataLeitura;
    private BigDecimal leituraAnterior;
    private BigDecimal leituraAtual;
    private LocalDate dataEmissao;
    private LocalDate dataVencimento;
    private BigDecimal valorBase;
    private BigDecimal valorMulta = BigDecimal.ZERO;
    private String estadoFatura = "pendente";   // 'pendente', 'paga' ou 'vencida'
    private int idContador;
    private int idTarifa;
    private int idFuncionario;                  // técnico que fez a leitura

    // Só leitura, para mostrar nas tabelas (vêm de contador e cliente)
    private String numeroContador;
    private String nomeCliente;

    public int getIdFatura() { return idFatura; }
    public void setIdFatura(int idFatura) { this.idFatura = idFatura; }

    public int getMesReferencia() { return mesReferencia; }
    public void setMesReferencia(int mesReferencia) { this.mesReferencia = mesReferencia; }

    public int getAnoReferencia() { return anoReferencia; }
    public void setAnoReferencia(int anoReferencia) { this.anoReferencia = anoReferencia; }

    public LocalDate getDataLeitura() { return dataLeitura; }
    public void setDataLeitura(LocalDate dataLeitura) { this.dataLeitura = dataLeitura; }

    public BigDecimal getLeituraAnterior() { return leituraAnterior; }
    public void setLeituraAnterior(BigDecimal leituraAnterior) { this.leituraAnterior = leituraAnterior; }

    public BigDecimal getLeituraAtual() { return leituraAtual; }
    public void setLeituraAtual(BigDecimal leituraAtual) { this.leituraAtual = leituraAtual; }

    public LocalDate getDataEmissao() { return dataEmissao; }
    public void setDataEmissao(LocalDate dataEmissao) { this.dataEmissao = dataEmissao; }

    public LocalDate getDataVencimento() { return dataVencimento; }
    public void setDataVencimento(LocalDate dataVencimento) { this.dataVencimento = dataVencimento; }

    public BigDecimal getValorBase() { return valorBase; }
    public void setValorBase(BigDecimal valorBase) { this.valorBase = valorBase; }

    public BigDecimal getValorMulta() { return valorMulta; }
    public void setValorMulta(BigDecimal valorMulta) { this.valorMulta = valorMulta; }

    public String getEstadoFatura() { return estadoFatura; }
    public void setEstadoFatura(String estadoFatura) { this.estadoFatura = estadoFatura; }

    public int getIdContador() { return idContador; }
    public void setIdContador(int idContador) { this.idContador = idContador; }

    public int getIdTarifa() { return idTarifa; }
    public void setIdTarifa(int idTarifa) { this.idTarifa = idTarifa; }

    public int getIdFuncionario() { return idFuncionario; }
    public void setIdFuncionario(int idFuncionario) { this.idFuncionario = idFuncionario; }

    public String getNumeroContador() { return numeroContador; }
    public void setNumeroContador(String numeroContador) { this.numeroContador = numeroContador; }

    public String getNomeCliente() { return nomeCliente; }
    public void setNomeCliente(String nomeCliente) { this.nomeCliente = nomeCliente; }

    /** m³ consumidos: leitura actual - leitura anterior. */
    public BigDecimal getConsumo() {
        return leituraAtual.subtract(leituraAnterior);
    }

    /** Valor base + multa (o saldo em dívida desconta ainda os pagamentos). */
    public BigDecimal getValorTotal() {
        return valorBase.add(valorMulta);
    }

    @Override
    public String toString() {
        return "Fatura{id=" + idFatura + ", referencia=" + mesReferencia + "/" + anoReferencia
                + ", contador=" + idContador + ", valorBase=" + valorBase + ", estado=" + estadoFatura + "}";
    }
}
