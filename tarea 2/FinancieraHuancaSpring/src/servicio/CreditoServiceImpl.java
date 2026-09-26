package servicio;

import java.text.DecimalFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

import baseDeDatos.CreditosJDBCTemplate;
import dominio.Credito;

/**
 * Tarea 2 - Tecnologias para Desarrollos en Internet.
 *
 * Aqui vive el negocio: la cuota mensual por el sistema frances y la fecha de
 * vencimiento. Despues le pide al DAO que guarde la solicitud, igual que
 * UserServiceImpl del ejemplo.
 *
 * @author Marquez Cristoval Miguel Angel
 */
public class CreditoServiceImpl implements CreditoService {

	@Override
	public void add( Credito credito ) {

		double monto = Double.parseDouble( credito.getMonto() );
		double tea = Double.parseDouble( credito.getTea() );
		int periodo = Integer.parseInt( credito.getPeriodo() );

		credito.setCuota( new DecimalFormat( "0.00" ).format( cuotaMensual( monto, tea, periodo ) ) );
		credito.setFechaVencimiento( vencimiento( credito.getFecha(), periodo ) );

		System.out.println( "Solicitud de credito recibida:" );
		System.out.println( "  Solicitante: " + credito.getNombres() + " " + credito.getApellidos() );
		System.out.println( "  DNI: " + credito.getDni() );
		System.out.println( "  Fecha: " + credito.getFecha() + "   Moneda: " + credito.getMoneda() );
		System.out.println( "  Monto: " + credito.getMonto() + "   Periodo: " + periodo + " meses" );
		System.out.println( "  TEA: " + credito.getTea() + " %" );
		System.out.println( "  Cuota calculada: " + credito.getCuota() );
		System.out.println( "  Vence: " + credito.getFechaVencimiento() );

		CreditosJDBCTemplate conn = new CreditosJDBCTemplate();
		try {
			conn.insertarC( credito );
		} catch ( Exception e ) {
			System.out.println( "Error al guardar la solicitud: " + e.toString() );
		}
	}

	@Override
	public List<Credito> ultimas( int cuantas ) {
		return new CreditosJDBCTemplate().ultimas( cuantas );
	}

	/**
	 * Cuota mensual por el sistema frances (cuotas fijas):
	 *
	 *     cuota = M * i / ( 1 - (1 + i)^(-n) )
	 *
	 * M = monto, n = numero de meses e i = tasa mensual equivalente a la TEA:
	 *
	 *     i = (1 + TEA)^(1/12) - 1
	 */
	private double cuotaMensual( double monto, double teaPorcentaje, int meses ) {

		double tasaMensual = Math.pow( 1 + teaPorcentaje / 100.0, 1.0 / 12.0 ) - 1;
		if ( tasaMensual == 0 )
			return monto / meses;
		return monto * tasaMensual / ( 1 - Math.pow( 1 + tasaMensual, -meses ) );
	}

	/** Fecha de desembolso (aaaa-mm-dd) mas el plazo en meses, en dd/mm/aaaa. */
	private String vencimiento( String fecha, int meses ) {

		int anio = Integer.parseInt( fecha.substring( 0, 4 ) );
		int mes = Integer.parseInt( fecha.substring( 5, 7 ) );
		int dia = Integer.parseInt( fecha.substring( 8, 10 ) );

		Calendar calendario = new GregorianCalendar( anio, mes - 1, dia );
		calendario.add( Calendar.MONTH, meses );

		DecimalFormat dosDigitos = new DecimalFormat( "00" );
		return dosDigitos.format( calendario.get( Calendar.DAY_OF_MONTH ) ) + "/"
				+ dosDigitos.format( calendario.get( Calendar.MONTH ) + 1 ) + "/"
				+ calendario.get( Calendar.YEAR );
	}
}
