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
import model.ModeloCompra.ItemCompra;

// Vista para gestión de compras/reabastecimiento - ventana modal
public class frmCompra extends Stage {
    private final BorderPane contenedorCompra = new BorderPane();

    // Controles principales
    private final ComboBox<String> cmbProductos = new ComboBox<>();
    private final TableView<ItemCompra> tablaCarrito = new TableView<>();
    private final Label lblTotal = new Label("$0.00");
    private final Button btnCompletarCompra = new Button("Completar Compra");
    private final Button btnSalir = new Button("Salir");


    public frmCompra(Stage ventanaPadre) {
        setTitle("Gestión de Compras");
        initOwner(ventanaPadre);
        initModality(Modality.APPLICATION_MODAL);
        initStyle(StageStyle.UNDECORATED);

        buildUI();
    }

    private void buildUI() {
        HBox header = buildHeader();

        VBox centro = new VBox(16);
        centro.getStyleClass().add("compras-centro-container");
        centro.setPadding(new Insets(20, 32, 24, 32));

        HBox controlsBar = createControlsBar();
        VBox tabla = createCenterPanel();

        centro.getChildren().addAll(controlsBar, tabla);

        contenedorCompra.setTop(header);
        contenedorCompra.setCenter(centro);
        contenedorCompra.setBottom(createBottomPanel());

        StackPane contenedorPrincipal = new StackPane(contenedorCompra);

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
            System.out.println("No se pudo cargar el CSS en frmCompra: " + e.getMessage());
        }

        setScene(scene);
        setResizable(false);

