package controller;

import com.google.gson.reflect.TypeToken;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import model.ModeloCliente;
import model.ModeloInventario;
import model.ModeloVenta;
import model.ModeloVenta.ItemVenta;
import view.frmVenta;
import files.ManejoJson;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class ControladorVenta {
    private final frmVenta vista;
    private List<ModeloCliente> clientes;
    private List<ModeloInventario> inventario;
    private ObservableList<ItemVenta> itemsCarrito;
    private SimpleDoubleProperty total;

    private static final String RUTA_CLIENTES = "src/main/java/model/tbCliente.json";
    private static final String RUTA_INVENTARIO = "src/main/java/model/tbInventario.json";
    private static final String RUTA_VENTAS = "src/main/java/model/tbVenta.json";

    public ControladorVenta(frmVenta vista) {
        this.vista = vista;
        this.itemsCarrito = FXCollections.observableArrayList();
        this.total = new SimpleDoubleProperty(0.0);

        inicializar();
        configurarEventos();
    }

    private void inicializar() {
        cargarClientes();
        cargarInventario();

        // Configurar tabla con la lista observable
        vista.getTablaCarrito().setItems(itemsCarrito);

        // Configurar callback para actualizar totales cuando cambien las cantidades
        vista.setOnCantidadChanged(this::actualizarTotales);

        // Listener para actualizar totales cuando cambie la tabla
        itemsCarrito.addListener((javafx.collections.ListChangeListener<? super ItemVenta>) c -> {
            actualizarTotales();
        });
    }

    private void cargarClientes() {
        try {
            Type tipoLista = new TypeToken<List<ModeloCliente>>() {
            }.getType();
            clientes = ManejoJson.leerJson(RUTA_CLIENTES, tipoLista);

            if (clientes == null) {
                clientes = new ArrayList<>();
            }

            // Poblar el ComboBox con formato: Nombre completo
            ObservableList<String> clientesDisponibles = FXCollections.observableArrayList();
            for (ModeloCliente cliente : clientes) {
                String display = cliente.getNombreCompleto();
                clientesDisponibles.add(display);
            }

            vista.getCmbClientes().setItems(clientesDisponibles);

            // Personalizar la visualización del ComboBox
            vista.getCmbClientes().setCellFactory(lv -> new ListCell<String>() {
                @Override
                protected void updateItem(String item, boolean empty) {
                    super.updateItem(item, empty);
                    setText(empty ? null : item);
                }
            });

        } catch (Exception e) {
            mostrarAlerta(Alert.AlertType.ERROR, "Error",
                    "No se pudieron cargar los clientes: " + e.getMessage());
            clientes = new ArrayList<>();
        }
    }

    private void cargarInventario() {
        try {
            Type tipoLista = new TypeToken<List<ModeloInventario>>() {
            }.getType();
            inventario = ManejoJson.leerJson(RUTA_INVENTARIO, tipoLista);

            if (inventario == null) {
                inventario = new ArrayList<>();
            }

            // Poblar el ComboBox con formato: Nombre | Precio de venta
            ObservableList<String> productosDisponibles = FXCollections.observableArrayList();
            for (ModeloInventario producto : inventario) {
                String display = String.format("%s | $%,.2f",
                        producto.getNombre(),
                        producto.getPrecioVenta());
                productosDisponibles.add(display);
            }

            vista.getCmbProductos().setItems(productosDisponibles);

            // Personalizar la visualización del ComboBox
            vista.getCmbProductos().setCellFactory(lv -> new ListCell<String>() {
                @Override
                protected void updateItem(String item, boolean empty) {
                    super.updateItem(item, empty);
                    setText(empty ? null : item);
                }
            });

        } catch (Exception e) {
            mostrarAlerta(Alert.AlertType.ERROR, "Error",
                    "No se pudieron cargar los productos: " + e.getMessage());
            inventario = new ArrayList<>();
        }
    }

    private void configurarEventos() {
        // Evento: selección de producto
        vista.getCmbProductos().valueProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null && !newVal.isEmpty()) {
                agregarProductoAlCarrito(newVal);
            }
        });

        // Evento: completar venta
        vista.getBtnCompletarVenta().setOnAction(e -> completarVenta());

        // Evento: cambio de selección de cliente
        vista.getCmbClientes().valueProperty().addListener((obs, oldVal, newVal) -> {
            verificarFormularioCompleto();
        });

        // Evento: cerrar ventana
        vista.getBtnCerrar().setOnAction(e -> vista.close());
    }

    private void agregarProductoAlCarrito(String seleccion) {
        try {
            // Parsear la selección para extraer el nombre del producto
            // Formato: "Aguardiente Antioqueño Sin Azúcar 750ml | $42,000.00"
            String[] partes = seleccion.split("\\|");
            if (partes.length < 2)
                return;

            String nombreProducto = partes[0].trim();

            // Buscar el producto en el inventario
            ModeloInventario productoSeleccionado = inventario.stream()
                    .filter(p -> p.getNombre().equals(nombreProducto))
                    .findFirst()
                    .orElse(null);

            if (productoSeleccionado == null)
                return;

            // Verificar stock disponible
            if (productoSeleccionado.getStock() <= 0) {
                mostrarAlerta(Alert.AlertType.WARNING, "Stock insuficiente",
                        "El producto '" + productoSeleccionado.getNombre() + "' no tiene stock disponible.");
                return;
            }

            // Verificar si el producto ya está en el carrito
            boolean existe = false;
            for (ItemVenta item : itemsCarrito) {
                if (item.getNombreProducto().equals(productoSeleccionado.getNombre())) {
                    // Verificar que no se exceda el stock
                    if (item.getCantidad() >= productoSeleccionado.getStock()) {
                        mostrarAlerta(Alert.AlertType.WARNING, "Stock insuficiente",
                                "No hay más stock disponible del producto '" + productoSeleccionado.getNombre()
                                        + "'.\nStock disponible: " + productoSeleccionado.getStock());
                        return;
                    }
                    // Si ya existe, incrementar cantidad
                    item.setCantidad(item.getCantidad() + 1);
                    existe = true;
                    break;
                }
            }

            if (!existe) {
                // Si no existe, agregar nuevo item (usando precio de VENTA, no costo)
                String idProducto = productoSeleccionado.getId() != null ? productoSeleccionado.getId()
                        : productoSeleccionado.getNombre();
                ItemVenta nuevoItem = new ItemVenta(
                        idProducto, // ID del producto
                        productoSeleccionado.getNombre(),
                        1,
                        productoSeleccionado.getPrecioVenta() // Usar precio de venta
                );
                itemsCarrito.add(nuevoItem);
            }

            vista.getTablaCarrito().refresh();
            actualizarTotales();

            // Limpiar selección del combo
            vista.getCmbProductos().setValue(null);

        } catch (Exception e) {
            mostrarAlerta(Alert.AlertType.ERROR, "Error",
                    "No se pudo agregar el producto: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void actualizarTotales() {
        // Calcular total
        double totalFinal = itemsCarrito.stream()
                .mapToDouble(item -> item.getCantidad() * item.getPrecioUnitario())
                .sum();

        total.set(totalFinal);

        // Actualizar labels
        vista.getLblTotal().setText(String.format("$%,.2f", total.get()));

        // Actualizar contador de items
        actualizarContadorItems();

        verificarFormularioCompleto();
    }

    private void actualizarContadorItems() {
        int totalItems = itemsCarrito.stream()
                .mapToInt(ItemVenta::getCantidad)
                .sum();

        // Buscar el label del contador
        try {
            Label lblItemCount = (Label) vista.getScene().lookup("#item-count-label");
            if (lblItemCount != null) {
                String texto = totalItems == 1 ? "1 item" : totalItems + " items";
                lblItemCount.setText(texto);

                // Animación de color si hay items
                if (totalItems > 0) {
                    lblItemCount.setStyle(
                            "-fx-text-fill: #d4af37; -fx-font-size: 12px; -fx-font-weight: bold; -fx-background-color: rgba(212, 175, 55, 0.2); -fx-padding: 4 8 4 8; -fx-background-radius: 10;");
                } else {
                    lblItemCount.setStyle(
                            "-fx-text-fill: #808080; -fx-font-size: 12px; -fx-font-weight: bold; -fx-background-color: rgba(128, 128, 128, 0.1); -fx-padding: 4 8 4 8; -fx-background-radius: 10;");
                }
            }
        } catch (Exception ignored) {
        }
    }

    private void verificarFormularioCompleto() {
        // Habilitar botón solo si hay cliente y productos
        boolean clienteSeleccionado = vista.getCmbClientes().getValue() != null;
        boolean hayProductos = !itemsCarrito.isEmpty();
        boolean formularioCompleto = clienteSeleccionado && hayProductos;

        vista.getBtnCompletarVenta().setDisable(!formularioCompleto);
    }

    private void completarVenta() {
        try {
            if (itemsCarrito.isEmpty()) {
                mostrarAlerta(Alert.AlertType.WARNING, "Advertencia", "Debe agregar productos al carrito.");
                return;
            }

            // Obtener el cliente seleccionado
            String nombreClienteSeleccionado = vista.getCmbClientes().getValue();
            ModeloCliente clienteSeleccionado = clientes.stream()
                    .filter(c -> c.getNombreCompleto().equals(nombreClienteSeleccionado))
                    .findFirst()
                    .orElse(null);

            if (clienteSeleccionado == null) {
                mostrarAlerta(Alert.AlertType.ERROR, "Error", "Debe seleccionar un cliente.");
                return;
            }

            // Verificar stock disponible para todos los productos
            for (ItemVenta item : itemsCarrito) {
                ModeloInventario producto = inventario.stream()
                        .filter(p -> p.getNombre().equals(item.getNombreProducto()))
                        .findFirst()
                        .orElse(null);

                if (producto == null) {
                    mostrarAlerta(Alert.AlertType.ERROR, "Error",
                            "Producto no encontrado: " + item.getNombreProducto());
                    return;
                }

                if (producto.getStock() < item.getCantidad()) {
                    mostrarAlerta(Alert.AlertType.WARNING, "Stock insuficiente",
                            "No hay suficiente stock del producto: " + producto.getNombre() +
                                    "\nStock disponible: " + producto.getStock() +
                                    "\nCantidad requerida: " + item.getCantidad());
                    return;
                }
            }

            // Crear la venta con ID formato VEN-001, VEN-002, etc.
            Type tipoListaVentas = ManejoJson.obtenerTipoLista(ModeloVenta.class);
            List<ModeloVenta> ventasExistentes = ManejoJson.leerJson(RUTA_VENTAS, tipoListaVentas);
            if (ventasExistentes == null) {
                ventasExistentes = new ArrayList<>();
            }
            String idVenta = generarIdVenta(ventasExistentes.size());
            List<ItemVenta> items = new ArrayList<>(itemsCarrito);

            ModeloVenta nuevaVenta = new ModeloVenta(
                    idVenta,
                    clienteSeleccionado.getDocumento(),
                    clienteSeleccionado.getNombreCompleto(),
                    items);

            // Actualizar stock de productos (DESCONTAR)
            for (ItemVenta item : items) {
                ModeloInventario producto = inventario.stream()
                        .filter(p -> p.getNombre().equals(item.getNombreProducto()))
                        .findFirst()
                        .orElse(null);

                if (producto != null) {
                    int nuevoStock = producto.getStock() - item.getCantidad();
                    producto.setStock(nuevoStock);

                    System.out.println("✓ Stock actualizado: " + producto.getNombre() +
                            " | Nuevo stock: " + nuevoStock);
                }
            }

            // Guardar inventario actualizado
            ManejoJson.escribirJson(RUTA_INVENTARIO, inventario);

            // Guardar la venta
            Type tipoLista = new TypeToken<List<ModeloVenta>>() {
            }.getType();
            List<ModeloVenta> ventas = ManejoJson.leerJson(RUTA_VENTAS, tipoLista);
            if (ventas == null) {
                ventas = new ArrayList<>();
            }
            ventas.add(nuevaVenta);
            ManejoJson.escribirJson(RUTA_VENTAS, ventas);

            mostrarAlerta(Alert.AlertType.INFORMATION, "Éxito",
                    "Venta completada exitosamente.\n" +
                            "ID Venta: " + idVenta + "\n" +
                            "Cliente: " + clienteSeleccionado.getNombreCompleto() + "\n" +
                            "Total: $" + String.format("%,.2f", total.get()));

            // Limpiar formulario
            limpiarFormulario();

            // Recargar inventario para reflejar cambios
            cargarInventario();

        } catch (Exception e) {
            mostrarAlerta(Alert.AlertType.ERROR, "Error",
                    "No se pudo completar la venta: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void limpiarFormulario() {
        vista.getCmbClientes().setValue(null);
        vista.getCmbProductos().setValue(null);
        itemsCarrito.clear();
        actualizarTotales();
    }

    // Genera un ID de venta con formato VEN-001, VEN-002, etc.
    private String generarIdVenta(int cantidadActual) {
        int nuevoNumero = cantidadActual + 1;
        return String.format("VEN-%03d", nuevoNumero);
    }

    private void mostrarAlerta(Alert.AlertType tipo, String titulo, String mensaje) {
        Alert alerta = new Alert(tipo);
        alerta.setTitle(titulo);
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);
        alerta.showAndWait();
    }
}
