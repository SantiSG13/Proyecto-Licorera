package controller;

// Importaciones de JavaFX para manejo de eventos y alertas
import javafx.scene.control.Alert;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.scene.Scene;

// Importaciones del proyecto
import view.frmInicio;
import view.frmPrincipal;
import view.frmRegistro;
import model.ModeloAdmin;
import files.ManejoJson;

// Importaciones de Java estándar
import java.lang.reflect.Type;
import java.util.List;

// Clase controladora que maneja la lógica de autenticación.
// Responsabilidad: validar credenciales, registrar usuarios y gestionar transición a la pantalla principal.
public class ControladorInicio {
    // Referencias a la vista, stage y ruta del archivo de usuarios
    private frmInicio vista;
    private Stage stage;
    private static final String RUTA_JSON = "src/main/java/model/tbAdmin.json";

    // Constructor: enlaza la vista con los manejadores de eventos
    public ControladorInicio(frmInicio vista, Stage stage) {
        this.vista = vista;
        this.stage = stage;
        configurarEventos();
    }

    // Metodo que registra los manejadores de eventos a los botones
    private void configurarEventos() {
        // Evento: al hacer clic en "Iniciar Sesión"
        vista.getBtnIniciarSesion().setOnAction(e -> iniciarSesion());

        // Evento: al hacer clic en "Registrarse" - abre ventana de registro
        vista.getLinkRegistrarse().setOnAction(e -> abrirVentanaRegistro());

        // Evento: al hacer clic en "Olvidaste tu contraseña" (por implementar)
        vista.getLinkOlvidastePassword().setOnAction(e -> {
            mostrarAlerta(Alert.AlertType.INFORMATION, "Función no disponible",
                    "Esta funcionalidad será implementada próximamente.");
        });

        // Evento: permitir login al presionar ENTER en el campo de contraseña
        vista.getTxtPasswordLogin().setOnAction(e -> iniciarSesion());
    }

    // Metodo que valida las credenciales y abre la ventana principal si son correctas
    private void iniciarSesion() {
        // 1) Obtener los datos ingresados por el usuario
        String usuario = vista.getTxtUsuarioLogin().getText().trim();
        String password = vista.getTxtPasswordLogin().getText();

        // 2) Validación básica: campos vacíos
        if (usuario.isEmpty() || password.isEmpty()) {
            mostrarAlerta(Alert.AlertType.WARNING, "Campos vacíos", "Por favor complete todos los campos.");
            return; // Termina el metodo sin continuar
        }

        // 3) Leer la lista de usuarios desde el archivo JSON
        Type tipoLista = ManejoJson.obtenerTipoLista(ModeloAdmin.class);
        List<ModeloAdmin> usuarios = ManejoJson.leerJson(RUTA_JSON, tipoLista);

        // 4) Buscar coincidencia de usuario y contraseña
        // Stream API: forma funcional de iterar y buscar en colecciones
        ModeloAdmin usuarioEncontrado = usuarios.stream()
                .filter(u -> u.getUsuario().equals(usuario) && u.getContrasena().equals(password)) // Filtra por usuario y contraseña
                .findFirst() // Obtiene el primer elemento que coincida
                .orElse(null); // Si no encuentra, retorna null

        // 5) Verificar si se encontró el usuario
        if (usuarioEncontrado != null) {
            // Login exitoso: mostrar mensaje y abrir ventana principal
            mostrarAlerta(Alert.AlertType.INFORMATION, "Inicio de sesión exitoso",
                    "Bienvenido, " + usuarioEncontrado.getNombre());
            abrirVentanaPrincipal();
        } else {
            // Credenciales incorrectas
            mostrarAlerta(Alert.AlertType.ERROR, "Error de autenticación",
                    "Usuario o contraseña incorrectos.");
        }
    }

    // Metodo que abre la ventana de registro
    private void abrirVentanaRegistro() {
        try {
            // Crear una nueva instancia de la ventana de registro
            frmRegistro ventanaRegistro = new frmRegistro(stage);

            // Crear el controlador para manejar los eventos de registro
            new ControladorRegistro(ventanaRegistro);

            // Mostrar la ventana de registro (modal)
            ventanaRegistro.showAndWait();

        } catch (Exception e) {
            mostrarAlerta(Alert.AlertType.ERROR, "Error",
                    "No se pudo abrir la ventana de registro: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // Metodo que abre la ventana principal del sistema
    private void abrirVentanaPrincipal() {
        try {
            // 1) Cerrar la ventana de login
            stage.close();

            // 2) Crear un nuevo Stage para la ventana principal
            Stage stagePrincipal = new Stage();
            stagePrincipal.initStyle(StageStyle.UNDECORATED); // Quitar botones de gestión de ventanas (DEBE ir antes de setScene)

            // 3) Crear instancia de la vista principal
            frmPrincipal vistaPrincipal = new frmPrincipal();

            // 4) Crear y asociar el controlador
            new ControladorPrincipal(vistaPrincipal, stagePrincipal);

            // 5) Crear nueva escena con la vista principal (tamaño de pantalla completa)
            Scene scene = new Scene(vistaPrincipal.getPanelPrincipal());

            // 6) Cargar estilos CSS
            try {
                String css = getClass().getResource("/styles.css").toExternalForm();
                scene.getStylesheets().add(css);
            } catch (Exception ignored) {
                // Si no se encuentra el CSS, continuar sin estilos
            }

            // 7) Configurar ventana para pantalla completa sin redimensionar
            stagePrincipal.setScene(scene);
            stagePrincipal.setTitle("Proyecto Licorera - Sistema Principal");
            stagePrincipal.setMaximized(true); // Maximizar la ventana
            stagePrincipal.setResizable(false); // Bloquear redimensionamiento
            stagePrincipal.show();

        } catch (Exception e) {
            // En caso de error al abrir la ventana principal
            mostrarAlerta(Alert.AlertType.ERROR, "Error",
                    "No se pudo abrir la ventana principal: " + e.getMessage());
            e.printStackTrace();
        }
    }


    // Metodo utilitario para mostrar alertas al usuario
    // Alert.AlertType define el tipo de alerta (INFO, WARNING, ERROR, etc.)
    private void mostrarAlerta(Alert.AlertType tipo, String titulo, String mensaje) {
        Alert alerta = new Alert(tipo);
        alerta.setTitle(titulo);
        alerta.setHeaderText(null); // Sin encabezado
        alerta.setContentText(mensaje);
        alerta.showAndWait(); // Muestra la alerta y espera que el usuario la cierre
    }
}

