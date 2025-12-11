package model;

import java.util.ArrayList;
import java.util.List;

// Modelo que representa un Proveedor del sistema.
// Contiene los datos del proveedor y su lista de productos
public class ModeloProveedor {
    private String id;              // ID único del proveedor (PRV-001, PRV-002, etc.)
    private String nombre;          // Nombre del proveedor
    private List<ProductoProveedor> productos; // Lista de productos que ofrece el proveedor

    // Clase interna para representar un producto del proveedor
    public static class ProductoProveedor {
        private String id;          // ID del producto (PROD-001, PROD-002, etc.)
        private String nombre;      // Nombre del producto
        private String tipo;        // Tipo/Categoría del producto
        private double precio;      // Precio del producto

        public ProductoProveedor() {
            // Constructor vacío para Gson
        }

        public ProductoProveedor(String id, String nombre, String tipo, double precio) {
            this.id = id;
            this.nombre = nombre;
            this.tipo = tipo;
            this.precio = precio;
        }

        // Getters y Setters
        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getNombre() {
            return nombre;
        }

        public void setNombre(String nombre) {
            this.nombre = nombre;
        }

        public String getTipo() {
            return tipo;
        }

        public void setTipo(String tipo) {
            this.tipo = tipo;
        }

        public double getPrecio() {
            return precio;
        }

        public void setPrecio(double precio) {
            this.precio = precio;
        }

        @Override
        public String toString() {
            return String.format("%s - %s (%s) - $%.2f", id, nombre, tipo, precio);
        }
    }

    // Constructores
    public ModeloProveedor() {
        this.productos = new ArrayList<>();
    }

    public ModeloProveedor(String id, String nombre) {
        this.id = id;
        this.nombre = nombre;
        this.productos = new ArrayList<>();
    }

    public ModeloProveedor(String id, String nombre, List<ProductoProveedor> productos) {
        this.id = id;
        this.nombre = nombre;
        this.productos = productos != null ? productos : new ArrayList<>();
    }

    // Getters y Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public List<ProductoProveedor> getProductos() {
        return productos;
    }

    public void setProductos(List<ProductoProveedor> productos) {
        this.productos = productos;
    }

    // Métodos auxiliares para gestionar productos
    public void agregarProducto(ProductoProveedor producto) {
        if (this.productos == null) {
            this.productos = new ArrayList<>();
        }
        this.productos.add(producto);
    }

    public void eliminarProducto(String idProducto) {
        if (this.productos != null) {
            this.productos.removeIf(p -> p.getId().equals(idProducto));
        }
    }

    public int getCantidadProductos() {
        return this.productos != null ? this.productos.size() : 0;
    }

    @Override
    public String toString() {
        return String.format("ModeloProveedor{id='%s', nombre='%s', productos=%d}",
                           id, nombre, getCantidadProductos());
    }
}

