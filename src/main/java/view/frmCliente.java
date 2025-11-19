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

// Vista para la gestión de clientes.
// Permite registrar nuevos clientes y ver la lista de clientes registrados.
public class frmCliente extends Stage {
    private final BorderPane contenedorCliente = new BorderPane();

    // Campos del formulario
    private final ComboBox<String> cboTipoDocumento = new ComboBox<>();
    private final TextField txtDocumento = new TextField();
    private final TextField txtNombreCompleto = new TextField();
    private final TextField txtTelefono = new TextField();
    private final TextField txtCorreo = new TextField();

    // Tabla para mostrar clientes
    private final TableView<String[]> tablaClientes = new TableView<>();

    // Buscador (estilo similar a la caja de búsqueda de la imagen)
    private final TextField txtBuscar = new TextField();

    // Botones
    private final Button btnGuardar = new Button("Guardar");
    private final Button btnSalir = new Button("Salir");
    private final Button btnAgregarCliente = new Button("+ Agregar cliente");

    // Panel flotante con formulario para nuevo cliente
    private final StackPane panelEmergenteNuevoUsuario = new StackPane();
    private final VBox panelFormulario = new VBox(12);

    public frmCliente(Stage ventanaPadre) {
        setTitle("Gestión de Clientes");
        initOwner(ventanaPadre);
        initModality(Modality.APPLICATION_MODAL);
        initStyle(StageStyle.UNDECORATED);

        // Construir la parte superior (título grande + subtítulo y botón agregar similar a la imagen)
        HBox header = buildHeader();

        // Buscador y tabla en un solo bloque central oscuro
        VBox centro = new VBox(16);
        centro.getStyleClass().add("clientes-centro-container");
        centro.setPadding(new Insets(20, 32, 24, 32));

        // Buscador
        txtBuscar.setPromptText("Buscar clientes por nombre, correo electrónico o teléfono.");
        txtBuscar.getStyleClass().add("clientes-buscador");

        VBox buscadorWrapper = new VBox(txtBuscar);
        buscadorWrapper.getStyleClass().add("clientes-buscador-wrapper");

        // Tabla
        VBox tabla = buildTabla();

        centro.getChildren().addAll(buscadorWrapper, tabla);

        contenedorCliente.setTop(header);
        contenedorCliente.setCenter(centro);
        contenedorCliente.setBottom(buildBotones());

        // Configurar panelEmergenteNuevoUsuario para formulario flotante
        configurarPanelNuevoCliente();
        StackPane contenedorPrincipal = new StackPane(contenedorCliente, panelEmergenteNuevoUsuario);

        // Obtener dimensiones de la pantalla (igual que frmAdmin)
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
            System.out.println("No se pudo cargar el CSS en frmCliente: " + e.getMessage());
        }

        setScene(scene);
        setResizable(false);

