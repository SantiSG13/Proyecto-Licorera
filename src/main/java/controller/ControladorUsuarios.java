package controller;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import view.frmUsuarios;
import model.ModeloAdmin;
import model.ModeloTienda;
import files.ManejoJson;

import java.lang.reflect.Type;
import java.util.List;

import files.ConfigRutas;

// Controlador de la vista de usuarios.
// Responsabilidad: gestionar las acciones de los botones y coordinar la lógica de negocio con la vista.
public class ControladorUsuarios {
    private final frmUsuarios vista; // Referencia a la vista que se controla

    // Constructor: recibe la vista y configura los eventos de los botones
    public ControladorUsuarios(frmUsuarios vista) {
        this.vista = vista;
        wireActions(); // Registra todos los manejadores de eventos
        cargarDatos(); // Carga los datos iniciales en la tabla
    }

    // Registra las acciones asociadas a cada botón
    private void wireActions() {
        // Acción del botón Guardar: valida y guarda un nuevo usuario
        vista.getBtnGuardar().setOnAction(e -> guardarUsuario());

        // Acción del botón Modificar: abre el formulario con los datos del usuario
        // seleccionado
        vista.getBtnModificar().setOnAction(e -> abrirFormularioParaModificar());

        // Acción del botón Eliminar: elimina el usuario seleccionado previa
        // confirmación
        vista.getBtnEliminar().setOnAction(e -> eliminarUsuario());

        // Acción del botón Salir: cierra la vista actual o vuelve al menú principal
        vista.getBtnSalir().setOnAction(e -> salir());
    }

