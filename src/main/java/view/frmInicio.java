package view;

// Importaciones de JavaFX para construir la interfaz gráfica
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;

// Clase vista que construye la interfaz de inicio de sesión/registro.
// Responsabilidad: solo definir la estructura visual y exponer los componentes.
// No contiene lógica de negocio (eso va en el controlador).
public class frmInicio {
    // Contenedor raíz de la vista (BorderPane permite organizar contenido en 5 zonas)
    private BorderPane root;

    // Componentes para LOGIN (parte izquierda)
    private TextField txtUsuarioLogin;       // Campo para ingresar nombre de usuario
    private PasswordField txtPasswordLogin;  // Campo oculto para contraseña
    private Button btnIniciarSesion;         // Botón para ejecutar el login
    private Hyperlink linkOlvidastePassword; // Link "Olvidaste tu contraseña" (no implementado)

    // Componentes para REGISTRO (parte derecha)
    private TextField txtUsuarioRegistro;
    private TextField txtNombreRegistro;
    private TextField txtEmailRegistro;
    private PasswordField txtPasswordRegistro;
    private PasswordField txtConfirmarPasswordRegistro;
    private ComboBox<String> cboRolRegistro;  // Selector de rol (Admin, Empleado, etc.)
    private Button btnRegistrarse;

    // Constructor: inicializa todos los componentes visuales
    public frmInicio() {
        inicializarComponentes();
    }

    // Metodo que construye toda la estructura visual de la pantalla
    private void inicializarComponentes() {
        // BorderPane como contenedor principal (permite layout flexible)
        root = new BorderPane();
        root.setId("login-root"); // ID para aplicar estilos CSS

        // HBox: contenedor horizontal que divide la pantalla en dos secciones
        HBox mainContainer = new HBox(0); // 0 = sin espaciado entre children
        mainContainer.setAlignment(Pos.CENTER); // Centra horizontalmente
        mainContainer.setPrefSize(700, 700); // Tamaño fijo de la ventana

        // ========== PANEL IZQUIERDO: LOGIN ==========
        VBox panelLogin = crearPanelLogin();
        panelLogin.setId("panel-login");
        panelLogin.setPrefWidth(400); // Mitad de la pantalla

        // ========== PANEL DERECHO: REGISTRO ==========
        VBox panelRegistro = crearPanelRegistro();
        panelRegistro.setId("panel-registro");
        panelRegistro.setPrefWidth(400); // Mitad de la pantalla

        // Agregar ambos paneles al contenedor horizontal
        mainContainer.getChildren().addAll(panelLogin, panelRegistro);

        // Colocar el contenedor en el centro del BorderPane
        root.setCenter(mainContainer);
    }

    // Metodo que construye el panel de INICIO DE SESIÓN
    private VBox crearPanelLogin() {
        VBox panel = new VBox(20); // VBox con 20px de espaciado entre elementos
        panel.setAlignment(Pos.CENTER); // Centra verticalmente
        panel.setPadding(new Insets(40, 40, 40, 40)); // Padding interno

        // Título del panel
        Label lblTitulo = new Label("INICIAR SESIÓN");
        lblTitulo.getStyleClass().add("titulo-login"); // Clase CSS

        // Subtítulo
        Label lblSubtitulo = new Label("Accede a tu cuenta");
        lblSubtitulo.getStyleClass().add("subtitulo-login");

        // Campo de texto para usuario
        txtUsuarioLogin = new TextField();
        txtUsuarioLogin.setPromptText("Usuario"); // Placeholder
        txtUsuarioLogin.setMaxWidth(300);
        txtUsuarioLogin.getStyleClass().add("login-field");

        // Campo de contraseña (oculta el texto ingresado)
        txtPasswordLogin = new PasswordField();
        txtPasswordLogin.setPromptText("Contraseña");
        txtPasswordLogin.setMaxWidth(300);
        txtPasswordLogin.getStyleClass().add("login-field");

        // Link "Olvidaste tu contraseña" (funcionalidad no implementada)
        linkOlvidastePassword = new Hyperlink("¿Olvidaste tu contraseña?");
        linkOlvidastePassword.getStyleClass().add("link-olvido");

        // Botón de inicio de sesión
        btnIniciarSesion = new Button("INICIAR SESIÓN");
        btnIniciarSesion.setMaxWidth(300);
        btnIniciarSesion.getStyleClass().add("btn-login");

        // Agregar todos los componentes al panel en orden vertical
        panel.getChildren().addAll(
                lblTitulo,
                lblSubtitulo,
                txtUsuarioLogin,
                txtPasswordLogin,
                linkOlvidastePassword,
                btnIniciarSesion
        );

        return panel;
    }

