package mx.unam.ciencias.tdi.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import mx.unam.ciencias.tdi.model.Book;
import mx.unam.ciencias.tdi.util.ConexionBD;

/**
 * Practica 2 - Tecnologias para Desarrollos en Internet.
 *
 * DAO (Data Access Object): unico punto por el que el Controller toca los
 * datos. Habla con la tabla "libros" de MySQL (ver esquema.sql) usando JDBC
 * con PreparedStatement, para anadir, listar y buscar/filtrar/ordenar.
 *
 * @author Miguel Angel Marquez Cristoval
 */
public class BookDAO {

    /** Anade un libro nuevo; MySQL le asigna el id (AUTO_INCREMENT). */
    public void agregar(Book libro) {
        String sql = "INSERT INTO libros (nombre, autor, precio) VALUES (?, ?, ?)";
        try (Connection conexion = ConexionBD.obtener();
             PreparedStatement sentencia = conexion.prepareStatement(sql)) {

            sentencia.setString(1, libro.getNombre());
            sentencia.setString(2, libro.getAutor());
            sentencia.setDouble(3, libro.getPrecio());
            sentencia.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("No se pudo agregar el libro", e);
        }
    }

    /** Devuelve todos los libros almacenados. */
    public List<Book> listar() {
        return buscarYOrdenar(null, null, null);
    }

    /**
     * Busca y filtra/ordena el listado.
     *
     * @param texto texto a buscar en nombre o autor (vacio/nulo = sin filtro).
     * @param campo atributo por el que ordenar: "nombre", "autor" o "precio".
     * @param orden "asc" o "desc".
     */
    public List<Book> buscarYOrdenar(String texto, String campo, String orden) {
        List<Book> resultado = new ArrayList<Book>();
        boolean hayFiltro = texto != null && !texto.trim().isEmpty();

        StringBuilder sql = new StringBuilder("SELECT id, nombre, autor, precio FROM libros");
        if (hayFiltro) {
            sql.append(" WHERE nombre LIKE ? OR autor LIKE ?");
        }
        // El nombre de columna no se puede parametrizar con "?": se valida
        // contra una lista fija (columnaValida) para no exponer la consulta
        // a inyeccion SQL.
        String columna = columnaValida(campo);
        if (columna != null) {
            sql.append(" ORDER BY ").append(columna);
            if ("desc".equalsIgnoreCase(orden)) {
                sql.append(" DESC");
            }
        }

        try (Connection conexion = ConexionBD.obtener();
             PreparedStatement sentencia = conexion.prepareStatement(sql.toString())) {

            if (hayFiltro) {
                String comodin = "%" + texto.trim() + "%";
                sentencia.setString(1, comodin);
                sentencia.setString(2, comodin);
            }

            try (ResultSet filas = sentencia.executeQuery()) {
                while (filas.next()) {
                    resultado.add(new Book(
                            filas.getInt("id"),
                            filas.getString("nombre"),
                            filas.getString("autor"),
                            filas.getDouble("precio")));
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("No se pudo consultar el catalogo", e);
        }

        return resultado;
    }

    private static String columnaValida(String campo) {
        if (campo == null) {
            return null;
        }
        switch (campo) {
            case "nombre":
            case "autor":
            case "precio":
                return campo;
            default:
                return null;
        }
    }
}
