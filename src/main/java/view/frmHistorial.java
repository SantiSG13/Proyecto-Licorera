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
import model.ModeloVenta;
import model.ModeloCompra;

// Vista para el historial de ventas y compras - ventana modal
public class frmHistorial extends Stage {
    private final BorderPane contenedorHistorial = new BorderPane();

    // Pestañas para separar ventas y compras
    private final TabPane tabPane = new TabPane();
    private final Tab tabVentas = new Tab("Historial de Ventas");
    private final Tab tabCompras = new Tab("Historial de Compras");

    // Tabla de ventas
    private final TableView<ModeloVenta> tablaVentas = new TableView<>();

    // Tabla de compras
    private final TableView<ModeloCompra> tablaCompras = new TableView<>();

    // Botón de salir
    private final Button btnSalir = new Button("Salir");

    public frmHistorial(Stage ventanaPadre) {
        setTitle("Historial de Ventas y Compras");
        initOwner(ventanaPadre);
        initModality(Modality.APPLICATION_MODAL);
        initStyle(StageStyle.UNDECORATED);

        buildUI();
    }

    private void buildUI() {
        HBox header = buildHeader();

        VBox centro = new VBox(16);
        centro.getStyleClass().add("historial-centro-container");
        centro.setPadding(new Insets(20, 32, 24, 32));

        // Configurar pestañas
        tabVentas.setClosable(false);
        tabCompras.setClosable(false);

        // Configurar tabla de ventas
        VBox ventasContent = createVentasContent();
        tabVentas.setContent(ventasContent);

        // Configurar tabla de compras
        VBox comprasContent = createComprasContent();
        tabCompras.setContent(comprasContent);

        tabPane.getTabs().addAll(tabVentas, tabCompras);
        tabPane.getStyleClass().add("historial-tab-pane");

        centro.getChildren().add(tabPane);

        contenedorHistorial.setTop(header);
        contenedorHistorial.setCenter(centro);
        contenedorHistorial.setBottom(createBottomPanel());

        Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();
        Scene scene = new Scene(contenedorHistorial, screenBounds.getWidth() * 0.85,
                screenBounds.getHeight() * 0.85);
        scene.getStylesheets().add(getClass().getResource("/styles.css").toExternalForm());
        setScene(scene);
    }

    private HBox buildHeader() {
        HBox header = new HBox();
        header.setAlignment(Pos.CENTER);
        header.getStyleClass().add("historial-header");

        Label titulo = new Label("Historial de Ventas y Compras");
        titulo.getStyleClass().add("historial-titulo");

        header.getChildren().add(titulo);

        return header;
    }

    private VBox createVentasContent() {
        VBox content = new VBox(10);
        content.setPadding(new Insets(10));

        Label lblTitulo = new Label("Registro de Ventas");
        lblTitulo.getStyleClass().add("historial-seccion-titulo");

        // Configurar columnas de la tabla de ventas
        TableColumn<ModeloVenta, String> colId = new TableColumn<>("ID Venta");
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colId.getStyleClass().add("tabla-col-center");

        TableColumn<ModeloVenta, String> colFecha = new TableColumn<>("Fecha");
        colFecha.setCellValueFactory(new PropertyValueFactory<>("fecha"));
        colFecha.getStyleClass().add("tabla-col-center");

        TableColumn<ModeloVenta, String> colDocumentoCliente = new TableColumn<>("Documento Cliente");
        colDocumentoCliente.setCellValueFactory(new PropertyValueFactory<>("documentoCliente"));
        colDocumentoCliente.getStyleClass().add("tabla-col-center");

        TableColumn<ModeloVenta, String> colNombreCliente = new TableColumn<>("Nombre Cliente");
        colNombreCliente.setCellValueFactory(new PropertyValueFactory<>("nombreCliente"));
        colNombreCliente.getStyleClass().add("tabla-col-center");

        TableColumn<ModeloVenta, Double> colTotal = new TableColumn<>("Total");
        colTotal.setCellValueFactory(new PropertyValueFactory<>("total"));
        colTotal.getStyleClass().add("tabla-col-center");
        colTotal.setCellFactory(col -> new TableCell<ModeloVenta, Double>() {
            @Override
            protected void updateItem(Double item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(String.format("$%.2f", item));
                }
            }
        });

