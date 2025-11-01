public class Correo {

    private String tipo;
    private String idCorreo;
    private boolean flagSpam; // true es spam, false no es spam
    private ClienteEmisor clienteEmisor;

    public Correo(String tipo, String idCorreo, boolean flagSpam, ClienteEmisor clienteEmisor) {
        this.tipo = tipo;
        this.idCorreo = idCorreo;
        this.flagSpam = flagSpam;
        this.clienteEmisor = clienteEmisor;
    }

    public String getTipo() {
        return tipo;
    }

    public String getIdCorreo() {
        return idCorreo;
    }

    public ClienteEmisor getClienteEmisor() {
        return clienteEmisor;
    }

    public boolean isFlagSpam() {
        return flagSpam;
    }

}
