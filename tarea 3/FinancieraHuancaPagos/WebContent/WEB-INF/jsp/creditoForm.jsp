<%--
    Tarea 3 - Tecnologias para Desarrollos en Internet.

    PRIMERA VISTA: el formulario de la Tarea 2 y, debajo, la tabla de pagos con
    tarjeta de credito. Usa las etiquetas <form:...> de Spring, igual que
    userForm.jsp del ejemplo FormularioDBSpring3MVC, y la tabla se arma como
    add_contacto.jsp del ejemplo MultiRowDBSpring3MVC: un <c:forEach> con
    inputs llamados listaPagos[0].pagos, listaPagos[1].pagos, ...

    Fecha, Mes, Tarjeta, Transaccion y Gastos se muestran como texto (no son
    editables); solo Pagos es un <input>. Todo se guarda con el boton ACEPTAR.

    En el navegador se usa jQuery con los ejemplos de la 3er entrega:
    jquery.validate (Validacion Avanzada) con los mensajes en espanol de Peru,
    y jQuery para las pestanas, la vista previa de la cuota y los totales de
    la tabla.

    Los campos de fecha y numero van con <input> normal porque el
    <form:input> de Spring 3.0 no admite el atributo type de HTML5; el enlace
    con el objeto Credito funciona igual, por el atributo name.
--%>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<fmt:setLocale value="es_PE" />
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
   <meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
   <title>Financiera Huanca - Solicitud de crédito</title>
   <link rel="stylesheet" href="css/estilo.css">
   <script type="text/javascript" src="js/jquery.js"></script>
   <script type="text/javascript" src="js/jquery.validate.js"></script>
   <script type="text/javascript" src="js/messages_es_PE.js" charset="UTF-8"></script>
</head>
<body>

