<%--
    Tarea 2 - Tecnologias para Desarrollos en Internet.

    Se muestra cuando la base de datos no responde (MySQL apagado, base sin
    crear o contrasena equivocada), en vez de la pagina de error del servidor.
--%>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Financiera Huanca - Error</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/estilo.css">
</head>
<body>

<div class="ventana">
    <div class="barra-titulo">
        <span class="titulo">@ Financiera Huanca</span>
        <span class="cerrar">&#10005;</span>
    </div>
    <div class="panel activo">
        <h2 id="titulo-resumen">No se pudo guardar la solicitud</h2>
        <p>La aplicación no pudo hablar con MySQL. Revisa que el servidor esté
           encendido y que la base <strong>financiera_huanca</strong> exista
           (<code>sudo mysql -u root &lt; esquema.sql</code>).</p>
        <p class="error"><c:out value="${mensaje}"/></p>
        <div class="fila fila-aceptar">
            <a class="boton boton-aceptar"
               href="${pageContext.request.contextPath}/credito">Volver al formulario</a>
        </div>
    </div>
</div>

</body>
</html>
