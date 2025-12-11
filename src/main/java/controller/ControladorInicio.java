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
import model.ModeloTienda;
import files.ManejoJson;

// Importaciones de Java estándar
import java.lang.reflect.Type;
import java.util.List;

// Clase controladora que maneja la lógica de autenticación.
// Responsabilidad: validar credenciales, registrar usuarios y gestionar transición a la pantalla principal.
public class ControladorInicio {
    // Referencias a la vista, stage y rutas de los archivos de usuarios
    private frmInicio vista;
    private Stage stage;
    private static final String RUTA_JSON_ADMIN = "src/main/java/model/tbAdmin.json";
    private static final String RUTA_JSON_TIENDA = "src/main/java/model/tbTienda.json";

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

    // Metodo que valida las credenciales y abre la ventana principal si son
    // correctas
    private void iniciarSesion() {
        // 1) Obtener los datos ingresados por el usuario
        String usuario = vista.getTxtUsuarioLogin().getText().trim();
        String password = vista.getTxtPasswordLogin().getText();

        // 2) Validación básica: campos vacíos
        if (usuario.isEmpty() || password.isEmpty()) {
            mostrarAlerta(Alert.AlertType.WARNING, "Campos vacíos", "Por favor complete todos los campos.");
            return; // Termina el metodo sin continuar
        }

        // 3) Buscar el usuario en ambas tablas (tbAdmin y tbTienda)
        String nombreUsuario = null;
        String rolUsuario = null; // Variable para guardar el rol
        boolean autenticado = false;

        // Buscar primero en tbAdmin.json
        Type tipoListaAdmin = ManejoJson.obtenerTipoLista(ModeloAdmin.class);
        List<ModeloAdmin> usuariosAdmin = ManejoJson.leerJson(RUTA_JSON_ADMIN, tipoListaAdmin);

        ModeloAdmin adminEncontrado = usuariosAdmin.stream()
                .filter(u -> u.getUsuario().equals(usuario) && u.getContrasena().equals(password))
                .findFirst()
                .orElse(null);

        if (adminEncontrado != null) {
            nombreUsuario = adminEncontrado.getNombre();
            rolUsuario = adminEncontrado.getRol(); // Obtener rol de Admin
            autenticado = true;
        } else {
            // Si no se encuentra en Admin, buscar en tbTienda.json
            Type tipoListaTienda = ManejoJson.obtenerTipoLista(ModeloTienda.class);
            List<ModeloTienda> usuariosTienda = ManejoJson.leerJson(RUTA_JSON_TIENDA, tipoListaTienda);

            ModeloTienda tiendaEncontrado = usuariosTienda.stream()
                    .filter(u -> u.getUsuario().equals(usuario) && u.getContrasena().equals(password))
                    .findFirst()
                    .orElse(null);

            if (tiendaEncontrado != null) {
                nombreUsuario = tiendaEncontrado.getNombre();
                rolUsuario = tiendaEncontrado.getRol(); // Obtener rol de Tienda
                autenticado = true;
            }
        }

        // 4) Verificar si se encontró el usuario
        if (autenticado) {
            // Login exitoso: mostrar mensaje y abrir ventana principal
            mostrarAlerta(Alert.AlertType.INFORMATION, "Inicio de sesión exitoso",
                    "Bienvenido, " + nombreUsuario);
            abrirVentanaPrincipal(rolUsuario); // Pasar el rol al metodo
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
    private void abrirVentanaPrincipal(String rol) {
        try {
            // 1) Cerrar la ventana de login
            stage.close();

            // 2) Crear un nuevo Stage para la ventana principal
            Stage stagePrincipal = new Stage();
            stagePrincipal.initStyle(StageStyle.UNDECORATED); // Quitar botones de gestión de ventanas (DEBE ir antes de
                                                              // setScene)

            // 3) Crear instancia de la vista principal
            frmPrincipal vistaPrincipal = new frmPrincipal();

            // 4) Crear y asociar el controlador
            new ControladorPrincipal(vistaPrincipal, stagePrincipal, rol);

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