<form:form method="POST" commandName="credito" cssClass="ventana ventana-ancha">

	<div class="barra-titulo">
		<span class="titulo">@ Financiera Huanca</span>
		<span class="cerrar">&#10005;</span>
	</div>

	<div class="tabs">
		<button type="button" class="tab" id="tab-personales" data-panel="panel-personales">Datos personales</button>
		<button type="button" class="tab activo" id="tab-credito" data-panel="panel-credito">Datos del crédito</button>
	</div>

	<%-- ---------- Pestaña 1: datos personales ---------- --%>
	<div class="panel" id="panel-personales">

		<div class="fila">
			<label for="nombres">Nombres</label>
			<form:input path="nombres" id="nombres" maxlength="100" />
		</div>
		<form:errors path="nombres" cssClass="error" element="div" />

		<div class="fila">
			<label for="apellidos">Apellidos</label>
			<form:input path="apellidos" id="apellidos" maxlength="100" />
		</div>
		<form:errors path="apellidos" cssClass="error" element="div" />

		<div class="fila">
			<label for="dni">DNI</label>
			<form:input path="dni" id="dni" maxlength="8" />
		</div>
		<form:errors path="dni" cssClass="error" element="div" />

		<div class="fila">
			<label for="correo">Correo</label>
			<form:input path="correo" id="correo" maxlength="150" />
		</div>
		<form:errors path="correo" cssClass="error" element="div" />

	</div>

	<%-- ---------- Pestaña 2: datos del crédito (la del enunciado) ---------- --%>
	<div class="panel activo" id="panel-credito">

		<div class="fila">
			<span class="boton-etiqueta">Fecha</span>
			<input type="date" id="fecha" name="fecha" value="${credito.fecha}">
		</div>
		<form:errors path="fecha" cssClass="error" element="div" />

		<div class="fila fila-moneda">
			<span class="grupo-titulo">Moneda</span>
			<label class="radio"><form:radiobutton path="moneda" value="soles" /> Soles (S/.)</label>
			<label class="radio"><form:radiobutton path="moneda" value="dolares" /> Dólares (US$)</label>
		</div>
		<form:errors path="moneda" cssClass="error" element="div" />

		<div class="fila fila-triple">
			<span>
				<label for="monto">Monto</label>
				<input type="number" id="monto" name="monto" step="0.01" min="0" value="${credito.monto}">
			</span>
			<span>
				<label for="periodo">Periodo</label>
				<input type="number" id="periodo" name="periodo" min="1" class="corto" value="${credito.periodo}"> meses
			</span>
		</div>
		<form:errors path="monto" cssClass="error" element="div" />
		<form:errors path="periodo" cssClass="error" element="div" />

		<%-- La cuota y el vencimiento los calcula el servidor (CreditoServiceImpl)
		     y se ven en la segunda vista. Estos dos campos son solo una vista
		     previa en el navegador: no se envían. --%>
		<div class="fila">
			<button type="button" class="boton" id="boton-cuota">Cuota</button>
			<input type="text" id="cuota" readonly placeholder="la calcula el servidor">
		</div>

		<div class="fila fila-triple">
			<span>
				<label for="tea" class="boton-etiqueta">TEA</label>
				<input type="number" id="tea" name="tea" step="0.01" min="0" class="corto" value="${credito.tea}"> %
			</span>
			<span>
				<button type="button" class="boton" id="boton-vencimiento">Fecha vencimiento</button>
				<input type="text" id="fechaVencimiento" readonly placeholder="la calcula el servidor">
			</span>
		</div>
		<form:errors path="tea" cssClass="error" element="div" />

	</div>

	<%-- ---------- Tabla de pagos con tarjeta de crédito (Tarea 3) ---------- --%>
	<div class="seccion-pagos">

		<h3 class="seccion-titulo">Gastos con tarjeta de crédito</h3>
		<p class="indicacion">Registre abajo los pagos realizados en cada tarjeta.
			Solo se puede escribir en la columna <strong>Pagos</strong>; se guarda al oprimir ACEPTAR.</p>

		<table class="resultado tabla-lista tabla-pagos" id="tabla-pagos">
			<thead>
				<tr>
					<th>Fecha</th>
					<th>Mes</th>
					<th>Tarjeta</th>
					<th>Transacción</th>
					<th>Gastos</th>
					<th>Pagos</th>
				</tr>
			</thead>
			<tbody>
			<c:forEach items="${credito.listaPagos}" var="pago" varStatus="status">
				<tr data-tarjeta="${pago.tarjeta}">
					<td>${pago.fecha}</td>
					<td class="mes">${pago.mes}</td>
					<td>${pago.tarjeta}</td>
					<td><c:out value="${pago.transaccion}" /></td>
					<td class="numero gasto" data-valor="${pago.gastos}">
						<c:if test="${not empty pago.gastos}">$ <fmt:formatNumber value="${pago.gastos}" pattern="#,##0.00" /></c:if>
					</td>
					<td class="celda-pago">
						<input type="text" class="pago" name="listaPagos[${status.index}].pagos"
							value="<c:out value='${pago.pagos}' />" size="10" autocomplete="off"
							title="Pago a la tarjeta ${pago.tarjeta}">
						<form:errors path="listaPagos[${status.index}].pagos" cssClass="error" element="div" />
					</td>
				</tr>
			</c:forEach>
			</tbody>
		</table>

		<%-- Lo llena jQuery cada vez que cambia un pago --%>
		<table class="resultado tabla-lista tabla-totales" id="tabla-totales">
			<caption>Totales por tarjeta</caption>
			<thead>
				<tr><th>Tarjeta</th><th>Gastos</th><th>Pagos</th><th>Saldo</th></tr>
			</thead>
			<tbody></tbody>
		</table>

	</div>

	<div class="fila fila-aceptar">
		<input type="submit" class="boton boton-aceptar" value="ACEPTAR">
	</div>

</form:form>

