package mx.unam.ciencias.tdi.model;

import java.io.Serializable;

/**
 * Practica 2 - Tecnologias para Desarrollos en Internet.
 *
 * Model de la arquitectura MVC: representa un libro de la libreria.
 * Solo datos y accesores, sin logica de negocio ni de persistencia
 * (eso vive en el DAO) ni de peticiones HTTP (eso vive en el Controller).
 *
 * @author Miguel Angel Marquez Cristoval
 */
public class Book implements Serializable {

    private static final long serialVersionUID = 1L;

    private int id;
    private String nombre;
    private String autor;
    private double precio;

    public Book() {
    }

    public Book(int id, String nombre, String autor, double precio) {
        this.id = id;
        this.nombre = nombre;
        this.autor = autor;
        this.precio = precio;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
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

    public double getPrecio() {
        return precio;
    }

    public void setPrecio(double precio) {
        this.precio = precio;
    }
}