        setX(screenBounds.getMinX());
        setY(screenBounds.getMinY() + navBarHeight);
    }

    private HBox buildHeader() {
        Label titulo = new Label("Compras");
        titulo.getStyleClass().add("compras-titulo");

        Label subtitulo = new Label("Reabastecer inventario desde proveedores registrados");
        subtitulo.getStyleClass().add("compras-subtitulo");

        VBox textos = new VBox(4, titulo, subtitulo);

        btnSalir.getStyleClass().add("btn-salir");
        btnSalir.setOnAction(e -> close());

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        HBox header = new HBox(16, textos, spacer, btnSalir);
        header.getStyleClass().add("compras-header");
        header.setPadding(new Insets(24, 32, 16, 32));
        header.setAlignment(Pos.CENTER_LEFT);
        return header;
    }

    private HBox createControlsBar() {
        HBox controlsBar = new HBox(15);
        controlsBar.setAlignment(Pos.CENTER_LEFT);
        controlsBar.setPadding(new Insets(15, 0, 10, 0));
        controlsBar.getStyleClass().add("compras-controls-bar");

        Label lblIcon = new Label("🛒");
        lblIcon.getStyleClass().add("compras-controls-icon");

        Label lblProductos = new Label("Seleccionar Producto:");
        lblProductos.getStyleClass().add("compras-controls-label");

        cmbProductos.setPromptText("Buscar producto por proveedor...");
        cmbProductos.setPrefHeight(40);
        cmbProductos.setPrefWidth(500);
        cmbProductos.getStyleClass().add("compras-combo");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Label lblItemCount = new Label("0 items");
        lblItemCount.setId("item-count-label");
        lblItemCount.getStyleClass().add("compras-item-count");

        controlsBar.getChildren().addAll(lblIcon, lblProductos, cmbProductos, spacer, lblItemCount);
        return controlsBar;
    }

    private VBox createCenterPanel() {
        VBox centerPanel = new VBox(12);
        centerPanel.setPadding(new Insets(15, 0, 15, 0));
        VBox.setVgrow(centerPanel, Priority.ALWAYS);

        // Header de la tabla con título elegante
        HBox tableHeader = new HBox(12);
        tableHeader.setAlignment(Pos.CENTER_LEFT);
        tableHeader.setPadding(new Insets(10, 15, 10, 15));
        tableHeader.getStyleClass().add("compras-table-header");

        Label iconoTabla = new Label("📋");
        iconoTabla.getStyleClass().add("compras-table-icon");

        Label lblTabla = new Label("Productos del Carrito de Compra");
        lblTabla.getStyleClass().add("compras-table-titulo");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Label lblInfo = new Label("Agregue productos seleccionándolos del menú superior");
        lblInfo.getStyleClass().add("compras-table-info");

        tableHeader.getChildren().addAll(iconoTabla, lblTabla, spacer, lblInfo);

        // Configurar y estilizar tabla
        configureTable();
        VBox.setVgrow(tablaCarrito, Priority.ALWAYS);

        centerPanel.getChildren().addAll(tableHeader, tablaCarrito);
        return centerPanel;
    }

    private void configureTable() {
        tablaCarrito.setId("compras-tabla");
        tablaCarrito.getStyleClass().add("table-view");
        tablaCarrito.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        tablaCarrito.setFixedCellSize(60); // Altura fija para todas las filas

        Label placeholder = new Label("El carrito está vacío");
        placeholder.getStyleClass().add("compras-placeholder");
        tablaCarrito.setPlaceholder(placeholder);

        // Columna ID Proveedor
        TableColumn<ItemCompra, String> colProducto = new TableColumn<>("ID PRODUCTO");
        colProducto.setCellValueFactory(new PropertyValueFactory<>("idProducto"));
        colProducto.setMinWidth(100);
        colProducto.setStyle("-fx-alignment: CENTER; -fx-padding: 0;");
        colProducto.setCellFactory(col -> new TableCell<ItemCompra, String>() {
            private final VBox container = new VBox();
            private final Label lblId = new Label();

            {
                lblId.getStyleClass().add("compras-cell-proveedor");
                container.setAlignment(Pos.CENTER);
                container.setMinHeight(60);
                container.setMaxHeight(60);
                container.setPadding(Insets.EMPTY);
                container.getChildren().add(lblId);
                setPadding(Insets.EMPTY);
            }

            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setGraphic(null);
                } else {
                    lblId.setText(item);
                    setGraphic(container);
                }
            }
        });

        // Columna Nombre del Producto
        TableColumn<ItemCompra, String> colNombre = new TableColumn<>("PRODUCTO");
        colNombre.setCellValueFactory(new PropertyValueFactory<>("nombreProducto"));
        colNombre.setMinWidth(250);
        colNombre.setStyle("-fx-padding: 0;");
        colNombre.setCellFactory(col -> new TableCell<ItemCompra, String>() {
            private final VBox container = new VBox();
            private final Label lblNombre = new Label();

            {
                lblNombre.getStyleClass().add("compras-cell-producto");
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

        // Columna Precio Unitario con formato mejorado
        TableColumn<ItemCompra, Double> colPrecio = new TableColumn<>("PRECIO UNITARIO");
        colPrecio.setCellValueFactory(new PropertyValueFactory<>("precioUnitario"));
        colPrecio.setMinWidth(150);
        colPrecio.setStyle("-fx-padding: 0;");
        colPrecio.setCellFactory(col -> new TableCell<ItemCompra, Double>() {
            private final VBox container = new VBox();
            private final Label lblPrecio = new Label();

            {
                lblPrecio.getStyleClass().add("compras-cell-precio");
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

        // Columna Cantidad con botones +/- y campo editable
        TableColumn<ItemCompra, Integer> colCantidad = new TableColumn<>("CANTIDAD");
        colCantidad.setCellValueFactory(new PropertyValueFactory<>("cantidad"));
        colCantidad.setMinWidth(180);
        colCantidad.setStyle("-fx-alignment: CENTER; -fx-padding: 0;");
        colCantidad.setCellFactory(col -> new TableCell<ItemCompra, Integer>() {
            private final HBox container = new HBox(10);
            private final Button btnMenos = new Button("−");
            private final TextField txtCantidad = new TextField();
            private final Button btnMas = new Button("+");

            {
                // Aplicar clases CSS a los botones
                btnMenos.getStyleClass().add("compras-cantidad-btn");
                btnMas.getStyleClass().add("compras-cantidad-btn");

                // Configurar campo de texto personalizado
                txtCantidad.setPrefWidth(80);
                txtCantidad.setPrefHeight(38);
                txtCantidad.setAlignment(Pos.CENTER);
                txtCantidad.getStyleClass().add("compras-cantidad-field");

                container.setAlignment(Pos.CENTER);
                container.setMinHeight(60);
                container.setMaxHeight(60);
                container.setPadding(Insets.EMPTY);
                container.getChildren().addAll(btnMenos, txtCantidad, btnMas);
                setPadding(Insets.EMPTY);

                // Eventos de los botones
                btnMenos.setOnAction(e -> {
                    try {
                        int currentValue = Integer.parseInt(txtCantidad.getText());
                        if (currentValue > 1) {
                            txtCantidad.setText(String.valueOf(currentValue - 1));
                            actualizarCantidad();
                        }
                    } catch (NumberFormatException ignored) {}
                });

                btnMas.setOnAction(e -> {
                    try {
                        int currentValue = Integer.parseInt(txtCantidad.getText());
                        if (currentValue < 999) {
                            txtCantidad.setText(String.valueOf(currentValue + 1));
                            actualizarCantidad();
                        }
                    } catch (NumberFormatException ignored) {}
                });

                // Listener para validar entrada manual
                txtCantidad.textProperty().addListener((obs, oldVal, newVal) -> {
                    if (!newVal.matches("\\d*")) {
                        txtCantidad.setText(oldVal);
                    } else if (!newVal.isEmpty()) {
                        try {
                            int valor = Integer.parseInt(newVal);
                            if (valor > 999) {
                                txtCantidad.setText("999");
                            } else if (valor < 1) {
                                txtCantidad.setText("1");
                            }
                        } catch (NumberFormatException e) {
                            txtCantidad.setText(oldVal);
                        }
                    }
                });

                // Actualizar al perder foco
                txtCantidad.focusedProperty().addListener((obs, oldVal, newVal) -> {
                    if (!newVal) { // Perdió el foco
                        if (txtCantidad.getText().isEmpty()) {
                            txtCantidad.setText("1");
                        }
                        actualizarCantidad();
                    }
                });
            }

            private void actualizarCantidad() {
                if (getTableRow() != null && getTableRow().getItem() != null) {
                    try {
                        int nuevaCantidad = Integer.parseInt(txtCantidad.getText());
                        ItemCompra item = getTableRow().getItem();
                        item.setCantidad(nuevaCantidad);
                        getTableView().refresh();
                        if (onCantidadChanged != null) {
                            onCantidadChanged.run();
                        }
                    } catch (NumberFormatException ignored) {}
                }
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


        // Columna Acciones (botón eliminar mejorado)
        TableColumn<ItemCompra, Void> colAcciones = new TableColumn<>("ACCIONES");
        colAcciones.setMinWidth(160);
        colAcciones.setMaxWidth(160);
        colAcciones.setResizable(false);
        colAcciones.setStyle("-fx-alignment: CENTER; -fx-padding: 0;");
        colAcciones.setCellFactory(col -> new TableCell<ItemCompra, Void>() {
            private final VBox container = new VBox();
            private final Button btnEliminar = new Button("🗑 Eliminar");

            {
                btnEliminar.getStyleClass().add("compras-btn-eliminar");

                container.setAlignment(Pos.CENTER);
                container.setMinHeight(60);
                container.setMaxHeight(60);
                container.setPadding(Insets.EMPTY);
                container.getChildren().add(btnEliminar);
                setPadding(Insets.EMPTY);

                btnEliminar.setOnAction(e -> {
                    ItemCompra item = getTableView().getItems().get(getIndex());
                    getTableView().getItems().remove(item);
                    // Notificar al listener externo si existe
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

        tablaCarrito.getColumns().addAll(colProducto, colNombre, colPrecio, colCantidad, colAcciones);
    }

    // Callback para notificar cambios en las cantidades
    private Runnable onCantidadChanged;

    public void setOnCantidadChanged(Runnable callback) {
        this.onCantidadChanged = callback;
    }

    private HBox createBottomPanel() {
        HBox bottomPanel = new HBox(20);
        bottomPanel.setPadding(new Insets(16, 32, 24, 32));
        bottomPanel.setAlignment(Pos.CENTER);
        bottomPanel.getStyleClass().add("compras-bottom-panel");

        // Sección izquierda - Información
        VBox infoSection = new VBox(8);
        infoSection.setAlignment(Pos.CENTER_LEFT);

        Label lblInfo = new Label("💡 Información");
        lblInfo.getStyleClass().add("compras-info-titulo");

        Label lblDetalle = new Label("• El inventario se actualizará automáticamente\n• Los productos nuevos se agregarán al sistema\n• Se generará un registro de la compra");
        lblDetalle.getStyleClass().add("compras-info-detalle");

        infoSection.getChildren().addAll(lblInfo, lblDetalle);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        // Sección central - Total
        VBox totalSection = new VBox(5);
        totalSection.setAlignment(Pos.CENTER);
        totalSection.getStyleClass().add("compras-total-section");

        Label lblTotalTxt = new Label("TOTAL A PAGAR");
        lblTotalTxt.getStyleClass().add("compras-total-label");

        lblTotal.getStyleClass().add("compras-total-monto");

        totalSection.getChildren().addAll(lblTotalTxt, lblTotal);

        // Sección derecha - Botón
        VBox buttonSection = new VBox(8);
        buttonSection.setAlignment(Pos.CENTER_RIGHT);
        buttonSection.setPrefWidth(300);

        btnCompletarCompra.setText("✓  COMPLETAR COMPRA");
        btnCompletarCompra.setPrefWidth(280);
        btnCompletarCompra.setPrefHeight(60);
        btnCompletarCompra.getStyleClass().add("compras-btn-completar");
        btnCompletarCompra.setDisable(true);

        Label lblHint = new Label("Presione para confirmar la orden");
        lblHint.getStyleClass().add("compras-btn-hint");
        lblHint.setAlignment(Pos.CENTER);

        buttonSection.getChildren().addAll(btnCompletarCompra, lblHint);

        bottomPanel.getChildren().addAll(infoSection, spacer, totalSection, buttonSection);
        return bottomPanel;
    }

    // Getters para el controlador
    public ComboBox<String> getCmbProductos() {
        return cmbProductos;
    }

    public TableView<ItemCompra> getTablaCarrito() {
        return tablaCarrito;
    }

    public Label getLblTotal() {
        return lblTotal;
    }

    public Button getBtnCompletarCompra() {
        return btnCompletarCompra;
    }

    public Button getBtnCerrar() {
        return btnSalir;
    }
}

