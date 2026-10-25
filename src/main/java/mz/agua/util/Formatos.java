package mz.agua.util;

import mz.agua.exception.ValidacaoException;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;

/**
 * Conversão entre o texto dos formulários Swing e os tipos do Model (datas dd/mm/aaaa e números
 * decimais). Erros de escrita viram ValidacaoException com o nome do campo.
 */
public final class Formatos {

    private static final DateTimeFormatter DATA =
            DateTimeFormatter.ofPattern("dd/MM/uuuu").withResolverStyle(ResolverStyle.STRICT);

    private Formatos() { }

    /** Data no formato dd/mm/aaaa (texto vazio se for null). */
    public static String data(LocalDate data) {
        return data == null ? "" : data.format(DATA);
    }

    /** Lê uma data dd/mm/aaaa escrita num campo obrigatório. */
    public static LocalDate lerData(String texto, String campo) {
        String valor = obrigatorio(texto, campo);
        try {
            return LocalDate.parse(valor, DATA);
        } catch (DateTimeParseException e) {
            throw new ValidacaoException(campo + ": data inválida. Use o formato dd/mm/aaaa.");
        }
    }

    /** Lê um número decimal escrito num campo obrigatório (aceita vírgula ou ponto). */
    public static BigDecimal lerDecimal(String texto, String campo) {
        String valor = obrigatorio(texto, campo).replace(',', '.');
        try {
            return new BigDecimal(valor);
        } catch (NumberFormatException e) {
            throw new ValidacaoException(campo + ": número inválido.");
        }
    }

    private static String obrigatorio(String texto, String campo) {
        if (texto == null || texto.isBlank()) {
            throw new ValidacaoException(campo + " é obrigatório.");
        }
        return texto.trim();
    }
}
