package view;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Modality;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

// Vista para la gestión de productos.
// Permite registrar nuevos productos y ver la lista de productos registrados.
public class frmInventario extends Stage {
    private final BorderPane contenedorProductos = new BorderPane();

    // Campos del formulario
    private final TextField txtNombre = new TextField();
    private final ComboBox<String> cboCategoria = new ComboBox<>();
    private final TextField txtCosto = new TextField();
    private final TextField txtStock = new TextField();

    // Tabla para mostrar productos
    private final TableView<String[]> tablaProductos = new TableView<>();

    // Botones
    private final Button btnGuardar = new Button("Guardar");
    private final Button btnSalir = new Button("Salir");
    private final Button btnAgregarProducto = new Button("+ Agregar producto");

    // Panel flotante con formulario para nuevo producto
    private final StackPane panelEmergenteNuevoProducto = new StackPane();
    private final VBox panelFormulario = new VBox(12);

    public frmInventario(Stage ventanaPadre) {
        setTitle("Gestión de Productos");
        initOwner(ventanaPadre);
        initModality(Modality.APPLICATION_MODAL);
        initStyle(StageStyle.UNDECORATED);

        // Construir la parte superior (título grande + subtítulo y botón agregar)
        HBox header = buildHeader();

        // Tabla en un solo bloque central oscuro
        VBox centro = new VBox(16);
        centro.getStyleClass().add("productos-centro-container");
        centro.setPadding(new Insets(20, 32, 24, 32));

        // Tabla
        VBox tabla = buildTabla();

        centro.getChildren().addAll(tabla);

        contenedorProductos.setTop(header);
        contenedorProductos.setCenter(centro);
        contenedorProductos.setBottom(buildBotones());

        // Configurar panelEmergenteNuevoProducto para formulario flotante
        configurarPanelNuevoProducto();
        StackPane contenedorPrincipal = new StackPane(contenedorProductos, panelEmergenteNuevoProducto);

        // Obtener dimensiones de la pantalla
        Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();
        double screenWidth = screenBounds.getWidth();
        double screenHeight = screenBounds.getHeight();

        // Altura aproximada de la barra superior de frmPrincipal
        double navBarHeight = 50;
        double windowHeight = screenHeight - navBarHeight;

        Scene scene = new Scene(contenedorPrincipal, screenWidth, windowHeight);
        try {
            String css = getClass().getResource("/styles.css").toExternalForm();
            scene.getStylesheets().add(css);
        } catch (Exception e) {
            System.out.println("No se pudo cargar el CSS en frmProductos: " + e.getMessage());
        }

        setScene(scene);
        setResizable(false);

        // Posicionar la ventana justo debajo de la barra de navegación
        setX(screenBounds.getMinX());
        setY(screenBounds.getMinY() + navBarHeight);
    }

    // Encabezado: "Productos" + subtítulo + botón agregar
    private HBox buildHeader() {
        Label titulo = new Label("Productos");
        titulo.getStyleClass().add("productos-titulo");

        Label subtitulo = new Label("Gestiona tu inventario de productos");
        subtitulo.getStyleClass().add("productos-subtitulo");

        VBox textos = new VBox(4, titulo, subtitulo);

        btnAgregarProducto.getStyleClass().add("btn-agregar");
        btnAgregarProducto.setOnAction(e -> mostrarPanelFormulario());

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        HBox header = new HBox(16, textos, spacer, btnAgregarProducto);
        header.getStyleClass().add("productos-header");
        header.setPadding(new Insets(24, 32, 16, 32));
        header.setAlignment(Pos.CENTER_LEFT);
        return header;
    }

