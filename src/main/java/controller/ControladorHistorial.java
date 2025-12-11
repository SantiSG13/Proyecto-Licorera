package controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import model.ModeloVenta;
import model.ModeloCompra;
import view.frmHistorial;
import files.ManejoJson;

import java.lang.reflect.Type;
import java.util.List;

// Controlador del historial de ventas y compras
public class ControladorHistorial {
    private final frmHistorial vista;

    // Rutas de los archivos JSON
    private static final String RUTA_VENTAS = "src/main/java/model/tbVenta.json";
    private static final String RUTA_COMPRAS = "src/main/java/model/tbCompra.json";

    public ControladorHistorial(frmHistorial vista) {
        this.vista = vista;
        wireActions();
        cargarDatos();
    }

    // Registra los eventos de la vista
    private void wireActions() {
        // Acción del botón salir
        vista.getBtnSalir().setOnAction(e -> vista.close());
    }

    // Carga los datos de ventas y compras desde los archivos JSON
    private void cargarDatos() {
        cargarVentas();
        cargarCompras();
    }

    // Carga las ventas desde el archivo JSON
    private void cargarVentas() {
        try {
            Type tipoListaVentas = ManejoJson.obtenerTipoLista(ModeloVenta.class);
            List<ModeloVenta> listaVentas = ManejoJson.leerJson(RUTA_VENTAS, tipoListaVentas);
            ObservableList<ModeloVenta> ventasObservable = FXCollections.observableArrayList(listaVentas);
            vista.getTablaVentas().setItems(ventasObservable);
        } catch (Exception e) {
            System.err.println("Error al cargar ventas: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // Carga las compras desde el archivo JSON
    private void cargarCompras() {
        try {
            Type tipoListaCompras = ManejoJson.obtenerTipoLista(ModeloCompra.class);
            List<ModeloCompra> listaCompras = ManejoJson.leerJson(RUTA_COMPRAS, tipoListaCompras);
            ObservableList<ModeloCompra> comprasObservable = FXCollections.observableArrayList(listaCompras);
            vista.getTablaCompras().setItems(comprasObservable);
        } catch (Exception e) {
            System.err.println("Error al cargar compras: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // Método público para actualizar los datos (útil si se llama desde otros controladores)
    public void actualizarDatos() {
        cargarDatos();
    }
}