        // Posicionar la ventana justo debajo de la barra de navegación, igual que frmAdmin
        setX(screenBounds.getMinX());
        setY(screenBounds.getMinY() + navBarHeight);
    }

    // Encabezado similar a la imagen: "Clientes" + subtítulo + botón agregar
    private HBox buildHeader() {
        Label titulo = new Label("Clientes");
        titulo.getStyleClass().add("clientes-titulo");

        Label subtitulo = new Label("Gestiona tu base de datos de clientes");
        subtitulo.getStyleClass().add("clientes-subtitulo");

        VBox textos = new VBox(4, titulo, subtitulo);

        btnAgregarCliente.getStyleClass().add("btn-agregar");
        btnAgregarCliente.setOnAction(e -> mostrarPanelFormulario());

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        HBox header = new HBox(16, textos, spacer, btnAgregarCliente);
        header.getStyleClass().add("clientes-header");
        header.setPadding(new Insets(24, 32, 16, 32));
        header.setAlignment(Pos.CENTER_LEFT);
        return header;
    }

    private void configurarPanelNuevoCliente() {
        panelFormulario.getChildren().clear();
        panelFormulario.getStyleClass().add("clientes-form-overlay");
        panelFormulario.setPadding(new Insets(20));

        Label tituloNuevoCliente = new Label("Nuevo cliente");
        tituloNuevoCliente.getStyleClass().add("titulo-formulario");

        GridPane gridNuevoCliente = new GridPane();
        gridNuevoCliente.setHgap(10);
        gridNuevoCliente.setVgap(10);

        // reutilizamos los campos ya declarados
        cboTipoDocumento.getItems().setAll("CC", "NIT", "CE");
        cboTipoDocumento.setPromptText("Tipo de documento");

        // Agregar al gridNuevoCliente (formulario arriba)
        gridNuevoCliente.add(new Label("Tipo Doc:"), 0, 0);
        gridNuevoCliente.add(cboTipoDocumento, 1, 0);
        gridNuevoCliente.add(new Label("Documento:"), 0, 1);
        gridNuevoCliente.add(txtDocumento, 1, 1);
        gridNuevoCliente.add(new Label("Nombre completo:"), 0, 2);
        gridNuevoCliente.add(txtNombreCompleto, 1, 2);
        gridNuevoCliente.add(new Label("Teléfono:"), 0, 3);
        gridNuevoCliente.add(txtTelefono, 1, 3);
        gridNuevoCliente.add(new Label("Correo:"), 0, 4);
        gridNuevoCliente.add(txtCorreo, 1, 4);

        HBox botonesNuevoCliente = new HBox(12);
        botonesNuevoCliente.setAlignment(Pos.CENTER);
        Button btnSalir = new Button("Salir");
        btnSalir.getStyleClass().add("btn-salir");
        btnSalir.setOnAction(e -> ocultarPanelFormulario());
        botonesNuevoCliente.getChildren().addAll(btnGuardar, btnSalir);

        panelFormulario.getChildren().addAll(tituloNuevoCliente, new Separator(), gridNuevoCliente, botonesNuevoCliente);
        panelEmergenteNuevoUsuario.getChildren().setAll(panelFormulario);
        panelEmergenteNuevoUsuario.getStyleClass().add("clientes-overlay");
        panelEmergenteNuevoUsuario.setVisible(false);
    }

    private void mostrarPanelFormulario() {
        // Limpiar campos para un nuevo cliente
        cboTipoDocumento.getSelectionModel().clearSelection();
        txtDocumento.clear();
        txtNombreCompleto.clear();
        txtTelefono.clear();
        txtCorreo.clear();
        panelEmergenteNuevoUsuario.setVisible(true);
    }

    private void ocultarPanelFormulario() {
        panelEmergenteNuevoUsuario.setVisible(false);
    }

    private VBox buildTabla() {
        TableColumn<String[], String> colTipoDoc = new TableColumn<>("Tipo Doc");
        colTipoDoc.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue()[0]));
        colTipoDoc.prefWidthProperty().bind(tablaClientes.widthProperty().multiply(0.12));

        TableColumn<String[], String> colDocumento = new TableColumn<>("Documento");
        colDocumento.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue()[1]));
        colDocumento.prefWidthProperty().bind(tablaClientes.widthProperty().multiply(0.15));

        TableColumn<String[], String> colNombre = new TableColumn<>("Nombre completo");
        colNombre.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue()[2]));
        colNombre.prefWidthProperty().bind(tablaClientes.widthProperty().multiply(0.30));

        TableColumn<String[], String> colTelefono = new TableColumn<>("Teléfono");
        colTelefono.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue()[3]));
        colTelefono.prefWidthProperty().bind(tablaClientes.widthProperty().multiply(0.18));

        TableColumn<String[], String> colCorreo = new TableColumn<>("Correo");
        colCorreo.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue()[4]));
        colCorreo.prefWidthProperty().bind(tablaClientes.widthProperty().multiply(0.25));

        tablaClientes.getColumns().addAll(colTipoDoc, colDocumento, colNombre, colTelefono, colCorreo);
        tablaClientes.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        VBox box = new VBox(10);
        Label titulo = new Label("Lista de Clientes");
        box.getChildren().addAll(titulo, tablaClientes);
        VBox.setVgrow(tablaClientes, Priority.ALWAYS);
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
    public ComboBox<String> getCboTipoDocumento() { return cboTipoDocumento; }
    public TextField getTxtDocumento() { return txtDocumento; }
    public TextField getTxtNombreCompleto() { return txtNombreCompleto; }
    public TextField getTxtTelefono() { return txtTelefono; }
    public TextField getTxtCorreo() { return txtCorreo; }
    public TableView<String[]> getTablaClientes() { return tablaClientes; }
    public Button getBtnGuardar() { return btnGuardar; }
    public TextField getTxtBuscar() { return txtBuscar; }
}

