package view;

// Importaciones de JavaFX para construir la interfaz gráfica
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;

// Clase vista que construye la interfaz de inicio de sesión.
public class frmInicio {
    // Contenedor raíz de la vista (BorderPane permite organizar contenido en 5 zonas)
    private BorderPane contenedorInicio;

    // Componentes para LOGIN
    private TextField txtUsuarioLogin;       // Campo para ingresar nombre de usuario
    private PasswordField txtPasswordLogin;  // Campo oculto para contraseña
    private Button btnIniciarSesion;         // Botón para ejecutar el login
    private Hyperlink linkOlvidastePassword; // Link "Olvidaste tu contraseña" 
    private Hyperlink linkRegistrarse;       // Link "¿No tienes cuenta? Regístrate"

    // Constructor: inicializa todos los componentes visuales
    public frmInicio() {
        inicializarComponentes();
    }

    // Metodo que construye toda la estructura visual de la pantalla
    private void inicializarComponentes() {
        // BorderPane como contenedor principal (permite layout flexible)
        contenedorInicio = new BorderPane();
        contenedorInicio.setId("login-contenedorInicio"); // ID para aplicar estilos CSS

        // Panel de login centrado
        VBox panelLogin = crearPanelLogin();
        panelLogin.setId("panel-login");
        panelLogin.setAlignment(Pos.CENTER);
        panelLogin.setPrefSize(500, 600); // Tamaño del panel de login

        // Colocar el panel de login en el centro del BorderPane
        contenedorInicio.setCenter(panelLogin);
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

        // Link "¿No tienes cuenta? Regístrate"
        linkRegistrarse = new Hyperlink("¿No tienes cuenta? Regístrate");
        linkRegistrarse.getStyleClass().add("link-registro");

        // Agregar todos los componentes al panel en orden vertical
        panel.getChildren().addAll(
                lblTitulo,
                lblSubtitulo,
                txtUsuarioLogin,
                txtPasswordLogin,
                linkOlvidastePassword,
                btnIniciarSesion,
                linkRegistrarse
        );

        return panel;
    }


    // ========== GETTERS: Permiten que el controlador acceda a los componentes ==========

    public BorderPane getContenedorInicioRaiz() {
        return contenedorInicio;
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

    public Hyperlink getLinkRegistrarse() {
        return linkRegistrarse;
    }
}

