package dominio;

/**
 * Tarea 2 - Tecnologias para Desarrollos en Internet.
 *
 * Guarda los datos de conexion a MySQL que Spring lee de servicio.properties
 * (ver Spring-Datasource.xml). Igual que en el ejemplo FormularioDBSpring3MVC.
 *
 * @author Marquez Cristoval Miguel Angel
 */
public class BDMySql {

	private String urlBD;
	private String user;
	private String password;

	public String getUrlBD() {
		return urlBD;
	}

	public void setUrlBD( String urlBD ) {
		this.urlBD = urlBD;
	}

	public String getUser() {
		return user;
	}

	public void setUser( String user ) {
		this.user = user;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword( String password ) {
		this.password = password;
	}
}
