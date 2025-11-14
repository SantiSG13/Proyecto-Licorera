package view;

import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;

// Vista principal de la aplicación.
// Define la estructura base con: barra de menús (arriba), contenido central intercambiable y barra de estado (abajo).
public class frmPrincipal {
    // Contenedor raíz con regiones: top, center, bottom, left, right.
    private final BorderPane root = new BorderPane();

    // Etiqueta para mostrar mensajes de estado en la parte inferior.
    private final Label statusLabel = new Label("Listo");
    // Botones de navegación principal
    private final Button btnNuevoCliente = new Button("Nuevo Cliente");
    private final Button btnNuevaVenta = new Button("Nueva Venta");
    private final Button btnAdministrarUsuarios = new Button("Administrar Usuarios");
    private final Button btnSalir = new Button("Salir");

    // Constructor: arma la estructura visual inicial de la ventana principal.
    public frmPrincipal() {
        root.setPrefSize(1280, 850); // Tamaño preferido de la ventana principal.
        root.setTop(buildNavBar()); // Crea y coloca la barra de navegación en la parte superior.

        // Mensaje de bienvenida con ID para aplicar estilos CSS
        Label welcomeLabel = new Label("Bienvenido al Sistema de Gestión de Licorera");
        welcomeLabel.setId("welcome-label"); // Asigna ID para referencia CSS
        root.setCenter(welcomeLabel); // Contenido inicial en el centro.

        root.setBottom(buildStatusBar()); // Crea y coloca la barra de estado en la parte inferior.
    }

    // Construye la barra de navegación superior con botones
    private HBox buildNavBar() {
        HBox navBar = new HBox(15); // Espaciado de 15px entre botones
        navBar.setId("nav-bar"); // ID para aplicar estilos CSS

        // Aplicar clases CSS a los botones
        btnNuevoCliente.getStyleClass().add("nav-button");
        btnNuevaVenta.getStyleClass().add("nav-button-highlight"); // Botón destacado
        btnAdministrarUsuarios.getStyleClass().add("nav-button");
        btnSalir.getStyleClass().add("nav-button-exit");

        // Agregar todos los botones a la barra de navegación
        navBar.getChildren().addAll(btnNuevoCliente, btnNuevaVenta, btnAdministrarUsuarios, btnSalir);

        return navBar;
    }

    // Construye la barra de estado (parte inferior) donde se muestran mensajes breves al usuario.
    private HBox buildStatusBar() {
        HBox bar = new HBox(statusLabel); // Contenedor horizontal con la etiqueta de estado.
        bar.setId("status-bar"); // Asigna ID para aplicar estilos desde CSS
        statusLabel.setId("status-label"); // Asigna ID a la etiqueta de estado
        return bar;
    }

    // Expone el nodo raíz para que App cree la Scene con este contenedor.
    public BorderPane getRoot() { return root; }

    // Permite reemplazar dinámicamente el contenido central de la ventana principal.
    public void setCenter(Node node) {
        root.setCenter(node); // Cambia el nodo que se muestra en el centro.
        setStatus("Vista actualizada."); // Actualiza el mensaje de estado tras el cambio de vista.
    }

    // Actualiza el texto de la barra de estado inferior.
    public void setStatus(String text) { statusLabel.setText(text); }

    // Getters de botones para que el controlador registre manejadores de eventos.
    public Button getBtnNuevoCliente() { return btnNuevoCliente; }
    public Button getBtnNuevaVenta() { return btnNuevaVenta; }
    public Button getBtnAdministrarUsuarios() { return btnAdministrarUsuarios; }
    public Button getBtnSalir() { return btnSalir; }
}
