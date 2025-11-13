package controller;

import javafx.application.Platform; // Proporciona métodos para interactuar con el hilo de la UI (ej: salir de la app)
import javafx.stage.Stage; // Representa una ventana
import view.frmPrincipal; // Vista principal que este controlador gestiona
import view.frmAdmin; // Vista de gestión de usuarios


// Controlador de la vista principal.
// Responsabilidad: registrar manejadores de eventos y coordinar actualizaciones en la UI.
public class ControladorPrincipal {
    private final frmPrincipal vista; // Referencia a la vista que se controla (inyectada vía constructor)
    private final Stage stagePrincipal; // Referencia al Stage principal para pasarlo a las ventanas hijas

    // Constructor: recibe la vista y el Stage principal, configura los eventos inmediatamente.
    public ControladorPrincipal(frmPrincipal vista, Stage stagePrincipal) {
        this.vista = vista; // Guarda la referencia para usarla en métodos internos.
        this.stagePrincipal = stagePrincipal; // Guarda el Stage principal para pasarlo a ventanas modales
        wireActions(); // Registra todos los manejadores de acción de los menús.
    }

    // Registra las acciones/lógicas asociadas a cada botón.
    private void wireActions() {
        // Acción: Nuevo Cliente (pendiente de implementar)
        vista.getBtnNuevoCliente().setOnAction(e -> {
            vista.setStatus("Nuevo Cliente - Funcionalidad pendiente de implementar");
        });

        // Acción: Nueva Venta (pendiente de implementar)
        vista.getBtnNuevaVenta().setOnAction(e -> {
            vista.setStatus("Nueva Venta - Funcionalidad pendiente de implementar");
        });

        // Acción: abrir ventana emergente de gestión de usuarios (Administrar Usuarios)
        vista.getBtnAdministrarUsuarios().setOnAction(e -> abrirVentanaUsuarios());

        // Acción: Salir de la aplicación
        vista.getBtnSalir().setOnAction(e -> Platform.exit());
    }

    // Abre la ventana emergente (modal) de gestión de usuarios
    private void abrirVentanaUsuarios() {
        try {
            // Crear la ventana de usuarios pasándole el Stage principal como padre
            frmAdmin ventanaUsuarios = new frmAdmin(stagePrincipal);

            // Crear el controlador y pasarle la ventana
            new ControladorAdmin(ventanaUsuarios);

            // Mostrar la ventana como modal (bloquea la ventana principal hasta que se cierre)
            ventanaUsuarios.showAndWait();

            // Actualizar estado cuando se cierra la ventana
            vista.setStatus("Ventana de Usuarios cerrada.");
        } catch (Exception e) {
            // Manejo de errores en caso de que falle la carga
            vista.setStatus("Error al abrir ventana de Usuarios: " + e.getMessage());
            e.printStackTrace();
        }
    }
}