        // Botón para ver detalles de la venta
        TableColumn<ModeloVenta, Void> colAcciones = new TableColumn<>("Acciones");
        colAcciones.setCellFactory(col -> new TableCell<ModeloVenta, Void>() {
            private final Button btnDetalles = new Button("Ver Detalles");

            {
                btnDetalles.getStyleClass().add("btn-detalles");
                btnDetalles.setOnAction(e -> {
                    ModeloVenta venta = getTableView().getItems().get(getIndex());
                    mostrarDetallesVenta(venta);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(btnDetalles);
                }
            }
        });

        tablaVentas.getColumns().addAll(colId, colFecha, colDocumentoCliente, colNombreCliente, colTotal, colAcciones);
        tablaVentas.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        tablaVentas.getStyleClass().add("historial-tabla");

        VBox.setVgrow(tablaVentas, Priority.ALWAYS);

        content.getChildren().addAll(lblTitulo, tablaVentas);

        return content;
    }

    private VBox createComprasContent() {
        VBox content = new VBox(10);
        content.setPadding(new Insets(10));

        Label lblTitulo = new Label("Registro de Compras");
        lblTitulo.getStyleClass().add("historial-seccion-titulo");

        // Configurar columnas de la tabla de compras
        TableColumn<ModeloCompra, String> colId = new TableColumn<>("ID Compra");
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colId.getStyleClass().add("tabla-col-center");

        TableColumn<ModeloCompra, String> colFecha = new TableColumn<>("Fecha");
        colFecha.setCellValueFactory(new PropertyValueFactory<>("fecha"));
        colFecha.getStyleClass().add("tabla-col-center");

        TableColumn<ModeloCompra, Double> colTotal = new TableColumn<>("Total");
        colTotal.setCellValueFactory(new PropertyValueFactory<>("total"));
        colTotal.getStyleClass().add("tabla-col-center");
        colTotal.setCellFactory(col -> new TableCell<ModeloCompra, Double>() {
            @Override
            protected void updateItem(Double item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(String.format("$%.2f", item));
                }
            }
        });

        // Botón para ver detalles de la compra
        TableColumn<ModeloCompra, Void> colAcciones = new TableColumn<>("Acciones");
        colAcciones.setCellFactory(col -> new TableCell<ModeloCompra, Void>() {
            private final Button btnDetalles = new Button("Ver Detalles");

            {
                btnDetalles.getStyleClass().add("btn-detalles");
                btnDetalles.setOnAction(e -> {
                    ModeloCompra compra = getTableView().getItems().get(getIndex());
                    mostrarDetallesCompra(compra);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(btnDetalles);
                }
            }
        });

        tablaCompras.getColumns().addAll(colId, colFecha, colTotal, colAcciones);
        tablaCompras.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        tablaCompras.getStyleClass().add("historial-tabla");

        VBox.setVgrow(tablaCompras, Priority.ALWAYS);

        content.getChildren().addAll(lblTitulo, tablaCompras);

        return content;
    }

    private HBox createBottomPanel() {
        HBox bottomPanel = new HBox();
        bottomPanel.setAlignment(Pos.CENTER);
        bottomPanel.getStyleClass().add("historial-bottom-panel");

        btnSalir.getStyleClass().add("btn-salir");

        bottomPanel.getChildren().add(btnSalir);

        return bottomPanel;
    }

