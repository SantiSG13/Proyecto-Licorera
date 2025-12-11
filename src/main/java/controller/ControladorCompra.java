package controller;

import com.google.gson.reflect.TypeToken;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import model.ModeloInventario;
import model.ModeloCompra;
import model.ModeloCompra.ItemCompra;
import model.ModeloProveedor;
import model.ModeloProveedor.ProductoProveedor;
import view.frmCompra;
import files.ManejoJson;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import files.ConfigRutas;

public class ControladorCompra {
    private final frmCompra vista;
    private List<ModeloProveedor> proveedores;
    private List<ProductoProveedor> productosProveedores; // Lista de todos los productos de todos los proveedores
    private List<ModeloInventario> inventario;
    private ObservableList<ItemCompra> itemsCarrito;
    private SimpleDoubleProperty total;

    public ControladorCompra(frmCompra vista) {
        this.vista = vista;
        this.itemsCarrito = FXCollections.observableArrayList();
        this.total = new SimpleDoubleProperty(0.0);
        this.productosProveedores = new ArrayList<>();

        inicializar();
        configurarEventos();
    }

    private void inicializar() {
        // Cargar proveedores y productos desde JSON
        cargarProveedores();
        cargarInventario();

        // Configurar tabla con la lista observable
        vista.getTablaCarrito().setItems(itemsCarrito);

        // Configurar bindings para actualización automática de totales
        configurarBindings();

        // Configurar callback para actualizar totales cuando cambien las cantidades
        vista.setOnCantidadChanged(this::actualizarTotales);
    }

    private void cargarProveedores() {
        try {
            Type tipoLista = new TypeToken<List<ModeloProveedor>>() {
            }.getType();
            proveedores = ManejoJson.leerJson(ConfigRutas.PROVEEDOR, tipoLista);

            if (proveedores == null) {
                proveedores = new ArrayList<>();
            }

            // Recopilar todos los productos de todos los proveedores
            productosProveedores.clear();
            for (ModeloProveedor proveedor : proveedores) {
                if (proveedor.getProductos() != null) {
                    productosProveedores.addAll(proveedor.getProductos());
                }
            }

            // Poblar el ComboBox con los productos (formato: ID_PRODUCTO | NOMBRE_PRODUCTO
            // - TIPO - PRECIO)
            ObservableList<String> productosDisponibles = FXCollections.observableArrayList();

            for (ModeloProveedor proveedor : proveedores) {
                if (proveedor.getProductos() != null) {
                    for (ProductoProveedor producto : proveedor.getProductos()) {
                        String display = String.format("%s | %s - %s ($%.2f)",
                                producto.getId(),
                                producto.getNombre(),
                                producto.getTipo(),
                                producto.getPrecio());
                        productosDisponibles.add(display);
                    }
                }
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
                    "No se pudieron cargar los proveedores: " + e.getMessage());
            proveedores = new ArrayList<>();
        }
    }

    private void cargarInventario() {
        try {
            Type tipoLista = new TypeToken<List<ModeloInventario>>() {
            }.getType();
            inventario = ManejoJson.leerJson(ConfigRutas.INVENTARIO, tipoLista);

            if (inventario == null) {
                inventario = new ArrayList<>();
            }
        } catch (Exception e) {
            mostrarAlerta(Alert.AlertType.ERROR, "Error",
                    "No se pudo cargar el inventario: " + e.getMessage());
            inventario = new ArrayList<>();
        }
    }

    private void configurarBindings() {
        // Actualizar total cuando cambie la tabla (agregar o eliminar items)
        itemsCarrito.addListener((javafx.collections.ListChangeListener<? super ItemCompra>) c -> {
            actualizarTotales();
        });

        // Forzar actualización periódica de la tabla para reflejar cambios en
        // subtotales
        vista.getTablaCarrito().refresh();
    }

