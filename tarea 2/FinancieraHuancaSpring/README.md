# Tarea 2 - Formulario con Spring MVC y MySQL

**Miguel Ángel Márquez Cristóval**

Aplicación web que muestra el formulario de *Financiera Huanca* del enunciado
**mediante una vista** y, al enviarlo, **devuelve otra vista con los datos
capturados**, usando el framework **Spring** y guardando cada solicitud en
**MySQL**.

Está hecha sobre el ejemplo de clase **FormularioDBSpring3MVC**: mismo tipo de
proyecto, mismas librerías y la misma organización en paquetes
(`dominio`, `servicio`, `baseDeDatos`, `control`, `validator`).

## Versiones

Las mismas del ejemplo de clase:

- **Java:** 1.8
- **Servidor:** Apache Tomcat 9.0.100 (Servlet 2.5, paquete `javax.servlet`)
- **Framework:** Spring 3.0.0.M3 + `spring-jdbc` y `spring-tx` 3.0.7.RELEASE
- **Base de datos:** MySQL Server 8.0
- **Conector JDBC:** `mysql-connector-java-8.0.18.jar`
- **Vistas:** JSP con las etiquetas `<form:...>` de Spring y JSTL 1.2
- **Eclipse IDE for Enterprise Java and Web Developers 2025-06** (*Dynamic Web Project*)

Los `.jar` van en `WebContent/WEB-INF/lib/`, así que el proyecto compila sin
descargar nada.

## Cómo correrlo

### Solo la primera vez: crear la base de datos

```bash
mysql -u root -p < "BD Financiera.sql"
```

En Linux, con una instalación nueva de MySQL 8, `root` entra por *auth_socket*:

```bash
sudo mysql -u root < "BD Financiera.sql"
```

El script crea la base `financiera_huanca`, la tabla `creditos` y el usuario
`huanca_app` que usa la aplicación. Si tu MySQL usa otro usuario o contraseña,
se ajusta en `src/servicio.properties`.

### En Eclipse

1. Registrar el servidor una sola vez: *Window ▸ Preferences ▸ Server ▸
   Runtime Environments ▸ Add ▸ Apache Tomcat v9.0*, apuntando a la carpeta de
   Tomcat 9. Conviene dejarle de nombre **`apache-tomcat-9.0.100`**: es el
   nombre que trae el `.classpath`, y así el proyecto resuelve las librerías
   del servidor sin tocar nada.
2. *File ▸ Import ▸ General ▸ Existing Projects into Workspace* y elegir la
   carpeta `FinancieraHuancaSpring`.
3. Si el servidor quedó con otro nombre: clic derecho en el proyecto ▸
   *Properties ▸ Targeted Runtimes* y marcar el Tomcat instalado.
4. Clic derecho ▸ *Run As ▸ Run on Server*.
5. Abrir <http://localhost:8080/FinancieraHuancaSpring/>

## Estructura

```
src/
├── Spring-Datasource.xml           de dónde saca Spring los datos de conexión
├── servicio.properties             url, usuario y contraseña de MySQL
├── messages.properties             mensajes de validación
├── dominio/
│   ├── Credito.java                la solicitud (y command object de Spring)
│   └── BDMySql.java                datos de conexión, como en el ejemplo
├── validator/CreditoValidator.java revisa lo capturado
├── servicio/
│   ├── CreditoService.java
│   └── CreditoServiceImpl.java     cuota y fecha de vencimiento
├── baseDeDatos/
│   └── CreditosJDBCTemplate.java   guarda y consulta en MySQL
└── control/
    ├── CreditoController.java      las dos vistas
    └── CreditoExitoController.java

WebContent/
├── redirect.jsp                    entrada: manda a creditoRegistro.htm
├── css/estilo.css
└── WEB-INF/
    ├── web.xml                     registra el DispatcherServlet en *.htm
    ├── dispatcher-servlet.xml      beans: vistas, mensajes, servicio, validador
    ├── lib/                        Spring 3, conector MySQL, JSTL
    └── jsp/
        ├── creditoForm.jsp         PRIMERA vista
        └── creditoExito.jsp        SEGUNDA vista

BD Financiera.sql                   crea la base, la tabla y el usuario
```

| URL | Método | Qué hace |
|---|---|---|
| `/creditoRegistro.htm` | GET | El Controller manda un `Credito` vacío a `creditoForm.jsp` |
| `/creditoRegistro.htm` | POST | Valida; si está bien calcula, guarda y redirige |
| `/creditoExito.htm` | GET | Muestra lo capturado y las últimas solicitudes guardadas |

## Cómo funciona

1. `web.xml` registra el **DispatcherServlet** de Spring para las URL `*.htm`.
2. `dispatcher-servlet.xml` declara los beans: el escaneo de `@Controller`, el
   `InternalResourceViewResolver` (traduce `"creditoForm"` a
   `/WEB-INF/jsp/creditoForm.jsp`), el `ResourceBundleMessageSource` de los
   mensajes, el servicio y el validador.
3. El **Controller** no arma HTML: llena el objeto `Credito` con lo que mandó
   el formulario (`@ModelAttribute`) y devuelve el nombre de una vista.
   `@SessionAttributes` deja el objeto en la sesión para que la segunda vista
   lo pueda mostrar después del *redirect*.
4. El **validador** (`CreditoValidator`, un `Validator` de Spring) marca los
   campos con problemas y el JSP los imprime con `<form:errors>`.
5. El **servicio** calcula y el **DAO** (`CreditosJDBCTemplate`) guarda, con la
   conexión que sale de `Spring-Datasource.xml` + `servicio.properties`.

La cuota usa el sistema francés:
`cuota = M · i / (1 − (1 + i)^−n)`, con `i = (1 + TEA)^(1/12) − 1`.

---

*El contenido de este README se redactó con ayuda de Claude (Anthropic).*
