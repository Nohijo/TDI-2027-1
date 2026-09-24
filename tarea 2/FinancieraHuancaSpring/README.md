# Tarea 2 - Formulario con Spring MVC y MySQL

**Miguel Ángel Márquez Cristóval**

Aplicación web que muestra el formulario de *Financiera Huanca* del enunciado
**mediante una vista** y, al enviarlo, **devuelve otra vista con los datos
capturados**, usando el framework **Spring** y guardando cada solicitud en
**MySQL**.

Es el mismo formulario de la Tarea 1, ahora con Spring MVC en lugar de un
servlet suelto, y con base de datos: el servidor calcula la cuota mensual y la
fecha de vencimiento, guarda la solicitud y la muestra junto con las últimas
que hay en la base.

## Versiones

- **Java:** JDK 21
- **Servidor:** Apache Tomcat 10.1
- **Framework:** Spring Framework 6.2.19 (`spring-webmvc` + `spring-jdbc`)
- **Base de datos:** MySQL Server 8.0
- **Conector JDBC:** `mysql-connector-j-8.4.0.jar`
- **Vistas:** JSP + JSTL 3.0
- **NetBeans** (proyecto *Web Application*, con Ant)

El ejemplo de clase es *FormularioDBSpring3MVC* (Spring 3, paquete
`javax.servlet`). Aquí se usa Spring 6 porque es la versión que corre sobre
Tomcat 10.1, que usa `jakarta.servlet` — el mismo servidor de la Práctica 2.
La estructura es la misma del ejemplo: `web.xml` registra el
`DispatcherServlet` y los beans se declaran en un XML aparte.

Todos los `.jar` están en `lib/`, así que el proyecto compila sin descargar
nada.

## Cómo correrlo

1. Encender MySQL:

   ```bash
   sudo systemctl start mysql
   ```

2. Abrir el proyecto en NetBeans ▸ clic derecho ▸ **Clean and Build**.
3. Clic derecho ▸ **Run**.
4. Abrir <http://localhost:8080/FinancieraHuancaSpring/>

### Solo la primera vez

Crear la base de datos (crea `financiera_huanca`, la tabla `creditos` y el
usuario que usa la aplicación):

```bash
sudo mysql -u root < esquema.sql
```

Si al dar *Run* Tomcat pide usuario y contraseña, es porque tu Tomcat no
tiene un usuario del *Manager*; se agrega en
`<carpeta-de-tomcat>/conf/tomcat-users.xml`:

```xml
<role rolename="manager-script"/>
<user username="admin" password="admin" roles="manager-script"/>
```

## Estructura

```
src/java/
├── db.properties                       datos de conexión a MySQL
├── messages.properties                 mensajes de error de conversión
└── mx/unam/ciencias/tdi/
    ├── model/Credito.java              Model: la solicitud (y command object de Spring)
    ├── service/CalculadoraCredito.java cuota mensual y fecha de vencimiento
    ├── dao/CreditoDAO.java             guarda y consulta con JdbcTemplate
    └── controller/CreditoController.java  @Controller: las dos vistas

web/
├── WEB-INF/
│   ├── web.xml                         registra el DispatcherServlet en "/"
│   ├── spring-servlet.xml              beans: scan, vistas, DataSource, JdbcTemplate
│   └── vistas/
│       ├── formulario.jsp              PRIMERA vista
│       ├── resultado.jsp               SEGUNDA vista
│       └── error.jsp                   aviso si MySQL no responde
└── css/estilo.css

esquema.sql                             crea la base, la tabla y el usuario
```

| URL | Método | Qué hace |
|---|---|---|
| `/` o `/credito` | GET | El Controller manda un `Credito` vacío a `formulario.jsp` |
| `/credito` | POST | Valida, calcula, guarda con el DAO y devuelve `resultado.jsp` |

## Cómo funciona

1. `web.xml` registra el **DispatcherServlet** de Spring en `/`: todas las
   peticiones pasan por él.
2. `spring-servlet.xml` declara los beans: el escaneo de `@Controller`,
   `@Service` y `@Repository`, el `InternalResourceViewResolver` (traduce
   `"formulario"` a `/WEB-INF/vistas/formulario.jsp`), el `DataSource` de
   MySQL y el `JdbcTemplate`.
3. El **Controller** no arma HTML: llena un objeto del Model y devuelve el
   nombre de la vista. Spring enlaza solo los campos del formulario con el
   objeto `Credito` (`@ModelAttribute`) y reporta en el `BindingResult` lo que
   no cuadra.
4. Las vistas son JSP con las etiquetas `<form:...>` de Spring, que pintan
   cada campo con el valor del objeto y los mensajes de error junto a él.
5. El **DAO** usa `JdbcTemplate`: Spring abre y cierra la conexión y traduce
   los errores de JDBC, así que el DAO solo tiene el SQL.

La cuota usa el sistema francés:
`cuota = M · i / (1 − (1 + i)^−n)`, con `i = (1 + TEA)^(1/12) − 1`.

Los datos de conexión están en `src/java/db.properties`.

---

*El contenido de este README se redactó con ayuda de Claude (Anthropic).*
