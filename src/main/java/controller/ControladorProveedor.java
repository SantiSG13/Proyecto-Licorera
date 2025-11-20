package controller;

import files.ManejoJson;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.layout.*;
import model.ModeloProveedor;
import model.ModeloProveedor.ProductoProveedor;
import view.frmProveedor;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

// Controlador para la vista de gestión de proveedores (frmProveedor).
// Se encarga de manejar eventos, validaciones y persistencia de datos en tbProveedor.json
public class ControladorProveedor {

    private final frmProveedor vista;
    private final ObservableList<String[]> datosTablaProveedores = FXCollections.observableArrayList();
    private final String RUTA_ARCHIVO = "src/main/java/model/tbProveedor.json";


    public ControladorProveedor(frmProveedor vista) {
        this.vista = vista;
        configurarTablas();
        cargarProveedoresDesdeArchivo();
        wireActions();
        // Establecer la acción para el botón de gestionar productos
        vista.setAccionGestionProductos(this::abrirVentanaGestionProductos);
    }

    private void configurarTablas() {
        vista.getTablaProveedores().setItems(datosTablaProveedores);
        vista.getTablaProveedores().setPlaceholder(new Label("No hay proveedores registrados"));
    }

    private void wireActions() {
        vista.getBtnGuardarProveedor().setOnAction(e -> guardarProveedor());
        vista.getBtnModificar().setOnAction(e -> abrirFormularioParaModificar());
        vista.getBtnEliminar().setOnAction(e -> eliminarProveedor());
    }

    private void guardarProveedor() {
        if (vista.isModoEdicion()) {
            ejecutarModificacion();
        } else {
            ejecutarGuardado();
        }
    }

