package model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

// Clase que representa una venta en el sistema
public class ModeloVenta {
    private String id;
    private String idCliente;
    private String nombreCliente;
    private LocalDateTime fecha;
    private List<ItemVenta> items;
    private double descuento;
    private double total;

    // Constructor vacío (requerido para serialización JSON)
    public ModeloVenta() {
        this.items = new ArrayList<>();
        this.fecha = LocalDateTime.now();
    }

    // Constructor con parámetros
    public ModeloVenta(String id, String idCliente, String nombreCliente, List<ItemVenta> items, double descuento) {
        this.id = id;
        this.idCliente = idCliente;
        this.nombreCliente = nombreCliente;
        this.fecha = LocalDateTime.now();
        this.items = items != null ? items : new ArrayList<>();
        this.descuento = descuento;
        this.total = calcularTotal();
    }

    // Clase interna para representar un item de la venta
    public static class ItemVenta {
        private String idProducto;
        private String nombreProducto;
        private int cantidad;
        private double precioUnitario;
        private double subtotal;

        public ItemVenta() {}

        public ItemVenta(String idProducto, String nombreProducto, int cantidad, double precioUnitario) {
            this.idProducto = idProducto;
            this.nombreProducto = nombreProducto;
            this.cantidad = cantidad;
            this.precioUnitario = precioUnitario;
            this.subtotal = cantidad * precioUnitario;
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
            this.subtotal = cantidad * precioUnitario;
        }

        public double getPrecioUnitario() {
            return precioUnitario;
        }

        public void setPrecioUnitario(double precioUnitario) {
            this.precioUnitario = precioUnitario;
            this.subtotal = cantidad * precioUnitario;
        }

        public double getSubtotal() {
            return subtotal;
        }

        public void setSubtotal(double subtotal) {
            this.subtotal = subtotal;
        }
    }

    // Metodo para calcular el total de la venta
    private double calcularTotal() {
        double subtotal = items.stream()
                .mapToDouble(ItemVenta::getSubtotal)
                .sum();
        return subtotal - descuento;
    }

    // Metodo para recalcular el total (útil cuando se modifican items o descuento)
    public void recalcularTotal() {
        this.total = calcularTotal();
    }

    // Getters y Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getIdCliente() {
        return idCliente;
    }

    public void setIdCliente(String idCliente) {
        this.idCliente = idCliente;
    }

    public String getNombreCliente() {
        return nombreCliente;
    }

    public void setNombreCliente(String nombreCliente) {
        this.nombreCliente = nombreCliente;
    }

    public LocalDateTime getFecha() {
        return fecha;
    }

    public void setFecha(LocalDateTime fecha) {
        this.fecha = fecha;
    }

    public List<ItemVenta> getItems() {
        return items;
    }

    public void setItems(List<ItemVenta> items) {
        this.items = items;
        recalcularTotal();
    }

    public double getDescuento() {
        return descuento;
    }

    public void setDescuento(double descuento) {
        this.descuento = descuento;
        recalcularTotal();
    }

    public double getTotal() {
        return total;
    }

    public void setTotal(double total) {
        this.total = total;
    }

    @Override
    public String toString() {
        return "ModeloVenta{" +
                "id='" + id + '\'' +
                ", cliente='" + nombreCliente + '\'' +
                ", fecha=" + fecha +
                ", items=" + items.size() +
                ", total=" + total +
                '}';
    }
}

