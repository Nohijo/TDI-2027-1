package dominio;

/**
 * Tarea 3 - Tecnologias para Desarrollos en Internet.
 *
 * Un renglon de la tabla de pagos con tarjeta de credito (tabla pagos_tarjeta).
 * Es el equivalente de la clase Proyecto del ejemplo MultiRowDBSpring3MVC: un
 * bean con un campo por columna, todos String como en el ejemplo.
 *
 * fecha, mes, tarjeta, transaccion y gastos son de solo lectura en la vista;
 * pagos es lo unico que captura el usuario.
 *
 * @author Marquez Cristoval Miguel Angel
 */
public class Pago {

	private int id;
	private String fecha;        // dd/mm/aaaa
	private String mes;
	private String tarjeta;
	private String transaccion;
	private String gastos;       // vacio si el movimiento no es un gasto
	private String pagos;        // vacio mientras el usuario no lo capture

	public Pago() {
	}

	public int getId() {
		return id;
	}

	public void setId( int id ) {
		this.id = id;
	}

	public String getFecha() {
		return fecha;
	}

	public void setFecha( String fecha ) {
		this.fecha = fecha;
	}

	public String getMes() {
		return mes;
	}

	public void setMes( String mes ) {
		this.mes = mes;
	}

	public String getTarjeta() {
		return tarjeta;
	}

	public void setTarjeta( String tarjeta ) {
		this.tarjeta = tarjeta;
	}

	public String getTransaccion() {
		return transaccion;
	}

	public void setTransaccion( String transaccion ) {
		this.transaccion = transaccion;
	}

	public String getGastos() {
		return gastos;
	}

	public void setGastos( String gastos ) {
		this.gastos = gastos;
	}

	public String getPagos() {
		return pagos;
	}

	public void setPagos( String pagos ) {
		this.pagos = pagos;
	}
}