    // Carga los datos iniciales desde ambos archivos JSON (Admin y Tienda)
    private void cargarDatos() {
        try {
            vista.getTabla().getItems().clear();

            // Leer usuarios administradores
            Type tipoListaAdmin = ManejoJson.obtenerTipoLista(ModeloAdmin.class);
            List<ModeloAdmin> usuariosAdmin = ManejoJson.leerJson(ConfigRutas.ADMIN, tipoListaAdmin);

            // Agregar administradores a la tabla
            for (ModeloAdmin usuario : usuariosAdmin) {
                String[] fila = {
                        usuario.getId(),
                        usuario.getNombre(),
                        usuario.getUsuario(),
                        usuario.getEmail(),
                        usuario.getRol()
                };
                vista.getTabla().getItems().add(fila);
            }

            // Leer usuarios de tienda
            Type tipoListaTienda = ManejoJson.obtenerTipoLista(ModeloTienda.class);
            List<ModeloTienda> usuariosTienda = ManejoJson.leerJson(ConfigRutas.TIENDA, tipoListaTienda);

            // Agregar usuarios de tienda a la tabla
            for (ModeloTienda usuario : usuariosTienda) {
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

    // Guarda un nuevo usuario o modifica uno existente según el modo del formulario
    private void guardarUsuario() {
        // Detectar si estamos en modo edición o agregar
        if (vista.isModoEdicion()) {
            ejecutarModificacion();
        } else {
            ejecutarGuardado();
        }
    }

    // Ejecuta el guardado de un nuevo usuario
    private void ejecutarGuardado() {
        // Validar campos obligatorios
        if (!validarCampos()) {
            return;
        }

        try {
            String nuevoUsuario = vista.getTxtUsuario().getText().trim();
            String rolSeleccionado = vista.getCboRol().getValue();

            // Verificar que el usuario no exista en ninguna de las dos tablas
            if (usuarioExisteEnSistema(nuevoUsuario)) {
                mostrarAdvertencia("Usuario existente", "El nombre de usuario ya está en uso. Elija otro.");
                return;
            }

            // Guardar según el rol seleccionado
            if (rolSeleccionado.equals("Administrador")) {
                // Guardar en tbAdmin.json
                Type tipoListaAdmin = ManejoJson.obtenerTipoLista(ModeloAdmin.class);
                List<ModeloAdmin> usuariosAdmin = ManejoJson.leerJson(ConfigRutas.ADMIN, tipoListaAdmin);

                // Generar ID con formato ADM-001, ADM-002, etc.
                String idUnico = generarIdPorRol("ADM", usuariosAdmin.size());

                ModeloAdmin usuario = new ModeloAdmin(
                        idUnico,
                        vista.getTxtUsuario().getText().trim(),
                        vista.getTxtPassword().getText(),
                        vista.getTxtNombre().getText().trim(),
                        rolSeleccionado,
                        vista.getTxtEmail().getText().trim());

                usuariosAdmin.add(usuario);
                ManejoJson.escribirJson(ConfigRutas.ADMIN, usuariosAdmin);

            } else if (rolSeleccionado.equals("Tienda")) {
                // Guardar en tbTienda.json
                Type tipoListaTienda = ManejoJson.obtenerTipoLista(ModeloTienda.class);
                List<ModeloTienda> usuariosTienda = ManejoJson.leerJson(ConfigRutas.TIENDA, tipoListaTienda);

                // Generar ID con formato TDA-001, TDA-002, etc.
                String idUnico = generarIdPorRol("TDA", usuariosTienda.size());

                ModeloTienda usuario = new ModeloTienda(
                        idUnico,
                        vista.getTxtUsuario().getText().trim(),
                        vista.getTxtPassword().getText(),
                        vista.getTxtNombre().getText().trim(),
                        rolSeleccionado,
                        vista.getTxtEmail().getText().trim());

                usuariosTienda.add(usuario);
                ManejoJson.escribirJson(ConfigRutas.TIENDA, usuariosTienda);
            }

            // Recargar la tabla
            cargarDatos();
            vista.limpiarFormulario();
            vista.cerrarPanelFormulario(); // Cerrar el panel flotante
            mostrarExito("Usuario guardado correctamente en " +
                    (rolSeleccionado.equals("Administrador") ? "tbAdmin.json" : "tbTienda.json"));

        } catch (Exception e) {
            mostrarError("Error al guardar", "No se pudo guardar el usuario: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // Verifica si un usuario ya existe en cualquiera de las dos tablas
    private boolean usuarioExisteEnSistema(String nombreUsuario) {
        try {
            // Verificar en tbAdmin
            Type tipoListaAdmin = ManejoJson.obtenerTipoLista(ModeloAdmin.class);
            List<ModeloAdmin> usuariosAdmin = ManejoJson.leerJson(ConfigRutas.ADMIN, tipoListaAdmin);
            boolean existeEnAdmin = usuariosAdmin.stream()
                    .anyMatch(u -> u.getUsuario().equalsIgnoreCase(nombreUsuario));

            if (existeEnAdmin)
                return true;

            // Verificar en tbTienda
            Type tipoListaTienda = ManejoJson.obtenerTipoLista(ModeloTienda.class);
            List<ModeloTienda> usuariosTienda = ManejoJson.leerJson(ConfigRutas.TIENDA, tipoListaTienda);
            boolean existeEnTienda = usuariosTienda.stream()
                    .anyMatch(u -> u.getUsuario().equalsIgnoreCase(nombreUsuario));

            return existeEnTienda;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // Abre el formulario flotante en modo edición con los datos del usuario
    // seleccionado
    private void abrirFormularioParaModificar() {
        String[] seleccionado = vista.getTabla().getSelectionModel().getSelectedItem();

        if (seleccionado == null) {
            mostrarAdvertencia("Seleccione un usuario", "Debe seleccionar un usuario de la tabla para modificar.");
            return;
        }

        // Abrir el formulario con los datos pre-cargados
        vista.mostrarPanelParaEditar(
                seleccionado[0], // ID
                seleccionado[1], // Nombre
                seleccionado[2], // Usuario
                seleccionado[3], // Email
                seleccionado[4] // Rol
        );
    }

    // Ejecuta la modificación del usuario desde el formulario flotante
    private void ejecutarModificacion() {
        if (!validarCampos()) {
            return;
        }

        try {
            String idBuscado = vista.getIdOriginal();

            // Necesitamos obtener el rol original de la tabla para saber de dónde mover
            String[] seleccionado = vista.getTabla().getItems().stream()
                    .filter(item -> item[0].equals(idBuscado))
                    .findFirst()
                    .orElse(null);

            if (seleccionado == null) {
                mostrarError("Error", "No se encontró el usuario en la tabla.");
                return;
            }

            String rolOriginalTabla = seleccionado[4];
            String rolNuevo = vista.getCboRol().getValue();

            // Si se cambió el rol, necesitamos mover el usuario de una tabla a otra
            if (!rolOriginalTabla.equals(rolNuevo)) {
                // Eliminar del archivo original
                if (rolOriginalTabla.equals("Administrador")) {
                    Type tipoListaAdmin = ManejoJson.obtenerTipoLista(ModeloAdmin.class);
                    List<ModeloAdmin> usuariosAdmin = ManejoJson.leerJson(ConfigRutas.ADMIN, tipoListaAdmin);
                    usuariosAdmin.removeIf(u -> u.getId().equals(idBuscado));
                    ManejoJson.escribirJson(ConfigRutas.ADMIN, usuariosAdmin);
                } else {
                    Type tipoListaTienda = ManejoJson.obtenerTipoLista(ModeloTienda.class);
                    List<ModeloTienda> usuariosTienda = ManejoJson.leerJson(ConfigRutas.TIENDA, tipoListaTienda);
                    usuariosTienda.removeIf(u -> u.getId().equals(idBuscado));
                    ManejoJson.escribirJson(ConfigRutas.TIENDA, usuariosTienda);
                }

                // Agregar al nuevo archivo
                if (rolNuevo.equals("Administrador")) {
                    Type tipoListaAdmin = ManejoJson.obtenerTipoLista(ModeloAdmin.class);
                    List<ModeloAdmin> usuariosAdmin = ManejoJson.leerJson(ConfigRutas.ADMIN, tipoListaAdmin);

                    ModeloAdmin usuario = new ModeloAdmin(
                            idBuscado,
                            vista.getTxtUsuario().getText().trim(),
                            vista.getTxtPassword().getText().trim().isEmpty() ? "" : vista.getTxtPassword().getText(),
                            vista.getTxtNombre().getText().trim(),
                            rolNuevo,
                            vista.getTxtEmail().getText().trim());
                    usuariosAdmin.add(usuario);
                    ManejoJson.escribirJson(ConfigRutas.ADMIN, usuariosAdmin);
                } else {
                    Type tipoListaTienda = ManejoJson.obtenerTipoLista(ModeloTienda.class);
                    List<ModeloTienda> usuariosTienda = ManejoJson.leerJson(ConfigRutas.TIENDA, tipoListaTienda);

                    ModeloTienda usuario = new ModeloTienda(
                            idBuscado,
                            vista.getTxtUsuario().getText().trim(),
                            vista.getTxtPassword().getText().trim().isEmpty() ? "" : vista.getTxtPassword().getText(),
                            vista.getTxtNombre().getText().trim(),
                            rolNuevo,
                            vista.getTxtEmail().getText().trim());
                    usuariosTienda.add(usuario);
                    ManejoJson.escribirJson(ConfigRutas.TIENDA, usuariosTienda);
                }
            } else {
                // Si el rol no cambió, solo actualizar en la tabla correspondiente
                if (rolOriginalTabla.equals("Administrador")) {
                    Type tipoListaAdmin = ManejoJson.obtenerTipoLista(ModeloAdmin.class);
                    List<ModeloAdmin> usuariosAdmin = ManejoJson.leerJson(ConfigRutas.ADMIN, tipoListaAdmin);

                    for (ModeloAdmin usuario : usuariosAdmin) {
                        if (usuario.getId().equals(idBuscado)) {
                            usuario.setNombre(vista.getTxtNombre().getText().trim());
                            usuario.setUsuario(vista.getTxtUsuario().getText().trim());
                            usuario.setEmail(vista.getTxtEmail().getText().trim());
                            usuario.setRol(rolNuevo);

                            if (!vista.getTxtPassword().getText().trim().isEmpty()) {
                                usuario.setContrasena(vista.getTxtPassword().getText());
                            }
                            break;
                        }
                    }
                    ManejoJson.escribirJson(ConfigRutas.ADMIN, usuariosAdmin);
                } else {
                    Type tipoListaTienda = ManejoJson.obtenerTipoLista(ModeloTienda.class);
                    List<ModeloTienda> usuariosTienda = ManejoJson.leerJson(ConfigRutas.TIENDA, tipoListaTienda);

                    for (ModeloTienda usuario : usuariosTienda) {
                        if (usuario.getId().equals(idBuscado)) {
                            usuario.setNombre(vista.getTxtNombre().getText().trim());
                            usuario.setUsuario(vista.getTxtUsuario().getText().trim());
                            usuario.setEmail(vista.getTxtEmail().getText().trim());
                            usuario.setRol(rolNuevo);

                            if (!vista.getTxtPassword().getText().trim().isEmpty()) {
                                usuario.setContrasena(vista.getTxtPassword().getText());
                            }
                            break;
                        }
                    }
                    ManejoJson.escribirJson(ConfigRutas.TIENDA, usuariosTienda);
                }
            }

            // Recargar la tabla
            cargarDatos();
            vista.limpiarFormulario();
            vista.cerrarPanelFormulario(); // Cerrar el panel flotante
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
        confirmacion
                .setContentText("Usuario: " + seleccionado[2] + " (" + seleccionado[1] + ")\nRol: " + seleccionado[4]);

        confirmacion.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                try {
                    String idBuscado = seleccionado[0];
                    String rol = seleccionado[4];

                    // Eliminar del archivo correspondiente según el rol
                    if (rol.equals("Administrador")) {
                        Type tipoListaAdmin = ManejoJson.obtenerTipoLista(ModeloAdmin.class);
                        List<ModeloAdmin> usuariosAdmin = ManejoJson.leerJson(ConfigRutas.ADMIN, tipoListaAdmin);
                        usuariosAdmin.removeIf(usuario -> usuario.getId().equals(idBuscado));
                        ManejoJson.escribirJson(ConfigRutas.ADMIN, usuariosAdmin);
                    } else if (rol.equals("Tienda")) {
                        Type tipoListaTienda = ManejoJson.obtenerTipoLista(ModeloTienda.class);
                        List<ModeloTienda> usuariosTienda = ManejoJson.leerJson(ConfigRutas.TIENDA, tipoListaTienda);
                        usuariosTienda.removeIf(usuario -> usuario.getId().equals(idBuscado));
                        ManejoJson.escribirJson(ConfigRutas.TIENDA, usuariosTienda);
                    }

                    // Recargar la tabla
                    cargarDatos();
                    vista.limpiarFormulario();
                    mostrarExito("Usuario eliminado correctamente de " +
                            (rol.equals("Administrador") ? "tbAdmin.json" : "tbTienda.json"));

                } catch (Exception e) {
                    mostrarError("Error al eliminar", "No se pudo eliminar el usuario: " + e.getMessage());
                    e.printStackTrace();
                }
            }
        });
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

    // Genera un ID con formato: PREFIJO-### (ej: ADM-001, TDA-015)
    // @param prefijo - Prefijo del ID (ADM para Administrador, TDA para Tienda)
    // @param cantidadActual - Cantidad actual de usuarios en esa tabla
    private String generarIdPorRol(String prefijo, int cantidadActual) {
        int nuevoNumero = cantidadActual + 1;
        // Formatea el número con 3 dígitos (001, 002, ..., 999)
        return String.format("%s-%03d", prefijo, nuevoNumero);
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