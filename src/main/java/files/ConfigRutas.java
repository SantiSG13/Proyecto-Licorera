package files;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

/**
 * Clase que centraliza las rutas de los archivos JSON del sistema.
 * Detecta automáticamente si se ejecuta desde IDE o desde JAR y ajusta las
 * rutas.
 */
public class ConfigRutas {

    // Directorio base donde se almacenan los datos
    private static final String DIRECTORIO_DATOS = "data";

    // Rutas de los archivos JSON (las variables no son finales porque se
    // inicializan dinámicamente)
    public static String ADMIN;
    public static String TIENDA;
    public static String CLIENTE;
    public static String PROVEEDOR;
    public static String INVENTARIO;
    public static String VENTA;
    public static String COMPRA;

    // Bloque estático que se ejecuta al cargar la clase
    static {
        inicializarRutas();
    }

    /**
     * Inicializa las rutas según el entorno de ejecución.
     * Si estamos en desarrollo (IDE), usa las rutas de src/main/java/model.
     * Si estamos en producción (JAR), usa una carpeta 'data' junto al JAR.
     */
    private static void inicializarRutas() {
        // Verificar si estamos ejecutando desde el IDE o desde el JAR
        File rutaDesarrollo = new File("src/main/java/model/tbAdmin.json");

        if (rutaDesarrollo.exists()) {
            // Modo desarrollo (desde IDE)
            String base = "src/main/java/model/";
            ADMIN = base + "tbAdmin.json";
            TIENDA = base + "tbTienda.json";
            CLIENTE = base + "tbCliente.json";
            PROVEEDOR = base + "tbProveedor.json";
            INVENTARIO = base + "tbInventario.json";
            VENTA = base + "tbVenta.json";
            COMPRA = base + "tbCompra.json";
        } else {
            // Modo producción (desde JAR)
            // Crear directorio de datos si no existe
            File directorioData = new File(DIRECTORIO_DATOS);
            if (!directorioData.exists()) {
                directorioData.mkdirs();
            }

            // Asignar rutas en el directorio data
            String base = DIRECTORIO_DATOS + "/";
            ADMIN = base + "tbAdmin.json";
            TIENDA = base + "tbTienda.json";
            CLIENTE = base + "tbCliente.json";
            PROVEEDOR = base + "tbProveedor.json";
            INVENTARIO = base + "tbInventario.json";
            VENTA = base + "tbVenta.json";
            COMPRA = base + "tbCompra.json";

            // Copiar archivos JSON desde los recursos del JAR si no existen
            copiarArchivoSiNoExiste("tbAdmin.json", ADMIN);
            copiarArchivoSiNoExiste("tbTienda.json", TIENDA);
            copiarArchivoSiNoExiste("tbCliente.json", CLIENTE);
            copiarArchivoSiNoExiste("tbProveedor.json", PROVEEDOR);
            copiarArchivoSiNoExiste("tbInventario.json", INVENTARIO);
            copiarArchivoSiNoExiste("tbVenta.json", VENTA);
            copiarArchivoSiNoExiste("tbCompra.json", COMPRA);
        }
    }

    /**
     * Copia un archivo desde los recursos del JAR al sistema de archivos si no
     * existe.
     * 
     * @param nombreRecurso Nombre del archivo de recurso (sin ruta)
     * @param rutaDestino   Ruta completa del archivo destino
     */
    private static void copiarArchivoSiNoExiste(String nombreRecurso, String rutaDestino) {
        File archivoDestino = new File(rutaDestino);
        if (!archivoDestino.exists()) {
            try {
                // Intentar cargar desde recursos empaquetados en el JAR
                InputStream inputStream = ConfigRutas.class.getResourceAsStream("/model/" + nombreRecurso);
                if (inputStream != null) {
                    Files.copy(inputStream, archivoDestino.toPath(), StandardCopyOption.REPLACE_EXISTING);
                    inputStream.close();
                } else {
                    // Si no existe el recurso, crear archivo vacío con array JSON vacío
                    Files.writeString(archivoDestino.toPath(), "[]");
                }
            } catch (IOException e) {
                // Si falla, crear archivo con array vacío
                try {
                    Files.writeString(archivoDestino.toPath(), "[]");
                } catch (IOException ignored) {
                }
            }
        }
    }
}
