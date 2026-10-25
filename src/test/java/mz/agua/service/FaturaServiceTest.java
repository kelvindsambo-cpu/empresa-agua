package mz.agua.service;

import mz.agua.exception.ValidacaoException;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class FaturaServiceTest {

    private static BigDecimal bd(String valor) {
        return new BigDecimal(valor);
    }

    /** Valor de uma factura com os cálculos encadeados, como na emissão. */
    private static BigDecimal valorBase(String anterior, String atual, String minimo, String preco) {
        BigDecimal consumo = FaturaService.calcularConsumo(bd(anterior), bd(atual));
        BigDecimal cobrado = FaturaService.calcularConsumoCobrado(consumo, bd(minimo));
        return FaturaService.calcularValorBase(cobrado, bd(preco));
    }

    @Test
    void consumoAcimaDoMinimoCobraOConsumoReal() {
        assertEquals(bd("450.00"), valorBase("120", "130", "5", "45"));
    }

    @Test
    void consumoAbaixoDoMinimoCobraOMinimo() {
        // exemplo do plano: 120 -> 123, mínimo 5, preço 45 = 225.00 MT
        assertEquals(bd("3"), FaturaService.calcularConsumo(bd("120"), bd("123")));
        assertEquals(bd("225.00"), valorBase("120", "123", "5", "45"));
    }

    @Test
    void semConsumoCobraOMinimo() {
        assertEquals(bd("225.00"), valorBase("120", "120", "5", "45"));
    }

    @Test
    void leituraMenorQueAAnteriorERecusada() {
        assertThrows(ValidacaoException.class, () -> FaturaService.calcularConsumo(bd("120"), bd("119.99")));
    }

    @Test
    void valorEArredondadoADuasCasas() {
        // 7.25 m³ × 45.50 MT = 329.875 -> 329.88
        assertEquals(bd("329.88"), valorBase("100.00", "107.25", "5", "45.50"));
    }

    @Test
    void venceNoDia10DoMesSeguinteAEmissao() {
        assertEquals(LocalDate.of(2026, 11, 10), FaturaService.calcularVencimento(LocalDate.of(2026, 10, 25)));
        assertEquals(LocalDate.of(2026, 2, 10), FaturaService.calcularVencimento(LocalDate.of(2026, 1, 31)));
    }

    @Test
    void emissaoEmDezembroVenceEmJaneiroDoAnoSeguinte() {
        assertEquals(LocalDate.of(2027, 1, 10), FaturaService.calcularVencimento(LocalDate.of(2026, 12, 31)));
    }
}
