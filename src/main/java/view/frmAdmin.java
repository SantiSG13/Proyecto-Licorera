package view;

import javafx.geometry.Insets;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.stage.Modality;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle; // Para quitar botones de gestión de ventana

// Vista de gestión de usuarios como ventana independiente (Stage).
// Define el formulario con campos de entrada, tabla de datos y botones de acción.
public class frmAdmin extends Stage {
    // Contenedor raíz principal de la vista
    private final BorderPane root = new BorderPane();

    // Campos de entrada para datos del usuario
    private final TextField txtId = new TextField();
    private final TextField txtNombre = new TextField();
    private final TextField txtUsuario = new TextField();
    private final PasswordField txtPassword = new PasswordField();
    private final TextField txtEmail = new TextField();
    private final ComboBox<String> cboRol = new ComboBox<>();

    // Tabla para mostrar los usuarios registrados
    private final TableView<String[]> tabla = new TableView<>();

    // Botones de acción expuestos al controlador
    private final Button btnGuardar = new Button("Guardar");
    private final Button btnModificar = new Button("Modificar");
    private final Button btnEliminar = new Button("Eliminar");
    private final Button btnSalir = new Button("Salir");

    // Constructor: crea una ventana independiente para gestión de usuarios
    // @param ventanaPadre - Stage padre para hacer esta ventana modal respecto a ella
    public frmAdmin(Stage ventanaPadre) {
        // Configurar la ventana
        setTitle("Gestión de Usuarios");

        // Hacer la ventana modal (bloquea la ventana padre hasta que se cierre)
        initModality(Modality.APPLICATION_MODAL);
        initOwner(ventanaPadre);
        initStyle(StageStyle.UNDECORATED); // Quitar botones de gestión de ventanas (minimizar, maximizar, cerrar)

        // Construir la estructura de la vista (formulario arriba, tabla abajo)
        root.setTop(buildFormulario());
        root.setCenter(buildTabla());
        root.setBottom(buildBotones());

        // Obtener dimensiones de la pantalla
        Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();
        double screenWidth = screenBounds.getWidth();
        double screenHeight = screenBounds.getHeight();

        // Altura de la barra de navegación (aproximadamente 50px según el CSS)
        double navBarHeight = 50;

        // Calcular altura de la ventana (altura total menos la barra de navegación)
        double windowHeight = screenHeight - navBarHeight;

        // Crear la escena con el ancho completo y altura ajustada
        Scene scene = new Scene(root, screenWidth, windowHeight);

        // Cargar el CSS
        try {
            String css = getClass().getResource("/styles.css").toExternalForm();
            scene.getStylesheets().add(css);
        } catch (Exception e) {
            System.out.println("No se pudo cargar el CSS: " + e.getMessage());
        }

        setScene(scene);
        setResizable(false); // Opcional: evitar redimensionar

        // Posicionar la ventana justo debajo de la barra de navegación
        setX(screenBounds.getMinX());
        setY(screenBounds.getMinY() + navBarHeight);
    }

    // Construye el formulario de entrada en la parte superior
    private VBox buildFormulario() {
        GridPane grid = new GridPane(); //Crea el grid para organizar los campos (es como una tabla)
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(10));

        // Configurar campos
        txtId.setPromptText("ID (auto)");
        txtId.setEditable(false);
        txtNombre.setPromptText("Nombre del usuario");
        txtUsuario.setPromptText("Nombre de usuario");
        txtPassword.setPromptText("Contraseña");
        txtEmail.setPromptText("correo@ejemplo.com");

        // ComboBox para roles
        cboRol.getItems().addAll("Administrador", "Tienda");
        cboRol.setPromptText("Seleccione rol");
        cboRol.setPrefWidth(200); // Ancho preferido

        // Agregar campos al grid
        grid.add(new Label("ID:"), 0, 0); // Fila 0, Columna 0
        grid.add(txtId, 1, 0); // Fila 0, Columna 1

        grid.add(new Label("Nombre:"), 0, 1);
        grid.add(txtNombre, 1, 1);

        grid.add(new Label("Usuario:"), 2, 0);
        grid.add(txtUsuario, 3, 0);

        grid.add(new Label("Contraseña:"), 2, 1);
        grid.add(txtPassword, 3, 1);

        grid.add(new Label("Email:"), 0, 2);
        grid.add(txtEmail, 1, 2);

        grid.add(new Label("Rol:"), 2, 2);
        grid.add(cboRol, 3, 2);

        VBox box = new VBox(10); // Espaciado entre elementos
        box.getStyleClass().add("formulario-container"); // Aplica clase CSS al contenedor
        Label titulo = new Label("Gestión de Usuarios");
        titulo.getStyleClass().add("titulo-formulario"); // Aplica clase CSS al título
        box.getChildren().addAll(titulo, new Separator(), grid);

