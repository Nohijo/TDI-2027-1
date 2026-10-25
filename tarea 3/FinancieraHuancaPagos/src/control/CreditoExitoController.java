package control;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

import dominio.Pago;
import servicio.CreditoService;
import servicio.PagoService;

/**
 * Tarea 3 - Tecnologias para Desarrollos en Internet.
 *
 * SEGUNDA vista: muestra lo capturado (el Credito quedo en la sesion), los
 * pagos con tarjeta tal como quedaron en MySQL, los totales por tarjeta y las
 * ultimas solicitudes. Es el equivalente de UserSuccessController del ejemplo.
 *
 * @author Marquez Cristoval Miguel Angel
 */
@Controller
public class CreditoExitoController {

	/** Cuantas solicitudes anteriores se listan. */
	private static final int ULTIMAS = 5;

	private CreditoService creditoService;
	private PagoService pagoService;

	@Autowired
	public CreditoExitoController( CreditoService creditoService, PagoService pagoService ) {
		this.creditoService = creditoService;
		this.pagoService = pagoService;
	}

	@RequestMapping( "/creditoExito.htm" )
	public String mostrarExito( ModelMap model ) {

		// Se vuelve a leer de MySQL para mostrar lo que de verdad se guardo.
		List<Pago> pagos = pagoService.consultaTodos();

		model.addAttribute( "pagos", pagos );
		model.addAttribute( "resumen", pagoService.resumenPorTarjeta( pagos ) );
		model.addAttribute( "ultimas", creditoService.ultimas( ULTIMAS ) );
		return "creditoExito";
	}
}
