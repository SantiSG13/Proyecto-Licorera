package controller;

import javafx.scene.control.Alert;
import model.ModeloAdmin;
import files.ManejoJson;
import view.frmRegistro;

import java.lang.reflect.Type;
import java.util.List;
import java.util.UUID;

// Controlador para la ventana de registro de usuarios
public class ControladorRegistro {
    private frmRegistro vista;
    private static final String RUTA_JSON = "src/main/java/model/tbAdmin.json";

    public ControladorRegistro(frmRegistro vista) {
        this.vista = vista;
        configurarEventos();
    }

    private void configurarEventos() {
        // Evento: al hacer clic en "Registrarse"
        vista.getBtnRegistrarse().setOnAction(e -> registrarUsuario());

        // Permitir registro al presionar ENTER en el campo de confirmación
        vista.getTxtConfirmarPasswordRegistro().setOnAction(e -> registrarUsuario());
    }

    // Metodo que registra un nuevo usuario en el sistema
    private void registrarUsuario() {
        // 1) Obtener los datos ingresados en el formulario de registro
        String usuario = vista.getTxtUsuarioRegistro().getText().trim();
        String nombre = vista.getTxtNombreRegistro().getText().trim();
        String email = vista.getTxtEmailRegistro().getText().trim();
        String rol = vista.getCboRolRegistro().getValue();
        String password = vista.getTxtPasswordRegistro().getText();
        String confirmarPassword = vista.getTxtConfirmarPasswordRegistro().getText();

        // 2) Validación: todos los campos deben estar completos
        if (usuario.isEmpty() || nombre.isEmpty() || email.isEmpty() ||
            rol == null || password.isEmpty() || confirmarPassword.isEmpty()) {
            mostrarAlerta(Alert.AlertType.WARNING, "Campos vacíos",
                    "Por favor complete todos los campos.");
            return;
        }

        // 3) Validación: las contraseñas deben coincidir
        if (!password.equals(confirmarPassword)) {
            mostrarAlerta(Alert.AlertType.ERROR, "Error en contraseña",
                    "Las contraseñas no coinciden.");
            return;
        }

        // 4) Validación: contraseña debe tener al menos 6 caracteres (seguridad básica)
        if (password.length() < 6) {
            mostrarAlerta(Alert.AlertType.WARNING, "Contraseña débil",
                    "La contraseña debe tener al menos 6 caracteres.");
            return;
        }

        // 5) Leer la lista de usuarios existentes
        Type tipoLista = ManejoJson.obtenerTipoLista(ModeloAdmin.class);
        List<ModeloAdmin> usuarios = ManejoJson.leerJson(RUTA_JSON, tipoLista);

        // 6) Validación: verificar que el nombre de usuario no esté en uso
        boolean usuarioExiste = usuarios.stream()
                .anyMatch(u -> u.getUsuario().equalsIgnoreCase(usuario));

        if (usuarioExiste) {
            mostrarAlerta(Alert.AlertType.ERROR, "Usuario existente",
                    "El nombre de usuario ya está en uso. Elija otro.");
            return;
        }

        // 7) Crear el nuevo usuario
        ModeloAdmin nuevoUsuario = new ModeloAdmin(
                UUID.randomUUID().toString(),  // ID único generado automáticamente
                usuario,                        // Nombre de usuario
                password,                       // Contraseña
                nombre,                         // Nombre completo
                rol,                           // Rol del usuario
                email                          // Email
        );

        // 8) Agregar el nuevo usuario a la lista y guardar en el archivo
        usuarios.add(nuevoUsuario);
        ManejoJson.escribirJson(RUTA_JSON, usuarios);

        // 9) Mostrar mensaje de éxito y limpiar el formulario
        mostrarAlerta(Alert.AlertType.INFORMATION, "Registro exitoso",
                "Usuario registrado correctamente. Ya puede iniciar sesión.");
        limpiarCampos();

        // Cerrar la ventana de registro
        vista.close();
    }

    // Limpia todos los campos del formulario
    private void limpiarCampos() {
        vista.getTxtUsuarioRegistro().clear();
        vista.getTxtNombreRegistro().clear();
        vista.getTxtEmailRegistro().clear();
        vista.getTxtPasswordRegistro().clear();
        vista.getTxtConfirmarPasswordRegistro().clear();
        vista.getCboRolRegistro().setValue(null);
    }

    // Metodo auxiliar para mostrar alertas al usuario
    private void mostrarAlerta(Alert.AlertType tipo, String titulo, String mensaje) {
        Alert alerta = new Alert(tipo);
        alerta.setTitle(titulo);
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);
        alerta.showAndWait();
    }
}

