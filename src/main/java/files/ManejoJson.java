package files;

// Importaciones necesarias de Gson para manejar JSON
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

// Importaciones de Java IO para manejo de archivos
import java.io.*;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

// Clase utilitaria para leer y escribir archivos JSON usando Gson.
// Responsabilidad: Convertir objetos Java a JSON y viceversa, persistiendo en archivos.
public class ManejoJson {
    // Instancia única de Gson con formato legible (prettyPrinting).
    // setPrettyPrinting() genera JSON indentado y fácil de leer para humanos.
    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    // Metodo genérico para leer una lista de objetos desde un archivo JSON.
    // <T> = parámetro de tipo genérico, permite reutilizar este metodo para cualquier clase.
    public static <T> List<T> leerJson(String ruta, Type tipoLista) {
        // Validación: si el archivo no existe, retornar lista vacía en lugar de lanzar excepción.
        File archivo = new File(ruta);
        if (!archivo.exists()) {
            return new ArrayList<>(); // Retorna lista vacía, no null (evita NullPointerException).
        }

        // Try-with-resources: garantiza que el FileReader se cierre automáticamente.
        try (FileReader reader = new FileReader(archivo)) {
            // fromJson: deserializa el JSON y reconstruye la lista de objetos Java.
            List<T> lista = gson.fromJson(reader, tipoLista);
            // Si el JSON está vacío o es null, retornar lista vacía.
            return lista != null ? lista : new ArrayList<>();
        } catch (IOException e) {
            // En caso de error de lectura, imprimir traza y retornar lista vacía.
            // Decisión: no propagar la excepción para no interrumpir la app.
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    // Metodo genérico para escribir una lista de objetos a un archivo JSON.
    public static <T> void escribirJson(String ruta, List<T> lista) {
        // Try-with-resources: cierra automáticamente el FileWriter.
        try (FileWriter writer = new FileWriter(ruta)) {
            // toJson: serializa la lista de objetos Java a formato JSON y escribe en el archivo.
            gson.toJson(lista, writer);
        } catch (IOException e) {
            // En caso de error de escritura, imprimir la traza.
            // En producción se debería registrar en un log y notificar al usuario.
            e.printStackTrace();
        }
    }

    // Metodo auxiliar para obtener el TypeToken de una lista genérica.
    // TypeToken es necesario porque Java borra los tipos genéricos en tiempo de ejecución (type erasure).
    // Ejemplo de uso: TypeToken<List<ModeloUsuario>> = ManejoJson.obtenerTipoLista(ModeloUsuario.class)
    public static <T> Type obtenerTipoLista(Class<T> clase) {
        // TypeToken.getParameterized crea un Type que representa List<T>.
        return TypeToken.getParameterized(List.class, clase).getType();
    }
}
