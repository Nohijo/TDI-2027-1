package validator;

import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

import dominio.Credito;

/**
 * Tarea 2 - Tecnologias para Desarrollos en Internet.
 *
 * Revisa lo que llego del formulario. Sigue el mismo patron que
 * UserValidator del ejemplo: implementa Validator de Spring y registra cada
 * problema con una clave de messages.properties.
 *
 * @author Marquez Cristoval Miguel Angel
 */
public class CreditoValidator implements Validator {

	@Override
	public boolean supports( Class<?> clazz ) {
		return Credito.class.isAssignableFrom( clazz );
	}

	@Override
	public void validate( Object target, Errors errors ) {

		ValidationUtils.rejectIfEmptyOrWhitespace( errors, "nombres", "nombres.required" );
		ValidationUtils.rejectIfEmptyOrWhitespace( errors, "apellidos", "apellidos.required" );
		ValidationUtils.rejectIfEmptyOrWhitespace( errors, "dni", "dni.required" );
		ValidationUtils.rejectIfEmptyOrWhitespace( errors, "fecha", "fecha.required" );
		ValidationUtils.rejectIfEmpty( errors, "moneda", "moneda.required" );
		ValidationUtils.rejectIfEmptyOrWhitespace( errors, "monto", "monto.required" );
		ValidationUtils.rejectIfEmptyOrWhitespace( errors, "periodo", "periodo.required" );
		ValidationUtils.rejectIfEmptyOrWhitespace( errors, "tea", "tea.required" );

		Credito credito = (Credito) target;

		if ( lleno( credito.getDni() ) && !credito.getDni().matches( "\\d{8}" ) )
			errors.rejectValue( "dni", "dni.formato" );

		if ( lleno( credito.getCorreo() ) && credito.getCorreo().indexOf( '@' ) < 0 )
			errors.rejectValue( "correo", "correo.formato" );

		if ( lleno( credito.getFecha() ) && !credito.getFecha().matches( "\\d{4}-\\d{2}-\\d{2}" ) )
			errors.rejectValue( "fecha", "fecha.formato" );

		if ( lleno( credito.getMonto() ) && !errors.hasFieldErrors( "monto" ) ) {
			try {
				if ( Double.parseDouble( credito.getMonto() ) <= 0 )
					errors.rejectValue( "monto", "monto.invalido" );
			} catch ( NumberFormatException e ) {
				errors.rejectValue( "monto", "monto.invalido" );
			}
		}

		if ( lleno( credito.getPeriodo() ) && !errors.hasFieldErrors( "periodo" ) ) {
			try {
				if ( Integer.parseInt( credito.getPeriodo() ) < 1 )
					errors.rejectValue( "periodo", "periodo.invalido" );
			} catch ( NumberFormatException e ) {
				errors.rejectValue( "periodo", "periodo.invalido" );
			}
		}

		if ( lleno( credito.getTea() ) && !errors.hasFieldErrors( "tea" ) ) {
			try {
				if ( Double.parseDouble( credito.getTea() ) < 0 )
					errors.rejectValue( "tea", "tea.invalida" );
			} catch ( NumberFormatException e ) {
				errors.rejectValue( "tea", "tea.invalida" );
			}
		}
	}

	private boolean lleno( String valor ) {
		return valor != null && valor.trim().length() > 0;
	}
}
