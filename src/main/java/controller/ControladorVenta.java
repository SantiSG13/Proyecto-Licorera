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
import java.util.stream.Collectors;

public class ControladorVenta {
    private final frmVenta vista;
    private List<ModeloCliente> clientes;
    private List<ModeloInventario> productos;
    private ObservableList<ItemVenta> itemsCarrito;
    private SimpleDoubleProperty totalParcial;
    private SimpleDoubleProperty descuento;
    private SimpleDoubleProperty total;

    private static final String RUTA_CLIENTES = "src/main/java/model/tbCliente.json";
    private static final String RUTA_PRODUCTOS = "src/main/java/model/tbInventario.json";
    private static final String RUTA_VENTAS = "src/main/java/model/tbVenta.json";

    public ControladorVenta(frmVenta vista) {
        this.vista = vista;
        this.itemsCarrito = FXCollections.observableArrayList();
        this.totalParcial = new SimpleDoubleProperty(0.0);
        this.descuento = new SimpleDoubleProperty(0.0);
        this.total = new SimpleDoubleProperty(0.0);

        inicializar();
        configurarEventos();
    }

    private void inicializar() {
        // Cargar clientes y productos desde JSON
        cargarClientes();
        cargarProductos();

        // Configurar tabla con la lista observable
        vista.getTablaCarrito().setItems(itemsCarrito);

        // Configurar bindings para actualización automática de totales
        configurarBindings();

        // Configurar callback para actualizar totales cuando cambien las cantidades
        vista.setOnCantidadChanged(this::actualizarTotales);
    }

