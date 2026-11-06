package mx.unam.ciencias.tdi.libreria.service;

/**
 * Practica 3 - Tecnologias para Desarrollos en Internet.
 *
 * Se pidio editar o eliminar un libro que ya no esta en el catalogo.
 *
 * @author Miguel Angel Marquez Cristoval
 */
public class LibroNoEncontradoException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public LibroNoEncontradoException(int id) {
        super("No existe el libro con id " + id);
    }
}
