package mx.unam.ciencias.tdi.libreria;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

/**
 * Practica 3 - Tecnologias para Desarrollos en Internet.
 *
 * Punto de entrada. Hereda de SpringBootServletInitializer para que el war
 * corra con "java -jar" y tambien desplegado en un Tomcat externo.
 *
 * @author Miguel Angel Marquez Cristoval
 */
@SpringBootApplication
public class LibreriaApplication extends SpringBootServletInitializer {

    public static void main(String[] args) {
        SpringApplication.run(LibreriaApplication.class, args);
    }

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder aplicacion) {
        return aplicacion.sources(LibreriaApplication.class);
    }
}
