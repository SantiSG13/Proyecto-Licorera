package controller;

import javafx.application.Platform; // Proporciona métodos para interactuar con el hilo de la UI (ej: salir de la app)
import javafx.stage.Stage; // Representa una ventana
import view.frmPrincipal; // Vista principal que este controlador gestiona
import view.frmCliente; // Vista de gestión de clientes
import view.frmProveedor; // Vista de gestión de proveedores
import view.frmInventario; // Vista de gestión de productos
import view.frmVenta; // Vista de gestión de ventas
import view.frmCompra; // Vista de gestión de compras
import view.frmHistorial; // Vista de historial de ventas y compras
import view.frmUsuarios; // Vista de gestión de usuarios

// Controlador de la vista principal.
// Responsabilidad: registrar manejadores de eventos y coordinar actualizaciones en la UI.
public class ControladorPrincipal {
    private final frmPrincipal vista; // Referencia a la vista que se controla (inyectada vía constructor)
    private final Stage stagePrincipal; // Referencia al Stage principal para pasarlo a las ventanas hijas
    private final String rolUsuario; // Rol del usuario actual

    // Constructor: recibe la vista, el Stage principal y el ROL del usuario
    public ControladorPrincipal(frmPrincipal vista, Stage stagePrincipal, String rol) {
        this.vista = vista; // Guarda la referencia para usarla en métodos internos.
        this.stagePrincipal = stagePrincipal; // Guarda el Stage principal para pasarlo a ventanas modales
        this.rolUsuario = rol; // Almacena el rol
        wireActions(); // Registra todos los manejadores de acción de los menús.
        vista.configurarPermisos(rol); // Configura la visibilidad basada en el rol
    }

    // Registra las acciones/lógicas asociadas a cada botón.
    private void wireActions() {
        // Acción: Administrar Usuarios - abre la ventana de gestión de usuarios
        vista.getBtnAdministarUsuarios().setOnAction(e -> abrirVentanaUsuarios());

        // Acción: Nuevo Cliente - abre la ventana de gestión de clientes
        vista.getBtnNuevoCliente().setOnAction(e -> abrirVentanaClientes());

        // Acción: Proveedores - abre la ventana de gestión de proveedores
        vista.getBtnProveedores().setOnAction(e -> abrirVentanaProveedores());

        // Acción: Productos - abre la ventana de gestión de productos
        vista.getBtnInventario().setOnAction(e -> abrirVentanaProductos());

        // Acción: Ventas - abre la ventana de gestión de ventas
        vista.getBtnVentas().setOnAction(e -> abrirVentanaVentas());

        // Acción: Compras - abre la ventana de gestión de compras
        vista.getBtnCompras().setOnAction(e -> abrirVentanaCompras());

        // Acción: Historial - abre la ventana de historial
        vista.getBtnHistorial().setOnAction(e -> abrirVentanaHistorial());

        // Acción: Salir de la aplicación
        vista.getBtnSalir().setOnAction(e -> Platform.exit());
    }

    // Abre la ventana emergente (modal) de gestión de usuarios
    private void abrirVentanaUsuarios() {
        try {
            // Deshabilitar el navbar mientras la ventana modal está abierta
            vista.setNavBarEnabled(false);

            // Crear la ventana de usuarios pasándole el Stage principal como padre
            frmUsuarios ventanaUsuarios = new frmUsuarios(stagePrincipal);

            // Crear el controlador y pasarle la ventana
            new ControladorUsuarios(ventanaUsuarios);

            // Mostrar la ventana como modal (bloquea la ventana principal hasta que se
            // cierre)
            ventanaUsuarios.showAndWait();

            // Actualizar estado cuando se cierra la ventana
            vista.setStatus("Ventana de Usuarios cerrada.");
        } catch (Exception e) {
            // Manejo de errores en caso de que falle la carga
            vista.setStatus("Error al abrir ventana de Usuarios: " + e.getMessage());
            e.printStackTrace();
        } finally {
            // Habilitar el navbar nuevamente cuando se cierra la ventana
            vista.setNavBarEnabled(true);
        }
    }