    // Método para mostrar detalles de una venta en un diálogo modal estilo RECIBO
    private void mostrarDetallesVenta(ModeloVenta venta) {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Recibo de Venta");

        // Aplicar estilos CSS
        try {
            dialog.getDialogPane().getStylesheets().add(
                    getClass().getResource("/styles.css").toExternalForm());
        } catch (Exception ex) {
            System.out.println("No se pudo cargar CSS para el diálogo");
        }

        // Contenedor principal estilo papel/recibo
        VBox recibo = new VBox(0);
        recibo.setStyle(
                "-fx-background-color: #fefdf8;" +
                        "-fx-padding: 30 40 30 40;" +
                        "-fx-min-width: 420px;" +
                        "-fx-max-width: 420px;" +
                        "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.3), 15, 0, 0, 5);");

        // ═══════════════════════════════════════════════════════════════
        // ENCABEZADO DEL RECIBO
        // ═══════════════════════════════════════════════════════════════
        Label lblNegocio = new Label("MOE'S TAVERN");
        lblNegocio.setStyle(
                "-fx-font-family: 'Courier New', monospace; -fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #1a1a1a;");
        lblNegocio.setAlignment(Pos.CENTER);
        lblNegocio.setMaxWidth(Double.MAX_VALUE);

        Label lblDireccion = new Label("Springfield, IL 62704");
        lblDireccion
                .setStyle("-fx-font-family: 'Courier New', monospace; -fx-font-size: 11px; -fx-text-fill: #555555;");
        lblDireccion.setAlignment(Pos.CENTER);
        lblDireccion.setMaxWidth(Double.MAX_VALUE);

        Label lblTelefono = new Label("Tel: (555) 123-4567");
        lblTelefono.setStyle("-fx-font-family: 'Courier New', monospace; -fx-font-size: 11px; -fx-text-fill: #555555;");
        lblTelefono.setAlignment(Pos.CENTER);
        lblTelefono.setMaxWidth(Double.MAX_VALUE);

        Label lblNit = new Label("NIT: 900.123.456-7");
        lblNit.setStyle("-fx-font-family: 'Courier New', monospace; -fx-font-size: 11px; -fx-text-fill: #555555;");
        lblNit.setAlignment(Pos.CENTER);
        lblNit.setMaxWidth(Double.MAX_VALUE);

        // Separador superior
        Label sep1 = crearSeparadorRecibo();

        // ═══════════════════════════════════════════════════════════════
        // INFORMACIÓN DE LA VENTA
        // ═══════════════════════════════════════════════════════════════
        Label lblTipoDoc = new Label("*** FACTURA DE VENTA ***");
        lblTipoDoc.setStyle(
                "-fx-font-family: 'Courier New', monospace; -fx-font-size: 12px; -fx-font-weight: bold; -fx-text-fill: #1a1a1a;");
        lblTipoDoc.setAlignment(Pos.CENTER);
        lblTipoDoc.setMaxWidth(Double.MAX_VALUE);

        Label lblNumVenta = new Label("No. " + venta.getId());
        lblNumVenta.setStyle(
                "-fx-font-family: 'Courier New', monospace; -fx-font-size: 12px; -fx-font-weight: bold; -fx-text-fill: #1a1a1a;");
        lblNumVenta.setAlignment(Pos.CENTER);
        lblNumVenta.setMaxWidth(Double.MAX_VALUE);

        Label sep2 = crearSeparadorRecibo();

        // Fecha y hora
        Label lblFecha = new Label("Fecha: " + venta.getFecha());
        lblFecha.setStyle("-fx-font-family: 'Courier New', monospace; -fx-font-size: 11px; -fx-text-fill: #1a1a1a;");

        // Cliente
        Label lblCliente = new Label("Cliente: " + venta.getNombreCliente());
        lblCliente.setStyle("-fx-font-family: 'Courier New', monospace; -fx-font-size: 11px; -fx-text-fill: #1a1a1a;");

        Label lblDocCliente = new Label("Doc: " + venta.getDocumentoCliente());
        lblDocCliente
                .setStyle("-fx-font-family: 'Courier New', monospace; -fx-font-size: 11px; -fx-text-fill: #1a1a1a;");

        Label sep3 = crearSeparadorRecibo();

        // ═══════════════════════════════════════════════════════════════
        // DETALLE DE PRODUCTOS
        // ═══════════════════════════════════════════════════════════════
        // Encabezado de productos
        HBox headerProductos = new HBox();
        headerProductos.setSpacing(0);
        Label hId = new Label("ID");
        hId.setStyle(
                "-fx-font-family: 'Courier New', monospace; -fx-font-size: 10px; -fx-font-weight: bold; -fx-text-fill: #1a1a1a;");
        hId.setPrefWidth(60);
        hId.setAlignment(Pos.CENTER);
        Label hProd = new Label("PRODUCTO");
        hProd.setStyle(
                "-fx-font-family: 'Courier New', monospace; -fx-font-size: 10px; -fx-font-weight: bold; -fx-text-fill: #1a1a1a;");
        hProd.setPrefWidth(120);
        Label hCant = new Label("CANT");
        hCant.setStyle(
                "-fx-font-family: 'Courier New', monospace; -fx-font-size: 10px; -fx-font-weight: bold; -fx-text-fill: #1a1a1a;");
        hCant.setPrefWidth(50);
        hCant.setAlignment(Pos.CENTER_RIGHT);
        Label hPrec = new Label("P.UNIT");
        hPrec.setStyle(
                "-fx-font-family: 'Courier New', monospace; -fx-font-size: 10px; -fx-font-weight: bold; -fx-text-fill: #1a1a1a;");
        hPrec.setPrefWidth(70);
        hPrec.setAlignment(Pos.CENTER_RIGHT);
        Label hSubt = new Label("SUBTOTAL");
        hSubt.setStyle(
                "-fx-font-family: 'Courier New', monospace; -fx-font-size: 10px; -fx-font-weight: bold; -fx-text-fill: #1a1a1a;");
        hSubt.setPrefWidth(80);
        hSubt.setAlignment(Pos.CENTER_RIGHT);
        headerProductos.getChildren().addAll(hId, hProd, hCant, hPrec, hSubt);

        // Lista de productos
        VBox listaProductos = new VBox(4);
        for (ModeloVenta.ItemVenta item : venta.getItems()) {
            HBox filaProducto = new HBox();
            filaProducto.setSpacing(0);

            // ID del producto
            String idProd = item.getIdProducto() != null ? item.getIdProducto() : "-";
            if (idProd.length() > 8) {
                idProd = idProd.substring(0, 8) + "..";
            }
            Label id = new Label(idProd);
            id.setStyle("-fx-font-family: 'Courier New', monospace; -fx-font-size: 10px; -fx-text-fill: #1a1a1a;");
            id.setPrefWidth(60);
            id.setAlignment(Pos.CENTER);

            String nombreCorto = item.getNombreProducto();
            if (nombreCorto.length() > 14) {
                nombreCorto = nombreCorto.substring(0, 14) + "..";
            }

            Label prod = new Label(nombreCorto);
            prod.setStyle("-fx-font-family: 'Courier New', monospace; -fx-font-size: 10px; -fx-text-fill: #1a1a1a;");
            prod.setPrefWidth(120);

            Label cant = new Label(String.valueOf(item.getCantidad()));
            cant.setStyle("-fx-font-family: 'Courier New', monospace; -fx-font-size: 10px; -fx-text-fill: #1a1a1a;");
            cant.setPrefWidth(50);
            cant.setAlignment(Pos.CENTER_RIGHT);

            Label prec = new Label(String.format("$%.0f", item.getPrecioUnitario()));
            prec.setStyle("-fx-font-family: 'Courier New', monospace; -fx-font-size: 10px; -fx-text-fill: #1a1a1a;");
            prec.setPrefWidth(70);
            prec.setAlignment(Pos.CENTER_RIGHT);

            double subtotal = item.getCantidad() * item.getPrecioUnitario();
            Label subt = new Label(String.format("$%.0f", subtotal));
            subt.setStyle("-fx-font-family: 'Courier New', monospace; -fx-font-size: 10px; -fx-text-fill: #1a1a1a;");
            subt.setPrefWidth(80);
            subt.setAlignment(Pos.CENTER_RIGHT);

            filaProducto.getChildren().addAll(id, prod, cant, prec, subt);
            listaProductos.getChildren().add(filaProducto);
        }

        Label sep4 = crearSeparadorRecibo();

        // ═══════════════════════════════════════════════════════════════
        // TOTAL
        // ═══════════════════════════════════════════════════════════════
        HBox totalBox = new HBox();
        totalBox.setSpacing(0);
        Label lblTotalTexto = new Label("TOTAL A PAGAR:");
        lblTotalTexto.setStyle(
                "-fx-font-family: 'Courier New', monospace; -fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #1a1a1a;");
        lblTotalTexto.setPrefWidth(200);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Label lblTotalValor = new Label(String.format("$%.0f", venta.getTotal()));
        lblTotalValor.setStyle(
                "-fx-font-family: 'Courier New', monospace; -fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #1a1a1a;");
        lblTotalValor.setAlignment(Pos.CENTER_RIGHT);

        totalBox.getChildren().addAll(lblTotalTexto, spacer, lblTotalValor);

        Label sep5 = crearSeparadorRecibo();

        // ═══════════════════════════════════════════════════════════════
        // PIE DEL RECIBO
        // ═══════════════════════════════════════════════════════════════
        Label lblGracias = new Label("¡GRACIAS POR SU COMPRA!");
        lblGracias.setStyle(
                "-fx-font-family: 'Courier New', monospace; -fx-font-size: 12px; -fx-font-weight: bold; -fx-text-fill: #1a1a1a;");
        lblGracias.setAlignment(Pos.CENTER);
        lblGracias.setMaxWidth(Double.MAX_VALUE);

        Label lblVuelva = new Label("Vuelva pronto");
        lblVuelva.setStyle("-fx-font-family: 'Courier New', monospace; -fx-font-size: 10px; -fx-text-fill: #555555;");
        lblVuelva.setAlignment(Pos.CENTER);
        lblVuelva.setMaxWidth(Double.MAX_VALUE);

        // Espaciado vertical
        Region sp1 = new Region();
        sp1.setPrefHeight(8);
        Region sp2 = new Region();
        sp2.setPrefHeight(8);
        Region sp3 = new Region();
        sp3.setPrefHeight(10);
        Region sp4 = new Region();
        sp4.setPrefHeight(4);
        Region sp5 = new Region();
        sp5.setPrefHeight(10);
        Region sp6 = new Region();
        sp6.setPrefHeight(15);

        // Agregar todos los elementos al recibo
        recibo.getChildren().addAll(
                lblNegocio, lblDireccion, lblTelefono, lblNit,
                sp1, sep1, sp2,
                lblTipoDoc, lblNumVenta,
                sp3, sep2, sp4,
                lblFecha, lblCliente, lblDocCliente,
                sp5, sep3,
                headerProductos, listaProductos,
                sep4, sp6,
                totalBox,
                sep5,
                lblGracias, lblVuelva);

        // Contenedor con fondo oscuro para contraste
        VBox contenedorExterno = new VBox(recibo);
        contenedorExterno.setAlignment(Pos.CENTER);
        contenedorExterno.setPadding(new Insets(20));
        contenedorExterno.setStyle("-fx-background-color: #1a1a1a;");

        dialog.getDialogPane().setContent(contenedorExterno);
        dialog.getDialogPane().getButtonTypes().add(ButtonType.CLOSE);
        dialog.getDialogPane().setStyle("-fx-background-color: #1a1a1a;");

        // Estilizar el botón de cerrar
        Button closeButton = (Button) dialog.getDialogPane().lookupButton(ButtonType.CLOSE);
        closeButton.setText("Cerrar");
        closeButton.getStyleClass().add("btn-salir");

        dialog.showAndWait();
    }

