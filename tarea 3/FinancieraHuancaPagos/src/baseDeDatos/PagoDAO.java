package baseDeDatos;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.jdbc.core.simple.ParameterizedRowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcDaoSupport;
import org.springframework.jdbc.core.simple.SimpleJdbcTemplate;

import dominio.Pago;

/**
 * Tarea 3 - Tecnologias para Desarrollos en Internet.
 *
 * Lee y guarda la tabla pagos_tarjeta. Esta hecho como ProyectoDAO del ejemplo
 * MultiRowDBSpring3MVC: extiende SimpleJdbcDaoSupport y Spring le pasa el
 * DataSource (bean "pagoDAO" en Spring-Datasource.xml).
 *
 * La unica columna que se modifica es "pagos": las demas son de solo lectura.
 *
 * @author Marquez Cristoval Miguel Angel
 */
public class PagoDAO extends SimpleJdbcDaoSupport {

	// La fecha sale ya como texto dd/mm/aaaa: asi el conector no le aplica la
	// zona horaria y no se recorre un dia (el mismo problema de la Tarea 2).
	private static final String CONSULTATODOS =
			"SELECT id, DATE_FORMAT( fecha, '%d/%m/%Y' ) AS fecha, mes, tarjeta, transaccion, gastos, pagos"
			+ " FROM pagos_tarjeta ORDER BY id";
	private static final String MODIFICAPAGO =
			"UPDATE pagos_tarjeta SET pagos = ? WHERE id = ?";

	class PagosRowMapper implements ParameterizedRowMapper<Pago> {

		public Pago mapRow( ResultSet rs, int numeroRenglon ) throws SQLException {

			Pago to = new Pago();
			to.setId( rs.getInt( "id" ) );
			to.setFecha( rs.getString( "fecha" ) );
			to.setMes( rs.getString( "mes" ) );
			to.setTarjeta( rs.getString( "tarjeta" ) );
			to.setTransaccion( rs.getString( "transaccion" ) );
			to.setGastos( texto( rs.getBigDecimal( "gastos" ) ) );
			to.setPagos( texto( rs.getBigDecimal( "pagos" ) ) );
			return to;
		}
	}

	public List<Pago> consultaTodos() {

		SimpleJdbcTemplate sjdbc = getSimpleJdbcTemplate();
		return new ArrayList<Pago>( sjdbc.query( CONSULTATODOS, new PagosRowMapper() ) );
	}

	/** Guarda lo que el usuario escribio en la columna Pagos (vacio = NULL). */
	public void modificaPago( Pago pago ) {

		SimpleJdbcTemplate sjdbct = getSimpleJdbcTemplate();
		sjdbct.update( MODIFICAPAGO, numero( pago.getPagos() ), pago.getId() );
	}

	/** NULL de MySQL -> cadena vacia en el bean. */
	private static String texto( BigDecimal valor ) {
		return valor == null ? "" : valor.toPlainString();
	}

	/** Cadena vacia del formulario -> NULL en MySQL. */
	private static BigDecimal numero( String valor ) {
		if ( valor == null || valor.trim().length() == 0 )
			return null;
		return new BigDecimal( valor.trim() );
	}
}
