/* ==========================================================================
   Financiera Huanca - JavaScript del formulario
   - Cambio de pestanas.
   - Vista previa de la cuota y de la fecha de vencimiento (el calculo
     "oficial" lo vuelve a hacer el servlet cuando se envia el formulario).
   ========================================================================== */

// Muestra el panel indicado y marca su pestana como activa.
function mostrarPestana(idPanel, boton) {
    document.querySelectorAll('.panel').forEach(function (p) {
        p.classList.remove('activo');
    });
    document.querySelectorAll('.tab').forEach(function (t) {
        t.classList.remove('activo');
    });
    document.getElementById(idPanel).classList.add('activo');
    boton.classList.add('activo');
}

// Tasa mensual equivalente a la TEA (tasa efectiva anual, en porcentaje).
function tasaMensualDesdeTEA(teaPorcentaje) {
    return Math.pow(1 + teaPorcentaje / 100, 1 / 12) - 1;
}

// Cuota fija por el sistema frances.
function calcularCuota() {
    var monto = parseFloat(document.getElementById('monto').value);
    var tea = parseFloat(document.getElementById('tea').value);
    var meses = parseInt(document.getElementById('periodo').value, 10);
    var salida = document.getElementById('cuota');

    if (!(monto > 0) || !(meses > 0) || isNaN(tea)) {
        salida.value = 'Faltan datos';
        return;
    }

    var i = tasaMensualDesdeTEA(tea);
    var cuota = (i === 0)
        ? monto / meses
        : monto * i / (1 - Math.pow(1 + i, -meses));

    var moneda = document.querySelector('input[name="moneda"]:checked');
    var simbolo = (moneda && moneda.value === 'dolares') ? 'US$' : 'S/.';
    salida.value = simbolo + ' ' + cuota.toFixed(2);
}

// Fecha de vencimiento = fecha de desembolso + periodo (en meses).
function calcularVencimiento() {
    var fecha = document.getElementById('fecha').value;
    var meses = parseInt(document.getElementById('periodo').value, 10);
    var salida = document.getElementById('fechaVencimiento');

    if (!fecha || !(meses > 0)) {
        salida.value = 'Faltan datos';
        return;
    }

    var d = new Date(fecha + 'T00:00:00');
    d.setMonth(d.getMonth() + meses);

    var dia = String(d.getDate()).padStart(2, '0');
    var mes = String(d.getMonth() + 1).padStart(2, '0');
    salida.value = dia + '/' + mes + '/' + d.getFullYear();
}
