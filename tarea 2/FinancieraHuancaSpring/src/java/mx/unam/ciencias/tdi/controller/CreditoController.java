package mx.unam.ciencias.tdi.controller;

import java.math.BigDecimal;
import java.time.LocalDate;

import org.springframework.beans.propertyeditors.StringTrimmerEditor;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import mx.unam.ciencias.tdi.dao.CreditoDAO;
import mx.unam.ciencias.tdi.model.Credito;
import mx.unam.ciencias.tdi.service.CalculadoraCredito;

/**
 * Tarea 2 - Tecnologias para Desarrollos en Internet.
 *
 * Controller de Spring MVC. El DispatcherServlet (declarado en web.xml) le
 * pasa las peticiones segun la URL y el metodo HTTP:
 *
 *   1. GET  /  o  /credito  -> PRIMERA vista: el formulario del enunciado.
 *   2. POST /credito        -> valida, calcula, guarda en MySQL y devuelve la
 *                              SEGUNDA vista con los datos capturados.
 *
 * Spring arma solo el objeto Credito con los parametros del formulario
 * (@ModelAttribute) y reporta en el BindingResult lo que no pudo convertir.
 *
 * @author Miguel Angel Marquez Cristoval
 */
@Controller
public class CreditoController {

    /** Cuantas solicitudes anteriores se muestran en la segunda vista. */
    private static final int ULTIMAS = 5;

    private final CreditoDAO creditoDAO;
    private final CalculadoraCredito calculadora;

    /** Spring inyecta los dos beans por el constructor. */
    public CreditoController(CreditoDAO creditoDAO, CalculadoraCredito calculadora) {
        this.creditoDAO = creditoDAO;
        this.calculadora = calculadora;
    }

    /**
     * Recorta los espacios de los campos de texto y convierte a null los que
     * llegan vacios, para que la validacion de abajo sea una sola revision.
     */
    @InitBinder
    public void configuraEnlace(WebDataBinder enlazador) {
        enlazador.registerCustomEditor(String.class, new StringTrimmerEditor(true));
    }

    /* ================================================================= */
    /*  PRIMERA VISTA: el formulario                                      */
    /* ================================================================= */

    /**
     * Muestra el formulario vacio, con la fecha de hoy y soles preseleccionados.
     * El objeto que se pone en el Model es el que la vista usa para pintar los
     * campos (y para volver a llenarlos si la validacion falla).
     */
    @GetMapping({"/", "/credito"})
    public String formulario(Model modelo) {
        Credito credito = new Credito();
        credito.setFecha(LocalDate.now());
        credito.setMoneda("soles");
        modelo.addAttribute("credito", credito);
        return "formulario";
    }

    /* ================================================================= */
    /*  SEGUNDA VISTA: los datos capturados                               */
    /* ================================================================= */

    /**
     * Recibe el formulario: valida, calcula la cuota y el vencimiento, guarda
     * la solicitud en MySQL y devuelve la vista con todo lo capturado.
     */
    @PostMapping("/credito")
    public String registrar(@ModelAttribute("credito") Credito credito,
                            BindingResult errores,
                            Model modelo) {

        valida(credito, errores);
        if (errores.hasErrors()) {
            // Regresa al formulario con lo que ya habia escrito el usuario.
            return "formulario";
        }

        credito.setCuota(calculadora.cuotaMensual(
                credito.getMonto(), credito.getTea(), credito.getPeriodo()));
        credito.setFechaVencimiento(calculadora.fechaVencimiento(
                credito.getFecha(), credito.getPeriodo()));

        creditoDAO.guardar(credito);

        modelo.addAttribute("ultimas", creditoDAO.ultimas(ULTIMAS));
        return "resultado";
    }

    /* ================================================================= */
    /*  APOYO                                                             */
    /* ================================================================= */

    /**
     * Revisa que no falte nada y que los numeros tengan sentido. Cada problema
     * se anota en el BindingResult con el nombre del campo, y la vista lo
     * imprime junto a ese campo con <form:errors>.
     */
    private static void valida(Credito credito, BindingResult errores) {
        exigeTexto(credito.getNombres(), "nombres", "Escribe los nombres.", errores);
        exigeTexto(credito.getApellidos(), "apellidos", "Escribe los apellidos.", errores);

        String dni = credito.getDni();
        if (dni == null) {
            errores.rejectValue("dni", "requerido", "Escribe el DNI.");
        } else if (!dni.matches("\\d{8}")) {
            errores.rejectValue("dni", "formato", "El DNI son 8 digitos.");
        }

        String correo = credito.getCorreo();
        if (correo != null && !correo.contains("@")) {
            errores.rejectValue("correo", "formato", "El correo no parece valido.");
        }

        if (credito.getFecha() == null && !errores.hasFieldErrors("fecha")) {
            errores.rejectValue("fecha", "requerido", "Elige la fecha.");
        }

        if (credito.getMoneda() == null) {
            errores.rejectValue("moneda", "requerido", "Elige la moneda.");
        }

        if (credito.getMonto() == null) {
            if (!errores.hasFieldErrors("monto")) {
                errores.rejectValue("monto", "requerido", "Escribe el monto.");
            }
        } else if (credito.getMonto().compareTo(BigDecimal.ZERO) <= 0) {
            errores.rejectValue("monto", "rango", "El monto debe ser mayor que cero.");
        }

        if (credito.getPeriodo() == null) {
            if (!errores.hasFieldErrors("periodo")) {
                errores.rejectValue("periodo", "requerido", "Escribe el periodo en meses.");
            }
        } else if (credito.getPeriodo() < 1) {
            errores.rejectValue("periodo", "rango", "El periodo es de al menos un mes.");
        }

        if (credito.getTea() == null) {
            if (!errores.hasFieldErrors("tea")) {
                errores.rejectValue("tea", "requerido", "Escribe la TEA.");
            }
        } else if (credito.getTea().compareTo(BigDecimal.ZERO) < 0) {
            errores.rejectValue("tea", "rango", "La TEA no puede ser negativa.");
        }
    }

    private static void exigeTexto(String valor, String campo, String mensaje,
                                   BindingResult errores) {
        if (valor == null) {
            errores.rejectValue(campo, "requerido", mensaje);
        }
    }

    /**
     * Si MySQL no responde (apagado, contrasena equivocada, base sin crear),
     * en vez de la pagina de error del servidor se muestra un aviso claro.
     */
    @ExceptionHandler(DataAccessException.class)
    public String falloLaBaseDeDatos(DataAccessException fallo, Model modelo) {
        modelo.addAttribute("mensaje", fallo.getMostSpecificCause().getMessage());
        return "error";
    }
}