    private void configurarEventos() {
        // Evento: selección de producto
        vista.getCmbProductos().valueProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null && !newVal.isEmpty()) {
                agregarProductoAlCarrito(newVal);
                vista.getCmbProductos().setValue(null); // Limpiar selección
            }
        });

        // Evento: completar compra
        vista.getBtnCompletarCompra().setOnAction(e -> completarCompra());

        // Evento: cerrar ventana
        vista.getBtnCerrar().setOnAction(e -> vista.close());
    }

    private void agregarProductoAlCarrito(String seleccion) {
        try {
            // Parsear la selección para extraer ID del producto
            // Formato: "PROD-001 | Aguardiente tapa roja - Aguardiente ($30,000.00)"
            String[] partes = seleccion.split("\\|");
            if (partes.length < 2)
                return;

            String idProducto = partes[0].trim();
            String productoParte = partes[1].trim();

            // Extraer nombre del producto (antes del guion)
            String nombreProducto = productoParte.substring(0, productoParte.indexOf(" - ")).trim();

            // Buscar el producto en todos los proveedores usando el ID
            ProductoProveedor productoSeleccionado = null;
            ModeloProveedor proveedorSeleccionado = null;

            for (ModeloProveedor proveedor : proveedores) {
                if (proveedor.getProductos() != null) {
                    for (ProductoProveedor producto : proveedor.getProductos()) {
                        if (producto.getId().equals(idProducto)) {
                            productoSeleccionado = producto;
                            proveedorSeleccionado = proveedor;
                            break;
                        }
                    }
                    if (productoSeleccionado != null)
                        break;
                }
            }

            if (productoSeleccionado == null || proveedorSeleccionado == null)
                return;

            // Verificar si el producto ya está en el carrito
            boolean existe = false;
            for (ItemCompra item : itemsCarrito) {
                if (item.getIdProducto().equals(productoSeleccionado.getId()) &&
                        item.getIdProveedor().equals(proveedorSeleccionado.getId())) {
                    // Si ya existe, incrementar cantidad
                    item.setCantidad(item.getCantidad() + 1);
                    existe = true;
                    break;
                }
            }

            if (!existe) {
                // Si no existe, agregar nuevo item
                ItemCompra nuevoItem = new ItemCompra(
                        productoSeleccionado.getId(),
                        productoSeleccionado.getNombre(),
                        proveedorSeleccionado.getId(),
                        1,
                        productoSeleccionado.getPrecio());
                itemsCarrito.add(nuevoItem);
            }

            vista.getTablaCarrito().refresh();
            actualizarTotales();

        } catch (Exception e) {
            mostrarAlerta(Alert.AlertType.ERROR, "Error",
                    "Error al agregar producto: " + e.getMessage());
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
        vista.getLblTotal().setText(String.format("$%.2f", total.get()));

        // Actualizar contador de items
        actualizarContadorItems();

        verificarFormularioCompleto();
    }

    private void actualizarContadorItems() {
        int totalItems = itemsCarrito.stream()
                .mapToInt(ItemCompra::getCantidad)
                .sum();

        // Buscar el label del contador (si existe en la vista)
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
            // Si no encuentra el label, continuar sin error
        }
    }

    private void verificarFormularioCompleto() {
        // Habilitar botón de completar compra solo si hay productos
        boolean hayProductos = !itemsCarrito.isEmpty();

        vista.getBtnCompletarCompra().setDisable(!hayProductos);

        // Actualizar estilo del botón según el estado
        if (hayProductos) {
            vista.getBtnCompletarCompra().getStyleClass().remove("compras-btn-completar:disabled");
            vista.getBtnCompletarCompra().getStyleClass().add("compras-btn-completar");
        } else {
            vista.getBtnCompletarCompra().getStyleClass().add("compras-btn-completar");
        }
    }

    private void completarCompra() {
        try {
            if (itemsCarrito.isEmpty()) {
                mostrarAlerta(Alert.AlertType.WARNING, "Advertencia", "Debe agregar productos al carrito.");
                return;
            }

            // Crear la compra con ID formato CMP-001, CMP-002, etc.
            Type tipoListaCompras = ManejoJson.obtenerTipoLista(ModeloCompra.class);
            List<ModeloCompra> comprasExistentes = ManejoJson.leerJson(ConfigRutas.COMPRA, tipoListaCompras);
            if (comprasExistentes == null) {
                comprasExistentes = new ArrayList<>();
            }
            String idCompra = generarIdCompra(comprasExistentes.size());
            List<ItemCompra> items = new ArrayList<>(itemsCarrito);

            ModeloCompra nuevaCompra = new ModeloCompra(idCompra, items);

            // Actualizar inventario (agregar stock)
            for (ItemCompra item : items) {
                // Buscar el producto en el inventario por nombre (identificador único)
                ModeloInventario productoInventario = inventario.stream()
                        .filter(p -> p.getNombre().equalsIgnoreCase(item.getNombreProducto()))
                        .findFirst()
                        .orElse(null);

                if (productoInventario != null) {
                    // Si el producto ya existe en el inventario, aumentar el stock
                    int nuevoStock = productoInventario.getStock() + item.getCantidad();
                    productoInventario.setStock(nuevoStock);

                    System.out.println("✓ Producto actualizado en inventario: " + item.getNombreProducto() +
                            " | Nuevo stock: " + nuevoStock);
                } else {
                    // Verificación adicional: asegurar que el producto NO existe antes de agregarlo
                    boolean yaExiste = inventario.stream()
                            .anyMatch(p -> p.getNombre().equalsIgnoreCase(item.getNombreProducto()));

                    if (yaExiste) {
                        System.out.println("⚠ Advertencia: Producto duplicado detectado, se omite: " +
                                item.getNombreProducto());
                        continue; // Saltar este producto
                    }

                    // Si el producto no existe en el inventario, agregarlo
                    // Buscar el producto en los proveedores para obtener el tipo
                    String tipoProducto = "Licor"; // Valor por defecto
                    for (ModeloProveedor proveedor : proveedores) {
                        if (proveedor.getId().equals(item.getIdProveedor()) && proveedor.getProductos() != null) {
                            ProductoProveedor producto = proveedor.getProductos().stream()
                                    .filter(p -> p.getId().equals(item.getIdProducto()))
                                    .findFirst()
                                    .orElse(null);
                            if (producto != null) {
                                tipoProducto = producto.getTipo();
                                break;
                            }
                        }
                    }

                    // Crear nuevo producto en el inventario
                    // Constructor: (id, nombre, categoria, costo, stock)
                    // El precio de venta se calcula automáticamente (costo * 1.5)
                    ModeloInventario nuevoProducto = new ModeloInventario(
                            item.getIdProducto(), // ID del producto (del proveedor)
                            item.getNombreProducto(), // nombre
                            tipoProducto, // categoria
                            item.getPrecioUnitario(), // costo (precio de compra)
                            item.getCantidad() // stock
                    );
                    inventario.add(nuevoProducto);

                    System.out.println("✓ Nuevo producto agregado al inventario: " + item.getNombreProducto() +
                            " | ID: " + item.getIdProducto() + " | Stock inicial: " + item.getCantidad());
                }
            }

            // Guardar inventario actualizado
            ManejoJson.escribirJson(ConfigRutas.INVENTARIO, inventario);

            // Guardar la compra
            Type tipoLista = new TypeToken<List<ModeloCompra>>() {
            }.getType();
            List<ModeloCompra> compras = ManejoJson.leerJson(ConfigRutas.COMPRA, tipoLista);
            if (compras == null) {
                compras = new ArrayList<>();
            }
            compras.add(nuevaCompra);
            ManejoJson.escribirJson(ConfigRutas.COMPRA, compras);

            mostrarAlerta(Alert.AlertType.INFORMATION, "Éxito",
                    "Compra completada exitosamente.\nTotal: $" + String.format("%.2f", total.get()) +
                            "\n\nEl inventario ha sido actualizado.");

            // Limpiar formulario
            limpiarFormulario();

        } catch (Exception e) {
            mostrarAlerta(Alert.AlertType.ERROR, "Error",
                    "No se pudo completar la compra: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void limpiarFormulario() {
        vista.getCmbProductos().setValue(null);
        itemsCarrito.clear();
        actualizarTotales();

        // Recargar inventario para reflejar los cambios
        cargarInventario();
    }

    // Genera un ID de compra con formato CMP-001, CMP-002, etc.
    private String generarIdCompra(int cantidadActual) {
        int nuevoNumero = cantidadActual + 1;
        return String.format("CMP-%03d", nuevoNumero);
    }

    private void mostrarAlerta(Alert.AlertType tipo, String titulo, String mensaje) {
        Alert alerta = new Alert(tipo);
        alerta.setTitle(titulo);
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);
        alerta.showAndWait();
    }
}
