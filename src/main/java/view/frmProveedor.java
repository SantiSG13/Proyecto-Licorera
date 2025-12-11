package view;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.stage.Modality;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

// Vista para la gestión de proveedores.
// Permite registrar nuevos proveedores con sus productos y ver la lista de proveedores registrados.
public class frmProveedor extends Stage {
    private final BorderPane contenedorProveedor = new BorderPane();

    // Campos del formulario de proveedor
    private final TextField txtId = new TextField();
    private final TextField txtNombre = new TextField();

    // Campos del formulario de productos
    private final TextField txtProductoId = new TextField();
    private final TextField txtProductoNombre = new TextField();
    private final TextField txtProductoTipo = new TextField();
    private final TextField txtProductoPrecio = new TextField();

    // Tablas
    private final TableView<String[]> tablaProveedores = new TableView<>();
    private final TableView<String[]> tablaProductos = new TableView<>();

    // Botones principales
    private final Button btnGuardarProveedor = new Button("Guardar Proveedor");
    private final Button btnModificar = new Button("Modificar");
    private final Button btnEliminar = new Button("Eliminar");
    private final Button btnSalir = new Button("Salir");
    private final Button btnAgregarProveedor = new Button("+ Agregar proveedor");

    // Botones de productos
    private final Button btnAgregarProducto = new Button("+ Agregar Producto");
    private final Button btnEliminarProducto = new Button("Eliminar Producto");

    // Panel flotante con formulario para nuevo proveedor
    private final StackPane panelEmergenteProveedor = new StackPane();
    private final VBox panelFormulario = new VBox(12);

    // Control de modo del formulario
    private boolean modoEdicion = false;
    private String idOriginal = null;
    private Label tituloFormulario = new Label();

    // Callback para gestionar productos (será establecido por el controlador)
    private java.util.function.BiConsumer<String, String> accionGestionProductos;

    public frmProveedor(Stage ventanaPadre) {
        setTitle("Gestión de Proveedores");
        initOwner(ventanaPadre);
        initModality(Modality.APPLICATION_MODAL);
        initStyle(StageStyle.UNDECORATED);

        HBox header = buildHeader();

        VBox centro = new VBox(16);
        centro.getStyleClass().add("proveedores-centro-container");
        centro.setPadding(new Insets(20, 32, 24, 32));

        VBox tabla = buildTabla();

        centro.getChildren().addAll(tabla);

        contenedorProveedor.setTop(header);
        contenedorProveedor.setCenter(centro);
        contenedorProveedor.setBottom(buildBotones());

        configurarPanelNuevoProveedor();
        StackPane contenedorPrincipal = new StackPane(contenedorProveedor, panelEmergenteProveedor);

        Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();
        double screenWidth = screenBounds.getWidth();
        double screenHeight = screenBounds.getHeight();

        double navBarHeight = 50;
        double windowHeight = screenHeight - navBarHeight;

        Scene scene = new Scene(contenedorPrincipal, screenWidth, windowHeight);
        try {
            String css = getClass().getResource("/styles.css").toExternalForm();
            scene.getStylesheets().add(css);
        } catch (Exception e) {
            System.out.println("No se pudo cargar el CSS en frmProveedor: " + e.getMessage());
        }

        setScene(scene);
        setResizable(false);

        setX(screenBounds.getMinX());
        setY(screenBounds.getMinY() + navBarHeight);
    }

    private HBox buildHeader() {
        Label titulo = new Label("Proveedores");
        titulo.getStyleClass().add("proveedores-titulo");

        Label subtitulo = new Label("Gestiona tu base de datos de proveedores y sus productos");
        subtitulo.getStyleClass().add("proveedores-subtitulo");

        VBox textos = new VBox(4, titulo, subtitulo);

        btnAgregarProveedor.getStyleClass().add("btn-agregar");
        btnAgregarProveedor.setOnAction(e -> mostrarPanelFormulario());

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        HBox header = new HBox(16, textos, spacer, btnAgregarProveedor);
        header.getStyleClass().add("proveedores-header");
        header.setPadding(new Insets(24, 32, 16, 32));
        header.setAlignment(Pos.CENTER_LEFT);
        return header;
    }

