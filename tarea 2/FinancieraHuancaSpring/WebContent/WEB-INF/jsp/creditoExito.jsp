<%--
    Tarea 2 - Tecnologias para Desarrollos en Internet.

    SEGUNDA VISTA: los datos capturados, lo que calculó el servidor y las
    últimas solicitudes que hay en MySQL. El objeto "credito" viene de la
    sesión (@SessionAttributes en CreditoController), igual que userSuccess.jsp
    del ejemplo.
--%>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
	<title>Financiera Huanca - Resumen de la solicitud</title>
	<link rel="stylesheet" href="css/estilo.css">
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
			<tr><th>Nombres</th>   <td><c:out value="${credito.nombres}" /></td></tr>
			<tr><th>Apellidos</th> <td><c:out value="${credito.apellidos}" /></td></tr>
			<tr><th>DNI</th>       <td><c:out value="${credito.dni}" /></td></tr>
			<tr><th>Correo</th>    <td><c:out value="${credito.correo}" default="(no capturado)" /></td></tr>
		</table>

		<table class="resultado">
			<caption>Datos del crédito capturados</caption>
			<tr><th>Fecha</th>   <td>${credito.fechaTexto}</td></tr>
			<tr><th>Moneda</th>  <td>${credito.monedaTexto}</td></tr>
			<tr><th>Monto</th>   <td>${credito.simbolo} <fmt:formatNumber value="${credito.monto}" pattern="#,##0.00" /></td></tr>
			<tr><th>Periodo</th> <td>${credito.periodo} meses</td></tr>
			<tr><th>TEA</th>     <td><fmt:formatNumber value="${credito.tea}" pattern="#,##0.00" /> %</td></tr>
		</table>

		<table class="resultado">
			<caption>Cálculos del servidor</caption>
			<tr>
				<th>Cuota mensual (sistema francés)</th>
				<td>${credito.simbolo} <fmt:formatNumber value="${credito.cuota}" pattern="#,##0.00" /></td>
			</tr>
			<tr>
				<th>Fecha de vencimiento</th>
				<td>${credito.fechaVencimiento}</td>
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
					<td><c:out value="${fila.nombres} ${fila.apellidos}" /></td>
					<td>${fila.fechaTexto}</td>
					<td>${fila.simbolo} <fmt:formatNumber value="${fila.monto}" pattern="#,##0.00" /></td>
					<td>${fila.simbolo} <fmt:formatNumber value="${fila.cuota}" pattern="#,##0.00" /></td>
					<td>${fila.fechaVencimiento}</td>
				</tr>
			</c:forEach>
		</table>

		<div class="fila fila-aceptar">
			<a class="boton boton-aceptar" href="creditoRegistro.htm">Nueva solicitud</a>
		</div>

	</div>
</div>

</body>
</html>
