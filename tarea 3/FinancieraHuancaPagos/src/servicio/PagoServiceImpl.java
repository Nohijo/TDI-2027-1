package servicio;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import baseDeDatos.PagoDAO;
import dominio.Pago;
import dominio.ResumenTarjeta;

/**
 * Tarea 3 - Tecnologias para Desarrollos en Internet.
 *
 * Saca el PagoDAO de Spring-Datasource.xml, como hace ProyectoControl en el
 * ejemplo MultiRowDBSpring3MVC. El contexto se carga una sola vez (este
 * servicio es un bean singleton de dispatcher-servlet.xml) en lugar de en cada
 * peticion.
 *
 * @author Marquez Cristoval Miguel Angel
 */
public class PagoServiceImpl implements PagoService {

	private PagoDAO dao;

	public PagoServiceImpl() {
		ApplicationContext context = new ClassPathXmlApplicationContext( "Spring-Datasource.xml" );
		dao = (PagoDAO) context.getBean( "pagoDAO" );
	}

	@Override
	public List<Pago> consultaTodos() {
		return dao.consultaTodos();
	}

	@Override
	public void guardaPagos( List<Pago> pagos ) {

		if ( null != pagos && pagos.size() > 0 ) {
			for ( Pago pago : pagos ) {
				System.out.printf( "Pago %d \t %s \t %s \n", pago.getId(), pago.getTransaccion(), pago.getPagos() );
				dao.modificaPago( pago );
			}
		}
	}

	@Override
	public List<ResumenTarjeta> resumenPorTarjeta( List<Pago> pagos ) {

		Map<String, ResumenTarjeta> porTarjeta = new LinkedHashMap<String, ResumenTarjeta>();

		for ( Pago pago : pagos ) {
			ResumenTarjeta resumen = porTarjeta.get( pago.getTarjeta() );
			if ( resumen == null ) {
				resumen = new ResumenTarjeta( pago.getTarjeta() );
				porTarjeta.put( pago.getTarjeta(), resumen );
			}
			if ( lleno( pago.getGastos() ) )
				resumen.sumaGasto( new BigDecimal( pago.getGastos() ) );
			if ( lleno( pago.getPagos() ) )
				resumen.sumaPago( new BigDecimal( pago.getPagos() ) );
		}
		return new ArrayList<ResumenTarjeta>( porTarjeta.values() );
	}

	private boolean lleno( String valor ) {
		return valor != null && valor.trim().length() > 0;
	}
}
