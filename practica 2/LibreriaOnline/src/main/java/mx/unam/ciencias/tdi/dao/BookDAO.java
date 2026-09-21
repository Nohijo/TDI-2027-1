package mx.unam.ciencias.tdi.dao;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicInteger;

import mx.unam.ciencias.tdi.model.Book;

/**
 * Practica 2 - Tecnologias para Desarrollos en Internet.
 *
 * DAO (Data Access Object): unico punto por el que el Controller toca los
 * datos. Aqui viven las operaciones de "base de datos" (anadir, listar,
 * buscar/filtrar/ordenar). Se guarda en memoria (una lista) para que el
 * proyecto corra sin configurar un motor de BD aparte; si mas adelante se
 * conecta una BD real, solo cambia la implementacion de esta clase.
 */
public class BookDAO {

    private final List<Book> libros = new ArrayList<Book>();
    private final AtomicInteger siguienteId = new AtomicInteger(1);

    public BookDAO() {
        // Datos de ejemplo para que la vista no arranque vacia.
        agregar(new Book(0, "Cien anios de soledad", "Gabriel Garcia Marquez", 259.00));
        agregar(new Book(0, "1984", "George Orwell", 189.90));
        agregar(new Book(0, "El Principito", "Antoine de Saint-Exupery", 99.50));
        agregar(new Book(0, "Rayuela", "Julio Cortazar", 329.00));
    }

    /** Anade un libro nuevo y le asigna un id autoincremental. */
    public synchronized void agregar(Book libro) {
        libro.setId(siguienteId.getAndIncrement());
        libros.add(libro);
    }

    /** Devuelve todos los libros almacenados. */
    public synchronized List<Book> listar() {
        return new ArrayList<Book>(libros);
    }

    /**
     * Busca y filtra/ordena el listado.
     *
     * @param texto   texto a buscar en nombre o autor (vacio/nulo = sin filtro).
     * @param campo   atributo por el que ordenar: "nombre", "autor" o "precio".
     * @param orden   "asc" o "desc".
     */
    public synchronized List<Book> buscarYOrdenar(String texto, String campo, String orden) {
        List<Book> resultado = new ArrayList<Book>();
        String filtro = (texto == null) ? "" : texto.trim().toLowerCase(Locale.forLanguageTag("es"));

        for (Book libro : libros) {
            if (filtro.isEmpty()
                    || libro.getNombre().toLowerCase(Locale.forLanguageTag("es")).contains(filtro)
                    || libro.getAutor().toLowerCase(Locale.forLanguageTag("es")).contains(filtro)) {
                resultado.add(libro);
            }
        }

        Comparator<Book> comparador = comparadorPara(campo);
        if (comparador != null) {
            resultado.sort("desc".equalsIgnoreCase(orden) ? comparador.reversed() : comparador);
        }

        return resultado;
    }

    private Comparator<Book> comparadorPara(String campo) {
        if (campo == null) {
            return null;
        }
        switch (campo) {
            case "nombre":
                return Comparator.comparing(Book::getNombre, String.CASE_INSENSITIVE_ORDER);
            case "autor":
                return Comparator.comparing(Book::getAutor, String.CASE_INSENSITIVE_ORDER);
            case "precio":
                return Comparator.comparingDouble(Book::getPrecio);
            default:
                return null;
        }
    }
}