<script type="text/javascript">
$(document).ready(function() {

	/* ---------------- Pestañas ---------------- */
	function mostrarPestana(idPanel) {
		$('.panel').removeClass('activo');
		$('.tab').removeClass('activo');
		$('#' + idPanel).addClass('activo');
		$('.tab[data-panel="' + idPanel + '"]').addClass('activo');
	}

	$('.tab').click(function() {
		mostrarPestana($(this).data('panel'));
	});

	/* Marca las pestañas que tienen avisos y abre la primera de ellas: si no,
	   los avisos de la pestaña oculta no se verían. */
	function marcarPestanasConErrores(selectorAviso) {
		var primera = null;
		$('.panel').each(function() {
			var tab = $('.tab[data-panel="' + this.id + '"]');
			var conAviso = $(this).find(selectorAviso).filter(function() {
				return $(this).text().length > 0 && this.style.display !== 'none';
			}).length > 0;
			tab.toggleClass('con-error', conAviso);
			if (conAviso && primera === null) primera = this.id;
		});
		if (primera !== null) mostrarPestana(primera);
	}

	/* Avisos que mandó el servidor (CreditoValidator) */
	marcarPestanasConErrores('.error');

	/* ---------------- Vista previa de cuota y vencimiento ---------------- */
	function simboloMoneda() {
		return $('input[name="moneda"]:checked').val() === 'dolares' ? 'US$' : 'S/.';
	}

	/* Misma fórmula que CreditoServiceImpl, solo para la vista previa. */
	$('#boton-cuota').click(function() {
		var monto = parseFloat($('#monto').val());
		var tea = parseFloat($('#tea').val());
		var meses = parseInt($('#periodo').val(), 10);
		if (!(monto > 0) || !(meses > 0) || isNaN(tea)) { $('#cuota').val('Faltan datos'); return; }
		var i = Math.pow(1 + tea / 100, 1 / 12) - 1;
		var cuota = (i === 0) ? monto / meses : monto * i / (1 - Math.pow(1 + i, -meses));
		$('#cuota').val(simboloMoneda() + ' ' + formato(cuota));
	});

	$('#boton-vencimiento').click(function() {
		var fecha = $('#fecha').val();
		var meses = parseInt($('#periodo').val(), 10);
		if (!fecha || !(meses > 0)) { $('#fechaVencimiento').val('Faltan datos'); return; }
		var d = new Date(fecha + 'T00:00:00');
		d.setMonth(d.getMonth() + meses);
		var dia = ('0' + d.getDate()).slice(-2);
		var mes = ('0' + (d.getMonth() + 1)).slice(-2);
		$('#fechaVencimiento').val(dia + '/' + mes + '/' + d.getFullYear());
	});

	/* ---------------- Totales de la tabla de pagos ---------------- */
	var IMPORTE = /^\d{1,10}(\.\d{1,2})?$/;

	/* 1234.5 -> "1,234.50" (igual que fmt:formatNumber con es_PE) */
	function formato(n) {
		return n.toFixed(2).replace(/\B(?=(\d{3})+(?!\d))/g, ',');
	}

	function actualizaTotales() {
		var totales = {}, orden = [];
		$('#tabla-pagos tbody tr').each(function() {
			var tarjeta = $(this).data('tarjeta');
			if (!totales[tarjeta]) { totales[tarjeta] = { gastos: 0, pagos: 0 }; orden.push(tarjeta); }
			var gasto = parseFloat($(this).find('.gasto').data('valor'));
			var pago = $.trim($(this).find('input.pago').val());
			if (!isNaN(gasto)) totales[tarjeta].gastos += gasto;
			if (IMPORTE.test(pago)) totales[tarjeta].pagos += parseFloat(pago);
		});

		var cuerpo = $('#tabla-totales tbody').empty();
		$.each(orden, function(k, tarjeta) {
			var t = totales[tarjeta];
			$('<tr>')
				.append($('<td>').text(tarjeta))
				.append($('<td class="numero">').text('$ ' + formato(t.gastos)))
				.append($('<td class="numero monto-pago">').text('$ ' + formato(t.pagos)))
				.append($('<td class="numero">').text('$ ' + formato(t.gastos - t.pagos)))
				.appendTo(cuerpo);
		});
	}

	$('#tabla-pagos').on('input change', 'input.pago', actualizaTotales);
	actualizaTotales();

	/* ---------------- Validación con jquery.validate ---------------- */
	$.validator.addMethod('importe', function(valor, elemento) {
		return this.optional(elemento) || IMPORTE.test($.trim(valor));
	}, 'Escriba el pago con punto decimal y sin comas, por ejemplo 2670.50');

	/* Cada input de la columna Pagos tiene la clase "pago" */
	$.validator.addClassRules('pago', { importe: true });

	$('#credito').validate({
		ignore: [],                 /* también revisa la pestaña que no está a la vista */
		ignoreTitle: true,          /* el title de los inputs no es un mensaje de error */
		errorElement: 'div',
		errorClass: 'invalido',
		rules: {
			nombres:   { required: true },
			apellidos: { required: true },
			dni:       { required: true, digits: true, minlength: 8, maxlength: 8 },
			correo:    { email: true },
			fecha:     { required: true, dateISO: true },
			/* step: false -> sin la regla que jquery.validate 1.15 saca del
			   atributo step="0.01"; redondea mal (5000 no le sale multiplo) */
			monto:     { required: true, number: true, min: 0.01, step: false },
			periodo:   { required: true, digits: true, min: 1 },
			tea:       { required: true, number: true, min: 0, step: false }
		},
		messages: {
			dni: {
				digits: 'El DNI son 8 dígitos',
				minlength: 'El DNI son 8 dígitos',
				maxlength: 'El DNI son 8 dígitos'
			}
		},
		errorPlacement: function(aviso, elemento) {
			if (elemento.closest('td').length)
				aviso.appendTo(elemento.closest('td'));
			else
				aviso.insertAfter(elemento.closest('.fila'));
		},
		invalidHandler: function() {
			/* jquery.validate pinta los avisos después de este evento */
			setTimeout(function() { marcarPestanasConErrores('div.invalido'); }, 0);
		}
	});
});
</script>

</body>
</html>
