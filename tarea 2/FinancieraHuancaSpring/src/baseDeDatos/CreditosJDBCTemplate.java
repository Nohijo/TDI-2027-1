package baseDeDatos;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import javax.naming.NamingException;

import com.mysql.cj.jdbc.MysqlDataSource;   // Version mysql-connector-java-8.0.18

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import dominio.BDMySql;
import dominio.Credito;

/**
 * Tarea 2 - Tecnologias para Desarrollos en Internet.
 *
 * Unico punto que habla con la tabla "creditos" de MySQL. Abre la conexion
 * igual que UsuariosJDBCTemplate del ejemplo: un MysqlDataSource con los datos
 * que Spring saca de servicio.properties a traves de Spring-Datasource.xml.
 *
 * @author Marquez Cristoval Miguel Angel
 */
public class CreditosJDBCTemplate {

	public Connection getConexion() {

		MysqlDataSource ds = null;
		Connection connect = null;

		try {
			ds = new MysqlDataSource();

			ApplicationContext context = new ClassPathXmlApplicationContext( "Spring-Datasource.xml" );
			BDMySql bdMySql = (BDMySql) context.getBean( "servicioPropiedades" );

			ds.setUrl( bdMySql.getUrlBD() );
			ds.setUser( bdMySql.getUser() );
			ds.setPassword( bdMySql.getPassword() );
			connect = ds.getConnection();
			return connect;
		} catch ( SQLException error ) {
			System.out.println( error.toString() );
			return connect;
		}
	}

	/** Guarda la solicitud y le pone el folio que genero MySQL. */
	public void insertarC( Credito credito ) throws NamingException {

		Connection connect = null;
		PreparedStatement ps = null;

		try {
			connect = getConexion();

			String sql = "insert into creditos ( nombres, apellidos, dni, correo, fecha, moneda,"
					+ " monto, periodo, tea, cuota, fecha_vencimiento )"
					+ " values ( ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ? )";

			ps = connect.prepareStatement( sql, Statement.RETURN_GENERATED_KEYS );
			ps.setString( 1, credito.getNombres() );
			ps.setString( 2, credito.getApellidos() );
			ps.setString( 3, credito.getDni() );
			ps.setString( 4, credito.getCorreo() );
			// Las fechas van y vienen como texto "aaaa-mm-dd": con setDate/getDate
			// el conector aplica la zona horaria (serverTimezone) y la fecha se
			// puede recorrer un dia. Como texto no hay conversion.
			ps.setString( 5, credito.getFecha() );
			ps.setString( 6, credito.getMoneda() );
			ps.setBigDecimal( 7, new java.math.BigDecimal( credito.getMonto() ) );
			ps.setInt( 8, Integer.parseInt( credito.getPeriodo() ) );
			ps.setBigDecimal( 9, new java.math.BigDecimal( credito.getTea() ) );
			ps.setBigDecimal( 10, new java.math.BigDecimal( credito.getCuota() ) );
			ps.setString( 11, aISO( credito.getFechaVencimiento() ) );
			ps.executeUpdate();

			ResultSet llaves = ps.getGeneratedKeys();
			if ( llaves.next() )
				credito.setId( llaves.getInt( 1 ) );

			System.out.println( "Solicitud guardada con folio " + credito.getId() );

		} catch ( SQLException error ) {
			System.out.println( error.toString() );
		} finally {
			try { if ( ps != null ) ps.close(); }
			  catch ( SQLException error ) { System.out.println( "Error Statement : " + error.toString() ); }
			try { if ( connect != null ) connect.close(); }
			  catch ( SQLException error ) { System.out.println( "Error Connect : " + error.toString() ); }
		}
	}

	/** Las ultimas solicitudes registradas, de la mas nueva a la mas vieja. */
	public List<Credito> ultimas( int cuantas ) {

		List<Credito> lista = new ArrayList<Credito>();
		Connection connect = null;
		PreparedStatement ps = null;

		try {
			connect = getConexion();

			String sql = "select id, nombres, apellidos, dni, correo, fecha, moneda, monto,"
					+ " periodo, tea, cuota, fecha_vencimiento"
					+ " from creditos order by id desc limit ?";

			ps = connect.prepareStatement( sql );
			ps.setInt( 1, cuantas );
			ResultSet renglones = ps.executeQuery();

			while ( renglones.next() ) {
				Credito credito = new Credito();
				credito.setId( renglones.getInt( "id" ) );
				credito.setNombres( renglones.getString( "nombres" ) );
				credito.setApellidos( renglones.getString( "apellidos" ) );
				credito.setDni( renglones.getString( "dni" ) );
				credito.setCorreo( renglones.getString( "correo" ) );
				credito.setFecha( renglones.getString( "fecha" ) );
				credito.setMoneda( renglones.getString( "moneda" ) );
				credito.setMonto( renglones.getBigDecimal( "monto" ).toPlainString() );
				credito.setPeriodo( String.valueOf( renglones.getInt( "periodo" ) ) );
				credito.setTea( renglones.getBigDecimal( "tea" ).toPlainString() );
				credito.setCuota( renglones.getBigDecimal( "cuota" ).toPlainString() );
				credito.setFechaVencimiento( aDMA( renglones.getString( "fecha_vencimiento" ) ) );
				lista.add( credito );
			}
		} catch ( SQLException error ) {
			System.out.println( error.toString() );
		} finally {
			try { if ( ps != null ) ps.close(); }
			  catch ( SQLException error ) { System.out.println( "Error Statement : " + error.toString() ); }
			try { if ( connect != null ) connect.close(); }
			  catch ( SQLException error ) { System.out.println( "Error Connect : " + error.toString() ); }
		}
		return lista;
	}

	/** dd/mm/aaaa -> aaaa-mm-dd (lo que entiende MySQL). */
	private String aISO( String fecha ) {
		return fecha.substring( 6, 10 ) + "-" + fecha.substring( 3, 5 ) + "-" + fecha.substring( 0, 2 );
	}

	/** aaaa-mm-dd -> dd/mm/aaaa (lo que se ve en las vistas). */
	private String aDMA( String fecha ) {
		return fecha.substring( 8, 10 ) + "/" + fecha.substring( 5, 7 ) + "/" + fecha.substring( 0, 4 );
	}
}