    private void configurarPanelNuevoProducto() {
        panelFormulario.getChildren().clear();
        panelFormulario.getStyleClass().add("productos-form-overlay");
        panelFormulario.setPadding(new Insets(20));

        Label tituloNuevoProducto = new Label("Nuevo producto");
        tituloNuevoProducto.getStyleClass().add("titulo-formulario");

        GridPane gridNuevoProducto = new GridPane();
        gridNuevoProducto.setHgap(10);
        gridNuevoProducto.setVgap(10);

        // Configurar ComboBox de categorías
        cboCategoria.getItems().setAll("Licores", "Vinos", "Cervezas", "Whisky", "Ron", "Vodka", "Tequila", "Otros");
        cboCategoria.setPromptText("Seleccione categoría");

        txtNombre.setPromptText("Nombre del producto");
        txtCosto.setPromptText("Costo de compra");
        txtStock.setPromptText("Cantidad en stock");

        // Agregar al grid
        gridNuevoProducto.add(new Label("Nombre:"), 0, 0);
        gridNuevoProducto.add(txtNombre, 1, 0);
        gridNuevoProducto.add(new Label("Categoría:"), 0, 1);
        gridNuevoProducto.add(cboCategoria, 1, 1);
        gridNuevoProducto.add(new Label("Costo:"), 0, 2);
        gridNuevoProducto.add(txtCosto, 1, 2);
        gridNuevoProducto.add(new Label("Stock:"), 0, 3);
        gridNuevoProducto.add(txtStock, 1, 3);

        VBox botonesNuevoProducto = new VBox(12);
        botonesNuevoProducto.setAlignment(Pos.CENTER);
        Button btnSalir = new Button("Salir");
        btnSalir.getStyleClass().add("btn-salir");
        btnSalir.setOnAction(e -> ocultarPanelFormulario());
        botonesNuevoProducto.getChildren().addAll(btnGuardar, btnSalir);

        panelFormulario.getChildren().addAll(tituloNuevoProducto, new Separator(), gridNuevoProducto,
                botonesNuevoProducto);
        panelEmergenteNuevoProducto.getChildren().setAll(panelFormulario);
        panelEmergenteNuevoProducto.getStyleClass().add("productos-overlay");
        panelEmergenteNuevoProducto.setVisible(false);
    }

    private void mostrarPanelFormulario() {
        // Limpiar campos para un nuevo producto
        txtNombre.clear();
        cboCategoria.getSelectionModel().clearSelection();
        txtCosto.clear();
        txtStock.clear();
        panelEmergenteNuevoProducto.setVisible(true);
    }

    private void ocultarPanelFormulario() {
        panelEmergenteNuevoProducto.setVisible(false);
    }

    private VBox buildTabla() {
        TableColumn<String[], String> colId = new TableColumn<>("ID");
        colId.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue()[0]));

        TableColumn<String[], String> colNombre = new TableColumn<>("Nombre");
        colNombre.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue()[1]));

        TableColumn<String[], String> colCategoria = new TableColumn<>("Categoría");
        colCategoria.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue()[2]));

        TableColumn<String[], String> colCosto = new TableColumn<>("Costo");
        colCosto.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue()[3]));

        TableColumn<String[], String> colPrecioVenta = new TableColumn<>("Precio Venta");
        colPrecioVenta.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue()[4]));

        TableColumn<String[], String> colStock = new TableColumn<>("Stock");
        colStock.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue()[5]));

        tablaProductos.getColumns().addAll(colId, colNombre, colCategoria, colCosto, colPrecioVenta, colStock);
        tablaProductos.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);

        VBox box = new VBox(10);
        box.getStyleClass().add("tabla-container");
        Label titulo = new Label("Lista de Productos");
        titulo.getStyleClass().add("titulo-tabla");
        box.getChildren().addAll(titulo, tablaProductos);
        VBox.setVgrow(tablaProductos, Priority.ALWAYS);
        return box;
    }

    private HBox buildBotones() {
        HBox box = new HBox();
        box.getStyleClass().add("botones-container");

        // Aplicar clases de estilo a los botones
        btnSalir.getStyleClass().add("btn-salir");
        btnGuardar.getStyleClass().add("btn-guardar");

        btnSalir.setOnAction(e -> close());

        box.getChildren().addAll(btnSalir);
        return box;
    }

    // Getters para el controlador
    public TextField getTxtNombre() {
        return txtNombre;
    }

    public ComboBox<String> getCboCategoria() {
        return cboCategoria;
    }

    public TextField getTxtCosto() {
        return txtCosto;
    }

    public TextField getTxtPrecio() {
        return txtCosto;
    } // Compatibilidad legacy

    public TextField getTxtStock() {
        return txtStock;
    }

    public TableView<String[]> getTablaProductos() {
        return tablaProductos;
    }

    public Button getBtnGuardar() {
        return btnGuardar;
    }

    public Button getBtnSalir() {
        return btnSalir;
    }
}
