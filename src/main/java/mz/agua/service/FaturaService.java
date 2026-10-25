package mz.agua.service;

import mz.agua.config.DatabaseConnection;
import mz.agua.dao.FaturaDAO;
import mz.agua.dao.FaturaDAO.ContadorResumo;
import mz.agua.dao.FaturaDAO.ContratoResumo;
import mz.agua.dao.TarifaDAO;
import mz.agua.exception.ValidacaoException;
import mz.agua.model.Fatura;
import mz.agua.model.Tarifa;
import mz.agua.util.Formatos;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;

/**
 * Cálculo e emissão de facturas (RN11, RN15–RN23). Os cálculos são estáticos e não usam a base
 * de dados, para poderem ser testados com JUnit.
 */
public class FaturaService {

    /** O que o funcionário preenche no formulário de emissão. */
    public record DadosLeitura(int idContrato, int mes, int ano, LocalDate dataLeitura,
                               BigDecimal leituraAtual, int idTecnico) { }

    /** Contador activo de um contrato e a leitura de onde parte a próxima factura. */
    public record InfoContador(ContadorResumo contador, BigDecimal leituraAnterior) { }

    /** Factura calculada (ainda não gravada) e os valores intermédios para mostrar ao utilizador. */
    public record Simulacao(Fatura fatura, Tarifa tarifa, BigDecimal consumo, BigDecimal consumoCobrado) { }

    // ------------------------------------------------------------------ cálculos

    /** consumo = leitura actual - leitura anterior; a leitura não pode descer (RN18). */
    public static BigDecimal calcularConsumo(BigDecimal leituraAnterior, BigDecimal leituraAtual) {
        if (leituraAtual.compareTo(leituraAnterior) < 0) {
            throw new ValidacaoException("A leitura actual (" + leituraAtual
                    + ") não pode ser menor que a leitura anterior (" + leituraAnterior + ").");
        }
        return leituraAtual.subtract(leituraAnterior);
    }

    /** Cobra-se pelo menos o consumo mínimo da tarifa (RN20). */
    public static BigDecimal calcularConsumoCobrado(BigDecimal consumo, BigDecimal consumoMinimo) {
        return consumo.max(consumoMinimo);
    }

    /** valor_base = consumo cobrado × preço por m³, arredondado a 2 casas (RN20). */
    public static BigDecimal calcularValorBase(BigDecimal consumoCobrado, BigDecimal precoPorM3) {
        return consumoCobrado.multiply(precoPorM3).setScale(2, RoundingMode.HALF_UP);
    }

    /** Vencimento: dia 10 do mês seguinte à emissão (RN22). */
    public static LocalDate calcularVencimento(LocalDate dataEmissao) {
        return dataEmissao.plusMonths(1).withDayOfMonth(10);
    }

    // ------------------------------------------------------------------ operações

    /** Contador activo do contrato e leitura anterior (RN11/RN17). */
    public InfoContador obterInfoContador(int idContrato) {
        return DatabaseConnection.executar(c -> infoContador(new FaturaDAO(c), idContrato));
    }

    /** Calcula a factura sem a gravar (botão "Calcular"). */
    public Simulacao simular(DadosLeitura dados) {
        return DatabaseConnection.executar(c -> preparar(c, dados));
    }

    /** Calcula e grava a factura numa transacção. Devolve o número da factura. */
    public int emitir(DadosLeitura dados) {
        return DatabaseConnection.emTransaccao(c -> {
            Simulacao simulacao = preparar(c, dados);
            return new FaturaDAO(c).inserir(simulacao.fatura());
        });
    }

    private Simulacao preparar(Connection c, DadosLeitura dados) throws SQLException {
        LocalDate hoje = LocalDate.now();
        validarCampos(dados, hoje);

        FaturaDAO faturaDAO = new FaturaDAO(c);
        ContratoResumo contrato = faturaDAO.buscarContrato(dados.idContrato())
                .orElseThrow(() -> new ValidacaoException("Contrato não encontrado."));
        if (!"activo".equals(contrato.estadoContrato())) {
            throw new ValidacaoException("Só se emitem facturas para contratos activos (RN15).");
        }
        InfoContador info = infoContador(faturaDAO, dados.idContrato());
        int idContador = info.contador().idContador();
        if (faturaDAO.existeFatura(idContador, dados.ano(), dados.mes())) {
            throw new ValidacaoException(String.format(
                    "O contador já tem factura de %02d/%d (RN16).", dados.mes(), dados.ano()));
        }
        Tarifa tarifa = new TarifaDAO(c).buscarVigente(contrato.tipoContrato(), dados.dataLeitura())
                .orElseThrow(() -> new ValidacaoException("Não há tarifa " + contrato.tipoContrato()
                        + " em vigor em " + Formatos.data(dados.dataLeitura()) + "."));

        BigDecimal consumo = calcularConsumo(info.leituraAnterior(), dados.leituraAtual());
        BigDecimal consumoCobrado = calcularConsumoCobrado(consumo, tarifa.getConsumoMinimo());

        Fatura fatura = new Fatura();
        fatura.setMesReferencia(dados.mes());
        fatura.setAnoReferencia(dados.ano());
        fatura.setDataLeitura(dados.dataLeitura());
        fatura.setLeituraAnterior(info.leituraAnterior());
        fatura.setLeituraAtual(dados.leituraAtual());
        fatura.setDataEmissao(hoje);
        fatura.setDataVencimento(calcularVencimento(hoje));
        fatura.setValorBase(calcularValorBase(consumoCobrado, tarifa.getPrecoPorM3()));
        fatura.setIdContador(idContador);
        fatura.setIdTarifa(tarifa.getIdTarifa());
        fatura.setIdFuncionario(dados.idTecnico());
        return new Simulacao(fatura, tarifa, consumo, consumoCobrado);
    }

    private static InfoContador infoContador(FaturaDAO faturaDAO, int idContrato) throws SQLException {
        ContadorResumo contador = faturaDAO.buscarContadorActivo(idContrato)
                .orElseThrow(() -> new ValidacaoException("O contrato não tem contador activo (RN15)."));
        BigDecimal anterior = faturaDAO.buscarUltimaLeitura(contador.idContador())
                .orElse(contador.leituraInicial());
        return new InfoContador(contador, anterior);
    }

    private static void validarCampos(DadosLeitura dados, LocalDate hoje) {
        if (dados.mes() < 1 || dados.mes() > 12) {
            throw new ValidacaoException("Mês de referência inválido.");
        }
        if (dados.dataLeitura().isAfter(hoje)) {
            throw new ValidacaoException("A data da leitura não pode ser no futuro.");
        }
        if (dados.leituraAtual().signum() < 0) {
            throw new ValidacaoException("A leitura actual não pode ser negativa.");
        }
        if (dados.idTecnico() <= 0) {
            throw new ValidacaoException("Escolha o técnico que fez a leitura.");
        }
    }
}
