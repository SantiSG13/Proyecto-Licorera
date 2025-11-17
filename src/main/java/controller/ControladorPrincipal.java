package controller;

import javafx.application.Platform; // Proporciona métodos para interactuar con el hilo de la UI (ej: salir de la app)
import javafx.stage.Stage; // Representa una ventana
import view.frmPrincipal; // Vista principal que este controlador gestiona
import view.frmCliente; // Vista de gestión de clientes
import view.frmProductos; // Vista de gestión de productos
import view.frmVenta; // Vista de gestión de ventas


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
        // Acción: Nuevo Cliente - abre la ventana de gestión de clientes
        vista.getBtnNuevoCliente().setOnAction(e -> abrirVentanaClientes());

        // Acción: Productos - abre la ventana de gestión de productos
        vista.getBtnProductos().setOnAction(e -> abrirVentanaProductos());

        // Acción: Ventas - abre la ventana de gestión de ventas
        vista.getBtnVentas().setOnAction(e -> abrirVentanaVentas());

        // Acción: Salir de la aplicación
        vista.getBtnSalir().setOnAction(e -> Platform.exit());
    }

    // Abre la ventana emergente (modal) de gestión de clientes
    private void abrirVentanaClientes() {
        try {
            // Deshabilitar el navbar mientras la ventana modal está abierta
            vista.setNavBarEnabled(false);

            // Crear la ventana de clientes pasándole el Stage principal como padre
            frmCliente ventanaClientes = new frmCliente(stagePrincipal);

            // Crear el controlador y pasarle la ventana
            new ControladorCliente(ventanaClientes);

            // Mostrar la ventana como modal (bloquea la ventana principal hasta que se cierre)
            ventanaClientes.showAndWait();

            // Actualizar estado cuando se cierra la ventana
            vista.setStatus("Ventana de Clientes cerrada.");
        } catch (Exception e) {
            // Manejo de errores en caso de que falle la carga
            vista.setStatus("Error al abrir ventana de Clientes: " + e.getMessage());
            e.printStackTrace();
        } finally {
            // Habilitar el navbar nuevamente cuando se cierra la ventana
            vista.setNavBarEnabled(true);
        }
    }

    // Abre la ventana emergente (modal) de gestión de productos
    private void abrirVentanaProductos() {
        try {
            // Deshabilitar el navbar mientras la ventana modal está abierta
            vista.setNavBarEnabled(false);

            // Crear la ventana de productos pasándole el Stage principal como padre
            frmProductos ventanaProductos = new frmProductos(stagePrincipal);

            // Crear el controlador y pasarle la ventana
            new ControladorProducto(ventanaProductos);

            // Mostrar la ventana como modal (bloquea la ventana principal hasta que se cierre)
            ventanaProductos.showAndWait();

            // Actualizar estado cuando se cierra la ventana
            vista.setStatus("Ventana de Productos cerrada.");
        } catch (Exception e) {
            // Manejo de errores en caso de que falle la carga
            vista.setStatus("Error al abrir ventana de Productos: " + e.getMessage());
            e.printStackTrace();
        } finally {
            // Habilitar el navbar nuevamente cuando se cierra la ventana
            vista.setNavBarEnabled(true);
        }
    }

    // Abre la ventana emergente (modal) de gestión de ventas
    private void abrirVentanaVentas() {
        try {
            // Deshabilitar el navbar mientras la ventana modal está abierta
            vista.setNavBarEnabled(false);

            // Crear la ventana de ventas pasándole el Stage principal como padre
            frmVenta ventanaVentas = new frmVenta(stagePrincipal);

            // Crear el controlador y pasarle la ventana
            new ControladorVenta(ventanaVentas);

            // Mostrar la ventana como modal (bloquea la ventana principal hasta que se cierre)
            ventanaVentas.showAndWait();

            // Actualizar estado cuando se cierra la ventana
            vista.setStatus("Ventana de Ventas cerrada.");
        } catch (Exception e) {
            // Manejo de errores en caso de que falle la carga
            vista.setStatus("Error al abrir ventana de Ventas: " + e.getMessage());
            e.printStackTrace();
        } finally {
            // Habilitar el navbar nuevamente cuando se cierra la ventana
            vista.setNavBarEnabled(true);
        }
    }
}
