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

// Vista de gestión de usuarios como ventana independiente (Stage).
// Define el formulario con campos de entrada, tabla de datos y botones de acción.
public class frmUsuarios extends Stage {
    // Contenedor raíz principal de la vista
    private final BorderPane contenedorUsuarios = new BorderPane();

    // Campos de entrada para datos del usuario
    private final TextField txtId = new TextField();
    private final TextField txtNombre = new TextField();
    private final TextField txtUsuario = new TextField();
    private final PasswordField txtPassword = new PasswordField();
    private final TextField txtEmail = new TextField();
    private final ComboBox<String> cboRol = new ComboBox<>();

    // Tabla para mostrar los usuarios registrados
    private final TableView<String[]> tabla = new TableView<>();

    // Botones de acción expuestos al controlador
    private final Button btnGuardar = new Button("Guardar");
    private final Button btnModificar = new Button("Modificar");
    private final Button btnEliminar = new Button("Eliminar");
    private final Button btnSalir = new Button("Salir");
    private final Button btnAgregarUsuario = new Button("+ Agregar usuario");

    // Panel flotante con formulario para nuevo usuario
    private final StackPane panelEmergenteNuevoUsuario = new StackPane();
    private final VBox panelFormulario = new VBox(12);

    // Control de modo del formulario
    private boolean modoEdicion = false; // false = agregar, true = modificar
    private String idOriginal = null; // ID del usuario que se está modificando
    private Label tituloFormulario = new Label();

    // Constructor: crea una ventana independiente para gestión de usuarios
    // @param ventanaPadre - Stage padre para hacer esta ventana modal respecto a
    // ella
    public frmUsuarios(Stage ventanaPadre) {
        // Configurar la ventana
        setTitle("Gestión de Usuarios");

        // Hacer la ventana modal (bloquea la ventana padre hasta que se cierre)
        initModality(Modality.APPLICATION_MODAL);
        initOwner(ventanaPadre);
        initStyle(StageStyle.UNDECORATED);

        // Construir la nueva estructura similar a frmCliente
        HBox header = buildHeader();

        // Tabla en un solo bloque central
        VBox centro = new VBox(16);
        centro.getStyleClass().add("admin-centro-container");
        centro.setPadding(new Insets(20, 32, 24, 32));

        // Tabla
        VBox tabla = buildTabla();

        centro.getChildren().addAll(tabla);

        contenedorUsuarios.setTop(header);
        contenedorUsuarios.setCenter(centro);
        contenedorUsuarios.setBottom(buildBotones());

        // Configurar panel flotante para formulario de nuevo usuario
        configurarPanelNuevoUsuario();
        StackPane contenedorPrincipal = new StackPane(contenedorUsuarios, panelEmergenteNuevoUsuario);

        // Obtener dimensiones de la pantalla
        Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();
        double screenWidth = screenBounds.getWidth();
        double screenHeight = screenBounds.getHeight();

        // Altura de la barra de navegación (aproximadamente 50px según el CSS)
        double navBarHeight = 50;

        // Calcular altura de la ventana (altura total menos la barra de navegación)
        double windowHeight = screenHeight - navBarHeight;

        // Crear la escena con el ancho completo y altura ajustada
        Scene scene = new Scene(contenedorPrincipal, screenWidth, windowHeight);

        // Cargar el CSS
        try {
            String css = getClass().getResource("/styles.css").toExternalForm();
            scene.getStylesheets().add(css);
        } catch (Exception e) {
            System.out.println("No se pudo cargar el CSS: " + e.getMessage());
        }

        setScene(scene);
        setResizable(false);

        // Posicionar la ventana justo debajo de la barra de navegación
        setX(screenBounds.getMinX());
        setY(screenBounds.getMinY() + navBarHeight);
    }

