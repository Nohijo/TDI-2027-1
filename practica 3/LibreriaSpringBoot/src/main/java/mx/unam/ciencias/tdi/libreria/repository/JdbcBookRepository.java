package mx.unam.ciencias.tdi.libreria.repository;

import java.util.ArrayList;
import java.util.List;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import mx.unam.ciencias.tdi.libreria.model.Book;

/**
 * Practica 3 - Tecnologias para Desarrollos en Internet.
 *
 * Implementacion con JdbcTemplate; sustituye al BookDAO manual de la Practica
 * 2 (mismas consultas, sin abrir conexiones ni recorrer ResultSet a mano).
 *
 * @author Miguel Angel Marquez Cristoval
 */
@Repository
public class JdbcBookRepository implements BookRepository {

    private static final RowMapper<Book> MAPEADOR = (fila, numero) -> new Book(
            fila.getInt("id"),
            fila.getString("nombre"),
            fila.getString("autor"),
            fila.getBigDecimal("precio"));

    private final JdbcTemplate jdbc;

    public JdbcBookRepository(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    @Override
    public List<Book> buscar(String texto, String campo, String orden) {
        StringBuilder sql = new StringBuilder("SELECT id, nombre, autor, precio FROM libros");
        List<Object> parametros = new ArrayList<>();

        if (texto != null && !texto.trim().isEmpty()) {
            String comodin = "%" + texto.trim() + "%";
            sql.append(" WHERE nombre LIKE ? OR autor LIKE ?");
            parametros.add(comodin);
            parametros.add(comodin);
        }

        // El nombre de columna no se puede parametrizar con "?": se valida
        // contra una lista fija (columnaValida) para no exponer la consulta
        // a inyeccion SQL.
        sql.append(" ORDER BY ").append(columnaValida(campo));
        if ("desc".equalsIgnoreCase(orden)) {
            sql.append(" DESC");
        }

        return jdbc.query(sql.toString(), MAPEADOR, parametros.toArray());
    }

    @Override
    public void agregar(Book libro) {
        jdbc.update("INSERT INTO libros (nombre, autor, precio) VALUES (?, ?, ?)",
                libro.getNombre(), libro.getAutor(), libro.getPrecio());
    }

    @Override
    public boolean actualizar(Book libro) {
        return jdbc.update("UPDATE libros SET nombre = ?, autor = ?, precio = ? WHERE id = ?",
                libro.getNombre(), libro.getAutor(), libro.getPrecio(), libro.getId()) > 0;
    }

    @Override
    public boolean eliminar(int id) {
        return jdbc.update("DELETE FROM libros WHERE id = ?", id) > 0;
    }

    private static String columnaValida(String campo) {
        if (campo == null) {
            return "id";
        }
        switch (campo) {
            case "nombre":
            case "autor":
            case "precio":
                return campo;
            default:
                return "id";
        }
    }
}
