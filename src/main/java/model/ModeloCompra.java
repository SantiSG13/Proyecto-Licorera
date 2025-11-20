package model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

// Clase que representa una compra/reabastecimiento en el sistema
// Esta clase almacena el historial de compras a proveedores (factura de compra)
public class ModeloCompra {
    private String id;                      // ID único de la compra (CMP-001, CMP-002, etc.)
    private String fecha;                   // Fecha y hora de la compra en formato String
    private List<ItemCompra> items;         // Lista de productos comprados
    private double total;                   // Total neto de la compra

    // Constructor vacío (requerido para serialización JSON)
    public ModeloCompra() {
        this.items = new ArrayList<>();
        this.fecha = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }

    // Constructor con parámetros
    public ModeloCompra(String id, List<ItemCompra> items) {
        this.id = id;
        this.fecha = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        this.items = items != null ? items : new ArrayList<>();
        this.total = calcularTotal();
    }

    // Clase interna para representar un item de la compra
    public static class ItemCompra {
        private String idProducto;          // ID del producto
        private String nombreProducto;      // Nombre del producto
        private String idProveedor;         // ID del proveedor (PRV-###)
        private int cantidad;               // Cantidad comprada
        private double precioUnitario;      // Precio unitario del producto

        public ItemCompra() {}

        public ItemCompra(String idProducto, String nombreProducto, String idProveedor,
                          int cantidad, double precioUnitario) {
            this.idProducto = idProducto;
            this.nombreProducto = nombreProducto;
            this.idProveedor = idProveedor;
            this.cantidad = cantidad;
            this.precioUnitario = precioUnitario;
        }

        // Getters y Setters
        public String getIdProducto() {
            return idProducto;
        }

        public void setIdProducto(String idProducto) {
            this.idProducto = idProducto;
        }

        public String getNombreProducto() {
            return nombreProducto;
        }

        public void setNombreProducto(String nombreProducto) {
            this.nombreProducto = nombreProducto;
        }

        public String getIdProveedor() {
            return idProveedor;
        }

        public void setIdProveedor(String idProveedor) {
            this.idProveedor = idProveedor;
        }

        public int getCantidad() {
            return cantidad;
        }

        public void setCantidad(int cantidad) {
            this.cantidad = cantidad;
        }

        public double getPrecioUnitario() {
            return precioUnitario;
        }

        public void setPrecioUnitario(double precioUnitario) {
            this.precioUnitario = precioUnitario;
        }
    }

    // Metodo para calcular el total de la compra
    private double calcularTotal() {
        return items.stream()
                .mapToDouble(item -> item.getCantidad() * item.getPrecioUnitario())
                .sum();
    }

    // Getters y Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public List<ItemCompra> getItems() {
        return items;
    }

    public void setItems(List<ItemCompra> items) {
        this.items = items;
        this.total = calcularTotal();
    }

    public double getTotal() {
        return total;
    }

    public void setTotal(double total) {
        this.total = total;
    }

    // Metodo para agregar un item a la compra
    public void agregarItem(ItemCompra item) {
        this.items.add(item);
        this.total = calcularTotal();
    }

    // Metodo para eliminar un item de la compra
    public void eliminarItem(ItemCompra item) {
        this.items.remove(item);
        this.total = calcularTotal();
    }
}

