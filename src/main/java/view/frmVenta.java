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
import model.ModeloVenta.ItemVenta;

// Vista para gestión de ventas - ventana modal
public class frmVenta extends Stage {
    private final BorderPane contenedorVenta = new BorderPane();

    // Controles principales
    private final ComboBox<String> cmbClientes = new ComboBox<>();
    private final ComboBox<String> cmbProductos = new ComboBox<>();
    private final TableView<ItemVenta> tablaCarrito = new TableView<>();
    private final Label lblTotal = new Label("$0.00");
    private final Button btnCompletarVenta = new Button("Completar Venta");
    private final Button btnSalir = new Button("Salir");

    // Callback para cuando cambia la cantidad
    private Runnable onCantidadChanged;

    public frmVenta(Stage ventanaPadre) {
        setTitle("Gestión de Ventas");
        initOwner(ventanaPadre);
        initModality(Modality.APPLICATION_MODAL);
        initStyle(StageStyle.UNDECORATED);

        buildUI();
    }

    private void buildUI() {
        HBox header = buildHeader();

        VBox centro = new VBox(16);
        centro.getStyleClass().add("ventas-centro-container");
        centro.setPadding(new Insets(20, 32, 24, 32));

        HBox controlsBar = createControlsBar();
        VBox tabla = createCenterPanel();

        centro.getChildren().addAll(controlsBar, tabla);

        contenedorVenta.setTop(header);
        contenedorVenta.setCenter(centro);
        contenedorVenta.setBottom(createBottomPanel());

        StackPane contenedorPrincipal = new StackPane(contenedorVenta);

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
            System.out.println("No se pudo cargar el CSS en frmVenta: " + e.getMessage());
        }

        setScene(scene);
        setResizable(false);