    // Encabezado con título, subtítulo y botón agregar usuario
    private HBox buildHeader() {
        Label titulo = new Label("Usuarios");
        titulo.getStyleClass().add("admin-titulo");

        Label subtitulo = new Label("Gestiona los usuarios del sistema");
        subtitulo.getStyleClass().add("admin-subtitulo");

        VBox textos = new VBox(4, titulo, subtitulo);

        btnAgregarUsuario.getStyleClass().add("btn-agregar");
        btnAgregarUsuario.setOnAction(e -> mostrarPanelFormulario());

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        HBox header = new HBox(16, textos, spacer, btnAgregarUsuario);
        header.getStyleClass().add("admin-header");
        header.setPadding(new Insets(24, 32, 16, 32));
        header.setAlignment(Pos.CENTER_LEFT);
        return header;
    }

    // Configura el panel flotante con el formulario para nuevo usuario
    private void configurarPanelNuevoUsuario() {
        panelFormulario.getChildren().clear();
        panelFormulario.getStyleClass().add("admin-form-overlay");
        panelFormulario.setPadding(new Insets(20));

        tituloFormulario.setText("Nuevo usuario");
        tituloFormulario.getStyleClass().add("titulo-formulario");

        GridPane gridNuevoUsuario = new GridPane();
        gridNuevoUsuario.setHgap(10);
        gridNuevoUsuario.setVgap(10);

        // Configurar campos
        txtId.setPromptText("ID (auto)");
        txtId.setEditable(false);
        txtNombre.setPromptText("Nombre del usuario");
        txtUsuario.setPromptText("Nombre de usuario");
        txtPassword.setPromptText("Contraseña");
        txtEmail.setPromptText("correo@ejemplo.com");

        // ComboBox para roles
        cboRol.getItems().addAll("Administrador", "Tienda");
        cboRol.setPromptText("Seleccione rol");

        // Agregar campos al grid
        gridNuevoUsuario.add(new Label("ID:"), 0, 0);
        gridNuevoUsuario.add(txtId, 1, 0);
        gridNuevoUsuario.add(new Label("Nombre:"), 0, 1);
        gridNuevoUsuario.add(txtNombre, 1, 1);
        gridNuevoUsuario.add(new Label("Usuario:"), 0, 2);
        gridNuevoUsuario.add(txtUsuario, 1, 2);
        gridNuevoUsuario.add(new Label("Contraseña:"), 0, 3);
        gridNuevoUsuario.add(txtPassword, 1, 3);
        gridNuevoUsuario.add(new Label("Email:"), 0, 4);
        gridNuevoUsuario.add(txtEmail, 1, 4);
        gridNuevoUsuario.add(new Label("Rol:"), 0, 5);
        gridNuevoUsuario.add(cboRol, 1, 5);

        HBox botonesNuevoUsuario = new HBox(12);
        botonesNuevoUsuario.setAlignment(Pos.CENTER);
        Button btnCerrar = new Button("Cerrar");
        btnCerrar.getStyleClass().add("btn-salir");
        btnCerrar.setOnAction(e -> ocultarPanelFormulario());
        botonesNuevoUsuario.getChildren().addAll(btnGuardar, btnCerrar);

        panelFormulario.getChildren().addAll(tituloFormulario, new Separator(), gridNuevoUsuario, botonesNuevoUsuario);
        panelEmergenteNuevoUsuario.getChildren().setAll(panelFormulario);
        panelEmergenteNuevoUsuario.getStyleClass().add("admin-overlay");
        panelEmergenteNuevoUsuario.setVisible(false);
    }

    private void mostrarPanelFormulario() {
        // Modo AGREGAR: Limpiar campos para un nuevo usuario
        modoEdicion = false;
        idOriginal = null;
        tituloFormulario.setText("Nuevo usuario");
        limpiarFormulario();
        txtPassword.setPromptText("Contraseña");
        panelEmergenteNuevoUsuario.setVisible(true);
    }

    private void ocultarPanelFormulario() {
        panelEmergenteNuevoUsuario.setVisible(false);
        modoEdicion = false;
        idOriginal = null;
    }