    // Metodo que construye el panel de REGISTRO
    private VBox crearPanelRegistro() {
        VBox panel = new VBox(15); // VBox con 15px de espaciado (más compacto)
        panel.setAlignment(Pos.CENTER);
        panel.setPadding(new Insets(40, 40, 40, 40));

        // Título del panel
        Label lblTitulo = new Label("REGISTRARSE");
        lblTitulo.getStyleClass().add("titulo-registro");

        // Subtítulo
        Label lblSubtitulo = new Label("Crea una nueva cuenta");
        lblSubtitulo.getStyleClass().add("subtitulo-registro");

        // Campo de usuario
        txtUsuarioRegistro = new TextField();
        txtUsuarioRegistro.setPromptText("Usuario");
        txtUsuarioRegistro.setMaxWidth(300);
        txtUsuarioRegistro.getStyleClass().add("registro-field");

        // Campo de nombre completo
        txtNombreRegistro = new TextField();
        txtNombreRegistro.setPromptText("Nombre completo");
        txtNombreRegistro.setMaxWidth(300);
        txtNombreRegistro.getStyleClass().add("registro-field");

        // Campo de email
        txtEmailRegistro = new TextField();
        txtEmailRegistro.setPromptText("Email");
        txtEmailRegistro.setMaxWidth(300);
        txtEmailRegistro.getStyleClass().add("registro-field");

        // ComboBox para seleccionar el rol
        cboRolRegistro = new ComboBox<>();
        cboRolRegistro.getItems().addAll("Administrador", "Proveedor");
        cboRolRegistro.setPromptText("Seleccione un rol");
        cboRolRegistro.setMaxWidth(300);
        cboRolRegistro.getStyleClass().add("registro-field");

        // Campo de contraseña
        txtPasswordRegistro = new PasswordField();
        txtPasswordRegistro.setPromptText("Contraseña");
        txtPasswordRegistro.setMaxWidth(300);
        txtPasswordRegistro.getStyleClass().add("registro-field");

        // Campo de confirmación de contraseña
        txtConfirmarPasswordRegistro = new PasswordField();
        txtConfirmarPasswordRegistro.setPromptText("Confirmar contraseña");
        txtConfirmarPasswordRegistro.setMaxWidth(300);
        txtConfirmarPasswordRegistro.getStyleClass().add("registro-field");

        // Botón de registro
        btnRegistrarse = new Button("REGISTRARSE");
        btnRegistrarse.setMaxWidth(300);
        btnRegistrarse.getStyleClass().add("btn-registro");

        // Agregar todos los componentes al panel
        panel.getChildren().addAll(
                lblTitulo,
                lblSubtitulo,
                txtUsuarioRegistro,
                txtNombreRegistro,
                txtEmailRegistro,
                cboRolRegistro,
                txtPasswordRegistro,
                txtConfirmarPasswordRegistro,
                btnRegistrarse
        );

        return panel;
    }

    // ========== GETTERS: Permiten que el controlador acceda a los componentes ==========

    public BorderPane getRoot() {
        return root;
    }

    // Getters del panel LOGIN
    public TextField getTxtUsuarioLogin() {
        return txtUsuarioLogin;
    }

    public PasswordField getTxtPasswordLogin() {
        return txtPasswordLogin;
    }

    public Button getBtnIniciarSesion() {
        return btnIniciarSesion;
    }

    public Hyperlink getLinkOlvidastePassword() {
        return linkOlvidastePassword;
    }

    // Getters del panel REGISTRO
    public TextField getTxtUsuarioRegistro() {
        return txtUsuarioRegistro;
    }

    public TextField getTxtNombreRegistro() {
        return txtNombreRegistro;
    }

    public TextField getTxtEmailRegistro() {
        return txtEmailRegistro;
    }

    public PasswordField getTxtPasswordRegistro() {
        return txtPasswordRegistro;
    }

    public PasswordField getTxtConfirmarPasswordRegistro() {
        return txtConfirmarPasswordRegistro;
    }

    public ComboBox<String> getCboRolRegistro() {
        return cboRolRegistro;
    }

    public Button getBtnRegistrarse() {
        return btnRegistrarse;
    }
}

