package mx.unam.ciencias.tdi.util;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/**
 * Practica 2 - Tecnologias para Desarrollos en Internet.
 *
 * Punto unico para abrir conexiones a MySQL. Los datos de conexion viven en
 * db.properties (classpath) para que se puedan ajustar sin recompilar.
 */
public final class ConexionBD {

    private static final Properties PROPIEDADES = cargarPropiedades();

    static {
        // DriverManager registra los drivers disponibles la primera vez que
        // se usa, con el classloader que tenga el hilo EN ESE MOMENTO. En
        // Tomcat eso puede pasar antes de que esta aplicacion se despliegue,
        // asi que el driver de MySQL (en WEB-INF/lib) queda sin registrar y
        // getConnection() falla con "No suitable driver found". Cargar la
        // clase aqui, con el classloader de esta aplicacion, lo registra.
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            throw new IllegalStateException("No se encontro el driver de MySQL", e);
        }
    }

    private ConexionBD() {
    }

    private static Properties cargarPropiedades() {
        Properties propiedades = new Properties();
        try (InputStream in = ConexionBD.class.getResourceAsStream("/db.properties")) {
            if (in == null) {
                throw new IllegalStateException("No se encontro db.properties en el classpath");
            }
            propiedades.load(in);
        } catch (IOException e) {
            throw new IllegalStateException("No se pudo leer db.properties", e);
        }
        return propiedades;
    }

    public static Connection obtener() throws SQLException {
        return DriverManager.getConnection(
                PROPIEDADES.getProperty("db.url"),
                PROPIEDADES.getProperty("db.usuario"),
                PROPIEDADES.getProperty("db.password"));
    }
}
