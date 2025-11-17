import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import view.frmInicio; // Importa la vista de inicio de sesión (primera pantalla que ve el usuario)
import controller.ControladorInicio; // Importa el controlador que maneja login y registro

// Clase principal de la aplicación JavaFX.
// Responsabilidad: inicializar JavaFX, construir la vista de inicio de sesión y mostrar la ventana.
public class App extends Application {
    // Al extender Application, JavaFX podrá invocar el metodo start() cuando el toolkit esté listo.

    @Override
    public void start(Stage primaryStage) throws Exception { // Stage = ventana principal que provee JavaFX automáticamente.
        // 1) Crear instancia de la vista de inicio de sesión (pantalla de login/registro).
        frmInicio vistaInicio = new frmInicio();

        // 2) Enlazar la vista con su controlador (maneja la lógica de autenticación).
        // Pasamos el Stage para que el controlador pueda cambiar de escena después del login exitoso.
        new ControladorInicio(vistaInicio, primaryStage);

        // 3) Crear la escena (Scene = contenedor de nodos que se monta sobre el Stage).
        Scene scene = new Scene(vistaInicio.getContenedorInicioRaiz(), 700, 700); // Tamaño ajustado para pantalla de login.

        // 4) Intentar cargar hoja de estilos CSS opcional desde resources (no es crítico si falla).
        try {
            String css = getClass().getResource("/styles.css").toExternalForm(); // Ruta relativa dentro de src/main/resources
            scene.getStylesheets().add(css); // Asocia el stylesheet a la escena.
        } catch (Exception ignored) { // Silencia cualquier error (archivo no encontrado u otra excepción).
            // Decisión: no interrumpir la ejecución si falta el CSS. En producción se recomendaría loggear.
        }

        // 5) Configurar propiedades visibles de la ventana y mostrarla.
        primaryStage.setTitle("Proyecto Licorera - Inicio de Sesión"); // Título que aparece en la barra de la ventana.
        primaryStage.setScene(scene); // Asignar la escena al Stage.
        primaryStage.show(); // Renderizar y hacer visible la ventana.
    }

    // Punto de entrada estándar de una aplicación Java; delega a launch() que inicia el ciclo de vida JavaFX.
    public static void main(String[] args) {
        launch(args); // Inicializa el entorno gráfico y llama internamente a start().
    }
}
