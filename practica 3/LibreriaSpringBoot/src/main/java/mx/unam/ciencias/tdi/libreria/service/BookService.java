package mx.unam.ciencias.tdi.libreria.service;

import java.util.List;

import mx.unam.ciencias.tdi.libreria.model.Book;

/**
 * Practica 3 - Tecnologias para Desarrollos en Internet.
 *
 * Logica de negocio del catalogo, entre el controlador y el repositorio.
 *
 * @author Miguel Angel Marquez Cristoval
 */
public interface BookService {

    List<Book> buscar(String texto, String campo, String orden);

    void agregar(Book libro);

    /** @throws LibroNoEncontradoException si el libro ya no existe. */
    Book actualizar(Book libro);

    /** @throws LibroNoEncontradoException si el libro ya no existe. */
    void eliminar(int id);
}