    private void cargarClientes() {
        try {
            Type tipoLista = new TypeToken<List<ModeloCliente>>(){}.getType();
            clientes = ManejoJson.leerJson(RUTA_CLIENTES, tipoLista);

            if (clientes == null) {
                clientes = new ArrayList<>();
            }

            // Poblar el ComboBox con los nombres de los clientes
            ObservableList<String> nombresClientes = FXCollections.observableArrayList(
                clientes.stream()
                    .map(ModeloCliente::getNombreCompleto)
                    .collect(Collectors.toList())
            );

            vista.getCmbClientes().setItems(nombresClientes);

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

    private void cargarProductos() {
        try {
            Type tipoLista = new TypeToken<List<ModeloInventario>>(){}.getType();
            productos = ManejoJson.leerJson(RUTA_PRODUCTOS, tipoLista);

            if (productos == null) {
                productos = new ArrayList<>();
            }

            // Poblar el ComboBox con los nombres de los productos
            ObservableList<String> nombresProductos = FXCollections.observableArrayList(
                productos.stream()
                    .map(ModeloInventario::getNombre)
                    .collect(Collectors.toList())
            );

            vista.getCmbProductos().setItems(nombresProductos);

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
            productos = new ArrayList<>();
        }
    }

    private void configurarBindings() {
        // Actualizar total parcial cuando cambie la tabla (agregar o eliminar items)
        itemsCarrito.addListener((javafx.collections.ListChangeListener<? super ItemVenta>) c -> {
            actualizarTotales();
        });

        // Actualizar totales cuando cambie el descuento
        vista.getTxtDescuento().textProperty().addListener((obs, oldVal, newVal) -> {
            try {
                if (newVal.isEmpty()) {
                    descuento.set(0.0);
                } else {
                    double desc = Double.parseDouble(newVal);
                    if (desc < 0) {
                        vista.getTxtDescuento().setText("0");
                        return;
                    }
                    descuento.set(desc);
                }
                actualizarTotales();
            } catch (NumberFormatException e) {
                vista.getTxtDescuento().setText(oldVal);
            }
        });

        // Forzar actualización periódica de la tabla para reflejar cambios en subtotales
        vista.getTablaCarrito().refresh();
    }

    private void configurarEventos() {
        // Evento: selección de producto
        vista.getCmbProductos().valueProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null && !newVal.isEmpty()) {
                // Buscar el producto seleccionado
                ModeloInventario productoSeleccionado = productos.stream()
                    .filter(p -> p.getNombre().equals(newVal))
                    .findFirst()
                    .orElse(null);

                if (productoSeleccionado != null) {
                    agregarProductoAlCarrito(productoSeleccionado);
                    vista.getCmbProductos().setValue(null); // Limpiar selección
                }
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


    private void agregarProductoAlCarrito(ModeloInventario producto) {
        // Verificar stock disponible
        if (producto.getStock() <= 0) {
            mostrarAlerta(Alert.AlertType.WARNING, "Stock insuficiente",
                "El producto '" + producto.getNombre() + "' no tiene stock disponible.");
            return;
        }

        // Verificar si el producto ya está en el carrito
        boolean existe = false;
        for (ItemVenta item : itemsCarrito) {
            if (item.getIdProducto().equals(producto.getNombre())) {
                // Verificar que no se exceda el stock
                if (item.getCantidad() >= producto.getStock()) {
                    mostrarAlerta(Alert.AlertType.WARNING, "Stock insuficiente",
                        "No hay más stock disponible del producto '" + producto.getNombre() + "'.\nStock disponible: " + producto.getStock());
                    return;
                }
                // Si ya existe, incrementar cantidad
                item.setCantidad(item.getCantidad() + 1);
                existe = true;
                break;
            }
        }

        if (!existe) {
            // Si no existe, agregar nuevo item
            ItemVenta nuevoItem = new ItemVenta(
                producto.getNombre(),
                producto.getNombre(),
                1,
                producto.getPrecio()
            );
            itemsCarrito.add(nuevoItem);
        }

        vista.getTablaCarrito().refresh();
        actualizarTotales();
    }

    private void actualizarTotales() {
        // Calcular total parcial
        double parcial = itemsCarrito.stream()
            .mapToDouble(ItemVenta::getSubtotal)
            .sum();

        totalParcial.set(parcial);

        // Calcular total con descuento
        double totalFinal = parcial - descuento.get();
        total.set(Math.max(0, totalFinal)); // Evitar totales negativos

        // Actualizar labels
        vista.getLblTotalParcial().setText(String.format("$%.2f", parcial));
        vista.getLblTotal().setText(String.format("$%.2f", total.get()));

        // Actualizar contador de items
        actualizarContadorItems();

        verificarFormularioCompleto();
    }

    private void actualizarContadorItems() {
        int totalItems = itemsCarrito.stream()
            .mapToInt(ItemVenta::getCantidad)
            .sum();

        // Buscar el label del contador (si existe en la vista)
        try {
            Label lblItemCount = (Label) vista.getScene().lookup("#item-count-label");
            if (lblItemCount != null) {
                String texto = totalItems == 1 ? "1 item" : totalItems + " items";
                lblItemCount.setText(texto);

                // Animación de color si hay items
                if (totalItems > 0) {
                    lblItemCount.setStyle("-fx-text-fill: #4CAF50; -fx-font-size: 12px; -fx-font-weight: bold; -fx-background-color: rgba(76, 175, 80, 0.2); -fx-padding: 4 8 4 8; -fx-background-radius: 10;");
                } else {
                    lblItemCount.setStyle("-fx-text-fill: #808080; -fx-font-size: 12px; -fx-font-weight: bold; -fx-background-color: rgba(128, 128, 128, 0.1); -fx-padding: 4 8 4 8; -fx-background-radius: 10;");
                }
            }
        } catch (Exception ignored) {
            // Si no encuentra el label, continuar sin error
        }
    }

    private void verificarFormularioCompleto() {
        // Habilitar botón de completar venta solo si hay cliente y productos
        boolean clienteSeleccionado = vista.getCmbClientes().getValue() != null;
        boolean hayProductos = !itemsCarrito.isEmpty();
        boolean formularioCompleto = clienteSeleccionado && hayProductos;

        vista.getBtnCompletarVenta().setDisable(!formularioCompleto);

        // Actualizar estilo del botón según el estado
        if (formularioCompleto) {
            vista.getBtnCompletarVenta().setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-font-size: 16px; -fx-font-weight: bold; -fx-cursor: hand; -fx-background-radius: 8;");
        } else {
            vista.getBtnCompletarVenta().setStyle("-fx-background-color: #555555; -fx-text-fill: #999999; -fx-font-size: 16px; -fx-font-weight: bold; -fx-cursor: not-allowed; -fx-background-radius: 8; -fx-opacity: 0.6;");
        }
    }

    private void completarVenta() {
        try {
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

            // Crear la venta con ID formato VTA-001, VTA-002, etc.
            Type tipoListaVentas = ManejoJson.obtenerTipoLista(ModeloVenta.class);
            List<ModeloVenta> ventasExistentes = ManejoJson.leerJson(RUTA_VENTAS, tipoListaVentas);
            String idVenta = generarIdVenta(ventasExistentes.size());
            List<ItemVenta> items = new ArrayList<>(itemsCarrito);

            ModeloVenta nuevaVenta = new ModeloVenta(
                idVenta,
                clienteSeleccionado.getDocumento(),
                clienteSeleccionado.getNombreCompleto(),
                items,
                descuento.get()
            );

            // Actualizar stock de productos
            for (ItemVenta item : items) {
                ModeloInventario producto = productos.stream()
                    .filter(p -> p.getNombre().equals(item.getIdProducto()))
                    .findFirst()
                    .orElse(null);

                if (producto != null) {
                    int nuevoStock = producto.getStock() - item.getCantidad();
                    if (nuevoStock < 0) {
                        mostrarAlerta(Alert.AlertType.WARNING, "Advertencia",
                            "No hay suficiente stock del producto: " + producto.getNombre());
                        return;
                    }
                    producto.setStock(nuevoStock);
                }
            }

            // Guardar productos actualizados
            ManejoJson.escribirJson(RUTA_PRODUCTOS, productos);

            // Guardar la venta
            Type tipoLista = new TypeToken<List<ModeloVenta>>(){}.getType();
            List<ModeloVenta> ventas = ManejoJson.leerJson(RUTA_VENTAS, tipoLista);
            if (ventas == null) {
                ventas = new ArrayList<>();
            }
            ventas.add(nuevaVenta);
            ManejoJson.escribirJson(RUTA_VENTAS, ventas);

            mostrarAlerta(Alert.AlertType.INFORMATION, "Éxito",
                "Venta completada exitosamente.\nTotal: $" + String.format("%.2f", total.get()));

            // Limpiar formulario
            limpiarFormulario();

        } catch (Exception e) {
            mostrarAlerta(Alert.AlertType.ERROR, "Error",
                "No se pudo completar la venta: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void limpiarFormulario() {
        vista.getCmbClientes().setValue(null);
        vista.getCmbProductos().setValue(null);
        vista.getTxtDescuento().setText("0");
        itemsCarrito.clear();
        actualizarTotales();
    }

    // Genera un ID de venta con formato VTA-001, VTA-002, etc.
    private String generarIdVenta(int cantidadActual) {
        int nuevoNumero = cantidadActual + 1;
        return String.format("VTA-%03d", nuevoNumero);
    }

    private void mostrarAlerta(Alert.AlertType tipo, String titulo, String mensaje) {
        Alert alerta = new Alert(tipo);
        alerta.setTitle(titulo);
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);
        alerta.showAndWait();
    }
}

