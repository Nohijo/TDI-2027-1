package mx.unam.ciencias.tdi.libreria.controller;

import java.io.IOException;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;

import org.springframework.context.MessageSource;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.TransactionException;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;

import mx.unam.ciencias.tdi.libreria.model.Book;
import mx.unam.ciencias.tdi.libreria.service.BookService;
import mx.unam.ciencias.tdi.libreria.service.LibroNoEncontradoException;

/**
 * Practica 3 - Tecnologias para Desarrollos en Internet.
 *
 * Sustituye a BookServlet: un metodo por operacion en vez de despachar con un
 * parametro "accion". Listar y agregar responden con la vista JSP; editar y
 * eliminar los llama jQuery con $.ajax, porque un formulario HTML no puede
 * enviar PUT ni DELETE, y la pagina se actualiza sin recargarse.
 *
 * @author Miguel Angel Marquez Cristoval
 */
@Controller
@RequestMapping("/libros")
public class BookController {

    private static final String VISTA = "libros";

    private final BookService bookService;
    private final MessageSource mensajes;

    public BookController(BookService bookService, MessageSource mensajes) {
        this.bookService = bookService;
        this.mensajes = mensajes;
    }

    /** Lista el catalogo; con q busca en la base y con campo/orden lo ordena. */
    @GetMapping
    public String listar(@RequestParam(required = false) String q,
                         @RequestParam(required = false) String campo,
                         @RequestParam(required = false) String orden,
                         Model modelo) {
        modelo.addAttribute("libro", new Book());
        modelo.addAttribute("libros", bookService.buscar(q, campo, orden));
        // Se regresan para que la vista deje la busqueda como el usuario la dejo.
        modelo.addAttribute("q", q);
        modelo.addAttribute("campo", campo);
        modelo.addAttribute("orden", orden);
        return VISTA;
    }

    /** Valida y guarda; termina en redirect (Post/Redirect/Get) para no duplicar al recargar. */
    @PostMapping
    public String agregar(@Valid @ModelAttribute("libro") Book libro, BindingResult resultado,
                          Model modelo, RedirectAttributes redireccion) {
        if (resultado.hasErrors()) {
            modelo.addAttribute("libros", bookService.buscar(null, null, null));
            return VISTA;
        }
        bookService.agregar(libro);
        redireccion.addFlashAttribute("aviso", "aviso.agregado");
        return "redirect:/libros";
    }

    /** Responde el libro guardado, o 400 con el mensaje de error de cada campo. */
    @PutMapping("/{id}")
    public ResponseEntity<?> actualizar(@PathVariable int id,
                                        @Valid @ModelAttribute("libro") Book libro,
                                        BindingResult resultado, Locale idioma) {
        if (resultado.hasErrors()) {
            Map<String, String> errores = new LinkedHashMap<>();
            for (FieldError error : resultado.getFieldErrors()) {
                errores.putIfAbsent(error.getField(), mensajes.getMessage(error, idioma));
            }
            return ResponseEntity.badRequest().body(errores);
        }
        libro.setId(id);
        return ResponseEntity.ok(bookService.actualizar(libro));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable int id) {
        bookService.eliminar(id);
        return ResponseEntity.noContent().build();
    }

    @ExceptionHandler(LibroNoEncontradoException.class)
    public ResponseEntity<Void> libroNoEncontrado() {
        return ResponseEntity.notFound().build();
    }

    /**
     * Por ejemplo, MySQL no esta corriendo: se muestra la vista con el catalogo
     * vacio y un aviso, en vez de la pagina de error generica. Una JSP no puede
     * responder a PUT ni DELETE; ahi basta el codigo, y jQuery muestra el aviso.
     * TransactionException es lo que llega cuando ni siquiera se pudo abrir la
     * conexion para iniciar la transaccion del servicio.
     */
    @ExceptionHandler({DataAccessException.class, TransactionException.class})
    public ModelAndView errorDeBaseDeDatos(HttpServletRequest peticion,
                                           HttpServletResponse respuesta) throws IOException {
        String metodo = peticion.getMethod();
        if (!"GET".equals(metodo) && !"POST".equals(metodo)) {
            respuesta.sendError(HttpStatus.SERVICE_UNAVAILABLE.value());
            return null;
        }
        ModelAndView vista = new ModelAndView(VISTA, HttpStatus.SERVICE_UNAVAILABLE);
        vista.addObject("libro", new Book());
        vista.addObject("libros", Collections.emptyList());
        vista.addObject("errorBD", Boolean.TRUE);
        return vista;
    }
}
