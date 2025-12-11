// Clase lanzadora necesaria para crear JAR ejecutable con JavaFX.
// JavaFX requiere que la clase principal del JAR NO extienda Application directamente.
// Esta clase actúa como punto de entrada y delega a App.main()
public class Launcher {
    public static void main(String[] args) {
        App.main(args);
    }
}

        
