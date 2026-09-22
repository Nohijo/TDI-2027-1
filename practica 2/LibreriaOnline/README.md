# Practica 2 - Libreria en Linea (MVC con Servlets y JSP)

Aplicacion web que simula una libreria en linea: permite **agregar**,
**listar** y **buscar/filtrar/ordenar** libros. Implementada con la
arquitectura **MVC** usando Servlets y JSP, siguiendo el mismo patron de
capas Model / DAO / Controller / View que se vio en las notas de
laboratorio.

## Resumen rapido (si ya esta todo instalado)

```bash
sudo systemctl start mysql          # 1. la base de datos debe estar encendida
```

2. Abrir el proyecto en NetBeans ▸ clic derecho ▸ **Clean and Build**.
3. Clic derecho ▸ **Run**. Si pide usuario del Manager de Tomcat:
   **admin / admin**.
4. Abrir `http://localhost:8080/LibreriaOnline/`

Si es la primera vez en esta computadora, seguir las secciones completas
de abajo (preparar MySQL y preparar NetBeans/Tomcat).

## Versiones

Para que el proyecto corra sin problemas de compatibilidad, se utilizaron
estas versiones:

- **Java:** JDK 21
- **Servidor:** Apache Tomcat 10.1
- **Base de datos:** MySQL Server 8.0
- **Conector JDBC:** `mysql-connector-j-8.4.0.jar`
- **NetBeans** (proyecto *Web Application*, con Ant)

## Estructura (MVC)

Proyecto **Web Application de NetBeans (Ant)**, que es el tipo de proyecto
que el IDE despliega directamente en Tomcat con el boton *Run*.

```
LibreriaOnline/
├── build.xml                         script de Ant (lo usa NetBeans)
├── nbproject/                        configuracion del proyecto de NetBeans
├── esquema.sql                       crea la BD y la tabla "libros" (con datos de ejemplo)
├── lib/                              .jar de los que depende el proyecto
│   ├── mysql-connector-j-8.4.0.jar   conector JDBC de MySQL
│   ├── protobuf-java-3.25.1.jar      (lo necesita el conector)
│   └── jakarta.servlet.jsp.jstl*.jar JSTL para las etiquetas <c:...> del JSP
├── src/
│   ├── conf/MANIFEST.MF
│   └── java/
│       ├── db.properties               datos de conexion a MySQL
│       └── mx/unam/ciencias/tdi/
│           ├── model/Book.java              Model: entidad Libro (nombre, autor, precio)
│           ├── dao/BookDAO.java             DAO: alta, listado, busqueda/orden (JDBC + MySQL)
│           ├── controller/BookServlet.java  Controller: unico servlet (/libreria)
│           └── util/ConexionBD.java         abre conexiones a MySQL usando db.properties
└── web/
    ├── index.jsp                     redirige al controller
    ├── libreria.jsp                  View: unica pagina (agregar + tabla + buscar)
    ├── css/libreria.css
    ├── META-INF/context.xml          fija el contexto /LibreriaOnline
    └── WEB-INF/web.xml               registro del servlet
```

Los `.jar` viven dentro del proyecto (`lib/`) y se referencian con rutas
relativas, asi que el proyecto compila igual en cualquier maquina; Ant los
copia solo a `web/WEB-INF/lib` dentro del `.war` al construir.

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

3. `src/java/db.properties` ya trae los datos que crea `esquema.sql`;
   solo edítalo si usas otro usuario/base/contraseña:

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

3. **Crear un usuario del Manager de Tomcat.** NetBeans despliega a traves
   de la aplicacion *Manager* de Tomcat, y un Tomcat recien descargado no
   trae ningun usuario dado de alta (vienen todos comentados). Sin esto,
   al dar *Run* aparece una y otra vez la ventana **"Authentication
   Required - Tomcat Manager Application"**.

   Editar `<carpeta-de-tomcat>/conf/tomcat-users.xml` y, antes de
   `</tomcat-users>`, agregar:

   ```xml
   <role rolename="manager-script"/>
   <role rolename="manager-gui"/>
   <user username="admin" password="admin" roles="manager-script,manager-gui"/>
   ```

   Para comprobar que quedo bien (con Tomcat encendido):

   ```bash
   curl -u admin:admin http://localhost:8080/manager/text/list
   ```

   Debe responder `OK - Listed applications for virtual host [localhost]`.

4. Abrir el proyecto: `File ▸ Open Project…` y seleccionar la carpeta
   `LibreriaOnline` (NetBeans la reconoce por `nbproject/`).
5. Confirmar que `mysql-connector-j-8.4.0.jar` aparece en el nodo
   **Libraries** del proyecto (ya viene referenciado desde `lib/`;
   Ant lo copia a `web/WEB-INF/lib` dentro del `.war` al construir).
6. Clic derecho en el proyecto ▸ **Clean and Build**.
7. Clic derecho en el proyecto ▸ **Run**. La primera vez pide el usuario
   del Manager: **admin / admin** (el del paso 3).
8. Ver la aplicacion: `http://localhost:8080/LibreriaOnline/`

> Si el servidor no aparece seleccionado: clic derecho en el proyecto ▸
> `Properties ▸ Run ▸ Server`. La instancia concreta se guarda en
> `nbproject/private/private.properties` (`j2ee.server.instance`), que es
> propio de cada maquina.

## Generar el .war a mano (opcional)

```bash
ant -f "practica 2/LibreriaOnline/build.xml" clean dist
# resultado: practica 2/LibreriaOnline/dist/LibreriaOnline.war
```

Probado con Apache Tomcat 10.1.31, JDK 21 y MySQL 8.0: el `.war` incluye
JSTL y el conector de MySQL en `WEB-INF/lib`, y desde el navegador
funcionan agregar, listar, buscar y ordenar.

## Problemas comunes

| Que se ve | Causa y solucion |
|---|---|
| Ventana **"Authentication Required - Tomcat Manager Application"** al dar Run | Falta el usuario del Manager en `conf/tomcat-users.xml` (paso 3). Usuario/contraseña: `admin` / `admin`. |
| Aviso **"No se pudo conectar con la base de datos"** en la pagina | MySQL no esta encendido (`sudo systemctl start mysql`) o los datos de `src/java/db.properties` no coinciden con los que creo `esquema.sql`. |
| La tabla sale vacia sin ningun aviso | La base existe pero la tabla `libros` esta vacia: volver a correr `sudo mysql -u root < esquema.sql`. |
| **"No suitable Deployment Server is defined"** | Le falta el servidor al proyecto: clic derecho ▸ `Properties ▸ Run ▸ Server` y elegir Tomcat. (Esto le pasa a los proyectos **Maven**; por eso este es un proyecto *Web Application* con Ant.) |
| El puerto 8080 esta ocupado | Ya hay otro Tomcat corriendo: `pkill -f catalina` y volver a dar Run. |
