package mz.agua.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PasswordHasherTest {

    @Test
    void hashEhBcryptEVerificaSoACorrecta() {
        char[] password = "Segredo123".toCharArray();
        String hash = PasswordHasher.gerarHash(password);

        assertTrue(hash.startsWith("$2a$12$"), "formato bcrypt com custo 12");
        assertEquals(60, hash.length());
        assertFalse(hash.contains("Segredo123"));
        assertTrue(PasswordHasher.verificar("Segredo123".toCharArray(), hash));
        assertFalse(PasswordHasher.verificar("segredo123".toCharArray(), hash));
        assertFalse(PasswordHasher.verificar("".toCharArray(), hash));
    }

    @Test
    void mesmaPasswordDaHashesDiferentesPorCausaDoSalt() {
        char[] password = "Segredo123".toCharArray();
        assertNotEquals(PasswordHasher.gerarHash(password), PasswordHasher.gerarHash(password));
    }

    @Test
    void entradasInvalidasNuncaVerificam() {
        assertFalse(PasswordHasher.verificar(null, "$2a$12$x"));
        assertFalse(PasswordHasher.verificar("abc".toCharArray(), null));
        assertFalse(PasswordHasher.verificar("abc".toCharArray(), "isto-nao-e-um-hash"));
    }

    @Test
    void hashFicticioEhValidoEEstavel() {
        String h = PasswordHasher.hashFicticio();
        assertEquals(h, PasswordHasher.hashFicticio());
        assertFalse(PasswordHasher.verificar("qualquer".toCharArray(), h));
    }
}
