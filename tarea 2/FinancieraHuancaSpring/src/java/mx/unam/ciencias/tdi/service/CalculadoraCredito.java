package mx.unam.ciencias.tdi.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;

import org.springframework.stereotype.Service;

/**
 * Tarea 2 - Tecnologias para Desarrollos en Internet.
 *
 * Reglas de negocio de la solicitud: la cuota mensual y la fecha de
 * vencimiento. Va aparte del Controller (que solo atiende HTTP) y del DAO
 * (que solo habla con MySQL).
 *
 * @Service la marca como bean de Spring, para que el contenedor la construya
 * y la inyecte sola en el Controller.
 *
 * @author Miguel Angel Marquez Cristoval
 */
@Service
public class CalculadoraCredito {

    /**
     * Cuota mensual por el sistema frances (cuotas fijas):
     *
     *     cuota = M * i / (1 - (1 + i)^(-n))
     *
     * M = monto, n = numero de meses e i = tasa mensual equivalente a la TEA:
     *
     *     i = (1 + TEA)^(1/12) - 1
     *
     * @param monto   monto solicitado.
     * @param tea     tasa efectiva anual, en porcentaje.
     * @param periodo plazo en meses.
     * @return la cuota redondeada a dos decimales.
     */
    public BigDecimal cuotaMensual(BigDecimal monto, BigDecimal tea, int periodo) {
        double capital = monto.doubleValue();
        double tasaAnual = tea.doubleValue() / 100.0;
        double tasaMensual = Math.pow(1 + tasaAnual, 1.0 / 12.0) - 1;

        double cuota;
        if (tasaMensual == 0) {
            cuota = capital / periodo;
        } else {
            cuota = capital * tasaMensual / (1 - Math.pow(1 + tasaMensual, -periodo));
        }
        return BigDecimal.valueOf(cuota).setScale(2, RoundingMode.HALF_UP);
    }

    /** Fecha de vencimiento = fecha de desembolso + el plazo, en meses. */
    public LocalDate fechaVencimiento(LocalDate desembolso, int periodo) {
        return desembolso.plusMonths(periodo);
    }
}
