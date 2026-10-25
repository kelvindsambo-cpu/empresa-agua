package mz.agua.controller;

import mz.agua.config.DatabaseConnection;
import mz.agua.dao.FaturaDAO;
import mz.agua.dao.FaturaDAO.ContratoResumo;
import mz.agua.dao.FaturaDAO.TecnicoResumo;
import mz.agua.exception.PersistenciaException;
import mz.agua.exception.ValidacaoException;
import mz.agua.model.Fatura;
import mz.agua.service.FaturaService;
import mz.agua.service.FaturaService.DadosLeitura;
import mz.agua.service.FaturaService.InfoContador;
import mz.agua.service.FaturaService.Simulacao;
import mz.agua.util.Formatos;
import mz.agua.view.EmissaoFaturaDialog;
import mz.agua.view.FaturaPanel;

import javax.swing.SwingUtilities;
import java.util.List;

/** Liga o FaturaPanel/EmissaoFaturaDialog ao FaturaService e ao FaturaDAO. */
public class FaturaController {

    private final FaturaPanel painel;
    private final FaturaService servico = new FaturaService();

    public FaturaController(FaturaPanel painel) {
        this.painel = painel;
        painel.addEmitirListener(e -> abrirEmissao());
        painel.addActualizarListener(e -> carregar());
        carregar();
    }

    public void carregar() {
        try {
            painel.mostrarFaturas(DatabaseConnection.executar(c -> new FaturaDAO(c).listar()));
        } catch (PersistenciaException e) {
            painel.mostrarErro(e.getMessage());
        }
    }

    private void abrirEmissao() {
        List<ContratoResumo> contratos;
        List<TecnicoResumo> tecnicos;
        try {
            contratos = DatabaseConnection.executar(c -> new FaturaDAO(c).listarContratosActivos());
            tecnicos = DatabaseConnection.executar(c -> new FaturaDAO(c).listarTecnicosLeitura());
        } catch (PersistenciaException e) {
            painel.mostrarErro(e.getMessage());
            return;
        }
        if (contratos.isEmpty()) {
            painel.mostrarErro("Não há contratos activos.");
            return;
        }
        if (tecnicos.isEmpty()) {
            painel.mostrarErro("Não há funcionários com o cargo de técnico de leitura.");
            return;
        }

        EmissaoFaturaDialog dialogo = new EmissaoFaturaDialog(SwingUtilities.getWindowAncestor(painel));
        dialogo.setContratos(contratos);
        dialogo.setTecnicos(tecnicos);
        dialogo.addContratoListener(e -> mostrarContador(dialogo));
        dialogo.addCalcularListener(e -> calcular(dialogo));
        dialogo.addEmitirListener(e -> emitir(dialogo));
        mostrarContador(dialogo);
        dialogo.setVisible(true);
    }

    private void mostrarContador(EmissaoFaturaDialog dialogo) {
        try {
            InfoContador info = servico.obterInfoContador(dialogo.getContrato().idContrato());
            dialogo.mostrarContador(info.contador().numeroContador(), info.leituraAnterior());
        } catch (ValidacaoException | PersistenciaException e) {
            dialogo.mostrarSemContador(e.getMessage());
        }
    }

    private void calcular(EmissaoFaturaDialog dialogo) {
        try {
            dialogo.mostrarResumo(resumo(servico.simular(lerDados(dialogo))));
        } catch (ValidacaoException | PersistenciaException e) {
            dialogo.mostrarErro(e.getMessage());
        }
    }

    private void emitir(EmissaoFaturaDialog dialogo) {
        try {
            int numero = servico.emitir(lerDados(dialogo));
            dialogo.dispose();
            painel.mostrarMensagem("Factura nº " + numero + " emitida.");
            carregar();
        } catch (ValidacaoException | PersistenciaException e) {
            dialogo.mostrarErro(e.getMessage());
        }
    }

    private static DadosLeitura lerDados(EmissaoFaturaDialog dialogo) {
        return new DadosLeitura(
                dialogo.getContrato().idContrato(),
                dialogo.getMes(),
                dialogo.getAno(),
                Formatos.lerData(dialogo.getDataLeitura(), "Data da leitura"),
                Formatos.lerDecimal(dialogo.getLeituraAtual(), "Leitura actual"),
                dialogo.getTecnico().idFuncionario());
    }

    private static String resumo(Simulacao s) {
        Fatura f = s.fatura();
        return String.format("""
                Leitura anterior: %s m³
                Leitura actual:   %s m³
                Consumo:          %s m³
                Consumo cobrado:  %s m³ (mínimo da tarifa: %s m³)
                Preço por m³:     %s MT
                Valor a pagar:    %s MT
                Vencimento:       %s""",
                f.getLeituraAnterior(), f.getLeituraAtual(), s.consumo(),
                s.consumoCobrado(), s.tarifa().getConsumoMinimo(),
                s.tarifa().getPrecoPorM3(), f.getValorBase(), Formatos.data(f.getDataVencimento()));
    }
}
