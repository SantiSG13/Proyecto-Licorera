package model;

// Modelo que representa a un Producto del sistema.
// Contiene los datos que se almacenarán en tbInventario.json
public class ModeloInventario {
    private String id; // ID único del producto (ej: PROD-001)
    private String nombre; // Nombre del producto
    private String categoria; // Categoría del producto (Licores, Vinos, Cervezas, etc.)
    private double costo; // Costo de compra del producto
    private double precioVenta; // Precio de venta (50% más que el costo)
    private int stock; // Cantidad en inventario

    public ModeloInventario() {
    }

    public ModeloInventario(String id, String nombre, String categoria, double costo, int stock) {
        this.id = id;
        this.nombre = nombre;
        this.categoria = categoria;
        this.costo = costo;
        this.precioVenta = costo * 1.5; // 50% de ganancia
        this.stock = stock;
    }

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

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    public double getCosto() {
        return costo;
    }

    public void setCosto(double costo) {
        this.costo = costo;
        this.precioVenta = costo * 1.5; // Actualizar precio de venta automáticamente
    }

    public double getPrecioVenta() {
        return precioVenta;
    }

    public void setPrecioVenta(double precioVenta) {
        this.precioVenta = precioVenta;
    }

    // Método legacy para compatibilidad (retorna precio de venta)
    public double getPrecio() {
        return precioVenta;
    }

    public void setPrecio(double precio) {
        this.precioVenta = precio;
    }

    public int getStock() {
        return stock;
    }

    public void setStock(int stock) {
        this.stock = stock;
    }
}