        setX(screenBounds.getMinX());
        setY(screenBounds.getMinY() + navBarHeight);
    }

    private HBox buildHeader() {
        Label titulo = new Label("Ventas");
        titulo.getStyleClass().add("ventas-titulo");

        Label subtitulo = new Label("Realizar ventas a clientes registrados");
        subtitulo.getStyleClass().add("ventas-subtitulo");

        VBox textos = new VBox(4, titulo, subtitulo);

        btnSalir.getStyleClass().add("btn-salir");
        btnSalir.setOnAction(e -> close());

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        HBox header = new HBox(16, textos, spacer, btnSalir);
        header.getStyleClass().add("ventas-header");
        header.setPadding(new Insets(24, 32, 16, 32));
        header.setAlignment(Pos.CENTER_LEFT);
        return header;
    }

    private HBox createControlsBar() {
        HBox controlsBar = new HBox(15);
        controlsBar.setAlignment(Pos.CENTER_LEFT);
        controlsBar.setPadding(new Insets(15, 0, 10, 0));
        controlsBar.getStyleClass().add("ventas-controls-bar");

        // Cliente
        Label lblIconCliente = new Label("👤");
        lblIconCliente.getStyleClass().add("ventas-controls-icon");

        Label lblCliente = new Label("Seleccionar Cliente:");
        lblCliente.getStyleClass().add("ventas-controls-label");

        cmbClientes.setPromptText("Buscar cliente...");
        cmbClientes.setPrefHeight(40);
        cmbClientes.setPrefWidth(350);
        cmbClientes.getStyleClass().add("ventas-combo");

        // Producto
        Label lblIconProducto = new Label("🛒");
        lblIconProducto.getStyleClass().add("ventas-controls-icon");

        Label lblProducto = new Label("Seleccionar Producto:");
        lblProducto.getStyleClass().add("ventas-controls-label");

        cmbProductos.setPromptText("Buscar producto...");
        cmbProductos.setPrefHeight(40);
        cmbProductos.setPrefWidth(350);
        cmbProductos.getStyleClass().add("ventas-combo");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Label lblItemCount = new Label("0 items");
        lblItemCount.setId("item-count-label");
        lblItemCount.getStyleClass().add("ventas-item-count");

        controlsBar.getChildren().addAll(
            lblIconCliente, lblCliente, cmbClientes,
            lblIconProducto, lblProducto, cmbProductos,
            spacer, lblItemCount
        );
        return controlsBar;
    }

    private VBox createCenterPanel() {
        VBox centerPanel = new VBox(0);
        centerPanel.setPadding(new Insets(10, 0, 10, 0));

        // Header de la tabla con título elegante
        HBox tableHeader = new HBox(12);
        tableHeader.setAlignment(Pos.CENTER_LEFT);
        tableHeader.setPadding(new Insets(10, 15, 10, 15));
        tableHeader.getStyleClass().add("ventas-table-header");

        Label iconoTabla = new Label("🛍️");
        iconoTabla.getStyleClass().add("ventas-table-icon");

        Label lblTabla = new Label("Productos del Carrito de Venta");
        lblTabla.getStyleClass().add("ventas-table-titulo");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Label lblInfo = new Label("Agregue productos seleccionándolos del menú superior");
        lblInfo.getStyleClass().add("ventas-table-info");

        tableHeader.getChildren().addAll(iconoTabla, lblTabla, spacer, lblInfo);

        // Configurar tabla
        configureTable();
        VBox.setVgrow(tablaCarrito, Priority.ALWAYS);

        centerPanel.getChildren().addAll(tableHeader, tablaCarrito);
        return centerPanel;
    }

    private void configureTable() {
        tablaCarrito.setId("ventas-tabla");
        tablaCarrito.getStyleClass().add("table-view");
        tablaCarrito.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        tablaCarrito.setFixedCellSize(60); // Altura fija para todas las filas

        Label placeholder = new Label("El carrito está vacío");
        placeholder.getStyleClass().add("ventas-placeholder");
        tablaCarrito.setPlaceholder(placeholder);

        // Columna Nombre del Producto
        TableColumn<ItemVenta, String> colNombre = new TableColumn<>("PRODUCTO");
        colNombre.setCellValueFactory(new PropertyValueFactory<>("nombreProducto"));
        colNombre.setMinWidth(300);
        colNombre.setStyle("-fx-padding: 0;");
        colNombre.setCellFactory(col -> new TableCell<ItemVenta, String>() {
            private final VBox container = new VBox();
            private final Label lblNombre = new Label();

            {
                lblNombre.getStyleClass().add("ventas-cell-producto");
                container.setAlignment(Pos.CENTER);
                container.setMinHeight(60);
                container.setMaxHeight(60);
                container.setPadding(Insets.EMPTY);
                container.getChildren().add(lblNombre);
                setPadding(Insets.EMPTY);
            }

            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setGraphic(null);
                } else {
                    lblNombre.setText(item);
                    setGraphic(container);
                }
            }
        });

        // Columna Precio Unitario
        TableColumn<ItemVenta, Double> colPrecio = new TableColumn<>("PRECIO UNITARIO");
        colPrecio.setCellValueFactory(new PropertyValueFactory<>("precioUnitario"));
        colPrecio.setMinWidth(150);
        colPrecio.setStyle("-fx-padding: 0;");
        colPrecio.setCellFactory(col -> new TableCell<ItemVenta, Double>() {
            private final VBox container = new VBox();
            private final Label lblPrecio = new Label();

            {
                lblPrecio.getStyleClass().add("ventas-cell-precio");
                container.setAlignment(Pos.CENTER);
                container.setMinHeight(60);
                container.setMaxHeight(60);
                container.setPadding(Insets.EMPTY);
                container.getChildren().add(lblPrecio);
                setPadding(Insets.EMPTY);
            }

            @Override
            protected void updateItem(Double item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setGraphic(null);
                } else {
                    lblPrecio.setText(String.format("$%,.2f", item));
                    setGraphic(container);
                }
            }
        });

        // Columna Cantidad con botones +/-
        TableColumn<ItemVenta, Integer> colCantidad = new TableColumn<>("CANTIDAD");
        colCantidad.setCellValueFactory(new PropertyValueFactory<>("cantidad"));
        colCantidad.setMinWidth(180);
        colCantidad.setStyle("-fx-alignment: CENTER; -fx-padding: 0;");
        colCantidad.setCellFactory(col -> new TableCell<ItemVenta, Integer>() {
            private final HBox container = new HBox(10);
            private final Button btnMenos = new Button("−");
            private final TextField txtCantidad = new TextField();
            private final Button btnMas = new Button("+");

            {
                btnMenos.getStyleClass().add("ventas-cantidad-btn");
                btnMas.getStyleClass().add("ventas-cantidad-btn");

                txtCantidad.setPrefWidth(80);
                txtCantidad.setPrefHeight(38);
                txtCantidad.setAlignment(Pos.CENTER);
                txtCantidad.getStyleClass().add("ventas-cantidad-field");

                container.setAlignment(Pos.CENTER);
                container.setMinHeight(60);
                container.setMaxHeight(60);
                container.setPadding(Insets.EMPTY);
                container.getChildren().addAll(btnMenos, txtCantidad, btnMas);
                setPadding(Insets.EMPTY);

                btnMenos.setOnAction(e -> {
                    ItemVenta item = getTableView().getItems().get(getIndex());
                    if (item.getCantidad() > 1) {
                        item.setCantidad(item.getCantidad() - 1);
                        txtCantidad.setText(String.valueOf(item.getCantidad()));
                        getTableView().refresh();
                        if (onCantidadChanged != null) onCantidadChanged.run();
                    }
                });

                btnMas.setOnAction(e -> {
                    ItemVenta item = getTableView().getItems().get(getIndex());
                    item.setCantidad(item.getCantidad() + 1);
                    txtCantidad.setText(String.valueOf(item.getCantidad()));
                    getTableView().refresh();
                    if (onCantidadChanged != null) onCantidadChanged.run();
                });

                txtCantidad.setOnAction(e -> {
                    try {
                        int nuevaCantidad = Integer.parseInt(txtCantidad.getText());
                        if (nuevaCantidad > 0) {
                            ItemVenta item = getTableView().getItems().get(getIndex());
                            item.setCantidad(nuevaCantidad);
                            getTableView().refresh();
                            if (onCantidadChanged != null) onCantidadChanged.run();
                        }
                    } catch (NumberFormatException ex) {
                        txtCantidad.setText(String.valueOf(getItem()));
                    }
                });
            }

            @Override
            protected void updateItem(Integer item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setGraphic(null);
                } else {
                    txtCantidad.setText(String.valueOf(item));
                    setGraphic(container);
                }
            }
        });

        // Columna Acciones (botón eliminar)
        TableColumn<ItemVenta, Void> colAcciones = new TableColumn<>("ACCIONES");
        colAcciones.setMinWidth(160);
        colAcciones.setMaxWidth(160);
        colAcciones.setResizable(false);
        colAcciones.setStyle("-fx-alignment: CENTER; -fx-padding: 0;");
        colAcciones.setCellFactory(col -> new TableCell<ItemVenta, Void>() {
            private final VBox container = new VBox();
            private final Button btnEliminar = new Button("🗑 Eliminar");

            {
                btnEliminar.getStyleClass().add("ventas-btn-eliminar");

                container.setAlignment(Pos.CENTER);
                container.setMinHeight(60);
                container.setMaxHeight(60);
                container.setPadding(Insets.EMPTY);
                container.getChildren().add(btnEliminar);
                setPadding(Insets.EMPTY);

                btnEliminar.setOnAction(e -> {
                    ItemVenta item = getTableView().getItems().get(getIndex());
                    getTableView().getItems().remove(item);
                    if (onCantidadChanged != null) {
                        onCantidadChanged.run();
                    }
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(container);
                }
            }
        });

        tablaCarrito.getColumns().addAll(colNombre, colPrecio, colCantidad, colAcciones);
    }

    private HBox createBottomPanel() {
        HBox bottomPanel = new HBox(20);
        bottomPanel.setPadding(new Insets(16, 32, 24, 32));
        bottomPanel.setAlignment(Pos.CENTER);
        bottomPanel.getStyleClass().add("ventas-bottom-panel");

        // Sección izquierda - Información
        VBox infoSection = new VBox(8);
        infoSection.setAlignment(Pos.CENTER_LEFT);

        Label lblInfo = new Label("💡 Información");
        lblInfo.getStyleClass().add("ventas-info-titulo");

        Label lblDetalle = new Label("• El inventario se actualizará automáticamente\n• Se descontará el stock de los productos\n• Se generará un registro de la venta");
        lblDetalle.getStyleClass().add("ventas-info-detalle");

        infoSection.getChildren().addAll(lblInfo, lblDetalle);

        // Sección central - Total
        VBox totalSection = new VBox(5);
        totalSection.setAlignment(Pos.CENTER);
        totalSection.getStyleClass().add("ventas-total-section");

        Label lblTotalTxt = new Label("TOTAL A PAGAR");
        lblTotalTxt.getStyleClass().add("ventas-total-label");

        lblTotal.getStyleClass().add("ventas-total-monto");

        totalSection.getChildren().addAll(lblTotalTxt, lblTotal);

        // Sección derecha - Botón
        VBox buttonSection = new VBox(8);
        buttonSection.setAlignment(Pos.CENTER_RIGHT);
        buttonSection.setPrefWidth(300);

        btnCompletarVenta.setText("✓  COMPLETAR VENTA");
        btnCompletarVenta.setPrefWidth(280);
        btnCompletarVenta.setPrefHeight(60);
        btnCompletarVenta.getStyleClass().add("ventas-btn-completar");
        btnCompletarVenta.setDisable(true);

        Label lblHint = new Label("Presione para confirmar la venta");
        lblHint.getStyleClass().add("ventas-btn-hint");
        lblHint.setAlignment(Pos.CENTER);

        buttonSection.getChildren().addAll(btnCompletarVenta, lblHint);

        bottomPanel.getChildren().addAll(infoSection, totalSection, buttonSection);
        return bottomPanel;
    }

    // Getters
    public ComboBox<String> getCmbClientes() { return cmbClientes; }
    public ComboBox<String> getCmbProductos() { return cmbProductos; }
    public TableView<ItemVenta> getTablaCarrito() { return tablaCarrito; }
    public Label getLblTotal() { return lblTotal; }
    public Button getBtnCompletarVenta() { return btnCompletarVenta; }
    public Button getBtnCerrar() { return btnSalir; }

    public void setOnCantidadChanged(Runnable callback) {
        this.onCantidadChanged = callback;
    }
}

