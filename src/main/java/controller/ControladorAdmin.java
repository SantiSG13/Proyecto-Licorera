package controller;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import view.frmAdmin;
import model.ModeloAdmin;
import files.ManejoJson;

import java.lang.reflect.Type;
import java.util.List;
import java.util.UUID;

// Controlador de la vista de usuarios.
// Responsabilidad: gestionar las acciones de los botones y coordinar la lógica de negocio con la vista.
public class ControladorAdmin {
    private final frmAdmin vista; // Referencia a la vista que se controla
    private static final String RUTA_JSON = "src/main/java/model/tbAdmin.json"; // Ruta del archivo de usuarios

    // Constructor: recibe la vista y configura los eventos de los botones
    public ControladorAdmin(frmAdmin vista) {
        this.vista = vista;
        wireActions(); // Registra todos los manejadores de eventos
        cargarDatos(); // Carga los datos iniciales en la tabla
    }

    // Registra las acciones asociadas a cada botón
    private void wireActions() {
        // Acción del botón Guardar: valida y guarda un nuevo usuario
        vista.getBtnGuardar().setOnAction(e -> guardarUsuario());

        // Acción del botón Modificar: actualiza los datos del usuario seleccionado
        vista.getBtnModificar().setOnAction(e -> modificarUsuario());

        // Acción del botón Eliminar: elimina el usuario seleccionado previa confirmación
        vista.getBtnEliminar().setOnAction(e -> eliminarUsuario());

        // Acción del botón Salir: cierra la vista actual o vuelve al menú principal
        vista.getBtnSalir().setOnAction(e -> salir());

        // Acción al seleccionar una fila en la tabla: carga los datos en el formulario
        vista.getTabla().setOnMouseClicked(e -> {
            if (e.getClickCount() == 1 && vista.getTabla().getSelectionModel().getSelectedItem() != null) {
                cargarDatosSeleccionados();
            }
        });
    }

