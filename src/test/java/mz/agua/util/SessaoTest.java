package mz.agua.util;

import mz.agua.exception.AcessoNegadoException;
import mz.agua.model.Perfil;
import mz.agua.model.Usuario;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SessaoTest {

    private static Usuario conta(Perfil perfil, boolean activo, String hash) {
        return new Usuario(1, "ana", hash, perfil, activo, 5, "Ana", "atendimento");
    }

    @AfterEach
    void limpar() {
        Sessao.terminarSessao();
    }

    @Test
    void semSessaoTudoEhNegado() {
        assertFalse(Sessao.estaAutenticada());
        assertFalse(Sessao.isAdmin());
        assertNull(Sessao.getUsernameOuNull());
        assertThrows(AcessoNegadoException.class, Sessao::exigirAutenticado);
        assertThrows(AcessoNegadoException.class, Sessao::exigirAdmin);
    }

    @Test
    void operadorEhAutenticadoMasNaoEhAdmin() {
        Sessao.iniciar(conta(Perfil.OPERADOR, true, null));
        assertDoesNotThrow(Sessao::exigirAutenticado);
        assertFalse(Sessao.isAdmin());
        assertThrows(AcessoNegadoException.class, Sessao::exigirAdmin);
        assertEquals(5, Sessao.getIdFuncionarioAutenticado());
        assertEquals("ana", Sessao.getUsernameOuNull());
    }

    @Test
    void adminPassaTodasAsVerificacoes() {
        Sessao.iniciar(conta(Perfil.ADMIN, true, null));
        assertTrue(Sessao.isAdmin());
        assertDoesNotThrow(Sessao::exigirAdmin);
    }

    @Test
    void terminarSessaoRetiraAsPermissoes() {
        Sessao.iniciar(conta(Perfil.ADMIN, true, null));
        Sessao.terminarSessao();
        assertFalse(Sessao.estaAutenticada());
        assertThrows(AcessoNegadoException.class, Sessao::exigirAdmin);
    }

    @Test
    void naoAceitaHashNemContaInactiva() {
        assertThrows(IllegalArgumentException.class, () -> Sessao.iniciar(conta(Perfil.ADMIN, true, "$2a$12$x")));
        assertThrows(IllegalArgumentException.class, () -> Sessao.iniciar(conta(Perfil.ADMIN, false, null)));
        assertThrows(IllegalArgumentException.class, () -> Sessao.iniciar(null));
        assertFalse(Sessao.estaAutenticada());
    }
}
