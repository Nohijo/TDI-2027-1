<%--
    Tarea 2 - Tecnologias para Desarrollos en Internet.

    SEGUNDA VISTA: los datos capturados, lo que calculo el servidor y las
    ultimas solicitudes que hay en MySQL (para ver que si se guardaron).
--%>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Financiera Huanca - Resumen de la solicitud</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/estilo.css">
</head>
<body>

<div class="ventana">

    <div class="barra-titulo">
        <span class="titulo">@ Financiera Huanca</span>
        <span class="cerrar">&#10005;</span>
    </div>

    <div class="panel activo">

        <h2 id="titulo-resumen">Solicitud registrada (folio ${credito.id})</h2>
        <p>Los datos quedaron guardados en la base de datos <strong>financiera_huanca</strong>.</p>

        <table class="resultado">
            <caption>Datos personales capturados</caption>
            <tr><th>Nombres</th>   <td><c:out value="${credito.nombres}"/></td></tr>
            <tr><th>Apellidos</th> <td><c:out value="${credito.apellidos}"/></td></tr>
            <tr><th>DNI</th>       <td><c:out value="${credito.dni}"/></td></tr>
            <tr><th>Correo</th>    <td><c:out value="${credito.correo}" default="(no capturado)"/></td></tr>
        </table>

        <table class="resultado">
            <caption>Datos del crédito capturados</caption>
            <tr><th>Fecha</th>   <td>${credito.fechaTexto}</td></tr>
            <tr><th>Moneda</th>  <td>${credito.monedaTexto}</td></tr>
            <tr><th>Monto</th>   <td>${credito.simbolo} <fmt:formatNumber value="${credito.monto}" pattern="#,##0.00"/></td></tr>
            <tr><th>Periodo</th> <td>${credito.periodo} meses</td></tr>
            <tr><th>TEA</th>     <td><fmt:formatNumber value="${credito.tea}" pattern="#,##0.00"/> %</td></tr>
        </table>

        <table class="resultado">
            <caption>Cálculos del servidor</caption>
            <tr>
                <th>Cuota mensual (sistema francés)</th>
                <td>${credito.simbolo} <fmt:formatNumber value="${credito.cuota}" pattern="#,##0.00"/></td>
            </tr>
            <tr>
                <th>Fecha de vencimiento</th>
                <td>${credito.fechaVencimientoTexto}</td>
            </tr>
        </table>

        <table class="resultado tabla-lista">
            <caption>Últimas solicitudes en la base de datos</caption>
            <tr>
                <th>Folio</th><th>Solicitante</th><th>Fecha</th>
                <th>Monto</th><th>Cuota</th><th>Vence</th>
            </tr>
            <c:forEach var="fila" items="${ultimas}">
                <tr>
                    <td>${fila.id}</td>
                    <td><c:out value="${fila.nombres} ${fila.apellidos}"/></td>
                    <td>${fila.fechaTexto}</td>
                    <td>${fila.simbolo} <fmt:formatNumber value="${fila.monto}" pattern="#,##0.00"/></td>
                    <td>${fila.simbolo} <fmt:formatNumber value="${fila.cuota}" pattern="#,##0.00"/></td>
                    <td>${fila.fechaVencimientoTexto}</td>
                </tr>
            </c:forEach>
        </table>

        <div class="fila fila-aceptar">
            <a class="boton boton-aceptar"
               href="${pageContext.request.contextPath}/credito">Nueva solicitud</a>
        </div>

    </div>
</div>

</body>
</html>
