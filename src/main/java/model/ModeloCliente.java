package model;

// Modelo que representa a un Cliente del sistema.
// Contiene los datos que se almacenarán en tbCliente.json
public class ModeloCliente {
    private String tipoDocumento;   // CC, NIT, CE
    private String documento;       // Número de identificación (único)
    private String nombreCompleto;  // Nombre completo del cliente
    private String telefono;        // Teléfono de contacto
    private String correo;          // Correo electrónico

    public ModeloCliente() {
    }

    public ModeloCliente(String tipoDocumento, String documento, String nombreCompleto, String telefono, String correo) {
        this.tipoDocumento = tipoDocumento;
        this.documento = documento;
        this.nombreCompleto = nombreCompleto;
        this.telefono = telefono;
        this.correo = correo;
    }

    public String getTipoDocumento() {
        return tipoDocumento;
    }

    public void setTipoDocumento(String tipoDocumento) {
        this.tipoDocumento = tipoDocumento;
    }

    public String getDocumento() {
        return documento;
    }

    public void setDocumento(String documento) {
        this.documento = documento;
    }

    public String getNombreCompleto() {
        return nombreCompleto;
    }

    public void setNombreCompleto(String nombreCompleto) {
        this.nombreCompleto = nombreCompleto;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }
}
