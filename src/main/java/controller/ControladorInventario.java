package controller;

import files.ManejoJson;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import model.ModeloInventario;
import view.frmInventario;

import java.lang.reflect.Type;
import java.util.List;

// Controlador para la vista de gestión de productos (frmProductos).
// Se encarga de manejar eventos, validaciones y persistencia de datos en tbInventario.json
public class ControladorInventario {

    private final frmInventario vista;
    private final ObservableList<String[]> datosTabla = FXCollections.observableArrayList();
    private final String RUTA_ARCHIVO = "src/main/java/model/tbInventario.json";

    public ControladorInventario(frmInventario vista) {
        this.vista = vista;
        configurarTabla();
        cargarProductosDesdeArchivo();
        wireActions();
    }

    private void configurarTabla() {
        vista.getTablaProductos().setItems(datosTabla);
        vista.getTablaProductos().setPlaceholder(new javafx.scene.control.Label("No hay productos registrados"));
    }

    private void wireActions() {
        vista.getBtnGuardar().setOnAction(e -> guardarProducto());
        // El botón Salir ya cierra la ventana desde la vista
    }

    private void guardarProducto() {
        String nombre = vista.getTxtNombre().getText().trim();
        String categoria = vista.getCboCategoria().getValue();
        String precioStr = vista.getTxtPrecio().getText().trim();
        String stockStr = vista.getTxtStock().getText().trim();

        // Validaciones
        if (nombre.isEmpty()) {
            mostrarAlerta("Validación", "El nombre del producto es obligatorio.");
            return;
        }

        if (categoria == null || categoria.isEmpty()) {
            mostrarAlerta("Validación", "Debe seleccionar una categoría.");
            return;
        }

        if (precioStr.isEmpty()) {
            mostrarAlerta("Validación", "El precio es obligatorio.");
            return;
        }

        double precio;
        try {
            precio = Double.parseDouble(precioStr);
            if (precio < 0) {
                mostrarAlerta("Validación", "El precio no puede ser negativo.");
                return;
            }
        } catch (NumberFormatException e) {
            mostrarAlerta("Validación", "El precio debe ser un número válido.");
            return;
        }

        if (stockStr.isEmpty()) {
            mostrarAlerta("Validación", "El stock es obligatorio.");
            return;
        }

        int stock;
        try {
            stock = Integer.parseInt(stockStr);
            if (stock < 0) {
                mostrarAlerta("Validación", "El stock no puede ser negativo.");
                return;
            }
        } catch (NumberFormatException e) {
            mostrarAlerta("Validación", "El stock debe ser un número entero válido.");
            return;
        }

        // Leer lista actual del archivo
        Type tipoLista = ManejoJson.obtenerTipoLista(ModeloInventario.class);
        List<ModeloInventario> listaProductos = ManejoJson.leerJson(RUTA_ARCHIVO, tipoLista);

        // Verificar nombre único
        boolean existe = listaProductos.stream()
                .anyMatch(p -> p.getNombre().equalsIgnoreCase(nombre));
        if (existe) {
            mostrarAlerta("Validación", "Ya existe un producto con ese nombre.");
            return;
        }

        // Crear y agregar nuevo producto con ID único
        String idProducto = generarIdProducto(listaProductos.size());
        ModeloInventario producto = new ModeloInventario(idProducto, nombre, categoria, precio, stock);
        listaProductos.add(producto);
        ManejoJson.escribirJson(RUTA_ARCHIVO, listaProductos);

        // Actualizar tabla
        datosTabla.add(new String[] {
                idProducto,
                nombre,
                categoria,
                String.format("$%.2f", precio), // Costo
                String.format("$%.2f", precio * 1.5), // Precio de venta (50% más)
                String.valueOf(stock)
        });

        limpiarFormulario();
        mostrarAlerta("Éxito", "Producto guardado correctamente. ID: " + idProducto);
    }

    // Genera un ID de producto con formato PROD-001, PROD-002, etc.
    private String generarIdProducto(int cantidadActual) {
        int nuevoNumero = cantidadActual + 1;
        return String.format("PROD-%03d", nuevoNumero);
    }

    private void cargarProductosDesdeArchivo() {
        Type tipoLista = ManejoJson.obtenerTipoLista(ModeloInventario.class);
        List<ModeloInventario> listaProductos = ManejoJson.leerJson(RUTA_ARCHIVO, tipoLista);

        datosTabla.clear();
        for (ModeloInventario p : listaProductos) {
            datosTabla.add(new String[] {
                    p.getId() != null ? p.getId() : "-",
                    p.getNombre(),
                    p.getCategoria(),
                    String.format("$%.2f", p.getCosto()), // Costo de compra
                    String.format("$%.2f", p.getPrecioVenta()), // Precio de venta (50% más)
                    String.valueOf(p.getStock())
            });
        }
    }

    private void limpiarFormulario() {
        vista.getTxtNombre().clear();
        vista.getCboCategoria().setValue(null);
        vista.getTxtPrecio().clear();
        vista.getTxtStock().clear();
    }

    private void mostrarAlerta(String titulo, String mensaje) {
        javafx.scene.control.Alert alert = new javafx.scene.control.Alert(
                javafx.scene.control.Alert.AlertType.INFORMATION);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}