    private void configurarPanelNuevoProveedor() {
        panelFormulario.getChildren().clear();
        panelFormulario.getStyleClass().add("proveedores-form-overlay");
        panelFormulario.setPadding(new Insets(20));

        tituloFormulario.setText("Nuevo proveedor");
        tituloFormulario.getStyleClass().add("titulo-formulario");

        // Formulario del proveedor (solo ID y Nombre)
        GridPane gridProveedor = new GridPane();
        gridProveedor.setHgap(10);
        gridProveedor.setVgap(10);

        txtId.setPromptText("ID (auto)");
        txtId.setEditable(false);
        txtNombre.setPromptText("Nombre del proveedor");

        gridProveedor.add(new Label("ID:"), 0, 0);
        gridProveedor.add(txtId, 1, 0);
        gridProveedor.add(new Label("Nombre:"), 0, 1);
        gridProveedor.add(txtNombre, 1, 1);

        // Botones del formulario
        HBox botonesFormulario = new HBox(12);
        botonesFormulario.setAlignment(Pos.CENTER);
        Button btnCerrar = new Button("Cerrar");
        btnCerrar.getStyleClass().add("btn-salir");
        btnCerrar.setOnAction(e -> ocultarPanelFormulario());
        botonesFormulario.getChildren().addAll(btnGuardarProveedor, btnCerrar);

        panelFormulario.getChildren().addAll(
                tituloFormulario,
                new Separator(),
                gridProveedor,
                new Separator(),
                botonesFormulario);

        panelEmergenteProveedor.getChildren().setAll(panelFormulario);
        panelEmergenteProveedor.getStyleClass().add("proveedores-overlay");
        panelEmergenteProveedor.setVisible(false);
    }

    private void mostrarPanelFormulario() {
        modoEdicion = false;
        idOriginal = null;
        tituloFormulario.setText("Nuevo proveedor");
        limpiarFormulario();
        panelEmergenteProveedor.setVisible(true);
    }

    private void ocultarPanelFormulario() {
        panelEmergenteProveedor.setVisible(false);
        modoEdicion = false;
        idOriginal = null;
    }

    public void mostrarPanelParaEditar(String id, String nombre) {
        modoEdicion = true;
        idOriginal = id;
        tituloFormulario.setText("Modificar proveedor");

        txtId.setText(id);
        txtNombre.setText(nombre);

        panelEmergenteProveedor.setVisible(true);
    }

