package view;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import model.ModeloVenta.ItemVenta;

// Vista para gestión de ventas - ventana modal
public class frmVenta extends Stage {
    // Controles principales
    private final ComboBox<String> cmbClientes = new ComboBox<>();
    private final ComboBox<String> cmbProductos = new ComboBox<>();
    private final TableView<ItemVenta> tablaCarrito = new TableView<>();
    private final TextField txtDescuento = new TextField("0");
    private final Label lblTotalParcial = new Label("$0.00");
    private final Label lblTotal = new Label("$0.00");
    private final Button btnCompletarVenta = new Button("Venta completa");
    private final Button btnCerrar = new Button("← Atrás");

    // Referencia al stage principal
    private final Stage stagePadre;

    public frmVenta(Stage padre) {
        this.stagePadre = padre;
        initOwner(padre);
        initModality(Modality.WINDOW_MODAL);
        initStyle(StageStyle.UNDECORATED);

        setTitle("Nueva Venta");
        buildUI();
    }

    private void buildUI() {
        // Contenedor principal
        BorderPane root = new BorderPane();
        root.setPadding(new Insets(20));
        root.setId("dialog-pane");

        // Panel superior con título y botón cerrar
        HBox header = createHeader();
        root.setTop(header);

        // Contenedor central con dos paneles
        HBox centerContainer = new HBox(20);
        centerContainer.setPadding(new Insets(20, 0, 0, 0));

        // Panel izquierdo - Detalles de la venta
        VBox leftPanel = createLeftPanel();

        // Panel derecho - Resumen
        VBox rightPanel = createRightPanel();

        centerContainer.getChildren().addAll(leftPanel, rightPanel);
        root.setCenter(centerContainer);

        Scene scene = new Scene(root, 1200, 700);

        // Cargar estilos CSS
        try {
            String css = getClass().getResource("/styles.css").toExternalForm();
            scene.getStylesheets().add(css);
        } catch (Exception ignored) {}

        setScene(scene);
    }

    private HBox createHeader() {
        HBox header = new HBox(15);
        header.setAlignment(Pos.CENTER_LEFT);
        header.setPadding(new Insets(0, 0, 20, 0));

        btnCerrar.getStyleClass().add("btn-salir");
        btnCerrar.setOnAction(e -> close());

        VBox titleBox = new VBox(5);
        Label title = new Label("Nueva oferta");
        title.setStyle("-fx-font-size: 28px; -fx-font-weight: bold; -fx-text-fill: white;");

        Label subtitle = new Label("Crear una nueva transacción de venta");
        subtitle.setStyle("-fx-font-size: 14px; -fx-text-fill: #a0a0a0;");

        titleBox.getChildren().addAll(title, subtitle);

        header.getChildren().addAll(btnCerrar, titleBox);
        return header;
    }

