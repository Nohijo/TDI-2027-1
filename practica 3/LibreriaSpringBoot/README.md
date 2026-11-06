# Practica 3 - Libreria en Linea (Spring Boot)

**Miguel Angel Marquez Cristoval**

Migracion de la Libreria de la Practica 2 (Servlets y JSP) a **Spring Boot**.
Permite **registrar**, **listar**, **buscar/filtrar/ordenar**, **editar** y
**eliminar** libros, con la interfaz en espanol e ingles.

## Versiones

- **Java:** 17 o mayor (probado con JDK 21)
- **Spring Boot:** 4.1.1 (Spring MVC, JdbcTemplate, Tomcat embebido)
- **Base de datos:** MySQL Server 8.0
- **Vistas:** JSP con JSTL y las etiquetas de Spring
- **Interfaz:** Bootstrap 5.0.2, jQuery 1.12.3 y jQuery Validation 1.15
  (van incluidos en `src/main/resources/static/vendor/`, no hace falta internet)

## Como correrlo

1. Encender MySQL y, solo la primera vez, crear la base de datos. El script crea
   `libreria_online`, la tabla `libros` con libros de ejemplo y el usuario de la
   aplicacion:

   ```bash
   sudo systemctl start mysql
   sudo mysql -u root < esquema.sql
   ```

2. Levantar el proyecto desde esta carpeta:

   ```bash
   ./mvnw spring-boot:run
   ```

3. Abrir <http://localhost:8081/>

### Con el war ya compilado

```bash
./mvnw package
java -jar target/libreria-spring-boot.war
```

> **Importante:** `java -jar` solo funciona si la ruta del `.war` no tiene
> espacios. Con espacios, Tomcat no puede leer las etiquetas de las JSP y la
> pagina responde con error 500. Si es el caso, copia el `.war` a otra carpeta o
> usa `./mvnw spring-boot:run`, que no tiene ese problema.

## Base de datos

Los parametros estan en `src/main/resources/application.properties`:

```properties
server.port=8081
spring.datasource.url=jdbc:mysql://${DB_HOST:localhost}:3306/libreria_online?useSSL=false&allowPublicKeyRetrieval=true&characterEncoding=UTF-8
spring.datasource.username=libreria_app
spring.datasource.password=libreria_pass
```

La tabla es la misma de la Practica 2 (`esquema.sql`).

## Estructura

| Carpeta | Contenido |
|---|---|
| `config/` | `MessageSource`, `LocaleResolver` y el cambio de idioma con `?lang=` |
| `controller/` | `BookController`: `@GetMapping`, `@PostMapping`, `@PutMapping`, `@DeleteMapping` |
| `service/` | `BookService` y su implementacion con `@Service` |
| `repository/` | `BookRepository` y su implementacion con `JdbcTemplate` |
| `model/` | `Book`, con las validaciones de los campos |
| `resources/static/` | Estilos propios, `js/libreria.js` y las librerias |
| `webapp/WEB-INF/jsp/` | `libros.jsp` y los fragmentos de cabecera y pie |

## Decisiones

- **`@Entity` con JdbcTemplate:** el enunciado pide las dos cosas. `Book` lleva
  las anotaciones de `jakarta.persistence` para documentar el mapeo, pero el
  proyecto no usa JPA: todo el acceso a datos pasa por `JdbcTemplate`.
- **Vistas en `src/main/webapp/WEB-INF/jsp`:** la estructura sugerida las pone
  dentro de `resources/`, pero Spring Boot las busca en `webapp`. Por lo mismo el
  proyecto se empaqueta como `war`: las JSP no funcionan en un `jar` ejecutable.
- **Editar y eliminar con `$.ajax`:** un formulario HTML solo envia GET y POST, y
  asi la tabla se actualiza sin recargar la pagina.

## Material del curso que se uso

- **Practica 2:** modelo, tabla y consultas de la libreria.
- **Repositorio del laboratorio (`spring_boot`):** `pom.xml` y version de Spring
  Boot, y el Maven Wrapper.
- **InternacionalSpring3MVC:** los beans de internacionalizacion, aqui en Java.
- **3er Entrega Ejemplos JQuery y Tarea 3:** jQuery y jQuery Validation.

---

*El contenido de este README se redactó con ayuda de Claude (Anthropic).*
