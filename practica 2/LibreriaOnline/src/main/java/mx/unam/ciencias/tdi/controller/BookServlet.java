package mx.unam.ciencias.tdi.controller;

import java.io.IOException;
import java.util.List;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import mx.unam.ciencias.tdi.dao.BookDAO;
import mx.unam.ciencias.tdi.model.Book;

/**
 * Practica 2 - Tecnologias para Desarrollos en Internet.
 *
 * Controller de la arquitectura MVC: unico servlet que recibe las acciones
 * del usuario (agregar, buscar, listar), habla con el DAO/Model y reenvia
 * (forward) la peticion a la vista JSP (libreria.jsp).
 *
 * Compatible con Apache Tomcat 7 (Servlet 3.0, paquete javax.servlet) y Java 8.
 * El mapeo a la URL /libreria esta en WEB-INF/web.xml.
 */
public class BookServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    private static final String VISTA = "/libreria.jsp";

    // Una sola instancia del DAO para toda la aplicacion (Tomcat crea un solo
    // servlet por clase y atiende las peticiones con hilos, no instancias).
    private BookDAO bookDAO;

    @Override
    public void init() throws ServletException {
        bookDAO = new BookDAO();
    }

    @Override
    protected void doGet(HttpServletRequest peticion, HttpServletResponse respuesta)
            throws ServletException, IOException {
        atender(peticion, respuesta);
    }

    @Override
    protected void doPost(HttpServletRequest peticion, HttpServletResponse respuesta)
            throws ServletException, IOException {
        peticion.setCharacterEncoding("UTF-8");
        atender(peticion, respuesta);
    }

    /** Despacha segun la accion pedida: agregar, buscar o listar (por omision). */
    private void atender(HttpServletRequest peticion, HttpServletResponse respuesta)
            throws ServletException, IOException {

        String accion = peticion.getParameter("accion");
        if (accion == null) {
            accion = "listar";
        }

        switch (accion) {
            case "agregar":
                agregar(peticion, respuesta);
                return; // agregar termina en un redirect, no en forward.
            case "buscar":
                buscar(peticion);
                break;
            default:
                listar(peticion);
        }

        mostrarVista(peticion, respuesta);
    }

    /** Accion "listar": todos los libros, sin filtro. */
    private void listar(HttpServletRequest peticion) {
        peticion.setAttribute("libros", bookDAO.listar());
    }

    /** Accion "buscar": busca por texto y/o ordena por un atributo. */
    private void buscar(HttpServletRequest peticion) {
        String q = peticion.getParameter("q");
        String campo = peticion.getParameter("campo");
        String orden = peticion.getParameter("orden");

        List<Book> resultado = bookDAO.buscarYOrdenar(q, campo, orden);

        peticion.setAttribute("libros", resultado);
        // Se regresan para que la vista deje el formulario de busqueda tal
        // como el usuario lo dejo (texto buscado, campo y orden elegidos).
        peticion.setAttribute("q", q);
        peticion.setAttribute("campo", campo);
        peticion.setAttribute("orden", orden);
    }

    /** Accion "agregar": valida, guarda el libro nuevo y redirige a listar. */
    private void agregar(HttpServletRequest peticion, HttpServletResponse respuesta)
            throws IOException {

        String contexto = peticion.getContextPath();
        String nombre = recortar(peticion.getParameter("nombre"));
        String autor = recortar(peticion.getParameter("autor"));
        String precioTexto = recortar(peticion.getParameter("precio"));

        double precio;
        try {
            precio = Double.parseDouble(precioTexto);
        } catch (NumberFormatException e) {
            precio = -1;
        }

        if (nombre.isEmpty() || autor.isEmpty() || precio < 0) {
            respuesta.sendRedirect(contexto + "/libreria?accion=listar&msg=error");
            return;
        }

        bookDAO.agregar(new Book(0, nombre, autor, precio));
        // Patron Post/Redirect/Get: evita que al recargar la pagina se vuelva
        // a enviar el formulario y se duplique el libro.
        respuesta.sendRedirect(contexto + "/libreria?accion=listar&msg=ok");
    }

    private void mostrarVista(HttpServletRequest peticion, HttpServletResponse respuesta)
            throws ServletException, IOException {
        respuesta.setContentType("text/html;charset=UTF-8");
        RequestDispatcher dispatcher = peticion.getRequestDispatcher(VISTA);
        dispatcher.forward(peticion, respuesta);
    }

    private static String recortar(String valor) {
        return valor == null ? "" : valor.trim();
    }
}
