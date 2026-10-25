# Tarea 3 - Tabla de pagos con tarjeta de crédito (Spring MVC y MySQL)

**Miguel Ángel Márquez Cristóval**

Continuación de la Tarea 2. Debajo del formulario de *Financiera Huanca* hay
ahora una **tabla de pagos con tarjeta de crédito**:

- Las columnas **Fecha, Mes, Tarjeta, Transacción y Gastos** vienen de MySQL y
  no se pueden editar.
- La columna **Pagos** la captura el usuario y se guarda en MySQL al oprimir
  **ACEPTAR**, junto con la solicitud de crédito.
- La segunda vista muestra los datos capturados, la tabla de pagos tal como
  quedó en la base, lo pagado en cada tarjeta y una gráfica de gastos contra
  pagos.

La tabla de pagos (`pagos_tarjeta`) **no está relacionada** con la de
solicitudes (`creditos`) por llave foránea, como aclaró el profesor.

Está hecha sobre los ejemplos de clase:

- **FormularioDBSpring3MVC** (base de la Tarea 2): tipo de proyecto, librerías
  y paquetes `dominio`, `servicio`, `baseDeDatos`, `control`, `validator`.
- **MultiRowDBSpring3MVC**: la tabla de varios renglones. `Pago` hace el papel
  de `Proyecto`, `PagoDAO` el de `ProyectoDAO` (extiende `SimpleJdbcDaoSupport`)
  y el DataSource se declara en `Spring-Datasource.xml`, como en el ejemplo.
- **3er Entrega Ejemplos JQuery**: jQuery 1.12.3, *jQuery Validation* 1.15
  (ejemplo *Validacion Avanzada*) con los mensajes en español de Perú, y el
  plugin *visualize* del ejemplo *grafico* para la gráfica.

## Versiones

Las mismas de la Tarea 2:

- **Java:** 1.8
- **Servidor:** Apache Tomcat 9.0.100 (Servlet 2.5, paquete `javax.servlet`)
- **Framework:** Spring 3.0.0.M3 + `spring-jdbc` y `spring-tx` 3.0.7.RELEASE
- **Base de datos:** MySQL Server 8.0
- **Conector JDBC:** `mysql-connector-java-8.0.18.jar`
- **Vistas:** JSP con las etiquetas `<form:...>` de Spring y JSTL 1.2
- **Navegador:** jQuery 1.12.3, jquery.validate 1.15.0, visualize.jQuery
- **Eclipse IDE for Enterprise Java and Web Developers 2025-06** (*Dynamic Web Project*)

Los `.jar` van en `WebContent/WEB-INF/lib/` y los `.js` en `WebContent/js/`,
así que el proyecto corre sin descargar nada, ni siquiera desde un CDN.

## Cómo correrlo

### Primero: crear la base de datos

```bash
mysql -u root -p < "BD Financiera.sql"
```

En Linux, con una instalación nueva de MySQL 8, `root` entra por *auth_socket*:

```bash
sudo mysql -u root < "BD Financiera.sql"
```

El script crea la base `financiera_huanca`, la tabla `creditos` (si no existe;
las solicitudes de la Tarea 2 se conservan), la tabla `pagos_tarjeta` con los
movimientos del enunciado y el usuario `huanca_app` que usa la aplicación.
Volver a ejecutarlo deja la tabla de pagos como al principio. Si tu MySQL usa
otro usuario o contraseña, se ajusta en `src/servicio.properties`.

### En Eclipse

1. Registrar el servidor una sola vez: *Window ▸ Preferences ▸ Server ▸
   Runtime Environments ▸ Add ▸ Apache Tomcat v9.0*, apuntando a la carpeta de
   Tomcat 9. Conviene dejarle de nombre **`apache-tomcat-9.0.100`**: es el
   nombre que trae el `.classpath`.
2. *File ▸ Import ▸ General ▸ Existing Projects into Workspace* y elegir la
   carpeta `FinancieraHuancaPagos`. Tiene otro nombre que el proyecto de la
   Tarea 2, así que los dos caben en el mismo workspace.
3. Si el servidor quedó con otro nombre: clic derecho en el proyecto ▸
   *Properties ▸ Targeted Runtimes* y marcar el Tomcat instalado.
4. Clic derecho ▸ *Run As ▸ Run on Server*.
5. Abrir <http://localhost:8080/FinancieraHuancaPagos/>

## Estructura

