<%--
    Tarea 3 - Tecnologias para Desarrollos en Internet.

    SEGUNDA VISTA: los datos capturados, lo que calculó el servidor, los pagos
    con tarjeta tal como quedaron en MySQL (como show_contacto.jsp del ejemplo
    MultiRowDBSpring3MVC), los totales por tarjeta y las últimas solicitudes.
    El objeto "credito" viene de la sesión (@SessionAttributes en
    CreditoController), igual que userSuccess.jsp del ejemplo.

    La gráfica de gastos y pagos por tarjeta la dibuja el plugin
    visualize.jQuery del ejemplo "grafico" de la 3er entrega de jQuery, a
    partir de la tabla #tabla-grafica.
--%>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<fmt:setLocale value="es_PE" />
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
	<title>Financiera Huanca - Resumen de la solicitud</title>
	<%-- visualize.css va primero: estilo.css trae el tema de la grafica y tiene
	     que poder cambiar sus reglas, como visualize-light.css en el ejemplo --%>
	<link rel="stylesheet" href="css/visualize.css">
	<link rel="stylesheet" href="css/estilo.css">
	<script type="text/javascript" src="js/jquery.js"></script>
	<script type="text/javascript" src="js/visualize.jQuery.js"></script>
</head>
<body>

<div class="ventana ventana-ancha">

	<div class="barra-titulo">
		<span class="titulo">@ Financiera Huanca</span>
		<span class="cerrar">&#10005;</span>
	</div>

	<div class="panel activo">

		<h2 id="titulo-resumen">Solicitud registrada (folio ${credito.id})</h2>
		<p>Los datos y los pagos quedaron guardados en la base de datos <strong>financiera_huanca</strong>.</p>

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

		<%-- ---------- Pagos con tarjeta (Tarea 3) ---------- --%>
		<table class="resultado tabla-lista tabla-pagos">
			<caption>Gastos y pagos con tarjeta de crédito</caption>
			<tr>
				<th>Fecha</th><th>Mes</th><th>Tarjeta</th><th>Transacción</th><th>Gastos</th><th>Pagos</th>
			</tr>
			<c:forEach var="pago" items="${pagos}">
				<tr>
					<td>${pago.fecha}</td>
					<td class="mes">${pago.mes}</td>
					<td>${pago.tarjeta}</td>
					<td><c:out value="${pago.transaccion}" /></td>
					<td class="numero">
						<c:if test="${not empty pago.gastos}">$ <fmt:formatNumber value="${pago.gastos}" pattern="#,##0.00" /></c:if>
					</td>
					<td class="numero monto-pago">
						<c:if test="${not empty pago.pagos}">$ <fmt:formatNumber value="${pago.pagos}" pattern="#,##0.00" /></c:if>
					</td>
				</tr>
			</c:forEach>
		</table>

		<table class="resultado tabla-lista tabla-totales">
			<caption>Pagos realizados en cada tarjeta</caption>
			<tr><th>Tarjeta</th><th>Gastos</th><th>Pagos</th><th>Saldo</th></tr>
			<c:forEach var="fila" items="${resumen}">
				<tr>
					<td>${fila.tarjeta}</td>
					<td class="numero">$ <fmt:formatNumber value="${fila.gastos}" pattern="#,##0.00" /></td>
					<td class="numero monto-pago">$ <fmt:formatNumber value="${fila.pagos}" pattern="#,##0.00" /></td>
					<td class="numero">$ <fmt:formatNumber value="${fila.saldo}" pattern="#,##0.00" /></td>
				</tr>
			</c:forEach>
		</table>

		<%-- Datos de la gráfica: una columna por tarjeta y un renglón por serie,
		     como la tabla del ejemplo "grafico". Va fuera de la vista; los
		     números van sin formato para que el plugin los pueda leer. --%>
		<table id="tabla-grafica" class="oculta">
			<caption>Gastos y pagos por tarjeta</caption>
			<thead>
				<tr>
					<td></td>
					<c:forEach var="fila" items="${resumen}"><th>${fila.tarjeta}</th></c:forEach>
				</tr>
			</thead>
			<tbody>
				<tr>
					<th>Gastos</th>
					<c:forEach var="fila" items="${resumen}"><td>${fila.gastos}</td></c:forEach>
				</tr>
				<tr>
					<th>Pagos</th>
					<c:forEach var="fila" items="${resumen}"><td>${fila.pagos}</td></c:forEach>
				</tr>
			</tbody>
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

<script type="text/javascript">
$(document).ready(function() {
	/* Igual que example.js del ejemplo "grafico"; el plugin pone la gráfica
	   justo después de la tabla. Rojo para gastos y verde para pagos. */
	$('#tabla-grafica').visualize({
		type: 'bar',
		width: '520px',
		height: '220px',
		colors: ['#be1e2d', '#2e8b57']
	});
});
</script>

</body>
</html>
