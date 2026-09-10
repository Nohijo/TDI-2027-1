# Tarea 1 - Servlet "Financiera Huanca"

Servlet que muestra un formulario de solicitud de crédito y, al enviarlo,
devuelve otra vista con los campos capturados y dos cálculos:
la **cuota mensual** (sistema francés) y la **fecha de vencimiento**.

Mismo patrón del ejemplo de clase *Forma_de_Compra*: un solo servlet con
`doGet` (muestra el formulario) y `doPost` (procesa y responde).

## Stack

- **Java 8**
- **Apache Tomcat 7** (Servlet 3.0, paquete `javax.servlet`)
- **NetBeans**
- Maven (para empaquetar el `.war`)

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
        └── WEB-INF/web.xml        registro del servlet (<servlet-mapping> /credito)
```

## Cómo abrir y ejecutar en NetBeans

### 1. Tener JDK 8 y registrarlo

Si NetBeans corre con un Java más nuevo, agrega el 8 igual:
`Tools ▸ Java Platforms ▸ Add Platform…` y elige la carpeta del JDK 8.
Luego en el proyecto: clic derecho ▸ `Properties ▸ Build ▸ Compile` (o
`Sources`) ▸ *Java Platform* / *Source-Binary Format* = **8**.

### 2. Registrar Tomcat 7 (una sola vez)

`Tools ▸ Servers ▸ Add Server… ▸ Apache Tomcat or TomEE ▸ Next`.
En *Server Location* pon la carpeta de Tomcat 7. Si no lo tienes:

```bash
cd ~ && curl -LO https://archive.apache.org/dist/tomcat/tomcat-7/v7.0.109/bin/apache-tomcat-7.0.109.tar.gz && tar xzf apache-tomcat-7.0.109.tar.gz
```

Deja usuario/contraseña en blanco.

### 3. Abrir el proyecto

`File ▸ Open Project…` y selecciona la carpeta `FinancieraHuanca`
(NetBeans la reconoce por el `pom.xml`).

### 4. Ejecutar

Clic derecho en el proyecto ▸ **Run**. Elige el Tomcat 7 cuando pregunte
(o en `Properties ▸ Run ▸ Server`).

### 5. Ver la aplicación

- Entrada:    `http://localhost:8080/FinancieraHuanca/`
- Formulario: `http://localhost:8080/FinancieraHuanca/credito`

## Cómo funciona

| Momento | Método | Qué hace |
|---|---|---|
| Se abre `/credito` | `doGet` | Escribe el HTML del formulario con `out.println(...)` (primera vista) |
| Se envía el formulario | `doPost` | Lee los parámetros con `request.getParameter(...)`, calcula cuota y vencimiento, y escribe el HTML de resultados (segunda vista) |

La cuota usa el sistema francés:
`cuota = M · i / (1 − (1 + i)^−n)`, con `i = (1 + TEA)^(1/12) − 1`.

## Generar el .war a mano (opcional)

```bash
mvn -f "tarea 1/FinancieraHuanca/pom.xml" clean package
# resultado: tarea 1/FinancieraHuanca/target/FinancieraHuanca.war
```

Probado en Apache Tomcat 7.0.109 con Temurin JDK 1.8.0_504: las dos vistas
funcionan, los acentos y la ñ se capturan bien.
