package view;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

// Vista de registro como ventana independiente modal
public class frmRegistro extends Stage {
    private final BorderPane contenedorRegistro = new BorderPane();

    // Componentes para REGISTRO
    private TextField txtUsuarioRegistro;
    private TextField txtNombreRegistro;
    private TextField txtEmailRegistro;
    private PasswordField txtPasswordRegistro;
    private PasswordField txtConfirmarPasswordRegistro;
    private ComboBox<String> cboRolRegistro;
    private Button btnRegistrarse;
    private Button btnCancelar;

    // Constructor: crea una ventana modal para registro
    public frmRegistro(Stage ventanaPadre) {
        // Configurar la ventana
        setTitle("Registro de Usuario");

        // Hacer la ventana modal
        initModality(Modality.APPLICATION_MODAL);
        initOwner(ventanaPadre);
        initStyle(StageStyle.DECORATED);

        // Construir la estructura
        inicializarComponentes();

        // Crear la escena
        Scene scene = new Scene(contenedorRegistro, 700, 700);

        // Cargar el CSS
        try {
            String css = getClass().getResource("/styles.css").toExternalForm();
            scene.getStylesheets().add(css);
        } catch (Exception e) {
            System.out.println("No se pudo cargar el CSS: " + e.getMessage());
        }

        setScene(scene);
        setResizable(false);
    }

    private void inicializarComponentes() {
        contenedorRegistro.setId("registro-contenedorRegistro");

        VBox contenedorRegistro = crearPanelRegistro();
        this.contenedorRegistro.setCenter(contenedorRegistro);
    }

    private VBox crearPanelRegistro() {
        VBox panel = new VBox(15);
        panel.setAlignment(Pos.CENTER);
        panel.setPadding(new Insets(40, 40, 40, 40));

        // Título del panel
        Label lblTitulo = new Label("REGISTRARSE");
        lblTitulo.getStyleClass().add("titulo-login");

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
        cboRolRegistro.getItems().addAll("Administrador", "Tienda");
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

        // Botones
        HBox panelBotones = new HBox(15);
        panelBotones.setAlignment(Pos.CENTER);

        btnRegistrarse = new Button("REGISTRARSE");
        btnRegistrarse.setMaxWidth(145);
        btnRegistrarse.getStyleClass().add("btn-registro");

        btnCancelar = new Button("CANCELAR");
        btnCancelar.setMaxWidth(145);
        btnCancelar.getStyleClass().add("btn-cancelar");
        btnCancelar.setOnAction(e -> close());

        panelBotones.getChildren().addAll(btnRegistrarse, btnCancelar);

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
                panelBotones
        );

        return panel;
    }

    // Getters
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

    public Button getBtnCancelar() {
        return btnCancelar;
    }
}