        return box;
    }

    // Construye la tabla para mostrar los usuarios en el centro
    private VBox buildTabla() {
        // Configurar columnas
        TableColumn<String[], String> colId = new TableColumn<>("ID");
        colId.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue()[0]));
        colId.setPrefWidth(50);

        TableColumn<String[], String> colNombre = new TableColumn<>("Nombre");
        colNombre.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue()[1]));
        colNombre.setPrefWidth(150);

        TableColumn<String[], String> colUsuario = new TableColumn<>("Usuario");
        colUsuario.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue()[2]));
        colUsuario.setPrefWidth(120);

        TableColumn<String[], String> colEmail = new TableColumn<>("Email");
        colEmail.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue()[3]));
        colEmail.setPrefWidth(200);

        TableColumn<String[], String> colRol = new TableColumn<>("Rol");
        colRol.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue()[4]));
        colRol.setPrefWidth(120);

        tabla.getColumns().addAll(colId, colNombre, colUsuario, colEmail, colRol);

        // Hacer que las columnas se ajusten proporcionalmente al ancho de la tabla
        tabla.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);

        VBox box = new VBox(10);
        box.getStyleClass().add("tabla-container"); // Aplica clase CSS al contenedor
        // No agregar padding aquí porque ya lo maneja el CSS
        Label titulo = new Label("Lista de Usuarios");
        titulo.getStyleClass().add("titulo-tabla"); // Aplica clase CSS al título
        box.getChildren().addAll(titulo, tabla);
        VBox.setVgrow(tabla, Priority.ALWAYS); // La tabla ocupa todo el espacio vertical disponible

        return box;
    }

    // Construye la barra de botones en la parte inferior
    private HBox buildBotones() {
        HBox box = new HBox(); // Contenedor horizontal para los botones
        box.getStyleClass().add("botones-container"); // Aplica clase CSS que incluye spacing, padding y alignment

        // Aplicar clases CSS: base más gradiente tipo "button-87"
        btnGuardar.getStyleClass().addAll("btn-guardar", "btn-gradient");
        btnModificar.getStyleClass().addAll("btn-modificar", "btn-gradient");
        btnEliminar.getStyleClass().addAll("btn-eliminar", "btn-gradient");
        btnSalir.getStyleClass().addAll("btn-salir", "btn-gradient-exit");

        // Configurar acción del botón salir para cerrar solo esta ventana
        btnSalir.setOnAction(e -> close());

        // Intentar cargar iconos si existen
        try {
            String basePath = "file:src/main/java/model/Img/"; // Ruta base para las imágenes

            ImageView imgGuardar = new ImageView(new Image(basePath + "guardar.png"));
            imgGuardar.setFitWidth(16);
            imgGuardar.setFitHeight(16);
            btnGuardar.setGraphic(imgGuardar);

            ImageView imgModificar = new ImageView(new Image(basePath + "modificar.png"));
            imgModificar.setFitWidth(16);
            imgModificar.setFitHeight(16);
            btnModificar.setGraphic(imgModificar);

            ImageView imgEliminar = new ImageView(new Image(basePath + "eliminar.png"));
            imgEliminar.setFitWidth(16);
            imgEliminar.setFitHeight(16);
            btnEliminar.setGraphic(imgEliminar);

            ImageView imgSalir = new ImageView(new Image(basePath + "salir.png"));
            imgSalir.setFitWidth(16);
            imgSalir.setFitHeight(16);
            btnSalir.setGraphic(imgSalir);
        } catch (Exception e) {
            // Si no se pueden cargar los iconos, continuar sin ellos
            System.out.println("No se pudieron cargar los iconos: " + e.getMessage());
        }

        box.getChildren().addAll(btnGuardar, btnModificar, btnEliminar, btnSalir);

        return box;
    }

    // Getters para el controlador
    public TextField getTxtId() { return txtId; }
    public TextField getTxtNombre() { return txtNombre; }
    public TextField getTxtUsuario() { return txtUsuario; }
    public PasswordField getTxtPassword() { return txtPassword; }
    public TextField getTxtEmail() { return txtEmail; }
    public ComboBox<String> getCboRol() { return cboRol; }
    public TableView<String[]> getTabla() { return tabla; }
    public Button getBtnGuardar() { return btnGuardar; }
    public Button getBtnModificar() { return btnModificar; }
    public Button getBtnEliminar() { return btnEliminar; }
    public Button getBtnSalir() { return btnSalir; }

    // Metodo auxiliar para limpiar el formulario
    public void limpiarFormulario() {
        txtId.clear();
        txtNombre.clear();
        txtUsuario.clear();
        txtPassword.clear();
        txtEmail.clear();
        cboRol.setValue(null);
    }
}