    // Método público para abrir el formulario en modo EDICIÓN con datos
    // pre-cargados
    public void mostrarPanelParaEditar(String id, String nombre, String usuario, String email, String rol) {
        modoEdicion = true;
        idOriginal = id;
        tituloFormulario.setText("Modificar usuario");

        // Cargar datos en los campos
        txtId.setText(id);
        txtNombre.setText(nombre);
        txtUsuario.setText(usuario);
        txtEmail.setText(email);
        cboRol.setValue(rol);
        txtPassword.clear();
        txtPassword.setPromptText("Dejar vacío para mantener la contraseña actual");

        panelEmergenteNuevoUsuario.setVisible(true);
    }

    // Construye la tabla para mostrar los usuarios en el centro
    private VBox buildTabla() {
        // Configurar columnas
        TableColumn<String[], String> colId = new TableColumn<>("ID");
        colId.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue()[0]));
        colId.setPrefWidth(80);

        TableColumn<String[], String> colNombre = new TableColumn<>("Nombre");
        colNombre.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue()[1]));
        colNombre.setPrefWidth(200);

        TableColumn<String[], String> colUsuario = new TableColumn<>("Usuario");
        colUsuario.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue()[2]));
        colUsuario.setPrefWidth(150);

        TableColumn<String[], String> colEmail = new TableColumn<>("Email");
        colEmail.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue()[3]));
        colEmail.setPrefWidth(250);

        TableColumn<String[], String> colRol = new TableColumn<>("Rol");
        colRol.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue()[4]));
        colRol.setPrefWidth(150);

        tabla.getColumns().addAll(colId, colNombre, colUsuario, colEmail, colRol);

        // Hacer que las columnas ocupen todo el espacio disponible
        tabla.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_ALL_COLUMNS);

        VBox box = new VBox(10);
        Label titulo = new Label("Registro de Usuarios");
        titulo.getStyleClass().add("titulo-tabla");
        box.getChildren().addAll(titulo, tabla);
        VBox.setVgrow(tabla, Priority.ALWAYS);

        return box;
    }

    // Construye la barra de botones en la parte inferior (solo Modificar, Eliminar
    // y Salir)
    private HBox buildBotones() {
        HBox box = new HBox();
        box.getStyleClass().add("botones-container");

        // Aplicar clases CSS (sin el btnGuardar que ahora está en el formulario
        // flotante)
        btnGuardar.getStyleClass().add("btn-guardar");
        btnModificar.getStyleClass().add("btn-modificar");
        btnEliminar.getStyleClass().add("btn-eliminar");
        btnSalir.getStyleClass().add("btn-salir");

        // Configurar acción del botón salir para cerrar solo esta ventana
        btnSalir.setOnAction(e -> close());

        // Solo agregamos Modificar, Eliminar y Salir (Guardar está en el formulario
        // flotante)
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

    public TextField getTxtUsuario() {
        return txtUsuario;
    }

    public PasswordField getTxtPassword() {
        return txtPassword;
    }

    public TextField getTxtEmail() {
        return txtEmail;
    }

    public ComboBox<String> getCboRol() {
        return cboRol;
    }

    public TableView<String[]> getTabla() {
        return tabla;
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

    public Button getBtnSalir() {
        return btnSalir;
    }

    // Getters para control del modo del formulario
    public boolean isModoEdicion() {
        return modoEdicion;
    }

    public String getIdOriginal() {
        return idOriginal;
    }

    // Metodo auxiliar para limpiar el formulario
    public void limpiarFormulario() {
        txtId.clear();
        txtNombre.clear();
        txtUsuario.clear();
        txtPassword.clear();
        txtEmail.clear();
        cboRol.setValue(null);
    }

    // Metodo público para ocultar el panel flotante desde el controlador
    public void cerrarPanelFormulario() {
        ocultarPanelFormulario();
    }
}