    private void ejecutarGuardado() {
        String nombre = vista.getTxtNombre().getText().trim();

        // Validaciones
        if (nombre.isEmpty()) {
            mostrarAdvertencia("Validación", "El nombre del proveedor es obligatorio.");
            return;
        }

        try {
            Type tipoLista = ManejoJson.obtenerTipoLista(ModeloProveedor.class);
            List<ModeloProveedor> listaProveedores = ManejoJson.leerJson(RUTA_ARCHIVO, tipoLista);

            // Generar ID con formato PRV-001, PRV-002, etc.
            String idNuevo = generarIdProveedor(listaProveedores.size());

            // Crear nuevo proveedor sin productos (se agregarán después)
            ModeloProveedor proveedor = new ModeloProveedor(idNuevo, nombre);
            listaProveedores.add(proveedor);
            ManejoJson.escribirJson(RUTA_ARCHIVO, listaProveedores);

            cargarProveedoresDesdeArchivo();
            vista.limpiarFormulario();
            vista.cerrarPanelFormulario();
            mostrarExito("Proveedor guardado correctamente. Ahora puede agregar productos.");

        } catch (Exception e) {
            mostrarError("Error al guardar", "No se pudo guardar el proveedor: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void abrirFormularioParaModificar() {
        String[] seleccionado = vista.getTablaProveedores().getSelectionModel().getSelectedItem();

        if (seleccionado == null) {
            mostrarAdvertencia("Seleccione un proveedor", "Debe seleccionar un proveedor de la tabla para modificar.");
            return;
        }

        // Abrir formulario solo con los datos del proveedor
        vista.mostrarPanelParaEditar(
            seleccionado[0], // ID
            seleccionado[1]  // Nombre
        );
    }

    private void ejecutarModificacion() {
        String nombre = vista.getTxtNombre().getText().trim();

        if (nombre.isEmpty()) {
            mostrarAdvertencia("Validación", "El nombre del proveedor es obligatorio.");
            return;
        }

        try {
            Type tipoLista = ManejoJson.obtenerTipoLista(ModeloProveedor.class);
            List<ModeloProveedor> listaProveedores = ManejoJson.leerJson(RUTA_ARCHIVO, tipoLista);

            String idOriginal = vista.getIdOriginal();

            // Buscar y actualizar solo el nombre del proveedor
            for (ModeloProveedor proveedor : listaProveedores) {
                if (proveedor.getId().equals(idOriginal)) {
                    proveedor.setNombre(nombre);
                    break;
                }
            }

            ManejoJson.escribirJson(RUTA_ARCHIVO, listaProveedores);
            cargarProveedoresDesdeArchivo();
            vista.limpiarFormulario();
            vista.cerrarPanelFormulario();
            mostrarExito("Proveedor modificado correctamente.");

        } catch (Exception e) {
            mostrarError("Error al modificar", "No se pudo modificar el proveedor: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void eliminarProveedor() {
        String[] seleccionado = vista.getTablaProveedores().getSelectionModel().getSelectedItem();

        if (seleccionado == null) {
            mostrarAdvertencia("Seleccione un proveedor", "Debe seleccionar un proveedor de la tabla para eliminar.");
            return;
        }

        Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
        confirmacion.setTitle("Confirmar eliminación");
        confirmacion.setHeaderText("¿Está seguro de eliminar este proveedor?");
        confirmacion.setContentText("Proveedor: " + seleccionado[1] + "\nID: " + seleccionado[0] + "\nProductos: " + seleccionado[2]);

        confirmacion.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                try {
                    Type tipoLista = ManejoJson.obtenerTipoLista(ModeloProveedor.class);
                    List<ModeloProveedor> listaProveedores = ManejoJson.leerJson(RUTA_ARCHIVO, tipoLista);

                    String idBuscado = seleccionado[0];
                    listaProveedores.removeIf(proveedor -> proveedor.getId().equals(idBuscado));

                    ManejoJson.escribirJson(RUTA_ARCHIVO, listaProveedores);
                    cargarProveedoresDesdeArchivo();
                    mostrarExito("Proveedor eliminado correctamente.");

                } catch (Exception e) {
                    mostrarError("Error al eliminar", "No se pudo eliminar el proveedor: " + e.getMessage());
                    e.printStackTrace();
                }
            }
        });
    }

    // Método para abrir la ventana de gestión de productos
    private void abrirVentanaGestionProductos(String idProveedor, String nombreProveedor) {
        try {
            // Cargar el proveedor actual
            Type tipoLista = ManejoJson.obtenerTipoLista(ModeloProveedor.class);
            List<ModeloProveedor> listaProveedores = ManejoJson.leerJson(RUTA_ARCHIVO, tipoLista);

            ModeloProveedor proveedorActual = listaProveedores.stream()
                .filter(p -> p.getId().equals(idProveedor))
                .findFirst()
                .orElse(null);

            if (proveedorActual == null) {
                mostrarError("Error", "No se encontró el proveedor seleccionado.");
                return;
            }

            // Crear diálogo para gestionar productos
            Dialog<ButtonType> dialog = new Dialog<>();
            dialog.setTitle("Gestionar Productos");

            // Aplicar estilos CSS
            try {
                dialog.getDialogPane().getStylesheets().add(
                    getClass().getResource("/styles.css").toExternalForm()
                );
            } catch (Exception ex) {
                System.out.println("No se pudo cargar CSS para el diálogo");
            }

            // Contenedor principal
            VBox contenedor = new VBox(12);
            contenedor.setPadding(new Insets(20));
            contenedor.setPrefWidth(800);
            contenedor.setMaxWidth(800);

            // Título del proveedor
            Label lblTituloProveedor = new Label("Productos de: " + nombreProveedor + " (ID: " + idProveedor + ")");
            lblTituloProveedor.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #d4af37; -fx-padding: 0 0 10 0;");

            // Formulario de productos
            GridPane gridProducto = new GridPane();
            gridProducto.setHgap(10);
            gridProducto.setVgap(10);

            TextField txtProductoNombre = new TextField();
            ComboBox<String> cboProductoTipo = new ComboBox<>();
            TextField txtProductoPrecio = new TextField();

            txtProductoNombre.setPromptText("Nombre del producto");

            // Agregar tipos de licores más famosos
            cboProductoTipo.getItems().addAll(
                "Whisky",
                "Ron",
                "Vodka",
                "Tequila",
                "Ginebra",
                "Aguardiente",
                "Brandy",
                "Cerveza",
                "Vino Tinto",
                "Vino Blanco",
                "Champagne",
                "Licor de Café",
                "Licor de Crema",
                "Anís",
                "Pisco",
                "Mezcal",
                "Sake",
                "Cognac",
                "Otro"
            );
            cboProductoTipo.setPromptText("Seleccione tipo");
            cboProductoTipo.setPrefWidth(350);

            txtProductoPrecio.setPromptText("Precio");

            gridProducto.add(new Label("Nombre:"), 0, 0);
            gridProducto.add(txtProductoNombre, 1, 0);
            gridProducto.add(new Label("Tipo:"), 0, 1);
            gridProducto.add(cboProductoTipo, 1, 1);
            gridProducto.add(new Label("Precio:"), 0, 2);
            gridProducto.add(txtProductoPrecio, 1, 2);

            // Tabla de productos
            TableView<String[]> tablaProductos = new TableView<>();
            ObservableList<String[]> datosProductos = FXCollections.observableArrayList();
            tablaProductos.setItems(datosProductos);

            TableColumn<String[], String> colId = new TableColumn<>("ID");
            colId.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue()[0]));
            colId.setPrefWidth(120);
            colId.setMinWidth(100);

            TableColumn<String[], String> colNombre = new TableColumn<>("Nombre");
            colNombre.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue()[1]));
            colNombre.setPrefWidth(300);
            colNombre.setMinWidth(200);

            TableColumn<String[], String> colTipo = new TableColumn<>("Tipo");
            colTipo.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue()[2]));
            colTipo.setPrefWidth(200);
            colTipo.setMinWidth(150);

            TableColumn<String[], String> colPrecio = new TableColumn<>("Precio");
            colPrecio.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue()[3]));
            colPrecio.setPrefWidth(140);
            colPrecio.setMinWidth(100);

            tablaProductos.getColumns().addAll(colId, colNombre, colTipo, colPrecio);
            tablaProductos.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_ALL_COLUMNS);
            tablaProductos.setPrefHeight(300);
            tablaProductos.setPrefWidth(760);

            // Cargar productos actuales
            for (ProductoProveedor p : proveedorActual.getProductos()) {
                datosProductos.add(new String[]{
                    p.getId(), p.getNombre(), p.getTipo(), String.format("$%.2f", p.getPrecio())
                });
            }

            // Botones de acción
            HBox botonesProducto = new HBox(12);
            botonesProducto.setAlignment(Pos.CENTER);
            Button btnAgregar = new Button("Agregar Producto");
            Button btnEliminar = new Button("Eliminar Producto");
            btnAgregar.getStyleClass().add("btn-guardar");
            btnEliminar.getStyleClass().add("btn-eliminar");

            btnAgregar.setOnAction(e -> {
                String nombre = txtProductoNombre.getText().trim();
                String tipo = cboProductoTipo.getValue();
                String precioStr = txtProductoPrecio.getText().trim();

                if (nombre.isEmpty() || tipo == null || tipo.isEmpty() || precioStr.isEmpty()) {
                    mostrarAdvertencia("Validación", "Todos los campos son obligatorios.");
                    return;
                }

                try {
                    double precio = Double.parseDouble(precioStr);
                    if (precio < 0) {
                        mostrarAdvertencia("Validación", "El precio debe ser positivo.");
                        return;
                    }

                    // Generar ID único global contando todos los productos de todos los proveedores
                    int totalProductosGlobal = contarTodosLosProductos(listaProveedores);
                    String idProducto = generarIdProducto(totalProductosGlobal);
                    ProductoProveedor producto = new ProductoProveedor(idProducto, nombre, tipo, precio);
                    proveedorActual.agregarProducto(producto);

                    // Actualizar tabla
                    datosProductos.add(new String[]{
                        idProducto, nombre, tipo, String.format("$%.2f", precio)
                    });

                    // Guardar cambios
                    ManejoJson.escribirJson(RUTA_ARCHIVO, listaProveedores);
                    cargarProveedoresDesdeArchivo();

                    txtProductoNombre.clear();
                    cboProductoTipo.setValue(null);
                    txtProductoPrecio.clear();
                    mostrarExito("Producto agregado correctamente.");
                } catch (NumberFormatException ex) {
                    mostrarAdvertencia("Validación", "El precio debe ser un número válido.");
                }
            });

            btnEliminar.setOnAction(e -> {
                String[] seleccionado = tablaProductos.getSelectionModel().getSelectedItem();
                if (seleccionado == null) {
                    mostrarAdvertencia("Selección", "Seleccione un producto para eliminar.");
                    return;
                }

                proveedorActual.eliminarProducto(seleccionado[0]);
                datosProductos.remove(seleccionado);
                ManejoJson.escribirJson(RUTA_ARCHIVO, listaProveedores);
                cargarProveedoresDesdeArchivo();
                mostrarExito("Producto eliminado correctamente.");
            });

            botonesProducto.getChildren().addAll(btnAgregar, btnEliminar);

            Label lblAgregar = new Label("Agregar nuevo producto:");
            lblAgregar.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #d4af37;");

            Label lblActuales = new Label("Productos actuales:");
            lblActuales.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #d4af37;");

            contenedor.getChildren().addAll(
                lblTituloProveedor,
                new Separator(),
                lblAgregar,
                gridProducto,
                new Separator(),
                lblActuales,
                tablaProductos,
                botonesProducto
            );

            dialog.getDialogPane().setContent(contenedor);

            // Agregar botón de cerrar y aplicarle estilos
            ButtonType btnCerrarType = new ButtonType("Cerrar", ButtonBar.ButtonData.CANCEL_CLOSE);
            dialog.getDialogPane().getButtonTypes().add(btnCerrarType);

            // Aplicar estilo al botón de cerrar
            Button btnCerrar = (Button) dialog.getDialogPane().lookupButton(btnCerrarType);
            if (btnCerrar != null) {
                btnCerrar.getStyleClass().add("btn-salir");
            }

            // Eliminar el header y reducir padding
            dialog.getDialogPane().setStyle("-fx-padding: 15px;");
            dialog.getDialogPane().setHeaderText(null);

            dialog.showAndWait();

        } catch (Exception e) {
            mostrarError("Error", "No se pudo abrir la gestión de productos: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void cargarProveedoresDesdeArchivo() {
        Type tipoLista = ManejoJson.obtenerTipoLista(ModeloProveedor.class);
        List<ModeloProveedor> listaProveedores = ManejoJson.leerJson(RUTA_ARCHIVO, tipoLista);

        datosTablaProveedores.clear();
        for (ModeloProveedor p : listaProveedores) {
            datosTablaProveedores.add(new String[]{
                p.getId(),
                p.getNombre(),
                String.valueOf(p.getCantidadProductos())
            });
        }
    }

    private String generarIdProveedor(int cantidadActual) {
        int nuevoNumero = cantidadActual + 1;
        return String.format("PRV-%03d", nuevoNumero);
    }

    // Cuenta todos los productos de todos los proveedores para generar IDs únicos globales
    private int contarTodosLosProductos(List<ModeloProveedor> proveedores) {
        int total = 0;
        for (ModeloProveedor proveedor : proveedores) {
            if (proveedor.getProductos() != null) {
                total += proveedor.getProductos().size();
            }
        }
        return total;
    }

    private String generarIdProducto(int cantidadActual) {
        int nuevoNumero = cantidadActual + 1;
        return String.format("PROD-%03d", nuevoNumero);
    }

    private void mostrarExito(String mensaje) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Éxito");
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    private void mostrarError(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    private void mostrarAdvertencia(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}

