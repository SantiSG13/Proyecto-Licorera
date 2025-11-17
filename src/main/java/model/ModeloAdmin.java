package model;

// Clase que representa un usuario administrador del sistema
public class ModeloAdmin {
    private String id;
    private String usuario;
    private String contrasena;
    private String nombre;
    private String rol;
    private String correo;

    // Constructor vacío (requerido para serialización JSON)
    public ModeloAdmin() {}

    // Constructor con parámetros
    public ModeloAdmin(String id, String usuario, String contrasena, String nombre, String rol, String correo) {
        this.id = id;
        this.usuario = usuario;
        this.contrasena = contrasena;
        this.nombre = nombre;
        this.rol = rol;
        this.correo = correo;
    }

    // Getters y Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUsuario() {
        return usuario;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }

    public String getContrasena() {
        return contrasena;
    }

    public void setContrasena(String contrasena) {
        this.contrasena = contrasena;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getRol() {
        return rol;
    }

    public void setRol(String rol) {
        this.rol = rol;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    @Override
    public String toString() {
        return "ModeloAdmin{" +
                "id='" + id + '\'' +
                ", usuario='" + usuario + '\'' +
                ", nombre='" + nombre + '\'' +
                ", rol='" + rol + '\'' +
                ", correo='" + correo + '\'' +
                '}';
    }
}

