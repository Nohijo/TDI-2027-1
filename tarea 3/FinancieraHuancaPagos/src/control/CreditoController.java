package control;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.HttpSessionRequiredException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.SessionAttributes;

import dominio.Credito;
import servicio.CreditoService;
import servicio.PagoService;
import validator.CreditoValidator;

/**
 * Tarea 3 - Tecnologias para Desarrollos en Internet.
 *
 * Controller de Spring MVC, con la misma forma que UserController del ejemplo
 * FormularioDBSpring3MVC, mas la tabla de pagos de MultiRowDBSpring3MVC:
 *
 *   GET  /creditoRegistro.htm -> PRIMERA vista: el formulario del enunciado y,
 *                                debajo, la tabla de pagos leida de MySQL.
 *   POST /creditoRegistro.htm -> valida; si todo esta bien calcula, guarda la
 *                                solicitud y la columna Pagos en MySQL y
 *                                redirige a la SEGUNDA vista.
 *
 * @SessionAttributes deja el objeto Credito en la sesion: la vista de exito
 * muestra lo capturado despues del redirect y, al enviar el formulario, Spring
 * escribe sobre el mismo objeto. Por eso del navegador solo llega la columna
 * Pagos; fecha, mes, tarjeta, transaccion y gastos se quedan como salieron de
 * la base de datos.
 *
 * @author Marquez Cristoval Miguel Angel
 */
@Controller
@RequestMapping( "/creditoRegistro.htm" )
@SessionAttributes( "credito" )
public class CreditoController {

	private CreditoService creditoService;
	private PagoService pagoService;
	private CreditoValidator creditoValidator;

	@Autowired
	public CreditoController( CreditoService creditoService, PagoService pagoService,
			CreditoValidator creditoValidator ) {
		this.creditoService = creditoService;
		this.pagoService = pagoService;
		this.creditoValidator = creditoValidator;
	}

	@RequestMapping( method = RequestMethod.GET )
	public String mostrarFormulario( ModelMap model ) {

		Credito credito = new Credito();
		credito.setFecha( new SimpleDateFormat( "yyyy-MM-dd" ).format( new Date() ) );
		credito.setMoneda( "soles" );
		credito.setListaPagos( pagoService.consultaTodos() );
		model.addAttribute( "credito", credito );
		return "creditoForm";
	}

	@RequestMapping( method = RequestMethod.POST )
	public String onSubmit( @ModelAttribute("credito") Credito credito, BindingResult result ) {

		creditoValidator.validate( credito, result );
		if ( result.hasErrors() )
			return "creditoForm";

		creditoService.add( credito );
		pagoService.guardaPagos( credito.getListaPagos() );
		return "redirect:creditoExito.htm";
	}

	/**
	 * Si la sesion expiro (o se reinicio el servidor) con el formulario abierto,
	 * el Credito ya no esta en la sesion y Spring no puede enlazar el POST: en
	 * lugar de un error 500 se vuelve a mostrar el formulario.
	 */
	@ExceptionHandler( HttpSessionRequiredException.class )
	public String sesionExpirada() {
		return "redirect:creditoRegistro.htm";
	}
}