    // Abre la ventana emergente (modal) de gestión de clientes
    private void abrirVentanaClientes() {
        try {
            // Deshabilitar el navbar mientras la ventana modal está abierta
            vista.setNavBarEnabled(false);

            // Crear la ventana de clientes pasándole el Stage principal como padre
            frmCliente ventanaClientes = new frmCliente(stagePrincipal);

            // Crear el controlador y pasarle la ventana
            new ControladorCliente(ventanaClientes, rolUsuario); // Pasar el rol al controlador

            // Mostrar la ventana como modal (bloquea la ventana principal hasta que se
            // cierre)
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

    // Abre la ventana emergente (modal) de gestión de proveedores
    private void abrirVentanaProveedores() {
        try {
            // Deshabilitar el navbar mientras la ventana modal está abierta
            vista.setNavBarEnabled(false);

            // Crear la ventana de proveedores pasándole el Stage principal como padre
            frmProveedor ventanaProveedores = new frmProveedor(stagePrincipal);

            // Crear el controlador y pasarle la ventana
            new ControladorProveedor(ventanaProveedores);

            // Mostrar la ventana como modal (bloquea la ventana principal hasta que se
            // cierre)
            ventanaProveedores.showAndWait();

            // Actualizar estado cuando se cierra la ventana
            vista.setStatus("Ventana de Proveedores cerrada.");
        } catch (Exception e) {
            // Manejo de errores en caso de que falle la carga
            vista.setStatus("Error al abrir ventana de Proveedores: " + e.getMessage());
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
            frmInventario ventanaProductos = new frmInventario(stagePrincipal);

            // Crear el controlador y pasarle la ventana
            new ControladorInventario(ventanaProductos);

            // Mostrar la ventana como modal (bloquea la ventana principal hasta que se
            // cierre)
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

            // Mostrar la ventana como modal (bloquea la ventana principal hasta que se
            // cierre)
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

    // Abre la ventana emergente (modal) de gestión de compras
    private void abrirVentanaCompras() {
        try {
            // Deshabilitar el navbar mientras la ventana modal está abierta
            vista.setNavBarEnabled(false);

            // Crear la ventana de compras pasándole el Stage principal como padre
            frmCompra ventanaCompras = new frmCompra(stagePrincipal);

            // Crear el controlador y pasarle la ventana
            new ControladorCompra(ventanaCompras);

            // Mostrar la ventana como modal (bloquea la ventana principal hasta que se
            // cierre)
            ventanaCompras.showAndWait();

            // Actualizar estado cuando se cierra la ventana
            vista.setStatus("Ventana de Compras cerrada.");
        } catch (Exception e) {
            // Manejo de errores en caso de que falle la carga
            vista.setStatus("Error al abrir ventana de Compras: " + e.getMessage());
            e.printStackTrace();
        } finally {
            // Habilitar el navbar nuevamente cuando se cierra la ventana
            vista.setNavBarEnabled(true);
        }
    }

    // Abre la ventana emergente (modal) de historial
    private void abrirVentanaHistorial() {
        try {
            // Deshabilitar el navbar mientras la ventana modal está abierta
            vista.setNavBarEnabled(false);

            // Crear la ventana de historial pasándole el Stage principal como padre
            frmHistorial ventanaHistorial = new frmHistorial(stagePrincipal);

            // Crear el controlador y pasarle la ventana
            new ControladorHistorial(ventanaHistorial);

            // Mostrar la ventana como modal (bloquea la ventana principal hasta que se
            // cierre)
            ventanaHistorial.showAndWait();

            // Actualizar estado cuando se cierra la ventana
            vista.setStatus("Ventana de Historial cerrada.");
        } catch (Exception e) {
            // Manejo de errores en caso de que falle la carga
            vista.setStatus("Error al abrir ventana de Historial: " + e.getMessage());
            e.printStackTrace();
        } finally {
            // Habilitar el navbar nuevamente cuando se cierra la ventana
            vista.setNavBarEnabled(true);
        }
    }
}
