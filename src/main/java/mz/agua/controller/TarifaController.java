package mz.agua.controller;

import mz.agua.config.DatabaseConnection;
import mz.agua.dao.TarifaDAO;
import mz.agua.exception.AcessoNegadoException;
import mz.agua.exception.PersistenciaException;
import mz.agua.exception.ValidacaoException;
import mz.agua.model.Tarifa;
import mz.agua.util.Formatos;
import mz.agua.util.Sessao;
import mz.agua.view.TarifaDialog;
import mz.agua.view.TarifaPanel;

import javax.swing.SwingUtilities;
import java.time.LocalDate;
import java.util.Optional;

/**
 * Liga o TarifaPanel/TarifaDialog ao TarifaDAO. Consultar tarifas: todos. Criar: só ADMIN.
 */
public class TarifaController {

    private final TarifaPanel painel;

    public TarifaController(TarifaPanel painel) {
        this.painel = painel;
        painel.setNovaTarifaVisivel(Sessao.isAdmin());
        painel.addNovaTarifaListener(e -> abrirNovaTarifa());
        painel.addActualizarListener(e -> carregar());
        carregar();
    }

    public void carregar() {
        try {
            painel.mostrarTarifas(DatabaseConnection.executar(c -> new TarifaDAO(c).listar()));
        } catch (PersistenciaException e) {
            painel.mostrarErro(e.getMessage());
        }
    }

    private void abrirNovaTarifa() {
        TarifaDialog dialogo = new TarifaDialog(SwingUtilities.getWindowAncestor(painel));
        dialogo.addGuardarListener(e -> guardar(dialogo));
        dialogo.setVisible(true);
    }

    private void guardar(TarifaDialog dialogo) {
        try {
            Sessao.exigirAdmin();
            Tarifa nova = new Tarifa(0, dialogo.getTipoContrato(),
                    Formatos.lerDecimal(dialogo.getPreco(), "Preço por m³"),
                    Formatos.lerDecimal(dialogo.getConsumoMinimo(), "Consumo mínimo"),
                    Formatos.lerData(dialogo.getDataInicio(), "Data de início"),
                    null);
            criar(nova);
            dialogo.dispose();
            painel.mostrarMensagem("Tarifa " + nova.getTipoContrato() + " criada.");
            carregar();
        } catch (ValidacaoException | AcessoNegadoException | PersistenciaException e) {
            dialogo.mostrarErro(e.getMessage());
        }
    }

    /**
     * Numa só transacção: termina a tarifa em vigor do mesmo tipo na véspera da nova e insere a
     * nova. Sem isto o trigger do Anexo A recusa a tarifa nova por sobreposição (RN13).
     */
    private void criar(Tarifa nova) {
        if (nova.getPrecoPorM3().signum() <= 0) {
            throw new ValidacaoException("O preço por m³ tem de ser maior que zero.");
        }
        if (nova.getConsumoMinimo().signum() <= 0) {
            throw new ValidacaoException("O consumo mínimo tem de ser maior que zero.");
        }
        DatabaseConnection.emTransaccao(c -> {
            TarifaDAO dao = new TarifaDAO(c);
            Optional<Tarifa> emVigor = dao.buscarEmVigor(nova.getTipoContrato());
            if (emVigor.isPresent()) {
                LocalDate inicioActual = emVigor.get().getDataInicio();
                LocalDate vespera = nova.getDataInicio().minusDays(1);
                if (!vespera.isAfter(inicioActual)) {
                    throw new ValidacaoException("A nova tarifa tem de começar depois de "
                            + Formatos.data(inicioActual.plusDays(1)) + " (a tarifa em vigor começou em "
                            + Formatos.data(inicioActual) + ").");
                }
                dao.fechar(emVigor.get().getIdTarifa(), vespera);
            }
            return dao.inserir(nova);
        });
    }
}
