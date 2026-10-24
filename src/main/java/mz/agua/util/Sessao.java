package mz.agua.util;

import mz.agua.exception.AcessoNegadoException;
import mz.agua.model.Usuario;

/**
 * Sessão do utilizador autenticado (aplicação desktop, um utilizador de cada vez).
 * É o ponto único para saber "quem está autenticado" e para exigir permissões.
 *
 * <p>Uso nos Services/Controllers dos outros módulos (primeira linha do método):
 * <pre>
 *   Sessao.exigirAutenticado();   // qualquer perfil
 *   Sessao.exigirAdmin();         // UC06-A, UC11, UC12
 * </pre>
 * Esconder botões não chega: estas verificações impedem a operação mesmo que alguém invoque
 * o Controller/Service directamente.
 */
public final class Sessao {

    private static volatile Usuario autenticado;   // nunca contém o hash

    private Sessao() { }

    /** Inicia a sessão. Recebe um Usuario SEM hash e activo. */
    public static void iniciar(Usuario usuario) {
        if (usuario == null || !usuario.isActivo()) {
            throw new IllegalArgumentException("Só uma conta activa pode iniciar sessão.");
        }
        if (usuario.getPasswordHash() != null) {
            throw new IllegalArgumentException("A sessão não pode guardar o hash da password.");
        }
        autenticado = usuario;
    }

    /** Termina a sessão (logout). */
    public static void terminarSessao() {
        autenticado = null;
    }

    public static boolean estaAutenticada() {
        return autenticado != null;
    }

    /** Utilizador autenticado; lança AcessoNegadoException se não houver sessão. */
    public static Usuario getUsuarioAutenticado() {
        Usuario u = autenticado;
        if (u == null) {
            throw new AcessoNegadoException("Sessão não iniciada.");
        }
        return u;
    }

    /** id_funcionario do utilizador autenticado (ex.: para registado_por). */
    public static int getIdFuncionarioAutenticado() {
        return getUsuarioAutenticado().getIdFuncionario();
    }

    /** true só se houver sessão e o perfil for ADMIN. */
    public static boolean isAdmin() {
        Usuario u = autenticado;
        return u != null && u.isAdmin();
    }

    /** Username para a auditoria (SET @app_utilizador) ou null se não houver sessão. */
    public static String getUsernameOuNull() {
        Usuario u = autenticado;
        return u == null ? null : u.getUsername();
    }

    public static void exigirAutenticado() {
        getUsuarioAutenticado();
    }

    public static void exigirAdmin() {
        Usuario u = getUsuarioAutenticado();
        if (!u.isAdmin()) {
            throw new AcessoNegadoException("Operação reservada ao perfil ADMIN.");
        }
    }
}
