/*
 * Practica 3 - Tecnologias para Desarrollos en Internet.
 *
 * Dinamismo del catalogo con jQuery: filtrado en vivo, validacion con jQuery
 * Validation (como en la Tarea 3) y editar/eliminar por $.ajax, sin recargar.
 * Los textos vienen de messages.properties por los atributos data-* de #app.
 *
 * Autor: Miguel Angel Marquez Cristoval
 */
$(function () {
    var $app = $('#app');
    var contexto = $app.attr('data-contexto');
    var formatoPrecio = new Intl.NumberFormat($app.attr('data-idioma'), {
        minimumFractionDigits: 2,
        maximumFractionDigits: 2
    });

    var modalEditar = new bootstrap.Modal(document.getElementById('modalEditar'));
    var modalEliminar = new bootstrap.Modal(document.getElementById('modalEliminar'));
    var $filaEnEdicion = null;
    var $filaAEliminar = null;

    function urlDelLibro($fila) {
        return contexto + '/libros/' + $fila.attr('data-id');
    }

    /** Se usa .attr y no .data: .data convertiria el libro "1984" en un numero. */
    function datosDe($fila) {
        return {
            nombre: $fila.attr('data-nombre'),
            autor: $fila.attr('data-autor'),
            precio: $fila.attr('data-precio')
        };
    }

    function avisar(tipo, texto) {
        var $aviso = $('<div class="alert alert-dismissible fade show" role="alert">')
            .addClass('alert-' + tipo)
            .text(texto);
        $('<button type="button" class="btn-close" data-bs-dismiss="alert">')
            .attr('aria-label', $app.attr('data-cerrar'))
            .appendTo($aviso);
        $('#avisos').empty().append($aviso);
    }

    function avisarFallo(xhr) {
        avisar('danger', $app.attr(xhr.status === 404 ? 'data-no-existe' : 'data-error'));
    }

    /* ---------- Filtrado en vivo ---------- */

    /** Sin mayusculas ni acentos: "garcia" encuentra a "García". */
    function normalizar(texto) {
        return texto.normalize('NFD').replace(/[̀-ͯ]/g, '').toLowerCase();
    }

    function filtrar() {
        var buscado = normalizar($.trim($('#filtro').val()));
        var visibles = 0;

        $('#tablaLibros .fila-libro').each(function () {
            var $fila = $(this);
            var datos = datosDe($fila);
            var coincide = normalizar(datos.nombre + ' ' + datos.autor).indexOf(buscado) !== -1;
            $fila.toggleClass('d-none', !coincide);
            if (coincide) {
                visibles++;
            }
        });

        $('#sinResultados').toggleClass('d-none', visibles > 0);
        $('#contador').text(visibles === 1
            ? $app.attr('data-uno')
            : $app.attr('data-varios').replace('{0}', visibles));
    }

    $('#filtro').on('input', filtrar);
    filtrar();

    /* ---------- Validacion en el cliente ---------- */

    $.validator.setDefaults({
        errorElement: 'div',
        errorClass: 'is-invalid',
        normalizer: function (valor) {
            return $.trim(valor);
        },
        errorPlacement: function (error, campo) {
            error.addClass('invalid-feedback').insertAfter(campo);
        }
    });

    // El metodo "step" de jQuery Validation 1.15 calcula valor % 0.01 con
    // decimales binarios y rechaza precios correctos como 1234.5.
    $.validator.methods.step = function (valor, campo) {
        return this.optional(campo) || /^\d+(\.\d{1,2})?$/.test(valor);
    };

    $('#formAgregar').validate();

    /* ---------- Editar ---------- */

    var validadorEditar = $('#formEditar').validate({
        submitHandler: function () {
            $.ajax({
                url: urlDelLibro($filaEnEdicion),
                method: 'PUT',
                data: $('#formEditar').serialize()
            }).done(function (libro) {
                $filaEnEdicion.attr({
                    'data-nombre': libro.nombre,
                    'data-autor': libro.autor,
                    'data-precio': libro.precio
                });
                $filaEnEdicion.find('.celda-nombre').text(libro.nombre);
                $filaEnEdicion.find('.celda-autor').text(libro.autor);
                $filaEnEdicion.find('.celda-precio').text('$' + formatoPrecio.format(libro.precio));
                filtrar();
                modalEditar.hide();
                avisar('success', $app.attr('data-actualizado'));
            }).fail(function (xhr) {
                if (xhr.status === 400 && xhr.responseJSON) {
                    validadorEditar.showErrors(xhr.responseJSON);
                    return;
                }
                modalEditar.hide();
                avisarFallo(xhr);
            });
        }
    });

    $('#tablaLibros').on('click', '.btn-editar', function () {
        $filaEnEdicion = $(this).closest('tr');
        var datos = datosDe($filaEnEdicion);
        validadorEditar.resetForm();
        $('#editarNombre').val(datos.nombre);
        $('#editarAutor').val(datos.autor);
        $('#editarPrecio').val(datos.precio);
        modalEditar.show();
    });

    /* ---------- Eliminar ---------- */

    $('#tablaLibros').on('click', '.btn-eliminar', function () {
        $filaAEliminar = $(this).closest('tr');
        var datos = datosDe($filaAEliminar);
        $('#libroAEliminar').text(datos.nombre + ' — ' + datos.autor);
        modalEliminar.show();
    });

    $('#confirmarEliminar').on('click', function () {
        $.ajax({
            url: urlDelLibro($filaAEliminar),
            method: 'DELETE'
        }).done(function () {
            $filaAEliminar.remove();
            filtrar();
            avisar('success', $app.attr('data-eliminado'));
        }).fail(avisarFallo).always(function () {
            modalEliminar.hide();
        });
    });
});
