package mx.unam.ciencias.tdi;

import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Tarea 1 - Tecnologias para Desarrollos en Internet.
 *
 * Servlet de "Financiera Huanca". Un mismo servlet atiende dos momentos:
 *
 *   1. doGet()  -> el navegador PIDE la pagina: se devuelve la PRIMERA vista,
 *                  el formulario de solicitud de credito.
 *   2. doPost() -> el usuario ENVIA el formulario: se leen los campos, se
 *                  calculan la cuota mensual y la fecha de vencimiento y se
 *                  devuelve la SEGUNDA vista con todo lo capturado.
 *
 * Es el mismo patron del ejemplo de clase "Forma_de_Compra".
 *
 * La anotacion @WebServlet asocia la clase con la URL /credito; dentro del
 * contexto de la aplicacion se llega en:
 *     http://localhost:8080/FinancieraHuanca/credito
 */
@WebServlet(name = "CreditoServlet", urlPatterns = {"/credito"})
public class CreditoServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    /* ===================================================================== */
    /*  PRIMERA VISTA: el formulario                                          */
    /* ===================================================================== */
    @Override
    protected void doGet(HttpServletRequest peticion, HttpServletResponse respuesta)
            throws ServletException, IOException {

        respuesta.setContentType("text/html;charset=UTF-8");

        // getContextPath() devuelve "/FinancieraHuanca": se antepone a las rutas
        // para que el CSS, el JS y el action funcionen aunque cambie el nombre
        // con el que el servidor publique la aplicacion.
        String ctx = peticion.getContextPath();

        String html = """
                <!DOCTYPE html>
                <html lang="es">
                <head>
                    <meta charset="UTF-8">
                    <meta name="viewport" content="width=device-width, initial-scale=1">
                    <title>Financiera Huanca - Solicitud de credito</title>
                    <link rel="stylesheet" href="__CTX__/css/estilo.css">
                </head>
                <body>
                <form class="ventana" method="post" action="__CTX__/credito">

                    <div class="barra-titulo">
                        <span class="titulo">@ Financiera Huanca</span>
                        <span class="cerrar">&#10005;</span>
                    </div>

                    <div class="tabs">
                        <button type="button" class="tab" id="tab-personales"
                                onclick="mostrarPestana('panel-personales', this)">Datos personales</button>
                        <button type="button" class="tab activo" id="tab-credito"
                                onclick="mostrarPestana('panel-credito', this)">Datos del credito</button>
                    </div>

                    <!-- Pestana 1: datos personales -->
                    <div class="panel" id="panel-personales">
                        <div class="fila">
                            <label for="nombres">Nombres</label>
                            <input type="text" id="nombres" name="nombres">
                        </div>
                        <div class="fila">
                            <label for="apellidos">Apellidos</label>
                            <input type="text" id="apellidos" name="apellidos">
                        </div>
                        <div class="fila">
                            <label for="dni">DNI</label>
                            <input type="text" id="dni" name="dni" maxlength="8">
                        </div>
                        <div class="fila">
                            <label for="correo">Correo</label>
                            <input type="email" id="correo" name="correo">
                        </div>
                    </div>

                    <!-- Pestana 2: datos del credito (la que aparece en el enunciado) -->
                    <div class="panel activo" id="panel-credito">

                        <div class="fila">
                            <span class="boton-etiqueta">Fecha</span>
                            <input type="date" id="fecha" name="fecha">
                        </div>

                        <div class="fila fila-moneda">
                            <span class="grupo-titulo">Moneda</span>
                            <label class="radio"><input type="radio" name="moneda" value="soles" checked> Soles (S/.)</label>
                            <label class="radio"><input type="radio" name="moneda" value="dolares"> Dolares (US$)</label>
                        </div>

                        <div class="fila fila-triple">
                            <span>
                                <label for="monto">Monto</label>
                                <input type="number" id="monto" name="monto" step="0.01" min="0">
                            </span>
                            <span>
                                <label for="periodo">Periodo</label>
                                <input type="number" id="periodo" name="periodo" min="1" class="corto"> meses
                            </span>
                        </div>

                        <div class="fila">
                            <button type="button" class="boton" onclick="calcularCuota()">Cuota</button>
                            <input type="text" id="cuota" name="cuota" readonly placeholder="se calcula al enviar">
                        </div>

                        <div class="fila fila-triple">
                            <span>
                                <span class="boton-etiqueta">TEA</span>
                                <input type="number" id="tea" name="tea" step="0.01" min="0" class="corto"> %
                            </span>
                            <span>
                                <button type="button" class="boton" onclick="calcularVencimiento()">Fecha vencimiento</button>
                                <input type="text" id="fechaVencimiento" name="fechaVencimiento" readonly placeholder="se calcula al enviar">
                            </span>
                        </div>

                        <div class="fila fila-aceptar">
                            <button type="submit" class="boton boton-aceptar">ACEPTAR</button>
                        </div>
                    </div>
                </form>

                <script src="__CTX__/js/financiera.js"></script>
                </body>
                </html>
                """.replace("__CTX__", ctx);

        try (PrintWriter salida = respuesta.getWriter()) {
            salida.print(html);
        }
    }

    /* ===================================================================== */
    /*  SEGUNDA VISTA: los campos capturados + los calculos                   */
    /* ===================================================================== */
    @Override
    protected void doPost(HttpServletRequest peticion, HttpServletResponse respuesta)
            throws ServletException, IOException {

        // Para que los acentos y la "n" lleguen bien desde el formulario.
        peticion.setCharacterEncoding("UTF-8");
        respuesta.setContentType("text/html;charset=UTF-8");
        String ctx = peticion.getContextPath();

        // ---- 1) Leer TODOS los campos que mando el formulario ----
        String nombres   = leer(peticion, "nombres");
        String apellidos = leer(peticion, "apellidos");
        String dni       = leer(peticion, "dni");
        String correo    = leer(peticion, "correo");
        String fecha     = leer(peticion, "fecha");
        String moneda    = leer(peticion, "moneda");
        String monto     = leer(peticion, "monto");
        String periodo   = leer(peticion, "periodo");
        String tea       = leer(peticion, "tea");
        String cuotaForm = leer(peticion, "cuota");
        String vencForm  = leer(peticion, "fechaVencimiento");

        boolean esDolares = "dolares".equals(moneda);
        String simbolo = esDolares ? "US$" : "S/.";
        String monedaTexto = esDolares ? "Dolares (US$)" : "Soles (S/.)";

        // ---- 2) Calcular con lo capturado ----
        String cuotaCalc = calcularCuota(monto, tea, periodo, simbolo);
        String vencCalc  = calcularVencimiento(fecha, periodo);

        // ---- 3) Armar las tablas de la respuesta ----
        Map<String, String> personales = new LinkedHashMap<>();
        personales.put("Nombres", nombres);
        personales.put("Apellidos", apellidos);
        personales.put("DNI", dni);
        personales.put("Correo", correo);

        Map<String, String> credito = new LinkedHashMap<>();
        credito.put("Fecha", fecha);
        credito.put("Moneda", monedaTexto);
        credito.put("Monto", simbolo + " " + monto);
        credito.put("Periodo", periodo + " meses");
        credito.put("TEA", tea + " %");
        credito.put("Cuota (enviada por el formulario)", cuotaForm);
        credito.put("Fecha de vencimiento (enviada por el formulario)", vencForm);

        Map<String, String> calculos = new LinkedHashMap<>();
        calculos.put("Cuota mensual (sistema frances)", cuotaCalc);
        calculos.put("Fecha de vencimiento", vencCalc);

        String html = """
                <!DOCTYPE html>
                <html lang="es">
                <head>
                    <meta charset="UTF-8">
                    <meta name="viewport" content="width=device-width, initial-scale=1">
                    <title>Financiera Huanca - Resumen de la solicitud</title>
                    <link rel="stylesheet" href="__CTX__/css/estilo.css">
                </head>
                <body>
                <div class="ventana">

                    <div class="barra-titulo">
                        <span class="titulo">@ Financiera Huanca</span>
                        <span class="cerrar">&#10005;</span>
                    </div>

                    <div class="panel activo">
                        <h2 id="titulo-resumen">Solicitud registrada</h2>
                        <p>Estos son los datos que recibio el servlet:</p>

                        __PERSONALES__
                        __CREDITO__
                        __CALCULOS__

                        <div class="fila fila-aceptar">
                            <a class="boton boton-aceptar" href="__CTX__/credito">Nueva solicitud</a>
                        </div>
                    </div>
                </div>
                </body>
                </html>
                """
                .replace("__PERSONALES__", tabla("Datos personales capturados", personales))
                .replace("__CREDITO__", tabla("Datos del credito capturados", credito))
                .replace("__CALCULOS__", tabla("Calculos realizados por el servlet", calculos))
                .replace("__CTX__", ctx);

        try (PrintWriter salida = respuesta.getWriter()) {
            salida.print(html);
        }
    }

    /* ===================================================================== */
    /*  METODOS AUXILIARES                                                    */
    /* ===================================================================== */

    /** Devuelve el parametro ya recortado, o "(no capturado)" si vino vacio. */
    private static String leer(HttpServletRequest peticion, String nombre) {
        String valor = peticion.getParameter(nombre);
        if (valor == null || valor.isBlank()) {
            return "(no capturado)";
        }
        return valor.trim();
    }

    /** Construye una tabla HTML a partir de un mapa etiqueta -> valor. */
    private static String tabla(String titulo, Map<String, String> filas) {
        StringBuilder sb = new StringBuilder();
        sb.append("<table class=\"resultado\"><caption>").append(esc(titulo)).append("</caption>");
        for (Map.Entry<String, String> fila : filas.entrySet()) {
            sb.append("<tr><th>").append(esc(fila.getKey()))
              .append("</th><td>").append(esc(fila.getValue())).append("</td></tr>");
        }
        sb.append("</table>");
        return sb.toString();
    }

    /** Escapa los caracteres especiales de HTML (evita romper la pagina / XSS). */
    private static String esc(String texto) {
        if (texto == null) {
            return "";
        }
        return texto.replace("&", "&amp;")
                    .replace("<", "&lt;")
                    .replace(">", "&gt;")
                    .replace("\"", "&quot;");
    }

    /**
     * Cuota mensual por el sistema frances (cuotas fijas):
     *
     *     cuota = M * i / (1 - (1 + i)^(-n))
     *
     * M = monto, n = numero de meses, i = tasa mensual equivalente a la TEA:
     *
     *     i = (1 + TEA)^(1/12) - 1
     */
    private static String calcularCuota(String montoTxt, String teaTxt,
                                        String periodoTxt, String simbolo) {
        try {
            double monto = Double.parseDouble(montoTxt);
            double teaPorcentaje = Double.parseDouble(teaTxt);
            int meses = Integer.parseInt(periodoTxt);

            if (monto <= 0 || meses <= 0) {
                return "(faltan datos para calcular)";
            }

            double tasaAnual = teaPorcentaje / 100.0;
            double tasaMensual = Math.pow(1 + tasaAnual, 1.0 / 12.0) - 1;

            double cuota;
            if (tasaMensual == 0) {
                cuota = monto / meses;
            } else {
                cuota = monto * tasaMensual / (1 - Math.pow(1 + tasaMensual, -meses));
            }

            BigDecimal redondeada = BigDecimal.valueOf(cuota).setScale(2, RoundingMode.HALF_UP);
            return simbolo + " " + String.format(Locale.US, "%,.2f", redondeada);
        } catch (NumberFormatException e) {
            return "(revisa monto, TEA y periodo)";
        }
    }

    /** Fecha de vencimiento = fecha de desembolso + periodo (en meses). */
    private static String calcularVencimiento(String fechaTxt, String periodoTxt) {
        try {
            LocalDate desembolso = LocalDate.parse(fechaTxt); // <input type="date"> -> yyyy-MM-dd
            int meses = Integer.parseInt(periodoTxt);
            LocalDate vencimiento = desembolso.plusMonths(meses);
            return vencimiento.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
        } catch (Exception e) {
            return "(revisa la fecha y el periodo)";
        }
    }
}
