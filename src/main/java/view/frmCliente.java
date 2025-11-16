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
import javafx.stage.StageStyle;

// Vista para la gestión de clientes.
// Permite registrar nuevos clientes y ver la lista de clientes registrados.
public class frmCliente extends Stage {
    private final BorderPane root = new BorderPane();

    // Campos del formulario
    private final ComboBox<String> cboTipoDocumento = new ComboBox<>();
    private final TextField txtDocumento = new TextField();
    private final TextField txtNombreCompleto = new TextField();
    private final TextField txtTelefono = new TextField();
    private final TextField txtCorreo = new TextField();
    private final ComboBox<String> cboTipoCliente = new ComboBox<>();

    // Tabla para mostrar clientes
    private final TableView<String[]> tablaClientes = new TableView<>();

    // Botones
    private final Button btnGuardar = new Button("Guardar");
    private final Button btnSalir = new Button("Salir");

    public frmCliente(Stage ventanaPadre) {
        setTitle("Gestión de Clientes");
        initOwner(ventanaPadre);
        initModality(Modality.APPLICATION_MODAL);
        initStyle(StageStyle.UNDECORATED);

        root.setTop(buildFormulario());
        root.setCenter(buildTabla());
        root.setBottom(buildBotones());

        // Obtener dimensiones de la pantalla (igual que frmAdmin)
        Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();
        double screenWidth = screenBounds.getWidth();
        double screenHeight = screenBounds.getHeight();

        // Altura aproximada de la barra superior de frmPrincipal
        double navBarHeight = 50;
        double windowHeight = screenHeight - navBarHeight;

        Scene scene = new Scene(root, screenWidth, windowHeight);
        try {
            String css = getClass().getResource("/styles.css").toExternalForm();
            scene.getStylesheets().add(css);
        } catch (Exception e) {
            System.out.println("No se pudo cargar el CSS en frmCliente: " + e.getMessage());
        }

        setScene(scene);
        setResizable(false);

        // Posicionar la ventana justo debajo de la barra de navegación, igual que frmAdmin
        setX(screenBounds.getMinX());
        setY(screenBounds.getMinY() + navBarHeight);
    }

    private VBox buildFormulario() {
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(10));

        // Configurar combos y campos
        cboTipoDocumento.getItems().addAll("CC", "NIT", "CE");
        cboTipoDocumento.setPromptText("Tipo de documento");

        txtDocumento.setPromptText("Número de documento");
        txtNombreCompleto.setPromptText("Nombre completo");
        txtTelefono.setPromptText("Teléfono");
        txtCorreo.setPromptText("correo@ejemplo.com");

        cboTipoCliente.getItems().addAll("Normal", "Empresa");
        cboTipoCliente.setPromptText("Tipo de cliente");

        // Agregar al grid (formulario arriba)
        grid.add(new Label("Tipo Doc:"), 0, 0);
        grid.add(cboTipoDocumento, 1, 0);

        grid.add(new Label("Documento:"), 2, 0);
        grid.add(txtDocumento, 3, 0);

        grid.add(new Label("Nombre completo:"), 0, 1);
        grid.add(txtNombreCompleto, 1, 1, 3, 1);

        grid.add(new Label("Teléfono:"), 0, 2);
        grid.add(txtTelefono, 1, 2);

        grid.add(new Label("Correo:"), 2, 2);
        grid.add(txtCorreo, 3, 2);

        grid.add(new Label("Tipo cliente:"), 0, 3);
        grid.add(cboTipoCliente, 1, 3);

        VBox box = new VBox(10);
        box.getStyleClass().add("formulario-container");
        Label titulo = new Label("Gestión de Clientes");
        titulo.getStyleClass().add("titulo-formulario");
        box.getChildren().addAll(titulo, new Separator(), grid);
        return box;
    }

    private VBox buildTabla() {
        TableColumn<String[], String> colTipoDoc = new TableColumn<>("Tipo Doc");
        colTipoDoc.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue()[0]));

        TableColumn<String[], String> colDocumento = new TableColumn<>("Documento");
        colDocumento.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue()[1]));

        TableColumn<String[], String> colNombre = new TableColumn<>("Nombre completo");
        colNombre.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue()[2]));

        TableColumn<String[], String> colTelefono = new TableColumn<>("Teléfono");
        colTelefono.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue()[3]));

        TableColumn<String[], String> colCorreo = new TableColumn<>("Correo");
        colCorreo.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue()[4]));

        TableColumn<String[], String> colTipoCliente = new TableColumn<>("Tipo cliente");
        colTipoCliente.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue()[5]));

        tablaClientes.getColumns().addAll(colTipoDoc, colDocumento, colNombre, colTelefono, colCorreo, colTipoCliente);
        tablaClientes.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);

        VBox box = new VBox(10);
        box.getStyleClass().add("tabla-container");
        Label titulo = new Label("Lista de Clientes");
        titulo.getStyleClass().add("titulo-tabla");
        box.getChildren().addAll(titulo, tablaClientes);
        VBox.setVgrow(tablaClientes, Priority.ALWAYS);
        return box;
    }

    private HBox buildBotones() {
        HBox box = new HBox();
        box.getStyleClass().add("botones-container");

        // Aplicar clases de estilo a los botones
        btnGuardar.getStyleClass().add("btn-guardar");
        btnSalir.getStyleClass().add("btn-salir");

        // Agregar iconos a los botones (misma ruta que en frmAdmin)
        try {
            String basePath = "file:src/main/java/model/Img/";

            ImageView imgGuardar = new ImageView(new Image(basePath + "guardar.png"));
            imgGuardar.setFitWidth(16);
            imgGuardar.setFitHeight(16);
            btnGuardar.setGraphic(imgGuardar);

            ImageView imgSalir = new ImageView(new Image(basePath + "salir.png"));
            imgSalir.setFitWidth(16);
            imgSalir.setFitHeight(16);
            btnSalir.setGraphic(imgSalir);
        } catch (Exception e) {
            System.out.println("No se pudieron cargar los iconos en frmCliente: " + e.getMessage());
        }

        btnSalir.setOnAction(e -> close());

        box.getChildren().addAll(btnGuardar, btnSalir);
        return box;
    }

    // Getters para el controlador
    public ComboBox<String> getCboTipoDocumento() { return cboTipoDocumento; }
    public TextField getTxtDocumento() { return txtDocumento; }
    public TextField getTxtNombreCompleto() { return txtNombreCompleto; }
    public TextField getTxtTelefono() { return txtTelefono; }
    public TextField getTxtCorreo() { return txtCorreo; }
    public ComboBox<String> getCboTipoCliente() { return cboTipoCliente; }
    public TableView<String[]> getTablaClientes() { return tablaClientes; }
    public Button getBtnGuardar() { return btnGuardar; }
    public Button getBtnSalir() { return btnSalir; }
}
