# Practica 2 - Libreria en Linea (MVC con Servlets y JSP)

Aplicacion web que simula una libreria en linea: permite **agregar**,
**listar** y **buscar/filtrar/ordenar** libros. Implementada con la
arquitectura **MVC** usando Servlets y JSP, siguiendo el mismo patron de
capas Model / DAO / Controller / View que se vio en las notas de
laboratorio.

## Versiones

Para que el proyecto corra sin problemas de compatibilidad, se utilizaron
estas versiones:

- **Java:** JDK 21
- **Servidor:** Apache Tomcat 10.1
- **Base de datos:** MySQL Server 8.0
- **Conector JDBC:** `mysql-connector-j-8.4.0.jar`
- **NetBeans**
- Maven (para empaquetar el `.war`; el conector JDBC y JSTL se agregan
  solos al compilar, via `pom.xml` — no hay que copiar ningun `.jar` a mano)

## Estructura (MVC)

```
LibreriaOnline/
├── pom.xml
├── esquema.sql                       crea la BD y la tabla "libros" (con datos de ejemplo)
└── src/main/
    ├── java/mx/unam/ciencias/tdi/
    │   ├── model/Book.java              Model: entidad Libro (nombre, autor, precio)
    │   ├── dao/BookDAO.java             DAO: alta, listado, busqueda/orden (JDBC + MySQL)
    │   ├── controller/BookServlet.java  Controller: unico servlet (/libreria)
    │   └── util/ConexionBD.java         abre conexiones a MySQL usando db.properties
    ├── resources/db.properties          datos de conexion a MySQL (editar antes de correr)
    └── webapp/
        ├── index.jsp                    redirige al controller
        ├── libreria.jsp                 View: unica pagina (agregar + tabla + buscar)
        ├── css/libreria.css
        └── WEB-INF/web.xml
```

> La consigna pide la ruta `web/libreria.jsp`; en un proyecto Maven esa
> carpeta se llama `webapp`, que es la convencion estandar que reconoce
> NetBeans/Tomcat. El archivo vive en `src/main/webapp/libreria.jsp`.

## Como funciona el flujo MVC

| Accion (`?accion=`) | Metodo | Que hace |
|---|---|---|
| `listar` (por omision) | GET | El Controller pide al DAO todos los libros y hace **forward** a `libreria.jsp` |
| `buscar` | GET | El Controller lee `q` (texto), `campo` (nombre/autor/precio) y `orden` (asc/desc), se los pasa al DAO y hace forward a la vista con el resultado |
| `agregar` | POST | El Controller valida los datos del formulario, crea un `Book`, lo guarda con el DAO y hace **redirect** a `listar` (patron Post/Redirect/Get, para no duplicar el libro si se recarga la pagina) |

El `BookDAO` habla con MySQL por JDBC (tabla `libros`, ver `esquema.sql`).
Si MySQL no esta corriendo o `db.properties` tiene datos incorrectos, el
Controller lo atrapa y muestra un aviso en la vista en vez de una pagina
de error del servidor.

## Antes de correrlo: preparar MySQL

1. Tener **MySQL Server 8.0** corriendo localmente.
2. Ejecutar `esquema.sql` una sola vez como root del sistema (crea la base
   `libreria_online`, la tabla `libros`, unos libros de ejemplo, y el
   usuario `libreria_app` que usa la aplicacion):

   ```bash
   sudo mysql -u root < esquema.sql
   ```

   > En una instalacion nueva de MySQL 8.0 en Linux, `root`@`localhost`
   > suele usar el plugin `auth_socket` (solo entra el usuario del sistema
   > operativo "root", por eso el `sudo`). Por esto mismo la aplicacion
   > **no** se conecta como `root`: `esquema.sql` crea un usuario aparte,
   > `libreria_app`, con contraseña, que sí sirve para conectarse por TCP
   > (JDBC) — ya viene configurado en `db.properties`.

3. `src/main/resources/db.properties` ya trae los datos que crea
   `esquema.sql`; solo edítalo si usas otro usuario/base/contraseña:

   ```properties
   db.url=jdbc:mysql://localhost:3306/libreria_online?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true
   db.usuario=libreria_app
   db.password=libreria_pass
   ```

## Como levantarlo en NetBeans

1. Tener **JDK 21** registrado en NetBeans (`Tools ▸ Java Platforms`).
2. Registrar **Apache Tomcat 10.1** (`Tools ▸ Servers ▸ Add Server… ▸
   Apache Tomcat or TomEE`), apuntando a la carpeta donde lo descargaste.
   Si no lo tienes:

   ```bash
   cd ~ && curl -LO https://archive.apache.org/dist/tomcat/tomcat-10/v10.1.31/bin/apache-tomcat-10.1.31.tar.gz && tar xzf apache-tomcat-10.1.31.tar.gz
   ```

3. Abrir el proyecto: `File ▸ Open Project…` y seleccionar la carpeta
   `LibreriaOnline` (NetBeans la reconoce por el `pom.xml`).
4. Confirmar que `mysql-connector-j-8.4.0.jar` aparece dentro de
   **Dependencies/Libraries** del proyecto (Maven lo descarga solo la
   primera vez que compilas, gracias al `pom.xml`; no hace falta copiarlo
   a mano a `WEB-INF/lib`, eso Maven lo empaqueta automaticamente).
5. Clic derecho en el proyecto ▸ **Clean and Build**.
6. Clic derecho en el proyecto ▸ **Run**. Elige Tomcat 10.1 cuando
   pregunte (o en `Properties ▸ Run ▸ Server`).
7. Ver la aplicacion: `http://localhost:8080/LibreriaOnline/`

## Generar el .war a mano (opcional)

```bash
mvn -f "practica 2/LibreriaOnline/pom.xml" clean package
# resultado: practica 2/LibreriaOnline/target/LibreriaOnline.war
```

`mvn clean package` compila sin errores; el `.war` incluye JSTL y el
conector de MySQL empaquetados en `WEB-INF/lib`.
