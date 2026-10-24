package mz.agua.service;

import mz.agua.exception.AcessoNegadoException;
import mz.agua.exception.ValidacaoException;
import mz.agua.model.Perfil;
import mz.agua.model.Usuario;
import mz.agua.util.Sessao;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/** Testes sem base de dados: validações e verificação de permissões (que acontecem antes da BD). */
class UtilizadorServiceTest {

    private final UtilizadorService servico = new UtilizadorService();

    @AfterEach
    void limpar() {
        Sessao.terminarSessao();
    }

    private static void entrarComo(Perfil perfil) {
        Sessao.iniciar(new Usuario(2, "op", null, perfil, true, 5, "Elsa", "atendimento"));
    }

    // ---- T02: um OPERADOR não gere utilizadores, mesmo chamando o Service directamente

    @Test
    void operadorNaoPodeGerirContas() {
        entrarComo(Perfil.OPERADOR);
        char[] pw = "Segredo123".toCharArray();
        assertThrows(AcessoNegadoException.class, () -> servico.criarConta(5, "nova.conta", Perfil.OPERADOR, pw));
        assertThrows(AcessoNegadoException.class, servico::listarContas);
        assertThrows(AcessoNegadoException.class, () -> servico.activarConta(1));
        assertThrows(AcessoNegadoException.class, () -> servico.desactivarConta(1));
        assertThrows(AcessoNegadoException.class, () -> servico.redefinirPassword(1, pw));
    }

    @Test
    void semSessaoNaoPodeGerirContas() {
        char[] pw = "Segredo123".toCharArray();
        assertThrows(AcessoNegadoException.class, () -> servico.criarConta(5, "nova.conta", Perfil.OPERADOR, pw));
        assertThrows(AcessoNegadoException.class, servico::listarContas);
    }

    @Test
    void adminNaoPodeDesactivarAPropriaConta() {
        Sessao.iniciar(new Usuario(7, "chefe", null, Perfil.ADMIN, true, 6, "Fábio", "administrador"));
        ValidacaoException e = assertThrows(ValidacaoException.class, () -> servico.desactivarConta(7));
        assertEquals("Não pode desactivar a sua própria conta.", e.getMessage());
    }

    // ---- validações

    @Test
    void usernameValido() {
        assertEquals("ana.silva", UtilizadorService.validarUsername("  ana.silva "));
        assertThrows(ValidacaoException.class, () -> UtilizadorService.validarUsername("ab"));
        assertThrows(ValidacaoException.class, () -> UtilizadorService.validarUsername("tem espaço"));
        assertThrows(ValidacaoException.class, () -> UtilizadorService.validarUsername("x'; DROP TABLE"));
        assertThrows(ValidacaoException.class, () -> UtilizadorService.validarUsername(null));
    }

    @Test
    void politicaDePassword() {
        assertDoesNotThrow(() -> UtilizadorService.validarPassword("Segredo123".toCharArray(), "ana"));
        assertThrows(ValidacaoException.class, () -> UtilizadorService.validarPassword("Ab1".toCharArray(), "ana"));
        assertThrows(ValidacaoException.class, () -> UtilizadorService.validarPassword("somenteletras".toCharArray(), "ana"));
        assertThrows(ValidacaoException.class, () -> UtilizadorService.validarPassword("12345678".toCharArray(), "ana"));
        assertThrows(ValidacaoException.class, () -> UtilizadorService.validarPassword("Ana12345".toCharArray(), "ana12345"));
        assertThrows(ValidacaoException.class, () -> UtilizadorService.validarPassword(null, "ana"));
        // 73 bytes: acima do limite do bcrypt
        assertThrows(ValidacaoException.class, () -> UtilizadorService.validarPassword(("a1" + "x".repeat(71)).toCharArray(), "ana"));
        // 72 bytes: ainda aceite
        assertDoesNotThrow(() -> UtilizadorService.validarPassword(("a1" + "x".repeat(70)).toCharArray(), "ana"));
    }
}
