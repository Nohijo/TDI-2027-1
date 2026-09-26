package dominio;

/**
 * Tarea 2 - Tecnologias para Desarrollos en Internet.
 *
 * Objeto de dominio: una solicitud de credito de Financiera Huanca. Es el
 * "command object" que Spring llena con lo que manda el formulario, igual que
 * la clase User del ejemplo FormularioDBSpring3MVC.
 *
 * Todos los campos son String, como en el ejemplo (ahi la edad tambien es
 * String): asi el enlace de datos nunca falla y la revision de los valores se
 * hace en CreditoValidator.
 *
 * @author Marquez Cristoval Miguel Angel
 */
public class Credito {

	/* ---- Datos personales ---- */
	private String nombres;
	private String apellidos;
	private String dni;
	private String correo;

	/* ---- Datos del credito ---- */
	private String fecha;      // aaaa-mm-dd, como lo manda <input type="date">
	private String moneda;     // "soles" o "dolares"
	private String monto;
	private String periodo;    // en meses
	private String tea;        // tasa efectiva anual, en porcentaje

	/* ---- Lo que calcula el servidor ---- */
	private String cuota;
	private String fechaVencimiento;

	/* ---- Folio que asigna MySQL ---- */
	private int id;

	public String getNombres() {
		return nombres;
	}

	public void setNombres( String nombres ) {
		this.nombres = nombres;
	}

	public String getApellidos() {
		return apellidos;
	}

	public void setApellidos( String apellidos ) {
		this.apellidos = apellidos;
	}

	public String getDni() {
		return dni;
	}

	public void setDni( String dni ) {
		this.dni = dni;
	}

	public String getCorreo() {
		return correo;
	}

	public void setCorreo( String correo ) {
		this.correo = correo;
	}

	public String getFecha() {
		return fecha;
	}

	public void setFecha( String fecha ) {
		this.fecha = fecha;
	}

	public String getMoneda() {
		return moneda;
	}

	public void setMoneda( String moneda ) {
		this.moneda = moneda;
	}

	public String getMonto() {
		return monto;
	}

	public void setMonto( String monto ) {
		this.monto = monto;
	}

	public String getPeriodo() {
		return periodo;
	}

	public void setPeriodo( String periodo ) {
		this.periodo = periodo;
	}

	public String getTea() {
		return tea;
	}

	public void setTea( String tea ) {
		this.tea = tea;
	}

	public String getCuota() {
		return cuota;
	}

	public void setCuota( String cuota ) {
		this.cuota = cuota;
	}

	public String getFechaVencimiento() {
		return fechaVencimiento;
	}

	public void setFechaVencimiento( String fechaVencimiento ) {
		this.fechaVencimiento = fechaVencimiento;
	}

	public int getId() {
		return id;
	}

	public void setId( int id ) {
		this.id = id;
	}

	/* ================================================================= */
	/*  Ayudas para las vistas                                            */
	/* ================================================================= */

	/** Simbolo de la moneda elegida: "US$" o "S/.". */
	public String getSimbolo() {
		return "dolares".equals( moneda ) ? "US$" : "S/.";
	}

	/** Nombre completo de la moneda, como aparece en el formulario. */
	public String getMonedaTexto() {
		return "dolares".equals( moneda ) ? "Dolares (US$)" : "Soles (S/.)";
	}

	/** La fecha de desembolso como dd/mm/aaaa. */
	public String getFechaTexto() {
		if ( fecha == null || fecha.length() != 10 )
			return fecha;
		return fecha.substring( 8, 10 ) + "/" + fecha.substring( 5, 7 ) + "/" + fecha.substring( 0, 4 );
	}
}
