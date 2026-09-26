package control;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

import servicio.CreditoService;

/**
 * Tarea 2 - Tecnologias para Desarrollos en Internet.
 *
 * SEGUNDA vista: muestra lo capturado (el Credito quedo en la sesion) y las
 * ultimas solicitudes que hay en MySQL. Es el equivalente de
 * UserSuccessController del ejemplo.
 *
 * @author Marquez Cristoval Miguel Angel
 */
@Controller
public class CreditoExitoController {

	/** Cuantas solicitudes anteriores se listan. */
	private static final int ULTIMAS = 5;

	private CreditoService creditoService;

	@Autowired
	public CreditoExitoController( CreditoService creditoService ) {
		this.creditoService = creditoService;
	}

	@RequestMapping( "/creditoExito.htm" )
	public String mostrarExito( ModelMap model ) {
		model.addAttribute( "ultimas", creditoService.ultimas( ULTIMAS ) );
		return "creditoExito";
	}
}