    private VBox createLeftPanel() {
        VBox leftPanel = new VBox(15);
        leftPanel.setPrefWidth(750);
        leftPanel.setPadding(new Insets(25));
        leftPanel.setStyle("-fx-background-color: #2b2b2b; -fx-background-radius: 12; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.3), 10, 0, 0, 2);");

        // Título del panel con separador
        VBox headerBox = new VBox(10);
        Label lblDetalles = new Label("Detalles de la venta");
        lblDetalles.setStyle("-fx-font-size: 22px; -fx-font-weight: bold; -fx-text-fill: white;");

        Separator separator1 = new Separator();
        separator1.setStyle("-fx-background-color: #444444;");

        headerBox.getChildren().addAll(lblDetalles, separator1);

        // ComboBox de clientes con estilo mejorado
        VBox clienteBox = new VBox(10);
        Label lblCliente = new Label("Cliente *");
        lblCliente.setStyle("-fx-text-fill: #e0e0e0; -fx-font-size: 13px; -fx-font-weight: bold;");

        cmbClientes.setPromptText("Seleccione un cliente");
        cmbClientes.setPrefHeight(40);
        cmbClientes.setPrefWidth(Double.MAX_VALUE);
        cmbClientes.setStyle("-fx-background-color: #1e1e1e; -fx-text-fill: white; -fx-prompt-text-fill: #888888; -fx-font-size: 14px;");

        clienteBox.getChildren().addAll(lblCliente, cmbClientes);

        // ComboBox de productos con icono
        VBox productosBox = new VBox(10);
        HBox lblProductosContainer = new HBox(8);
        lblProductosContainer.setAlignment(Pos.CENTER_LEFT);

        Label iconoProducto = new Label("🛒");
        iconoProducto.setStyle("-fx-font-size: 16px;");

        Label lblProductos = new Label("Agregar productos al carrito");
        lblProductos.setStyle("-fx-text-fill: #e0e0e0; -fx-font-size: 13px; -fx-font-weight: bold;");

        lblProductosContainer.getChildren().addAll(iconoProducto, lblProductos);

        cmbProductos.setPromptText("Buscar y seleccionar producto...");
        cmbProductos.setPrefHeight(40);
        cmbProductos.setPrefWidth(Double.MAX_VALUE);
        cmbProductos.setStyle("-fx-background-color: #1e1e1e; -fx-text-fill: white; -fx-prompt-text-fill: #888888; -fx-font-size: 14px;");

        productosBox.getChildren().addAll(lblProductosContainer, cmbProductos);

        // Separador visual
        Separator separator2 = new Separator();
        separator2.setStyle("-fx-background-color: #444444;");

        // Tabla de artículos del carrito
        VBox carritoBox = new VBox(12);
        HBox carritoHeader = new HBox(10);
        carritoHeader.setAlignment(Pos.CENTER_LEFT);

        Label iconoCarrito = new Label("📦");
        iconoCarrito.setStyle("-fx-font-size: 18px;");

        Label lblCarrito = new Label("Carrito de compras");
        lblCarrito.setStyle("-fx-text-fill: white; -fx-font-size: 16px; -fx-font-weight: bold;");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Label lblItemCount = new Label("0 items");
        lblItemCount.setId("item-count-label");
        lblItemCount.setStyle("-fx-text-fill: #4CAF50; -fx-font-size: 12px; -fx-font-weight: bold; -fx-background-color: rgba(76, 175, 80, 0.2); -fx-padding: 4 8 4 8; -fx-background-radius: 10;");

        carritoHeader.getChildren().addAll(iconoCarrito, lblCarrito, spacer, lblItemCount);

        configureTable();

        carritoBox.getChildren().addAll(carritoHeader, tablaCarrito);
        VBox.setVgrow(carritoBox, Priority.ALWAYS);

        leftPanel.getChildren().addAll(headerBox, clienteBox, productosBox, separator2, carritoBox);
        return leftPanel;
    }

    private void configureTable() {
        tablaCarrito.setPrefHeight(350);
        tablaCarrito.getStyleClass().add("table-view");
        tablaCarrito.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        tablaCarrito.setPlaceholder(new Label("No hay productos en el carrito"));

        // Columna Nombre del Producto
        TableColumn<ItemVenta, String> colNombre = new TableColumn<>("Producto");
        colNombre.setCellValueFactory(new PropertyValueFactory<>("nombreProducto"));
        colNombre.setMinWidth(200);
        colNombre.setStyle("-fx-font-size: 13px; -fx-font-weight: bold;");

        // Columna Precio Unitario con formato mejorado
        TableColumn<ItemVenta, Double> colPrecio = new TableColumn<>("Precio Unit.");
        colPrecio.setCellValueFactory(new PropertyValueFactory<>("precioUnitario"));
        colPrecio.setMinWidth(90);
        colPrecio.setStyle("-fx-alignment: CENTER-RIGHT;");
        colPrecio.setCellFactory(col -> new TableCell<ItemVenta, Double>() {
            @Override
            protected void updateItem(Double item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(String.format("$%.2f", item));
                    setStyle("-fx-alignment: CENTER-RIGHT; -fx-text-fill: #90EE90;");
                }
            }
        });

