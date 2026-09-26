package control;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.SessionAttributes;

import dominio.Credito;
import servicio.CreditoService;
import validator.CreditoValidator;

/**
 * Tarea 2 - Tecnologias para Desarrollos en Internet.
 *
 * Controller de Spring MVC, con la misma forma que UserController del ejemplo
 * FormularioDBSpring3MVC:
 *
 *   GET  /creditoRegistro.htm -> PRIMERA vista: el formulario del enunciado.
 *   POST /creditoRegistro.htm -> valida; si todo esta bien calcula, guarda en
 *                                MySQL y redirige a la SEGUNDA vista.
 *
 * @SessionAttributes deja el objeto Credito en la sesion para que la vista de
 * exito pueda mostrar lo capturado despues del redirect.
 *
 * @author Marquez Cristoval Miguel Angel
 */
@Controller
@RequestMapping( "/creditoRegistro.htm" )
@SessionAttributes( "credito" )
public class CreditoController {

	private CreditoService creditoService;
	private CreditoValidator creditoValidator;

	@Autowired
	public CreditoController( CreditoService creditoService, CreditoValidator creditoValidator ) {
		this.creditoService = creditoService;
		this.creditoValidator = creditoValidator;
	}

	@RequestMapping( method = RequestMethod.GET )
	public String mostrarFormulario( ModelMap model ) {

		Credito credito = new Credito();
		credito.setFecha( new SimpleDateFormat( "yyyy-MM-dd" ).format( new Date() ) );
		credito.setMoneda( "soles" );
		model.addAttribute( "credito", credito );
		return "creditoForm";
	}

	@RequestMapping( method = RequestMethod.POST )
	public String onSubmit( @ModelAttribute("credito") Credito credito, BindingResult result ) {

		creditoValidator.validate( credito, result );
		if ( result.hasErrors() )
			return "creditoForm";

		creditoService.add( credito );
		return "redirect:creditoExito.htm";
	}
}
