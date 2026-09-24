<%--
    Tarea 2 - Tecnologias para Desarrollos en Internet.

    PRIMERA VISTA: el formulario del enunciado. Las etiquetas <form:...> son
    las de Spring MVC: cada una se amarra a una propiedad del objeto "credito"
    que el Controller puso en el Model, asi que se pinta sola con el valor que
    traiga y vuelve a llenarse si la validacion falla.
--%>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Financiera Huanca - Solicitud de crédito</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/estilo.css">
</head>
<body>

<form:form modelAttribute="credito" method="post" cssClass="ventana"
           action="${pageContext.request.contextPath}/credito">

    <div class="barra-titulo">
        <span class="titulo">@ Financiera Huanca</span>
        <span class="cerrar">&#10005;</span>
    </div>

    <div class="tabs">
        <button type="button" class="tab"
                onclick="mostrarPestana('panel-personales', this)">Datos personales</button>
        <button type="button" class="tab activo"
                onclick="mostrarPestana('panel-credito', this)">Datos del crédito</button>
    </div>

    <%-- ---------- Pestaña 1: datos personales ---------- --%>
    <div class="panel" id="panel-personales">

        <div class="fila">
            <label for="nombres">Nombres</label>
            <form:input path="nombres" id="nombres" maxlength="100"/>
        </div>
        <form:errors path="nombres" cssClass="error" element="div"/>

        <div class="fila">
            <label for="apellidos">Apellidos</label>
            <form:input path="apellidos" id="apellidos" maxlength="100"/>
        </div>
        <form:errors path="apellidos" cssClass="error" element="div"/>

        <div class="fila">
            <label for="dni">DNI</label>
            <form:input path="dni" id="dni" maxlength="8"/>
        </div>
        <form:errors path="dni" cssClass="error" element="div"/>

        <div class="fila">
            <label for="correo">Correo</label>
            <form:input path="correo" id="correo" type="email" maxlength="150"/>
        </div>
        <form:errors path="correo" cssClass="error" element="div"/>

    </div>

    <%-- ---------- Pestaña 2: datos del crédito (la del enunciado) ---------- --%>
    <div class="panel activo" id="panel-credito">

        <div class="fila">
            <span class="boton-etiqueta">Fecha</span>
            <form:input path="fecha" id="fecha" type="date"/>
        </div>
        <form:errors path="fecha" cssClass="error" element="div"/>

        <div class="fila fila-moneda">
            <span class="grupo-titulo">Moneda</span>
            <label class="radio"><form:radiobutton path="moneda" value="soles"/> Soles (S/.)</label>
            <label class="radio"><form:radiobutton path="moneda" value="dolares"/> Dólares (US$)</label>
        </div>
        <form:errors path="moneda" cssClass="error" element="div"/>

        <div class="fila fila-triple">
            <span>
                <label for="monto">Monto</label>
                <form:input path="monto" id="monto" type="number" step="0.01" min="0"/>
            </span>
            <span>
                <label for="periodo">Periodo</label>
                <form:input path="periodo" id="periodo" type="number" min="1" cssClass="corto"/> meses
            </span>
        </div>
        <form:errors path="monto" cssClass="error" element="div"/>
        <form:errors path="periodo" cssClass="error" element="div"/>

        <%-- Cuota y fecha de vencimiento las calcula el servidor (el bean
             CalculadoraCredito) y se ven en la segunda vista. Estos dos
             campos son solo una vista previa en el navegador: no se envían. --%>
        <div class="fila">
            <button type="button" class="boton" onclick="calcularCuota()">Cuota</button>
            <input type="text" id="cuota" readonly placeholder="la calcula el servidor">
        </div>

        <div class="fila fila-triple">
            <span>
                <label for="tea" class="boton-etiqueta">TEA</label>
                <form:input path="tea" id="tea" type="number" step="0.01" min="0" cssClass="corto"/> %
            </span>
            <span>
                <button type="button" class="boton" onclick="calcularVencimiento()">Fecha vencimiento</button>
                <input type="text" id="fechaVencimiento" readonly placeholder="la calcula el servidor">
            </span>
        </div>
        <form:errors path="tea" cssClass="error" element="div"/>

        <div class="fila fila-aceptar">
            <button type="submit" class="boton boton-aceptar">ACEPTAR</button>
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

    /* Misma fórmula que CalculadoraCredito, solo para la vista previa. */
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
</script>

</body>
</html>
