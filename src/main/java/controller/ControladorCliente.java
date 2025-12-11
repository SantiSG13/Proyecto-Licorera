package controller;

import files.ManejoJson;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import model.ModeloCliente;
import view.frmCliente;

import java.lang.reflect.Type;
import java.util.List;

import files.ConfigRutas;

// Controlador para la vista de gestión de clientes (frmCliente).
// Se encarga de manejar eventos, validaciones y persistencia de datos en tbCliente.json
public class ControladorCliente {

    private final frmCliente vista;
    private final ObservableList<String[]> datosTabla = FXCollections.observableArrayList();

    private final String rolUsuario;

    public ControladorCliente(frmCliente vista, String rol) {
        this.vista = vista;
        this.rolUsuario = rol;
        configurarTabla();
        cargarClientesDesdeArchivo();
        wireActions();
        aplicarRestriccionesIniciales();
    }

    private void configurarTabla() {
        vista.getTablaClientes().setItems(datosTabla);
        vista.getTablaClientes().setPlaceholder(new javafx.scene.control.Label("No hay clientes registrados"));
    }

    private void aplicarRestriccionesIniciales() {
        if ("Tienda".equalsIgnoreCase(rolUsuario)) {
            vista.aplicarRestriccionesTienda();
        }
    }

    private void wireActions() {
        vista.getBtnGuardar().setOnAction(e -> guardarCliente());
        vista.getBtnModificar().setOnAction(e -> abrirFormularioParaModificar());
        vista.getBtnEliminar().setOnAction(e -> eliminarCliente());
    }

    private void guardarCliente() {
        // Detectar si estamos en modo edición o agregar
        if (vista.isModoEdicion()) {
            ejecutarModificacion();
        } else {
            ejecutarGuardado();
        }
    }

    private void ejecutarGuardado() {
        String tipoDoc = vista.getCboTipoDocumento().getValue();
        String documento = vista.getTxtDocumento().getText().trim();
        String nombre = vista.getTxtNombreCompleto().getText().trim();
        String telefono = vista.getTxtTelefono().getText().trim();
        String correo = vista.getTxtCorreo().getText().trim();

        // Validaciones
        if (tipoDoc == null || tipoDoc.isEmpty()) {
            mostrarAdvertencia("Validación", "Debe seleccionar un tipo de documento.");
            return;
        }

        if (documento.isEmpty()) {
            mostrarAdvertencia("Validación", "El documento es obligatorio.");
            return;
        }

        if (!documento.matches("\\d+")) {
            mostrarAdvertencia("Validación", "El documento solo debe contener números.");
            return;
        }

        if (nombre.isEmpty()) {
            mostrarAdvertencia("Validación", "El nombre completo es obligatorio.");
            return;
        }

        if (!telefono.isEmpty() && !telefono.matches("\\d+")) {
            mostrarAdvertencia("Validación", "El teléfono solo debe contener números.");
            return;
        }

        if (!correo.isEmpty() && !correo.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")) {
            mostrarAdvertencia("Validación", "El correo electrónico no tiene un formato válido.");
            return;
        }

        // Leer lista actual del archivo
        Type tipoLista = ManejoJson.obtenerTipoLista(ModeloCliente.class);
        List<ModeloCliente> listaClientes = ManejoJson.leerJson(ConfigRutas.CLIENTE, tipoLista);

        // Verificar documento único
        boolean existe = listaClientes.stream()
                .anyMatch(c -> c.getDocumento().equals(documento));
        if (existe) {
            mostrarAdvertencia("Validación", "Ya existe un cliente con ese número de documento.");
            return;
        }

        // Crear y agregar nuevo cliente
        ModeloCliente cliente = new ModeloCliente(tipoDoc, documento, nombre, telefono, correo);
        listaClientes.add(cliente);
        ManejoJson.escribirJson(ConfigRutas.CLIENTE, listaClientes);

        // Recargar tabla completa
        cargarClientesDesdeArchivo();
        vista.limpiarFormulario();
        vista.cerrarPanelFormulario();
        mostrarExito("Cliente guardado correctamente.");
    }

    // Abre el formulario flotante en modo edición con los datos del cliente
    // seleccionado
    private void abrirFormularioParaModificar() {
        String[] seleccionado = vista.getTablaClientes().getSelectionModel().getSelectedItem();

        if (seleccionado == null) {
            mostrarAdvertencia("Seleccione un cliente", "Debe seleccionar un cliente de la tabla para modificar.");
            return;
        }

        // Abrir el formulario con los datos pre-cargados
        vista.mostrarPanelParaEditar(
                seleccionado[0], // Tipo Doc
                seleccionado[1], // Documento
                seleccionado[2], // Nombre
                seleccionado[3], // Teléfono
                seleccionado[4] // Correo
        );

        // Si es rol Tienda, aplicar restricciones en el formulario
        if ("Tienda".equalsIgnoreCase(rolUsuario)) {
            vista.getCboTipoDocumento().setDisable(true);
            vista.getTxtDocumento().setEditable(false); // Ya debería estar false, pero aseguramos
            vista.getTxtNombreCompleto().setEditable(false);
            // Teléfono y Correo quedan editables por defecto
        } else {
            // Asegurar que estén habilitados si es admin
            vista.getCboTipoDocumento().setDisable(false);
            vista.getTxtNombreCompleto().setEditable(true);
        }
    }

