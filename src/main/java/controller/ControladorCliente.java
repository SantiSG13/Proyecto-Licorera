package controller;

import files.ManejoJson;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import model.ModeloCliente;
import view.frmCliente;

import java.lang.reflect.Type;
import java.util.List;

// Controlador para la vista de gestión de clientes (frmCliente).
// Se encarga de manejar eventos, validaciones y persistencia de datos en tbCliente.json
public class ControladorCliente {

    private final frmCliente vista;
    private final ObservableList<String[]> datosTabla = FXCollections.observableArrayList();
    private final String RUTA_ARCHIVO = "src/main/java/model/tbCliente.json";

    public ControladorCliente(frmCliente vista) {
        this.vista = vista;
        configurarTabla();
        cargarClientesDesdeArchivo();
        wireActions();
    }

    private void configurarTabla() {
        vista.getTablaClientes().setItems(datosTabla);
        vista.getTablaClientes().setPlaceholder(new javafx.scene.control.Label("No hay clientes registrados"));
    }

    private void wireActions() {
        vista.getBtnGuardar().setOnAction(e -> guardarCliente());
        // El botón Salir ya cierra la ventana desde la vista
    }

    private void guardarCliente() {
        String tipoDoc = vista.getCboTipoDocumento().getValue();
        String documento = vista.getTxtDocumento().getText().trim();
        String nombre = vista.getTxtNombreCompleto().getText().trim();
        String telefono = vista.getTxtTelefono().getText().trim();
        String correo = vista.getTxtCorreo().getText().trim();
        String tipoCliente = vista.getCboTipoCliente().getValue();

        // Validaciones
        if (tipoDoc == null || tipoDoc.isEmpty()) {
            mostrarAlerta("Validación", "Debe seleccionar un tipo de documento.");
            return;
        }

        if (documento.isEmpty()) {
            mostrarAlerta("Validación", "El documento es obligatorio.");
            return;
        }

        if (!documento.matches("\\d+")) {
            mostrarAlerta("Validación", "El documento solo debe contener números.");
            return;
        }

        if (nombre.isEmpty()) {
            mostrarAlerta("Validación", "El nombre completo es obligatorio.");
            return;
        }

        if (!telefono.isEmpty() && !telefono.matches("\\d+")) {
            mostrarAlerta("Validación", "El teléfono solo debe contener números.");
            return;
        }

        if (!correo.isEmpty() && !correo.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")) {
            mostrarAlerta("Validación", "El correo electrónico no tiene un formato válido.");
            return;
        }

        if (tipoCliente == null || tipoCliente.isEmpty()) {
            mostrarAlerta("Validación", "Debe seleccionar un tipo de cliente.");
            return;
        }

        // Leer lista actual del archivo
        Type tipoLista = ManejoJson.obtenerTipoLista(ModeloCliente.class);
        List<ModeloCliente> listaClientes = ManejoJson.leerJson(RUTA_ARCHIVO, tipoLista);

        // Verificar documento único
        boolean existe = listaClientes.stream()
                .anyMatch(c -> c.getDocumento().equals(documento));
        if (existe) {
            mostrarAlerta("Validación", "Ya existe un cliente con ese número de documento.");
            return;
        }

        // Crear y agregar nuevo cliente
        ModeloCliente cliente = new ModeloCliente(tipoDoc, documento, nombre, telefono, correo, tipoCliente);
        listaClientes.add(cliente);
        ManejoJson.escribirJson(RUTA_ARCHIVO, listaClientes);

        // Actualizar tabla
        datosTabla.add(new String[]{tipoDoc, documento, nombre, telefono, correo, tipoCliente});

        limpiarFormulario();
        mostrarAlerta("Éxito", "Cliente guardado correctamente.");
    }

    private void cargarClientesDesdeArchivo() {
        Type tipoLista = ManejoJson.obtenerTipoLista(ModeloCliente.class);
        List<ModeloCliente> listaClientes = ManejoJson.leerJson(RUTA_ARCHIVO, tipoLista);

        datosTabla.clear();
        for (ModeloCliente c : listaClientes) {
            datosTabla.add(new String[]{
                    c.getTipoDocumento(),
                    c.getDocumento(),
                    c.getNombreCompleto(),
                    c.getTelefono(),
                    c.getCorreo(),
                    c.getTipoCliente()
            });
        }
    }

    private void limpiarFormulario() {
        vista.getCboTipoDocumento().setValue(null);
        vista.getTxtDocumento().clear();
        vista.getTxtNombreCompleto().clear();
        vista.getTxtTelefono().clear();
        vista.getTxtCorreo().clear();
        vista.getCboTipoCliente().setValue(null);
    }

    private void mostrarAlerta(String titulo, String mensaje) {
        javafx.scene.control.Alert alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.INFORMATION);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}
