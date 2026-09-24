package mx.unam.ciencias.tdi.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import org.springframework.format.annotation.DateTimeFormat;

/**
 * Tarea 2 - Tecnologias para Desarrollos en Internet.
 *
 * Model de la arquitectura MVC: una solicitud de credito de Financiera Huanca,
 * con los campos de las dos pestanas del formulario del enunciado.
 *
 * Tambien es el "command object" de Spring MVC: el DispatcherServlet crea uno
 * por peticion y llena sus propiedades con los parametros que manda el
 * formulario (ver CreditoController). Por eso necesita constructor vacio y
 * accesores con nombres iguales a los de los campos del formulario.
 *
 * Solo datos y accesores: los calculos viven en CalculadoraCredito y la
 * persistencia en CreditoDAO.
 *
 * @author Miguel Angel Marquez Cristoval
 */
public class Credito implements Serializable {

    private static final long serialVersionUID = 1L;

    /** Como se imprimen las fechas en las vistas. */
    private static final DateTimeFormatter FORMATO =
            DateTimeFormatter.ofPattern("dd/MM/yyyy");

    /** Llave que asigna MySQL al guardar la solicitud. */
    private int id;

    /* ---- Datos personales ---- */
    private String nombres;
    private String apellidos;
    private String dni;
    private String correo;

    /* ---- Datos del credito ---- */

    /**
     * Fecha de desembolso. La anotacion le dice a Spring como convertir el
     * texto "aaaa-mm-dd" que manda <input type="date"> a un LocalDate (y al
     * reves para escribirlo de vuelta en el formulario).
     */
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate fecha;

    /** "soles" o "dolares" (los dos radio buttons del formulario). */
    private String moneda;

    private BigDecimal monto;

    /** Plazo del credito, en meses. */
    private Integer periodo;

    /** Tasa efectiva anual, en porcentaje. */
    private BigDecimal tea;

    /* ---- Resultados calculados ---- */
    private BigDecimal cuota;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate fechaVencimiento;

    public Credito() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNombres() {
        return nombres;
    }

    public void setNombres(String nombres) {
        this.nombres = nombres;
    }

    public String getApellidos() {
        return apellidos;
    }

    public void setApellidos(String apellidos) {
        this.apellidos = apellidos;
    }

    public String getDni() {
        return dni;
    }

    public void setDni(String dni) {
        this.dni = dni;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public LocalDate getFecha() {
        return fecha;
    }

    public void setFecha(LocalDate fecha) {
        this.fecha = fecha;
    }

    public String getMoneda() {
        return moneda;
    }

    public void setMoneda(String moneda) {
        this.moneda = moneda;
    }

    public BigDecimal getMonto() {
        return monto;
    }

    public void setMonto(BigDecimal monto) {
        this.monto = monto;
    }

    public Integer getPeriodo() {
        return periodo;
    }

    public void setPeriodo(Integer periodo) {
        this.periodo = periodo;
    }

    public BigDecimal getTea() {
        return tea;
    }

    public void setTea(BigDecimal tea) {
        this.tea = tea;
    }

    public BigDecimal getCuota() {
        return cuota;
    }

    public void setCuota(BigDecimal cuota) {
        this.cuota = cuota;
    }

    public LocalDate getFechaVencimiento() {
        return fechaVencimiento;
    }

    public void setFechaVencimiento(LocalDate fechaVencimiento) {
        this.fechaVencimiento = fechaVencimiento;
    }

    /* ================================================================= */
    /*  Accesores de conveniencia para las vistas (JSP)                   */
    /* ================================================================= */

    /** Simbolo de la moneda elegida: "US$" o "S/.". */
    public String getSimbolo() {
        return esDolares() ? "US$" : "S/.";
    }

    /** Nombre completo de la moneda, como aparece en el formulario. */
    public String getMonedaTexto() {
        return esDolares() ? "Dolares (US$)" : "Soles (S/.)";
    }

    /** La fecha de desembolso como dd/MM/aaaa, para imprimirla en las vistas. */
    public String getFechaTexto() {
        return fecha == null ? "" : fecha.format(FORMATO);
    }

    /** La fecha de vencimiento como dd/MM/aaaa. */
    public String getFechaVencimientoTexto() {
        return fechaVencimiento == null ? "" : fechaVencimiento.format(FORMATO);
    }

    private boolean esDolares() {
        return "dolares".equals(moneda);
    }
}