        // Columna Cantidad con botones +/- y campo editable
        TableColumn<ItemVenta, Integer> colCantidad = new TableColumn<>("Cantidad");
        colCantidad.setCellValueFactory(new PropertyValueFactory<>("cantidad"));
        colCantidad.setMinWidth(140);
        colCantidad.setStyle("-fx-alignment: CENTER;");
        colCantidad.setCellFactory(col -> new TableCell<ItemVenta, Integer>() {
            private final HBox container = new HBox(5);
            private final Button btnMenos = new Button("-");
            private final Spinner<Integer> spinner = new Spinner<>(1, 999, 1);
            private final Button btnMas = new Button("+");

            {
                // Configurar botones
                btnMenos.setStyle("-fx-background-color: #dc3545; -fx-text-fill: white; -fx-font-weight: bold; -fx-cursor: hand; -fx-min-width: 30px; -fx-pref-width: 30px;");
                btnMas.setStyle("-fx-background-color: #28a745; -fx-text-fill: white; -fx-font-weight: bold; -fx-cursor: hand; -fx-min-width: 30px; -fx-pref-width: 30px;");

                // Configurar spinner
                spinner.setEditable(true);
                spinner.setPrefWidth(60);
                spinner.getStyleClass().add(Spinner.STYLE_CLASS_SPLIT_ARROWS_HORIZONTAL);

                container.setAlignment(Pos.CENTER);
                container.getChildren().addAll(btnMenos, spinner, btnMas);

                // Eventos de los botones
                btnMenos.setOnAction(e -> {
                    int currentValue = spinner.getValue();
                    if (currentValue > 1) {
                        spinner.getValueFactory().setValue(currentValue - 1);
                    }
                });

                btnMas.setOnAction(e -> {
                    int currentValue = spinner.getValue();
                    if (currentValue < 999) {
                        spinner.getValueFactory().setValue(currentValue + 1);
                    }
                });

                // Listener para actualizar el item cuando cambia el valor
                spinner.valueProperty().addListener((obs, oldVal, newVal) -> {
                    if (getTableRow() != null && getTableRow().getItem() != null) {
                        ItemVenta item = (ItemVenta) getTableRow().getItem();
                        item.setCantidad(newVal);
                        getTableView().refresh();
                        // Notificar al listener externo si existe
                        if (onCantidadChanged != null) {
                            onCantidadChanged.run();
                        }
                    }
                });
            }

            @Override
            protected void updateItem(Integer item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setGraphic(null);
                } else {
                    spinner.getValueFactory().setValue(item);
                    setGraphic(container);
                }
            }
        });

        // Columna Subtotal con formato mejorado
        TableColumn<ItemVenta, Double> colSubtotal = new TableColumn<>("Subtotal");
        colSubtotal.setCellValueFactory(new PropertyValueFactory<>("subtotal"));
        colSubtotal.setMinWidth(100);
        colSubtotal.setStyle("-fx-alignment: CENTER-RIGHT;");
        colSubtotal.setCellFactory(col -> new TableCell<ItemVenta, Double>() {
            @Override
            protected void updateItem(Double item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(String.format("$%.2f", item));
                    setStyle("-fx-alignment: CENTER-RIGHT; -fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #4CAF50;");
                }
            }
        });

        // Columna Acciones (botón eliminar mejorado)
        TableColumn<ItemVenta, Void> colAcciones = new TableColumn<>("");
        colAcciones.setMinWidth(60);
        colAcciones.setMaxWidth(60);
        colAcciones.setResizable(false);
        colAcciones.setStyle("-fx-alignment: CENTER;");
        colAcciones.setCellFactory(col -> new TableCell<ItemVenta, Void>() {
            private final Button btnEliminar = new Button("🗑");

            {
                btnEliminar.setStyle("-fx-background-color: #dc3545; -fx-text-fill: white; -fx-cursor: hand; -fx-font-size: 14px; -fx-padding: 5 10 5 10; -fx-background-radius: 5;");
                btnEliminar.setOnMouseEntered(e -> btnEliminar.setStyle("-fx-background-color: #c82333; -fx-text-fill: white; -fx-cursor: hand; -fx-font-size: 14px; -fx-padding: 5 10 5 10; -fx-background-radius: 5;"));
                btnEliminar.setOnMouseExited(e -> btnEliminar.setStyle("-fx-background-color: #dc3545; -fx-text-fill: white; -fx-cursor: hand; -fx-font-size: 14px; -fx-padding: 5 10 5 10; -fx-background-radius: 5;"));

                btnEliminar.setOnAction(e -> {
                    ItemVenta item = getTableView().getItems().get(getIndex());
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
                    setGraphic(btnEliminar);
                }
            }
        });

        tablaCarrito.getColumns().addAll(colNombre, colPrecio, colCantidad, colSubtotal, colAcciones);
    }

    // Callback para notificar cambios en las cantidades
    private Runnable onCantidadChanged;

    public void setOnCantidadChanged(Runnable callback) {
        this.onCantidadChanged = callback;
    }

    private VBox createRightPanel() {
        VBox rightPanel = new VBox(20);
        rightPanel.setPrefWidth(420);
        rightPanel.setPadding(new Insets(25));
        rightPanel.setStyle("-fx-background-color: #2b2b2b; -fx-background-radius: 12; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.3), 10, 0, 0, 2);");

        // Título del panel con icono
        VBox headerBox = new VBox(10);
        HBox titleContainer = new HBox(10);
        titleContainer.setAlignment(Pos.CENTER_LEFT);

        Label iconoResumen = new Label("💰");
        iconoResumen.setStyle("-fx-font-size: 22px;");

        Label lblResumen = new Label("Resumen de la venta");
        lblResumen.setStyle("-fx-font-size: 22px; -fx-font-weight: bold; -fx-text-fill: white;");

        titleContainer.getChildren().addAll(iconoResumen, lblResumen);

        Separator separator = new Separator();
        separator.setStyle("-fx-background-color: #444444;");

        headerBox.getChildren().addAll(titleContainer, separator);

        // Sección de cálculos
        VBox calculosBox = new VBox(15);
        calculosBox.setPadding(new Insets(10, 0, 10, 0));

        // Total parcial (subtotal)
        VBox subtotalContainer = new VBox(5);
        HBox subtotalRow = new HBox();
        subtotalRow.setAlignment(Pos.CENTER_LEFT);
        HBox.setHgrow(subtotalRow, Priority.ALWAYS);

        Label lblSubtotalTxt = new Label("Subtotal");
        lblSubtotalTxt.setStyle("-fx-text-fill: #b0b0b0; -fx-font-size: 13px;");

        lblTotalParcial.setStyle("-fx-text-fill: white; -fx-font-size: 18px; -fx-font-weight: bold;");

        Region spacer1 = new Region();
        HBox.setHgrow(spacer1, Priority.ALWAYS);
        subtotalRow.getChildren().addAll(lblSubtotalTxt, spacer1, lblTotalParcial);
        subtotalContainer.getChildren().add(subtotalRow);

        // Descuento con diseño mejorado
        VBox descuentoBox = new VBox(8);
        Label lblDescuento = new Label("💸 Descuento (opcional)");
        lblDescuento.setStyle("-fx-text-fill: #b0b0b0; -fx-font-size: 13px; -fx-font-weight: bold;");

        HBox descuentoInputBox = new HBox(5);
        descuentoInputBox.setAlignment(Pos.CENTER_LEFT);

        Label simboloDolar = new Label("$");
        simboloDolar.setStyle("-fx-text-fill: #4CAF50; -fx-font-size: 16px; -fx-font-weight: bold;");

        txtDescuento.setPrefWidth(Double.MAX_VALUE);
        txtDescuento.setPrefHeight(38);
        txtDescuento.setStyle("-fx-background-color: #1e1e1e; -fx-text-fill: white; -fx-prompt-text-fill: #888888; -fx-font-size: 14px; -fx-padding: 8;");
        txtDescuento.setPromptText("0.00");

        descuentoInputBox.getChildren().addAll(simboloDolar, txtDescuento);
        descuentoBox.getChildren().addAll(lblDescuento, descuentoInputBox);

        calculosBox.getChildren().addAll(subtotalContainer, descuentoBox);

        // Separador antes del total
        Separator separadorTotal = new Separator();
        separadorTotal.setStyle("-fx-background-color: #4CAF50; -fx-padding: 2 0 2 0;");

        // Total final destacado
        VBox totalContainer = new VBox(10);
        totalContainer.setPadding(new Insets(15, 0, 15, 0));
        totalContainer.setStyle("-fx-background-color: rgba(76, 175, 80, 0.1); -fx-background-radius: 8; -fx-padding: 15;");

        HBox totalRow = new HBox();
        totalRow.setAlignment(Pos.CENTER_LEFT);
        HBox.setHgrow(totalRow, Priority.ALWAYS);

        Label lblTotalTxt = new Label("Total a pagar");
        lblTotalTxt.setStyle("-fx-text-fill: #4CAF50; -fx-font-size: 16px; -fx-font-weight: bold;");

        lblTotal.setStyle("-fx-text-fill: #4CAF50; -fx-font-size: 32px; -fx-font-weight: bold;");

        Region spacer2 = new Region();
        HBox.setHgrow(spacer2, Priority.ALWAYS);
        totalRow.getChildren().addAll(lblTotalTxt, spacer2, lblTotal);
        totalContainer.getChildren().add(totalRow);

        // Espaciador flexible
        Region spacer3 = new Region();
        VBox.setVgrow(spacer3, Priority.ALWAYS);

        // Botón completar venta mejorado
        VBox buttonContainer = new VBox(10);

        btnCompletarVenta.setText("✓  Completar Venta");
        btnCompletarVenta.setPrefWidth(Double.MAX_VALUE);
        btnCompletarVenta.setPrefHeight(55);
        btnCompletarVenta.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-font-size: 16px; -fx-font-weight: bold; -fx-cursor: hand; -fx-background-radius: 8;");
        btnCompletarVenta.setDisable(true);

        // Efecto hover
        btnCompletarVenta.setOnMouseEntered(e -> {
            if (!btnCompletarVenta.isDisabled()) {
                btnCompletarVenta.setStyle("-fx-background-color: #45a049; -fx-text-fill: white; -fx-font-size: 16px; -fx-font-weight: bold; -fx-cursor: hand; -fx-background-radius: 8; -fx-effect: dropshadow(gaussian, rgba(76, 175, 80, 0.6), 15, 0, 0, 0);");
            }
        });
        btnCompletarVenta.setOnMouseExited(e -> {
            if (!btnCompletarVenta.isDisabled()) {
                btnCompletarVenta.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-font-size: 16px; -fx-font-weight: bold; -fx-cursor: hand; -fx-background-radius: 8;");
            }
        });

        Label lblInfo = new Label("El stock se actualizará automáticamente");
        lblInfo.setStyle("-fx-text-fill: #808080; -fx-font-size: 11px; -fx-text-alignment: center;");
        lblInfo.setAlignment(Pos.CENTER);
        lblInfo.setWrapText(true);

        buttonContainer.getChildren().addAll(btnCompletarVenta, lblInfo);

        rightPanel.getChildren().addAll(
            headerBox,
            calculosBox,
            separadorTotal,
            totalContainer,
            spacer3,
            buttonContainer
        );

        return rightPanel;
    }

    // Getters para el controlador
    public ComboBox<String> getCmbClientes() {
        return cmbClientes;
    }

    public ComboBox<String> getCmbProductos() {
        return cmbProductos;
    }

    public TableView<ItemVenta> getTablaCarrito() {
        return tablaCarrito;
    }

    public TextField getTxtDescuento() {
        return txtDescuento;
    }

    public Label getLblTotalParcial() {
        return lblTotalParcial;
    }

    public Label getLblTotal() {
        return lblTotal;
    }

    public Button getBtnCompletarVenta() {
        return btnCompletarVenta;
    }

    public Button getBtnCerrar() {
        return btnCerrar;
    }
}