    // Método auxiliar para crear separadores estilo recibo
    private Label crearSeparadorRecibo() {
        Label sep = new Label("════════════════════════════════════════");
        sep.setStyle("-fx-font-family: 'Courier New', monospace; -fx-font-size: 10px; -fx-text-fill: #999999;");
        sep.setAlignment(Pos.CENTER);
        sep.setMaxWidth(Double.MAX_VALUE);
        return sep;
    }

    // Método para mostrar detalles de una compra en un diálogo modal estilo RECIBO
    private void mostrarDetallesCompra(ModeloCompra compra) {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Recibo de Compra");

        // Aplicar estilos CSS
        try {
            dialog.getDialogPane().getStylesheets().add(
                    getClass().getResource("/styles.css").toExternalForm());
        } catch (Exception ex) {
            System.out.println("No se pudo cargar CSS para el diálogo");
        }

        // Contenedor principal estilo papel/recibo
        VBox recibo = new VBox(0);
        recibo.setStyle(
                "-fx-background-color: #fefdf8;" +
                        "-fx-padding: 30 40 30 40;" +
                        "-fx-min-width: 480px;" +
                        "-fx-max-width: 480px;" +
                        "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.3), 15, 0, 0, 5);");

        // ═══════════════════════════════════════════════════════════════
        // ENCABEZADO DEL RECIBO
        // ═══════════════════════════════════════════════════════════════
        Label lblNegocio = new Label("MOE'S TAVERN");
        lblNegocio.setStyle(
                "-fx-font-family: 'Courier New', monospace; -fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #1a1a1a;");
        lblNegocio.setAlignment(Pos.CENTER);
        lblNegocio.setMaxWidth(Double.MAX_VALUE);

        Label lblDireccion = new Label("Calle Principal #123");
        lblDireccion
                .setStyle("-fx-font-family: 'Courier New', monospace; -fx-font-size: 11px; -fx-text-fill: #555555;");
        lblDireccion.setAlignment(Pos.CENTER);
        lblDireccion.setMaxWidth(Double.MAX_VALUE);

        Label lblTelefono = new Label("Tel: (555) 123-4567");
        lblTelefono.setStyle("-fx-font-family: 'Courier New', monospace; -fx-font-size: 11px; -fx-text-fill: #555555;");
        lblTelefono.setAlignment(Pos.CENTER);
        lblTelefono.setMaxWidth(Double.MAX_VALUE);

        Label lblNit = new Label("NIT: 900.123.456-7");
        lblNit.setStyle("-fx-font-family: 'Courier New', monospace; -fx-font-size: 11px; -fx-text-fill: #555555;");
        lblNit.setAlignment(Pos.CENTER);
        lblNit.setMaxWidth(Double.MAX_VALUE);

        // Separador superior
        Label sep1 = crearSeparadorRecibo();

        // ═══════════════════════════════════════════════════════════════
        // INFORMACIÓN DE LA COMPRA
        // ═══════════════════════════════════════════════════════════════
        Label lblTipoDoc = new Label("*** ORDEN DE COMPRA ***");
        lblTipoDoc.setStyle(
                "-fx-font-family: 'Courier New', monospace; -fx-font-size: 12px; -fx-font-weight: bold; -fx-text-fill: #1a1a1a;");
        lblTipoDoc.setAlignment(Pos.CENTER);
        lblTipoDoc.setMaxWidth(Double.MAX_VALUE);

        Label lblNumCompra = new Label("No. " + compra.getId());
        lblNumCompra.setStyle(
                "-fx-font-family: 'Courier New', monospace; -fx-font-size: 12px; -fx-font-weight: bold; -fx-text-fill: #1a1a1a;");
        lblNumCompra.setAlignment(Pos.CENTER);
        lblNumCompra.setMaxWidth(Double.MAX_VALUE);

        Label sep2 = crearSeparadorRecibo();

        // Fecha
        Label lblFecha = new Label("Fecha: " + compra.getFecha());
        lblFecha.setStyle("-fx-font-family: 'Courier New', monospace; -fx-font-size: 11px; -fx-text-fill: #1a1a1a;");

        Label sep3 = crearSeparadorRecibo();

        // ═══════════════════════════════════════════════════════════════
        // DETALLE DE PRODUCTOS
        // ═══════════════════════════════════════════════════════════════
        // Encabezado de productos
        HBox headerProductos = new HBox();
        headerProductos.setSpacing(0);
        Label hProd = new Label("PRODUCTO");
        hProd.setStyle(
                "-fx-font-family: 'Courier New', monospace; -fx-font-size: 10px; -fx-font-weight: bold; -fx-text-fill: #1a1a1a;");
        hProd.setPrefWidth(140);
        Label hProv = new Label("ID");
        hProv.setStyle(
                "-fx-font-family: 'Courier New', monospace; -fx-font-size: 10px; -fx-font-weight: bold; -fx-text-fill: #1a1a1a;");
        hProv.setPrefWidth(60);
        hProv.setAlignment(Pos.CENTER);
        Label hCant = new Label("CANT");
        hCant.setStyle(
                "-fx-font-family: 'Courier New', monospace; -fx-font-size: 10px; -fx-font-weight: bold; -fx-text-fill: #1a1a1a;");
        hCant.setPrefWidth(50);
        hCant.setAlignment(Pos.CENTER_RIGHT);
        Label hPrec = new Label("P.UNIT");
        hPrec.setStyle(
                "-fx-font-family: 'Courier New', monospace; -fx-font-size: 10px; -fx-font-weight: bold; -fx-text-fill: #1a1a1a;");
        hPrec.setPrefWidth(70);
        hPrec.setAlignment(Pos.CENTER_RIGHT);
        Label hSubt = new Label("SUBTOTAL");
        hSubt.setStyle(
                "-fx-font-family: 'Courier New', monospace; -fx-font-size: 10px; -fx-font-weight: bold; -fx-text-fill: #1a1a1a;");
        hSubt.setPrefWidth(80);
        hSubt.setAlignment(Pos.CENTER_RIGHT);
        headerProductos.getChildren().addAll(hProd, hProv, hCant, hPrec, hSubt);

        // Lista de productos
        VBox listaProductos = new VBox(4);
        for (ModeloCompra.ItemCompra item : compra.getItems()) {
            HBox filaProducto = new HBox();
            filaProducto.setSpacing(0);

            String nombreCorto = item.getNombreProducto();
            if (nombreCorto.length() > 16) {
                nombreCorto = nombreCorto.substring(0, 16) + "..";
            }

            Label prod = new Label(nombreCorto);
            prod.setStyle("-fx-font-family: 'Courier New', monospace; -fx-font-size: 10px; -fx-text-fill: #1a1a1a;");
            prod.setPrefWidth(140);

            Label prov = new Label(item.getIdProducto());
            prov.setStyle("-fx-font-family: 'Courier New', monospace; -fx-font-size: 10px; -fx-text-fill: #1a1a1a;");
            prov.setPrefWidth(60);
            prov.setAlignment(Pos.CENTER);

            Label cant = new Label(String.valueOf(item.getCantidad()));
            cant.setStyle("-fx-font-family: 'Courier New', monospace; -fx-font-size: 10px; -fx-text-fill: #1a1a1a;");
            cant.setPrefWidth(50);
            cant.setAlignment(Pos.CENTER_RIGHT);

            Label prec = new Label(String.format("$%.0f", item.getPrecioUnitario()));
            prec.setStyle("-fx-font-family: 'Courier New', monospace; -fx-font-size: 10px; -fx-text-fill: #1a1a1a;");
            prec.setPrefWidth(70);
            prec.setAlignment(Pos.CENTER_RIGHT);

            double subtotal = item.getCantidad() * item.getPrecioUnitario();
            Label subt = new Label(String.format("$%.0f", subtotal));
            subt.setStyle("-fx-font-family: 'Courier New', monospace; -fx-font-size: 10px; -fx-text-fill: #1a1a1a;");
            subt.setPrefWidth(80);
            subt.setAlignment(Pos.CENTER_RIGHT);

            filaProducto.getChildren().addAll(prod, prov, cant, prec, subt);
            listaProductos.getChildren().add(filaProducto);
        }

        Label sep4 = crearSeparadorRecibo();

        // ═══════════════════════════════════════════════════════════════
        // TOTAL
        // ═══════════════════════════════════════════════════════════════
        HBox totalBox = new HBox();
        totalBox.setSpacing(0);
        Label lblTotalTexto = new Label("TOTAL COMPRA:");
        lblTotalTexto.setStyle(
                "-fx-font-family: 'Courier New', monospace; -fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #1a1a1a;");
        lblTotalTexto.setPrefWidth(220);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Label lblTotalValor = new Label(String.format("$%.0f", compra.getTotal()));
        lblTotalValor.setStyle(
                "-fx-font-family: 'Courier New', monospace; -fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #1a1a1a;");
        lblTotalValor.setAlignment(Pos.CENTER_RIGHT);

        totalBox.getChildren().addAll(lblTotalTexto, spacer, lblTotalValor);

        Label sep5 = crearSeparadorRecibo();

        // ═══════════════════════════════════════════════════════════════
        // PIE DEL RECIBO
        // ═══════════════════════════════════════════════════════════════
        Label lblRegistro = new Label("Compra registrada correctamente");
        lblRegistro.setStyle("-fx-font-family: 'Courier New', monospace; -fx-font-size: 10px; -fx-text-fill: #555555;");
        lblRegistro.setAlignment(Pos.CENTER);
        lblRegistro.setMaxWidth(Double.MAX_VALUE);

        // Espaciado vertical
        Region sp1 = new Region();
        sp1.setPrefHeight(8);
        Region sp2 = new Region();
        sp2.setPrefHeight(8);
        Region sp3 = new Region();
        sp3.setPrefHeight(10);
        Region sp4 = new Region();
        sp4.setPrefHeight(4);
        Region sp5 = new Region();
        sp5.setPrefHeight(10);
        Region sp6 = new Region();
        sp6.setPrefHeight(15);

        // Agregar todos los elementos al recibo
        recibo.getChildren().addAll(
                lblNegocio, lblDireccion, lblTelefono, lblNit,
                sp1, sep1, sp2,
                lblTipoDoc, lblNumCompra,
                sp3, sep2, sp4,
                lblFecha,
                sp5, sep3,
                headerProductos, listaProductos,
                sep4, sp6,
                totalBox,
                sep5,
                lblRegistro);

        // Contenedor con fondo oscuro para contraste
        VBox contenedorExterno = new VBox(recibo);
        contenedorExterno.setAlignment(Pos.CENTER);
        contenedorExterno.setPadding(new Insets(20));
        contenedorExterno.setStyle("-fx-background-color: #1a1a1a;");

        dialog.getDialogPane().setContent(contenedorExterno);
        dialog.getDialogPane().getButtonTypes().add(ButtonType.CLOSE);
        dialog.getDialogPane().setStyle("-fx-background-color: #1a1a1a;");

        // Estilizar el botón de cerrar
        Button closeButton = (Button) dialog.getDialogPane().lookupButton(ButtonType.CLOSE);
        closeButton.setText("Cerrar");
        closeButton.getStyleClass().add("btn-salir");

        dialog.showAndWait();
    }

    // Getters
    public TableView<ModeloVenta> getTablaVentas() {
        return tablaVentas;
    }

    public TableView<ModeloCompra> getTablaCompras() {
        return tablaCompras;
    }

    public Button getBtnSalir() {
        return btnSalir;
    }
}
