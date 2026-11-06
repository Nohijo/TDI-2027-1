package mx.unam.ciencias.tdi.libreria.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import mx.unam.ciencias.tdi.libreria.model.Book;
import mx.unam.ciencias.tdi.libreria.repository.BookRepository;

/**
 * Practica 3 - Tecnologias para Desarrollos en Internet.
 *
 * Quita los espacios sobrantes antes de guardar y convierte "no se modifico
 * ningun renglon" en un error de negocio que el controlador pueda responder.
 *
 * @author Miguel Angel Marquez Cristoval
 */
@Service
@Transactional
public class BookServiceImpl implements BookService {

    private final BookRepository repositorio;

    public BookServiceImpl(BookRepository repositorio) {
        this.repositorio = repositorio;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Book> buscar(String texto, String campo, String orden) {
        return repositorio.buscar(texto, campo, orden);
    }

    @Override
    public void agregar(Book libro) {
        recortarTextos(libro);
        repositorio.agregar(libro);
    }

    @Override
    public Book actualizar(Book libro) {
        recortarTextos(libro);
        if (!repositorio.actualizar(libro)) {
            throw new LibroNoEncontradoException(libro.getId());
        }
        return libro;
    }

    @Override
    public void eliminar(int id) {
        if (!repositorio.eliminar(id)) {
            throw new LibroNoEncontradoException(id);
        }
    }

    private static void recortarTextos(Book libro) {
        libro.setNombre(libro.getNombre().trim());
        libro.setAutor(libro.getAutor().trim());
    }
}
