package mx.unam.ciencias.tdi.dao;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.List;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import mx.unam.ciencias.tdi.model.Credito;

/**
 * Tarea 2 - Tecnologias para Desarrollos en Internet.
 *
 * DAO: unico punto que habla con la tabla "creditos" de MySQL.
 *
 * Usa JdbcTemplate, la clase de Spring que envuelve JDBC: abre y cierra la
 * conexion, prepara la sentencia y traduce las SQLException a excepciones
 * propias de Spring, asi que aqui no hay try/catch/finally.
 *
 * @author Miguel Angel Marquez Cristoval
 */
@Repository
public class CreditoDAO {

    private static final String INSERTAR =
            "INSERT INTO creditos "
            + "(nombres, apellidos, dni, correo, fecha, moneda, monto, periodo, "
            + " tea, cuota, fecha_vencimiento) "
            + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

    private static final String ULTIMOS =
            "SELECT id, nombres, apellidos, dni, correo, fecha, moneda, monto, "
            + "       periodo, tea, cuota, fecha_vencimiento "
            + "FROM creditos ORDER BY id DESC LIMIT ?";

    private final JdbcTemplate plantilla;

    /**
     * Spring inyecta el JdbcTemplate declarado en spring-servlet.xml por el
     * constructor (no hace falta @Autowired cuando hay uno solo).
     */
    public CreditoDAO(JdbcTemplate plantilla) {
        this.plantilla = plantilla;
    }

    /**
     * Guarda la solicitud y le pone el id que genero MySQL.
     *
     * @param credito solicitud con los datos capturados y ya calculados.
     */
    public void guardar(final Credito credito) {
        KeyHolder llaveGenerada = new GeneratedKeyHolder();

        plantilla.update(conexion -> {
            PreparedStatement sentencia =
                    conexion.prepareStatement(INSERTAR, Statement.RETURN_GENERATED_KEYS);
            sentencia.setString(1, credito.getNombres());
            sentencia.setString(2, credito.getApellidos());
            sentencia.setString(3, credito.getDni());
            sentencia.setString(4, credito.getCorreo());
            sentencia.setObject(5, credito.getFecha());
            sentencia.setString(6, credito.getMoneda());
            sentencia.setBigDecimal(7, credito.getMonto());
            sentencia.setInt(8, credito.getPeriodo());
            sentencia.setBigDecimal(9, credito.getTea());
            sentencia.setBigDecimal(10, credito.getCuota());
            sentencia.setObject(11, credito.getFechaVencimiento());
            return sentencia;
        }, llaveGenerada);

        if (llaveGenerada.getKey() != null) {
            credito.setId(llaveGenerada.getKey().intValue());
        }
    }

    /** Las ultimas solicitudes registradas, de la mas nueva a la mas vieja. */
    public List<Credito> ultimas(int cuantas) {
        return plantilla.query(ULTIMOS, MAPEADOR, cuantas);
    }

    /** Traduce un renglon de la tabla "creditos" a un objeto Credito. */
    private static final RowMapper<Credito> MAPEADOR = new RowMapper<Credito>() {
        @Override
        public Credito mapRow(ResultSet renglon, int numero) throws SQLException {
            Credito credito = new Credito();
            credito.setId(renglon.getInt("id"));
            credito.setNombres(renglon.getString("nombres"));
            credito.setApellidos(renglon.getString("apellidos"));
            credito.setDni(renglon.getString("dni"));
            credito.setCorreo(renglon.getString("correo"));
            credito.setFecha(renglon.getObject("fecha", LocalDate.class));
            credito.setMoneda(renglon.getString("moneda"));
            credito.setMonto(renglon.getBigDecimal("monto"));
            credito.setPeriodo(renglon.getInt("periodo"));
            credito.setTea(renglon.getBigDecimal("tea"));
            credito.setCuota(renglon.getBigDecimal("cuota"));
            credito.setFechaVencimiento(renglon.getObject("fecha_vencimiento", LocalDate.class));
            return credito;
        }
    };
}
