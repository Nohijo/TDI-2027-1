<%--
    Tarea 2 - Tecnologias para Desarrollos en Internet.

    PRIMERA VISTA: el formulario del enunciado. Usa las etiquetas <form:...>
    de Spring, igual que userForm.jsp del ejemplo FormularioDBSpring3MVC.

    Los campos de fecha y numero van con <input> normal porque el
    <form:input> de Spring 3.0 no admite el atributo type de HTML5; el enlace
    con el objeto Credito funciona igual, por el atributo name.
--%>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
   <meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
   <title>Financiera Huanca - Solicitud de crédito</title>
   <link rel="stylesheet" href="css/estilo.css">
</head>
<body>

<form:form method="POST" commandName="credito" cssClass="ventana">

	<div class="barra-titulo">
		<span class="titulo">@ Financiera Huanca</span>
		<span class="cerrar">&#10005;</span>
	</div>

	<div class="tabs">
		<button type="button" class="tab" id="tab-personales"
			onclick="mostrarPestana('panel-personales', this)">Datos personales</button>
		<button type="button" class="tab activo" id="tab-credito"
			onclick="mostrarPestana('panel-credito', this)">Datos del crédito</button>
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
			<button type="button" class="boton" onclick="calcularCuota()">Cuota</button>
			<input type="text" id="cuota" readonly placeholder="la calcula el servidor">
		</div>

		<div class="fila fila-triple">
			<span>
				<label for="tea" class="boton-etiqueta">TEA</label>
				<input type="number" id="tea" name="tea" step="0.01" min="0" class="corto" value="${credito.tea}"> %
			</span>
			<span>
				<button type="button" class="boton" onclick="calcularVencimiento()">Fecha vencimiento</button>
				<input type="text" id="fechaVencimiento" readonly placeholder="la calcula el servidor">
			</span>
		</div>
		<form:errors path="tea" cssClass="error" element="div" />

		<div class="fila fila-aceptar">
			<input type="submit" class="boton boton-aceptar" value="ACEPTAR">
		</div>

	</div>

</form:form>

<script>
	function mostrarPestana(idPanel, boton) {
		var paneles = document.querySelectorAll('.panel');
		for (var i = 0; i < paneles.length; i++) paneles[i].classList.remove('activo');
		var tabs = document.querySelectorAll('.tab');
		for (var j = 0; j < tabs.length; j++) tabs[j].classList.remove('activo');
		document.getElementById(idPanel).classList.add('activo');
		boton.classList.add('activo');
	}

	function simboloMoneda() {
		var radios = document.getElementsByName('moneda');
		for (var k = 0; k < radios.length; k++) {
			if (radios[k].checked && radios[k].value === 'dolares') return 'US$';
		}
		return 'S/.';
	}

	/* Misma fórmula que CreditoServiceImpl, solo para la vista previa. */
	function calcularCuota() {
		var monto = parseFloat(document.getElementById('monto').value);
		var tea = parseFloat(document.getElementById('tea').value);
		var meses = parseInt(document.getElementById('periodo').value, 10);
		var salida = document.getElementById('cuota');
		if (!(monto > 0) || !(meses > 0) || isNaN(tea)) { salida.value = 'Faltan datos'; return; }
		var i = Math.pow(1 + tea / 100, 1 / 12) - 1;
		var cuota = (i === 0) ? monto / meses : monto * i / (1 - Math.pow(1 + i, -meses));
		salida.value = simboloMoneda() + ' ' + cuota.toFixed(2);
	}

	function calcularVencimiento() {
		var fecha = document.getElementById('fecha').value;
		var meses = parseInt(document.getElementById('periodo').value, 10);
		var salida = document.getElementById('fechaVencimiento');
		if (!fecha || !(meses > 0)) { salida.value = 'Faltan datos'; return; }
		var d = new Date(fecha + 'T00:00:00');
		d.setMonth(d.getMonth() + meses);
		var dia = ('0' + d.getDate()).slice(-2);
		var mes = ('0' + (d.getMonth() + 1)).slice(-2);
		salida.value = dia + '/' + mes + '/' + d.getFullYear();
	}
	/* Si la validación dejó avisos, marcar la pestaña que los tiene y abrir la
	   primera con problemas: si no, los avisos de la pestaña oculta no se verían. */
	(function marcarPestanasConErrores() {
		var paneles = [
			{ panel: 'panel-personales', tab: 'tab-personales' },
			{ panel: 'panel-credito',    tab: 'tab-credito' }
		];
		var primeraConError = null;
		for (var i = 0; i < paneles.length; i++) {
			var panel = document.getElementById(paneles[i].panel);
			var tab = document.getElementById(paneles[i].tab);
			if (panel && tab && panel.querySelector('.error')) {
				tab.classList.add('con-error');
				if (!primeraConError) primeraConError = paneles[i];
			}
		}
		if (primeraConError) {
			mostrarPestana(primeraConError.panel, document.getElementById(primeraConError.tab));
		}
	})();
</script>

</body>
</html>