    private VBox buildTabla() {
        TableColumn<String[], String> colId = new TableColumn<>("ID");
        colId.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue()[0]));
        colId.setPrefWidth(100);

        TableColumn<String[], String> colNombre = new TableColumn<>("Nombre");
        colNombre.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue()[1]));
        colNombre.setPrefWidth(300);

        TableColumn<String[], String> colProductos = new TableColumn<>("Productos");
        colProductos.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue()[2]));
        colProductos.setPrefWidth(120);

        // Columna con botón para gestionar productos
        TableColumn<String[], Button> colAccion = new TableColumn<>("Acción");
        colAccion.setCellFactory(param -> new TableCell<>() {
            private final Button btnGestionarProductos = new Button("Gestionar Productos");
            {
                btnGestionarProductos.getStyleClass().add("btn-agregar");
                btnGestionarProductos.setStyle("-fx-font-size: 11px; -fx-padding: 6px 12px;");
            }

            // Sobrescribe el metodo para actualizar la celda
            @Override
            protected void updateItem(Button item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(btnGestionarProductos);
                    btnGestionarProductos.setOnAction(e -> {
                        String[] proveedor = getTableView().getItems().get(getIndex());
                        abrirGestionProductos(proveedor[0], proveedor[1]); // ID y Nombre
                    });
                }
            }
        });
        colAccion.setPrefWidth(180);

        tablaProveedores.getColumns().addAll(colId, colNombre, colProductos, colAccion);
        tablaProveedores.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_ALL_COLUMNS);

        VBox box = new VBox(10);
        Label titulo = new Label("Registro de Proveedores");
        titulo.getStyleClass().add("titulo-tabla");
        box.getChildren().addAll(titulo, tablaProveedores);
        VBox.setVgrow(tablaProveedores, Priority.ALWAYS);
        return box;
    }

    private void buildTablaProductos() {
        TableColumn<String[], String> colId = new TableColumn<>("ID");
        colId.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue()[0]));
        colId.setPrefWidth(100);

        TableColumn<String[], String> colNombre = new TableColumn<>("Nombre");
        colNombre.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue()[1]));
        colNombre.setPrefWidth(200);

        TableColumn<String[], String> colTipo = new TableColumn<>("Tipo");
        colTipo.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue()[2]));
        colTipo.setPrefWidth(150);

        TableColumn<String[], String> colPrecio = new TableColumn<>("Precio");
        colPrecio.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue()[3]));
        colPrecio.setPrefWidth(100);

        tablaProductos.getColumns().addAll(colId, colNombre, colTipo, colPrecio);
        tablaProductos.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_ALL_COLUMNS);
        tablaProductos.setPlaceholder(new Label("No hay productos agregados"));
    }

    private HBox buildBotones() {
        HBox box = new HBox();
        box.getStyleClass().add("botones-container");

        btnGuardarProveedor.getStyleClass().add("btn-guardar");
        btnModificar.getStyleClass().add("btn-modificar");
        btnEliminar.getStyleClass().add("btn-eliminar");
        btnSalir.getStyleClass().add("btn-salir");

        btnSalir.setOnAction(e -> close());

        box.getChildren().addAll(btnModificar, btnEliminar, btnSalir);
        return box;
    }

    // Getters para el controlador
    public TextField getTxtId() {
        return txtId;
    }

    public TextField getTxtNombre() {
        return txtNombre;
    }

    public TextField getTxtProductoId() {
        return txtProductoId;
    }

    public TextField getTxtProductoNombre() {
        return txtProductoNombre;
    }

    public TextField getTxtProductoTipo() {
        return txtProductoTipo;
    }

    public TextField getTxtProductoPrecio() {
        return txtProductoPrecio;
    }

    public TableView<String[]> getTablaProveedores() {
        return tablaProveedores;
    }

    public TableView<String[]> getTablaProductos() {
        return tablaProductos;
    }

    public Button getBtnGuardarProveedor() {
        return btnGuardarProveedor;
    }

    public Button getBtnModificar() {
        return btnModificar;
    }

    public Button getBtnEliminar() {
        return btnEliminar;
    }

    public Button getBtnAgregarProducto() {
        return btnAgregarProducto;
    }

    public Button getBtnEliminarProducto() {
        return btnEliminarProducto;
    }

    public void limpiarFormulario() {
        txtId.clear();
        txtNombre.clear();
        tablaProductos.getItems().clear();
        limpiarFormularioProducto();
    }

    public void limpiarFormularioProducto() {
        txtProductoId.clear();
        txtProductoNombre.clear();
        txtProductoTipo.clear();
        txtProductoPrecio.clear();
    }

    public void cerrarPanelFormulario() {
        ocultarPanelFormulario();
    }

    public boolean isModoEdicion() {
        return modoEdicion;
    }

    public String getIdOriginal() {
        return idOriginal;
    }

    // Metodo que será llamado desde el botón de cada fila
    private void abrirGestionProductos(String idProveedor, String nombreProveedor) {
        if (accionGestionProductos != null) {
            accionGestionProductos.accept(idProveedor, nombreProveedor);
        }
    }

    // Metodo público para que el controlador pueda establecer la acción del botón
    public void setAccionGestionProductos(java.util.function.BiConsumer<String, String> accion) {
        this.accionGestionProductos = accion;
    }
}
