# Practica 2 - Libreria en Linea (MVC con Servlets y JSP)

Aplicacion web que simula una libreria en linea: permite **agregar**,
**listar** y **buscar/filtrar/ordenar** libros. Implementada con la
arquitectura **MVC** usando Servlets y JSP, siguiendo el mismo patron de
capas Model / DAO / Controller / View que se vio en las notas de
laboratorio.

## Stack

- **Java 8**
- **Apache Tomcat 7** (Servlet 3.0, paquete `javax.servlet`)
- **NetBeans**
- Maven (para empaquetar el `.war`)
- **JSTL 1.2** para las vistas JSP (`<c:...>`, `<fmt:...>`)

## Estructura (MVC)

```
LibreriaOnline/
├── pom.xml
└── src/main/
    ├── java/mx/unam/ciencias/tdi/
    │   ├── model/Book.java              Model: entidad Libro (nombre, autor, precio)
    │   ├── dao/BookDAO.java             DAO: alta, listado, busqueda/orden (en memoria)
    │   └── controller/BookServlet.java  Controller: unico servlet (/libreria)
    └── webapp/
        ├── index.jsp                    redirige al controller
        ├── libreria.jsp                 View: unica pagina (agregar + tabla + buscar)
        ├── css/libreria.css
        └── WEB-INF/web.xml
```

> La consigna pide la ruta `web/libreria.jsp`; en un proyecto Maven (el
> mismo esquema que en la Tarea 1) esa carpeta se llama `webapp`, que es
> la convencion estandar que reconoce NetBeans/Tomcat. El archivo vive en
> `src/main/webapp/libreria.jsp`.

## Como funciona el flujo MVC

| Accion (`?accion=`) | Metodo | Que hace |
|---|---|---|
| `listar` (por omision) | GET | El Controller pide al DAO todos los libros y hace **forward** a `libreria.jsp` |
| `buscar` | GET | El Controller lee `q` (texto), `campo` (nombre/autor/precio) y `orden` (asc/desc), se los pasa al DAO y hace forward a la vista con el resultado |
| `agregar` | POST | El Controller valida los datos del formulario, crea un `Book`, lo guarda con el DAO y hace **redirect** a `listar` (patron Post/Redirect/Get, para no duplicar el libro si se recarga la pagina) |

El `BookDAO` guarda los libros en memoria (una lista sincronizada) para
que el proyecto corra sin configurar un motor de base de datos aparte;
si se conecta una BD real (por ejemplo con JDBC), solo cambia la
implementacion interna de esa clase — el Model, el Controller y la vista
no se tocan.

## Como abrir y ejecutar en NetBeans

### 1. Tener JDK 8 y registrarlo

Si NetBeans corre con un Java mas nuevo, agrega el 8 igual:
`Tools ▸ Java Platforms ▸ Add Platform…` y elige la carpeta del JDK 8.
Luego en el proyecto: clic derecho ▸ `Properties ▸ Build ▸ Compile` ▸
*Java Platform* = **8**.

### 2. Registrar Tomcat 7 (una sola vez)

`Tools ▸ Servers ▸ Add Server… ▸ Apache Tomcat or TomEE ▸ Next`.
En *Server Location* pon la carpeta de Tomcat 7. Si no lo tienes:

```bash
cd ~ && curl -LO https://archive.apache.org/dist/tomcat/tomcat-7/v7.0.109/bin/apache-tomcat-7.0.109.tar.gz && tar xzf apache-tomcat-7.0.109.tar.gz
```

Deja usuario/contraseña en blanco.

### 3. Abrir el proyecto

`File ▸ Open Project…` y selecciona la carpeta `LibreriaOnline`
(NetBeans la reconoce por el `pom.xml`).

### 4. Ejecutar

Clic derecho en el proyecto ▸ **Run**. Elige Tomcat 7 cuando pregunte
(o en `Properties ▸ Run ▸ Server`).

### 5. Ver la aplicacion

- `http://localhost:8080/LibreriaOnline/`

## Generar el .war a mano (opcional)

```bash
mvn -f "practica 2/LibreriaOnline/pom.xml" clean package
# resultado: practica 2/LibreriaOnline/target/LibreriaOnline.war
```

Probado con `mvn clean package` (compila sin errores) y con Apache Tomcat
7.0.109: alta, listado, busqueda y orden funcionan; el catalogo trae
libros de ejemplo precargados para que la tabla no arranque vacia.
