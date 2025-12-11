package model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

// Clase que representa una venta en el sistema
public class ModeloVenta {
    private String id; // ID único de la venta (VEN-001, VEN-002, etc.)
    private String documentoCliente; // Documento del cliente
    private String nombreCliente; // Nombre completo del cliente
    private String fecha; // Fecha y hora de la venta en formato String
    private List<ItemVenta> items; // Lista de productos vendidos
    private double total; // Total de la venta

    // Constructor vacío (requerido para serialización JSON)
    public ModeloVenta() {
        this.items = new ArrayList<>();
        this.fecha = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }

    // Constructor con parámetros
    public ModeloVenta(String id, String documentoCliente, String nombreCliente, List<ItemVenta> items) {
        this.id = id;
        this.documentoCliente = documentoCliente;
        this.nombreCliente = nombreCliente;
        this.fecha = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        this.items = items != null ? items : new ArrayList<>();
        this.total = calcularTotal();
    }

    // Clase interna para representar un item de la venta
    public static class ItemVenta {
        private String idProducto; // ID del producto
        private String nombreProducto; // Nombre del producto
        private int cantidad; // Cantidad vendida
        private double precioUnitario; // Precio de venta unitario

        public ItemVenta() {
        }

        public ItemVenta(String idProducto, String nombreProducto, int cantidad, double precioUnitario) {
            this.idProducto = idProducto;
            this.nombreProducto = nombreProducto;
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

    // Metodo para calcular el total de la venta
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

    public String getDocumentoCliente() {
        return documentoCliente;
    }

    public void setDocumentoCliente(String documentoCliente) {
        this.documentoCliente = documentoCliente;
    }

    public String getNombreCliente() {
        return nombreCliente;
    }

    public void setNombreCliente(String nombreCliente) {
        this.nombreCliente = nombreCliente;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public List<ItemVenta> getItems() {
        return items;
    }

    public void setItems(List<ItemVenta> items) {
        this.items = items;
        this.total = calcularTotal();
    }

    public double getTotal() {
        return total;
    }

    public void setTotal(double total) {
        this.total = total;
    }

    // Metodo para agregar un item a la venta
    public void agregarItem(ItemVenta item) {
        this.items.add(item);
        this.total = calcularTotal();
    }

    // Metodo para eliminar un item de la venta
    public void eliminarItem(ItemVenta item) {
        this.items.remove(item);
        this.total = calcularTotal();
    }
}
