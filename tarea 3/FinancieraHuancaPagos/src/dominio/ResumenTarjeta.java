package dominio;

import java.math.BigDecimal;

/**
 * Tarea 3 - Tecnologias para Desarrollos en Internet.
 *
 * Totales de una tarjeta para la segunda vista: cuanto se gasto, cuanto se
 * pago y lo que queda por pagar.
 *
 * @author Marquez Cristoval Miguel Angel
 */
public class ResumenTarjeta {

	private String tarjeta;
	private BigDecimal gastos = BigDecimal.ZERO;
	private BigDecimal pagos = BigDecimal.ZERO;

	public ResumenTarjeta( String tarjeta ) {
		this.tarjeta = tarjeta;
	}

	public void sumaGasto( BigDecimal gasto ) {
		gastos = gastos.add( gasto );
	}

	public void sumaPago( BigDecimal pago ) {
		pagos = pagos.add( pago );
	}

	public String getTarjeta() {
		return tarjeta;
	}

	public BigDecimal getGastos() {
		return gastos;
	}

	public BigDecimal getPagos() {
		return pagos;
	}

	/** Lo que falta pagar: gastos - pagos. */
	public BigDecimal getSaldo() {
		return gastos.subtract( pagos );
	}
}
