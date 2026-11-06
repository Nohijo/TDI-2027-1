package mx.unam.ciencias.tdi.libreria.repository;

import java.util.List;

import mx.unam.ciencias.tdi.libreria.model.Book;

/**
 * Practica 3 - Tecnologias para Desarrollos en Internet.
 *
 * Acceso a los datos de la tabla "libros".
 *
 * @author Miguel Angel Marquez Cristoval
 */
public interface BookRepository {

    /**
     * Busca y ordena el catalogo.
     *
     * @param texto texto a buscar en nombre o autor (vacio/nulo = sin filtro).
     * @param campo atributo por el que ordenar: "nombre", "autor" o "precio".
     * @param orden "asc" o "desc".
     */
    List<Book> buscar(String texto, String campo, String orden);

    /** Agrega un libro nuevo; MySQL le asigna el id (AUTO_INCREMENT). */
    void agregar(Book libro);

    /** @return false si no existe un libro con ese id. */
    boolean actualizar(Book libro);

    /** @return false si no existe un libro con ese id. */
    boolean eliminar(int id);
}
