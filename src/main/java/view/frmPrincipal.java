package view;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

// Vista principal de la aplicación.
// Define la estructura base con: barra de menús (arriba), contenido central intercambiable y barra de estado (abajo).
public class frmPrincipal {
    // Contenedor raíz con regiones: top, center, bottom, left, right.
    private final BorderPane contenedorPrincipal = new BorderPane();

    // Etiqueta para mostrar mensajes de estado en la parte inferior.
    private final Label statusLabel = new Label("Listo");
    // Botones de navegación principal
    private final Button btnAdministarUsuarios = new Button("Usuarios");
    private final Button btnNuevoCliente = new Button("Clientes");
    private final Button btnProveedores = new Button("Proveedores");
    private final Button btnInventario = new Button("Inventario");
    private final Button btnVentas = new Button("Ventas");
    private final Button btnCompras = new Button("Compras");
    private final Button btnHistorial = new Button("Historial");
    private final Button btnSalir = new Button("Salir");

    // Constructor: arma la estructura visual inicial de la ventana principal.
    public frmPrincipal() {
        contenedorPrincipal.setPrefSize(1280, 850); // Tamaño preferido de la ventana principal.
        contenedorPrincipal.setTop(buildNavBar()); // Crea y coloca la barra de navegación en la parte superior.

        // Contenido de bienvenida con imagen temática
        contenedorPrincipal.setCenter(buildWelcomeContent());

        contenedorPrincipal.setBottom(buildStatusBar()); // Crea y coloca la barra de estado en la parte inferior.
    }

    // Construye el contenido de bienvenida con imagen de la taverna
    private VBox buildWelcomeContent() {
        VBox welcomeBox = new VBox(20);
        welcomeBox.setAlignment(Pos.CENTER);
        welcomeBox.setPadding(new Insets(40));
        welcomeBox.setStyle("-fx-background-color: #1a1a1a;");

        // Título principal
        Label titleLabel = new Label("MOE'S TAVERN");
        titleLabel.setStyle(
                "-fx-font-size: 48px; -fx-font-weight: bold; -fx-text-fill: #d4af37; -fx-font-family: 'Georgia';");

        // Subtítulo
        Label subtitleLabel = new Label("Sistema de Gestión de Licorera");
        subtitleLabel.setStyle("-fx-font-size: 18px; -fx-text-fill: #808080; -fx-font-style: italic;");

        // Imagen de la taverna
        ImageView tavernImage = new ImageView();
        try {
            Image image = new Image(getClass().getResourceAsStream("/moe.png"));
            tavernImage.setImage(image);
            tavernImage.setFitWidth(300);
            tavernImage.setPreserveRatio(true);
            tavernImage.setStyle("-fx-effect: dropshadow(gaussian, rgba(212, 175, 55, 0.3), 20, 0, 0, 0);");
        } catch (Exception e) {
            System.out.println("No se pudo cargar la imagen de la taverna");
        }

        // Mensaje de bienvenida
        Label welcomeLabel = new Label("Bienvenido");
        welcomeLabel.setId("welcome-label");

        // Instrucciones
        Label instructionLabel = new Label("Selecciona una opción del menú superior para comenzar");
        instructionLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #666666;");

        welcomeBox.getChildren().addAll(titleLabel, subtitleLabel, tavernImage, welcomeLabel, instructionLabel);
        return welcomeBox;
    }

    // Construye la barra de navegación superior con botones
    private HBox buildNavBar() {
        HBox navBar = new HBox(15); // Espaciado de 15px entre botones
        navBar.setAlignment(Pos.CENTER);
        navBar.setId("nav-bar"); // ID para aplicar estilos CSS

        // Aplicar clases CSS a los botones
        btnAdministarUsuarios.getStyleClass().add("nav-button");
        btnNuevoCliente.getStyleClass().add("nav-button");
        btnProveedores.getStyleClass().add("nav-button");
        btnInventario.getStyleClass().add("nav-button");
        btnVentas.getStyleClass().add("nav-button-highlight");
        btnCompras.getStyleClass().add("nav-button-highlight");
        btnHistorial.getStyleClass().add("nav-button-highlight");
        btnSalir.getStyleClass().add("nav-button-exit");

        // Agregar todos los botones a la barra de navegación
        navBar.getChildren().addAll(btnAdministarUsuarios, btnNuevoCliente, btnProveedores, btnInventario, btnVentas,
                btnCompras, btnHistorial, btnSalir);

        return navBar;
    }

    // Construye la barra de estado (parte inferior) donde se muestran mensajes
    // breves al usuario.
    private HBox buildStatusBar() {
        HBox bar = new HBox(statusLabel); // Contenedor horizontal con la etiqueta de estado.
        bar.setId("status-bar"); // Asigna ID para aplicar estilos desde CSS
        statusLabel.setId("status-label"); // Asigna ID a la etiqueta de estado
        return bar;
    }

    // Expone el nodo raíz para que App cree la Scene con este contenedor.
    public BorderPane getPanelPrincipal() {
        return contenedorPrincipal;
    }

    // Permite reemplazar dinámicamente el contenido central de la ventana
    // principal.
    public void setCenter(Node node) {
        contenedorPrincipal.setCenter(node); // Cambia el nodo que se muestra en el centro.
        setStatus("Vista actualizada."); // Actualiza el mensaje de estado tras el cambio de vista.
    }

    // Actualiza el texto de la barra de estado inferior.
    public void setStatus(String text) {
        statusLabel.setText(text);
    }

    // Getters de botones para que el controlador registre manejadores de eventos.
    public Button getBtnAdministarUsuarios() {
        return btnAdministarUsuarios;
    }

    public Button getBtnNuevoCliente() {
        return btnNuevoCliente;
    }

    public Button getBtnProveedores() {
        return btnProveedores;
    }

    public Button getBtnInventario() {
        return btnInventario;
    }

    public Button getBtnVentas() {
        return btnVentas;
    }

    public Button getBtnCompras() {
        return btnCompras;
    }

    public Button getBtnHistorial() {
        return btnHistorial;
    }

    public Button getBtnSalir() {
        return btnSalir;
    }

    // Habilitar o deshabilitar todos los botones del navbar
    public void setNavBarEnabled(boolean enabled) {
        btnAdministarUsuarios.setDisable(!enabled);
        btnNuevoCliente.setDisable(!enabled);
        btnProveedores.setDisable(!enabled);
        btnInventario.setDisable(!enabled);
        btnVentas.setDisable(!enabled);
        btnCompras.setDisable(!enabled);
        btnHistorial.setDisable(!enabled);
        btnSalir.setDisable(!enabled);
    }

    // Configura la visibilidad de los botones según el rol del usuario
    public void configurarPermisos(String rol) {
        if ("Tienda".equalsIgnoreCase(rol)) {
            // Si es Tienda, ocultar el botón de usuarios
            // Opción 1: Deshabilitar
            // btnAdministarUsuarios.setDisable(true);

            // Opción 2: Ocultar completamente (Remover del layout)
            if (btnAdministarUsuarios.getParent() instanceof HBox) {
                ((HBox) btnAdministarUsuarios.getParent()).getChildren().remove(btnAdministarUsuarios);
            }
        }
        // Si es Administrador, no se hace nada (todo visible por defecto)
    }
}
