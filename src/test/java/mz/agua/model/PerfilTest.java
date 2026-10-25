package mz.agua.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class PerfilTest {

    @Test
    void converteValoresValidosDaBaseDeDados() {
        assertEquals(Perfil.ADMIN, Perfil.deValorBd("ADMIN"));
        assertEquals(Perfil.OPERADOR, Perfil.deValorBd("OPERADOR"));
        assertEquals("ADMIN", Perfil.ADMIN.getValorBd());
    }

    @Test
    void recusaValoresDesconhecidos() {
        assertThrows(IllegalArgumentException.class, () -> Perfil.deValorBd("admin"));
        assertThrows(IllegalArgumentException.class, () -> Perfil.deValorBd("SUPER"));
        assertThrows(IllegalArgumentException.class, () -> Perfil.deValorBd(null));
    }
}
