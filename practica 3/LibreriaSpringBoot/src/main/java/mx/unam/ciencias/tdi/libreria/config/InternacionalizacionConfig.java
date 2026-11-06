package mx.unam.ciencias.tdi.libreria.config;

import java.time.Duration;
import java.util.Locale;

import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.i18n.CookieLocaleResolver;
import org.springframework.web.servlet.i18n.LocaleChangeInterceptor;

/**
 * Practica 3 - Tecnologias para Desarrollos en Internet.
 *
 * Internacionalizacion: los mismos tres beans del ejemplo
 * InternacionalSpring3MVC (messageSource, localeResolver y
 * localeChangeInterceptor), declarados en Java en vez de XML. El idioma se
 * cambia con el parametro ?lang= y se recuerda en una cookie.
 *
 * @author Miguel Angel Marquez Cristoval
 */
@Configuration
public class InternacionalizacionConfig implements WebMvcConfigurer {

    @Bean
    public MessageSource messageSource() {
        ReloadableResourceBundleMessageSource mensajes = new ReloadableResourceBundleMessageSource();
        mensajes.setBasename("classpath:messages");
        mensajes.setDefaultEncoding("UTF-8");
        // Sin esto, en un sistema en ingles el espanol tomaria messages_en.
        mensajes.setFallbackToSystemLocale(false);
        return mensajes;
    }

    /** Se usa es-MX (no solo es) para que los precios salgan como 1,234.50. */
    @Bean
    public LocaleResolver localeResolver() {
        CookieLocaleResolver resolutor = new CookieLocaleResolver("libreria-idioma");
        resolutor.setDefaultLocale(Locale.forLanguageTag("es-MX"));
        resolutor.setCookieMaxAge(Duration.ofDays(30));
        return resolutor;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registro) {
        LocaleChangeInterceptor cambioDeIdioma = new LocaleChangeInterceptor();
        cambioDeIdioma.setParamName("lang");
        cambioDeIdioma.setIgnoreInvalidLocale(true);
        registro.addInterceptor(cambioDeIdioma);
    }

    @Override
    public void addViewControllers(ViewControllerRegistry registro) {
        registro.addRedirectViewController("/", "/libros");
    }
}
