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

    // Botones
    private final Button btnGuardar = new Button("Guardar");
    private final Button btnModificar = new Button("Modificar");
    private final Button btnEliminar = new Button("Eliminar");
    private final Button btnSalir = new Button("Salir");
    private final Button btnAgregarCliente = new Button("+ Agregar cliente");

    // Panel flotante con formulario para nuevo cliente
    private final StackPane panelEmergenteNuevoUsuario = new StackPane();
    private final VBox panelFormulario = new VBox(12);

    // Control de modo del formulario
    private boolean modoEdicion = false; // false = agregar, true = modificar
    private String documentoOriginal = null; // Documento del cliente que se está modificando
    private Label tituloFormulario = new Label();

    public frmCliente(Stage ventanaPadre) {
        setTitle("Gestión de Clientes");
        initOwner(ventanaPadre);
        initModality(Modality.APPLICATION_MODAL);
        initStyle(StageStyle.UNDECORATED);

        // Construir la parte superior (título grande + subtítulo y botón agregar
        // similar a la imagen)
        HBox header = buildHeader();

        // Tabla en un solo bloque central oscuro
        VBox centro = new VBox(16);
        centro.getStyleClass().add("clientes-centro-container");
        centro.setPadding(new Insets(20, 32, 24, 32));

        // Tabla
        VBox tabla = buildTabla();

        centro.getChildren().addAll(tabla);

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

        // Posicionar la ventana justo debajo de la barra de navegación, igual que
        // frmAdmin
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

        tituloFormulario.setText("Nuevo cliente");
        tituloFormulario.getStyleClass().add("titulo-formulario");

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

        panelFormulario.getChildren().addAll(tituloFormulario, new Separator(), gridNuevoCliente, botonesNuevoCliente);
        panelEmergenteNuevoUsuario.getChildren().setAll(panelFormulario);
        panelEmergenteNuevoUsuario.getStyleClass().add("clientes-overlay");
        panelEmergenteNuevoUsuario.setVisible(false);
    }

    private void mostrarPanelFormulario() {
        // Modo AGREGAR: Limpiar campos para un nuevo cliente
        modoEdicion = false;
        documentoOriginal = null;
        tituloFormulario.setText("Nuevo cliente");
        cboTipoDocumento.getSelectionModel().clearSelection();
        txtDocumento.clear();
        txtDocumento.setEditable(true); // Habilitar edición del documento
        txtNombreCompleto.clear();
        txtTelefono.clear();
        txtCorreo.clear();
        panelEmergenteNuevoUsuario.setVisible(true);
    }

    private void ocultarPanelFormulario() {
        panelEmergenteNuevoUsuario.setVisible(false);
        modoEdicion = false;
        documentoOriginal = null;
    }

    // Método público para abrir el formulario en modo EDICIÓN con datos
    // pre-cargados
    public void mostrarPanelParaEditar(String tipoDoc, String documento, String nombre, String telefono,
            String correo) {
        modoEdicion = true;
        documentoOriginal = documento;
        tituloFormulario.setText("Modificar cliente");

        // Cargar datos en los campos
        cboTipoDocumento.setValue(tipoDoc);
        txtDocumento.setText(documento);
        txtDocumento.setEditable(false); // Deshabilitar edición del documento (es el ID único)
        txtNombreCompleto.setText(nombre);
        txtTelefono.setText(telefono);
        txtCorreo.setText(correo);

        panelEmergenteNuevoUsuario.setVisible(true);
    }

    private VBox buildTabla() {
        TableColumn<String[], String> colTipoDoc = new TableColumn<>("Tipo Doc");
        colTipoDoc.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue()[0]));
        colTipoDoc.setPrefWidth(100);

        TableColumn<String[], String> colDocumento = new TableColumn<>("Documento");
        colDocumento.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue()[1]));
        colDocumento.setPrefWidth(150);

        TableColumn<String[], String> colNombre = new TableColumn<>("Nombre completo");
        colNombre.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue()[2]));
        colNombre.setPrefWidth(300);

        TableColumn<String[], String> colTelefono = new TableColumn<>("Teléfono");
        colTelefono.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue()[3]));
        colTelefono.setPrefWidth(150);

        TableColumn<String[], String> colCorreo = new TableColumn<>("Correo");
        colCorreo.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue()[4]));
        colCorreo.setPrefWidth(250);

        tablaClientes.getColumns().addAll(colTipoDoc, colDocumento, colNombre, colTelefono, colCorreo);
        tablaClientes.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_ALL_COLUMNS);

        VBox box = new VBox(10);
        Label titulo = new Label("Registro de Clientes");
        titulo.getStyleClass().add("titulo-tabla");
        box.getChildren().addAll(titulo, tablaClientes);
        VBox.setVgrow(tablaClientes, Priority.ALWAYS);
        return box;
    }

    private HBox buildBotones() {
        HBox box = new HBox();
        box.getStyleClass().add("botones-container");

        // Aplicar clases de estilo a los botones
        btnGuardar.getStyleClass().add("btn-guardar");
        btnModificar.getStyleClass().add("btn-modificar");
        btnEliminar.getStyleClass().add("btn-eliminar");
        btnSalir.getStyleClass().add("btn-salir");

        btnSalir.setOnAction(e -> close());

        // Solo agregamos Modificar, Eliminar y Salir (Guardar está en el formulario
        // flotante)
        box.getChildren().addAll(btnModificar, btnEliminar, btnSalir);
        return box;
    }

    // Getters para el controlador
    public ComboBox<String> getCboTipoDocumento() {
        return cboTipoDocumento;
    }

    public TextField getTxtDocumento() {
        return txtDocumento;
    }

    public TextField getTxtNombreCompleto() {
        return txtNombreCompleto;
    }

    public TextField getTxtTelefono() {
        return txtTelefono;
    }

    public TextField getTxtCorreo() {
        return txtCorreo;
    }

    public TableView<String[]> getTablaClientes() {
        return tablaClientes;
    }

    public Button getBtnGuardar() {
        return btnGuardar;
    }

    public Button getBtnModificar() {
        return btnModificar;
    }

    public Button getBtnEliminar() {
        return btnEliminar;
    }

    // Metodo para limpiar el formulario
    public void limpiarFormulario() {
        cboTipoDocumento.getSelectionModel().clearSelection();
        txtDocumento.clear();
        txtNombreCompleto.clear();
        txtTelefono.clear();
        txtCorreo.clear();
    }

    // Metodo para cerrar el panel flotante desde el controlador
    public void cerrarPanelFormulario() {
        ocultarPanelFormulario();
    }

    // Getters para control del modo del formulario
    public boolean isModoEdicion() {
        return modoEdicion;
    }

    public String getDocumentoOriginal() {
        return documentoOriginal;
    }

    // Aplica restricciones visuales para el rol TIENDA
    public void aplicarRestriccionesTienda() {
        // Deshabilitar botón Eliminar
        btnEliminar.setDisable(true);
        // Opcional: Ocultarlo
        // if (btnEliminar.getParent() instanceof HBox) {
        // ((HBox) btnEliminar.getParent()).getChildren().remove(btnEliminar);
        // }
    }
}