    // Carga los datos iniciales desde el archivo JSON
    private void cargarDatos() {
        try {
            vista.getTabla().getItems().clear();

            // Leer usuarios desde el archivo JSON
            Type tipoLista = ManejoJson.obtenerTipoLista(ModeloAdmin.class);
            List<ModeloAdmin> usuarios = ManejoJson.leerJson(RUTA_JSON, tipoLista);

            // Agregar cada usuario a la tabla
            for (ModeloAdmin usuario : usuarios) {
                String[] fila = {
                    usuario.getId(),
                    usuario.getNombre(),
                    usuario.getUsuario(),
                    usuario.getEmail(),
                    usuario.getRol()
                };
                vista.getTabla().getItems().add(fila);
            }
        } catch (Exception e) {
            mostrarError("Error al cargar datos", "No se pudieron cargar los usuarios: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // Guarda un nuevo usuario validando los campos del formulario
    private void guardarUsuario() {
        // Validar campos obligatorios
        if (!validarCampos()) {
            return;
        }

        try {
            // Leer usuarios existentes
            Type tipoLista = ManejoJson.obtenerTipoLista(ModeloAdmin.class);
            List<ModeloAdmin> usuarios = ManejoJson.leerJson(RUTA_JSON, tipoLista);

            // Verificar si el nombre de usuario ya existe
            String nuevoUsuario = vista.getTxtUsuario().getText().trim();
            boolean usuarioExiste = usuarios.stream()
                    .anyMatch(u -> u.getUsuario().equalsIgnoreCase(nuevoUsuario));

            if (usuarioExiste) {
                mostrarAdvertencia("Usuario existente", "El nombre de usuario ya está en uso. Elija otro.");
                return;
            }

            // Crear nuevo usuario
            ModeloAdmin usuario = new ModeloAdmin(
                    UUID.randomUUID().toString(), // ID único
                    vista.getTxtUsuario().getText().trim(),
                    vista.getTxtPassword().getText(),
                    vista.getTxtNombre().getText().trim(),
                    vista.getCboRol().getValue(),
                    vista.getTxtEmail().getText().trim()
            );

            // Agregar a la lista y guardar en JSON
            usuarios.add(usuario);
            ManejoJson.escribirJson(RUTA_JSON, usuarios);

            // Recargar la tabla
            cargarDatos();
            vista.limpiarFormulario();
            mostrarExito("Usuario guardado correctamente");

        } catch (Exception e) {
            mostrarError("Error al guardar", "No se pudo guardar el usuario: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // Modifica el usuario seleccionado en la tabla
    private void modificarUsuario() {
        String[] seleccionado = vista.getTabla().getSelectionModel().getSelectedItem();

        if (seleccionado == null) {
            mostrarAdvertencia("Seleccione un usuario", "Debe seleccionar un usuario de la tabla para modificar.");
            return;
        }

        if (!validarCampos()) {
            return;
        }

        try {
            // Leer usuarios existentes
            Type tipoLista = ManejoJson.obtenerTipoLista(ModeloAdmin.class);
            List<ModeloAdmin> usuarios = ManejoJson.leerJson(RUTA_JSON, tipoLista);

            // Buscar el usuario por ID y actualizar
            String idBuscado = seleccionado[0];
            for (ModeloAdmin usuario : usuarios) {
                if (usuario.getId().equals(idBuscado)) {
                    usuario.setNombre(vista.getTxtNombre().getText().trim());
                    usuario.setUsuario(vista.getTxtUsuario().getText().trim());
                    usuario.setEmail(vista.getTxtEmail().getText().trim());
                    usuario.setRol(vista.getCboRol().getValue());

                    // Solo actualizar contraseña si se ingresó una nueva
                    if (!vista.getTxtPassword().getText().trim().isEmpty()) {
                        usuario.setContrasena(vista.getTxtPassword().getText());
                    }
                    break;
                }
            }

            // Guardar cambios en JSON
            ManejoJson.escribirJson(RUTA_JSON, usuarios);

            // Recargar la tabla
            cargarDatos();
            vista.limpiarFormulario();
            mostrarExito("Usuario modificado correctamente");

        } catch (Exception e) {
            mostrarError("Error al modificar", "No se pudo modificar el usuario: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // Elimina el usuario seleccionado previa confirmación
    private void eliminarUsuario() {
        String[] seleccionado = vista.getTabla().getSelectionModel().getSelectedItem();

        if (seleccionado == null) {
            mostrarAdvertencia("Seleccione un usuario", "Debe seleccionar un usuario de la tabla para eliminar.");
            return;
        }

        // Confirmar eliminación
        Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
        confirmacion.setTitle("Confirmar eliminación");
        confirmacion.setHeaderText("¿Está seguro de eliminar este usuario?");
        confirmacion.setContentText("Usuario: " + seleccionado[2] + " (" + seleccionado[1] + ")");

        confirmacion.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                try {
                    // Leer usuarios existentes
                    Type tipoLista = ManejoJson.obtenerTipoLista(ModeloAdmin.class);
                    List<ModeloAdmin> usuarios = ManejoJson.leerJson(RUTA_JSON, tipoLista);

                    // Buscar y eliminar el usuario por ID
                    String idBuscado = seleccionado[0];
                    usuarios.removeIf(usuario -> usuario.getId().equals(idBuscado));

                    // Guardar cambios en JSON
                    ManejoJson.escribirJson(RUTA_JSON, usuarios);

                    // Recargar la tabla
                    cargarDatos();
                    vista.limpiarFormulario();
                    mostrarExito("Usuario eliminado correctamente");

                } catch (Exception e) {
                    mostrarError("Error al eliminar", "No se pudo eliminar el usuario: " + e.getMessage());
                    e.printStackTrace();
                }
            }
        });
    }

    // Carga los datos del usuario seleccionado en el formulario
    private void cargarDatosSeleccionados() {
        String[] seleccionado = vista.getTabla().getSelectionModel().getSelectedItem();

        if (seleccionado != null && seleccionado.length >= 5) {
            vista.getTxtId().setText(seleccionado[0]);
            vista.getTxtNombre().setText(seleccionado[1]);
            vista.getTxtUsuario().setText(seleccionado[2]);
            vista.getTxtEmail().setText(seleccionado[3]);
            vista.getCboRol().setValue(seleccionado[4]);
            // No cargamos la contraseña por seguridad
        }
    }

    // Valida que todos los campos obligatorios estén completos
    private boolean validarCampos() {
        if (vista.getTxtNombre().getText().trim().isEmpty()) {
            mostrarAdvertencia("Campo requerido", "El nombre es obligatorio.");
            return false;
        }


        if (vista.getTxtUsuario().getText().trim().isEmpty()) {
            mostrarAdvertencia("Campo requerido", "El nombre de usuario es obligatorio.");
            return false;
        }

        // Contraseña obligatoria solo para usuarios nuevos (sin ID)
        if (vista.getTxtPassword().getText().trim().isEmpty() && vista.getTxtId().getText().trim().isEmpty()) {
            mostrarAdvertencia("Campo requerido", "La contraseña es obligatoria para usuarios nuevos.");
            return false;
        }

        if (vista.getTxtEmail().getText().trim().isEmpty()) {
            mostrarAdvertencia("Campo requerido", "El email es obligatorio.");
            return false;
        }

        if (vista.getCboRol().getValue() == null) {
            mostrarAdvertencia("Campo requerido", "Debe seleccionar un rol.");
            return false;
        }

        // Validar formato de email
        String email = vista.getTxtEmail().getText().trim();
        if (!email.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            mostrarAdvertencia("Email inválido", "El formato del email no es válido.");
            return false;
        }

        return true;
    }

    // Genera un ID único para el nuevo usuario
    private String generarId() {
        int maxId = 0;
        for (String[] usuario : vista.getTabla().getItems()) {
            try {
                int id = Integer.parseInt(usuario[0]);
                if (id > maxId) {
                    maxId = id;
                }
            } catch (NumberFormatException e) {
                // Ignorar IDs no numéricos
            }
        }
        return String.valueOf(maxId + 1);
    }

    // Cierra la vista actual y regresa a frmPrincipal
    private void salir() {
        // Cerrar la ventana modal (esto automáticamente regresa a frmPrincipal)
        vista.close();
    }

    // Métodos auxiliares para mostrar diálogos
    private void mostrarExito(String mensaje) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Éxito");
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    private void mostrarError(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    private void mostrarAdvertencia(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    private void mostrarInfo(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}
