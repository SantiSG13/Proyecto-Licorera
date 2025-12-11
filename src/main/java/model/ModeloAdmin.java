package model;

// Clase modelo que representa un usuario del sistema.
// Responsabilidad: Almacenar datos de usuario (POJO - Plain Old Java Object).
public class ModeloAdmin {
    // Atributos privados que representan las propiedades de un usuario
    private String id;          // Identificador único del usuario
    private String usuario;     // Nombre de usuario para login
    private String contrasena;  // Contraseña (en producción debería estar hasheada)
    private String nombre;      // Nombre completo del usuario
    private String rol;         // Rol del usuario (Admin, Empleado, etc.)
    private String email;       // Correo electrónico del usuario

    // Constructor vacío: necesario para que Gson pueda deserializar desde JSON
    public ModeloAdmin() {
    }

    // Constructor completo: facilita la creación de instancias con todos los datos
    public ModeloAdmin(String id, String usuario, String contrasena, String nombre, String rol, String email) {
        this.id = id;
        this.usuario = usuario;
        this.contrasena = contrasena;
        this.nombre = nombre;
        this.rol = rol;
        this.email = email;
    }

    // Getters y Setters: permiten acceder y modificar cada atributo de forma controlada
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    // toString: útil para debugging y logs, muestra el estado del objeto
    @Override
    public String toString() {
        return "ModeloUsuario{" +
                "id='" + id + '\'' +
                ", usuario='" + usuario + '\'' +
                ", nombre='" + nombre + '\'' +
                ", rol='" + rol + '\'' +
                ", email='" + email + '\'' +
                '}';
    }
}