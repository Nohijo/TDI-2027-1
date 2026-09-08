# Tarea 1 - Servlet "Financiera Huanca"

Servlet que muestra un formulario de solicitud de crédito y, al enviarlo,
devuelve otra vista con los campos capturados y dos cálculos:
la **cuota mensual** (sistema francés) y la **fecha de vencimiento**.

Es el mismo patrón del ejemplo de clase *Forma_de_Compra*: un solo servlet
con `doGet` (muestra el formulario) y `doPost` (procesa y responde).

## Estructura

```
FinancieraHuanca/
├── pom.xml                         proyecto Maven (empaqueta un .war)
└── src/main/
    ├── java/mx/unam/ciencias/tdi/
    │   └── CreditoServlet.java     el servlet (doGet + doPost)
    └── webapp/
        ├── index.html             página de entrada, enlaza al servlet
        ├── css/estilo.css
        ├── js/financiera.js       pestañas y vista previa de los cálculos
        └── WEB-INF/web.xml        welcome-file (el servlet usa @WebServlet)
```

## Requisitos

- JDK 17 o superior
- Apache Tomcat 10.1.x (usa el paquete `jakarta.servlet`)
- NetBeans 17+ (probado con NetBeans 31)

## Cómo abrir y ejecutar en NetBeans

1. **Registrar Tomcat una sola vez**
   `Tools ▸ Servers ▸ Add Server… ▸ Apache Tomcat or TomEE`.
   Si no tienes Tomcat, en ese mismo diálogo usa **Download** o descárgalo de
   <https://tomcat.apache.org/download-10.cgi> (Core, zip) y descomprímelo.
   Escoge la carpeta y deja usuario/clave en blanco.

2. **Abrir el proyecto**
   `File ▸ Open Project…` y selecciona la carpeta `FinancieraHuanca`
   (NetBeans la reconoce por el `pom.xml`).

3. **Ejecutar**
   Clic derecho en el proyecto ▸ **Run**.
   La primera vez NetBeans pregunta el servidor: elige el Tomcat del paso 1.

4. **Ver la aplicación**
   - Entrada:   `http://localhost:8080/FinancieraHuanca/`
   - Formulario: `http://localhost:8080/FinancieraHuanca/credito`

## Cómo funciona

| Momento | Método | Qué hace |
|---|---|---|
| Se abre `/credito` | `doGet` | Escribe el HTML del formulario (primera vista) |
| Se envía el formulario | `doPost` | Lee los parámetros con `request.getParameter(...)`, calcula cuota y vencimiento, y escribe el HTML de resultados (segunda vista) |

La cuota usa el sistema francés:
`cuota = M · i / (1 − (1 + i)^−n)`, con `i = (1 + TEA)^(1/12) − 1`.

## Si tu Tomcat es la versión 9 (paquete `javax`)

1. En `pom.xml` cambia la dependencia a
   `javax.servlet:javax.servlet-api:4.0.1`.
2. En `CreditoServlet.java` reemplaza los cinco imports
   `jakarta.servlet...` por `javax.servlet...`.
3. En `web.xml` cambia el namespace a
   `http://xmlns.jcp.org/xml/ns/javaee` y `version="4.0"`.

## Generar el .war a mano (opcional)

```bash
mvn -f "tarea 1/FinancieraHuanca/pom.xml" clean package
# resultado: tarea 1/FinancieraHuanca/target/FinancieraHuanca.war
```
