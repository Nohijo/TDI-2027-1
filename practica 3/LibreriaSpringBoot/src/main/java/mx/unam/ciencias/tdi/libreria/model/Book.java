package mx.unam.ciencias.tdi.libreria.model;

import java.io.Serializable;
import java.math.BigDecimal;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * Practica 3 - Tecnologias para Desarrollos en Internet.
 *
 * Entidad principal: un libro de la tabla "libros". Las anotaciones de
 * jakarta.persistence documentan el mapeo, como pide el enunciado, pero no hay
 * JPA en el proyecto: quien lee y escribe la tabla es BookRepository con
 * JdbcTemplate. Los mensajes de validacion estan en messages.properties.
 *
 * @author Miguel Angel Marquez Cristoval
 */
@Entity
@Table(name = "libros")
public class Book implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotBlank(message = "{libro.nombre.requerido}")
    @Size(max = 150, message = "{libro.nombre.largo}")
    @Column(nullable = false, length = 150)
    private String nombre;

    @NotBlank(message = "{libro.autor.requerido}")
    @Size(max = 150, message = "{libro.autor.largo}")
    @Column(nullable = false, length = 150)
    private String autor;

    @NotNull(message = "{libro.precio.requerido}")
    @DecimalMin(value = "0.00", message = "{libro.precio.invalido}")
    @Digits(integer = 8, fraction = 2, message = "{libro.precio.invalido}")
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal precio;

    public Book() {
    }

    public Book(Integer id, String nombre, String autor, BigDecimal precio) {
        this.id = id;
        this.nombre = nombre;
        this.autor = autor;
        this.precio = precio;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getAutor() {
        return autor;
    }

    public void setAutor(String autor) {
        this.autor = autor;
    }

    public BigDecimal getPrecio() {
        return precio;
    }

    public void setPrecio(BigDecimal precio) {
        this.precio = precio;
    }
}