```
src/
├── Spring-Datasource.xml           conexión: BDMySql y el DataSource del PagoDAO
├── servicio.properties             driver, url, usuario y contraseña de MySQL
├── messages.properties             mensajes de validación
├── dominio/
│   ├── Credito.java                la solicitud (command object); trae la lista de pagos
│   ├── Pago.java                   un renglón de la tabla de pagos       (Tarea 3)
│   ├── ResumenTarjeta.java         gastos, pagos y saldo de una tarjeta  (Tarea 3)
│   └── BDMySql.java
├── validator/CreditoValidator.java revisa el formulario y la columna Pagos
├── servicio/
│   ├── CreditoService.java / CreditoServiceImpl.java   cuota y vencimiento
│   └── PagoService.java / PagoServiceImpl.java         tabla de pagos (Tarea 3)
├── baseDeDatos/
│   ├── CreditosJDBCTemplate.java   solicitudes en MySQL
│   └── PagoDAO.java                pagos en MySQL (SimpleJdbcDaoSupport)   (Tarea 3)
└── control/
    ├── CreditoController.java      primera vista y envío
    └── CreditoExitoController.java segunda vista

WebContent/
├── redirect.jsp                    entrada: manda a creditoRegistro.htm
├── css/estilo.css, css/visualize.css
├── js/                             jquery, jquery.validate, mensajes es_PE, visualize
├── images/chartbg-vanilla.png      fondo de la gráfica
└── WEB-INF/
    ├── web.xml                     registra el DispatcherServlet en *.htm
    ├── dispatcher-servlet.xml      beans: vistas, mensajes, servicios, validador
    ├── lib/                        Spring 3, conector MySQL, JSTL
    └── jsp/
        ├── creditoForm.jsp         PRIMERA vista: formulario + tabla de pagos
        └── creditoExito.jsp        SEGUNDA vista: lo capturado, pagos y gráfica

BD Financiera.sql                   crea la base, las dos tablas y el usuario
```

| URL | Método | Qué hace |
|---|---|---|
| `/creditoRegistro.htm` | GET | Manda a `creditoForm.jsp` un `Credito` vacío con la tabla de pagos leída de MySQL |
| `/creditoRegistro.htm` | POST | Valida; si está bien calcula, guarda la solicitud y la columna Pagos, y redirige |
| `/creditoExito.htm` | GET | Muestra lo capturado, los pagos guardados, los totales por tarjeta y la gráfica |

## Cómo funciona la tabla de pagos

1. En el GET, `CreditoController` le pide a `PagoService` los renglones de
   `pagos_tarjeta` y los pone en `credito.listaPagos`. El JSP los recorre con
   `<c:forEach>`, como `add_contacto.jsp` del ejemplo MultiRowDBSpring3MVC.
2. Fecha, Mes, Tarjeta, Transacción y Gastos se imprimen como texto. Solo
   Pagos es un `<input>`, con nombre `listaPagos[0].pagos`,
   `listaPagos[1].pagos`, etc. Con esos índices Spring sabe a qué renglón
   pertenece cada valor.
3. `@SessionAttributes("credito")` deja el objeto en la sesión. Al enviar, Spring
   escribe los pagos sobre ese mismo objeto, así que los demás datos de cada
   renglón siguen siendo los que salieron de la base.
4. `CreditoValidator` revisa que cada pago esté vacío o sea un número mayor o
   igual que cero con dos decimales como máximo. Si algo está mal, el aviso
   aparece en la misma celda.
5. `PagoDAO.modificaPago` hace `UPDATE pagos_tarjeta SET pagos = ? WHERE id = ?`
   por cada renglón (vacío se guarda como `NULL`).
6. La segunda vista vuelve a leer la tabla de MySQL, así que muestra lo que de
   verdad quedó guardado.

## jQuery (3er entrega de ejemplos)

- **Validación en el navegador** con `jquery.validate`, como en *Validacion
  Avanzada*: campos obligatorios, DNI de 8 dígitos, correo, números y, en la
  tabla, que cada pago sea un importe válido. Revisa también la pestaña que no
  está a la vista y la abre si ahí hay avisos. La validación del servidor sigue
  activa por si el navegador no tiene JavaScript.
- **Totales por tarjeta** debajo de la tabla, que se recalculan mientras se
  escriben los pagos.
- **Gráfica de barras** de gastos y pagos por tarjeta en la segunda vista, con el
  plugin *visualize* del ejemplo *grafico*.

La cuota usa el sistema francés:
`cuota = M · i / (1 − (1 + i)^−n)`, con `i = (1 + TEA)^(1/12) − 1`.

---

*El contenido de este README se redactó con ayuda de Claude (Anthropic).*
