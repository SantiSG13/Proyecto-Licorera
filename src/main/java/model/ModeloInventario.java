package model;

// Modelo que representa a un Producto del sistema.
// Contiene los datos que se almacenarán en tbInventario.json
public class ModeloInventario {
    private String nombre;      // Nombre del producto
    private String categoria;   // Categoría del producto (Licores, Vinos, Cervezas, etc.)
    private double precio;      // Precio del producto
    private int stock;          // Cantidad en inventario

    public ModeloInventario() {
    }

    public ModeloInventario(String nombre, String categoria, double precio, int stock) {
        this.nombre = nombre;
        this.categoria = categoria;
        this.precio = precio;
        this.stock = stock;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    public double getPrecio() {
        return precio;
    }

    public void setPrecio(double precio) {
        this.precio = precio;
    }

    public int getStock() {
        return stock;
    }

    public void setStock(int stock) {
        this.stock = stock;
    }
}

