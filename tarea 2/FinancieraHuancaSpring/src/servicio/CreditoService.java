package servicio;

import java.util.List;

import dominio.Credito;

/**
 * Tarea 2 - Tecnologias para Desarrollos en Internet.
 *
 * Servicio de la solicitud de credito, como UserService en el ejemplo.
 *
 * @author Marquez Cristoval Miguel Angel
 */
public interface CreditoService {

	/** Calcula la cuota y el vencimiento, y guarda la solicitud en MySQL. */
	public void add( Credito credito );

	/** Las ultimas solicitudes registradas, de la mas nueva a la mas vieja. */
	public List<Credito> ultimas( int cuantas );
}
