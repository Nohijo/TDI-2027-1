# Tecnologías para Desarrollos en Internet

**Miguel Ángel Márquez Cristóval**

Trabajos de la materia: prácticas y tareas.

## Contenido

- **[práctica 1/](practica%201/MiPagina)** — Página web de un bar (HTML, CSS y JavaScript).
- **[tarea 1/](tarea%201/FinancieraHuanca)** — Servlet "Financiera Huanca": formulario de solicitud de crédito.
- **[práctica 2/](practica%202/LibreriaOnline)** — Librería en línea con arquitectura MVC (Servlets y JSP).

## Tecnologías

- HTML, CSS, JavaScript
- Java 8, Servlets, JSP
- Apache Tomcat 7
- Maven
- NetBeans

Cada proyecto tiene su propio README con instrucciones para abrirlo y ejecutarlo.

## Como correr la Práctica 2 (librería en línea)

Necesita **JDK 21**, **Apache Tomcat 10.1**, **MySQL 8.0** y **NetBeans**.

1. Encender MySQL y crear la base (solo la primera vez):

   ```bash
   sudo systemctl start mysql
   sudo mysql -u root < "practica 2/LibreriaOnline/esquema.sql"
   ```

2. Si tu Tomcat aún no tiene un usuario del *Manager* (NetBeans lo
   necesita para desplegar), agregarlo en
   `<carpeta-de-tomcat>/conf/tomcat-users.xml`, antes de
   `</tomcat-users>` — el usuario y la contraseña los eliges tú:

   ```xml
   <role rolename="manager-script"/>
   <user username="admin" password="admin" roles="manager-script"/>
   ```

3. Abrir `practica 2/LibreriaOnline` en NetBeans ▸ clic derecho ▸
   **Clean and Build** ▸ clic derecho ▸ **Run**.

4. Abrir <http://localhost:8080/LibreriaOnline/>

**Sin NetBeans** (no necesita ningún usuario): generar el `.war` con
`ant -f build.xml clean dist`, copiarlo a `<tomcat>/webapps/` y encender
Tomcat. Ver el README de la práctica para el detalle.

Los pasos completos, y que hacer si algo falla, están en
[practica 2/LibreriaOnline/README.md](practica%202/LibreriaOnline/README.md).

---

*El contenido de este README se redactó con ayuda de Claude (Anthropic).*
