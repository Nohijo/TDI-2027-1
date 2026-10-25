package servicio;

import java.util.List;

import dominio.Pago;
import dominio.ResumenTarjeta;

/**
 * Tarea 3 - Tecnologias para Desarrollos en Internet.
 *
 * Servicio de la tabla de pagos con tarjeta de credito.
 *
 * @author Marquez Cristoval Miguel Angel
 */
public interface PagoService {

	/** Todos los movimientos de la tabla pagos_tarjeta. */
	public List<Pago> consultaTodos();

	/** Guarda la columna Pagos de cada renglon. */
	public void guardaPagos( List<Pago> pagos );

	/** Gastos, pagos y saldo de cada tarjeta, en el orden en que aparecen. */
	public List<ResumenTarjeta> resumenPorTarjeta( List<Pago> pagos );
}
