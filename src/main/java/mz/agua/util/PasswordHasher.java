package mz.agua.util;

import at.favre.lib.crypto.bcrypt.BCrypt;

import java.util.UUID;

/**
 * Único ponto que conhece a biblioteca de hash (bcrypt, at.favre.lib). Se a equipa mudar para
 * Argon2id, só esta classe muda. O hash bcrypt inclui o salt e o custo.
 *
 * <p>Limite do bcrypt: só os primeiros 72 bytes da password contam; a política de passwords
 * (UtilizadorService.validarPassword) rejeita passwords maiores.
 */
public final class PasswordHasher {

    /** Custo do bcrypt (2^12 iterações): ~250 ms por hash num PC actual. */
    public static final int CUSTO = 12;

    private PasswordHasher() { }

    /** Gera o hash ($2a$...) a guardar em utilizador.password_hash. Não altera o array. */
    public static String gerarHash(char[] password) {
        return BCrypt.withDefaults().hashToString(CUSTO, password);
    }

    /** true se a password corresponde ao hash; false para qualquer entrada inválida. */
    public static boolean verificar(char[] password, String hash) {
        if (password == null || hash == null) {
            return false;
        }
        try {
            return BCrypt.verifyer().verify(password, hash).verified;
        } catch (IllegalArgumentException e) {
            return false;   // por exemplo, password demasiado longa para o bcrypt
        }
    }

    /**
     * Hash de uma password aleatória, usado para gastar o mesmo tempo quando o username não
     * existe (o tempo de resposta não revela se a conta existe). Calculado só quando necessário.
     */
    public static String hashFicticio() {
        return Ficticio.HASH;
    }

    private static final class Ficticio {
        static final String HASH = gerarHash(UUID.randomUUID().toString().toCharArray());
    }
}
