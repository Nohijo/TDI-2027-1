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

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Tarea 1 - Tecnologias para Desarrollos en Internet.
 *
 * Servlet de "Financiera Huanca". El mismo servlet atiende dos momentos:
 *
 *   1. doGet()  -> el navegador PIDE la pagina: devuelve la PRIMERA vista,
 *                  el formulario de solicitud de credito.
 *   2. doPost() -> el usuario ENVIA el formulario: lee los campos, calcula la
 *                  cuota mensual y la fecha de vencimiento, y devuelve la
 *                  SEGUNDA vista con todo lo capturado.
 *
 * Mismo patron que el ejemplo de clase "Forma_de_Compra".
 *
 * Compatible con Apache Tomcat 7 (Servlet 3.0, paquete javax.servlet) y Java 8.
 * El mapeo a la URL /credito esta en WEB-INF/web.xml.
 */
public class CreditoServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    /* ================================================================= */
    /*  PRIMERA VISTA: el formulario                                      */
    /* ================================================================= */
    @Override
    protected void doGet(HttpServletRequest peticion, HttpServletResponse respuesta)
            throws ServletException, IOException {

        respuesta.setContentType("text/html;charset=UTF-8");

        // getContextPath() devuelve "/FinancieraHuanca": se antepone a las rutas
        // para que el CSS, el JS y el "action" funcionen aunque cambie el nombre
        // con el que el servidor publique la aplicacion.
        String ctx = peticion.getContextPath();

        PrintWriter out = respuesta.getWriter();
        try {
            out.println("<!DOCTYPE html>");
            out.println("<html lang='es'>");
            out.println("<head>");
            out.println("  <meta charset='UTF-8'>");
            out.println("  <meta name='viewport' content='width=device-width, initial-scale=1'>");
            out.println("  <title>Financiera Huanca - Solicitud de credito</title>");
            out.println("  <link rel='stylesheet' href='" + ctx + "/css/estilo.css'>");
            out.println("</head>");
            out.println("<body>");

            out.println("<form class='ventana' method='post' action='" + ctx + "/credito'>");

            out.println("  <div class='barra-titulo'>");
            out.println("    <span class='titulo'>@ Financiera Huanca</span>");
            out.println("    <span class='cerrar'>&#10005;</span>");
            out.println("  </div>");

            out.println("  <div class='tabs'>");
            out.println("    <button type='button' class='tab' id='tab-personales' onclick='mostrarPestana(\"panel-personales\", this)'>Datos personales</button>");
            out.println("    <button type='button' class='tab activo' id='tab-credito' onclick='mostrarPestana(\"panel-credito\", this)'>Datos del credito</button>");
            out.println("  </div>");

            // ---- Pestana 1: datos personales ----
            out.println("  <div class='panel' id='panel-personales'>");
            out.println("    <div class='fila'><label for='nombres'>Nombres</label><input type='text' id='nombres' name='nombres'></div>");
            out.println("    <div class='fila'><label for='apellidos'>Apellidos</label><input type='text' id='apellidos' name='apellidos'></div>");
            out.println("    <div class='fila'><label for='dni'>DNI</label><input type='text' id='dni' name='dni' maxlength='8'></div>");
            out.println("    <div class='fila'><label for='correo'>Correo</label><input type='email' id='correo' name='correo'></div>");
            out.println("  </div>");

            // ---- Pestana 2: datos del credito (la que aparece en el enunciado) ----
            out.println("  <div class='panel activo' id='panel-credito'>");

            out.println("    <div class='fila'>");
            out.println("      <span class='boton-etiqueta'>Fecha</span>");
            out.println("      <input type='date' id='fecha' name='fecha'>");
            out.println("    </div>");

            out.println("    <div class='fila fila-moneda'>");
            out.println("      <span class='grupo-titulo'>Moneda</span>");
            out.println("      <label class='radio'><input type='radio' name='moneda' value='soles' checked> Soles (S/.)</label>");
            out.println("      <label class='radio'><input type='radio' name='moneda' value='dolares'> Dolares (US$)</label>");
            out.println("    </div>");

            out.println("    <div class='fila fila-triple'>");
            out.println("      <span><label for='monto'>Monto</label><input type='number' id='monto' name='monto' step='0.01' min='0'></span>");
            out.println("      <span><label for='periodo'>Periodo</label><input type='number' id='periodo' name='periodo' min='1' class='corto'> meses</span>");
            out.println("    </div>");

            out.println("    <div class='fila'>");
            out.println("      <button type='button' class='boton' onclick='calcularCuota()'>Cuota</button>");
            out.println("      <input type='text' id='cuota' name='cuota' readonly placeholder='se calcula al enviar'>");
            out.println("    </div>");

            out.println("    <div class='fila fila-triple'>");
            out.println("      <span><span class='boton-etiqueta'>TEA</span><input type='number' id='tea' name='tea' step='0.01' min='0' class='corto'> %</span>");
            out.println("      <span><button type='button' class='boton' onclick='calcularVencimiento()'>Fecha vencimiento</button><input type='text' id='fechaVencimiento' name='fechaVencimiento' readonly placeholder='se calcula al enviar'></span>");
            out.println("    </div>");

            out.println("    <div class='fila fila-aceptar'>");
            out.println("      <button type='submit' class='boton boton-aceptar'>ACEPTAR</button>");
            out.println("    </div>");

            out.println("  </div>");
            out.println("</form>");

            escribeScript(out);
            out.println("</body>");
            out.println("</html>");
        } finally {
            out.close();
        }
    }

    /**
     * Escribe el bloque <script> del formulario: cambio de pestanas y una vista
     * previa de la cuota y de la fecha de vencimiento. Va embebido (no como
     * archivo .js aparte) para que el servlet quede autocontenido.
     */
    private static void escribeScript(PrintWriter out) {
        out.println("<script>");
        out.println("function mostrarPestana(idPanel, boton) {");
        out.println("  var paneles = document.querySelectorAll('.panel');");
        out.println("  for (var i = 0; i < paneles.length; i++) paneles[i].classList.remove('activo');");
        out.println("  var tabs = document.querySelectorAll('.tab');");
        out.println("  for (var j = 0; j < tabs.length; j++) tabs[j].classList.remove('activo');");
        out.println("  document.getElementById(idPanel).classList.add('activo');");
        out.println("  boton.classList.add('activo');");
        out.println("}");
        out.println("function tasaMensualDesdeTEA(tea) {");
        out.println("  return Math.pow(1 + tea / 100, 1 / 12) - 1;");
        out.println("}");
        out.println("function simboloMoneda() {");
        out.println("  var radios = document.getElementsByName('moneda');");
        out.println("  for (var k = 0; k < radios.length; k++) {");
        out.println("    if (radios[k].checked && radios[k].value === 'dolares') return 'US$';");
        out.println("  }");
        out.println("  return 'S/.';");
        out.println("}");
        out.println("function calcularCuota() {");
        out.println("  var monto = parseFloat(document.getElementById('monto').value);");
        out.println("  var tea = parseFloat(document.getElementById('tea').value);");
        out.println("  var meses = parseInt(document.getElementById('periodo').value, 10);");
        out.println("  var salida = document.getElementById('cuota');");
        out.println("  if (!(monto > 0) || !(meses > 0) || isNaN(tea)) { salida.value = 'Faltan datos'; return; }");
        out.println("  var i = tasaMensualDesdeTEA(tea);");
        out.println("  var cuota = (i === 0) ? monto / meses : monto * i / (1 - Math.pow(1 + i, -meses));");
        out.println("  salida.value = simboloMoneda() + ' ' + cuota.toFixed(2);");
        out.println("}");
        out.println("function calcularVencimiento() {");
        out.println("  var fecha = document.getElementById('fecha').value;");
        out.println("  var meses = parseInt(document.getElementById('periodo').value, 10);");
        out.println("  var salida = document.getElementById('fechaVencimiento');");
        out.println("  if (!fecha || !(meses > 0)) { salida.value = 'Faltan datos'; return; }");
        out.println("  var d = new Date(fecha + 'T00:00:00');");
        out.println("  d.setMonth(d.getMonth() + meses);");
        out.println("  var dia = ('0' + d.getDate()).slice(-2);");
        out.println("  var mes = ('0' + (d.getMonth() + 1)).slice(-2);");
        out.println("  salida.value = dia + '/' + mes + '/' + d.getFullYear();");
        out.println("}");
        out.println("</script>");
    }

    /* ================================================================= */
    /*  SEGUNDA VISTA: los campos capturados + los calculos               */
    /* ================================================================= */
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
        Map<String, String> personales = new LinkedHashMap<String, String>();
        personales.put("Nombres", nombres);
        personales.put("Apellidos", apellidos);
        personales.put("DNI", dni);
        personales.put("Correo", correo);

        Map<String, String> credito = new LinkedHashMap<String, String>();
        credito.put("Fecha", fecha);
        credito.put("Moneda", monedaTexto);
        credito.put("Monto", simbolo + " " + monto);
        credito.put("Periodo", periodo + " meses");
        credito.put("TEA", tea + " %");
        credito.put("Cuota (enviada por el formulario)", cuotaForm);
        credito.put("Fecha de vencimiento (enviada por el formulario)", vencForm);

        Map<String, String> calculos = new LinkedHashMap<String, String>();
        calculos.put("Cuota mensual (sistema frances)", cuotaCalc);
        calculos.put("Fecha de vencimiento", vencCalc);

        PrintWriter out = respuesta.getWriter();
        try {
            out.println("<!DOCTYPE html>");
            out.println("<html lang='es'>");
            out.println("<head>");
            out.println("  <meta charset='UTF-8'>");
            out.println("  <meta name='viewport' content='width=device-width, initial-scale=1'>");
            out.println("  <title>Financiera Huanca - Resumen de la solicitud</title>");
            out.println("  <link rel='stylesheet' href='" + ctx + "/css/estilo.css'>");
            out.println("</head>");
            out.println("<body>");
            out.println("<div class='ventana'>");
            out.println("  <div class='barra-titulo'>");
            out.println("    <span class='titulo'>@ Financiera Huanca</span>");
            out.println("    <span class='cerrar'>&#10005;</span>");
            out.println("  </div>");
            out.println("  <div class='panel activo'>");
            out.println("    <h2 id='titulo-resumen'>Solicitud registrada</h2>");
            out.println("    <p>Estos son los datos que recibio el servlet:</p>");
            out.println(tabla("Datos personales capturados", personales));
            out.println(tabla("Datos del credito capturados", credito));
            out.println(tabla("Calculos realizados por el servlet", calculos));
            out.println("    <div class='fila fila-aceptar'>");
            out.println("      <a class='boton boton-aceptar' href='" + ctx + "/credito'>Nueva solicitud</a>");
            out.println("    </div>");
            out.println("  </div>");
            out.println("</div>");
            out.println("</body>");
            out.println("</html>");
        } finally {
            out.close();
        }
    }

    /* ================================================================= */
    /*  METODOS AUXILIARES                                                */
    /* ================================================================= */

    /** Devuelve el parametro ya recortado, o "(no capturado)" si vino vacio. */
    private static String leer(HttpServletRequest peticion, String nombre) {
        String valor = peticion.getParameter(nombre);
        if (valor == null || valor.trim().isEmpty()) {
            return "(no capturado)";
        }
        return valor.trim();
    }

    /** Construye una tabla HTML a partir de un mapa etiqueta -> valor. */
    private static String tabla(String titulo, Map<String, String> filas) {
        StringBuilder sb = new StringBuilder();
        sb.append("    <table class='resultado'><caption>").append(esc(titulo)).append("</caption>");
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
                    .replace("\"", "&quot;")
                    .replace("'", "&#39;");
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
            LocalDate desembolso = LocalDate.parse(fechaTxt); // <input type='date'> -> yyyy-MM-dd
            int meses = Integer.parseInt(periodoTxt);
            LocalDate vencimiento = desembolso.plusMonths(meses);
            return vencimiento.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
        } catch (Exception e) {
            return "(revisa la fecha y el periodo)";
        }
    }
}