    // Ejecuta la modificación del cliente desde el formulario flotante
    private void ejecutarModificacion() {
        String tipoDoc = vista.getCboTipoDocumento().getValue();
        String documento = vista.getTxtDocumento().getText().trim();
        String nombre = vista.getTxtNombreCompleto().getText().trim();
        String telefono = vista.getTxtTelefono().getText().trim();
        String correo = vista.getTxtCorreo().getText().trim();

        // Validaciones
        if (tipoDoc == null || tipoDoc.isEmpty()) {
            mostrarAdvertencia("Validación", "Debe seleccionar un tipo de documento.");
            return;
        }

        if (documento.isEmpty()) {
            mostrarAdvertencia("Validación", "El documento es obligatorio.");
            return;
        }

        if (!documento.matches("\\d+")) {
            mostrarAdvertencia("Validación", "El documento solo debe contener números.");
            return;
        }

        if (nombre.isEmpty()) {
            mostrarAdvertencia("Validación", "El nombre completo es obligatorio.");
            return;
        }

        if (!telefono.isEmpty() && !telefono.matches("\\d+")) {
            mostrarAdvertencia("Validación", "El teléfono solo debe contener números.");
            return;
        }

        if (!correo.isEmpty() && !correo.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")) {
            mostrarAdvertencia("Validación", "El correo electrónico no tiene un formato válido.");
            return;
        }

        try {
            Type tipoLista = ManejoJson.obtenerTipoLista(ModeloCliente.class);
            List<ModeloCliente> listaClientes = ManejoJson.leerJson(ConfigRutas.CLIENTE, tipoLista);

            String documentoOriginal = vista.getDocumentoOriginal();

            // Si cambió el documento, verificar que no exista
            if (!documento.equals(documentoOriginal)) {
                boolean existe = listaClientes.stream()
                        .anyMatch(c -> c.getDocumento().equals(documento));
                if (existe) {
                    mostrarAdvertencia("Validación", "Ya existe un cliente con ese número de documento.");
                    return;
                }
            }

            // Buscar y actualizar el cliente
            for (ModeloCliente cliente : listaClientes) {
                if (cliente.getDocumento().equals(documentoOriginal)) {
                    cliente.setTipoDocumento(tipoDoc);
                    cliente.setDocumento(documento);
                    cliente.setNombreCompleto(nombre);
                    cliente.setTelefono(telefono);
                    cliente.setCorreo(correo);
                    break;
                }
            }

            ManejoJson.escribirJson(ConfigRutas.CLIENTE, listaClientes);
            cargarClientesDesdeArchivo();
            vista.limpiarFormulario();
            vista.cerrarPanelFormulario(); // Cerrar el panel flotante
            mostrarExito("Cliente modificado correctamente.");

        } catch (Exception e) {
            mostrarError("Error al modificar", "No se pudo modificar el cliente: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void eliminarCliente() {
        String[] seleccionado = vista.getTablaClientes().getSelectionModel().getSelectedItem();

        if (seleccionado == null) {
            mostrarAdvertencia("Seleccione un cliente", "Debe seleccionar un cliente de la tabla para eliminar.");
            return;
        }

        // Confirmar eliminación
        Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
        confirmacion.setTitle("Confirmar eliminación");
        confirmacion.setHeaderText("¿Está seguro de eliminar este cliente?");
        confirmacion.setContentText("Cliente: " + seleccionado[2] + "\nDocumento: " + seleccionado[1]);

        confirmacion.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                try {
                    Type tipoLista = ManejoJson.obtenerTipoLista(ModeloCliente.class);
                    List<ModeloCliente> listaClientes = ManejoJson.leerJson(ConfigRutas.CLIENTE, tipoLista);

                    String documentoBuscado = seleccionado[1];
                    listaClientes.removeIf(cliente -> cliente.getDocumento().equals(documentoBuscado));

                    ManejoJson.escribirJson(ConfigRutas.CLIENTE, listaClientes);
                    cargarClientesDesdeArchivo();
                    vista.limpiarFormulario();
                    mostrarExito("Cliente eliminado correctamente.");

                } catch (Exception e) {
                    mostrarError("Error al eliminar", "No se pudo eliminar el cliente: " + e.getMessage());
                    e.printStackTrace();
                }
            }
        });
    }

    private void cargarClientesDesdeArchivo() {
        Type tipoLista = ManejoJson.obtenerTipoLista(ModeloCliente.class);
        List<ModeloCliente> listaClientes = ManejoJson.leerJson(ConfigRutas.CLIENTE, tipoLista);

        datosTabla.clear();
        for (ModeloCliente c : listaClientes) {
            datosTabla.add(new String[] {
                    c.getTipoDocumento(),
                    c.getDocumento(),
                    c.getNombreCompleto(),
                    c.getTelefono(),
                    c.getCorreo()
            });
        }
    }

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
}
