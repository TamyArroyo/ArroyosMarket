package com.example.proyecto;

import androidx.annotation.NonNull;

public class Modelo {
    private int id;
    private String nombre;
    private double precio; // Nuevo campo de precio

    // Constructor vacío para Firebase
    public Modelo() {
    }

    // Constructor con id, nombre y precio
    public Modelo(int id, String nombre, double precio) {
        this.id = id;
        this.nombre = nombre;
        this.precio = precio;
    }

    // Getters y Setters
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

    public double getPrecio() {
        return precio;
    }

    public void setPrecio(double precio) {
        this.precio = precio;
    }

    @NonNull
    @Override
    public String toString() {
        // Modificado para incluir el precio en la representación en texto
        return "ID: " + id + ", Nombre: " + nombre + ", Precio: " + precio;
    }
}
